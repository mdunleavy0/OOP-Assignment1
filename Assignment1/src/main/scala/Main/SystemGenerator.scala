package Main

// 1st party
import Util.Circle
import Util.Color
import Util.Constants.TauF
import Util.Functions.mod
import Util.Rng
import Util.Vec2


/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
object SystemGenerator {

  // galaxy generation procedure
  def galaxy(rng: Rng = Rng()): System = {
    val radius = 500000
    val revs = 1
    val corePadding = 0

    val armCount = 4

    val spiralConstant = radius / (revs * TauF)
    def theta(r: Float): Float = r / spiralConstant

    val satPadding = 1000

    def generateArm(mag: Float, baseAngle: Float, acc: (List[System], List[Circle])): (List[System], List[Circle]) = {
      val magIncrement = 500

      val partialSat = solarSystem(rng)

      val maxAngularDeviance = TauF * (15f / 360)

      def findOrbit(mag: Float): Orbit = {
        val angularDeviance = rng.nextNormalRange(-maxAngularDeviance, maxAngularDeviance)
        val angle = baseAngle + theta(mag) + angularDeviance

        val orbit = Orbit(mag, Float.PositiveInfinity, angle)
        val orbitCircle = Circle(Vec2 fromAngle (angle, mag), partialSat.radius + satPadding)

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

  // solar system generation procedure
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

    def generateAsteroidBelt(spaceUsed: Float): List[System] = {
      val (minCount, maxCount) = (100, 500)
      val count = rng.nextRange(minCount, maxCount).toInt

      val (minWidth, maxWidth) = (50, 300)
      val width = rng.nextLogUniformRange(minWidth, maxWidth, 2)
      val halfWidth = width / 2

      val avgHue = rng.nextFloat

      val avgOrbitRadius = spaceUsed + halfWidth
      val orbitRadiusStdDev = halfWidth / 3

      val (minAvgPeriod, maxAvgPeriod) = (30, 90)
      val avgPeriod = rng.nextRange(minAvgPeriod, maxAvgPeriod)
      val periodStdDev = 0.1f * avgPeriod

      def generateAsteroid(): System = {
        val partialSat = asteroidSystem(avgHue, rng)

        val orbitRadius = rng.nextNormalRange(avgOrbitRadius - orbitRadiusStdDev, avgOrbitRadius + orbitRadiusStdDev)
        val period = rng.nextNormalRange(avgPeriod - periodStdDev, avgPeriod + periodStdDev)
        val satOrbit = Orbit(orbitRadius, period, rng.nextAngle)

        System(partialSat.core, satOrbit, partialSat.satellites)
      }

      val unfiltered = List.fill(count)(generateAsteroid())
      val filtered = unfiltered filter (sys => {
        (sys.orbit.radius > spaceUsed) && (sys.orbit.radius < spaceUsed + width)
      })
      val sorted = filtered.sortBy(_.orbit.radius)
      sorted
    }

    val core = Star(coreRadius, coreHue)
    val satellites = generateSatellites(coreRadius + corePadding)

    System(core, NoOrbit, satellites)
  }

  // planetary system generation procedure
  def planetarySystem(rng: Rng = Rng()): System = {
    val (minCoreRadius, maxCoreRadius) = (5, 50)
    val coreRadius = rng.nextLogUniformRange(minCoreRadius, maxCoreRadius, 2)

    val coreHue = rng.nextFloat

    val partialCore = Planet(coreRadius, coreHue)

    val ring: Option[PlanetRing] =
      if (rng.nextFloat < 0.5f) {
        val ringPadding = rng.nextRange(0.1f * coreRadius, 0.3f * coreRadius)
        val ringWidth = rng.nextRange(0.2f * coreRadius, 0.7f * coreRadius)
        val ringRadius = coreRadius + ringPadding + ringWidth / 2

        val ringHue = mod(rng.nextRange(coreHue - 0.15f, coreHue + 0.15f), 1f)
        val ringColor = Color(ringHue, partialCore.color.s - 0.1f, partialCore.color.b, 0.5f)

        Some(PlanetRing(ringRadius, ringWidth, ringColor))
      }
      else
        None

    val coreRadiusWithRing = ring match {
      case Some(r) => r.radius + r.width / 2
      case None => coreRadius
    }

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

    val core = Planet(coreRadius, coreHue, ring)
    val satellites = generateSatellites(coreRadiusWithRing + corePadding)

    System(core, NoOrbit, satellites)
  }

  // asteroid system generation procedure
  def asteroidSystem(avgHue: Float, rng: Rng = Rng()): System = {
    val (minCoreRadius, maxCoreRadius) = (0.25f, 5)
    val coreRadius = rng.nextLogUniformRange(minCoreRadius, maxCoreRadius, 2)

    val coreHue = mod(rng.nextRange(avgHue - 0.15f, avgHue + 0.15f), 1f)

    val core = Asteroid(coreRadius, coreHue)

    System(core)
  }

  // lunar system generation procedure
  def lunarSystem(rng: Rng = Rng()): System = {
    val (minCoreRadius, maxCoreRadius) = (0.5f, 5)
    val coreRadius = rng.nextLogUniformRange(minCoreRadius, maxCoreRadius, 2)

    val coreHue = rng.nextFloat

    val core = Moon(coreRadius, coreHue)

    System(core)
  }
}
