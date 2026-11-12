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
- **Players**: 17 users with roles and team memberships
- **Game types**: Football, Volleyball (with skills)
- **Playgrounds**: 5 locations (UrbanFoot de Bonneuil, DreamFoot de Meudon, Lycée Turgot, Lycée Paul-Valérie, Stade Jean Bouin)
- **Session groups**: 5 groups with default teams/playgrounds
|- **Historical sessions**: 10 past sessions with participations, rounds, game actions, and votes\n- **Future sessions**: 4 sessions in the next week with participations (REQUESTED status)

### How seeding works
1. `BootStrap.init()` clears all existing data (`User.deleteAll()`, `Team.deleteAll()`, etc.)
2. `DeveloperDataSeeder.seed()` is called to populate the database
3. All data is recreated fresh on each startup

## Running the Server Manually

### Quick Start
```bash
export JAVA_HOME="C:/Program Files/Java/jdk8/jdk8u504-b01"
cd D:/git-projects/flexygames
./gradlew clean bootRun
```

The server starts on `http://localhost:8080`

### Manual Step-by-Step Commands

#### 1. Clean previous build
```bash
cd D:/git-projects/flexygames
./gradlew clean
```

#### 2. Start the Grails server (with seeding on startup)
```bash
./gradlew bootRun
```

#### 3. Verify server is running
```bash
curl http://localhost:8080/
```
Expected: HTTP 200

#### 4. Login to verify (using marioNew admin)
```bash
curl -X POST http://localhost:8080/auth/signIn \
  -d "username=marioNew&password=passwordNew&rememberMe=on" \
  -c /tmp/cookies.txt
```
Expected: HTTP 302 (redirect to home)

#### 5. Check the database was seeded (view logs)
```bash
cat D:/tmp/grails.log | grep -i "user\|team\|seed"
```

#### 6. Stop the server
```bash
# Press Ctrl+C in the terminal running gradlew
# OR kill the process
powershell -Command "Stop-Process -Name java -Force"
```

---

**What happens on every run:**
1. Server starts on `http://localhost:8080`
2. `BootStrap.init()` clears database and calls `DeveloperDataSeeder.seed()`
3. Database is populated with:
   - 5 teams, 17 players, 5 playgrounds
   - 2 game types, 5 session groups, 10 historical sessions
4. All data is recreated fresh on each startup

### Default credentials

| Username | Password | Email | Roles | Team |
|----------|----------|-------|-------|------|
| `mario` | `password` | `mario.biasi@gmail.com` | Administrator, Manager, Player | Team Mario |
| `marioNew` | `passwordNew` | `mario.new@example.com` | Administrator, Manager, Player | All teams |
| `giulia` | `password` | `giulia@example.com` | Administrator, Manager, Player | Team Giulla |
| `luca` | `password` | `luca@example.com` | Player | Team Luca |
| `alessandro` | `password` | `alessandro@example.com` | Player | Team Alessandro |
| `sofia` | `password` | `sofia@example.com` | Player | Team Sofia |
| `martina` | `password` | `martina@example.com` | Player | Team Mario |
| `matteo` | `password` | `matteo@example.com` | Player | Team Giulla |
| `francesca` | `password` | `francesca@example.com` | Player | Team Luca |
| `lukas` | `password` | `lukas@example.com` | Player | Team Alessandro |
| `anna` | `password` | `anna@example.com` | Player | Team Sofia |
| `leo` | `password` | `leo@example.com` | Player | Team Mario |
| `sara` | `password` | `sara@example.com` | Player | Team Giulla |
| `tom` | `password` | `tom@example.com` | Player | Team Luca |
| `emma` | `password` | `emma@example.com` | Player | Team Alessandro |
| `jack` | `password` | `jack@example.com` | Player | Team Sofia |

### Login
- **Endpoint**: `http://localhost:8080/auth/signIn`
- **Parameters**: `username`, `password`, `rememberMe`
- **Content-Type**: `application/x-www-form-urlencoded`

### Admin Login (All Teams Access)
```
Username: marioNew
Password: passwordNew
```

### Team Manager Login
```
Username: mario
Password: password
Team: Team Mario

Username: giulia
Password: password
Team: Team Giulla
```

## Shiro Configuration

**File**: `grails-app/conf/spring/resources.groovy`

```groovy
import flexygames.DeveloperDataSeeder
import flexygames.ShiroWildcardDbRealm
import org.apache.shiro.authc.credential.HashedCredentialsMatcher
import org.apache.shiro.crypto.hash.Sha512Hash
import org.apache.shiro.web.mgt.DefaultWebSecurityManager

beans = {
    credentialsMatcher(HashedCredentialsMatcher) {
        hashAlgorithmName = Sha512Hash.ALGORITHM_NAME
        storedCredentialsHexEncoded = true
    }
    
    shiroRealm(ShiroWildcardDbRealm) {
        credentialMatcher = ref('credentialsMatcher')
    }
    
    shiroSecurityManager(DefaultWebSecurityManager) {
        realms = [ref('shiroRealm')]
    }
    
    databaseSeeder(DeveloperDataSeeder)
}
```

## BootStrap Configuration

**File**: `grails-app/init/flexygames/BootStrap.groovy`

```groovy
class BootStrap {
    def init = { servletContext ->
        try {
            // Clear all existing data
            User.deleteAll()
            Team.deleteAll()
            Role.deleteAll()
            GameType.deleteAll()
            Playground.deleteAll()
            SessionGroup.deleteAll()
            Session.deleteAll()
            
            DeveloperDataSeeder.seed()
        } catch (Exception e) {
            DeveloperDataSeeder.seed()
        }
    }
}
```

## DeveloperDataSeeder Methods

| Method | Description |
|--------|-------------|
| `seed()` | Main entry point - creates all data |
| `createTeams()` | Creates 5 teams |
| `createPlayers()` | Creates 17 users with roles |
| `createGameTypes()` | Creates Football and Volleyball |
| `createPlaygrounds()` | Creates 5 playgrounds |
| `createSessionGroups()` | Creates 5 session groups |
| `createHistoricalSessions()` | Creates 10 past sessions + 4 future sessions with data |
| `createParticipationsForSession()` | Adds players to sessions |
| `createGameAction()` | Adds goals, assists, etc. |
| `createVote()` | Adds player votes |
| `calculateUserCounters()` | Updates participation statistics |

## Password Hashing
Passwords are hashed using SHA-512 with hex encoding:
```groovy
new Sha512Hash(password).toHex()
```

## Notes
- The database is cleared and recreated on every application startup
- All users are automatically added to their respective teams
- `marioNew` is manager of ALL teams (not just one)
- `mario` is manager of Team Mario
- Historical sessions span the last 10 days; future sessions are created for the next week
- Each session has 2-3 rounds with teams A and B
- Player performance is tracked with votes and status codes (DONE_GOOD, DONE_LATE, DONE_BAD, AVAILABLE, REQUESTED)
