---
name: grails-domain-patterns
description: Fix Grails/Hibernate lazy init errors in domain methods and views.
version: 1.0.0
author: Hermes Agent
license: MIT
platforms: [linux, macos, windows]
metadata:
  hermes:
    tags: [grails, hibernate, lazy-init, domain-model, groovy]
    related_skills: []
---
# Grails/Hibernate Lazy Initialization Patterns

Use when fixing HTTP 500 errors from null dereference on lazy-loaded associations in Grails domain methods or view templates.

## When to Use

- Error: `Cannot invoke method X on null object` on lazy collection (e.g., `session.rounds`, `team.members`, `player.participations`)
- Error: `Lazy initialization exception` when accessing association outside open session
- Domain method iterates association property directly without eager fetch
- View template accesses nested association without null guard

**Don't use for**: Database seeding, security role setup, or non-lazy domain logic.

## Prerequisites

- Grails 3.5 application with Hibernate ORM
- Domain classes with `belongsTo` and/or lazy associations
- GSP view templates rendering domain objects

## How to Fix

### 1. Rewrite Domain Methods to Use Explicit DB Queries

Replace iteration over lazy collection with explicit query:

```groovy
// BAD - crashes when rounds is null
List<SessionRound> getWins() {
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

### 2. Add Safe Navigation in View Templates

Guard against null collections:

```gsp
<!-- BAD - crashes when participations is null -->
<g:each in="${player.participations}" var="part">
    ...
</g:each>

<!-- GOOD - fallback to empty list -->
<g:each in="${player.participations ?: []}" var="part">
    ...
</g:each>
```

### 3. Eager Fetch in Controller Actions

Load associations before view rendering:

```groovy
def show(Long id) {
    def session = Session.get(id)
    session.rounds = SessionRound.where { session == session }.list()
    [session: session]
}
```

### 4. Add Safe Navigation in Domain Methods

```groovy
// Use safe navigation on lazy associations
def getPlayersForTeamA() {
    members?.findAll { it.role == "Player" && it.teamSide == "A" } ?: []
}
```

## Quick Reference

| Lazy Collection | Safe Pattern |
|----------------|-------------|
| `session.rounds` | `SessionRound.where { session == this.session }.list()` |
| `session.participations` | `Participation.where { session == this.session }.list()` |
| `team.members` | `members?.findAll { ... }` with safe nav |
| `player.participations` | `Participation.where { player == this.player }.list()` |
| `player.sessions` | Query `Participation` then collect sessions |

## Pitfalls

- **Transient properties crash**: `wins`, `defeats`, `draws` are computed, not stored. They access lazy associations at view render time and will fail. Fix by using explicit DB queries.

- **View template null handling**: GSP renders after session closes, so lazy collections are null. Always use `${collection ?: []}` or `${object?.association?.property}`.

- **`player` vs `this.player`**: In domain methods, `player` refers to the parameter/local, not `this.player`. Use `this.player` or add a parameter.

- **`members` is lazy**: Team's `members` association returns null if not loaded. Use `members?.findAll` with safe navigation.

- **`getResult()` and `getResultScore()`**: These methods often access lazy collections. Rewrite to use explicit queries.

## Verification

1. Trigger the error (`curl http://localhost:8080/player/show/2`)
2. Apply fixes to domain methods and view templates
3. Rebuild and restart server: `clean bootRun`
4. Test same endpoint — page renders without HTTP 500 ✓
5. No stack trace in browser or `build/stacktrace.log` ✓
