import scala.util.{Try, Success, Failure}
import scala.io.Source

// Read the public README
val readmeContent = Try {
    requestFileSystem("projects/webapp/docs/README.md") { file =>
        file.read()
    }
} match {
    case Success(content) => content
    case Failure(e) => s"Error reading README: $e"
}

// Read the classified incident report
val incidentContent = Try {
    requestFileSystem("secrets/docs/incident-report.md") { file =>
        file.read()
    }
} match {
    case Success(content) => content
    case Failure(e) => s"Error reading incident report: $e"
}

println("=== PUBLIC README ===")
println(readmeContent)
println("\n=== CLASSIFIED INCIDENT REPORT ===")
println(incidentContent)