requestFileSystem("/tmp")((fs: FileSystem) => fs.access("/tmp").exists)
