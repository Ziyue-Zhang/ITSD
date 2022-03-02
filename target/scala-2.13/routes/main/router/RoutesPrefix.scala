// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/zzy/Desktop/ITSD-DT2022-Template/ITSD/conf/routes
// @DATE:Wed Mar 02 15:52:43 CST 2022


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
