package Main

import Util.Vec2

import math._


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
trait System {
  def core: Satellite
  def orbit: Orbit
  def satellites: List[System]

  def position(time: Float, center: Vec2): Vec2 = center + orbit.displacement(time)
}


case class Galaxy(satellites: List[System]) extends System {
  def core = NoSatellite
  def orbit = Orbit()
}


case class Star(core: Satellite, orbit: Orbit, satellites: List[System]) extends System {

}


case class Planet(core: Satellite, orbit: Orbit, satellites: List[System]) extends System {

}


case class Moon(core: Satellite, orbit: Orbit) extends System {
  def satellites = Nil
}