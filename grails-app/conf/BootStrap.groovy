package flexygames

import flexygames.DeveloperDataSeeder

class BootStrap {

    def init = { servletContext ->
        def logFile = new File("D:/git-projects/flexygames/bootstrap.log")
        logFile.text = "BootStrap.init() called at ${new Date()}\n"
        println "***** FlexyGames: ApplicationBootStrap.init() started..."
        Locale.setDefault(new Locale("en"))
        
        // Force re-seed by clearing the database first
        logFile.text += "Forcing re-seed by clearing existing data...\n"
        try {
            User.deleteAll()
            Team.deleteAll()
            Role.deleteAll()
            GameType.deleteAll()
            Playground.deleteAll()
            SessionGroup.deleteAll()
            Session.deleteAll()
            logFile.text += "Database cleared successfully.\n"
        } catch (Exception e) {
            logFile.text += "Error clearing database: ${e.message}\n"
        }
        
        logFile.text += "Running DeveloperDataSeeder.seed()...\n"
        DeveloperDataSeeder.seed()
        logFile.text += "DeveloperDataSeeder.seed() completed at ${new Date()}\n"
        println "***** FlexyGames: ApplicationBootStrap.init() returned..."
        logFile.text += "BootStrap.init() completed at ${new Date()}\n"
    }
    
    def destroy = {
        println "***** FlexyGames: ApplicationBootStrap.destroy() started..."
        println "***** FlexyGames: ApplicationBootStrap.destroy() returned..."                   
    }
}
