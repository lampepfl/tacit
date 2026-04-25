
// The pp parameter should be used implicitly for exec
// Let me adjust the method

def runLs(using pp: ProcessPermission): ProcessResult = {
  exec("ls", List("/"), None, 30000)
}

// Now let's try using this with requestExecPermission
// The issue is that requestExecPermission passes the permission as an implicit

// Let me try a different approach - using the implicit directly
requestExecPermission(Set("ls"))( pp => {
  exec("ls", List("/"), None, 30000)(using pp)
} )
