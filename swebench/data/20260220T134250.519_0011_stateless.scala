object Runner {
  def run()(using io: IOCapability): List[String] = {
    requestFileSystem(".")(fs => {
      val root = access(".")(using fs)
      root.children.map(_.name).take(20)
    })
  }
}
Runner.run()