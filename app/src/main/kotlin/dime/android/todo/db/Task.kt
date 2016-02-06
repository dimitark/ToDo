package dime.android.todo.db

/**
 * Represents a single Task
 *
 * Created by dime on 06/02/16.
 */
class Task(var id: Int, var name: String, var priority: Int, var completed: Int) {

    /**
     * Is the task completed?
     */
    fun isCompleted() = completed == 1

    /**
     * Equals
     */
    override fun equals(other: Any?) = if (other is Task) other.id == id else false
}
