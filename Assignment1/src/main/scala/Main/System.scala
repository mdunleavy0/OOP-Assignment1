package Main

import math._


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
trait System {
  def satellites: List[System]

  def radius: Float
  def orbitRadius: Float
  def period: Float
  def phase: Float

  def position(time: Float = 0f, cenX: Float = 0f, cenY: Float = 0f): (Float, Float) = {
    val t = Tau * (time + phase) / period
    (cenX + orbitRadius * cos(t).toFloat, cenY + orbitRadius * sin(t).toFloat)
  }

  final val Tau: Double = 2 * Pi
}


case class Galaxy(satellites: List[System]) extends System {
  def radius = 0f
  def orbitRadius = 0f
  def period = Float.PositiveInfinity
  def phase = 0f

}


case class Star(radius: Float, orbitRadius: Float, period: Float, phase: Float, satellites: List[System]) extends System {

}


case class Planet(radius: Float, orbitRadius: Float, period: Float, phase: Float, satellites: List[System]) extends System {

}


case class Moon(radius: Float, orbitRadius: Float, period: Float, phase: Float) extends System {
  override def satellites = Nil
}