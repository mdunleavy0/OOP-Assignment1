package Main

import Util.Vec2
import math._


/**
  * Created by Michael Dunleavy on 29/10/2016.
  */
class Orbit(val radius: Float, val period: Float, val phaseAngle: Float) {
  def angle(t: Float): Float = t * Tau.toFloat / period + phaseAngle

  def displacement(t: Float): Vec2 = Vec2 fromAngle (angle(t), radius)

  private final val Tau: Double = 2 * Pi
}
