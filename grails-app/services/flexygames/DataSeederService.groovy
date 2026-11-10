package flexygames

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class DataSeederService {

    def seed() {
        println "DataSeederService.seed() called"
        // Seeding logic goes here
        "Seeding complete"
    }
}
