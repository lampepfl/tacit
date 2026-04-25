
// Let's try to see all available members of the Interface trait
// Maybe there's something we're missing
classOf[Interface].getMethods.map(_.getName).distinct.mkString(", ")
