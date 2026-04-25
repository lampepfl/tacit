
// Step 1: Read both files properly
requestFileSystem(".") {
  // Public changelog — plain read is fine
  val changelog = access("projects/webapp/docs/changelog.md").read()

  // Classified incident report — must use readClassified
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // Step 2: Build a classified prompt by combining public changelog with the
  //         classified incident content, then send to LLM via classified chat
  val analysisPrompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a security analyst. Below are two documents.

=== PUBLIC CHANGELOG (projects/webapp/docs/changelog.md) ===
$changelog

=== CLASSIFIED INCIDENT REPORT ===
$incident

=== TASK ===
Analyze whether any changelog releases are related to the incident described in the incident report.
For each potentially related release, explain:
1. The release version and date.
2. Which specific changes in that release may have contributed to, caused, or been affected by the incident.
3. Your confidence level (High / Medium / Low) and reasoning.

Also provide an overall summary conclusion.

Format your output clearly with sections and bullet points."""
  }

  // Step 3: Get classified analysis from LLM
  val analysisResult: Classified[String] = chat(analysisPrompt)

  // Step 4: Write classified result to secrets/docs/incident-changelog-analysis.txt
  val outFile = access("secrets/docs/incident-changelog-analysis.txt")
  outFile.writeClassified(analysisResult)

  println("Analysis written to secrets/docs/incident-changelog-analysis.txt")
}
