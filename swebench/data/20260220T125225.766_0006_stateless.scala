requestFileSystem("/tmp") { root =>
  root.children.map(f => f.name).take(5).mkString(",")
}