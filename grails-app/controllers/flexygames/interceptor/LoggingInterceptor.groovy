package flexygames.interceptor

import java.text.SimpleDateFormat

import org.apache.shiro.SecurityUtils

import flexygames.User
class  LoggingInterceptor {

    int order = HIGHEST_PRECEDENCE+99

    LoggingInterceptor() {
        matchAll().excludes(controller:"assets")
    }

    boolean before() {
        if (controllerName && controllerName != 'images' && controllerName != 'css'
                && controllerName != 'js' && controllerName != 'favicon.ico') {
            // keep init time for stats
            request.setProperty("timeBeforeController", System.currentTimeMillis())

            // logging every action, big brother is watching you !
            def username = "anonymous"
            try {
                def subject = SecurityUtils.getSubject()
                if (subject) {
                    def principal = subject.getPrincipal()
                    if (principal) username = principal.toString()
                }
            } catch (Exception e) {
                // SecurityManager not available yet (during startup)
                username = "anonymous (" + request.remoteAddr + ")"
            }
            if (username == "null") username = "anonymous (" + request.remoteAddr + ")"
            //println '**************************************************************************************************************'
            println (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " FlexyGames : User $username is doing " + controllerName + "/" + actionName)
            //println '**************************************************************************************************************'
        }
        true
    }

    boolean after() {
        // keep current time for stats
        request.setProperty("timeAfterController", System.currentTimeMillis())
        true
    }

    void afterView() {
        // no-op
    }
}
