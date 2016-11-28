package Main

import Util.Circle
import Util.Constants.TauF
import Util.Rng
import Util.Vec2


/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
object SystemGenerator {

  /*def galaxy(rng: Rng = Rng()): System = {
    val maxSolarSysRadius = 14 * 500

    val radius = 500000
    val revs = 1
    val corePadding = 0

    val armCount = 4

    val spiralConstant = radius / (revs * TauF)
    def theta(r: Float): Float = r / spiralConstant

    def generateArmPositions(mag: Float, baseAngle: Float): List[Vec2] = {
      val maxAngularDeviance = TauF * (15f / 360)
      val angularDeviance = rng.nextNormalRange(-maxAngularDeviance, maxAngularDeviance)

      val angle = baseAngle + theta(mag) + angularDeviance

      if (mag + maxSolarSysRadius < radius) (Vec2 fromAngle (angle, mag)) ::
        generateArmPositions(mag + 0.25f * maxSolarSysRadius, baseAngle)

      else Nil
    }

    val arms = (for (baseAngle <- 0f until (TauF, TauF / armCount))
      yield generateArmPositions(corePadding, baseAngle)).flatten.toList

    val sorted = arms.sortBy(_.mag)
    val maxCircles = sorted map (Circle(_, maxSolarSysRadius))

    def removeOverlaps(circles: List[Circle]): List[Circle] = circles match {
      case Nil => Nil
      case c :: cs =>
        if (cs forall (x => !(x intersects c))) c :: removeOverlaps(cs)
        else removeOverlaps(cs)
    }

    val nonOverlapping = sorted//removeOverlaps(maxCircles) map (_.center)

    val satellites = nonOverlapping map (pos => {
      val partialSat = solarSystem(rng)
      val orbit = Orbit(pos.mag, Float.PositiveInfinity, pos.angle)
      System(partialSat.core, orbit, partialSat.satellites)
    })

    System(NoSatellite, NoOrbit, satellites)
  }*/

  def galaxy(rng: Rng = Rng()): System = {
    val radius = 500000
    val revs = 1
    val corePadding = 0

    val armCount = 4

    val spiralConstant = radius / (revs * TauF)
    def theta(r: Float): Float = r / spiralConstant

    def generateArm(mag: Float, baseAngle: Float, acc: (List[System], List[Circle])): (List[System], List[Circle]) = {
      val magIncrement = 500

      val partialSat = solarSystem(rng)

      val maxAngularDeviance = TauF * (15f / 360)

      def findOrbit(mag: Float): Orbit = {
        val angularDeviance = rng.nextNormalRange(-maxAngularDeviance, maxAngularDeviance)
        val angle = baseAngle + theta(mag) + angularDeviance

        val orbit = Orbit(mag, Float.PositiveInfinity, angle)
        val orbitCircle = Circle(Vec2 fromAngle (angle, mag), partialSat.radius)

        if (acc._2 forall (c => !(c intersects orbitCircle)))
          orbit
        else
          findOrbit(mag + magIncrement)
      }

      val satOrbit = findOrbit(mag)
      val orbitPos = Vec2 fromAngle (satOrbit.phaseAngle, satOrbit.radius)
      val orbitCircle = Circle(orbitPos, partialSat.radius)

      val sat = System(partialSat.core, satOrbit, partialSat.satellites)

      if (satOrbit.radius < radius) generateArm(
        satOrbit.radius + magIncrement,
        baseAngle,
        (sat :: acc._1, orbitCircle :: acc._2)
      )

      else acc
    }

    val baseAngles = 0f until (TauF, TauF / armCount)

    val unsorted = baseAngles.foldLeft((List[System](), List[Circle]()))((b, a) => generateArm(corePadding, a, b))
    val satellites = unsorted._1 sortBy (_.orbit.radius)

    System(NoSatellite, NoOrbit, satellites)
  }

  def solarSystem(rng: Rng = Rng()): System = {
    val (minCoreRadius, maxCoreRadius) = (75, 500)
    val coreRadius = rng.nextLogUniformRange(minCoreRadius, maxCoreRadius, 2)

    val radius = rng.nextRange(2 * coreRadius, 14 * coreRadius)
    val corePadding = rng.nextRange(0.2f * coreRadius, 0.5f * coreRadius)

    val coreHue = rng.nextFloat
    
    def generateSatellites(spaceUsed: Float): List[System] = {
      val (minSatPadding, maxSatPadding) = (30, 400)
      val satPadding = rng.nextLogUniformRange(minSatPadding, maxSatPadding, 3)

      if (rng.nextFloat < 0.1) {
        val belt = generateAsteroidBelt(spaceUsed)
        val beltEnd = belt match {
          case _ :+ last => last.orbit.radius + last.core.radius
          case Nil => spaceUsed
        }
        val beltCenter = (spaceUsed + beltEnd) / 2

        if (beltCenter < radius) belt ++
          generateSatellites(beltEnd + satPadding)

        else Nil
      }

      else {
        val partialSat = planetarySystem(rng)

        val orbitRadius = spaceUsed + partialSat.radius
        val period = rng.nextRange(10, 120)
        val satOrbit = Orbit(orbitRadius, period, rng.nextAngle)

        val sat = System(partialSat.core, satOrbit, partialSat.satellites)

        if (orbitRadius < radius) sat ::
          generateSatellites(orbitRadius + sat.radius + satPadding)

        else Nil
      }
    }



    val core = Star(coreRadius, coreHue)
    val satellites = generateSatellites(coreRadius + corePadding)

    System(core, NoOrbit, satellites)
  }

  def generateAsteroidBelt(spaceUsed: Float, rng: Rng = Rng()): List[System] = {
    val (minCount, maxCount) = (100, 500)
    val count = rng.nextRange(minCount, maxCount).toInt
    println("Asteroid count: " + count)

    val (minWidth, maxWidth) = (50, 300)
    val width = rng.nextLogUniformRange(minWidth, maxWidth, 2)
    val halfWidth = width / 2
    println("Belt width: " + width)

    val avgOrbitRadius = spaceUsed + halfWidth
    val orbitRadiusStdDev = halfWidth / 3
    println("Belt average orbit radius: " + avgOrbitRadius)

    val (minAvgPeriod, maxAvgPeriod) = (30, 90)
    val avgPeriod = rng.nextRange(minAvgPeriod, maxAvgPeriod)
    val periodStdDev = 0.1f * avgPeriod
    println("Belt average period: " + avgPeriod)

    def generateAsteroid(): System = {
      val partialSat = asteroidSystem(rng)

      val orbitRadius = rng.nextNormalRange(avgOrbitRadius - orbitRadiusStdDev, avgOrbitRadius + orbitRadiusStdDev)
      val period = rng.nextNormalRange(avgPeriod - periodStdDev, avgPeriod + periodStdDev)
      val satOrbit = Orbit(orbitRadius, period, rng.nextAngle)

      System(partialSat.core, satOrbit, partialSat.satellites)
    }

    val unsorted = List.fill(count)(generateAsteroid())
    unsorted.sortBy(_.orbit.radius)
  }

  def planetarySystem(rng: Rng = Rng()): System = {
    val (minCoreRadius, maxCoreRadius) = (5, 50)
    val coreRadius = rng.nextLogUniformRange(minCoreRadius, maxCoreRadius, 2)

    val radius = rng.nextRange(2 * coreRadius, 4 * coreRadius)
    val corePadding = rng.nextRange(0.4f * coreRadius, 0.8f * coreRadius)

    val coreHue = rng.nextFloat

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

    val core = Planet(coreRadius, coreHue)
    val satellites = generateSatellites(coreRadius + corePadding)

    System(core, NoOrbit, satellites)
  }

  def asteroidSystem(rng: Rng = Rng()): System = {
    val (minCoreRadius, maxCoreRadius) = (0.25f, 5)
    val coreRadius = rng.nextLogUniformRange(minCoreRadius, maxCoreRadius, 2)

    val coreHue = rng.nextFloat

    val core = Asteroid(coreRadius, coreHue)

    System(core)
  }

  def lunarSystem(rng: Rng = Rng()): System = {
    val (minCoreRadius, maxCoreRadius) = (0.5f, 5)
    val coreRadius = rng.nextLogUniformRange(minCoreRadius, maxCoreRadius, 2)

    val coreHue = rng.nextFloat

    val core = Moon(coreRadius, coreHue)

    System(core)
  }
}
