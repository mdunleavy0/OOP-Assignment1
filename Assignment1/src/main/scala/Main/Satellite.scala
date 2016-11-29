package Main

// 1st party
import Util.Color


/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
trait Satellite {
  val radius: Float
  val diameter: Float = 2 * radius
  val color: Color
}

// empty object
case object NoSatellite extends Satellite {
  val radius = 0f
  val color = Color(0, 0, 0, 0)
}

// "self-commenting"

case class Star(radius: Float, hue: Float) extends Satellite {
  val color = Color(hue, 0.25f, 1)
}


case class Planet(radius: Float, hue: Float, ring: Option[PlanetRing] = None) extends Satellite {
  val color = Color(hue, 0.5f, 0.75f)
}

case class PlanetRing(radius: Float, width: Float, color: Color)


case class Asteroid(radius: Float, hue: Float) extends Satellite {
  val color = Color(hue, 0.5f, 0.25f)
}


case class Moon(radius: Float, hue: Float) extends Satellite {
  val color = Color(hue, 0.5f, 0.75f)
}



