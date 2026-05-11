// val scala3Version = {
//   val fallback = "3.8.3-RC1"
//   try {
//     val url = "https://repo.scala-lang.org/artifactory/api/storage/local-maven-nightlies/org/scala-lang/scala3-compiler_3/"
//     val content = scala.io.Source.fromURL(url, "UTF-8").mkString
//     val pattern = """"uri"\s*:\s*"/(3\.[^"]*NIGHTLY)"""".r
//     val versions = pattern.findAllMatchIn(content).map(_.group(1)).toList.sorted
//     val latest = versions.last
//     if (latest != fallback) println(s"[info] Use Scala 3 nightly: $latest")
//     latest
//   } catch { case _: Exception =>
//     println(s"[warn] Failed to fetch latest nightly, using fallback: $fallback")
//     fallback
//   }
// }
// Need the specific version with dynamic eval published locally 
val scala3Version = "3.9.0-RC1-bin-SNAPSHOT"
ThisBuild / resolvers += Resolver.scalaNightlyRepository

val tacitVersion = "0.1.4-SNAPSHOT"
val tacitLibVersion = tacitVersion

val circeVersion = "0.14.15"

lazy val lib = project
  .in(file("library"))
  .settings(
    organization := "lampepfl",
    name := "TACIT-library",
    version := tacitLibVersion,
    scalaVersion := scala3Version,
    Compile / unmanagedSourceDirectories := Seq(
      baseDirectory.value,
      baseDirectory.value / "impl"
    ),
    Compile / unmanagedSources / excludeFilter :=
      "*.test.scala" || "project.scala" || "README.md",
    libraryDependencies ++= Seq(
      "com.openai" % "openai-java" % "4.35.0",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.scala-lang" %% "scala3-compiler" % scala3Version,
      "org.scala-lang" %% "scala3-repl" % scala3Version,
    ),
    // Bundle Interface.scala source as a classpath resource so the agent
    // prompt can include the full capability API surface. We place it under
    // `tacit/` and use a non-`.scala` extension so the incremental compiler
    // doesn't re-discover it as a duplicate source on the next build.
    Compile / resourceGenerators += Def.task {
      val src = baseDirectory.value / "Interface.scala"
      val dst = (Compile / resourceManaged).value / "tacit" / "Interface.scala.txt"
      IO.copyFile(src, dst)
      Seq(dst)
    }.taskValue,
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
    },
    // Publish the assembled fat jar for the library
    Compile / packageBin := (Compile / assembly).value
  )

lazy val root = project
  .in(file("."))
  .aggregate(lib)
  .settings(
    name := "TACIT",
    organization := "lampepfl",
    version := tacitVersion,

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
      "org.scalameta" %% "munit" % "1.3.0" % Test,
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
    javaOptions += {
      val jarPath = (lib / assembly / assemblyOutputPath).value.getAbsolutePath
      s"-Dtacit.library.jar=$jarPath"
    },

    // Enable forking for the REPL execution
    fork := true,
    // Connect stdin to the forked process.
    connectInput := true,
    
    // Quick launcher for the developer REPL (tacit.StartDevRepl).
    // Usage: `sbt devRepl` or `sbt "devRepl --strict --config path.json"`.
    commands += Command.args("devRepl", "<args>") { (state, args) =>
      val cmd = ("runMain" :: "tacit.StartDevRepl" :: args.toList).mkString(" ")
      "lib/assembly" :: cmd :: state
    },

    // Default `sbt run` to the MCP server
    Compile / run / mainClass := Some("tacit.StartMCP"),

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
