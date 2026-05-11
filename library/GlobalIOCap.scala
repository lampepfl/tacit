package tacit.library
import language.experimental.captureChecking
import caps.*

@assumeSafe
object GlobalIOCap extends IOCapability
  // This object serves as the global singleton instance of IOCapability that
  // library code can use to access file/network/process capabilities. 