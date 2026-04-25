
requestFileSystem(".") {
  // 1. Read the public README normally
  val readme = access("projects/webapp/docs/README.md").read()

  // 2. Read the classified incident report
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // 3. Build a classified prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a senior application security engineer.
       |
       |Below are two documents about a web application:
       |
       |--- PUBLIC README ---
       |$readme
       |
       |--- CLASSIFIED INCIDENT REPORT ---
       |$incident
       |
       |Based on BOTH documents, produce a thorough, structured set of security
       |recommendations for the webapp. For each recommendation include:
       |  • A short title
       |  • The risk it addresses (referencing specific details from the documents where relevant)
       |  • Concrete remediation steps
       |
       |Format the output as a plain-text document suitable for saving to a file,
       |with clear section headings and numbered recommendations.
       |""".stripMargin
  }

  // 4. Send the classified prompt to the LLM — response stays classified
  val recommendationsClassified: Classified[String] = chat(prompt)

  // 5. Write the classified recommendations to the output file
  val outFile = access("secrets/docs/security-recommendations.txt")
  outFile.writeClassified(recommendationsClassified)

  println("Done! Security recommendations written to secrets/docs/security-recommendations.txt")
}
