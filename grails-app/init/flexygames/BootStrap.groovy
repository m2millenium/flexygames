package flexygames

import flexygames.DeveloperDataSeeder

class BootStrap {

    def init = { servletContext ->
        println "DEBUG: BootStrap.init() started!"
        try {
            new File("D:/tmp/bootstrap.log").text = "BootStrap.init() started...\n"
            println "DEBUG: After bootstrap log write"
            Locale.setDefault(new Locale("en"))
            println "DEBUG: After Locale.setDefault"
            
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
                System.out.println "Database cleared successfully."
            } catch (Exception e) {
                // Database not initialized yet, skip clearing
                System.out.println "Database clearing skipped (not initialized yet): ${e.message}"
            }
            
            println "DEBUG: About to call DeveloperDataSeeder.seed()..."
            try {
                DeveloperDataSeeder.seed()
                println "DeveloperDataSeeder.seed() completed."
            } catch (Exception e) {
                println "DeveloperDataSeeder.seed() error: ${e.message}"
                e.printStackTrace()
            }
            
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
