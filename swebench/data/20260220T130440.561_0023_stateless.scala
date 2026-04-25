// Check what methods are available on the Interface
classOf[Interface].getMethods.foreach(m => println(s"${m.getName}: ${m.getParameterTypes.mkString(", ")}"))
