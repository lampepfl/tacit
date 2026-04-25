requestFileSystem(".") {
    val assessment = access("secrets/docs/due-diligence-tech-assessment.txt")
    
    assessment.write("""TECHNICAL DUE DILIGENCE ASSESSMENT
==================================
Target: DataFlow Systems, Inc.
Prepared: 2024-12-01
Classification: Board Confidential

EXECUTIVE SUMMARY
-----------------
DataFlow Systems has a mature real-time data pipeline platform with significant
intellectual property. However, technical debt in the core engine and key-person
risks present material integration challenges.

TECHNOLOGY STACK ANALYSIS
-------------------------
Current State:
- Core engine: Legacy Java codebase (production)
- Modernization: Rust rewrite in progress (60% complete)
- Platform: Real-time streaming engine with exactly-once delivery
- Certifications: SOC 2 Type II, HIPAA-compliant option
- Customer base: 340+ enterprises, strong financial services vertical

TECHNOLOGY RISKS
----------------
1. CORE ENGINE TECHNICAL DEBT (HIGH RISK)
   - Legacy Java codebase requires ongoing maintenance
   - Rust rewrite is only 60% complete — significant work remaining
   - Risk of dual-maintenance burden during transition period
   - Estimated 18+ months to complete migration

2. KEY-PERSON DEPENDENCY (CRITICAL)
   - CTO L. Zhang holds 3 of 14 core patents
   - Single point of failure for critical IP and technical knowledge
   - Retention critical to deal value realization

3. ARCHITECTURE CONCERNS
   - No mention of modern cloud-native architecture (Kubernetes, etc.)
   - On-premises deployment may limit scalability
   - Unknown technical debt in operational tooling

4. SECURITY & COMPLIANCE
   - SOC 2 Type II and HIPAA are positives
   - Need deeper review of security practices during DD

OPPORTUNITIES
-------------
- 14 patents in stream processing provide competitive moat
- Engineering team includes 4 former Google Cloud Dataflow engineers
- Customer base (340+ enterprises, 38% top-5 concentration) indicates product-market fit
- Technology aligns with "Live Insights" roadmap — 18-month acceleration potential

RECOMMENDATIONS
---------------
1. Prioritize CTO retention package with extended vesting
2. Conduct detailed code review of Java core engine
3. Assess Rust rewrite architecture and timeline confidence
4. Evaluate cloud-readiness and migration path
5. Verify patent validity and freedom-to-operate

RISK RATING: MODERATE-HIGH
The technical foundation is sound with significant IP value, but technical
debt and key-person risks require mitigation before closing.

---
Prepared for Board Review
""")
    
    println("Technical due diligence assessment saved to secrets/docs/due-diligence-tech-assessment.txt")
}