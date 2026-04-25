
// Let me check the type of requestFileSystem
// Maybe it's defined differently than I thought

// Let's see if we can inspect it
val m = classOf[Interface].getMethod("requestFileSystem", classOf[String], classOf[Function1])
m.toString
