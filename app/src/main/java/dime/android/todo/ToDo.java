package dime.android.todo;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;

import java.util.List;

import dime.android.todo.db.DatabaseHelper;
import dime.android.todo.logic.Task;
import dime.android.todo.widget.ToDoWidgetService;

public class ToDo extends Application {

    public DatabaseHelper dbHelper;
    public List<Task> taskList;

    private boolean isDataValid;
    private Task taskToEdit;


    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DatabaseHelper(getApplicationContext());

        // TODO: Move this line to different thread (AsyncTask)
        reloadFromDb();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();

        // Close the database connection
        dbHelper.close();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * Reloads the tasks from the database
     */
    public void reloadFromDb() {
        taskList = dbHelper.getAllTasks();
        isDataValid = true;
        updateWidget();
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        isDataValid = true;
        updateWidget();
    }

    public void updateWidget() {
        startService(new Intent(this.getApplicationContext(), ToDoWidgetService.class));
    }


    public void setIsValidData(boolean isDataValid) {
        this.isDataValid = isDataValid;
    }


    public boolean isDataValid() {
        return isDataValid;
    }


    public void setTaskToEdit(Task taskToEdit) {
        this.taskToEdit = taskToEdit;
    }


    public Task getTaskToEdit() {
        return this.taskToEdit;
    }
}
