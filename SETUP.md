# FlexyGames Setup Guide

## Java & Gradle Configuration
- **Java 8 path**: `C:/Program Files/Java/jdk8/jdk8u504-b01`
- **Gradle wrapper**: `gradlew` from project root
- Always use Java 8 when running Gradle: `export JAVA_HOME="C:/Program Files/Java/jdk8/jdk8u504-b01" && gradlew <task>`

## Data Seeding

### Seeder Location
`grails-app/init/flexygames/DeveloperDataSeeder.groovy`

### What it creates
- **Roles**: Administrator, Manager, Player (with permissions)
- **Teams**: 5 teams (Team Mario, Team Giulla, Team Luca, Team Alessandro, Team Sofia)
- **Players**: 15+ users with roles and team memberships
- **Game types**: Football, Volleyball (with skills)
- **Playgrounds**: 5 locations (UrbanFoot de Bonneuil, DreamFoot de Meudon, Lycée Turgot, Lycée Paul-Valérie, Stade Jean Bouin)
- **Session groups**: 5 groups with default teams/playgrounds
- **Historical sessions**: 10 past sessions with participations, rounds, game actions, and votes

### How seeding works
1. `BootStrap.init()` checks if "Administrator" role exists
2. If not, it calls `DeveloperDataSeeder.seed()`
3. Seeding creates all master data using `findOrCreate` patterns to avoid duplicates

### Starting the server (with seeding on first run)
```bash
export JAVA_HOME="C:/Program Files/Java/jdk8/jdk8u504-b01"
cd D:/git-projects/flexygames
./gradlew clean bootRun
```

**What happens on first run:**
1. Server starts on `http://localhost:8080`
2. `BootStrap.init()` runs and calls `DeveloperDataSeeder.seed()`
3. Database is populated with:
   - 5 teams, 15+ players, 5 playgrounds
   - 2 game types, 5 session groups, 10 historical sessions
4. Subsequent runs skip seeding (uses `findOrCreate` patterns)

### After first run
- Database is populated with test data
- BootStrap detects existing data and skips seeding
- Access: http://localhost:8080/sessions/list

### Default credentials
- Mario: username `mario`, password `password` (Administrator + Manager)
- Giulia: username `giulia`, password `password` (Administrator)
