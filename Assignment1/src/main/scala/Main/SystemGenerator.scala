package Main

import Util.Rng

/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
object SystemGenerator {

  def planetarySystem(rng: Rng = Rng()): System = {
    val radius = 100
    val coreRadius = 10
    val corePadding = 5

    def fillSatellites(spaceUsed: Float): List[System] = {
      val sat = lunarSystem(rng)
      val satPadding = 3
      val orbitRadius = spaceUsed + sat.radius
      val satOrbit = Orbit(orbitRadius, 10)
      val sat1 = System(sat.core, satOrbit, sat.satellites)

      if (orbitRadius < radius) sat1 ::
        fillSatellites(orbitRadius + sat.radius + satPadding)

      else Nil
    }

    val core = Planet(coreRadius)
    val satellites = fillSatellites(coreRadius + corePadding)

    System(core, NoOrbit, satellites)
  }

  def lunarSystem(rng: Rng = Rng()): System = {
    val coreRadius = 1
    val core = Moon(coreRadius)

    System(core)
  }
}
