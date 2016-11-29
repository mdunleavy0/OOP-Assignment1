val ls = List(None, Some(1), Some(2))
ls collect {case Some(i) => i}