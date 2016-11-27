package Main

import Util.Constants.TauF
import Util.Rng
import Util.Vec2


/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
object SystemGenerator {

  def galaxy(rng: Rng = Rng()): System = {
    val maxSolarSysRadius = 14 * 500

    val radius = 500000
    val revs = 1
    val corePadding = 5000

    val armCount = 4

    val spiralConstant = radius / (revs * TauF)
    def theta(r: Float): Float = r / spiralConstant

    def generateArmPositions(mag: Float, baseAngle: Float): List[Vec2] = {
      //val (minRadialSeparation, maxRadialSeparation) = (maxSolarSysRadius, maxSolarSysRadius)

      val maxAngularDeviance = TauF / 30
      val angularDeviance = rng.nextNormalRange(-maxAngularDeviance, maxAngularDeviance)

      val angle = baseAngle + theta(mag) + angularDeviance

      if (mag + maxSolarSysRadius < radius) (Vec2 fromAngle (angle, mag)) ::
        generateArmPositions(mag + maxSolarSysRadius, baseAngle)

      else Nil
    }

    val positions = (for (baseAngle <- 0f until (TauF, TauF / armCount))
      yield generateArmPositions(corePadding, baseAngle)).flatten.toList

    val satellites = positions map (pos => {
      val partialSat = solarSystem(rng)
      val orbit = Orbit(pos.mag, Float.PositiveInfinity, pos.angle)
      System(partialSat.core, orbit, partialSat.satellites)
    })

    System(NoSatellite, NoOrbit, satellites)
  }

  def solarSystem(rng: Rng = Rng()): System = {
    val (minCoreRadius, maxCoreRadius) = (75, 500)
    val coreRadius = rng.nextLogUniformRange(minCoreRadius, maxCoreRadius, 2)

    val radius = rng.nextRange(2 * coreRadius, 14 * coreRadius)
    val corePadding = rng.nextRange(0.2f * coreRadius, 0.5f * coreRadius)
    
    def generateSatellites(spaceUsed: Float): List[System] = {
      val partialSat = planetarySystem(rng)

      val (minSatPadding, maxSatPadding) = (30, 400)
      val satPadding = rng.nextLogUniformRange(minSatPadding, maxSatPadding, 3)

      val orbitRadius = spaceUsed + partialSat.radius
      val period = rng.nextRange(10, 120)
      val satOrbit = Orbit(orbitRadius, period, rng.nextAngle)

      val sat = System(partialSat.core, satOrbit, partialSat.satellites)

      if (orbitRadius < radius) sat ::
        generateSatellites(orbitRadius + sat.radius + satPadding)

      else Nil
    }

    val core = Star(coreRadius)
    val satellites = generateSatellites(coreRadius + corePadding)

    System(core, NoOrbit, satellites)
  }

  def planetarySystem(rng: Rng = Rng()): System = {
    val (minCoreRadius, maxCoreRadius) = (5, 50)
    val coreRadius = rng.nextLogUniformRange(minCoreRadius, maxCoreRadius, 2)

    val radius = rng.nextRange(2 * coreRadius, 4 * coreRadius)
    val corePadding = rng.nextRange(0.4f * coreRadius, 0.8f * coreRadius)

    def generateSatellites(spaceUsed: Float): List[System] = {
      val partialSat = lunarSystem(rng)

      val (minSatPadding, maxSatPadding) = (1, 10)
      val satPadding = rng.nextLogUniformRange(minSatPadding, maxSatPadding, 2)

      val orbitRadius = spaceUsed + partialSat.radius
      val period = rng.nextRange(3, 30)
      val satOrbit = Orbit(orbitRadius, period, rng.nextAngle)

      val sat = System(partialSat.core, satOrbit, partialSat.satellites)

      if (orbitRadius < radius) sat ::
        generateSatellites(orbitRadius + sat.radius + satPadding)

      else Nil
    }

    val core = Planet(coreRadius)
    val satellites = generateSatellites(coreRadius + corePadding)

    System(core, NoOrbit, satellites)
  }

  def lunarSystem(rng: Rng = Rng()): System = {
    val (minCoreRadius, maxCoreRadius) = (0.5f, 5)
    val coreRadius = rng.nextLogUniformRange(minCoreRadius, maxCoreRadius, 2)

    val core = Moon(coreRadius)

    System(core)
  }
}
