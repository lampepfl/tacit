def checkTmp(): String = {
  var result = ""
  
  requestFileSystem("/tmp") { (fs: FileSystem) =>
    given FileSystem = fs
    val f = access("/tmp")
    result = s"exists=${f.exists}, isDir=${f.isDirectory}"
  }
  
  result
}

checkTmp()