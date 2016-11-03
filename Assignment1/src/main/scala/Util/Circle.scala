package Util

import math.abs


/**
  * Created by Michael Dunleavy on 01/11/2016.
  */
case class Circle(center: Vec2, radius: Float) {
  def intersects(point: Vec2): Boolean =
    abs(center dist point) <= abs(radius)

  def intersects(that: Circle): Boolean =
    abs(center dist that.center) <= abs(radius + that.radius)
}

object Circle {
  def apply(cenX: Float, cenY: Float, radius: Float) =
    new Circle(Vec2(cenX, cenY), radius)
}
