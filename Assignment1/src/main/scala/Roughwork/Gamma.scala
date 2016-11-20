package Roughwork

import processing.core._
import processing.core.PApplet._
import processing.core.PConstants._


/**
  * Created by micha on 20/11/2016.
  */
class Gamma extends PApplet {
  override def settings() = {
    size(winW, winH)
  }

  override def setup() = {
    background(255)
    noStroke()

    for (exp <- 0 until expCount) {
      val clr = color(random(100, 200), random(100, 200), random(100, 200))
      fill(clr)

      val expY = exp * (winH.toFloat / expCount)
      println(exp + ", " + expY)

      for (lane <- 0 until laneCount) {
        val y = expY + (lane + 0.5f) * (winH.toFloat / (expCount * laneCount))

        for (sample <- 0 until sampleCount) {
          val x = random(winW)

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
  val sampleCount = 5
}


object Gamma {
  def main(args: Array[String]) = {
    PApplet.main(Array[String]("Roughwork.Gamma"))
  }
}