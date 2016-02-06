package dime.android.todo

import android.app.Application
import android.content.Intent
import dime.android.todo.db.DatabaseHelper
import dime.android.todo.logic.Task
import dime.android.todo.widget.ToDoWidgetService
import kotlin.properties.Delegates

class ToDo : Application() {

    // The database helper
    lateinit var dbHelper: DatabaseHelper

    // The task list -- TODO must be moved from here
    var taskList: List<Task> by Delegates.observable(listOf()) {
        property, old, new ->
            isDataValid = true
            updateWidget()
    }

    // Is the data valid? -- TODO must be moved from here
    var isDataValid: Boolean = false
        private set

    // The task that needs to be edited -- TODO must be moved from here!
    var taskToEdit: Task? = null


    override fun onCreate() {
        super.onCreate()
        dbHelper = DatabaseHelper(applicationContext)

        // TODO: Move this line to different thread (AsyncTask)
        reloadFromDb()
    }


    override fun onTerminate() {
        super.onTerminate()

        // Close the database connection
        dbHelper.close()
    }


    /**
     * Reloads the tasks from the database
     */
    fun reloadFromDb() {
        taskList = dbHelper.allTasks
        isDataValid = true
        updateWidget()
    }


    fun updateWidget() {
        startService(Intent(this.applicationContext, ToDoWidgetService::class.java))
    }


    fun setIsValidData(isDataValid: Boolean) {
        this.isDataValid = isDataValid
    }
}
