package flexygames

class TaskType implements Comparable<TaskType> {

    String code

    static constraints = {
        code unique: true
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    int compareTo(TaskType o) {
        String thisCode = this.code ?: ''
        String otherCode = o.code ?: ''
        return thisCode.compareTo(otherCode)
    }

}
