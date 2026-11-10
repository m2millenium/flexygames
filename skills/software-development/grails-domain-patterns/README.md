# Domain Method Pattern Library

This directory contains reusable patterns for Grails domain classes that access lazy-loaded associations.

## Files

- `lazy-init.md` - Overview of lazy initialization pitfalls and fixes
- `query-snippets.groovy` - Pre-built query patterns for common associations

## Usage

When writing a domain method that iterates an association:

1. Check `lazy-init.md` for the pattern
2. Copy the relevant query snippet from `query-snippets.groovy`
3. Adapt to your specific domain class and association

## Snippet Index

- Session rounds: `SessionRound.where { session == this.session }.list()`
- Team members: `User.findAllByMembership_Team(team)`
- Player participations: `Participation.where { player == this.player }.list()`
- Player sessions: `Session.where { participations.player == this.player }.list()`
