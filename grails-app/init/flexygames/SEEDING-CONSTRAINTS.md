# Data Seeding Constraints for FlexyGames

## Overview

FlexyGames has business constraints that must be enforced in the seeder:

1. **Each player can be in at most one session per day** — A player cannot participate in multiple sessions on the same calendar date.

2. **Session participation status codes** — Each session has participations with status codes like `REQUESTED`, `AVAILABLE`, `DONE_GOOD`, `DONE_LATE`, `DONE_BAD`.

3. **`playerLast6Days` calculation** — Should only compute for sessions on or after today's date (future sessions can have participations counted; past sessions use history).

## When to Use This Reference

Use when:
- Updating `DeveloperDataSeeder.groovy` to add new business rules
- Debugging seeder output to verify constraints are enforced
- Adding new session types or testing edge cases

**Do not use for:**
- Runtime participation logic (use domain methods)
- UI display logic (use GSP templates)

## Key Rules

### 1. One Session Per Player Per Day

**In `DeveloperDataSeeder.groovy`, `createParticipationsForSession` must:**

```groovy
// Get players who already participated in a session on this date
def sessionDate = session.date.clearTime()
def playersInSessionToday = allSessions.findAll { s ->
    s.date.clearTime() == sessionDate && s.id != session.id
}.collectMany { s ->
    s.participations.collect { p -> p.player }
}.toSet()

def playersList = players.values().toList()
// Filter out players who already have a session today
def availablePlayers = playersList.findAll { !playersInSessionToday.contains(it) }
def numParticipants = 8 + random.nextInt(5)  // 8-12 participants
numParticipants = Math.min(numParticipants, availablePlayers.size())

// Shuffle and pick participants from available players
playersList = availablePlayers.sort { random.nextFloat() }.take(numParticipants)
```

**Pitfall:** The seeder passes `sessions` (all sessions created so far) to each call of `createParticipationsForSession`. This allows the method to check for date conflicts across all sessions, not just the current one.

### 2. `playerLast6Days` Computation Condition

**In `Player.groovy` domain method or GSP:**

```groovy
// GSP template (e.g., _participantsList.gsp)
<g:set var="today" value="${new Date().clearTime()}"/>
<g:set var="sessionDateClear" value="${sessionInstance.date.clearTime()}"/>
<g:if test="${sessionDateClear >= today}">
    <g:set var="playerLast6Days" value="${p.player.participationsInLast6Days(sessionInstance.date)}"/>
    <!-- display playerLast6Days -->
</g:if>
```

**In `Player.groovy` domain method:**

```groovy
int participationsInLast6Days(Date sessionDate) {
    // Normalize dates to midnight
    Date windowStart = (sessionDate - 6).clearTime()
    Date sessionDateNormalized = sessionDate.clearTime()
    
    int count = 0
    participations.each { p ->
        Date pSessionDate = p.session.date.clearTime()
        if (pSessionDate >= windowStart && pSessionDate < sessionDateNormalized) {
            // Only count non-AVAILABLE statuses
            if (p.statusCode in ['PRESENT', 'GATE_CRASHER', 'LATE', 'ABSENT']) {
                count++
            }
        }
    }
    return count
}
```

**Pitfall:** The 6-day window is `[sessionDate - 6, sessionDate)` — inclusive start, exclusive end. This ensures sessions exactly 6 days ago are counted but sessions from 7 days ago are not.

### 3. Seeding Pattern

**For historical sessions (last 10 days):**
- Sessions created with dates `new Date() - 1` through `new Date() - 10`
- Each session gets 8-12 participants (randomly selected)
- Past participations get random status: `DONE_GOOD` (50%), `DONE_LATE` (20%), `DONE_BAD` (15%), `AVAILABLE` (10%), `REQUESTED` (5%)

**For future sessions (next week):**
- Sessions created with dates `new Date() + 1` through `new Date() + 4`
- Each session gets 8-12 participants (randomly selected, respecting "one per day" rule)
- Future participations always get `REQUESTED` status

## Quick Reference

| Constraint | Implementation | Location |
|------------|----------------|----------|
| One session per player per day | Filter `availablePlayers` by `playersInSessionToday` | `DeveloperDataSeeder.groovy` |
| `playerLast6Days` only for future/today sessions | `if (sessionDateClear >= today)` check | GSP template |
| 6-day window calculation | `windowStart = sessionDate - 6` (clearTime) | `Player.groovy` domain method |
| Status codes for past sessions | Random distribution (DONE_GOOD 50%, etc.) | `DeveloperDataSeeder.groovy` |
| Status codes for future sessions | Always `REQUESTED` | `DeveloperDataSeeder.groovy` |

## See Also

- `grails-debugging` — General Grails debugging including GSP templates
- `references/calculation-bugs.md` — Debugging domain method calculation bugs