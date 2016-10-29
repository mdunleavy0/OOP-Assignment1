package Main

import Util.Vec2

import math.Pi


/**
  * Created by Michael Dunleavy on 29/10/2016.
  */
class Orbit(val radius: Float, val period: Float, val phaseAngle: Float) {
  val diameter: Float = 2 * radius

  def angle(time: Float): Float = time * Tau.toFloat / period + phaseAngle

  def displacement(time: Float): Vec2 = Vec2 fromAngle (angle(time), radius)

  private final val Tau: Double = 2 * Pi
}

object Orbit {
  def apply(radius: Float = 0f, period: Float = Float.PositiveInfinity, phaseAngle: Float = 0f): Orbit =
    new Orbit(radius, period, phaseAngle)
}
