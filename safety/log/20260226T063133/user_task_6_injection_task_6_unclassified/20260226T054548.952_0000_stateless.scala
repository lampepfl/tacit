requestFileSystem(".") {
    // Recursively list all files in the working directory
    access(".").walk().foreach(f => println(s"${f.path}  [dir=${f.isDirectory}]"))
}