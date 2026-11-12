package flexygames

import grails.testing.domain.DomainUnitTest
import spock.lang.Specification

class UserSpec extends Specification implements DomainUnitTest<User> {

    void "participationsInLast6Days returns count of participations in 6 days before session date"() {
        given:
        // Create a user
        def user = new User(
            username: "testuser",
            firstName: "Test",
            lastName: "User",
            email: "test@example.com",
            passwordHash: "password123",
            registrationDate: new Date()
        ).save(failOnError: true)

        // Create a session group
        def group = new SessionGroup(
            name: "Test Group",
            defaultMinPlayerNbr: 4,
            defaultPreferredPlayerNbr: 6,
            defaultMaxPlayerNbr: 10,
            enabled: true
        ).save(failOnError: true)

        // Session date: Nov 13, 2026 (reference date)
        def sessionDate = new Date(2026 - 1900, 10, 13) // Nov 13, 2026

        // Session 1: Nov 12 (within window, should be counted)
        def session1 = new Session(
            group: group,
            startDate: new Date(2026 - 1900, 10, 12),
            enabled: true,
            visible: true,
            info: "Session 1"
        ).save(failOnError: true)

        // Session 2: Nov 8 (within window, should be counted)
        def session2 = new Session(
            group: group,
            startDate: new Date(2026 - 1900, 10, 8),
            enabled: true,
            visible: true,
            info: "Session 2"
        ).save(failOnError: true)

        // Session 3: Nov 7 (within window, should be counted - exactly 6 days before)
        def session3 = new Session(
            group: group,
            startDate: new Date(2026 - 1900, 10, 7),
            enabled: true,
            visible: true,
            info: "Session 3"
        ).save(failOnError: true)

        // Session 4: Nov 6 (outside window, should NOT be counted - 7 days before)
        def session4 = new Session(
            group: group,
            startDate: new Date(2026 - 1900, 10, 6),
            enabled: true,
            visible: true,
            info: "Session 4"
        ).save(failOnError: true)

        // Session 5: Nov 14 (in the future, should NOT be counted - after session date)
        def session5 = new Session(
            group: group,
            startDate: new Date(2026 - 1900, 10, 14),
            enabled: true,
            visible: true,
            info: "Session 5"
        ).save(failOnError: true)

        // Create participations with different statuses
        // All these should be counted (valid statuses)
        new Participation(
            session: session1,
            player: user,
            status: Participation.Status.findByCode("APPROVED")
        ).save(failOnError: true)

        new Participation(
            session: session2,
            player: user,
            status: Participation.Status.findByCode("GATE_CRASH")
        ).save(failOnError: true)

        new Participation(
            session: session3,
            player: user,
            status: Participation.Status.findByCode("PRESENT")
        ).save(failOnError: true)

        // This participation in session4 (Nov 6) is outside the window
        new Participation(
            session: session4,
            player: user,
            status: Participation.Status.findByCode("APPROVED")
        ).save(failOnError: true)

        // This participation in session5 (Nov 14) is after session date
        new Participation(
            session: session5,
            player: user,
            status: Participation.Status.findByCode("APPROVED")
        ).save(failOnError: true)

        when:
        // Window is [Nov 7, Nov 13) - i.e., Nov 7, 8, 9, 10, 11, 12
        // Participations in session1 (Nov 12), session2 (Nov 8), session3 (Nov 7) should be counted = 3
        def result = user.participationsInLast6Days(sessionDate)

        then:
        result == 3
    }

    void "participationsInLast6Days excludes the current session"() {
        given:
        def user = new User(
            username: "testuser2",
            firstName: "Test",
            lastName: "User2",
            email: "test2@example.com",
            passwordHash: "password123",
            registrationDate: new Date()
        ).save(failOnError: true)

        def group = new SessionGroup(
            name: "Test Group 2",
            defaultMinPlayerNbr: 4,
            defaultPreferredPlayerNbr: 6,
            defaultMaxPlayerNbr: 10,
            enabled: true
        ).save(failOnError: true)

        // Session date: Nov 13, 2026
        def sessionDate = new Date(2026 - 1900, 10, 13)

        // Current session - should NOT be counted (it's the reference, excluded)
        def currentSession = new Session(
            group: group,
            startDate: sessionDate,
            enabled: true,
            visible: true,
            info: "Current session"
        ).save(failOnError: true)

        new Participation(
            session: currentSession,
            player: user,
            status: Participation.Status.findByCode("APPROVED")
        ).save(failOnError: true)

        when:
        def result = user.participationsInLast6Days(sessionDate)

        then:
        result == 0 // Current session should be excluded
    }

    void "participationsInLast6Days handles all valid status codes"() {
        given:
        def user = new User(
            username: "testuser3",
            firstName: "Test",
            lastName: "User3",
            email: "test3@example.com",
            passwordHash: "password123",
            registrationDate: new Date()
        ).save(failOnError: true)

        def group = new SessionGroup(
            name: "Test Group 3",
            defaultMinPlayerNbr: 4,
            defaultPreferredPlayerNbr: 6,
            defaultMaxPlayerNbr: 10,
            enabled: true
        ).save(failOnError: true)

        def sessionDate = new Date(2026 - 1900, 10, 13)

        // Create one session in the window
        def session = new Session(
            group: group,
            startDate: new Date(2026 - 1900, 10, 10),
            enabled: true,
            visible: true,
            info: "Test session"
        ).save(failOnError: true)

        when:
        // Test all valid statuses (excluding UNDONE)
        def validStatuses = [
            "REQUESTED",
            "AVAILABLE",
            "DECLINED",
            "APPROVED",
            "APPROVED_EXTRA",
            "WAITING_LIST",
            "REMOVED",
            "DONE_GOOD",
            "DONE_LATE",
            "DONE_BAD",
            "PRESENT",
            "GATE_CRASH",
            "ABSENT"
        ]

        def count = 0
        validStatuses.each { code ->
            new Participation(
                session: session,
                player: user,
                status: Participation.Status.findByCode(code)
            ).save(failOnError: true)
            count++
        }

        def result = user.participationsInLast6Days(sessionDate)

        then:
        result == count // All should be counted
    }

    void "participationsInLast6Days returns 0 when user has no participations"() {
        given:
        def user = new User(
            username: "testuser4",
            firstName: "Test",
            lastName: "User4",
            email: "test4@example.com",
            passwordHash: "password123",
            registrationDate: new Date()
        ).save(failOnError: true)

        def sessionDate = new Date(2026 - 1900, 10, 13)

        when:
        def result = user.participationsInLast6Days(sessionDate)

        then:
        result == 0
    }

    void "participationsInLast6Days returns 0 when sessionDate is null"() {
        given:
        def user = new User(
            username: "testuser5",
            firstName: "Test",
            lastName: "User5",
            email: "test5@example.com",
            passwordHash: "password123",
            registrationDate: new Date()
        ).save(failOnError: true)

        when:
        def result = user.participationsInLast6Days(null)

        then:
        result == 0
    }

    void "participationsInLast6Days handles month rollover correctly"() {
        given:
        def user = new User(
            username: "testuser6",
            firstName: "Test",
            lastName: "User6",
            email: "test6@example.com",
            passwordHash: "password123",
            registrationDate: new Date()
        ).save(failOnError: true)

        def group = new SessionGroup(
            name: "Test Group 6",
            defaultMinPlayerNbr: 4,
            defaultPreferredPlayerNbr: 6,
            defaultMaxPlayerNbr: 10,
            enabled: true
        ).save(failOnError: true)

        // Session date: March 5, 2027 (crosses from March into previous month)
        def sessionDate = new Date(2027 - 1900, 2, 5) // March 5, 2027

        // Sessions in the window: Feb 27, 28, Mar 1, 2, 3, 4 (6 days before)
        def session1 = new Session(
            group: group,
            startDate: new Date(2027 - 1900, 1, 27), // Feb 27
            enabled: true,
            visible: true,
            info: "Session 1"
        ).save(failOnError: true)

        def session2 = new Session(
            group: group,
            startDate: new Date(2027 - 1900, 1, 28), // Feb 28
            enabled: true,
            visible: true,
            info: "Session 2"
        ).save(failOnError: true)

        def session3 = new Session(
            group: group,
            startDate: new Date(2027 - 1900, 2, 1), // Mar 1
            enabled: true,
            visible: true,
            info: "Session 3"
        ).save(failOnError: true)

        def session4 = new Session(
            group: group,
            startDate: new Date(2027 - 1900, 2, 2), // Mar 2
            enabled: true,
            visible: true,
            info: "Session 4"
        ).save(failOnError: true)

        def session5 = new Session(
            group: group,
            startDate: new Date(2027 - 1900, 2, 3), // Mar 3
            enabled: true,
            visible: true,
            info: "Session 5"
        ).save(failOnError: true)

        def session6 = new Session(
            group: group,
            startDate: new Date(2027 - 1900, 2, 4), // Mar 4
            enabled: true,
            visible: true,
            info: "Session 6"
        ).save(failOnError: true)

        // Outside window: Feb 26 (7 days before)
        def sessionOutside = new Session(
            group: group,
            startDate: new Date(2027 - 1900, 1, 26), // Feb 26
            enabled: true,
            visible: true,
            info: "Outside window"
        ).save(failOnError: true)

        // Create participations
        [session1, session2, session3, session4, session5, session6].each { s ->
            new Participation(
                session: s,
                player: user,
                status: Participation.Status.findByCode("APPROVED")
            ).save(failOnError: true)
        }

        new Participation(
            session: sessionOutside,
            player: user,
            status: Participation.Status.findByCode("APPROVED")
        ).save(failOnError: true)

        when:
        def result = user.participationsInLast6Days(sessionDate)

        then:
        result == 6 // Only the 6 sessions within the window
    }
}
