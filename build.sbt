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
val scala3Versoin = "3.9.0-RC1-bin-20260424-4a012b1-NIGHTLY"
ThisBuild / resolvers += Resolver.scalaNightlyRepository

val circeVersion = "0.14.15"

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

lazy val root = project
  .in(file("."))
  .aggregate(lib)
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
