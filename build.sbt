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
ThisBuild / resolvers += Resolver.scalaNightlyRepository

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
    libraryDependencies += "com.openai" % "openai-java" % "4.29.1",
    scalacOptions ++= Seq(
      "-language:experimental.captureChecking",
      // "-language:experimental.separationChecking",
      "-language:experimental.modularity",
      "-deprecation", "-feature", "-unchecked",
      "-Yexplicit-nulls", "-Wsafe-init"
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

lazy val root = project
  .in(file("."))
  .settings(
    name := "TACIT",
    version := "0.1.2-SNAPSHOT",

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
    ),

    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.15",
      "io.circe" %% "circe-generic" % "0.14.15",
      "io.circe" %% "circe-parser" % "0.14.15",
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
