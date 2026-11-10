# Grails/Hibernate Lazy Initialization Pitfalls

## Overview

Grails 3.5 uses Hibernate for ORM with lazy-loaded associations by default. When lazy collections are accessed after the session closes (e.g., in GSP views), they return `null` instead of empty collections, causing `NullPointerException` or `MissingPropertyException`.

## Common Failures

### 1. Direct Collection Access in Domain Methods

```groovy
// BAD - crashes when rounds is null
def getWins() {
    def result = []
    for (SessionRound round : session.rounds) {
        // ...
    }
}

// GOOD - explicit DB query
List<SessionRound> getWins() {
    def result = []
    def rounds = SessionRound.where { session == this.session }.list()
    for (SessionRound round : rounds) {
        // ...
    }
}
```

### 2. Direct Collection Iteration in Views

```gsp
<!-- BAD - crashes when participations is null -->
<g:each in="${player.participations}" var="part">
    <!-- ... -->
</g:each>

<!-- GOOD - safe navigation -->
<g:each in="${player.participations ?: []}" var="part">
    <!-- ... -->
</g:each>
```

### 3. Null Check on Transient Properties

```groovy
// BAD - 'wins' returns null on uninitialized collection
def score = player.participation.wins.size()

// GOOD - check null first
def score = player.participation.wins?.size() ?: 0
```

## The Pattern

| Symptom | Root Cause | Fix |
|---------|-----------|-----|
| `Cannot invoke method X on null object` | Lazy collection is `null` | Use `where { ... }.list()` or `?: []` fallback |
| `Lazy instantiation exception` | Session closed, proxy accessed | Eager fetch in controller or explicit query |
| `No row with given identifier` | Associated entity not loaded | Add `fetch: 'join'` in mapping or use `include` criteria |

## Rules

1. **Domain methods that iterate collections must use explicit DB queries**
   - Replace `for (item : lazyCollection)` with `for (item : Collection.where { ... }.list())`
   - Transient properties like `wins`, `defeats`, `draws` are especially vulnerable

2. **Views must guard against null collections**
   - Use `${collection ?: []}` for iteration
   - Use `${object?.association?.property}` for nested access
   - Add `empty` checks before rendering tables/loops

3. **Controller actions should eager fetch associations needed by views**
   - `SessionRound.where { session == session }.list()` for rounds
   - `Participation.where { player == player }.list()` for participations

## Quick Reference

| Collection | Safe Query |
|------------|-----------|
| `session.rounds` | `SessionRound.where { session == session }.list()` |
| `session.participations` | `Participation.where { session == session }.list()` |
| `team.members` | `User.findAllByMembership_Team(team)` |
| `player.participations` | `Participation.where { player == player }.list()` |
| `player.sessions` | `Session.where { participations.player == player }.list()` |

## Fixes Applied This Session

1. `Participation.groovy`: Rewrote `getWins()`, `getDefeats()`, `getDraws()`, `getResultScore()`, `getActionScore()` to use explicit DB queries instead of iterating `session.rounds`

2. `Team.groovy`: Added safe navigation `members?.findAll { ... }` to `playersForTeamA()` and `playersForTeamB()`

3. `Player.groovy`: Rewrote `sessions` getter to use `Participation.where { player == this }.list()` instead of lazy `participations`

4. View templates: Added `${?.?: []}` fallbacks and conditional null guards
