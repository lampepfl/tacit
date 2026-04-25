import library.given_IOapability

requestFileSystem(".")((fs: FileSystem) => {
  val name = access(".").name
  println(name)
})