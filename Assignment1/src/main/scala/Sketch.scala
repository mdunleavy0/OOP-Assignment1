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
  }

  override def draw() = {
    val t: Float = frameCount.toFloat / targetFps
    if (frameCount % targetFps == 0) background(100, 0, 0)
    else background(0)
    drawSystem(sys, t, width / 2, height / 2)
  }

  // by contrast, frameRate gives the real fps
  val targetFps = 60

  val winW = 1000
  val winH = 1000

  val sys = Star(50, 0, Float.PositiveInfinity, 0, List(
    Planet(25, 150, 2, 0, Nil),
    Planet(25, 300, 2, 1, List(
      Moon(5, 50, 2, 0),
      Moon(10, 80, 2, 0)
    ))
  ))

  def drawSystem(sys: System, t: Float, x: Float, y: Float): Unit = {
    val diameter = 2 * sys.radius
    val orbitDiameter = 2 * sys.orbitRadius

    val (sysX, sysY) = sys.position(t, x, y)

    stroke(100)
    strokeWeight(3)
    noFill()
    ellipse(x, y, orbitDiameter, orbitDiameter)

    fill(255)
    noStroke()
    ellipse(sysX, sysY, diameter, diameter)

    sys.satellites foreach (drawSystem(_, t, sysX, sysY))
  }
}


object Sketch {
  def main(args: Array[String]) = {
    PApplet.main(Array[String]("Sketch"))
  }
}