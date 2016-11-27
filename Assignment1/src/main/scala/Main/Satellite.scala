package Main


/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
trait Satellite {
  val radius: Float
  val diameter: Float = 2 * radius

  /*val minRadius: Float
  val maxRadius: Float

  val minPadding: Float
  val maxPadding: Float*/
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



