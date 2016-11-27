package Main

import Util.Camera
import Util.Circle
import Util.Rng
import Util.Vec2

import processing.core._
import processing.core.PApplet._
import processing.core.PConstants._
import processing.event._


/**
  * Created by Michael Dunleavy on 27/11/2016.
  */
class Sketch extends PApplet {
  override def settings() = {
    size(winW, winH)
  }

  override def setup() = {
    frameRate(targetFps)
    cam.targetFps = targetFps
    colorMode(HSB, 1f)
  }

  override def draw() = {
    val t: Float = frameCount.toFloat / targetFps

    background(0)

    val v1 = visibleSystems(sys, t)
    val v2 = visibleOrbits(sys, t)

    cam.updatePosition()
    cam.transform()
    drawAreas(sys, t)
    drawOrbits(v2, t)
    drawCores(v1, t)
    cam.untransform()
  }

  val targetFps = 60

  val winW = 2000
  val winH = 1125

  val cam = Camera(this)

  val sys = System(Star(100), NoOrbit, List(
    System(Planet(10), Orbit(200, 100), Nil),
    System(Planet(10), Orbit(300, 100), List(
      System(Moon(1), Orbit(20, 10), Nil),
      System(Moon(1), Orbit(30, 10), Nil)
    ))
  ))

  def visibleSystems(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): System = {
    val pos = sys.position(t, center)
    val sysCircle = Circle(pos, sys.radius)

    if (cam likelyShows sysCircle) System(
      sys.core,
      sys.orbit,
      sys.satellites map (visibleSystems(_, t, pos)) filter (_ != NoSystem)
    )

    else NoSystem
  }

  def visibleOrbits(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): System = {
    val pos = sys.position(t, center)
    val orbCircle = Circle(center, sys.orbit.radius + sys.radius)

    if (cam likelyShows orbCircle) System(
      sys.core,
      sys.orbit,
      sys.satellites map (visibleOrbits(_, t, pos)) filter (_ != NoSystem)
    )

    else NoSystem
  }

  def drawCores(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): Unit = {
    val pos = sys.position(t, center)
    val d = sys.core.diameter

    fill(1, 0, 1)
    noStroke()
    ellipse(pos.x, pos.y, d, d)

    sys.satellites foreach (drawCores(_, t, pos))
  }

  def drawOrbits(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): Unit = {
    val pos = sys.position(t, center)
    val d = sys.orbit.diameter

    noFill()
    stroke(1, 0, 1)
    strokeWeight(1)
    ellipse(center.x, center.y, d, d)

    sys.satellites foreach (drawOrbits(_, t, pos))
  }

  def drawAreas(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): Unit = {
    val pos = sys.position(t, center)
    val d = sys.diameter

    fill(0, 1, 1, 0.1f)
    noStroke()
    ellipse(pos.x, pos.y, d, d)

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