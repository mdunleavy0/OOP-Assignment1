package Main

import Util.Vec2


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
trait System {
  val core: Satellite
  val orbit: Orbit
  val satellites: List[System]

  def position(time: Float, center: Vec2): Vec2 = center + orbit.displacement(time)
}


case class Galaxy(satellites: List[System]) extends System {
  val core = NoSatellite
  val orbit = Orbit()
}


case class SolarSystem(core: Star, orbit: Orbit, satellites: List[System]) extends System {

}


case class PlanetarySystem(core: Planet, orbit: Orbit, satellites: List[System]) extends System {

}


case class LunarSystem(core: Moon, orbit: Orbit) extends System {
  val satellites = Nil
}