package Main

/**
  * Created by Michael Dunleavy on 29/10/2016.
  */
trait Satellite {
  val radius: Float
  val diameter: Float = 2 * radius
  // TODO def color
}

case object NoSatellite extends Satellite {
  val radius = 0f
}

case class Star(radius: Float) extends Satellite {

}

case class Planet(radius: Float) extends Satellite {

}

case class Moon(radius: Float) extends Satellite {

}