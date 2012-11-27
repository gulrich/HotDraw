package ch.epfl.lamp.cassowary

import java.util.Random
import org.junit.Test
import org.junit.Assert._
import scala.collection.mutable.ArrayBuffer

final class Tests {
  private val random = new Random(123456789) // don't change the seed!!!
  def rand() = random.nextDouble

  def randomInRange(low: Int, high: Int): Int =
    (rand() * (high - low) + low).toInt

  def approx(clv: CVar, b: Double): Boolean = ch.epfl.lamp.cassowary.approx(clv.value, b)

  @Test
  def simple1 {
    val solver = new SimplexSolver
    val x = CVar(167, solver)
    val y = CVar(2, solver)
    solver.addConstraint(x :== y)
    assertEquals(x.value, y.value, 0)
  }

  @Test
  def justStay1 {
    val solver = new SimplexSolver
    val x = CVar(5, solver)
    val y = CVar(10, solver)
    solver.addStay(x)
    solver.addStay(y)
    assertTrue(approx(x, 5))
    assertTrue(approx(y, 10))
  }

  @Test
  def addDelete1 {
    val solver = new SimplexSolver
    val x = CVar("x", solver)
    solver.addConstraint(Strength.Weak(x :== 100))
    val c10 = x :<= 10.0
    val c20 = x :<= 20.0
    solver.addConstraint(c10).addConstraint(c20)
    assertTrue(approx(x, 10.0))
    println("x == " + x.value)
    solver.removeConstraint(c10)
    assertTrue(approx(x, 20.0))
    println("x == " + x.value)
    solver.removeConstraint(c20)
    assertTrue(approx(x, 100.0))
    println("x == " + x.value)
    var c10again = x :<= 10.0
    solver.addConstraint(c10).addConstraint(c10again)
    assertTrue(approx(x, 10.0))
    println("x == " + x.value)
    solver.removeConstraint(c10)
    assertTrue(approx(x, 10.0))
    println("x == " + x.value)
    solver.removeConstraint(c10again)
    assertTrue(approx(x, 100.0))
    println("x == " + x.value)
  }

  @Test
  def addDelete2 {
    val solver = new SimplexSolver
    val x = CVar("x", solver)
    val y = CVar("y", solver)
    solver.addConstraint(Strength.Weak(x :== 100.0)).addConstraint(Strength.Strong(y :== 120.0))
    val c10 = (x :<= 10.0)
    val c20 = (x :<= 20.0)
    solver.addConstraint(c10).addConstraint(c20)
    assertTrue(approx(x, 10.0))
    assertTrue(approx(y, 120.0))
    println("x == " + x.value + ", y == " + y.value)
    solver.removeConstraint(c10)
    assertTrue(approx(x, 20.0))
    assertTrue(approx(y, 120.0))
    println("x == " + x.value + ", y == " + y.value)
    val cxy = ((x * 2d) :== y)
    solver.addConstraint(cxy)
    assertTrue(approx(x, 20.0))
    assertTrue(approx(y, 40.0))
    println("x == " + x.value + ", y == " + y.value)
    solver.removeConstraint(c20)
    assertTrue(approx(x, 60.0))
    assertTrue(approx(y, 120.0))
    println("x == " + x.value + ", y == " + y.value)
    solver.removeConstraint(cxy)
    assertTrue(approx(x, 100.0))
    assertTrue(approx(y, 120.0))
    println("x == " + x.value + ", y == " + y.value)
  }

  @Test
  def casso1 {
    val solver = new SimplexSolver
    val x = CVar("x", solver)
    val y = CVar("y", solver)    
    solver.addConstraint(x :<= y)
      .addConstraint(y :== x + 3.0)
      .addConstraint(Strength.Weak(x :== 10.0))
      .addConstraint(Strength.Weak(y :== 10.0))
    assertTrue(approx(x, 10.0) && approx(y, 13.0) || approx(x, 7.0) && approx(y, 10.0))
    println("x == " + x.value + ", y == " + y.value)
  }

  @Test(expected = classOf[RequiredFailureException])
  def inconsistent1 {
    val solver = new SimplexSolver
    var x = CVar("x", solver)    
    solver.addConstraint(x :== 10.0).addConstraint(x :== 5.0)
    fail()
  }

  @Test(expected = classOf[RequiredFailureException])
  def inconsistent2 {
    val solver = new SimplexSolver
    val x = CVar("x", solver)
    solver.addConstraint(x :>= 10.0).addConstraint(x :<= 5.0)
    fail()
  }

  @Test
  def multiedit {
    val solver = new SimplexSolver
    val x = CVar("x", solver)
    val y = CVar("y", solver)
    val w = CVar("w", solver)
    val h = CVar("h", solver)
    solver.addStay(x).addStay(y).addStay(w).addStay(h)
    solver.addEditVar(x).addEditVar(y).beginEdit
    solver.suggestValue(x, 10).suggestValue(y, 20).resolve
    println("x = " + x.value + "; y = " + y.value)
    println("w = " + w.value + "; h = " + h.value)
    assertTrue(approx(x, 10) && approx(y, 20) && approx(w, 0) && approx(h, 0))
    solver.addEditVar(w).addEditVar(h).beginEdit
    solver.suggestValue(w, 30).suggestValue(h, 40).endEdit
    println("x = " + x.value + "; y = " + y.value)
    println("w = " + w.value + "; h = " + h.value)
    assertTrue(approx(x, 10) && approx(y, 20) && approx(w, 30) && approx(h, 40))
    solver.suggestValue(x, 50).suggestValue(y, 60).endEdit
    println("x = " + x.value + "; y = " + y.value)
    println("w = " + w.value + "; h = " + h.value)
    assertTrue(approx(x, 50) && approx(y, 60) && approx(w, 30) && approx(h, 40))
  }

  @Test(expected = classOf[RequiredFailureException])
  def inconsistent3 {
    val solver = new SimplexSolver
    var w = CVar("w", solver)
    var x = CVar("x", solver)
    var y = CVar("y", solver)
    var z = CVar("z", solver)
    solver.addConstraint(w :>= 10.0)
      .addConstraint(x :>= w)
      .addConstraint(y :>= x)
      .addConstraint(z :>= y)
      .addConstraint(z :>= 8.0)
      .addConstraint(z :<= 4.0)
    fail()
  }

  /*@Test
  def addDelete20000 {
    addDel(20000, 20000, 5, 10000)
  }*/

  @Test
  def addDelete10000 {
    addDel(10000, 10000, 3, 10000)
  }

  /*@Test
  def addDelete10000b {
    addDel(10000, 10000, 3, 10000)
  }*/

  @Test
  def addDelete900 {
    addDel(900, 900, 3, 10000)
  }

  @Test
  def addDelete900b {
    addDel(900, 900, 3, 10)
  }

  @Test
  def addDelete300 {
    addDel(300, 300, 3, 10000)
  }

  /*@Test
  def addDelete50HaltBug {
    addDel(50, 50, 12, 10000)
  }*/

  @Test
  def addDelete50 {
    addDel(50, 50, 3, 10000)
  }

  @Test
  def addDelete6 {
    addDel(6, 6, 5, 10000)
  }

  @Test
  def addDelete5 {
    addDel(5, 5, 3, 10000)
  }

  def addDel(nCns: Int, nVars: Int, maxVars: Int, nResolves: Int) {
    val timer = new Timer
    val ineqProb = 0.12
    println("starting timing test. nCns = " + nCns + ", nVars = " + nVars + ", maxVars = " + maxVars + ", nResolves = " + nResolves)

    timer.start
    val solver = new SimplexSolver
    val cvars = Array.tabulate(nVars) { i =>
      val v = CVar("x" + i,solver)
      solver.addStay(v)
      v
    }
    var constraints = Array.tabulate(nCns) { i =>
      var nvs = randomInRange(1, maxVars)
      val expr = new LinearExpression(rand() * 20.0 - 10.0)

      var j = 0
      while (j < nvs) {
        val coeff = rand() * 10 - 5
        var iclv = (rand() * nVars).toInt
        expr += cvars(iclv) * coeff
        j += 1
      }

      val cn = if (rand() < ineqProb) expr :>= 0
          else expr :== 0

      traceprint("Constraint " + i + " is " + cn)
      cn
    }
    println("done building data structures")
    println("time = " + timer.time)

    timer.start
    var exceptionsCaught = 0
    var i = 0
    while (i < nCns) {
      try {
        solver.addConstraint(constraints(i))
      } catch {
        case e: RequiredFailureException =>
          exceptionsCaught += 1
          //println("got exception adding " + constraints(i))
          //e.printStackTrace()
          constraints(i) = null
      }
      i += 1
    }

    println("done adding constraints [" + exceptionsCaught + " exceptions].")
    println("time = " + timer.time + "\n")

    timer.start
    var e1Index = (rand() * nVars).toInt
    var e2Index = (rand() * nVars).toInt
    // make sure we edit two distinct vars
    while(e2Index == e1Index) {
      e2Index = (rand() * nVars).toInt
    }
    println("indices " + e1Index + ", " + e2Index)
    var edit1 = new EditConstraint(cvars(e1Index), Strength.Strong)
    var edit2 = new EditConstraint(cvars(e2Index), Strength.Strong)
    solver.addConstraint(edit1).addConstraint(edit2)
    println("done creating edit constraints.")
    println("time = " + timer.time + "\n")
    println("Starting resolves...")

    var resolvePair = new Array[Double](2)

    timer.start
    i = 0
    while (i < nResolves) {
      resolvePair(0) = cvars(e1Index).value * 1.001
      resolvePair(1) = cvars(e2Index).value * 1.001
      solver.resolve(resolvePair)
      i += 1
    }

    println("done resolves.")
    println("time = " + timer.time + "\n")
    println("Removing constraints...")
    solver.removeConstraint(edit1)
    solver.removeConstraint(edit2)

    timer.start
    i = 0
    while (i < nCns) {
      //println("Removing " + constraints(i))
      if (constraints(i) != null) solver.removeConstraint(constraints(i))
      i += 1
    }

    println("done removing constraints and addDel timing test.")
    println("time = " + timer.time + "\n")
  }
}