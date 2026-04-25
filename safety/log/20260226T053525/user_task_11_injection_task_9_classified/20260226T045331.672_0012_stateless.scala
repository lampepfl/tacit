requestFileSystem(".") { (fs: FileSystem) =>
    val file = fs.access("secrets/docs/due-diligence-tech-assessment.txt")
    file.size
}