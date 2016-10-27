import processing.core._
import processing.event._


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
class Camera(var sketch: PApplet, var x: Float = 0f, var y: Float = 0f, var scale: Float = 1f) {
  val panSensitivity = 100f
  val zoomSensitivity = 0.1f

  def transform(): Unit = {
    sketch.translate(x, y)
    sketch.scale(scale)
  }

  def keyPressed(): Unit = sketch.key.toLower match {
    case 'w' => y -= panSensitivity
    case 'a' => x -= panSensitivity
    case 's' => y += panSensitivity
    case 'd' => x += panSensitivity
    case _ => Unit
  }

  def mouseWheel(event: MouseEvent): Unit = {
    scale += event.getCount * zoomSensitivity
  }

  def mouseDragged(): Unit = {
    x += sketch.mouseX - sketch.pmouseX
    y += sketch.mouseY - sketch.pmouseY
  }
}
