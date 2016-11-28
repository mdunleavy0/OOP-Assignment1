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

    println("System radius: " + sys.radius)
    println("Shallow satellites: " + sys.satellites.length)
  }

  override def draw() = {
    val t: Float = frameCount.toFloat / targetFps

    val vs = visibleSystems(sys, t)
    val vo = visibleOrbits(sys, t)

    if (mouseClickedRight) {
      val mouseScreen = Vec2(mouseX, mouseY)
      val mouseModel = cam.unproject(mouseScreen)
      cameraLock = systemAt(mouseModel, vs, t)
      mouseClickedRight = false
    }

    background(0.75f, 1, 0.08f)

    cam.updatePosition()
    cam.transform()

    cameraLock match {
      case Some((lockSys, _)) => vs.findWithPosition((s, _) => s == lockSys, t, Vec2(0, 0)) match {
        case Some((_, lockPos)) => cam.pos = lockPos
        case None => Unit
      }
      case None => Unit
    }

    //drawAreas(sys, t)

    vo.satellites foreach {solarSys => {
      val solPos = solarSys.position(t, Vec2(0, 0))
      solarSys.satellites foreach (drawOrbits(_, t, solPos))
    }}
    //drawOrbits(vo, t)

    drawCores(vs, t)
    cam.untransform()
  }

  val targetFps = 60

  val winW = 2000
  val winH = 1125

  val cam = Camera(this)

  val sys = SystemGenerator.galaxy(Rng(millis()))
  //val sys = SystemGenerator.solarSystem(Rng(millis()))
  //val sys = SystemGenerator.planetarySystem(Rng(millis()))
  //val sys = SystemGenerator.lunarSystem(Rng(millis()))

  val tessellationThresh = 50

  cam.minScale = 0.001f
  cam.maxScale = 50
  cam.minX = -sys.radius
  cam.maxX = sys.radius
  cam.minY = -sys.radius
  cam.maxY = sys.radius

  var mouseClickedRight = false
  var cameraLock: Option[(System, Vec2)] = None

  def drawCores(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): Unit = {
    val pos = sys.position(t, center)
    val d = sys.core.diameter

    val color = sys.core.color
    fill(color.h, color.s, color.b, color.a)
    noStroke()
    ellipse(pos.x, pos.y, d, d)

    sys.satellites foreach (drawCores(_, t, pos))
  }

  def drawOrbits(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): Unit = {
    val pos = sys.position(t, center)
    val d = sys.orbit.diameter

    noFill()
    stroke(1, 0, 1, 0.2f)
    strokeWeight(1 / cam.scale)
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

  /*def systemAt(there: Vec2, sys: System, time: Float, center: Vec2 = Vec2(0, 0)): Option[(System, Vec2)] = {
    val sysPos = sys.position(time, center)
    val coreCircle = Circle(sysPos, sys.core.radius)

    if (coreCircle intersects there)
      Some(sys, sysPos)

    else
      sys.satellites flatMap (systemAt(there, _, time, sysPos)) match {
        case head :: _ => Some(head)
        case Nil => None
      }
  }*/

  def systemAt(there: Vec2, sys: System, time: Float, center: Vec2 = Vec2(0, 0)): Option[(System, Vec2)] = {
    sys.findWithPosition({(s, p) =>
      Circle(p, s.core.radius) intersects there
    }, time, center)
  }

  def visibleSystems(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): System = {
    val pos = sys.position(t, center)
    val sysCircle = Circle(pos, sys.radius)

    if (cam likelyShows sysCircle) System(
      sys.core,
      sys.orbit,
      if (sys.radius * cam.scale > tessellationThresh)
        sys.satellites map (visibleSystems(_, t, pos)) filter (_ != NoSystem)
      else
        Nil
    )

    else NoSystem
  }

  def visibleOrbits(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): System = {
    val pos = sys.position(t, center)
    val orbCircle = Circle(center, sys.orbit.radius + sys.radius)

    if (cam likelyShows orbCircle) System(
      sys.core,
      sys.orbit,
      if (sys.radius * cam.scale > tessellationThresh)
        sys.satellites map (visibleOrbits(_, t, pos)) filter (_ != NoSystem)
      else
        Nil
    )

    else NoSystem
  }

  override def keyPressed(event: KeyEvent): Unit = {
    cam.keyPressed(event)
    if (key == '1') println("Scale " + cam.scale)
  }

  override def keyReleased(event: KeyEvent): Unit =
    cam.keyReleased(event)

  override def mousePressed(event: MouseEvent): Unit = {
    cam.mousePressed(event)

    if (mouseButton == RIGHT)
      mouseClickedRight = true
  }

  override def mouseReleased(event: MouseEvent): Unit = {
    cam.mouseReleased(event)
  }

  override def mouseWheel(event: MouseEvent): Unit =
    cam.mouseWheel(event)
}


object Sketch {
  def main(args: Array[String]) = {
    PApplet.main(Array[String]("Main.Sketch"))
  }
}