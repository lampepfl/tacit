requestExecPermission(Set("python", "python3")) {
  val result = exec("python", List("-c", "print('hello')"))
  println(s"Exit code: ${result.exitCode}")
  println(s"Output: ${result.stdout}")
}