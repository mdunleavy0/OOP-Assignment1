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

    println("System radius: " + masterSys.radius)
    println("Shallow satellites: " + masterSys.satellites.length)
  }

  override def draw() = {
    val t: Float = frameCount.toFloat / targetFps

    //val vs = visibleSystems(masterSys, t)
    //val vo = visibleOrbits(masterSys, t)

    val vs = System(
      masterSys.core,
      masterSys.orbit,
      (parallelSatellites map (visibleSystems(_, t)) filter (_ != NoSystem)).toList
    )

    val vo = System(
      masterSys.core,
      masterSys.orbit,
      (parallelSatellites map (visibleOrbits(_, t)) filter (_ != NoSystem)).toList
    )

    if (mouseClickedLeft) {
      mouseClickedLeft = false
      cameraLock = None
    }

    if (mouseClickedRight) {
      mouseClickedRight = false

      val mouseScreen = Vec2(mouseX, mouseY)
      val mouseModel = cam.unproject(mouseScreen)

      cameraLock = systemAt(mouseModel, vs, t) match {
        case Some(sys) => Some(sys)
        case None => cameraLock
      }
    }

    background(0.75f, 1, 0.08f)

    fill(0, 0, 1)
    noStroke()
    textSize(36)
    text(frameRate.toString, 100, 100)

    cam.updatePosition()
    cam.transform()

    cameraLock match {
      case Some((lockSys)) => vs.findWithPosition((s, _) => s.core == lockSys.core, t, Vec2(0, 0)) match {
        case Some((_, lockPos)) => cam.pos = lockPos
        case None => Unit
      }
      case None => Unit
    }

    //drawAreas(masterSys, t)

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

  val masterSys = SystemGenerator.galaxy(Rng(millis()))
  //val masterSys = SystemGenerator.solarSystem(Rng(millis()))
  //val masterSys = SystemGenerator.planetarySystem(Rng(millis()))
  //val masterSys = SystemGenerator.lunarSystem(Rng(millis()))

  val parallelSatellites = masterSys.satellites.par

  val tessellationThresh = 50
  val asteroidTessellationThresh = 150

  cam.minScale = 0.001f
  cam.maxScale = 50
  cam.minX = -masterSys.radius
  cam.maxX = masterSys.radius
  cam.minY = -masterSys.radius
  cam.maxY = masterSys.radius

  var mouseClickedLeft = false
  var mouseClickedRight = false
  var cameraLock: Option[System] = None

  def drawCores(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): Unit = {
    val pos = sys.position(t, center)
    val d = sys.core.diameter

    val color = sys.core.color
    fill(color.h, color.s, color.b, color.a)
    noStroke()
    ellipse(pos.x, pos.y, d, d)

    sys.core match {
      case planet: Planet => planet.ring match {
        case Some(ring) => {
          val c = ring.color
          val d = 2 * ring.radius

          noFill()
          stroke(c.h, c.s, c.b, c.a)
          strokeWeight(ring.width)

          ellipse(pos.x, pos.y, d, d)
        }
        case _ => Unit
      }
      case _ => Unit
    }

    sys.satellites foreach (drawCores(_, t, pos))
  }

  def drawOrbits(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): Unit = {
    val pos = sys.position(t, center)
    val d = sys.orbit.diameter

    noFill()
    //stroke(0, 0, 1, 0.2f)
    stroke(0, 0, 0.2f)
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

  def systemAt(there: Vec2, sys: System, time: Float, center: Vec2 = Vec2(0, 0)): Option[System] = {
    sys.findWithPosition({(s, p) =>
      Circle(p, s.core.radius) intersects there
    }, time, center) map (_._1)
  }

  def visibleSystems(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): System = {
    val pos = sys.position(t, center)
    val sysCircle = Circle(pos, sys.radius)

    def nonAsteroids(sats: List[System]): List[System] =
      sats filter (!_.core.isInstanceOf[Asteroid])

    if (cam likelyShows sysCircle) System(
      sys.core,
      sys.orbit,
      if (sys.radius * cam.scale > asteroidTessellationThresh)
        sys.satellites map (visibleSystems(_, t, pos)) filter (_ != NoSystem)
      else if (sys.radius * cam.scale > tessellationThresh)
        nonAsteroids(sys.satellites) map (visibleOrbits(_, t, pos)) filter (_ != NoSystem)
      else
        Nil
    )

    else NoSystem
  }

  def visibleOrbits(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): System = sys.core match {
    case _: Asteroid => NoSystem
    case _ => {
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
  }

  override def keyPressed(event: KeyEvent): Unit = {
    cam.keyPressed(event)
    if (key == '1') println("Scale " + cam.scale)
  }

  override def keyReleased(event: KeyEvent): Unit =
    cam.keyReleased(event)

  override def mousePressed(event: MouseEvent): Unit = {
    cam.mousePressed(event)

    mouseButton match {
      case LEFT => mouseClickedLeft = true
      case RIGHT => mouseClickedRight = true
      case _ => Unit
    }
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