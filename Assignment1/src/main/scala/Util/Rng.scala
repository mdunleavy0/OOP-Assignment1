package Util

import math.{abs, pow, Pi}

/**
  * Created by Michael Dunleavy on 20/11/2016.
  */
case class Rng(seed: Long = 0L) extends util.Random {
  setSeed(seed)

  private val defaultExp: Float = 2f
  final private val TauF: Float = 2f * Pi.toFloat

  private def lerp(x: Float, min: Float, max: Float): Float =
    min + x * (max - min)

  def nextRange(min: Float, max: Float): Float =
    lerp(nextFloat, min, max)

  def nextNormal: Float =
    nextGaussian.toFloat / 2 + 0.5f

  def nextNormalRange(min: Float, max: Float): Float =
    lerp(nextNormal, min, max)

  def nextLogUniform(exp: Float = defaultExp) =
    pow(abs(nextFloat), exp).toFloat

  def nextLogUniformRange(min: Float, max: Float, exp: Float = defaultExp): Float =
    lerp(nextLogUniform(exp), min, max)

  def nextLogNormal(exp: Float = defaultExp) =
    pow(abs(nextGaussian), exp).toFloat

  def nextLogNormalRange(min: Float, max: Float, exp: Float = defaultExp): Float =
    lerp(nextLogNormal(exp), min, max)

  def nextAngle: Float =
    lerp(nextFloat, 0, TauF)
}

