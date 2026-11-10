package flexygames

import grails.gorm.transactions.Transactional

@Transactional
class DatabaseSeedingService {

    def init() {
        // Create roles if not exist
        createRoles()

        // Create game types if not exist
        createGameTypes()

        // Create sample users if not exist
        createUsers()

        // Create teams if not exist
        createTeams()

        // Create session groups if not exist
        createSessionGroups()

        // Create playgrounds if not exist
        createPlaygrounds()
    }

    private void createRoles() {
        def adminRole = Role.findOrCreateByName('Administrator')
        adminRole.description = 'Administrator with full access'

        def managerRole = Role.findOrCreateByName('Manager')
        managerRole.description = 'Team manager withlimited access'

        def playerRole = Role.findOrCreateByName('Player')
        playerRole.description = 'Player with viewing and participation access'
    }

    private void createGameTypes() {
        def ballType = GameType.findOrCreateByName('Calcio Ball')
        ballType.description = 'Traditional football with a ball'
        ballType.save(failOnError: true)

        def flyingType = GameType.findOrCreateByName('Flying Disc')
        flyingType.description = 'Ultimate frisbee style game'
        flyingType.save(failOnError: true)
    }

    private void createUsers() {
        // Admin user
        def admin = User.findOrCreateByUsername('admin')
        admin.firstName = 'System'
        admin.lastName = 'Admin'
        admin.email = 'admin@flexygames.local'
        admin.passwordHash = 'admin123'
        admin.phoneNumber = '+39 333 0000000'
        admin.city = 'Milan'
        admin.registrationDate = new Date()
        admin.avatarName = 'default-avatar.png'
        admin.avatarColor = '#3498db'
        admin.scoreInCurrentSession = 0
        admin.membershipInCurrentSession = null
        admin.teamsInCurrentSession = new TreeSet<>()

        if (!admin.roles || admin.roles.isEmpty()) {
            def adminRole = Role.findByName('Administrator')
            admin.roles = [adminRole]
        }
        admin.save(failOnError: true)

        // Sample user 1
        def user1 = User.findOrCreateByUsername('lucarossi')
        user1.firstName = 'Luca'
        user1.lastName = 'Rossi'
        user1.email = 'luca.rossi@flexygames.local'
        user1.passwordHash = 'user123'
        user1.phoneNumber = '+39 333 1111111'
        user1.city = 'Milan'
        user1.yearBirthDate = 1985
        user1.registrationDate = new Date()
        user1.avatarName = 'user1-avatar.png'
        user1.avatarColor = '#2ecc71'

        if (!user1.roles || user1.roles.isEmpty()) {
            def playerRole = Role.findByName('Player')
            user1.roles = [playerRole]
        }
        user1.save(failOnError: true)

        // Sample user 2
        def user2 = User.findOrCreateByUsername('marcobianchi')
        user2.firstName = 'Marco'
        user2.lastName = 'Bianchi'
        user2.email = 'marco.bianchi@flexygames.local'
        user2.passwordHash = 'user123'
        user2.phoneNumber = '+39 333 2222222'
        user2.city = 'Rome'
        user2.yearBirthDate = 1990
        user2.registrationDate = new Date()
        user2.avatarName = 'user2-avatar.png'
        user2.avatarColor = '#f39c12'

        if (!user2.roles || user2.roles.isEmpty()) {
            def playerRole = Role.findByName('Player')
            user2.roles = [playerRole]
        }
        user2.save(failOnError: true)

        // Sample user 3
        def user3 = User.findOrCreateByUsername('giuseppeverdi')
        user3.firstName = 'Giuseppe'
        user3.lastName = 'Verdi'
        user3.email = 'giuseppe.verdi@flexygames.local'
        user3.passwordHash = 'user123'
        user3.phoneNumber = '+39 333 3333333'
        user3.city = 'Naples'
        user3.yearBirthDate = 1988
        user3.registrationDate = new Date()
        user3.avatarName = 'user3-avatar.png'
        user3.avatarColor = '#9b59b6'

        if (!user3.roles || user3.roles.isEmpty()) {
            def playerRole = Role.findByName('Player')
            user3.roles = [playerRole]
        }
        user3.save(failOnError: true)
    }

    private void createTeams() {
        // Team 1
        def team1 = Team.findOrCreateByName('Calcio Masters')
        team1.description = 'Professional football team'
        team1.city = 'Milan'
        team1.webUrl = 'http://www.calciomasters.example'
        team1.email = 'info@calciomasters.example'
        team1.logoName = 'team1-logo.png'

        if (!team1.defaultSessionGroup) {
            def sessionGroup = SessionGroup.findByName('Football League')
            team1.defaultSessionGroup = sessionGroup
        }
        team1.save(failOnError: true)

        // Team 2
        def team2 = Team.findOrCreateByName('Disc Squad')
        team2.description = 'Ultimate frisbee team'
        team2.city = 'Rome'
        team2.webUrl = 'http://www.disqsquad.example'
        team2.email = 'info@disqsquad.example'
        team2.logoName = 'team2-logo.png'

        if (!team2.defaultSessionGroup) {
            def sessionGroup = SessionGroup.findByName('Ultimate League')
            team2.defaultSessionGroup = sessionGroup
        }
        team2.save(failOnError: true)

        // Team 3
        def team3 = Team.findOrCreateByName('Volley Stars')
        team3.description = 'Volleyball team'
        team3.city = 'Naples'
        team3.webUrl = 'http://www.volleystars.example'
        team3.email = 'info@volleystars.example'
        team3.logoName = 'team3-logo.png'
        team3.save(failOnError: true)
    }

    private void createSessionGroups() {
        // Session Group 1
        def group1 = SessionGroup.findOrCreateByName('Football League')
        group1.description = 'Weekly football league matches'
        group1.visible = true
        group1.competition = true
        group1.defaultMinPlayerNbr = 5
        group1.defaultPreferredPlayerNbr = 11

        // Add default teams
        def team1 = Team.findByName('Calcio Masters')
        if (team1) {
            group1.addToDefaultTeams(team1)
        }
        group1.save(failOnError: true)

        // Session Group 2
        def group2 = SessionGroup.findOrCreateByName('Ultimate League')
        group2.description = '每周 Ultimate 比赛'
        group2.visible = true
        group2.competition = true
        group2.defaultMinPlayerNbr = 7
        group2.defaultPreferredPlayerNbr = 14

        def team2 = Team.findByName('Disc Squad')
        if (team2) {
            group2.addToDefaultTeams(team2)
        }
        group2.save(failOnError: true)

        // Session Group 3
        def group3 = SessionGroup.findOrCreateByName('Friendly Matches')
        group3.description = 'Casual weekend matches'
        group3.visible = true
        group3.competition = false
        group3.defaultMinPlayerNbr = 6
        group3.defaultPreferredPlayerNbr = 12
        group3.save(failOnError: true)
    }

    private void createPlaygrounds() {
        // Playground 1
        def pg1 = Playground.findOrCreateByName('Milan Football Field')
        pg1.street = 'Via Arena Torri'
        pg1.zip = '20121'
        pg1.city = 'Milan'
        pg1.country = 'Italy'
        pg1.description = 'Main football field in Milan'
        pg1.gmapsUrl = 'https://maps.google.com/?q=Milan+Football+Field'
        pg1.save(failOnError: true)

        // Playground 2
        def pg2 = Playground.findOrCreateByName('Rome Disc Field')
        pg2.street = 'Piazza di Giusppe'
        pg2.zip = '00184'
        pg2.city = 'Rome'
        pg2.country = 'Italy'
        pg2.description = 'Ultimate frisbee field in Rome'
        pg2.gmapsUrl = 'https://maps.google.com/?q=Rome+Disc+Field'
        pg2.save(failOnError: true)

        // Playground 3
        def pg3 = Playground.findOrCreateByName('Naples Beach Court')
        pg3.street = 'Lido di Napoli'
        pg3.zip = '80126'
        pg3.city = 'Naples'
        pg3.country = 'Italy'
        pg3.description = 'Beach volleyball court'
        pg3.gmapsUrl = 'https://maps.google.com/?q=Naples+Beach+Court'
        pg3.save(failOnError: true)
    }
}
