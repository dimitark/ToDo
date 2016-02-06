package dime.android.todo.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Created by dime on 06/02/16.
 */

class DBHelper(context: Context): ManagedSQLiteOpenHelper(context, "todo.db", null, 1) {

    // The table columns
    private val table = "todo"
    private val id = "id"
    private val name = "name"
    private val priority = "priority"
    private val completed = "completed"
    
    // Out custom parser
    private val parser = rowParser { id: Int, name: String, priority: Int, completed: Int -> Task(id, name, priority, completed) }

    /**
     * Singleton pattern
     */
    companion object {
        // The single instance
        private var instance: DBHelper? = null

        // The lock object
        private val lock = Object()

        // Returns the single instance of this object
        fun getInstance(ctx: Context) = synchronized(lock) {
            if (instance == null) instance = DBHelper(ctx.applicationContext)
            return instance
        }
     }

    /**
     * Called on database creating. Here the tables are created
     */
    override fun onCreate(db: SQLiteDatabase) {
        use {
            createTable(table, true,
                    id to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                    name to TEXT,
                    priority to INTEGER,
                    completed to INTEGER)
        }
    }

    /**
     * Called on database upgrade.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    //
    // region Helper functions
    //

    /**
     * Adds the given task to the database.
     *
     * @param task      -> The task that needs to be added to the database
     * @return  Boolean -> True if the adding was successful
     */
    fun addTask(task: Task) = use { insert(table, name to task.name, priority to task.priority, completed to task.completed) } > -1

    /**
     * Deletes the given task.
     *
     * @param task      -> The task that need to be removed from the database
     * @return Boolean  -> True if the removal was successful
     */
    fun deleteTask(task: Task) = use { delete(table, "$id = {id}", "id" to task.id) } > -1

    /**
     * Deletes all the completed tasks
     *
     * @return  -> The number of deleted tasks
     */
    fun deleteCompleted() = use { delete(table, "$completed = 1") }

    /**
     * Updates the given task.
     *
     * @param task      -> The task that needs to be updated
     * @return Boolean  -> True if the task was successfully updated
     */
    fun updateTask(task: Task) =
            use {
                update(table, name to task.name, priority to task.priority, completed to task.completed)
                        .where("$id = {id}", "id" to task.id)
                        .exec()
            } == 1

    /**
     * Returns a list of all tasks
     */
    fun allTasks() = use { select(table).orderBy(completed).orderBy(priority, SqlOrderDirection.DESC).exec { parseList(parser) } }

    /**
     * Returns a list of all uncompleted tasks
     */
    fun uncompletedTask() = use { select(table).where("$completed = 0").orderBy(priority, SqlOrderDirection.DESC).exec { parseList(parser) } }

    //
    // endregion Helper functions
    //
}

// Access property for Context
val Context.database: DBHelper
    get() = DBHelper.getInstance(applicationContext)
