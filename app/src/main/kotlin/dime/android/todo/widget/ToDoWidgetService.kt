package dime.android.todo.widget

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.preference.PreferenceManager
import android.view.View
import android.widget.RemoteViews
import dime.android.todo.R
import dime.android.todo.db.Task
import dime.android.todo.db.database
import dime.android.todo.main.MainActivity

// TODO Rewrite
class ToDoWidgetService : Service() {

    /**
     * Static
     */
    companion object {
        private val WIDGET_ITEMS_COUNT = 6

        private val task_names = intArrayOf(
                R.id.task_name_0,
                R.id.task_name_1,
                R.id.task_name_2,
                R.id.task_name_3,
                R.id.task_name_4,
                R.id.task_name_5)

        private val task_color = arrayOf(
                intArrayOf(R.id.priority_color_0_low, R.id.priority_color_1_low, R.id.priority_color_2_low, R.id.priority_color_3_low, R.id.priority_color_4_low, R.id.priority_color_5_low),
                intArrayOf(R.id.priority_color_0_normal, R.id.priority_color_1_normal, R.id.priority_color_2_normal, R.id.priority_color_3_normal, R.id.priority_color_4_normal, R.id.priority_color_5_normal),
                intArrayOf(R.id.priority_color_0_high, R.id.priority_color_1_high, R.id.priority_color_2_high, R.id.priority_color_3_high, R.id.priority_color_4_high, R.id.priority_color_5_high))
    }

    private fun setPriorityColor(rv: RemoteViews, index: Int, priority: Task.Priority) {
        for (i in 0..2) {
            if (i == priority.integer) {
                rv.setViewVisibility(task_color[i][index], View.VISIBLE)
            } else {
                rv.setViewVisibility(task_color[i][index], View.GONE)
            }

        }
    }

    private fun clearColor(rv: RemoteViews, index: Int) {
        for (i in 0..2) {
            rv.setViewVisibility(task_color[i][index], View.GONE)
        }
    }

    private fun buildUpdate(context: Context): RemoteViews {


        // Get the data from the db
        val uncompletedTasks = context.database.uncompletedTask()

        // Build an update that holds the updated widget contents
        val updateViews = RemoteViews(context.packageName, R.layout.todo_widget)

        /*
		 * Add click listener
		 */
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        updateViews.setOnClickPendingIntent(R.id.widget_layout, pendingIntent)

        /*
		 * Fill the widget with data
		 */
        for (i in 0..Math.min(WIDGET_ITEMS_COUNT, uncompletedTasks.size) - 1) {
            updateViews.setTextViewText(task_names[i], uncompletedTasks[i].name)
            setPriorityColor(updateViews, i, uncompletedTasks[i].priority)
        }

        /*
		 * Clear the data for the unused widget items
		 */
        for (i in Math.min(WIDGET_ITEMS_COUNT, uncompletedTasks.size)..WIDGET_ITEMS_COUNT - 1) {
            updateViews.setTextViewText(task_names[i], "")
            clearColor(updateViews, i)
        }

        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        val transparent = settings.getBoolean("widgetTransparentBackground", false)
        if (transparent)
            updateViews.setInt(R.id.widget_layout, "setBackgroundResource", 0)
        else
            updateViews.setInt(R.id.widget_layout, "setBackgroundResource", R.drawable.widget_background)

        // Hide/Show the title
        val hideTitle = settings.getBoolean("widgetHideTitle", false)
        if (hideTitle)
            updateViews.setViewVisibility(R.id.widget_title_layout, View.GONE)
        else
            updateViews.setViewVisibility(R.id.widget_title_layout, View.VISIBLE)

        return updateViews
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val superResult = super.onStartCommand(intent, flags, startId)

        // Build the widget update
        val updateViews = buildUpdate(this)

        // Push update for this widget to the home screen
        val thisWidget = ComponentName(this, ToDoWidget::class.java)
        val manager = AppWidgetManager.getInstance(this)
        manager.updateAppWidget(thisWidget, updateViews)

        this.stopSelf()
        return superResult
    }

    override fun onBind(arg0: Intent): IBinder? {
        // We don't need to bind to this service
        return null
    }



}
