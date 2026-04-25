import library.*

given FileSystem = new FileSystem:
  def access(path: String): FileEntry^{this} = throw new Exception("not implemented")

grep(".", "defer")