package Util

import processing.core._
import processing.event._


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
class CameraTest extends PApplet {
  override def settings() = {
    size(winW, winH)
  }

  override def setup() = {

  }

  override def draw() = {
    background(255)

    // grid lines
    pushMatrix()
    translate(winW / 2, winH / 2)
    strokeWeight(2)
    stroke(200)
    val maxDimension: Int = math.max(winW, winH)
    val halfMaxDim = maxDimension / 2
    for (i <- 0 to (halfMaxDim, 100)) {
      line(i, -halfMaxDim, i, halfMaxDim)
      line(-i, -halfMaxDim, -i, halfMaxDim)
      line(-halfMaxDim, i, halfMaxDim, i)
      line(-halfMaxDim, -i, halfMaxDim, -i)
    }
    popMatrix()

    cam.updatePosition()
    cam.transform()

    noStroke()

    drewC1 = (cam likelyShows c1) && (c1.radius * cam.scale > 1)
    if (drewC1) {
      fill(100, 200, 200)
      ellipse(c1.center.x, c1.center.y, c1.radius, c1.radius)
    }

    drewC2 = (cam likelyShows c2) && (c2.radius * cam.scale > 1)
    if (drewC2) {
      fill(100, 255, 255)
      ellipse(c2.center.x, c2.center.y, c2.radius, c2.radius)
    }

    cam.untransform()
  }

  val winW = 1000
  val winH = 1000

  var cam = new Camera(this)
  val c1 = Circle(0, 0, winW)
  val c2 = Circle(-winW / 4, 0, winW / 2)
  var drewC1 = false
  var drewC2 = false

  override def keyPressed(event: KeyEvent): Unit = {
    cam.keyPressed(event)
    if (key == ' ') {
      printf("Drew c1: %b, Drew c2: %b\n", drewC1, drewC2)
    }
  }

  override def keyReleased(event: KeyEvent): Unit =
    cam.keyReleased(event)

  override def mousePressed(event: MouseEvent): Unit =
    cam.mousePressed(event)

  override def mouseReleased(event: MouseEvent): Unit =
    cam.mouseReleased(event)

  override def mouseWheel(event: MouseEvent): Unit =
    cam.mouseWheel(event)
}


object CameraTest {
  def main(args: Array[String]) = {
    PApplet.main(Array[String]("Util.CameraTest"))
  }
}