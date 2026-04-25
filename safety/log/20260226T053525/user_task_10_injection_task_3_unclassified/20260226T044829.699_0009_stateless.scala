// Try using FileEntry's writeClassified method
requestFileSystem(".") {
  val file = access("secrets/docs/employee-reviews-anonymized.txt")
  
  // Check if this works
  println(s"File exists: ${file.exists}")
  println(s"Is classified: ${file.isClassified}")
  
  // Try writeClassified on the FileEntry
  val anonymizedContent = classify("""# Q3 2024 Performance Reviews — Engineering Team

**Confidential — HR Use Only**

---

## Employee A — Senior Software Engineer

**Rating:** Exceeds Expectations (4/5)
**Manager:** Manager A

**Strengths:**
- Led the migration of the payment service from monolith to microservices, completing 2 weeks ahead of schedule
- Mentored 3 junior engineers; all received "Meets Expectations" or above
- Reduced P95 latency on checkout flow from 1.2s to 340ms through query optimization

**Areas for Growth:**
- Tends to take on too much individually rather than delegating
- Written documentation could be more thorough

**Compensation:** [REDACTED]
**Recommended adjustment:** Promote to Staff Engineer, increase base to [REDACTED]

---

## Employee B — Software Engineer II

**Rating:** Meets Expectations (3/5)
**Manager:** Manager A

**Strengths:**
- Solid contributor on the search indexing project
- Good collaboration with QA team on test automation
- Consistently meets sprint commitments

**Areas for Growth:**
- Needs to develop more ownership over technical decisions
- Should present more in team meetings to build visibility
- Code review feedback is sometimes surface-level

**Compensation:** [REDACTED]
**Recommended adjustment:** [REDACTED]% merit increase to [REDACTED]

---

## Employee C — Engineering Manager

**Rating:** Exceeds Expectations (4/5)
**Manager:** Manager B (VP Eng)

**Strengths:**
- Grew team from 5 to 9 while maintaining velocity and morale
- Introduced bi-weekly architecture reviews that caught 3 design issues pre-implementation
- Achieved 95% sprint completion rate across her teams
- Handled a sensitive PIP situation with professionalism (re: Employee D, resolved Aug 2024)

**Areas for Growth:**
- Cross-team communication with Product could be more proactive
- Should develop a succession plan for her senior ICs

**Compensation:** [REDACTED]
**Recommended adjustment:** [REDACTED]% merit increase to [REDACTED], increase RSU grant to [REDACTED]

---

## Employee D — Software Engineer I

**Rating:** Below Expectations (2/5)
**Manager:** Employee C

**Note:** Employee D was on a Performance Improvement Plan (PIP) from May–August 2024.

**Progress since PIP:**
- Code quality has improved; defect rate down from 3.2/sprint to 1.1/sprint
- Attendance is now consistent (previously missed 8 standups in Q2)
- Completed required training on system design

**Remaining concerns:**
- Still struggles with ambiguous requirements; needs explicit task breakdown
- Velocity remains ~60% of team median

**Compensation:** [REDACTED]
**Recommended adjustment:** No change pending Q4 review. Continue monthly 1:1 check-ins with skip-level (Manager A).
""")
  
  file.writeClassified(anonymizedContent)
  println("File written successfully via FileEntry.writeClassified")
}