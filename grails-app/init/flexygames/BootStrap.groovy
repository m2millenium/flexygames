package flexygames

import flexygames.DeveloperDataSeeder

class BootStrap {

    def init = { servletContext ->
        println "***** FlexyGames: ApplicationBootStrap.init() started..."
        Locale.setDefault(new Locale("en"))
        
        // Force re-seed by clearing the database first
        try {
            User.deleteAll()
            Team.deleteAll()
            Role.deleteAll()
            GameType.deleteAll()
            Playground.deleteAll()
            SessionGroup.deleteAll()
            Session.deleteAll()
            println "Database cleared successfully."
        } catch (Exception e) {
            println "Error clearing database: ${e.message}"
        }
        
        println "Running DeveloperDataSeeder.seed()..."
        DeveloperDataSeeder.seed()
        println "DeveloperDataSeeder.seed() completed."
        println "***** FlexyGames: ApplicationBootStrap.init() returned..."
    }
    
    def destroy = {
        println "***** FlexyGames: ApplicationBootStrap.destroy() started..."
        println "***** FlexyGames: ApplicationBootStrap.destroy() returned..."                   
    }
}
