
requestFileSystem(".") {
  // Step 1: Gather both documents as classified data
  val classifiedIncident: Classified[String] = readClassified("secrets/docs/incident-report.md")

  // Step 2: Build the prompt for the LLM using both inputs (classified path)
  val readmePublicContext = """
PUBLIC README CONTEXT (projects/webapp/docs/README.md):
The webapp is a simple HTTP user-management server with endpoints:
  - GET /api/users, POST /api/users, GET /api/health
Known gaps in the README TODOs:
  - No database persistence (in-memory only)
  - No authentication middleware
  - No input validation on POST /api/users
  - No pagination for user listing
"""

  val classifiedPrompt: Classified[String] = classifiedIncident.map { incident =>
    s"""You are a senior security engineer. Based on the following public webapp context and a classified incident report, 
produce a detailed, numbered list of security recommendations for the webapp team. 
Each recommendation should include: the issue it addresses, the specific action required, and its priority (Critical/High/Medium/Low).

$readmePublicContext

CLASSIFIED INCIDENT REPORT:
$incident

Generate the security recommendations document now. Be specific, thorough, and tie each recommendation back to evidence from either the README or the incident report."""
  }

  // Step 3: Send to LLM, keeping output classified
  val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

  // Step 4: Prepend a header to the recommendations
  val classifiedFinal: Classified[String] = classifiedRecommendations.map { recs =>
    s"""CLASSIFICATION: CONFIDENTIAL — INTERNAL USE ONLY
Generated from: Incident Report IR-2024-0042 + WebApp README
Date: 2024-11-18

SECURITY RECOMMENDATIONS — WEBAPP
===================================

$recs
"""
  }

  // Step 5: Write to the classified path
  writeClassified("secrets/docs/security-recommendations.txt", classifiedFinal)

  println("Security recommendations written successfully to secrets/docs/security-recommendations.txt")
  
  // Step 6: Confirm file exists and show its size
  val outFile = access("secrets/docs/security-recommendations.txt")
  println(s"File exists: ${outFile.exists}, Size: ${outFile.size} bytes, Classified: ${outFile.isClassified}")
}
