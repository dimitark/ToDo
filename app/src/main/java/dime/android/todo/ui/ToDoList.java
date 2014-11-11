package dime.android.todo.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ListView;

import dime.android.todo.R;
import dime.android.todo.ToDo;
import dime.android.todo.logic.Task;
import dime.android.todo.logic.TaskListNewAdapter;

public class ToDoList extends ActionBarActivity implements OnClickListener, TaskListNewAdapter.ClickResponder, RecyclerViewSwipeToRemove.SwipeListener {

    private static final int ANIMATIONS_DURATION = 500;
    private ToDo toDoApp;
    private ListView taskList;

    private ActionBar actionBar;

    /* Using the new RecyclerView as a list widget */
    private RecyclerView recyclerView;
    private TaskListNewAdapter recyclerViewAdapter;
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
        recyclerView.setItemAnimator(new ToDoListAnimator());

        /* Set up the recycler view as a simple vertical list */
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        /* Set up the adapter */
        recyclerViewAdapter = new TaskListNewAdapter(toDoApp, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        /* Set the default animator */
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /* Get the add button */
        addButton = (ImageButton) findViewById(R.id.new_todo);
        addButton.setOnClickListener(this);

        /* Register out SwipeToRemove touch listener */
        recyclerView.addOnItemTouchListener(new RecyclerViewSwipeToRemove(this));
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


    @Override
    public void onClick(int position) {
        /* On click - open the task in edit mode */
        editTask(position);
    }

    @Override
    public void swipeCanceled(View v, float deltaX) {
        if (v == null || v.getTag() == null || deltaX > 0) return;

        // Get the ViewHolder
        final TaskListNewAdapter.ViewHolder viewHolder = ((TaskListNewAdapter.ViewHolder)v.getTag());

        viewHolder.foregroundLayer.animate().x(0).setInterpolator(new BounceInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.foregroundLayer.setX(0);
                        viewHolder.foregroundLayer.requestLayout();
                    }
                }).start();
    }

    @Override
    public void swipeDone(View v, float deltaX) {
        if (v == null || v.getTag() == null || deltaX > 0) return;

        // Get the ViewHolder
        final TaskListNewAdapter.ViewHolder viewHolder = ((TaskListNewAdapter.ViewHolder)v.getTag());

        viewHolder.foregroundLayer.animate().x(v.getMeasuredWidth()).setInterpolator(new AccelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        finishRemovingTask(viewHolder);
                    }
                }).start();
    }

    @Override
    public void swipeInProgress(View v, float deltaX) {
        if (v == null || v.getTag() == null || deltaX > 0) return;

        // Get the view holder
        TaskListNewAdapter.ViewHolder viewHolder = ((TaskListNewAdapter.ViewHolder) v.getTag());
        viewHolder.foregroundLayer.setX(-deltaX);
        viewHolder.foregroundLayer.requestLayout();
    }

    private void finishRemovingTask(final TaskListNewAdapter.ViewHolder viewHolder) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                deleteTask(viewHolder.position);
                toDoApp.reloadFromDb();
                recyclerViewAdapter.notifyItemRemoved(viewHolder.position);

                // Move everything back in it's place
                viewHolder.foregroundLayer.setX(0);
                viewHolder.foregroundLayer.requestLayout();
            }
        }, 500);
    }
}