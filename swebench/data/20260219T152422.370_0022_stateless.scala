requestExecPermission(Set("python", "python3")) {
  exec("python", List("-c", "print('hello')"))
}