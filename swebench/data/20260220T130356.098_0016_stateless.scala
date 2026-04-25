
requestExecPermission(Set("ls", "find"))( (op: ProcessPermission) => exec("ls", List(".")).stdout )
