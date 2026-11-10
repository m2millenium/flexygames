# Query Snippets for Lazy Associations

These are pre-built Hibernate criteria queries for common Grails domain patterns.

## Session Rounds

```groovy
// Get all rounds for a session
def rounds = SessionRound.where { session == this.session }.list()

// Filter rounds by round number
def rounds = SessionRound.where { session == this.session && roundNumber == 1 }.list()
```

## Team Members

```groovy
// Get all members of a team
def members = User.findAllByMembership_Team(team)

// Filter by role in team
def players = User.findAllByMembership_TeamAndMembership_Role(team, "Player")
```

## Player Participations

```groovy
// Get all participations for a player
def participations = Participation.where { player == this.player }.list()

// Filter by session
def participations = Participation.where { player == this.player && session == this.session }.list()
```

## Player Sessions

```groovy
// Get all sessions for a player (via participations)
def sessionIds = Participation.where { player == this.player }.list().collect { it.session.id }
def sessions = Session.where { id in sessionIds }.list()

// Or query through the join table
def sessions = Session.where {
    participations { player == this.player }
}.list()
```

## Participation Wins/Defeats/Draws

```groovy
// Get rounds where player's team won
def rounds = SessionRound.where { 
    session == this.session 
}.list().findAll { round ->
    def winningTeam = round.getResult()
    if (round.playersForTeamA.contains(this.player) && winningTeam == SessionRound.Result.WON_BY_TEAM_A) {
        true
    } else if (round.playersForTeamB.contains(this.player) && winningTeam == SessionRound.Result.WON_BY_TEAM_B) {
        true
    } else {
        false
    }
}
```

## Safe Property Access in Domain Methods

```groovy
// Use safe navigation on potentially null associations
def get effectiveMembers() {
    members?.findAll { it.role == "Player" } ?: []
}
```
