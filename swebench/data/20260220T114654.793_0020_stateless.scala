requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo") {
  val wcsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo/astropy/wcs/wcs.py")
  val content = wcsFile.read()
  // Find the position of the function to modify
  val startMarker = "        def _return_list_of_arrays(axes, origin):\n            try:\n                axes = np.broadcast_arrays(*axes)\n            except ValueError:\n                raise ValueError(\n                    \"Coordinate arrays are not broadcastable to each other\")\n\n            xy = np.hstack([x.reshape((x.size, 1)) for x in axes])"
  val endMarker = "            if ra_dec_order and sky == 'output':\n                output = self._normalize_sky(output)\n                return (output[:, 0].reshape(axes[0].shape),\n                        output[:, 1].reshape(axes[0].shape))\n            return [output[:, i].reshape(axes[0].shape)\n                    for i in range(output.shape[1])]"
  
  val newCode = """        def _return_list_of_arrays(axes, origin):
            try:
                axes = np.broadcast_arrays(*axes)
            except ValueError:
                raise ValueError(
                    "Coordinate arrays are not broadcastable to each other")

            if axes[0].size == 0:
                return [np.empty(axes[0].shape) for i in range(len(axes))]

            xy = np.hstack([x.reshape((x.size, 1)) for x in axes])

            if ra_dec_order and sky == 'input':
                xy = self._denormalize_sky(xy)
            output = func(xy, origin)
            if ra_dec_order and sky == 'output':
                output = self._normalize_sky(output)
                return (output[:, 0].reshape(axes[0].shape),
                        output[:, 1].reshape(axes[0].shape))
            return [output[:, i].reshape(axes[0].shape)
                    for i in range(output.shape[1])]"""
  
  val newContent = content.replace(startMarker, newCode)
  wcsFile.write(newContent)
  "Done"
}