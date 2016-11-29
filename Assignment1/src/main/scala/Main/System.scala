package Main

// 1st party
import Util.Vec2


/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
case class System(
  core: Satellite = NoSatellite,
  orbit: Orbit = NoOrbit,
  satellites: List[System] = Nil
) {
  // radius of system circumcircle
  lazy val radius: Float = satellites match {
    case _ :+ last => last.orbit.radius + last.radius
    case _ => core.radius
  }

  lazy val diameter: Float = 2 * radius

  // formula for system position a at given time relative to an orbital center
  def position(time: Float, center: Vec2): Vec2 = center + orbit.displacement(time)

  // replication of scala default collections' .find()
  def find(p: System => Boolean): Option[System] = {
    if (p(this))
      Some(this)

    else
      satellites flatMap (_ find p) match {
        case head :: _ => Some(head)
        case Nil => None
      }
  }

  // more useful but messy, replication of scala default collections' .find()
  def findWithPosition(p: (System, Vec2) => Boolean, time: Float, center: Vec2 = Vec2(0, 0)): Option[(System, Vec2)] = {
    val sysPos = position(time, center)

    if (p(this, sysPos))
      Some(this, sysPos)

    else
      satellites flatMap (_.findWithPosition(p, time, sysPos)) match {
        case head :: _ => Some(head)
        case Nil => None
      }
  }
}


// empty object
object NoSystem extends System()
