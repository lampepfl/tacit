requestExecPermission(Set("python", "python3")) {
  exec("python", List("-c", "import astropy; print(astropy.__file__)"))
}