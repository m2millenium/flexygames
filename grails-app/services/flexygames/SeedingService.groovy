package flexygames

import grails.gorm.transactions.Transactional

@Transactional
class SeedingService {

    static transactional = true
    
    def seed() {
        def output = new StringBuilder()
        output << "Starting seeding...\n"
        
        // Test database access
        def count = User.count()
        output << "User count before: ${count}\n"
        
        // Try to create a test user
        def user = User.findByUsername("seedtest123")
        if (!user) {
            user = new User(
                username: "seedtest123",
                passwordHash: "testpasswordhash",
                email: "seedtest@example.com",
                registrationDate: new Date()
            )
            if (user.save(failOnError: true, flush: true)) {
                output << "User created: ${user.username}\n"
            } else {
                output << "Failed to save user\n"
                if (user.errors) {
                    user.errors.allErrors.each { output << "Error: ${it}\n" }
                }
            }
        } else {
            output << "User already exists: ${user.username}\n"
        }
        
        count = User.count()
        output << "User count after: ${count}\n"
        output << "Done!\n"
        
        return [message: output.toString()]
    }
}
