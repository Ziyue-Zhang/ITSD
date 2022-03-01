// @GENERATOR:play-routes-compiler
// @SOURCE:E:/新桌面/新建文件夹/ITSD/conf/routes
// @DATE:Tue Mar 01 21:57:28 CST 2022


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
