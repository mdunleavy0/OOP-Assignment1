val ls = (0 until 10).toList

ls match {
  case _ :+ x => println(x)
  case _ => println("No match")
}
