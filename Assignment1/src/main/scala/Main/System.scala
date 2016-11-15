package Main

import Util.Functions.randRange
import Util.Vec2

import scala.util.Random


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
trait System {
  val core: Satellite
  val orbit: Orbit
  val satellites: List[System]

  def position(time: Float, center: Vec2): Vec2 = center + orbit.displacement(time)

  lazy val radius: Float = satellites match {
    case _ :+ last => last.orbit.radius + last.radius
    case _ => core.radius
  }
}


case class Galaxy(satellites: List[System]) extends System {
  val core = NoSatellite
  val orbit = Orbit()
}

object Galaxy {

}


case class SolarSystem(core: Star, orbit: Orbit, satellites: List[System]) extends System {

}

object SolarSystem {
  val (minRadius, maxRadius) = (100f, 1000f)

  def fromProcedure(targetRadius: Float, rng: Random = Random): SolarSystem = {

    def randomSatellites(spaceUsed: Float, rng: Random = Random): List[System] = {
      val sysRadius = randRange(PlanetarySystem.minRadius, PlanetarySystem.maxRadius, rng)
      val orbitRadius = spaceUsed + randRange(2 * sysRadius, 4 * sysRadius)

      if (spaceUsed + orbitRadius > targetRadius) Nil

      else (PlanetarySystem fromProcedure (sysRadius, orbitRadius, rng)) ::
          randomSatellites(sysRadius + orbitRadius, rng)
    }

    val coreRadius = minRadius / 5
    SolarSystem(Star(coreRadius), Orbit(), randomSatellites(coreRadius, rng))
  }
}


case class PlanetarySystem(core: Planet, orbit: Orbit, satellites: List[System]) extends System {

}

object PlanetarySystem {
  val (minRadius, maxRadius) = (1f, 20f)

  def fromProcedure(targetRadius: Float, orbitRadius: Float, rng: Random = Random): PlanetarySystem = {

    def randomSatellites(spaceUsed: Float, rng: Random = Random): List[System] = {
      val sysRadius = randRange(LunarSystem.minRadius, LunarSystem.maxRadius, rng)
      val orbitRadius = spaceUsed + randRange(2 * sysRadius, 4 * sysRadius)

      if (spaceUsed + orbitRadius > targetRadius) Nil

      else LunarSystem(Moon(sysRadius / 2), Orbit(orbitRadius, 10f)) ::
        randomSatellites(sysRadius + orbitRadius, rng)
    }

    val coreRadius = minRadius / 3
    PlanetarySystem(Planet(coreRadius), Orbit(orbitRadius, 100f), randomSatellites(coreRadius, rng))
  }
}


case class LunarSystem(core: Moon, orbit: Orbit) extends System {
  val satellites = Nil
}

object LunarSystem {
  val (minRadius, maxRadius) = (0.1f, 1f)
}