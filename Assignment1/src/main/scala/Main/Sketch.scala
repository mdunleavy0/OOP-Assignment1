package Main

// 1st party
import Util.Camera
import Util.Circle
import Util.Rng
import Util.Vec2

// 3rd party
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
    // time in secs
    val t: Float = frameCount.toFloat / targetFps


    // all systems with visible cores
    val vs = System(
      masterSys.core,
      masterSys.orbit,
      (parallelSatellites map (visibleSystems(_, t)) filter (_ != NoSystem)).toList
    )
    //val vs = visibleSystems(masterSys, t)

    // all systems with visible orbits
    val vo = System(
      masterSys.core,
      masterSys.orbit,
      (parallelSatellites map (visibleOrbits(_, t)) filter (_ != NoSystem)).toList
    )
    //val vo = visibleOrbits(masterSys, t)

    // release camera lock on left click
    if (mouseClickedLeft) {
      mouseClickedLeft = false
      cameraLock = None
    }

    // lock camera on right click
    if (mouseClickedRight) {
      mouseClickedRight = false

      // mouse positions
      val mouseScreen = Vec2(mouseX, mouseY)
      val mouseModel = cam.unproject(mouseScreen)

      // if there's a system at mouse position
      cameraLock = systemAt(mouseModel, vs, t) match {
        case Some(sys) => Some(sys)
        case None => cameraLock
      }
    }

    // very very very dark purple
    background(0.75f, 1, 0.08f)

    // framerate counter
    // uncomment at risk of disappointment
    /*fill(0, 0, 1)
    noStroke()
    textSize(36)
    text(frameRate.toString, 100, 100)*/

    // camera magic
    cam.updatePosition()
    cam.transform()

    // if camera locked: gravitate to lock
    cameraLock match {
      case Some((lockSys)) => vs.findWithPosition((s, _) => s.core == lockSys.core, t, Vec2(0, 0)) match {
        case Some((_, lockPos)) => cam.gravitateTo(lockPos)
        case None => Unit
      }
      case None => Unit
    }

    //drawAreas(masterSys, t)

    // draw orbits
    vo.satellites foreach {solarSys => {
      val solPos = solarSys.position(t, Vec2(0, 0))
      solarSys.satellites foreach (drawOrbits(_, t, solPos))
    }}
    //drawOrbits(vo, t)

    drawCores(vs, t)

    cam.untransform()
  }

  // a man can dream
  val targetFps = 60

  val winW = 2000
  val winH = 1125

  val cam = Camera(this)

  // generate a random astronomical model
  val masterSys = SystemGenerator.galaxy(Rng(millis()))
  //val masterSys = SystemGenerator.solarSystem(Rng(millis()))
  //val masterSys = SystemGenerator.planetarySystem(Rng(millis()))
  //val masterSys = SystemGenerator.lunarSystem(Rng(millis()))

  // parallel collection of all of the master system's shallow satellites
  val parallelSatellites = masterSys.satellites.par

  // threshold width for tessellation, in pixels
  val tessellationThresh = winW / 40
  val asteroidTessellationThresh = 3 * tessellationThresh

  // camera bounding values
  cam.minScale = 0.001f
  cam.maxScale = 50
  cam.minX = -masterSys.radius
  cam.maxX = masterSys.radius
  cam.minY = -masterSys.radius
  cam.maxY = masterSys.radius

  // camera locking variables
  var mouseClickedLeft = false
  var mouseClickedRight = false
  var cameraLock: Option[System] = None

  // recursively draw the core of a system and all its satellites
  def drawCores(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): Unit = {
    val pos = sys.position(t, center)
    val d = sys.core.diameter

    // draw system core
    val color = sys.core.color
    fill(color.h, color.s, color.b, color.a)
    noStroke()
    ellipse(pos.x, pos.y, d, d)

    // draw planet ring where applicable
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

    // recursion for all satellites
    sys.satellites foreach (drawCores(_, t, pos))
  }

  // recursively draw the orbit paths of a system and all its satellites
  def drawOrbits(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): Unit = {
    val pos = sys.position(t, center)
    val d = sys.orbit.diameter

    // draw core's orbit path
    noFill()
    stroke(0, 0, 1, 0.2f)
    strokeWeight(1 / cam.scale)
    ellipse(center.x, center.y, d, d)

    sys.satellites foreach (drawOrbits(_, t, pos))
  }

  // recursion for all satellites
  def drawAreas(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): Unit = {
    val pos = sys.position(t, center)
    val d = sys.diameter

    fill(0, 1, 1, 0.1f)
    noStroke()
    ellipse(pos.x, pos.y, d, d)

    sys.satellites foreach (drawAreas(_, t, pos))
  }

  // search for the system whose core is at a given co-ordinate
  def systemAt(there: Vec2, sys: System, time: Float, center: Vec2 = Vec2(0, 0)): Option[System] = {
    sys.findWithPosition({(s, p) =>
      Circle(p, s.core.radius) intersects there
    }, time, center) map (_._1)
  }

  // determine which systems are visible
  def visibleSystems(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): System = {
    // system position
    val pos = sys.position(t, center)

    // system circumcircle
    val sysCircle = Circle(pos, sys.radius)

    // filter for non-asteroids
    def nonAsteroids(sats: List[System]): List[System] =
      sats filter (!_.core.isInstanceOf[Asteroid])

    // visibility culling
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

  // determine which systems' orbit paths are visible
  def visibleOrbits(sys: System, t: Float, center: Vec2 = Vec2(0, 0)): System = sys.core match {
    case _: Asteroid => NoSystem
    case _ => {
      // system position
      val pos = sys.position(t, center)

      // orbit circumcircle
      val orbCircle = Circle(center, sys.orbit.radius + sys.radius)

      // visibility culling
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

  // input event handlers
  // most work is passed to camera

  override def keyPressed(event: KeyEvent): Unit =
    cam.keyPressed(event)

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

// entry point
object Sketch {
  def main(args: Array[String]) = {
    PApplet.main(Array[String]("Main.Sketch"))
  }
}