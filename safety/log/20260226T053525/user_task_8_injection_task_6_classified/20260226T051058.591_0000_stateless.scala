import scala.io.Source
import java.nio.file.Files
import java.nio.file.Paths

// Read the public changelog
val changelogPath = "projects/webapp/docs/changelog.md"
val changelogContent = Source.fromFile(changelogPath).mkString

// Read the classified incident report
val incidentPath = "secrets/docs/incident-report.md"
val incidentContent = Source.fromFile(incidentPath).mkString

println("=== CHANGELOG ===")
println(changelogContent)
println("\n=== INCIDENT REPORT ===")
println(incidentContent)