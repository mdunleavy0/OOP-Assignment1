package Util


/**
  * Created by Michael Dunleavy on 15/11/2016.
  */
object Functions {
  def mod(a: Int, b: Int) = (a % b + b) % b
  def mod(a: Float, b: Float) = (a % b + b) % b
}
