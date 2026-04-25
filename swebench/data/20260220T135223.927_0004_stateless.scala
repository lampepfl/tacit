import library.*
import language.experimental.captureChecking

given fs: FileSystem = null.asInstanceOf[FileSystem]
find(".", "**/widgets.py")