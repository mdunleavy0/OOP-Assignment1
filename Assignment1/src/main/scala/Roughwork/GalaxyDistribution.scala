package Roughwork

import Util.Circle
import Util.Rng
import Util.Vec2

import processing.core._
import processing.core.PApplet._
import processing.core.PConstants._


/**
  * Created by micha on 27/11/2016.
  */
class GalaxyDistribution extends PApplet {
  override def settings() = {
    size(winW, winH)
  }

  override def setup() = {
    translate(winW / 2, winH / 2)

    background(0)

    stroke(255)
    strokeWeight(1)
    line(-10, 0, 10, 0)
    line(0, -10, 0, 10)

    var circles = (for (theta <- List(0, 60, 120, 180, 240, 300))
      yield arm(radians(theta), maxSolarSysRadius / 2, winW / 2)).toArray.flatten
    println("Circle count: " + circles.length)

    /*def removeOverlaps(arr: Array[Circle]): Array[Circle] = arr match {
      case Array() => Array.empty
      case (c: Circle) +: cs =>
        if (cs forall (x => !x.intersects(c))) arr
        else cs
    }

    circles = removeOverlaps(circles)

    circles foreach (c => drawSolarSys(c.center))*/
  }

  override def draw() = {

  }

  val winW = 1000
  val winH = 1000

  val rng = Rng(millis())

  val maxSolarSysRadius = 50
  val deviance = radians(10)


  def drawSolarSys(pos: Vec2): Unit = {
    val coreRadius = rng.nextRange(1, 10)
    val sysRadius = rng.nextRange(2 * coreRadius, 5 * coreRadius)

    noFill()
    stroke(255)
    strokeWeight(1)
    //ellipse(pos.x, pos.y, 2 * maxSolarSysRadius, 2 * maxSolarSysRadius)
    ellipse(pos.x, pos.y, 2 * sysRadius, 2 * sysRadius)

    fill(255)
    noStroke()
    ellipse(pos.x, pos.y, 2 * coreRadius, 2 * coreRadius)
  }

  def arm(baseAngle: Float, start: Float, end: Float): Array[Circle] = {
    val angle = rng.nextNormalRange(baseAngle - deviance, baseAngle + deviance)
    val pos = Vec2 fromAngle(angle, start)
    val circle = Circle(pos, maxSolarSysRadius)

    val newStart = start + rng.nextRange(0.25f, 1) * maxSolarSysRadius

    if (newStart > end)
      circle +: Array.empty
    else
      circle +: arm(baseAngle + radians(5), newStart, end)
  }
}


object GalaxyDistribution {
  def main(args: Array[String]) = {
    PApplet.main(Array[String]("Roughwork.GalaxyDistribution"))
  }
}