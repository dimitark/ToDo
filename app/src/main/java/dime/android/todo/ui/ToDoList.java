package dime.android.todo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import dime.android.todo.R;
import dime.android.todo.ToDo;
import dime.android.todo.logic.Task;
import dime.android.todo.logic.TaskListNewAdapter;

public class ToDoList extends ActionBarActivity implements OnClickListener, OnItemClickListener,
        SwipeDismissListViewTouchListener.DismissCallbacks{
    private ToDo toDoApp;
    private ListView taskList;
    private SwipeDetector taskListSwipeDetector = new SwipeDetector();

    private ActionBar actionBar;

    /* Using the new RecyclerView as a list widget */
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    /* The floating add button */
    private ImageButton addButton;


    private void removeCompleted() {
        toDoApp.dbHelper.deleteCompleted();
        refreshUI();
    }

    private void openPreferencesActivity() {
        Intent intent = new Intent(this, Preferences.class);
        startActivityForResult(intent, 0);
    }


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        toDoApp = (ToDo) getApplication();

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        /* Set up the new Recycler view */
        recyclerView = (RecyclerView) findViewById(R.id.task_list_new);
        recyclerView.setHasFixedSize(true);

        /* Set up the recycler view as a simple vertical list */
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        /* Set up the adapter */
        recyclerViewAdapter = new TaskListNewAdapter(toDoApp);
        recyclerView.setAdapter(recyclerViewAdapter);

        /* Set the default animator */
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /* Get the add button */
        addButton = (ImageButton) findViewById(R.id.new_todo);
        addButton.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!toDoApp.isDataValid()) {
            toDoApp.reloadFromDb();
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        toDoApp.dbHelper.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        toDoApp.dbHelper.close();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        toDoApp.dbHelper.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                openPreferencesActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void refreshUI() {
        toDoApp.reloadFromDb();
        recyclerViewAdapter.notifyDataSetChanged();
    }


    public void showNewEditTodo() {
        Intent myIntent = new Intent(this, NewEditToDo.class);
        startActivityForResult(myIntent, 0);
    }


    private void deleteTask(int position) {
        Task task = toDoApp.taskList.get(position);
        toDoApp.dbHelper.deleteTask(task);
        refreshUI();
    }


    private void editTask(int position) {
        Task task = toDoApp.taskList.get(position);
        toDoApp.setTaskToEdit(task);
        showNewEditTodo();
    }


    private void toggleCompleted(Task task) {
        task.setCompleted(!task.isCompleted());
        toDoApp.dbHelper.updateTask(task);
        refreshUI();
    }


    public void onClick(View v) {
        if (v == addButton) {
            toDoApp.setTaskToEdit(null);
            showNewEditTodo();
        }
    }


    public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
        /* Check if the user swiped */
        if (taskListSwipeDetector.swipeDetected()) {
            if (taskListSwipeDetector.getAction() == SwipeDetector.Action.LR ||
                    taskListSwipeDetector.getAction() == SwipeDetector.Action.RL) {
                // Item removed
                deleteTask(position);
            }
        } else {
            /* On click - open the task in edit mode */
            editTask(position);
        }
    }

    @Override
    public boolean canDismiss(int position) {
        return true;
    }

    @Override
    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            deleteTask(position);
        }
        recyclerViewAdapter.notifyDataSetChanged();
    }
}