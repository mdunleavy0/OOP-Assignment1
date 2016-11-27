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
  def position(time: Float, center: Vec2): Vec2 = center + orbit.displacement(time)
}
