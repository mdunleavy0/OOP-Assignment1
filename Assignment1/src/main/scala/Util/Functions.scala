package Util

import util.Random

/**
  * Created by Michael Dunleavy on 15/11/2016.
  */
object Functions {
  def randRange(min: Float, max: Float, rng: Random = Random): Float = {
    min + (max - min) * rng.nextFloat
  }
}
