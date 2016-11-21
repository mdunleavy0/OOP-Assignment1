package Main


import Util.Color


/**
  * Created by Michael Dunleavy on 29/10/2016.
  */
trait Satellite {
  val radius: Float
  val diameter: Float = 2 * radius
  val color: Color
}


case object NoSatellite extends Satellite {
  val radius = 0f
  val color = Color()
}


case class Star(radius: Float, hue: Float) extends Satellite {
  val color = Color(hue, 0.25f, 1f)
}


case class Planet(radius: Float, hue: Float) extends Satellite {
  val color = Color(hue, 0.5f, 0.5f)
}


case class Moon(radius: Float, hue: Float) extends Satellite {
  val color = Color(hue, 0.5f, 0.5f)
}