requestFileSystem(".") {
    val acquisitionPlanFile = access("secrets/docs/acquisition-plan.md")
    val classifiedPlan = acquisitionPlanFile.readClassified()
    
    // Use chat with classified input to analyze the acquisition plan
    val analysisRequest = classifiedPlan.map(plan => s"Analyze this acquisition plan and identify key technical integration points, risks, and dependencies mentioned: $plan")
    val analysis = chat(analysisRequest)
    
    println("=== Analysis of Acquisition Plan ===")
    println(analysis)
}