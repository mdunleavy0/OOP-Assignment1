import processing.core._
import processing.core.PApplet._
import processing.core.PConstants._
import processing.event.MouseEvent


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
class Sketch extends PApplet {
  override def settings() = {
    size(winW, winH)
  }

  override def setup() = {

  }

  override def draw() = {
    cam.transform()

    background(255)
    noStroke()

    fill(100, 200, 200)
    ellipse(winW / 2, winH / 2, winW, winH)

    fill(100, 255, 255)
    ellipse(winW / 4, winH / 2, winW / 2, winH / 2)
  }

  val winW = 1000
  val winH = 1000

  var cam = new Camera(this)
  val panSensitivity = 10f
  val zoomSensitivity = 0.1f

  override def keyPressed(): Unit = key.toLower match {
    case 'w' => cam.y -= panSensitivity
    case 'a' => cam.x -= panSensitivity
    case 's' => cam.y += panSensitivity
    case 'd' => cam.x += panSensitivity
  }

  override def mouseWheel(event: MouseEvent): Unit = {
    cam.scale += event.getCount * zoomSensitivity
  }
}


object Sketch {
  def main(args: Array[String]) = {
    PApplet.main(Array[String]("Sketch"))
  }
}