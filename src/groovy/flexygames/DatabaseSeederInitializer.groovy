package flexygames

import groovy.transform.CompileStatic
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
@CompileStatic
class DatabaseSeederInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    void onApplicationEvent(ContextRefreshedEvent event) {
        println "***** FlexyGames: DatabaseSeederInitializer triggered *****"
        DeveloperDataSeeder.seed()
        println "***** FlexyGames: Database seeding completed *****"
    }
}
