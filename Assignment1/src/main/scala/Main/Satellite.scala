package Main

/**
  * Created by Michael Dunleavy on 29/10/2016.
  */
trait Satellite {
  def radius: Float
  // TODO def color
}

case object NoSatellite extends Satellite {
  def radius = 0f
}

case class Star(radius: Float) extends Satellite {

}

case class Planet(radius: Float) extends Satellite {

}

case class Moon(radius: Float) extends Satellite {

}