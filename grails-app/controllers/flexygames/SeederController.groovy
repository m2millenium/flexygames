package flexygames

class SeederController {
    def index() {
        def result = []
        result << "<html><body>"
        result << "<h2>Seeder Controller</h2>"
        result << "<p>Starting seeder...</p>"
        
        try {
            result << "<p>Calling DeveloperDataSeeder.seed()...</p>"
            DeveloperDataSeeder.seed()
            result << "<p>Seeder completed successfully</p>"
        } catch (Exception e) {
            result << "<p>Error: ${e.message}</p>"
            result << "<pre>${e.stackTrace.join('\n')}</pre>"
            result << "<p>Exception class: ${e.class.name}</p>"
        }
        
        result << "</body></html>"
        render result.join()
    }
}
