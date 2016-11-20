package Util

import math.{abs, pow}

/**
  * Created by Michael Dunleavy on 20/11/2016.
  */
class Rng extends util.Random {
  private val defaultExp: Float = 2f

  private def lerp(x: Float, min: Float, max: Float): Float =
    min + x * (max - min)

  def nextRange(min: Float, max: Float): Float =
    lerp(nextFloat, min, max)

  def nextLogUniform(exp: Float = defaultExp) =
    pow(abs(nextFloat), exp).toFloat

  def nextLogUniformRange(min: Float, max: Float, exp: Float = defaultExp): Float =
    lerp(nextLogUniform(exp), min, max)

  def nextLogNormal(exp: Float = defaultExp) =
    pow(abs(nextGaussian), exp).toFloat

  def nextLogNormalRange(min: Float, max: Float, exp: Float = defaultExp): Float =
    lerp(nextLogNormal(exp), min, max)
}
