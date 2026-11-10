package flexygames

class SeedController {

    def index() {
        [message: "Hello World!"]
    }
    
    def run() {
        println "=== SeedController.run() called ==="
        
        try {
            DeveloperDataSeeder.seed()
            render "Seed completed successfully"
        } catch (Exception e) {
            render "Error: ${e.message}"
            e.printStackTrace()
        }
    }
}
