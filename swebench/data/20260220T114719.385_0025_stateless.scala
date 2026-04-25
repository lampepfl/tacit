requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo") {
  val wcsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo/astropy/wcs/wcs.py")
  val content = wcsFile.read()
  val lines = content.split("\n").toList
  
  // Insert the check after line 1219 (index 1219-1 = 1218)
  // Line 1219 in the file is empty (index 1218), line 1220 is "            xy = np.hstack..."
  // We want to add after the except block which ends at line 1219
  // The new lines should be added between line 1219 and 1220
  
  val newLines = lines.zipWithIndex.flatMap { case (line, idx) =>
    if (idx == 1219) {  // This is the empty line after the except block (line 1220 in 1-based)
      List(
        "            if axes[0].size == 0:",
        "                return [np.empty(axes[0].shape) for i in range(len(axes))]",
        "",
        line
      )
    } else {
      List(line)
    }
  }
  
  wcsFile.write(newLines.mkString("\n"))
  "Done"
}