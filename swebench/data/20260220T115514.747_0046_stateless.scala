{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11019/repo") {
    exec("python", List("-m", "pytest", "tests/forms_tests/tests/test_media.py::MediaMergeTest::test_merge_warning", "-xvs"), workingDir = Option("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11019/repo"))
  }
}