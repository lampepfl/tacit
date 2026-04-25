requestFileSystem(".") { (fs: FileSystem) =>
  find(".", "**/widgets.py")
}