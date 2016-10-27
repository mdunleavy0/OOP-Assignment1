import processing.core._
import processing.event._

import collection.mutable


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
class Camera(var sketch: PApplet) {
  var x: Float = 0f
  var y: Float = 0f
  var scale: Float = 1f

  var targetFps = 60

  var panTime = 0.5f
  var zoomSensitivity = 0.5f

  private def panLength: Float =
    sketch.width / (panTime * targetFps)

  private def halfSketchWidth: Float = sketch.width / 2
  private def halfSketchHeight: Float = sketch.height / 2

  private val keyStates: mutable.Map[String, Boolean] = mutable.Map(
    "w" -> false,
    "a" -> false,
    "s" -> false,
    "d" -> false
  )

  def transform(): Unit = {
    pan()
    sketch.translate(halfSketchWidth + x, halfSketchHeight + y)
    sketch.scale(scale)
  }

  def project(modelX: Float, modelY: Float): (Float, Float) = (
    (modelX * scale) + halfSketchWidth + x,
    (modelY * scale) + halfSketchHeight + y
  )

  def unproject(screenX: Float, screenY: Float): (Float, Float) = (
    (screenX - halfSketchWidth - x) / scale,
    (screenY - halfSketchHeight - y) / scale
  )

  def keySet(key: Char, toggle: Boolean): Unit = key.toLower match {
    case 'w' => keyStates("w") = toggle
    case 'a' => keyStates("a") = toggle
    case 's' => keyStates("s") = toggle
    case 'd' => keyStates("d") = toggle
    case _ => Unit
  }

  def mouseWheel(event: MouseEvent): Unit =
    scale += scale * event.getCount * zoomSensitivity

  def mouseDragged(): Unit = {
    x += sketch.mouseX - sketch.pmouseX
    y += sketch.mouseY - sketch.pmouseY
  }

  private def pan(): Unit = {
    if (keyStates("w")) y -= panLength
    if (keyStates("a")) x -= panLength
    if (keyStates("s")) y += panLength
    if (keyStates("d")) x += panLength
  }
}
