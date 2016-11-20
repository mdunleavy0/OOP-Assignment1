package Main

import Util.Functions.{randRange, randLogNormalRange}
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

trait SystemCompanion {
  val minRadius: Float
  val maxRadius: Float
  val medianRadius: Float = (minRadius + maxRadius) / 2f
  val underSystems: List[SystemCompanion]

  def fromProcedure(targetRadius: Float, orbitRadius: Float = 0f, rng: Random = Random): System

  def randomSatellites(spaceUsed: Float, targetRadius: Float, rng: Random = Random): List[System] = {
    val us: SystemCompanion = underSystems(rng.nextInt(underSystems.length))

    val sysRadius = randRange(us.minRadius, us.maxRadius, rng)
    val padding = randRange(0.25f * sysRadius, 1.5f * sysRadius)
    val orbitRadius = spaceUsed + padding + sysRadius

    if (spaceUsed + orbitRadius > targetRadius) Nil

    else (us fromProcedure (sysRadius, orbitRadius, rng)) ::
      randomSatellites(orbitRadius + sysRadius + padding, targetRadius, rng)
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

object SolarSystem extends SystemCompanion {
  val (minRadius, maxRadius) = (100f, 1000f)
  val underSystems = List(PlanetarySystem)

  def fromProcedure(targetRadius: Float, orbitRadius: Float = 0f, rng: Random = Random): System = {
    val coreRadius = targetRadius / 12
    val satellites = randomSatellites(coreRadius, targetRadius, rng)
    SolarSystem(Star(coreRadius), Orbit(orbitRadius), satellites)
  }
}


case class PlanetarySystem(core: Planet, orbit: Orbit, satellites: List[System]) extends System {

}

object PlanetarySystem extends SystemCompanion {
  val (minRadius, maxRadius) = (1f, 20f)
  val underSystems = List(LunarSystem)

  def fromProcedure(targetRadius: Float, orbitRadius: Float = 0f, rng: Random = Random): System = {
    val coreRadius = targetRadius / 5
    val satellites = randomSatellites(coreRadius, targetRadius, rng)
    PlanetarySystem(Planet(coreRadius), Orbit(orbitRadius, 100f), satellites)
  }
}


case class LunarSystem(core: Moon, orbit: Orbit) extends System {
  val satellites = Nil
}

object LunarSystem extends SystemCompanion {
  val (minRadius, maxRadius) = (0.1f, 1f)
  val underSystems = Nil

  def fromProcedure(targetRadius: Float, orbitRadius: Float = 0f, rng: Random = Random): System = {
    LunarSystem(Moon(targetRadius), Orbit(orbitRadius, 10f))
  }
}