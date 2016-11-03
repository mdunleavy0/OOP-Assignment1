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

    fill(100, 200, 200)
    ellipse(c1.center.x, c1.center.y, c1.radius, c1.radius)

    fill(100, 255, 255)
    ellipse(c2.center.x, c2.center.y, c2.radius, c2.radius)

    strokeWeight(5)
    stroke(0)

    fill(255, 0, 0)
    ellipse(cam.pos.x, cam.pos.y, 50, 50)

    fill(255, 0, 0)
    val unprojection = cam.unproject(Vec2(500, 500))
    ellipse(unprojection.x, unprojection.y, 25, 25)

    cam.untransform()
  }

  val winW = 1000
  val winH = 1000

  var cam = new Camera(this)
  val c1 = Circle(0, 0, winW)
  val c2 = Circle(-winW / 4, 0, winW / 2)

  override def keyPressed(event: KeyEvent): Unit = {
    cam.keyPressed(event)
    if (key == ' ') {
      println("cam.pos: " + cam.pos)
      println("project(cam.pos): " + cam.project(cam.pos))
      println("unproject(0, 0): " + cam.unproject(Vec2(0, 0)))
      println("circumcircle: " + cam.circumcircle)
      printf("c1: %b, c2: %b\n\n", cam likelyShows c1, cam likelyShows c2)
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