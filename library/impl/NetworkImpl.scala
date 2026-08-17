package tacit.library

/** Concrete [[Network]] capability. Carries the glob allowlist of permitted
 *  hosts. Patterns may be literal hostnames (e.g. `"api.example.com"`) or
 *  contain `*` wildcards (e.g. `"*.example.com"`).
 *
 *  The allowlist is constructed by [[InterfaceImpl.requestNetwork]] from the
 *  scope's declared `hosts`, after validating those hosts against the
 *  server-configured `networkPermissions` policy. */
final class NetworkImpl private[library] (val permittedHosts: Set[String]) extends Network:
  /** The patterns are agent-declared, so they are compiled once per scope and
   *  kept here rather than in the shared [[GlobMatcher]] cache; otherwise agent
   *  code could grow that process-wide cache without bound by declaring
   *  ever-new hosts. */
  private val matchers = permittedHosts.toList.map(GlobMatcher.compile)

  def validateHost(host: String): Unit =
    if !matchers.exists(_.matcher(host).matches()) then
      throw SecurityException(
        s"Access denied: host '$host' does not match any permitted pattern in $permittedHosts"
      )
