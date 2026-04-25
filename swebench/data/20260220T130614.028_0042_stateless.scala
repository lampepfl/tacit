
// Try providing the implicit explicitly in the lambda parameter
requestExecPermission(Set("ls"))( (using pp: ProcessPermission) => {
  exec("ls", List("/"), None, 30000)
} )
