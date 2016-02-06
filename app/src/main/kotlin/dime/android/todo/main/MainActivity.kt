package dime.android.todo.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import dime.android.todo.R
import dime.android.todo.ToDo
import dime.android.todo.logic.TaskListNewAdapter
import dime.android.todo.ui.DividerItemDecoration
import dime.android.todo.ui.ToDoListAnimator
import org.jetbrains.anko.find


/**
 * Created by dime on 05/02/16.
 */
class MainActivity: AppCompatActivity() {

    //
    // region Properties
    //

    private val app by lazy { application as ToDo }
    private val rootView by lazy { find<View>(R.id.root_view) }
    private val emptyList by lazy { find<View>(R.id.empty_list) }
    private val addButton by lazy { find<ImageButton>(R.id.new_todo) }
    private val recyclerView by lazy { find<RecyclerView>(R.id.task_list_new) }

    private lateinit var adapter: TaskListNewAdapter

    //
    // endregion Properties
    //

    //
    // region Activity's lifecycle
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_list)

        // Enable the home button on the action bar
        supportActionBar?.setHomeButtonEnabled(true)

        // Setup the Recycler view
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = ToDoListAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST))

        // Setup the adapter
        adapter = TaskListNewAdapter(app) { /* TODO editTask(it) */ }
    }

    //
    // endregion Activity's lifecycle
    //
}