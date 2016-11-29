package Util

// 1st party
import Constants.TauF

// language
import math.{abs, pow}

/**
  * Created by Michael Dunleavy on 20/11/2016.
  */
case class Rng(seed: Long = 0L) extends util.Random {
  setSeed(seed)

  // default exponent for log based distributions
  private val defaultExp: Float = 2f

  // linear interpolation
  private def lerp(x: Float, min: Float, max: Float): Float =
    min + x * (max - min)

  // uniform range
  def nextRange(min: Float, max: Float): Float =
    lerp(nextFloat, min, max)

  // gaussian mapped from range (+/- 1 std deviation) to range (0 to 1)
  def nextNormal: Float =
    nextGaussian.toFloat / 2 + 0.5f

  // gaussian mapped from range (+/- 1 std deviation) to range (min to max)
  def nextNormalRange(min: Float, max: Float): Float =
    lerp(nextNormal, min, max)

  // power of a random float
  def nextLogUniform(exp: Float = defaultExp) =
    pow(abs(nextFloat), exp).toFloat

  // power of a random float mapped to range (min to max)
  def nextLogUniformRange(min: Float, max: Float, exp: Float = defaultExp): Float =
    lerp(nextLogUniform(exp), min, max)

  // power of a normal
  def nextLogNormal(exp: Float = defaultExp) =
    pow(abs(nextGaussian), exp).toFloat

  // power of a normal mapped to range (min to max)
  def nextLogNormalRange(min: Float, max: Float, exp: Float = defaultExp): Float =
    lerp(nextLogNormal(exp), min, max)

  // power of a random float mapped to range (0 to tau)
  def nextAngle: Float =
    lerp(nextFloat, 0, TauF)
}

