package dime.android.todo.main

import android.content.Context
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import dime.android.todo.R
import dime.android.todo.db.Task
import dime.android.todo.db.database
import dime.android.todo.extensions.doIfTrue
import dime.android.todo.extensions.inflate
import org.jetbrains.anko.find

/**
 * Created by dime on 06/02/16.
 */

class TaskListAdapter(val context: Context): RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    companion object {
        // The priority colors
        val priorityColors = mapOf(
                Task.Priority.LOW to R.color.low_priority,
                Task.Priority.NORMAL to R.color.normal_priority,
                Task.Priority.HIGH to R.color.high_priority)
    }

    // The alpha values of the priorities
    val priorityAlphas = mapOf(Task.Priority.LOW to 0.25f, Task.Priority.NORMAL to 0.5f, Task.Priority.HIGH to 1f)

    // The items
    val tasks = mutableListOf<Task>()

    // The task click listener
    var taskClickListener: ((id: Int) -> Unit)? = null

    // The error delegate (called when there is an error)
    var errorDelegate: ((message: String) -> Unit)? = null

    /**
     * The init block
     */
    init {
        tasks.addAll(context.database.allTasks())
    }

    /**
     * Refreshed the data from the database
     */
    fun refreshDataFromDB() {
        tasks.clear()
        tasks.addAll(context.database.allTasks())
        notifyDataSetChanged()
    }

    /**
     * Called when an item (row) needs to be updated
     */
    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        // Get the task
        val task = tasks[position]

        // Refresh the row
        vh.dataPosition = position
        vh.taskName.text = task.name
        vh.priorityImage.setColorFilter(vh.itemView.resources.getColor(priorityColors[task.priority]!!))
        refreshUIBasedOnCompleted(vh, task)
    }

    /**
     * Called when an item (row) needs to be created
     */
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder? {
        // Inflate the view
        val view = parent.inflate(R.layout.todo_list_item)
        view.setOnClickListener { taskClickListener?.invoke(tasks[position].id!!) }

        // Create the view holder
        return ViewHolder(view)
    }

    /**
     * Returns the number of items in this adapter
     */
    override fun getItemCount() = tasks.size


    /**
     * Refreshes the UI based on the completed property of the task
     */
    private fun refreshUIBasedOnCompleted(vh: ViewHolder, task: Task) {
        vh.doneLayer.visibility = if (task.completed) View.VISIBLE else View.GONE
        vh.taskName.alpha = if (task.completed) 0.2f else 1.0f
        vh.taskName.paintFlags = if (task.completed) vh.initialPaintFlags or Paint.STRIKE_THRU_TEXT_FLAG else vh.initialPaintFlags
        vh.priorityImage.alpha = if (task.completed) 0.2f else priorityAlphas[task.priority]!!
    }

    /**
     * The view holder
     */
    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val taskName by lazy { view.find<TextView>(R.id.task_name) }
        val foregroundLayer by lazy { view.find<View>(R.id.list_item_layout) }
        val checkBox by lazy { view.find<CheckBox>(R.id.done_checkbox) }
        val priorityImage by lazy { view.find<ImageView>(R.id.priority_image) }
        val doneLayer by lazy { view.find<View>(R.id.done_layer) }

        val initialPaintFlags: Int
        var dataPosition = 0

        init {
            // Setup the UI
            taskName; foregroundLayer; priorityImage
            checkBox.bringToFront()
            doneLayer.alpha = 0.2f
            initialPaintFlags = taskName.paintFlags
            checkBox.setOnCheckedChangeListener { compoundButton, isChecked ->
                val task = tasks[dataPosition]
                task.completed = isChecked

                if (context.database.updateTask(task)) {
                    refreshUIBasedOnCompleted(this, task)
                } else {
                    compoundButton.isChecked = !isChecked
                    task.completed = !isChecked
                    errorDelegate?.invoke(context.getString(R.string.error_while_saving))
                }
            }
        }
    }
}