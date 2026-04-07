val scala3Version = {
  val fallback = "3.8.3-RC1"
  try {
    val url = "https://repo.scala-lang.org/artifactory/api/storage/local-maven-nightlies/org/scala-lang/scala3-compiler_3/"
    val content = scala.io.Source.fromURL(url, "UTF-8").mkString
    val pattern = """"uri"\s*:\s*"/(3\.[^"]*NIGHTLY)"""".r
    val versions = pattern.findAllMatchIn(content).map(_.group(1)).toList.sorted
    val latest = versions.last
    if (latest != fallback) println(s"[info] Use Scala 3 nightly: $latest")
    latest
  } catch { case _: Exception =>
    println(s"[warn] Failed to fetch latest nightly, using fallback: $fallback")
    fallback
  }
}
// val scala3Version = "3.8.4-RC1-bin-SNAPSHOT"
ThisBuild / resolvers += Resolver.scalaNightlyRepository

val circeVersion = "0.14.15"

addCommandAlias("claw", "capybaraclaw/run")
addCommandAlias("slackbot", "capybaraclaw/runMain capybaraclaw.slackTestMain")

val stableScala3Version = "3.8.2"

val MUnitFramework = new TestFramework("munit.Framework")
val TestFull = config("testFull").extend(Test)

lazy val agents = project
  .in(file("agents"))
  .configs(TestFull)
  .settings(
    name := "tacit-agents",
    organization := "lampepfl",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := stableScala3Version,
    scalacOptions ++= Seq(
      "-deprecation", "-feature", "-unchecked",
      "-Yexplicit-nulls", "-Wsafe-init",
      "-language:experimental.modularity",
    ),
    libraryDependencies ++= Seq(
      "com.openai" % "openai-java" % "4.29.1",
      "com.anthropic" % "anthropic-java" % "2.18.0",
      "ch.epfl.lamp" %% "gears" % "0.2.0",
      "com.lihaoyi" %% "ujson" % "4.1.0",
      "org.scalameta" %% "munit" % "1.2.2" % Test,
    ),
    testFrameworks += MUnitFramework,
    Test / testOptions += Tests.Argument(MUnitFramework, "--exclude-tags=Network"),
    inConfig(TestFull)(Defaults.testTasks),
    TestFull / testOptions := Seq.empty,
  )

lazy val lib = project
  .in(file("library"))
  .settings(
    name := "TACIT-library",
    scalaVersion := scala3Version,
    Compile / unmanagedSourceDirectories := Seq(
      baseDirectory.value,
      baseDirectory.value / "impl"
    ),
    Compile / unmanagedSources / excludeFilter :=
      "*.test.scala" || "project.scala" || "README.md",
    libraryDependencies ++= Seq(
      "com.openai" % "openai-java" % "4.30.0",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
    ),
    scalacOptions ++= Seq(
      "-language:experimental.captureChecking",
      "-language:experimental.modularity",
      "-deprecation", "-feature", "-unchecked",
      "-Yexplicit-nulls", "-Wsafe-init",
      "-release:17",
    ),
    // Assembly settings for creating a standalone library JAR
    assembly / assemblyJarName := "TACIT-library.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "services", _*) => MergeStrategy.concat
      case PathList("META-INF", "MANIFEST.MF")  => MergeStrategy.discard
      case PathList("META-INF", x) if x.endsWith(".SF")
        || x.endsWith(".DSA") || x.endsWith(".RSA") => MergeStrategy.discard
      case PathList("META-INF", _*)              => MergeStrategy.first
      case "module-info.class"                   => MergeStrategy.discard
      case x if x.endsWith(".tasty") => MergeStrategy.first
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )

lazy val capybaraclaw = project
  .in(file("capybaraclaw"))
  .dependsOn(agents, root)
  .configs(TestFull)
  .settings(
    name := "capybaraclaw",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq(
      "-deprecation", "-feature", "-unchecked",
      "-Yexplicit-nulls", "-Wsafe-init",
      "-language:experimental.modularity",
    ),
    libraryDependencies ++= Seq(
      "com.slack.api" % "bolt" % "1.48.0",
      "com.slack.api" % "bolt-socket-mode" % "1.48.0",
      "org.glassfish.tyrus.bundles" % "tyrus-standalone-client" % "1.21",
      "org.scalameta" %% "munit" % "1.2.2" % Test,
    ),
    testFrameworks += MUnitFramework,
    Test / testOptions += Tests.Argument(MUnitFramework, "--exclude-tags=Network"),
    inConfig(TestFull)(Defaults.testTasks),
    TestFull / testOptions := Seq.empty,
    fork := true,
    run / connectInput := true,
    Compile / run := (Compile / run dependsOn (lib / assembly)).evaluated,
    javaOptions += {
      val jarPath = (lib / assembly / assemblyOutputPath).value.getAbsolutePath
      s"-Dtacit.library.jar=$jarPath"
    },
  )

lazy val root = project
  .in(file("."))
  .aggregate(lib)
  .dependsOn(agents)
  .settings(
    name := "TACIT",
    version := "0.1.4-SNAPSHOT",

    scalaVersion := scala3Version,

    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      // "-source:future",
      "-Yexplicit-nulls",
      "-Wunused:all",
      "-Wsafe-init",
      "-language:experimental.modularity",
      // "-Wall",
      "-release:17",
    ),

    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "com.github.scopt" %% "scopt" % "4.1.1-M3",
      "org.scala-lang" %% "scala3-compiler" % scala3Version,
      "org.scala-lang" %% "scala3-repl" % scala3Version,
      "org.scalameta" %% "munit" % "1.2.2" % Test,
    ),

    // Bundle Interface.scala source as a classpath resource so show_interface can serve it
    Compile / resourceGenerators += Def.task {
      val src = (lib / baseDirectory).value / "Interface.scala"
      val dst = (Compile / resourceManaged).value / "Interface.scala"
      IO.copyFile(src, dst)
      Seq(dst)
    }.taskValue,

    // Generate version.properties so the server can read its own version at runtime
    Compile / resourceGenerators += Def.task {
      val dst = (Compile / resourceManaged).value / "version.properties"
      IO.write(dst, s"version=${version.value}\nname=${name.value}\n")
      Seq(dst)
    }.taskValue,

    // Build library fat JAR before tests/run/assembly and pass its path
    Test / test := ((Test / test) dependsOn (lib / assembly)).value,
    Test / testOnly := ((Test / testOnly) dependsOn (lib / assembly)).evaluated,
    Compile / run := (Compile / run dependsOn (lib / assembly)).evaluated,
    assembly := (assembly dependsOn (lib / assembly)).value,
    javaOptions += {
      val jarPath = (lib / assembly / assemblyOutputPath).value.getAbsolutePath
      s"-Dtacit.library.jar=$jarPath"
    },

    // Enable forking for the REPL execution
    fork := true,
    // Connect stdin to the forked process (needed for MCP stdio communication)
    run / connectInput := true,
    
    // Assembly settings for creating a fat JAR
    assembly / mainClass := Some("tacit.StartMCP"),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "services", _*) => MergeStrategy.concat
      case PathList("META-INF", "MANIFEST.MF")  => MergeStrategy.discard
      case PathList("META-INF", x) if x.endsWith(".SF")
        || x.endsWith(".DSA") || x.endsWith(".RSA") => MergeStrategy.discard
      case PathList("META-INF", _*)              => MergeStrategy.first
      case "module-info.class"                   => MergeStrategy.discard
      case x if x.endsWith(".tasty") => MergeStrategy.first
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )
