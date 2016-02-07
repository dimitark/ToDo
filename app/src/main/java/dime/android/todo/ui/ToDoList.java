//package dime.android.todo.ui;
//
//public class ToDoList extends ActionBarActivity implements OnClickListener, TaskListNewAdapter.ClickResponder, RecyclerViewSwipeToRemove.SwipeListener {
//
//    private ToDo toDoApp;
//    private ActionBar actionBar;
//
//    /* Using the new RecyclerView as a list widget */
//    private RecyclerView recyclerView;
//    private TaskListNewAdapter recyclerViewAdapter;
//    private RecyclerView.LayoutManager recyclerViewLayoutManager;
//
//    /* The floating add button */
//    private ImageButton addButton;
//
//    // The main root view
//    private View rootView;
//
//    /* The empty list view */
//    private View emptyList;
//
//
//    private void openPreferencesActivity() {
//        Intent intent = new Intent(this, Preferences.class);
//        startActivityForResult(intent, 0);
//    }
//
//
//    /**
//     * Called when the activity is first created.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.todo_list);
//
//        // Get the root view
//        rootView = findViewById(R.id.root_view);
//
//        toDoApp = (ToDo) getApplication();
//
//        actionBar = getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
//
//        /* The empty list view */
//        emptyList = findViewById(R.id.empty_list);
//
//        /* Set up the new Recycler view */
//        recyclerView = (RecyclerView) findViewById(R.id.task_list_new);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemAnimator(new ToDoListAnimator());
//
//        /* Set up the recycler view as a simple vertical list */
//        recyclerViewLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(recyclerViewLayoutManager);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
//
//        /* Set up the adapter */
//        recyclerViewAdapter = new TaskListNewAdapter(toDoApp, this);
//        recyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                if (recyclerViewAdapter.getItemCount() == 0) {
//                    emptyList.setVisibility(View.VISIBLE);
//                } else {
//                    emptyList.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//        recyclerView.setAdapter(recyclerViewAdapter);
//        recyclerViewAdapter.notifyDataSetChanged();
//
//        /* Set the default animator */
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        /* Get the add button */
//        addButton = (ImageButton) findViewById(R.id.new_todo);
//        addButton.setOnClickListener(this);
//
//        /* Register out SwipeToRemove touch listener */
//        final RecyclerViewSwipeToRemove swipeToRemove = new RecyclerViewSwipeToRemove(this);
//        recyclerView.addOnItemTouchListener(swipeToRemove);
//        findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                swipeToRemove.recalculateMinDistance(recyclerView.getMeasuredWidth());
//            }
//        });
//    }
//
//
//
//
//
//
//    public void refreshUI() {
//        toDoApp.reloadFromDb();
//        recyclerViewAdapter.notifyDataSetChanged();
//    }
//
//
//
//    private void deleteTask(int position) {
//        Task task = toDoApp.getTaskList().get(position);
//        toDoApp.getDbHelper().deleteTask(task);
//        refreshUI();
//
//        // Show the UNDO snack bar
//        // TODO
//    }
//
//
//    @Override
//    public void swipeCanceled(View v, float deltaX) {
//        if (v == null || v.getTag() == null) return;
//
//        // Get the ViewHolder
//        final TaskListNewAdapter.ViewHolder viewHolder = ((TaskListNewAdapter.ViewHolder)v.getTag());
//
//        viewHolder.foregroundLayer.animate().x(0).setInterpolator(new BounceInterpolator())
//                .withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        viewHolder.foregroundLayer.setX(0);
//                        viewHolder.foregroundLayer.requestLayout();
//                    }
//                }).start();
//    }
//
//    @Override
//    public void swipeDone(View v, float deltaX) {
//        if (v == null || v.getTag() == null || deltaX > 0) return;
//
//        // Get the ViewHolder
//        final TaskListNewAdapter.ViewHolder viewHolder = ((TaskListNewAdapter.ViewHolder)v.getTag());
//
//        viewHolder.foregroundLayer.animate().x(v.getMeasuredWidth()).setInterpolator(new AccelerateInterpolator())
//                .withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        fadeOut(viewHolder);
//                    }
//                }).start();
//    }
//
//    @Override
//    public void swipeInProgress(View v, float deltaX) {
//        if (v == null || v.getTag() == null || deltaX > 0) return;
//
//        // Get the view holder
//        TaskListNewAdapter.ViewHolder viewHolder = ((TaskListNewAdapter.ViewHolder) v.getTag());
//        viewHolder.foregroundLayer.setX(-deltaX);
//        viewHolder.foregroundLayer.requestLayout();
//    }
//
//    private void fadeOut(final TaskListNewAdapter.ViewHolder viewHolder) {
//        viewHolder.itemView.animate().withEndAction(new Runnable() {
//            @Override
//            public void run() {
//                finishRemovingTask(viewHolder);
//            }
//        }).alpha(0);
//    }
//
//    private void finishRemovingTask(final TaskListNewAdapter.ViewHolder viewHolder) {
//        deleteTask(viewHolder.getPosition());
//        toDoApp.reloadFromDb();
//        recyclerViewAdapter.notifyItemRemoved(viewHolder.getPosition());
//
//        // Move everything back in it's place
//        viewHolder.foregroundLayer.setX(0);
//        viewHolder.foregroundLayer.requestLayout();
//        viewHolder.itemView.setAlpha(1f);
//    }
//}