package dime.android.todo.widget;

import java.util.List;

import dime.android.todo.R;
import dime.android.todo.db.DatabaseHelper;
import dime.android.todo.logic.Task;
import dime.android.todo.logic.TaskListAdapter;
import dime.android.todo.ui.ToDoList;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class ToDoWidgetService extends Service
{
	private static final int[] task_names = {R.id.task_name_0, R.id.task_name_1, R.id.task_name_2, R.id.task_name_3, R.id.task_name_4, R.id.task_name_5};
	private static final int[] task_color = {R.id.priority_color_0, R.id.priority_color_1, R.id.priority_color_2, R.id.priority_color_3, R.id.priority_color_4, R.id.priority_color_5};
	
	private static final int WIDGET_ITEMS_COUNT = 6;
	
	
	private DatabaseHelper dh;

	private void setPriorityColor (RemoteViews rv, int viewId, int priority)
	{
		rv.setInt (viewId, "setBackgroundResource", TaskListAdapter.colors[priority]);
	}
	
	private void clearColor (RemoteViews rv, int viewId)
	{
		rv.setInt (viewId, "setBackgroundResource", R.color.transparent);
	}
	
	private RemoteViews buildUpdate (Context context)
	{
		// Get the data from the db
		dh = new DatabaseHelper (context);
		List<Task> uncompletedTasks = dh.getUncompletedTasks ( );
		
		// Build an update that holds the updated widget contents
		RemoteViews updateViews = new RemoteViews (context.getPackageName ( ), R.layout.todo_widget);
		
		/*
		 * Add click listener
		 */
		Intent intent = new Intent(context, ToDoList.class);
		PendingIntent pendingIntent = PendingIntent.getActivity (context, 0, intent, 0);
		updateViews.setOnClickPendingIntent (R.id.widget_layout, pendingIntent);
		
		/*
		 * Fill the widget with data
		 */
		for (int i = 0; i < Math.min (WIDGET_ITEMS_COUNT, uncompletedTasks.size ( )); i++){
			updateViews.setTextViewText (task_names[i], uncompletedTasks.get (i).getName ( ));
			setPriorityColor (updateViews, task_color[i], uncompletedTasks.get (i).getPriority ( ));
		}
		
		/*
		 * Clear the data for the unused widget items
		 */
		for (int i = Math.min (WIDGET_ITEMS_COUNT, uncompletedTasks.size ( )); i < WIDGET_ITEMS_COUNT; i++ )
		{
			updateViews.setTextViewText (task_names[i], "");
			clearColor(updateViews, task_color[i]);
		}
		
		return updateViews;
	}


	@Override
	public void onStart (Intent intent, int startId)
	{
		// Build the widget update
		RemoteViews updateViews = buildUpdate (this);

		// Push update for this widget to the home screen
		ComponentName thisWidget = new ComponentName (this, ToDoWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance (this);
		manager.updateAppWidget (thisWidget, updateViews);

		dh.close ( );
		this.stopSelf ( );
	}


	@Override
	public IBinder onBind (Intent arg0)
	{
		// We don't need to bind to this service
		return null;
	}

}
