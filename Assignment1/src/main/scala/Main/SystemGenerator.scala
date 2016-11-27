package Main

import Util.Rng

/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
object SystemGenerator {
  def galaxy(rng: Rng = Rng()): System = {
    val radius = 100000
    val corePadding = 5000

    def generateSatellites(spaceUsed: Float): List[System] = {
      val sat = solarSystem(rng)
      val satPadding = 1000
      val orbitRadius = spaceUsed + sat.radius
      val satOrbit = Orbit(orbitRadius)
      val sat1 = System(sat.core, satOrbit, sat.satellites)

      if (orbitRadius < radius) sat1 ::
        generateSatellites(orbitRadius + sat.radius + satPadding)

      else Nil
    }

    val satellites = generateSatellites(corePadding)
    System(NoSatellite, NoOrbit, satellites)
  }

  def solarSystem(rng: Rng = Rng()): System = {
    val radius = 1000
    val coreRadius = 100
    val corePadding = 50
    
    def generateSatellites(spaceUsed: Float): List[System] = {
      val sat = planetarySystem(rng)
      val satPadding = 30
      val orbitRadius = spaceUsed + sat.radius
      val satOrbit = Orbit(orbitRadius, 100)
      val sat1 = System(sat.core, satOrbit, sat.satellites)

      if (orbitRadius < radius) sat1 ::
        generateSatellites(orbitRadius + sat.radius + satPadding)

      else Nil
    }

    val core = Star(coreRadius)
    val satellites = generateSatellites(coreRadius + corePadding)

    System(core, NoOrbit, satellites)
  }

  def planetarySystem(rng: Rng = Rng()): System = {
    val radius = 100
    val coreRadius = 10
    val corePadding = 5

    def generateSatellites(spaceUsed: Float): List[System] = {
      val sat = lunarSystem(rng)
      val satPadding = 3
      val orbitRadius = spaceUsed + sat.radius
      val satOrbit = Orbit(orbitRadius, 10)
      val sat1 = System(sat.core, satOrbit, sat.satellites)

      if (orbitRadius < radius) sat1 ::
        generateSatellites(orbitRadius + sat.radius + satPadding)

      else Nil
    }

    val core = Planet(coreRadius)
    val satellites = generateSatellites(coreRadius + corePadding)

    System(core, NoOrbit, satellites)
  }

  def lunarSystem(rng: Rng = Rng()): System = {
    val coreRadius = 1
    val core = Moon(coreRadius)

    System(core)
  }
}
