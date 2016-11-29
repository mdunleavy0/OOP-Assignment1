package Util


/**
  * Created by Michael Dunleavy on 15/11/2016.
  */
object Functions {
  // mathematically correct modulo operations
  // the price Scala pays for Java interoperability
  def mod(a: Int, b: Int) = (a % b + b) % b
  def mod(a: Float, b: Float) = (a % b + b) % b
}
