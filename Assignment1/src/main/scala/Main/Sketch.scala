package Main

import Util.Camera
import Util.Circle
import Util.Vec2

import processing.core._
import processing.core.PApplet._
import processing.core.PConstants._
import processing.event._


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
class Sketch extends PApplet {
  override def settings() = {
    size(winW, winH)
  }

  override def setup() = {
    frameRate(targetFps)
    cam.targetFps = targetFps

    println("Solar system radius: " + sys.radius)
    sys.satellites foreach (ps => println("Planetary system radius: " + ps.radius))
  }

  override def draw() = {
    val t: Float = frameCount.toFloat / targetFps

    background(0)
    systemsDrawn = 0

    cam.updatePosition()
    cam.transform()
    drawAreas(sys, t)
    drawOrbits(sys, t)
    drawCores(sys, t)
    cam.untransform()
  }

  // by contrast, frameRate gives the real fps
  val targetFps = 60

  val winW = 2000
  val winH = 1125

  val cam = Camera(this)

  /*val sys = SolarSystem(Star(100), Orbit(), List(
    PlanetarySystem(Planet(4), Orbit(100, 10), Nil),
    PlanetarySystem(Planet(9), Orbit(200, 10), Nil),
    PlanetarySystem(Planet(10), Orbit(300, 10), Nil),
    PlanetarySystem(Planet(6), Orbit(400, 10), Nil),
    PlanetarySystem(Planet(50), Orbit(500, 10), Nil),
    PlanetarySystem(Planet(40), Orbit(600, 10), Nil),
    PlanetarySystem(Planet(35), Orbit(700, 10), Nil),
    PlanetarySystem(Planet(30), Orbit(800, 10), Nil)
  ))*/

  val sys = SolarSystem fromProcedure 500f

  var systemsDrawn = 0

  def drawCores(sys: System, t: Float, center: Vec2 = Vec2(0f, 0f)): Unit = {
    val pos = sys.position(t, center)
    val sysCircle = Circle(pos, sys.radius)

    if (cam likelyShows sysCircle) {
      // draw core
      fill(255)
      noStroke()
      ellipse(pos.x, pos.y, sys.core.diameter, sys.core.diameter)

      // draw satellites
      sys.satellites foreach (drawCores(_, t, pos))
    }
  }

  def drawOrbits(sys: System, t: Float, center: Vec2 = Vec2(0f, 0f)): Unit = {
    val pos = sys.position(t, center)
    val sysCircle = Circle(center, sys.orbit.radius + sys.radius)

    if (cam likelyShows sysCircle) {
      // draw orbit path
      stroke(100)
      strokeWeight(0.25f)
      noFill()
      ellipse(center.x, center.y, sys.orbit.diameter, sys.orbit.diameter)

      // draw satellites
      sys.satellites foreach (drawOrbits(_, t, pos))
    }
  }

  def drawAreas(sys: System, t: Float, center: Vec2 = Vec2(0f, 0f)): Unit = {
    val pos = sys.position(t, center)

    noStroke()
    fill(255, 0, 0, 0.1f * 255)
    ellipse(pos.x, pos.y, 2 * sys.radius, 2 * sys.radius)

    sys.satellites foreach (drawAreas(_, t, pos))
  }

  override def keyPressed(event: KeyEvent): Unit =
    cam.keyPressed(event)

  override def keyReleased(event: KeyEvent): Unit =
    cam.keyReleased(event)

  override def mousePressed(event: MouseEvent): Unit =
    cam.mousePressed(event)

  override def mouseReleased(event: MouseEvent): Unit =
    cam.mouseReleased(event)

  override def mouseWheel(event: MouseEvent): Unit =
    cam.mouseWheel(event)
}


object Sketch {
  def main(args: Array[String]) = {
    PApplet.main(Array[String]("Main.Sketch"))
  }
}