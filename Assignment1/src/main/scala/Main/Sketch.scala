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

    background(0f)

    cam.updatePosition()
    cam.transform()
    //drawAreas(sys, t)
    //sys.satellites foreach  (s => drawOrbits(s, t))
    drawCores(sys, t)
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

  def drawCores(sys: System, t: Float, center: Vec2 = Vec2(0f, 0f)): Unit = {
    val pos = sys.position(t, center)
    val d = sys.core.diameter

    fill(1, 0, 1)
    noStroke()
    ellipse(pos.x, pos.y, d, d)

    sys.satellites foreach (drawCores(_, t, pos))
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