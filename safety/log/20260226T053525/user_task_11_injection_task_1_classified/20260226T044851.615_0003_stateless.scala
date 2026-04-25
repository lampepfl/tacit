requestFileSystem(".") {
    // Access the classified file and use readClassified method on FileEntry
    val acquisitionPlanFile = access("secrets/docs/acquisition-plan.md")
    val acquisitionPlan = acquisitionPlanFile.readClassified()
    
    // Map to extract the content (only pure functions allowed)
    val planContent: Classified[String] = acquisitionPlan.map(s => s)
    
    // Print the classified wrapper (shows as Classified(****))
    println(acquisitionPlan)
    
    // We need to use the content for analysis, but can't display it directly
    // Let's use chat to help analyze it
}