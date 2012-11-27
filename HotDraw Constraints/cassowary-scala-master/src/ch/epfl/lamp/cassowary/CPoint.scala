package ch.epfl.lamp.cassowary

class CPoint(x0: Double, y0: Double, idx: Int = -1, solver: SimplexSolver) {
  val x = if(idx < 0) CVar(x0,solver) else CVar("px"+idx, x0,solver)
  val y = if(idx < 0) CVar(y0,solver) else CVar("py"+idx, y0,solver)

  def set(x: Double, y: Double): Unit = {
    this.x.value = x
    this.y.value = y
  }

  override def toString: String = "(" + x.toString + ", " + y.toString + ")"
}