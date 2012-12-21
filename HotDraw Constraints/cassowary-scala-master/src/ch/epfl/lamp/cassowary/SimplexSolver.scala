// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// Scala implementation by Ingo Maier <ingo.maier@epfl.ch>
// (c) 1998, 1999, 2012 Alan Borning, Michael Noth, Greg Badros, and Ingo Maier.
package ch.epfl.lamp.cassowary

import scala.collection.mutable.{ ArrayBuffer, ArrayStack, FastMap, FastSet, TermsMap }

final class SimplexSolver extends Tableau with Serializable {
  private val _stayMinusErrorVars = new ArrayBuffer[SlackVar]
  private val _stayPlusErrorVars = new ArrayBuffer[SlackVar]
  private val _objective = new ObjectiveVar("Z")
  private val _editVarMap = new FastMap[CVar, EditInfo]
  private val Epsilon = 1e-8

  _objective.row = new LinearExpression(0)
  private val _stkCedcns = new ArrayStack[Int] += 0

  var autoSolve = true
  private var _needsSolving = false

  private var _slackCounter = 0L
  def newSlackVar(prefix: String): SlackVar = {
    _slackCounter += 1
    new SlackVar(if (debug) "s" + _slackCounter else "s")
  }

  private var _dummyCounter = 0L
  def newDummyName: String = {
    _dummyCounter += 1
    if (debug) "d" + _dummyCounter else "d"
  }

  private var _artificialCounter = 0L
  private def freshArtificalName() = {
    _artificialCounter += 1
    if(debug) "a" + _artificialCounter else "a"
  }

  traceprint("objective expr == " + rhs(_objective))

  def addConstraint(cn: Constraint): this.type = {
    fnenterprint("addConstraint: " + cn)
    val expr = newExpression(cn)
    addExpr(expr)

    _needsSolving = true
    if (autoSolve) {
      optimize(_objective)
      setExternalVariables()
    }
    this
  }

  def addConstraintNoException(cn: Constraint): Boolean = {
    fnenterprint("addConstraintNoException: " + cn)
    try {
      addConstraint(cn)
      true
    } catch {
      case e: RequiredFailureException => false
    }
  }

  def addEditVar(v: CVar, strength: Strength): this.type = {
    val cnEdit = new EditConstraint(v, strength)
    addConstraint(cnEdit)
  }

  def addEditVar(v: CVar): this.type =
    addEditVar(v, Strength.Strong)

  def removeEditVar(v: CVar): this.type = {
    val cei = _editVarMap(v)
    val cn = cei.constraint
    removeConstraint(cn)
  }

  def beginEdit(): this.type = {
    assert(_editVarMap.size > 0)
    _infeasibleRows.clear()
    resetStayConstants()
    _stkCedcns.push(_editVarMap.size)
    this
  }

  def endEdit(): this.type = {
    assert(_editVarMap.size > 0)
    resolve()
    _stkCedcns.pop
    val n = _stkCedcns.top.intValue
    removeEditVarsTo(n)
    this
  }

  def removeAllEditVars(): this.type = removeEditVarsTo(0)

  def removeEditVarsTo(n: Int): this.type = {
    for (v <- new FastSet[CVar] ++ _editVarMap.keySet) {
      val cei = _editVarMap(v)
      if (cei.index >= n) {
        removeEditVar(v)
      }
    }
    assert(_editVarMap.size == n)
    this
  }

  def addPointStays(listOfPoints: CPoint*): this.type = {
    fnenterprint("addPointStays" + listOfPoints)
    var weight = 1.0
    val multiplier = 2.0
    var i = 0
    while (i < listOfPoints.size) {
      addPointStay(listOfPoints(i), weight)
      weight *= multiplier
      i += 1
    }
    this
  }

  def addPointStay(vx: CVar, vy: CVar, weight: Double): this.type = {
    addStay(vx, Strength.Weak, weight)
    addStay(vy, Strength.Weak, weight)
    this
  }

  def addPointStay(vx: CVar, vy: CVar): this.type =
    addPointStay(vx, vy, 1.0)

  def addPointStay(clp: CPoint, weight: Double): this.type = {
    addStay(clp.x, Strength.Weak, weight)
    addStay(clp.y, Strength.Weak, weight)
    this
  }

  def addPointStay(clp: CPoint): SimplexSolver =
    addPointStay(clp, 1.0)

  def addStay(v: CVar, strength: Strength, weight: Double): Constraint = {
    val cn = new StayConstraint(v, strength, weight)
    addConstraint(cn)
    cn
  }

  def addStay(v: CVar, strength: Strength): Constraint = addStay(v, strength, 1.0)

  def addStay(v: CVar): Constraint = addStay(v, Strength.Weak, 1.0)

  def removeConstraint(cn: Constraint): this.type = {
    fnenterprint("removeConstraint: " + cn)
    traceprint(this.toString)
    _needsSolving = true
    resetStayConstants()
    val zRow = rhs(_objective)
    val eVars = cn.errorVars
    traceprint("eVars == " + eVars)
    if (eVars != null) {
      for (clv <- eVars) {
        val expr = rhs(clv)
        if (expr == null) zRow.addVariable(clv, -cn.weight * cn.strength.asDouble, _objective, this)
        else zRow.addExpression(expr, -cn.weight * cn.strength.asDouble, _objective, this)
      }
    }
    val marker = cn.markerVar
    if (marker == null) throw new ConstraintNotFoundException(cn)
    cn.markerVar = null

    traceprint("Looking to remove var " + marker)
    if (rhs(marker) == null) {
      val col = marker.column
      traceprint("Must pivot -- columns are " + col)
      var exitVar: AbstractVar = null
      var minRatio = 0d
      var i = 0
      while (i < col.size) {
        val v = col(i)
        if (v.isRestricted) {
          val expr = rhs(v)
          var coeff = expr.coefficientFor(marker)
          traceprint("Marker " + marker + "'s coefficient in " + expr + " is " + coeff)
          if (coeff < 0.0) {
            val r = -expr.constant / coeff
            if (exitVar == null || r < minRatio) {
              minRatio = r
              exitVar = v
            }
          }
        }
        i += 1
      }
      if (exitVar == null) {
        traceprint("exitVar is still null")
        var i = 0
        while (i < col.size) {
          val v = col(i)
          if (v.isRestricted) {
            val expr = rhs(v)
            var coeff = expr.coefficientFor(marker)
            val r = expr.constant / coeff
            if (exitVar == null || r < minRatio) {
              minRatio = r
              exitVar = v
            }
          }
          i += 1
        }
      }
      if (exitVar == null) {
        if (col.size == 0) removeColumn(marker)
        else exitVar = col(0)
      }
      else pivot(marker, exitVar)
    }
    if (rhs(marker) != null) {
      removeRow(marker)
    }
    if (eVars != null) {
      for (v <- eVars) if (v ne marker) removeColumn(v)
    }
    cn match {
      case cn: StayConstraint =>
        if (eVars != null) {
          var i = 0
          while (i < _stayPlusErrorVars.size) {
            eVars -= _stayPlusErrorVars(i)
            eVars -= _stayMinusErrorVars(i)
            i += 1
          }
        }
      case cn: EditConstraint =>
        assert(eVars != null, "eVars != null")
        val clv = cn.variable
        val cei = _editVarMap(clv)
        assert(cei != null,
          "Trying to remove an edit constraint that is not present (same variable edited twice?): " + cn)
        val clvEditMinus = cei.clvEditMinus
        removeColumn(clvEditMinus)
        _editVarMap -= clv
      case _ =>
    }
    if (eVars != null) {
      cn.errorVars = null // TODO: this falsely removed eVars in Java version, is this correct?
    }

    if (autoSolve) {
      optimize(_objective)
      setExternalVariables()
    }
    this
  }

  def resolve(newEditConstants: Array[Double]): Unit = {
    fnenterprint("resolve" + newEditConstants)
    _editVarMap.foreachE { (v, cei) =>
      val i = cei.index
      if (i < newEditConstants.length)
        suggestValue(v, newEditConstants(i))
    }
    resolve()
  }

  def resolve(): Unit = {
    fnenterprint("resolve()")
    dualOptimize()
    setExternalVariables()
    _infeasibleRows.clear()
    resetStayConstants()
  }

  def suggestValue(v: CVar, x: Double): this.type = {
    fnenterprint("suggestValue(" + v + ", " + x + ")")
    val editInfo = _editVarMap(v)
    if (editInfo == null) {
      throw new CLException("suggestValue for variable " + v + ", but it is not an edit variable\n")
    }
    val delta = x - editInfo.prevEditConstant
    editInfo.prevEditConstant = x
    deltaEditConstant(delta, editInfo.clvEditPlus, editInfo.clvEditMinus)
    this
  }

  def solve(): this.type = {
    if (_needsSolving) {
      optimize(_objective)
      setExternalVariables()
    }
    this
  }

  def setEditedValue(v: CVar, n: Double): this.type = {
    if (!containsVariable(v)) {
      v.changeValue(n)
    } else if (!approx(n, v.value)) {
      addEditVar(v)
      beginEdit
      suggestValue(v, n)
      endEdit()
    }
    this
  }

  def containsVariable(v: CVar): Boolean =
    isParameter(v) || rhs(v) != null

  def addVar(v: CVar): this.type = {
    if (!containsVariable(v)) {
      addStay(v)
      traceprint("added initial stay on " + v)
    }
    this
  }

  final override def internalInfo: String = {
    val s = new StringBuilder(super.internalInfo)
    s.append("\nSolver info:\n")
    s.append("Stay Error Variables: ")
    s.append(_stayPlusErrorVars.size + _stayMinusErrorVars.size)
    s.append(" (").append(_stayPlusErrorVars.size ).append(" +, ")
    s.append(_stayMinusErrorVars.size).append(" -)\n")
    s.append("Edit Variables: ").append(_editVarMap.size)
    s.append("\n")
    s.toString
  }

  def debugInfo: String = {
    val s = new StringBuilder(toString)
    s.append(internalInfo)
    s.append("\n")
    s.toString
  }

  final override def toString: String = {
    val s = new StringBuilder(super.toString)
    s.append("\n_stayPlusErrorVars: ")
    s.append(_stayPlusErrorVars)
    s.append("\n_stayMinusErrorVars: ")
    s.append(_stayMinusErrorVars)
    s.append("\n")
    s.toString
  }

  private def addExpr(expr: LinearExpression) {
    val subject = chooseSubject(expr)
    if (subject == null) {
      addExprWithArtificialVariable(expr)
    } else {
      expr.newSubject(subject)
      if (isParameter(subject))
        substitute(subject, expr)
      addRow(subject, expr)
    }
  }

  private def addExprWithArtificialVariable(expr: LinearExpression) {
    fnenterprint("addExprWithArtificialVariable: " + expr)
    val av = new SlackVar(freshArtificalName())
    val az = new ObjectiveVar("az")
    val azRow = expr.clone
    traceprint("before addRows:\n" + this)
    addRow(az, azRow)
    addRow(av, expr)
    traceprint("after addRows:\n" + this)
    optimize(az)
    val azTableauRow = rhs(az)
    traceprint("azTableauRow.constant() == " + azTableauRow.constant)
    if (!approx(azTableauRow.constant, 0.0)) {
      removeRow(az)
      removeColumn(av)
      throw new RequiredFailureException
    }
    val e = rhs(av)
    if (e != null) {
      if (e.isConstant) {
        removeRow(av)
        removeRow(az)
        return
      }
      val entryVar = e.anyPivotableVariable
      pivot(entryVar, av)
    }
    assert(rhs(av) == null, "rhs(av) == null")
    removeColumn(av)
    removeRow(az)
  }

  protected def chooseSubject(expr: LinearExpression): AbstractVar = {
    fnenterprint("chooseSubject: " + expr)
    var subject: AbstractVar = null
    var foundUnrestricted = false
    var foundNewRestricted = false
    val terms = expr.terms

    //terms.foreachE { (v, c) =>
    var i = 0
    while (i < terms.size) {
      val v = terms.varAt(i)
      val c = terms.coeffAt(i)
      if (foundUnrestricted) {
        if (!v.isRestricted) {
          if (!isParameter(v)) return v
        }
      } else {
        if (v.isRestricted) {
          if (!foundNewRestricted && !v.isDummy && c < 0.0) {
            val col = v.column
            if (col == null || (col.size == 1 && isParameter(_objective))) {
              subject = v
              foundNewRestricted = true
            }
          }
        } else {
          subject = v
          foundUnrestricted = true
        }
      }
      i += 1
    }
    if (subject == null) {
      var coeff = 0d
      //terms.foreachE { (v, c) =>
      var i = 0
      while (i < terms.size) {
        val v = terms.varAt(i)
        val c = terms.coeffAt(i)
        if (!v.isDummy) return null
        if (!isParameter(v)) {
          subject = v
          coeff = c
        }
        i += 1
      }
      if (!approx(expr.constant, 0.0)) throw new RequiredFailureException
      if (coeff > 0.0) expr *= -1.0
    }
    subject
  }

  protected def deltaEditConstant(delta: Double, plusErrorVar: AbstractVar, minusErrorVar: AbstractVar): Unit = {
    fnenterprint("deltaEditConstant :" + delta + ", " + plusErrorVar + ", " + minusErrorVar)
    val exprPlus = rhs(plusErrorVar)
    if (exprPlus != null) {
      exprPlus.constant += delta
      if (exprPlus.constant < 0d) {
        _infeasibleRows += plusErrorVar
      }
      return
    }
    val exprMinus = rhs(minusErrorVar)
    if (exprMinus != null) {
      exprMinus.constant += -delta
      if (exprMinus.constant < 0d) {
        _infeasibleRows += minusErrorVar
      }
      return
    }
    val columnVars = minusErrorVar.column
    var i = 0
    while (i < columnVars.size) {
      val bv = columnVars(i)
      val expr = rhs(bv)
      val c = expr.coefficientFor(minusErrorVar)
      expr.constant += c * delta
      if (bv.isRestricted && expr.constant < 0.0) {
        _infeasibleRows += bv
      }
      i += 1
    }
  }

  protected def dualOptimize(): Unit = {
    fnenterprint("dualOptimize:")
    val zRow = rhs(_objective)
    while (!_infeasibleRows.isEmpty) {
      //println(_infeasibleRows)
      val exitVar = _infeasibleRows.iterator.next
      _infeasibleRows -= exitVar
      var entryVar: AbstractVar = null
      val expr = rhs(exitVar)
      if (expr != null) {
        if (expr.constant < 0.0) {
          var ratio = Double.MaxValue
          val terms = expr.terms
          var i = 0
          while (i < terms.size) {
            val v = terms.varAt(i)
            val c = terms.coeffAt(i)
            if (c > 0.0 && v.isPivotable) {
              val zc = zRow.coefficientFor(v)
              val r = zc / c
              if (r < ratio) {
                entryVar = v
                ratio = r
              }
            }
            i += 1
          }

          /*terms foreachE { (v, c) =>
            if (c > 0.0 && v.isPivotable) {
              val zc = zRow.coefficientFor(v)
              val r = zc / c
              if (r < ratio) {
                entryVar = v
                ratio = r
              }
            }
          }*/
          assert((ratio != Double.MaxValue), "ratio == nil (MAX_VALUE) in dualOptimize")
          pivot(entryVar, exitVar)
        }
      }
    }
  }

  protected def newExpression(cn: Constraint): LinearExpression = {
    fnenterprint("newExpression: " + cn)
    traceprint("cn.isInequality() == " + cn.isInstanceOf[LinearInequality])
    traceprint("cn.isRequired() == " + cn.isRequired)
    val cnExpr = cn.expression
    val expr = new LinearExpression(cnExpr.constant)
    val cnTerms = cnExpr.terms

    //cnTerms.foreachE { (v, c) =>
    var i = 0
    while (i < cnTerms.size) {
      val v = cnTerms.varAt(i)
      val c = cnTerms.coeffAt(i)
      val e = rhs(v)
      if (e == null) expr += (v, c)
      else expr.addExpression(e, c)
      i += 1
    }
    cn match {
      case _: LinearInequality =>
        _slackCounter += 1
        val slackVar = newSlackVar("s")
        expr(slackVar) = -1
        cn.markerVar = slackVar
        if (!cn.isRequired) {
          _slackCounter += 1
          val eminus = newSlackVar("em")
          expr(eminus) = 1.0
          val zRow = rhs(_objective)
          val w = cn.strength.asDouble * cn.weight
          zRow(eminus) = w
          insertErrorVar(cn, eminus)
          noteAddedVariable(eminus, _objective)
        }
      case _ =>
        if (cn.isRequired) {
          val dummyVar = new DummyVar(newDummyName)
          expr(dummyVar) = 1.0
          cn.markerVar = dummyVar
        } else {
          _slackCounter += 1
          val eplus = newSlackVar("ep")
          val eminus = newSlackVar("em")
          expr(eplus) = -1.0
          expr(eminus) = 1.0
          cn.markerVar = eplus
          val zRow = rhs(_objective)
          val weight = cn.strength.asDouble * cn.weight
          if (weight == 0) {
            traceprint("cn == " + cn)
            traceprint("adding " + eplus + " and " + eminus + " with swCoeff == " + weight)
          }
          zRow(eplus) = weight
          noteAddedVariable(eplus, _objective)
          zRow(eminus) = weight
          noteAddedVariable(eminus, _objective)
          insertErrorVar(cn, eminus)
          insertErrorVar(cn, eplus)
          cn match {
            case _: StayConstraint =>
              _stayPlusErrorVars += eplus
              _stayMinusErrorVars += eminus
            case cn: EditConstraint =>
              val editInfo = new EditInfo(cn, eplus, eminus, cnExpr.constant, _editVarMap.size)
              _editVarMap(cn.variable) = editInfo
            case _ =>
          }
        }
    }
    if (expr.constant < 0) expr *= -1
    fnexitprint("returning " + expr)
    expr
  }

  protected def optimize(zVar: ObjectiveVar): Unit = {
    fnenterprint("optimize: " + zVar)
    traceprint(this.toString)
    val zRow = rhs(zVar)
    assert(zRow != null, "zRow != null")
    var entryVar: AbstractVar = null // this will become a basic var
    var exitVar: AbstractVar = null // this will become the former basic var
    while (true) {
      var objectiveCoeff = 0d
      val terms = zRow.terms

      // find variable in objective function with smallest coefficient
      //terms foreachE { (v, c) =>
      var i = 0
      while (i < terms.size) {
        val v = terms.varAt(i)
        val c = terms.coeffAt(i)
        if (v.isPivotable && c < objectiveCoeff) {
          objectiveCoeff = c
          entryVar = v
        }
        i += 1
      }

      if (objectiveCoeff >= -Epsilon || entryVar == null) return
      traceprint("entryVar == " + entryVar + ", objectiveCoeff == " + objectiveCoeff)
      var minRatio = Double.MaxValue
      val columnVars = entryVar.column
      //println(columnVars.size)

      i = 0
      while (i < columnVars.size) {
        val v = columnVars(i)
        traceprint("Checking " + v)
        if (v.isPivotable) {
          val expr = rhs(v)
          var coeff = expr.coefficientFor(entryVar)
          traceprint("pivotable, coeff = " + coeff)
          if (coeff < 0.0) {
            val r = -expr.constant / coeff
            if (r < minRatio) {
              traceprint("New minratio == " + r)
              minRatio = r
              exitVar = v
            }
          }
        }
        i+=1
      }
      assert((minRatio != Double.MaxValue), "Objective function is unbounded in optimize")
      //println(objectiveCoeff, entryVar, exitVar)
      pivot(entryVar, exitVar)
      traceprint(this.toString)
    }
  }

  protected def pivot(entryVar: AbstractVar, exitVar: AbstractVar): Unit = {
    fnenterprint("pivot: " + entryVar + ", " + exitVar)
    val rhs = removeRow(exitVar)
    rhs.changeSubject(exitVar, entryVar)
    substitute(entryVar, rhs)
    addRow(entryVar, rhs)
  }

  protected def resetStayConstants(): Unit = {
    fnenterprint("resetStayConstants")
    var i = 0
    while (i < _stayPlusErrorVars.size) {
      var expr = rhs(_stayPlusErrorVars(i))
      if (expr == null) expr = rhs(_stayMinusErrorVars(i))
      if (expr != null) expr.constant = 0d
      i += 1
    }
  }

  protected def setExternalVariables(): Unit = {
    fnenterprint("setExternalVariables:")
    traceprint(this.toString)
    for (v <- _externalVars) {
      val expr = rhs(v)
      if (expr == null) v.changeValue(0.0)
      else v.changeValue(expr.constant)
    }
    _needsSolving = false
  }

  protected def insertErrorVar(cn: Constraint, v: AbstractVar): Unit = {
    fnenterprint("insertErrorVar:" + cn + ", " + v)
    var cnset = cn.errorVars
    if (cnset == null) {
      cnset = new FastSet[AbstractVar]
      cn.errorVars = cnset
    }
    cnset.add(v)
  }
}