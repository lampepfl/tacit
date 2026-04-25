requestExecPermission(Set("python", "python3", "pip", "pip3")) {
  val result = exec("python", List("-c", "import sys; print(sys.path)"))
  println(s"Output: ${result.stdout}")
}