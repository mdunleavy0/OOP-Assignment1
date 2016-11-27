package Main


/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
trait Satellite {
  val radius: Float
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


