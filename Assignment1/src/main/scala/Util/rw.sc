import Util._

val o1 = Circle(0, 0, 10)
val o2 = Circle(0, 0, 5)
val o3 = Circle(15, 0, 7.5f)
val o4 = Circle(15, 0, 5)
val wayOff = Circle(1000, 1000, 10)

o1 intersects o2
o1 intersects o3
o2 intersects o3
o1 intersects o4
o1 intersects wayOff