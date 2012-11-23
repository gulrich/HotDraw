// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// Scala implementation by Ingo Maier <ingo.maier@epfl.ch>
// (c) 1998, 1999, 2012 Alan Borning, Michael Noth, Greg Badros, and Ingo Maier.
package ch.epfl.lamp.cassowary

import scala.collection.mutable._

/**
 * Table of equations.
 *
 * Equations, stored in the rows of the tableau, are of the form 'b = c + a0*x0 + ... + an*xn',
 * with c, a0, ..., an being constants, and b, x0, ..., xn being variables. Variables on the LHS
 * are called basic variables, variables on the RHS are called parametric. Any variable is either
 * basic or parametric, never both.
 *
 * Rows are stored as a map from their basic variable to the linear expression on the RHS.
 * Every variable ocurring on at least one RHS also occupies a column. Columns is a map from that
 * variable to the set of rows where it occurs on the RHS, with the row being represented by its
 * basic variable.
 */
class Tableau {
  protected val _infeasibleRows = new FastSet[AbstractVar]
  protected val _externalVars = new FastSet[CVar]

  final def noteRemovedVariable(v: AbstractVar, subject: AbstractVar): Unit = {
    fnenterprint("noteRemovedVariable: " + v + ", " + subject)
    if (subject != null) {
      v.column -= subject
    }
  }

  final def noteAddedVariable(v: AbstractVar, subject: AbstractVar): Unit = {
    fnenterprint("noteAddedVariable: " + v + ", " + subject)
    if (subject != null) {
      addColumnVar(v, subject)
    }
  }

  private def addColumnVar(param: AbstractVar, basic: AbstractVar): Unit = {
    var rows = param.column
    if (rows eq null) {
      rows = new FastBuffer[AbstractVar]
      setColumn(param, rows)
    }
    rows += basic
  }

  protected final def addRow(bv: AbstractVar, expr: LinearExpression): Unit = {
    assert(bv.row eq null)
    fnenterprint("addRow: " + bv + ", " + expr)
    setRow(bv, expr)
    //expr.terms foreachE { (v1, _) =>
    val terms = expr.terms
    var i = 0
    while (i < terms.size) {
      val v1 = terms.varAt(i)
      addColumnVar(v1, bv)
      v1 match {
        case v1: CVar => _externalVars += v1
        case _ =>
      }
      i += 1
    }
    bv match {
      case bv: CVar => _externalVars += bv
      case _ =>
    }
    traceprint(this.toString)
  }

  protected final def removeColumn(param: AbstractVar): Unit = {
    fnenterprint("removeColumn:" + param)
    val rowVars = param.column
    if (rowVars != null) {
      removeColumnVar(param)
      var i = 0
      while (i < rowVars.size) {
        rowVars(i).row.terms -= param
        i += 1
      }
    } else debugprint("Could not find var " + param + " in _columns")

    param match {
      case param: CVar => _externalVars -= param
      case _ =>
    }
  }

  /**
   * Remove the row with the given basic var. Assumes that such a row exists.
   */
  protected final def removeRow(bv: AbstractVar): LinearExpression = {
    fnenterprint("removeRow:" + bv)
    val rhs = bv.row
    assert(rhs != null)
    //rhs.terms.foreachE { (v, _) =>
    val terms = rhs.terms
    var i = 0
    while (i < terms.size) {
      val v = terms.varAt(i)
      val bvs = v.column
      if (bvs != null) {
        debugprint("removing from varset " + bv)
        bvs -= bv
      }
      i += 1
    }
    _infeasibleRows -= bv
    bv match {
      case bv: CVar => _externalVars -= bv
      case _ =>
    }
    removeRowVar(bv)
    fnexitprint("returning " + rhs)
    rhs
  }

  /**
   * Substitute the given variable by the given expression.
   */
  protected final def substitute(oldVar: AbstractVar, expr: LinearExpression): Unit = {
    fnenterprint("substituteOut:" + oldVar + ", " + expr)
    traceprint(this.toString)
    val bvs = oldVar.column
    var i = 0
    while (i < bvs.size) {
      val bv = bvs(i)
      val rhs = bv.row
      rhs.substitute(oldVar, expr, bv, this)
      if (bv.isRestricted && rhs.constant < 0.0) {
        _infeasibleRows += bv
      }
      i += 1
    }
    removeColumnVar(oldVar)
  }

  protected final def isParameter(subject: AbstractVar): Boolean =
    subject.column ne null

  protected final def rhs(v: AbstractVar): LinearExpression = {
    v.row
  }

  def internalInfo: String = {
    val s = new StringBuilder("Tableau Information:\n")
    s.append("Rows: ").append(_rowCount)
    s.append(" (= ").append(_rowCount - 1).append(" constraints)")
    s.append("\nColumns: ").append(_columnCount)
    s.append("\nInfeasible Rows: ").append(_infeasibleRows.size)
    s.append("\nExternal variables: ")
    s.append(_externalVars.size)
    s.append("\n")
    s.toString
  }

  override def toString: String = {
    val s = new StringBuilder("Tableau:\n")
    s.append("\nColumns:\n")
    s.append(_columnCount)
    s.append("\nInfeasible rows: ")
    s.append(_infeasibleRows.toString)
    s.append("\nExternal variables: ")
    s.append(_externalVars.toString)
    s.toString
  }

  private var _columnCount = 0
  private def removeColumnVar(v: AbstractVar) = if (v.column ne null) {
    _columnCount -= 1
    v.column = null
  }

  private def setColumn(v: AbstractVar, col: ArrayBuffer[AbstractVar]) {
    if ((v.column eq null) && (col ne null)) _columnCount += 1
    else if ((v.column ne null) && (col eq null)) _columnCount -= 1
    v.column = col
  }

  private var _rowCount = 0
  private def removeRowVar(v: AbstractVar) {
    if (v.row ne null) {
      _rowCount -= 1
      v.row = null
    }
  }

  def setRow(v: AbstractVar, expr: LinearExpression) {
    if ((v.row eq null) && (expr ne null)) _rowCount += 1
    else if ((v.row ne null) && (expr eq null)) _rowCount -= 1
    v.row = expr
  }
}

/*final class Rows extends ArrayBuffer[LinearExpression] {
  def get(v: AbstractVar): LinearExpression = if (v.index < size) this(v.index) else null

  def put(v: AbstractVar, expr: LinearExpression): Unit = {
    val idx = v.index
    ensureSize(idx - 1)
    var i = size
    if (v.index < i) {
      this(idx) = expr
    } else {
      while (idx > i) {
        this += null
        i += 1
      }
      this += expr
    }
  }

  def remove(v: AbstractVar): Unit = this(v.index) = null
}*/

final class Rows {
  var size = 0

  def apply(v: AbstractVar): LinearExpression = v.row

  def update(v: AbstractVar, expr: LinearExpression) {
    if (v.row == null && expr != null) size += 1
    else if (v.row != null && expr == null) size -= 1
    v.row = expr
  }

  def -=(v: AbstractVar): Unit = if (v.row != null) {
    size -= 1
    v.row = null
  }

  def contains(v: AbstractVar) = v.row != null
}