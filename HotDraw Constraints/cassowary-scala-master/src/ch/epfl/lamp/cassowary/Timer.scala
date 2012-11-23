package ch.epfl.lamp.cassowary

class Timer {
  private var timerIsRunning = false
  private var elapsedNanoSecs = 0L
  private var t0 = 0L

  def start(){
    t0 = System.nanoTime
  }

  def time: Double = (System.nanoTime - t0).toDouble / 1e9
}