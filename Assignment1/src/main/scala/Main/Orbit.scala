package Main

import Util.Constants.TauF
import Util.Vec2


/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
case class Orbit(
  radius: Float = 0f,
  period: Float = Float.PositiveInfinity,
  phaseAngle: Float = 0f
) {
  val diameter: Float = 2 * radius

  def angle(time: Float): Float = time * TauF / period + phaseAngle

  def displacement(time: Float): Vec2 = Vec2 fromAngle (angle(time), radius)
}


case object NoOrbit extends Orbit()
