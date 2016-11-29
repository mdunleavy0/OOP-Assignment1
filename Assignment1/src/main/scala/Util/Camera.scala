package Util

import processing.core._
import processing.core.PApplet._
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

  var panTime = 0.75f
  var zoomSensitivity = 0.5f

  var (minScale, maxScale): (Float, Float) = (Float.NegativeInfinity, Float.PositiveInfinity)
  var (minX, maxX): (Float, Float) = (Float.NegativeInfinity, Float.PositiveInfinity)
  var (minY, maxY): (Float, Float) = (Float.NegativeInfinity, Float.PositiveInfinity)

  private def circumcircleRadius: Float = pos dist unproject(Vec2(0f, 0f))
  //private def circumcircleRadius: Float = (pos dist unproject(Vec2(0f, 0f))) * 0.5f
  private def circumcircle: Circle = Circle(pos, circumcircleRadius)

  private def halfSketchWidth: Float = sketch.width / 2
  private def halfSketchHeight: Float = sketch.height / 2

  private def panLength: Float =
    (sketch.width / (panTime * targetFps)) / scale

  private val digitalInputs: mutable.Map[String, Boolean] = mutable.Map(
    "MOUSE_LEFT" -> false,
    "w" -> false,
    "a" -> false,
    "s" -> false,
    "d" -> false
  )

  def transform(): Unit = {
    sketch.pushMatrix()
    sketch.translate(halfSketchWidth, halfSketchHeight)
    sketch.scale(scale)
    sketch.translate(-pos.x, -pos.y)

    /*sketch.noFill()
    sketch.strokeWeight(10 / scale)
    sketch.stroke(1, 1, 1)
    sketch.ellipse(pos.x, pos.y, 2 * circumcircleRadius, 2 * circumcircleRadius)*/
  }

  def untransform(): Unit = sketch.popMatrix()

  def project(model: Vec2): Vec2 = Vec2(
    (model.x - pos.x) * scale + halfSketchWidth,
    (model.y - pos.y) * scale + halfSketchHeight
  )

  def unproject(screen: Vec2): Vec2 = Vec2(
    (screen.x - halfSketchWidth) / scale + pos.x,
    (screen.y - halfSketchHeight) / scale + pos.y
  )

  def likelyShows(area: Circle): Boolean = circumcircle intersects area

  def gravitateTo(target: Vec2): Unit = {
    val d = pos dist target

    if (d != 0) {
      val speed = min(panLength, d)
      val normSpd = speed / d

      pos = Vec2(
        lerp(pos.x, target.x, normSpd),
        lerp(pos.y, target.y, normSpd)
      )
    }
  }

  def updatePosition(): Unit = {
    var x = pos.x
    var y = pos.y

    if (digitalInputs("w")) y -= panLength
    if (digitalInputs("a")) x -= panLength
    if (digitalInputs("s")) y += panLength
    if (digitalInputs("d")) x += panLength

    if (digitalInputs("MOUSE_LEFT")) {
      x -= (sketch.mouseX - sketch.pmouseX) / scale
      y -= (sketch.mouseY - sketch.pmouseY) / scale
    }

    x = min(max(x, minX), maxX)
    y = min(max(y, minY), maxY)

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

  def mouseWheel(event: MouseEvent): Unit = {
    scale += scale * -event.getCount * zoomSensitivity
    scale = min(max(scale, minScale), maxScale)
  }
}

object Camera {
  def apply(sketch: PApplet): Camera = new Camera(sketch)
}
