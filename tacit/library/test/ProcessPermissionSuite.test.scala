package tacit.library

class ProcessPermissionSuite extends munit.FunSuite:

  private def validate(
    command: String,
    permission: ProcessPermissionImpl,
    args: List[String] = List.empty
  ): Unit =
    permission.validateCommand(command, args)

  test("validate allows command in allowed set"):
    validate("echo", ProcessPermissionImpl(Set("echo")))

  test("validate rejects command not in allowed set"):
    val ex = intercept[SecurityException]:
      validate("rm", ProcessPermissionImpl(Set("echo")))
    assert(ex.getMessage.nn.contains("Access denied"))
    assert(ex.getMessage.nn.contains("rm"))

  test("validate blocks cat in strict mode"):
    val ex = intercept[SecurityException]:
      validate("cat", ProcessPermissionImpl(Set("cat"), strictMode = true))
    assert(ex.getMessage.nn.contains("Strict mode"))

  test("validate blocks ls in strict mode"):
    val ex = intercept[SecurityException]:
      validate("ls", ProcessPermissionImpl(Set("ls"), strictMode = true))
    assert(ex.getMessage.nn.contains("Strict mode"))

  test("validate blocks rm in strict mode"):
    val ex = intercept[SecurityException]:
      validate("rm", ProcessPermissionImpl(Set("rm"), strictMode = true))
    assert(ex.getMessage.nn.contains("Strict mode"))

  test("validate blocks cp in strict mode"):
    val ex = intercept[SecurityException]:
      validate("cp", ProcessPermissionImpl(Set("cp"), strictMode = true))
    assert(ex.getMessage.nn.contains("Strict mode"))

  test("validate allows cat in non-strict mode"):
    validate("cat", ProcessPermissionImpl(Set("cat"), strictMode = false))

  test("validate allows non-file command in strict mode"):
    validate("echo", ProcessPermissionImpl(Set("echo"), strictMode = true))

  test("validate blocks tar in strict mode"):
    val ex = intercept[SecurityException]:
      validate("tar", ProcessPermissionImpl(Set("tar"), strictMode = true))
    assert(ex.getMessage.nn.contains("Strict mode"))

  test("validate blocks chmod in strict mode"):
    val ex = intercept[SecurityException]:
      validate("chmod", ProcessPermissionImpl(Set("chmod"), strictMode = true))
    assert(ex.getMessage.nn.contains("Strict mode"))

  test("validate rejects empty command not in allowed set"):
    val ex = intercept[SecurityException]:
      validate("", ProcessPermissionImpl(Set("echo")))
    assert(ex.getMessage.nn.contains("Access denied"))

  // ── commandPermissions (glob allowlist) ──────────────────────

  test("validate allows command matching a pattern"):
    validate(
      "echo",
      ProcessPermissionImpl(Set("echo"), commandPermissions = Some(Set("echo", "ls")))
    )

  test("validate allows command matching a glob pattern"):
    validate(
      "python3",
      ProcessPermissionImpl(Set("python3"), commandPermissions = Some(Set("py*")))
    )

  test("validate rejects command not matching any pattern"):
    val ex = intercept[SecurityException]:
      validate(
        "rm",
        ProcessPermissionImpl(Set("rm"), commandPermissions = Some(Set("echo", "ls")))
      )
    assert(ex.getMessage.nn.contains("does not match any permitted pattern"))

  test("validate with commandPermissions ignores strictMode"):
    // cat is a file-op command; strict mode would normally block it, but the
    // explicit allowlist overrides.
    validate(
      "cat",
      ProcessPermissionImpl(
        Set("cat"),
        strictMode = true,
        commandPermissions = Some(Set("cat"))
      )
    )

  test("validate with empty commandPermissions blocks everything"):
    val ex = intercept[SecurityException]:
      validate(
        "echo",
        ProcessPermissionImpl(Set("echo"), commandPermissions = Some(Set.empty))
      )
    assert(ex.getMessage.nn.contains("does not match any permitted pattern"))

  test("validate still checks allowedCommands before commandPermissions"):
    // Command is globally permitted but not in per-scope allowedCommands.
    val ex = intercept[SecurityException]:
      validate(
        "ls",
        ProcessPermissionImpl(Set("echo"), commandPermissions = Some(Set("ls", "echo")))
      )
    assert(ex.getMessage.nn.contains("Access denied"))
    assert(ex.getMessage.nn.contains("not in allowed commands"))

  // ── arg-aware commandPermissions matching ───────────────────

  test("validate matches patterns against full invocation with args"):
    validate(
      "sbt",
      ProcessPermissionImpl(Set("sbt"), commandPermissions = Some(Set("sbt run *"))),
      args = List("run", "main")
    )

  test("validate rejects args that don't match an arg-aware pattern"):
    val ex = intercept[SecurityException]:
      validate(
        "sbt",
        ProcessPermissionImpl(Set("sbt"), commandPermissions = Some(Set("sbt run *"))),
        args = List("clean")
      )
    assert(ex.getMessage.nn.contains("does not match any permitted pattern"))
    assert(ex.getMessage.nn.contains("sbt clean"))

  test("validate rejects bare command when pattern requires args"):
    // "sbt run *" requires at least one arg after "run".
    val ex = intercept[SecurityException]:
      validate(
        "sbt",
        ProcessPermissionImpl(Set("sbt"), commandPermissions = Some(Set("sbt run *")))
      )
    assert(ex.getMessage.nn.contains("does not match any permitted pattern"))

  test("validate allows bare command when pattern is command-only"):
    validate(
      "sbt",
      ProcessPermissionImpl(Set("sbt"), commandPermissions = Some(Set("sbt")))
    )

  test("validate rejects command with args when pattern is command-only"):
    val ex = intercept[SecurityException]:
      validate(
        "sbt",
        ProcessPermissionImpl(Set("sbt"), commandPermissions = Some(Set("sbt"))),
        args = List("clean")
      )
    assert(ex.getMessage.nn.contains("does not match any permitted pattern"))

  test("validate matches wildcard pattern against any args"):
    validate(
      "sbt",
      ProcessPermissionImpl(Set("sbt"), commandPermissions = Some(Set("sbt *"))),
      args = List("clean", "compile")
    )

  // ── GlobMatcher ─────────────────────────────────────────────

  test("GlobMatcher * matches anything"):
    assert(GlobMatcher.matches("anything", "*"))
    assert(GlobMatcher.matches("", "*"))

  test("GlobMatcher literal match"):
    assert(GlobMatcher.matches("echo", "echo"))
    assert(!GlobMatcher.matches("echoes", "echo"))

  test("GlobMatcher prefix and suffix globs"):
    assert(GlobMatcher.matches("python3", "py*"))
    assert(GlobMatcher.matches("mypython", "*python"))
    assert(GlobMatcher.matches("python3.11", "py*3.11"))
    assert(!GlobMatcher.matches("ruby", "py*"))

  test("GlobMatcher does not treat regex metacharacters specially"):
    // `.` in the pattern should match only a literal dot
    assert(GlobMatcher.matches("a.b", "a.b"))
    assert(!GlobMatcher.matches("axb", "a.b"))
