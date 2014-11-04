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

public class ToDoList extends ActionBarActivity
        implements OnClickListener, TaskListNewAdapter.ClickResponder, RecyclerViewSwipeToRemove.SwipeListener, ValueAnimator.AnimatorListener {

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

    /* The animators used to animated the swipeCanceled & swipeDone events */
    private ValueAnimator cancelLeftMarginAnimator = ValueAnimator.ofInt(0, 0);
    private ValueAnimator cancelRightMarginAnimator = ValueAnimator.ofInt(0, 0);
    private ValueAnimator doneLeftMarginAnimator = ValueAnimator.ofInt(0, 0);
    private ValueAnimator doneRightMarginAnimator = ValueAnimator.ofInt(0, 0);
    private ViewGroup.MarginLayoutParams animTargetParams;
    private View animTargetView;



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
        recyclerViewAdapter = new TaskListNewAdapter(toDoApp, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        /* Set the default animator */
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /* Get the add button */
        addButton = (ImageButton) findViewById(R.id.new_todo);
        addButton.setOnClickListener(this);

        /* Register out SwipeToRemove touch listener */
        recyclerView.addOnItemTouchListener(new RecyclerViewSwipeToRemove(this));

        /* Init the animations */
        cancelLeftMarginAnimator.setInterpolator(new BounceInterpolator());
        cancelRightMarginAnimator.setInterpolator(new BounceInterpolator());
        doneLeftMarginAnimator.setInterpolator(new AccelerateInterpolator());
        doneRightMarginAnimator.setInterpolator(new AccelerateInterpolator());
        cancelLeftMarginAnimator.setDuration(ANIMATIONS_DURATION);
        cancelRightMarginAnimator.setDuration(ANIMATIONS_DURATION);
        doneLeftMarginAnimator.setDuration(250);
        doneRightMarginAnimator.setDuration(250);
        cancelLeftMarginAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animTargetParams.setMargins((Integer) cancelLeftMarginAnimator.getAnimatedValue(), animTargetParams.topMargin,
                        (Integer) cancelRightMarginAnimator.getAnimatedValue(), animTargetParams.bottomMargin);
                animTargetView.requestLayout();
            }
        }); // It's enough just one animator (of each type) to register an update listener
        doneLeftMarginAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animTargetParams.setMargins((Integer) doneLeftMarginAnimator.getAnimatedValue(), animTargetParams.topMargin,
                        (Integer) doneRightMarginAnimator.getAnimatedValue(), animTargetParams.bottomMargin);
                animTargetView.requestLayout();

                Log.d("Anim", doneLeftMarginAnimator.getAnimatedValue() + ", " + doneRightMarginAnimator.getAnimatedValue());
            }
        });
        cancelLeftMarginAnimator.addListener(this);
        doneLeftMarginAnimator.addListener(this);
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

        //finishPrevAnimations();

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) ((TaskListNewAdapter.ViewHolder)v.getTag()).foregroundLayer.getLayoutParams();
        animTargetParams = params;
        animTargetView = v;
        cancelLeftMarginAnimator.setIntValues(params.leftMargin, 0);
        cancelRightMarginAnimator.setIntValues(params.rightMargin, 0);
        cancelLeftMarginAnimator.start();
        cancelRightMarginAnimator.start();
    }

    @Override
    public void swipeDone(View v, float deltaX) {
        if (v == null || v.getTag() == null || deltaX > 0) return;

        TaskListNewAdapter.ViewHolder vh = (TaskListNewAdapter.ViewHolder) v.getTag();

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) vh.foregroundLayer.getLayoutParams();
        animTargetParams = params;
        animTargetView = v;
        doneLeftMarginAnimator.setIntValues(params.leftMargin, animTargetView.getMeasuredWidth());
        doneRightMarginAnimator.setIntValues(params.rightMargin, -animTargetView.getMeasuredWidth());

        doneLeftMarginAnimator.start();
        doneRightMarginAnimator.start();
    }

    @Override
    public void swipeInProgress(View v, float deltaX) {
        if (v == null || v.getTag() == null || deltaX > 0) return;

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) ((TaskListNewAdapter.ViewHolder)v.getTag()).foregroundLayer.getLayoutParams();
        params.setMargins((int) -deltaX, params.topMargin, (int) deltaX, params.bottomMargin);
        v.requestLayout();
    }

    private void finishPrevAnimations() {
        cancelLeftMarginAnimator.cancel();
        cancelRightMarginAnimator.cancel();
    }

    private void finishRemovingTask() {
        TaskListNewAdapter.ViewHolder vh = (TaskListNewAdapter.ViewHolder) animTargetView.getTag();
        deleteTask(vh.position);
        toDoApp.reloadFromDb();
        recyclerViewAdapter.notifyItemRemoved(vh.position);
        animTargetParams.setMargins(0, animTargetParams.topMargin, 0, animTargetParams.bottomMargin);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (animation == doneLeftMarginAnimator) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishRemovingTask();
                }
            }, 500);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if (animation == doneLeftMarginAnimator) {
            finishRemovingTask();
        } else {
            animTargetParams.setMargins(0, animTargetParams.topMargin, 0, animTargetParams.bottomMargin);
        }
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}