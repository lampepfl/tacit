requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-6938/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-6938/repo/astropy/io/fits/fitsrec.py")
  val content = f.read()
  val fixedContent = content.replace("            output_field.replace(encode_ascii('E'), encode_ascii('D'))", "            output_field = output_field.replace(encode_ascii('E'), encode_ascii('D'))")
  f.write(fixedContent)
  println("Done")
}
