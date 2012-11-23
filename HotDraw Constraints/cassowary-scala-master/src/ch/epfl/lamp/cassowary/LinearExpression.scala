package ch.epfl.lamp.cassowary

import scala.collection.mutable.{ TermsMap, TermsBuffer, Terms }

/**
 * Linear expression of the form 'constant + c1*v1 + ... cn*vn'
 */
final class LinearExpression(var constant: Double, val terms: TermsBuffer) extends Serializable {
  def this(clv: AbstractVar, value: Double, constant: Double) = {
    this(constant, {
      val terms = new TermsBuffer
      if (clv != null) terms(clv) = value
      terms
    })
  }

  def this(constant: Double) = this(constant, new TermsBuffer)

  def :==(rhs: Double) = new LinearEquation(this.clone -= rhs)
  def :==(rhs: CVar) = new LinearEquation(this.clone += (rhs, -1.0))
  def :<=(rhs: CVar) = new LinearInequality(-this += rhs)
  def :>=(rhs: CVar) = new LinearInequality(this.clone -= rhs)
  def :>=(rhs: Double) = new LinearInequality(this.clone -= rhs)

  def *=(k: Double): this.type = {
    constant *= k
    //terms foreachE { (v, _) =>
    var i = 0
    while (i < terms.size) {
      val v = terms.varAt(i)
      terms(v) = terms(v) * k
      i+=1
    }
    this
  }

  override def clone: LinearExpression =
    new LinearExpression(constant, terms.clone)

  def *(x: Double): LinearExpression = clone *= x

  def *(expr: LinearExpression): LinearExpression = {
    if (isConstant) expr * constant
    else if (!expr.isConstant) throw new NonlinearExpressionException
    else this * expr.constant
  }

  def +(k: Double): LinearExpression = {
    val e = clone
    e.constant += k
    e
  }

  def +=(k: Double): this.type = {
    constant += k
    this
  }

  def unary_-(): LinearExpression = this.clone *= -1

  def -=(k: Double): this.type = {
    constant -= k
    this
  }

  def +(expr: LinearExpression): LinearExpression =
    clone.addExpression(expr, 1.0)

  def +(v: CVar): LinearExpression =
    clone += (v, 1.0)

  def -(expr: LinearExpression): LinearExpression =
    clone.addExpression(expr, -1.0)

  def -(v: CVar): LinearExpression = clone += (v, -1.0)

  def /(x: Double): LinearExpression = {
    if (approx(x, 0.0)) throw new NonlinearExpressionException
    this * (1.0 / x)
  }

  def /(expr: LinearExpression): LinearExpression = {
    if (!expr.isConstant) throw new NonlinearExpressionException
    this / expr.constant
  }

  def addExpression(expr: LinearExpression, n: Double, subject: AbstractVar, solver: Tableau): this.type = {
    constant += n * expr.constant

    //expr.terms.foreachE { (v, c) =>
    var i = 0
    while (i < expr.terms.size) {
      val v = expr.terms.varAt(i)
      val c = expr.terms.coeffAt(i)
      this += (v, c * n, subject, solver)
      i += 1
    }
    this
  }

  def addExpression(expr: LinearExpression, n: Double): this.type = {
    constant += n * expr.constant
    //expr.terms.foreachE { (v, c) =>
    var i = 0
    while (i < expr.terms.size) {
      val v = expr.terms.varAt(i)
      val c = expr.terms.coeffAt(i)
      this += (v, c * n)
      i+=1
    }
    this
  }

  def +=(expr: LinearExpression): this.type = addExpression(expr, 1.0)

  def +=(v: AbstractVar): this.type =
    this += (v, 1d)

  def -=(v: AbstractVar): this.type =
    this += (v, -1d)

  def +=(v: AbstractVar, c: Double): this.type =
    this += (v, c, null, null)

  def addVariable(v: AbstractVar, c: Double, subject: AbstractVar, solver: Tableau): this.type =
    this += (v, c, subject, solver)

  def update(v: AbstractVar, c: Double): this.type = {
    terms(v) = c
    this
  }

  def +=(v: AbstractVar, c: Double, basis: AbstractVar, solver: Tableau): this.type = {
    val oldCoeff = terms(v)
    if (oldCoeff != 0) {
      val newCoeff = oldCoeff + c
      if (approx(newCoeff, 0.0)) {
        if (solver != null) solver.noteRemovedVariable(v, basis)
        terms -= v
      } else {
        terms(v) = newCoeff
      }
    } else if (!approx(c, 0.0)) {
      terms(v) = c
      if (solver != null) solver.noteAddedVariable(v, basis)
    }
    this
  }

  def anyPivotableVariable: AbstractVar = {
    assert(!isConstant, "anyPivotableVariable called on a constant")
    //terms foreachE { (v, _) =>
    var i = 0
    while (i < terms.size) {
      val v = terms.varAt(i)
      if (v.isPivotable) return v
      i+=1
    }
    null
  }

  def substitute(v: AbstractVar, expr: LinearExpression, basis: AbstractVar, solver: Tableau): Unit = {
    //println(terms.size)
    fnenterprint("CLE:substituteOut: " + v + ", " + expr + ", " + basis + ", ...")
    traceprint("this = " + this)
    val multiplier = terms(v)
    terms -= v
    constant += multiplier * expr.constant
    //expr.terms foreachE { (v, c) =>
    var i = 0
    while (i < expr.terms.size) {
      val v = expr.terms.varAt(i)
      val c = expr.terms.coeffAt(i)
      this += (v, multiplier * c, basis, solver)
      i+=1
    }
    traceprint("Now this is " + this)
  }

  def changeSubject(oldSub: AbstractVar, newSub: AbstractVar): Unit = {
    terms(oldSub) = newSubject(newSub)
  }

  def newSubject(subject: AbstractVar): Double = {
    var coeff = terms(subject)
    terms -= subject
    var recip = 1.0 / coeff
    this *= -recip
    recip
  }

  def coefficientFor(v: AbstractVar): Double = terms(v)

  def isConstant: Boolean = terms.size == 0

  override def toString: String = {
    var s = new StringBuilder
    s.append(constant)
    if (approx(constant, 0)) s.append("(~zero)");
    terms foreachE { (v, c) =>
      s.append(" + " + c + "*" + v)
    }
    s.toString
  }
}