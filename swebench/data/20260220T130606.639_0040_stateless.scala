
// Try using a method reference
// Maybe this will work better than a lambda

// First, let's define a method
def runLs(pp: ProcessPermission): ProcessResult = {
  exec("ls", List("/"), None, 30000)
}

// Now let's try using the method reference
requestExecPermission(Set("ls"))(runLs)
