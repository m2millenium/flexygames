# Grails Debugging Quick Reference

## Fixing Lazy Initialization

**Problem:** `Cannot get property 'X' on null object` from domain methods.

**Fix:** Replace lazy collection access with explicit DB query:

```groovy
// BEFORE (causes LazyInitializationException)
def rounds = session.rounds
rounds.each { ... }

// AFTER (eager load)
def rounds = SessionRound.where { session == this.session }.list()
rounds.each { ... }
```

**Apply to:** `getWins()`, `getDefeats()`, `getDraws()`, `getResultScore()`, `getActionScore()`

---

## Fixing GSP Template Issues

### Missing Model Values

When rendering a template, explicitly pass required model values:

```gsp
<!-- BEFORE (sessionInstance not available in template) -->
<g:render template="/common/status" model="['part': p]"/>

<!-- AFTER (pass sessionInstance explicitly) -->
<g:render template="/common/status" model="['part': p, 'sessionInstance': sessionInstance]"/>
```

### i18n Key Mismatch

Check `messages.properties` for exact key names:

```
# BAD - will show &nbsp; (no text)
<i18n key="participation.status.GATE_CRASHER">Gate Crasher</i18n>

# GOOD - matches part.statusCode exactly
participation.status.GATECRASHER=Gate Crasher
```

### Syntax Errors in GSP Tags

Watch for hidden characters:

```groovy
<!-- BAD - extra space before APPROVED.code -->
<g:if test="${status == flexygames.Participation.Status. APPROVED.code}">

<!-- GOOD - no extra spaces -->
<g:if test="${status == flexygames.Participation.Status.APPROVED.code}">
```

---

## Restarting Grails Server

After any domain/changing changes, always restart with clean:

```bash
grails clean bootRun
```

Stale bytecode in `build/` causes old code to run.

---

## Debugging GSP Values

Add temporary debug output to see values:

```gsp
<!-- In GSP template -->
Status: ${part?.statusCode}<br/>

<!-- Or check null -->
Participation: ${part != null ? 'present' : 'null'}<br/>
```

---

## Quick Fix Checklist

| Symptom | Likely Cause | Fix |
|---------|-------------|-----|
| `Cannot get property 'X' on null object` | Lazy collection in domain | Use `.where().list()` |
| `msg()` returns empty | i18n key mismatch | Check `messages.properties` |
| `&nbsp;` in cell | Missing status value | Check `part.statusCode` i18n key |
| `null` dereference | Uninitialized collection | Eager load with `.where()` |
| Template model null | Missing model in render | Pass explicitly in `model=[]` |
