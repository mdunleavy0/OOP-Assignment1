package Roughwork

import processing.core._
import processing.core.PApplet._
import processing.core.PConstants._

import util.Random

/**
  * Created by Michael Dunleavy on 20/11/2016.
  */
class LogNormal extends PApplet {
  override def settings() = {
    size(winW, winH)
  }

  override def setup() = {
    val rng = new Random(millis)
    colorMode(HSB, 1f)
    background(1)
    noStroke()

    for (exp <- 1 to expCount) {
      val clr = color((exp - 1) * (1f / expCount), 0.5f, 0.75f)
      fill(clr)

      val expY = (exp - 1) * (winH.toFloat / expCount)

      for (lane <- 0 until laneCount) {
        val y = expY + (lane + 0.5f) * (winH.toFloat / (expCount * laneCount))

        for (sample <- 0 until sampleCount) {
          val g = rng.nextGaussian.toFloat
          val ln = pow(abs(g), exp)
          val x = ln * width

          ellipse(x, y, 10, 10)
        }
      }
    }
  }

  override def draw() = {

  }

  val winW = 1000
  val winH = 1000

  val expCount = 5
  val laneCount = 5
  val sampleCount = 25
}


object LogNormal {
  def main(args: Array[String]) = {
    PApplet.main(Array[String]("Roughwork.LogNormal"))
  }
}