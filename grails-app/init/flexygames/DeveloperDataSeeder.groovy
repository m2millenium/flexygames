package flexygames

import org.apache.shiro.crypto.hash.Sha512Hash

/**
 * DeveloperDataSeeder
 * 
 * Creates a rich development database with:
 * - Multiple teams (5)
 * - Multiple players (15+) with various roles
 * - Mario as manager of "Team Mario"
 * - Many historical sessions (last 10 days) with completed participations
 * - Game actions, votes, and historical stats for each player
 * 
 * Usage: Call DeveloperDataSeeder.seed() from a controller or console
 */
class DeveloperDataSeeder {

    static void seed() {
        println("***** Starting Developer Data Seeding *****")
        
        // 1. Create roles if not exist
        def adminRole = Role.findByName("Administrator")
        if (!adminRole) {
            adminRole = new Role(name: "Administrator", comments: "System admins with full access").save(failOnError: true, flush: true)
        }

        def playerRole = Role.findByName("Player")
        if (!playerRole) {
            playerRole = new Role(name: "Player", comments: "Regular players with basic access").save(failOnError: true, flush: true)
            playerRole.addToPermissions(["sessions: *", "myself: *", "manager: *", "fileUploader: *"]).save(failOnError: true)
        }

        def managerRole = Role.findByName("Manager")
        if (!managerRole) {
            managerRole = new Role(name: "Manager", comments: "Team managers with additional privileges").save(failOnError: true, flush: true)
            managerRole.addToPermissions(["sessions: *", "myself: *", "manager: *", "fileUploader: *", "team: *"]).save(failOnError: true)
        }

        println "Roles created/verified"

        // 2. Create teams
        def teams = createTeams()
        println "Teams created: ${teams.size()}"

        // 3. Create players
        def players = createPlayers(teams, adminRole, playerRole, managerRole)
        println "Players created: ${players.size()}"

        // 4. Create game types
        def gameTypes = createGameTypes()
        println "Game types created: ${gameTypes.size()}"

        // 5. Create playgrounds
        def playgrounds = createPlaygrounds()
        println "Playgrounds created: ${playgrounds.size()}"

        // 6. Create session groups
        def sessionGroups = createSessionGroups(gameTypes, playgrounds, teams)
        println "Session groups created: ${sessionGroups.size()}"

        // 7. Create historical sessions (last 10 days) with completed participations
        def sessions = createHistoricalSessions(sessionGroups, players, teams)
        println "Sessions created: ${sessions.size()}"

        println "***** Developer Data Seeding Complete *****"
    }

    private static Map<String, Team> createTeams() {
        def teamMap = [:]

        def teamNames = [
            [name: "Team Mario", city: "Paris", email: "mario@flexygames.com", webUrl: "http://mario.flexygames.com"],
            [name: "Team Giulla", city: "Lyon", email: "giulia@flexygames.com", webUrl: "http://giulia.flexygames.com"],
            [name: "Team Luca", city: "Milan", email: "luca@flexygames.com", webUrl: "http://luca.flexygames.com"],
            [name: "Team Alessandro", city: "Rome", email: "alessandro@flexygames.com", webUrl: "http://alessandro.flexygames.com"],
            [name: "Team Sofia", city: "Rome", email: "sofia@flexygames.com", webUrl: "http://sofia.flexygames.com"]
        ]

        teamNames.each { t ->
            def team = Team.findByName(t.name)
            if (!team) {
                team = new Team(
                    name: t.name,
                    city: t.city,
                    email: t.email,
                    webUrl: t.webUrl,
                    description: "Team ${t.name.substring(5)}"
                ).save(failOnError: true, flush: true)
            }
            teamMap[t.name] = team
        }

        // Set Mario as manager of "Team Mario"
        def mario = User.findByUsername("mario")
        if (mario && teamMap["Team Mario"]) {
            def membership = Membership.findWhere(user: mario, team: teamMap["Team Mario"])
            if (!membership) {
                new Membership(
                    user: mario,
                    team: teamMap["Team Mario"],
                    manager: true,
                    startIndex: new Date() - 365
                ).save(failOnError: true, flush: true)
            } else if (!membership.manager) {
                membership.manager = true
                membership.save(failOnError: true, flush: true)
            }
        }

        return teamMap
    }

    private static Map<String, User> createPlayers(Map<String, Team> teams, Role adminRole, Role playerRole, Role managerRole) {
        def playerMap = [:]

        def playersData = [
            // Mario - Administrator, manager
            [username: "mario", firstName: "Mario", lastName: "Biasi", email: "mario.biasi@gmail.com", password: "password", teamName: "Team Mario"],
            // Giulla - Administrator
            [username: "giulia", firstName: "Giulla", lastName: "Rossi", email: "giulia@example.com", password: "password", teamName: "Team Giulla"],
            // Luca - Player
            [username: "luca", firstName: "Luca", lastName: "Bianchi", email: "luca@example.com", password: "password", teamName: "Team Luca"],
            // More players
            [username: "alessandro", firstName: "Alessandro", lastName: "Verdi", email: "alessandro@example.com", password: "password", teamName: "Team Alessandro"],
            [username: "sofia", firstName: "Sofia", lastName: "Rossi", email: "sofia@example.com", password: "password", teamName: "Team Sofia"],
            [username: "martina", firstName: "Martina", lastName: "Bianchi", email: "martina@example.com", password: "password", teamName: "Team Mario"],
            [username: "matteo", firstName: "Matteo", lastName: "Verdi", email: "matteo@example.com", password: "password", teamName: "Team Giulla"],
            [username: "francesca", firstName: "Francesca", lastName: "Rossi", email: "francesca@example.com", password: "password", teamName: "Team Luca"],
            [username: "lukas", firstName: "Lukas", lastName: "Muller", email: "lukas@example.com", password: "password", teamName: "Team Alessandro"],
            [username: "anna", firstName: "Anna", lastName: "Muller", email: "anna@example.com", password: "password", teamName: "Team Sofia"],
            [username: "leo", firstName: "Leo", lastName: "Smith", email: "leo@example.com", password: "password", teamName: "Team Mario"],
            [username: "sara", firstName: "Sara", lastName: "Johnson", email: "sara@example.com", password: "password", teamName: "Team Giulla"],
            [username: "tom", firstName: "Tom", lastName: "Williams", email: "tom@example.com", password: "password", teamName: "Team Luca"],
            [username: "emma", firstName: "Emma", lastName: "Brown", email: "emma@example.com", password: "password", teamName: "Team Alessandro"],
            [username: "jack", firstName: "Jack", lastName: "Davis", email: "jack@example.com", password: "password", teamName: "Team Sofia"]
        ]

        playersData.each { p ->
            def user = User.findByUsername(p.username)
            if (!user) {
                user = new User(
                    avatarUrl: "https://api.dicebear.com/9.x/initials/svg?seed=${p.username}&name=${extractInitials(p.username)}&backgroundColor=FF6B6B",
                    avatarColor: "FF6B6B",
                    username: p.username,
                    passwordHash: new Sha512Hash(p.password).toHex(),
                    firstName: p.firstName,
                    lastName: p.lastName,
                    email: p.email,
                    registrationDate: new Date() - (Math.random() * 365 * 2).toInteger(),
                    yearBirthDate: 1960 + (Math.random() * 40).toInteger(),
                    city: p.teamName.contains("Mario") ? "Paris" : p.teamName.contains("Giulla") ? "Lyon" : "Milan",
                    phoneNumber: "+33 6 12 34 56 78"
                )

                // Assign roles BEFORE saving
                user.addToRoles(playerRole)
                if (p.username in ["mario", "giulia"]) {
                    user.addToRoles(adminRole)
                }
                // Assign Manager role to team managers
                if (p.username == "mario") {
                    user.addToRoles(managerRole)
                }

                // Generate and assign avatar
                user.avatarColor = generateAvatarColor(p.username)
                user.avatarUrl = generateAvatarUrl(p.username, user.avatarColor)

                // Now save AFTER roles are assigned
                user.save(failOnError: true, flush: true)

                // Add to team
                def team = teams[p.teamName]
                if (team) {
                    new Membership(
                        user: user,
                        team: team,
                        manager: p.username == "mario",
                        regularForTraining: true,
                        regularForCompetition: false
                    ).save(failOnError: true, flush: true)
                }
            }
            playerMap[p.username] = user
        }

        return playerMap
    }

    private static Map<String, GameType> createGameTypes() {
        def typeMap = [:]

        def footType = GameType.findByName("Football")
        if (!footType) {
            footType = new GameType(name: "Football").save(failOnError: true, flush: true)
            ["Goalkeeper", "Defender", "Midfielder", "Forward"].each { skill ->
                footType.addToSkills(new GameSkill(code: skill, type: footType)).save(failOnError: true)
            }
        }
        typeMap["Football"] = footType

        def volleyType = GameType.findByName("Volleyball")
        if (!volleyType) {
            volleyType = new GameType(name: "Volleyball").save(failOnError: true, flush: true)
            ["Libero", "Setter", "Blocker", "Hitter"].each { skill ->
                volleyType.addToSkills(new GameSkill(code: skill, type: volleyType)).save(failOnError: true)
            }
        }
        typeMap["Volleyball"] = volleyType

        return typeMap
    }

    private static Map<String, Playground> createPlaygrounds() {
        def playgroundMap = [:]

        def playgroundNames = ["UrbanFoot de Bonneuil", "DreamFoot de Meudon", "Lycée Turgot", "Lycée Paul-Valérie", "Stade Jean Bouin"]

        playgroundNames.each { name ->
            def pg = Playground.findByName(name)
            if (!pg) {
                pg = new Playground(name: name).save(failOnError: true, flush: true)
            }
            playgroundMap[name] = pg
        }

        return playgroundMap
    }

    private static List<SessionGroup> createSessionGroups(Map<String, GameType> gameTypes, Map<String, Playground> playgrounds, Map<String, Team> teams) {
        def sessionGroups = []

        def sgData = [
            [name: "Entraînements de foot à 5 d'ASAS", gameType: "Football", playground: "UrbanFoot de Bonneuil", team: "Team Mario"],
            [name: "Amical Football", gameType: "Football", playground: "DreamFoot de Meudon", team: "Team Giulla"],
            [name: "Volleyball d'ASAS", gameType: "Volleyball", playground: "Lycée Turgot", team: "Team Luca"],
            [name: "Matchs Amicaux", gameType: "Football", playground: "Lycée Paul-Valérie", team: "Team Alessandro"],
            [name: "Training Sessions", gameType: "Volleyball", playground: "Stade Jean Bouin", team: "Team Sofia"]
        ]

        sgData.each { sg ->
            def sgObj = SessionGroup.findByName(sg.name)
            if (!sgObj) {
                sgObj = new SessionGroup(
                    name: sg.name,
                    defaultType: gameTypes[sg.gameType],
                    defaultPlayground: playgrounds[sg.playground],
                    defaultMinPlayerNbr: 8,
                    defaultMaxPlayerNbr: 12,
                    defaultPreferredPlayerNbr: 10,
                    defaultDuration: 90,
                    defaultLockingTime: 24 * 60,
                    competition: false,
                    visible: true
                ).save(failOnError: true, flush: true)
                sgObj.addToDefaultTeams(teams[sg.team]).save(failOnError: true)
            }
            sessionGroups << sgObj
        }

        return sessionGroups
    }

    private static List<Session> createHistoricalSessions(List<SessionGroup> sessionGroups, Map<String, User> players, Map<String, Team> teams) {
        def sessions = []
        def random = new Random()

        // Create 10 historical sessions in the last 10 days
        (1..10).each { i ->
            def group = sessionGroups[i % sessionGroups.size()]
            def playground = group.defaultPlayground
            def sessionDate = new Date() - (10 - i)  // Sessions from yesterday back to 9 days ago
            
            // Set session time to afternoon (17:00)
            def sessionTime = sessionDate.clone() as Date
            sessionTime.setHours(17)
            sessionTime.setMinutes(0)
            sessionTime.setSeconds(0)
            
            def sessionName = "Session #${100 + i} - ${sessionTime.format('yyyy-MM-dd')}"

            def session = Session.findByName(sessionName)
            if (!session) {
                session = new Session(
                    name: sessionName,
                    date: sessionTime,
                    duration: 90,
                    rdvBeforeStart: 15,
                    lockingTime: 24 * 60,  // Locked 24 hours before
                    playground: playground,
                    type: group.defaultType,
                    group: group,
                    creation: new Date() - (11 - i),
                    lastUpdate: new Date() - (10 - i)
                ).save(failOnError: true, flush: true)
            }

            sessions << session

            // Create participations for this session
            createParticipationsForSession(session, players, random)
        }

        // Create session rounds (games) for each session
        sessions.each { session ->
            if (!session.rounds || session.rounds.isEmpty()) {
                createSessionRounds(session, players, random)
            }
        }

        // Calculate counter statistics from participations
        calculateUserCounters()

        return sessions
    }

    private static void createParticipationsForSession(Session session, Map<String, User> players, Random random) {
        def playersList = players.values().toList()
        def numParticipants = 8 + random.nextInt(5)  // 8-12 participants

        // Shuffle and pick participants
        playersList = playersList.sort { random.nextFloat() }.take(numParticipants)

        playersList.eachWithIndex { player, idx ->
            def statusCode
            def lastUpdater = player.username

            // Randomly assign status - mostly DONE_*
            def statusRandom = random.nextFloat()
            if (statusRandom < 0.5) {
                statusCode = "DONE_GOOD"  // 50% good performance
            } else if (statusRandom < 0.7) {
                statusCode = "DONE_LATE"  // 20% late
            } else if (statusRandom < 0.85) {
                statusCode = "DONE_BAD"   // 15% bad performance
            } else if (statusRandom < 0.95) {
                statusCode = "AVAILABLE"  // 10% available (didn't play)
            } else {
                statusCode = "REQUESTED"  // 5% requested (didn't show)
            }

            def participation = Participation.findBySessionAndPlayer(session, player)
            if (!participation) {
                participation = new Participation(
                    player: player,
                    session: session,
                    statusCode: statusCode,
                    userLog: "Participation created on ${new Date().format('yyyy-MM-dd HH:mm:ss')}",
                    lastUpdate: session.date,
                    lastUpdater: lastUpdater
                ).save(failOnError: true, flush: true)
            }

            // Create game actions for DONE_GOOD participants
            if (statusCode == "DONE_GOOD" && random.nextFloat() < 0.3) {
                createGameAction(session, player, random)
            }

            // Create votes for this participation
            if (statusCode in ["DONE_GOOD", "DONE_LATE", "DONE_BAD"] && random.nextFloat() < 0.4) {
                createVote(session, player, random)
            }
        }
    }

    private static void createGameAction(Session session, User player, Random random) {
        def round = session.rounds ? session.rounds[0] : null
        if (!round && session.rounds?.size() > 0) {
            round = session.rounds[0]
        }
        if (!round) {
            // Create round if it doesn't exist
            def playersFromParticipations = session.participations?.collect { it.player }
            def totalPlayers = playersFromParticipations?.size() ?: 0
            def half = Math.min(6, (totalPlayers / 2).toInteger())
            round = new SessionRound(
                session: session,
                playersForTeamA: playersFromParticipations?.take(half),
                playersForTeamB: playersFromParticipations?.drop(half)?.take(6)
            ).save(failOnError: true, flush: true)
            session.addToRounds(round).save(failOnError: true, flush: true)
        }

        // Create 1-3 game actions per player
        (1..(1 + random.nextInt(3))).each {
            def action = GameAction.findWhere(
                sessionRound: round,
                mainContributor: player,
                secondaryContributor: null,
                forFirstTeamIfOrphelin: null
            )
            if (!action) {
                new GameAction(
                    sessionRound: round,
                    mainContributor: player,
                    secondaryContributor: null,
                    forFirstTeamIfOrphelin: random.nextBoolean()
                ).save(failOnError: true, flush: true)
            }
        }
    }

    private static void createVote(Session session, User player, Random random) {
        // Query participations directly to avoid lazy-loading issues
        def playerIds = session.participations?.collect { it.player.id }
        if (!playerIds || playerIds.isEmpty()) return
        
        def voterPlayers = User.createCriteria().list {
            id 'in', playerIds
            ne 'id', player.id
        }
        if (voterPlayers.isEmpty()) return

        // Random voter from participants
        def voter = voterPlayers[random.nextInt(voterPlayers.size())]

        // Check if vote exists
        def vote = Vote.findWhere(user: voter, player: player, session: session)
        if (!vote) {
            new Vote(
                user: voter,
                player: player,
                session: session,
                score: 1 + random.nextInt(5)  // 1-5 stars
            ).save(failOnError: true, flush: true)
        }
    }

    private static void createSessionRounds(Session session, Map<String, User> players, Random random) {
        // Create 2-3 rounds per session
        def numRounds = 2 + random.nextInt(2)
        (1..numRounds).each { r ->
            def playersFromParticipations = session.participations?.collect { it.player }
            def totalPlayers = playersFromParticipations?.size() ?: 0
            def half = Math.min(6, (totalPlayers / 2).toInteger())
            def round = new SessionRound(
                session: session,
                playersForTeamA: playersFromParticipations?.take(half),
                playersForTeamB: playersFromParticipations?.drop(half)?.take(6)
            ).save(failOnError: true, flush: true)
            session.addToRounds(round).save(failOnError: true, flush: true)
        }
    }

    private static void calculateUserCounters() {
        // Calculate participation counters for all users from participations
        // Iterate and count manually since criteria.count doesn't support it well
        def users = User.list()
        users.each { user ->
            def participations = Participation.where { player == user }.list()
            user.participationCounter = participations.size()
            
            def absenceStatuses = ['REQUESTED', 'REMOVED']
            def absences = participations.findAll { it.statusCode in absenceStatuses }
            user.absenceCounter = absences.size()
            
            def delays = participations.findAll { it.statusCode == 'DONE_LATE' }
            user.delayCounter = delays.size()
            
            user.gateCrashCounter = 0
            
            def waitingList = participations.findAll { it.statusCode == 'WAITING_LIST' }
            user.waitingListCounter = waitingList.size()
            
            // Generate and assign avatar
            user.avatarColor = generateAvatarColor(user.username)
            user.avatarUrl = generateAvatarUrl(user.username, user.avatarColor)
            
            user.save(failOnError: true, flush: true)
            user.discard()
        }
    }
    
    /**
     * Generate avatar color based on username
     */
    private static String generateAvatarColor(String username) {
        def colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEEAD', 
                      '#D4A5A5', '#9B59B6', '#3498DB', '#E67E22', '#2ECC71']
        int hash = username.hashCode()
        return colors[Math.abs(hash) % colors.size()]
    }
    
    /**
     * Generate avatar URL using DiceBear API
     */
    private static String generateAvatarUrl(String username, String color) {
        // Use DiceBear's initial-based avatar with the user's name initials
        String initials = extractInitials(username)
        return "https://api.dicebear.com/9.x/initials/svg?seed=${username}&name=${initials}&backgroundColor=${color.replace('#', '')}"
    }
    
    /**
     * Extract initials from username
     */
    private static String extractInitials(String username) {
        def parts = username.split(/[\.\\-_]/)
        if (parts.size() >= 2) {
            return "${parts[0][0]}${parts[-1][0]}".toUpperCase()
        }
        return username.substring(0, Math.min(2, username.length())).toUpperCase()
    }
}
