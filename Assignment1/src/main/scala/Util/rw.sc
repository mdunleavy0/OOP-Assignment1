val lsNone: List[Option[Int]] = List(None, None, None)
val lsSome: List[Option[Int]] = List(Some(0), None, None)
val lsMany: List[Option[Int]] = List(Some(0), Some(1), None)

lsNone.flatten
lsSome.flatten
lsMany.flatten
