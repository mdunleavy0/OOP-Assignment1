package Main

import Util.Vec2


/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
case class System(
  core: Satellite = NoSatellite,
  orbit: Orbit = NoOrbit,
  satellites: List[System] = Nil
) {
  lazy val radius: Float = satellites match {
    case _ :+ last => last.orbit.radius + last.radius
    case _ => core.radius
  }

  lazy val diameter: Float = 2 * radius

  def position(time: Float, center: Vec2): Vec2 = center + orbit.displacement(time)

  def foreach(f: System => Unit): Unit = {
    f(this)
    satellites foreach f
  }
}

object NoSystem extends System()
