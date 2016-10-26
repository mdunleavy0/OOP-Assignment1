import processing.core._


/**
  * Created by Michael Dunleavy on 26/10/2016.
  */
class Camera(var sketch: PApplet, var x: Float = 0f, var y: Float = 0f, var scale: Float = 1f) {

  def transform(): Unit = {
    sketch.translate(x, y)
    sketch.scale(scale)
  }
}
