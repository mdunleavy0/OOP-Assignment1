package Util

import math.{abs, pow}
import util.Random

/**
  * Created by Michael Dunleavy on 15/11/2016.
  */
object Functions {
  def randRange(min: Float, max: Float, rng: Random = Random): Float = {
    min + (max - min) * rng.nextFloat
  }

  def randLogNormal(exp: Float = 2f, rng: Random = Random): Float = {
    val g = rng.nextGaussian
    val ln = pow(abs(g), exp)
    ln.toFloat
  }

  def randLogNormalRange(min: Float, max: Float, exp: Float = 2f, rng: Random = Random): Float = {
    min + (max - min) * randLogNormal(exp, rng)
  }
}
