package dime.android.todo.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import dime.android.todo.App
import dime.android.todo.R
import dime.android.todo.edit.EditActivity
import dime.android.todo.extensions.consume
import dime.android.todo.extensions.snack
import dime.android.todo.preferences.Preferences
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivityForResult


/**
 * Created by dime on 05/02/16.
 */
class MainActivity: AppCompatActivity() {

    //
    // region Properties
    //

    private val app by lazy { application as App }
    private val rootView by lazy { find<ViewGroup>(R.id.root_view) }
    private val emptyList by lazy { find<View>(R.id.empty_list) }
    private val addButton by lazy { find<ImageButton>(R.id.new_todo) }
    private val recyclerView by lazy { find<RecyclerView>(R.id.task_list_new) }

    private val adapter by lazy { TaskListAdapter(this) }

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

        // Setup the adapter
        adapter.errorDelegate = { rootView.snack(it) }
        adapter.taskClickListener = { startActivityForResult<EditActivity>(EditActivity.REQUEST_CODE, EditActivity.EXTRA_TASK_ID to it) }
        adapter.dataChangedListener = {
            emptyList.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.INVISIBLE
            app.updateWidget()
        }
        adapter.refreshDataFromDB()

        // Setup the Recycler view
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = ToDoListAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST))
        recyclerView.adapter = adapter

        // Setup the other UI components
        addButton.setOnClickListener { startActivityForResult<EditActivity>(EditActivity.REQUEST_CODE) }
    }

    override fun onCreateOptionsMenu(menu: Menu) = consume { menuInflater.inflate(R.menu.menu, menu) }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings -> consume { startActivityForResult<Preferences>(Preferences.REQUEST_CODE) }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EditActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the list
            adapter.refreshDataFromDB()
        }
    }

    //
    // endregion Activity's lifecycle
    //
}