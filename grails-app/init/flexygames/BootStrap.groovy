package flexygames

import flexygames.DeveloperDataSeeder

class BootStrap {

    def init = { servletContext ->
        try {
            new File("D:/tmp/bootstrap.log").text = "BootStrap.init() started...\n"
            Locale.setDefault(new Locale("en"))
            
            // Force re-seed by clearing the database first
            // Skip if database isn't initialized yet (first run)
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
                // Database not initialized yet, skip clearing
                println "Database clearing skipped (not initialized yet): ${e.message}"
            }
            
            DeveloperDataSeeder.seed()
            println "DeveloperDataSeeder.seed() completed."
            println "***** FlexyGames: BootStrap.init() returned..."
        } catch (Exception e) {
            println "BootStrap.init() error: ${e.message}"
            e.printStackTrace()
        }
    }
    
    def destroy = {
        println "***** FlexyGames: BootStrap.destroy() started..."
        println "***** FlexyGames: BootStrap.destroy() returned..."                   
    }
}
