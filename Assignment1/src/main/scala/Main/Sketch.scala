package Main

import Util.Vec2

import processing.core._
import processing.core.PApplet._
import processing.core.PConstants._


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
class Sketch extends PApplet {
  override def settings() = {
    size(winW, winH)
  }

  override def setup() = {
    frameRate(targetFps)
    println(sys.radius)
  }

  override def draw() = {
    val t: Float = frameCount.toFloat / targetFps
    if (frameCount % targetFps == 0) background(100, 0, 0)
    else background(0)
    drawSystem(sys, t, Vec2(width / 2, height / 2))
  }

  // by contrast, frameRate gives the real fps
  val targetFps = 60

  val winW = 1000
  val winH = 1000

  val sys = SolarSystem(Star(50), Orbit(), List(
    PlanetarySystem(Planet(25), Orbit(150, 2), Nil),
    PlanetarySystem(Planet(25), Orbit(300, 2, PI), List(
      LunarSystem(Moon(5), Orbit(50, 2)),
      LunarSystem(Moon(10), Orbit(80, 2))
    ))
  ))

  def drawSystem(sys: System, t: Float, center: Vec2): Unit = {
    val pos = sys.position(t, center)

    // draw orbit path
    stroke(100)
    strokeWeight(3)
    noFill()
    ellipse(center.x, center.y, sys.orbit.diameter, sys.orbit.diameter)

    // draw core
    fill(255)
    noStroke()
    ellipse(pos.x, pos.y, sys.core.diameter, sys.core.diameter)

    // draw satellites
    sys.satellites foreach (drawSystem(_, t, pos))
  }
}


object Sketch {
  def main(args: Array[String]) = {
    PApplet.main(Array[String]("Main.Sketch"))
  }
}