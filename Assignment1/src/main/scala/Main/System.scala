package Main

import Util.Rng
import Util.Vec2


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
  val radiusExp: Float

  val minPaddingRatio: Float
  val maxPaddingRatio: Float
  val paddingExp: Float

  val minCoreRatio: Float
  val maxCoreRatio: Float
  val coreRatioExp: Float

  val minOrbitPeriod: Float
  val maxOrbitPeriod: Float
  val orbitPeriodExp: Float

  val underSystems: List[SystemCompanion]

  def fromProcedure(targetRadius: Float, orbitRadius: Float = 0f, rng: Rng = Rng()): System

  def medianRadius: Float = (minRadius + maxRadius) / 2f

  protected def randomRadius(rng: Rng = Rng()): Float =
    rng.nextLogUniformRange(minRadius, maxRadius, radiusExp)
    //rng.nextRange(minRadius, maxRadius)

  protected def randomPadding(sysRadius: Float, rng: Rng = Rng()): Float =
    rng.nextLogUniformRange(minPaddingRatio * sysRadius, maxPaddingRatio * sysRadius, paddingExp)
    //rng.nextRange(minPaddingRatio * medianRadius, maxPaddingRatio * medianRadius)

  protected def randomCoreRadius(targetRadius: Float, rng: Rng = Rng()): Float =
    rng.nextLogUniformRange(minCoreRatio * targetRadius, maxCoreRatio * targetRadius, coreRatioExp)
  
  protected def randomOrbit(radius: Float, rng: Rng = Rng()): Orbit =
    Orbit(radius, randomOrbitPeriod(rng), rng.nextAngle)

  protected def randomOrbitPeriod(rng: Rng = Rng()): Float =
    rng.nextLogUniformRange(minOrbitPeriod, maxOrbitPeriod, orbitPeriodExp)

  def randomSatellites(spaceUsed: Float, targetRadius: Float, rng: Rng = Rng()): List[System] = {
    val us: SystemCompanion = underSystems(rng.nextInt(underSystems.length))

    val sysRadius = us.randomRadius(rng)
    val padding = us.randomPadding(sysRadius, rng)
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
  val (minRadius, maxRadius, radiusExp) = (100f, 1000f, 3f)
  val (minPaddingRatio, maxPaddingRatio, paddingExp) = (0.1f, 1f, 2f)
  val (minCoreRatio, maxCoreRatio, coreRatioExp) = (1f / 25, 1f / 10, 4f)
  val (minOrbitPeriod, maxOrbitPeriod, orbitPeriodExp) = (Float.PositiveInfinity, Float.PositiveInfinity, 1f)
  val underSystems = List(PlanetarySystem)


  def fromProcedure(targetRadius: Float, orbitRadius: Float = 0f, rng: Rng = Rng()): System = {
    val coreRadius = randomCoreRadius(targetRadius, rng)
    val orbit = Orbit(orbitRadius, Float.PositiveInfinity, rng.nextAngle)
    val satellites = randomSatellites(coreRadius, targetRadius, rng)
    SolarSystem(Star(coreRadius, rng.nextFloat), orbit, satellites)
  }
}


case class PlanetarySystem(core: Planet, orbit: Orbit, satellites: List[System]) extends System {

}

object PlanetarySystem extends SystemCompanion {
  val (minRadius, maxRadius, radiusExp) = (1f, 20f, 2f)
  val (minPaddingRatio, maxPaddingRatio, paddingExp) = (0.05f, 2f, 3f)
  val (minCoreRatio, maxCoreRatio, coreRatioExp) = (1f / 8, 1f / 4, 1f)
  val (minOrbitPeriod, maxOrbitPeriod, orbitPeriodExp) = (50f, 400f, 1.5f)
  val underSystems = List(LunarSystem)

  def fromProcedure(targetRadius: Float, orbitRadius: Float = 0f, rng: Rng = Rng()): System = {
    val coreRadius = randomCoreRadius(targetRadius, rng)
    val orbit = randomOrbit(orbitRadius, rng)
    val satellites = randomSatellites(1.5f * coreRadius, targetRadius, rng)
    PlanetarySystem(Planet(coreRadius, rng.nextFloat), orbit, satellites)
  }
}


case class LunarSystem(core: Moon, orbit: Orbit) extends System {
  val satellites = Nil
}

object LunarSystem extends SystemCompanion {
  val (minRadius, maxRadius, radiusExp) = (0.1f, 0.75f, 2f)
  val (minPaddingRatio, maxPaddingRatio, paddingExp) = (0.2f, 1f, 3f)
  val (minCoreRatio, maxCoreRatio, coreRatioExp) = (1f, 1f, 1f)
  val (minOrbitPeriod, maxOrbitPeriod, orbitPeriodExp) = (3f, 50f, 2f)
  val underSystems = Nil

  def fromProcedure(targetRadius: Float, orbitRadius: Float = 0f, rng: Rng = Rng()): System = {
    val coreRadius = randomCoreRadius(targetRadius, rng)
    val orbit = randomOrbit(orbitRadius, rng)
    LunarSystem(Moon(coreRadius, rng.nextFloat), orbit)
  }
}