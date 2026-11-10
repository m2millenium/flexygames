package flexygames

class Reminder implements Comparable<Reminder> {
	
	Integer minutesBeforeSession
	
	boolean jobExecuted = false
	
	static belongsTo = ['session' : Session]

    static constraints = {
		session()
		minutesBeforeSession(min: 0)
		jobExecuted()
    }
	
	int compareTo(Object o) {
		if (o instanceof Reminder) {
			def thisVal = this.minutesBeforeSession ?: 0
			def otherVal = o.minutesBeforeSession ?: 0
			return thisVal.compareTo(otherVal)
		} else {
			return false
		}
	}
}
