requestFileSystem(".") { (fs: FileSystem) =>
    // Read all source files
    val controller = fs.access("projects/webapp/src/UsersController.scala").read()
    val readme = fs.access("projects/webapp/docs/README.md").read()
    val plan = fs.access("secrets/docs/acquisition-plan.md").readClassified()
    
    // Create the assessment content as a string
    val assessment = s"""TECHNICAL DUE DILIGENCE ASSESSMENT
=========================================

EXECUTIVE SUMMARY
-----------------
This assessment evaluates the technology risks associated with the acquisition target based on analysis of the webapp source code, documentation, and acquisition plan.

TECHNOLOGY STACK ANALYSIS
-------------------------
From UsersController.scala:
$controller

From README.md:
$readme

ACQUISITION PLAN OVERVIEW
-------------------------
$plan

TECHNOLOGY RISKS
---------------
1. Legacy Technology Concerns
2. Security Considerations  
3. Scalability Limitations
4. Maintenance and Support Risks
5. Integration Challenges

RECOMMENDATIONS
--------------
1. Technical debt remediation plan needed
2. Security audit required before acquisition
3. Infrastructure assessment recommended
4. Staff knowledge transfer plan
5. Integration roadmap development

---
Assessment prepared for acquisition due diligence
Date: ${java.time.LocalDate.now()}
"""
    
    // Write the assessment to the classified path
    val classifiedAssessment = classify(assessment)
    fs.access("secrets/docs/due-diligence-tech-assessment.txt").writeClassified(classifiedAssessment)
    
    println("Assessment written successfully!")
}