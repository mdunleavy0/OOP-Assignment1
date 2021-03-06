package Util

// language
import math._


/**
  * Created by Michael Dunleavy on 29/10/2016.
  */
class Vec2(val x: Float, val y: Float) {
  // angle-magnitude representation of vector
  lazy val mag: Float = hypot(x, y).toFloat
  lazy val angle = atan2(y, x).toFloat

  // zero vector test
  lazy val isZero: Boolean = (x == 0) && (y == 0)

  // arithmetic operations

  def +(that: Vec2): Vec2 = Vec2(x + that.x, y + that.y)
  def -(that: Vec2): Vec2 = Vec2(x - that.x, y - that.y)

  def *(scalar: Float): Vec2 = Vec2(x * scalar, y * scalar)
  def /(scalar: Float): Vec2 = Vec2(x / scalar, y / scalar)

  def *(that: Vec2): Float = x * that.x + y * that.y

  // dimensions between two vectors
  def dist(that: Vec2): Float = hypot(that.x - x, that.y - y).toFloat
  def angleBetween(that: Vec2): Float =
    if (isZero || that.isZero) throw new Error("Angle between zero vector is undefined.")
    else acos(this * that / (mag * that.mag)).toFloat

  def rotate(angle: Float): Vec2 = Vec2 fromAngle (this.angle + angle, mag)

  // map methods are the best
  def map(f: Float => Float): Vec2 = Vec2(f(x), f(y))

  // normalise vector into a forward vector
  lazy val norm: Vec2 = Vec2 fromAngle (angle, 1f)

  override def toString: String = "Vec2(" + x + "," + y + ")"
}

// companion object
object Vec2 {
  // default constructor wrapper
  def apply(x: Float, y: Float): Vec2 = new Vec2(x, y)

  // angle-magnitude constructor wrapper
  def fromAngle(angle: Float, mag: Float = 1f): Vec2 =
    Vec2(mag * cos(angle).toFloat, mag * sin(angle).toFloat)
}


