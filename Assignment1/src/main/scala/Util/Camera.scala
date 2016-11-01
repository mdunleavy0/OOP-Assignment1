package Util

import processing.core._
import processing.core.PConstants._
import processing.event._

import scala.collection.mutable


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
class Camera(var sketch: PApplet) {
  var pos: Vec2 = Vec2(0f, 0f)
  var scale: Float = 1f

  var targetFps = 60

  var panTime = 0.5f
  var zoomSensitivity = 0.5f

  private def panLength: Float =
    sketch.width / (panTime * targetFps)

  private def halfSketchWidth: Float = sketch.width / 2
  private def halfSketchHeight: Float = sketch.height / 2

  private val digitalInputs: mutable.Map[String, Boolean] = mutable.Map(
    "MOUSE_LEFT" -> false,
    "w" -> false,
    "a" -> false,
    "s" -> false,
    "d" -> false
  )

  def transform(): Unit = {
    updatePosition()
    sketch.translate(halfSketchWidth + pos.x, halfSketchHeight + pos.y)
    sketch.scale(scale)
  }

  def project(model: Vec2): Vec2 = Vec2(
    model.x * scale + halfSketchWidth + pos.x,
    model.y * scale + halfSketchHeight + pos.y
  )

  def unproject(screen: Vec2): Vec2 = Vec2(
    (screen.x - halfSketchWidth - pos.x) / scale,
    (screen.y - halfSketchHeight - pos.y) / scale
  )

  def updatePosition(): Unit = {
    var x = pos.x
    var y = pos.y

    if (digitalInputs("w")) y -= panLength
    if (digitalInputs("a")) x -= panLength
    if (digitalInputs("s")) y += panLength
    if (digitalInputs("d")) x += panLength

    if (digitalInputs("MOUSE_LEFT")) {
      x += sketch.mouseX - sketch.pmouseX
      y += sketch.mouseY - sketch.pmouseY
    }

    pos = Vec2(x, y)
  }

  def toggleDigitalInput(input: String, state: Boolean): Unit =
    if (digitalInputs contains input) digitalInputs(input) = state

  def keyPressed(event: KeyEvent): Unit =
    toggleDigitalInput(sketch.key.toLower.toString, true)

  def keyReleased(event: KeyEvent): Unit =
    toggleDigitalInput(sketch.key.toLower.toString, false)

  def mousePressed(event: MouseEvent): Unit = sketch.mouseButton match {
    case LEFT => toggleDigitalInput("MOUSE_LEFT", true)
    case _ => Unit
  }

  def mouseReleased(event: MouseEvent): Unit = sketch.mouseButton match {
    case LEFT => toggleDigitalInput("MOUSE_LEFT", false)
    case _ => Unit
  }

  def mouseWheel(event: MouseEvent): Unit =
    scale += scale * event.getCount * zoomSensitivity

}
