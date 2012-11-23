package ch.epfl.lamp.cassowary

class CPoint(x0: Double, y0: Double, idx: Int = -1) {
  val x = if(idx < 0) CVar(x0) else CVar("px"+idx, x0)
  val y = if(idx < 0) CVar(y0) else CVar("py"+idx, y0)

  def set(x: Double, y: Double): Unit = {
    this.x.value = x
    this.y.value = y
  }

  override def toString: String = "(" + x.toString + ", " + y.toString + ")"
}