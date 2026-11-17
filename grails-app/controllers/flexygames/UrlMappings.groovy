package flexygames

class UrlMappings {

    static mappings = {
        
        // Debug mapping
        "/debug"(controller:"debug", action:"index")
        
        // Specific admin mappings must come before the generic one
        "/admin"(controller:"admin", action:"index")
        "/admin/comment"(controller:"comment", action:"index")
        "/admin/gameAction"(controller:"gameAction", action:"index")
        "/admin/gameSkill"(controller:"gameSkill", action:"index")
        "/admin/gameType"(controller:"gameType", action:"index")
        "/admin/membership"(controller:"membership", action:"index")
        "/admin/participation"(controller:"participation", action:"index")
        "/admin/playground"(controller:"playground", action:"index")
        "/admin/reminder"(controller:"reminder", action:"index")
        "/admin/role"(controller:"role", action:"index")
        "/admin/session"(controller:"session", namespace:"admin")
        "/admin/sessionGroup"(controller:"sessionGroup", action:"index")
        "/admin/sessionRound"(controller:"sessionRound", action:"index")
        "/admin/subscription"(controller:"subscription", action:"index")
        "/admin/team"(controller:"team", action:"index")
        "/admin/user"(controller:"user", action:"index")
        "/admin/vote"(controller:"vote", action:"index")
        
        // commented in order to use a static index.html
        //"/"(controller:"site", action:"home")
        
        "/"(controller:"site", action:"home")
        
        "/home"(controller:"site", action:"home")
        
        "/about"(controller:"site", action:"about")

        "500"(view:'/error')

        "404"(view:'/notFound')
        
        // Generic controller mapping - must come last
        "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }
    }
}
