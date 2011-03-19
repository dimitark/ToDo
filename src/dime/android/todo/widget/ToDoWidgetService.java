package dime.android.todo.widget;

import java.util.List;

import dime.android.todo.R;
import dime.android.todo.db.DatabaseHelper;
import dime.android.todo.logic.Task;
import dime.android.todo.ui.ToDoList;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;

public class ToDoWidgetService extends Service
{
	private static final int[] task_names = {R.id.task_name_0, R.id.task_name_1, R.id.task_name_2, R.id.task_name_3, R.id.task_name_4, R.id.task_name_5};
	private static final int[][] task_color = {{R.id.priority_color_0_low, R.id.priority_color_1_low, R.id.priority_color_2_low, R.id.priority_color_3_low, R.id.priority_color_4_low, R.id.priority_color_5_low},
												{R.id.priority_color_0_normal, R.id.priority_color_1_normal, R.id.priority_color_2_normal, R.id.priority_color_3_normal, R.id.priority_color_4_normal, R.id.priority_color_5_normal},
												{R.id.priority_color_0_high, R.id.priority_color_1_high, R.id.priority_color_2_high, R.id.priority_color_3_high, R.id.priority_color_4_high, R.id.priority_color_5_high}};
	
	private static final int WIDGET_ITEMS_COUNT = 6;
	
	
	private DatabaseHelper dh;

	private void setPriorityColor (RemoteViews rv, int index, int priority)
	{
		for (int i = 0; i < 3; i++)
		{
			if (i == priority)
			{
				rv.setViewVisibility (task_color[i][index], View.VISIBLE);
			}
			else
			{
				rv.setViewVisibility (task_color[i][index], View.GONE);
			}
			
		}
	}
	
	private void clearColor (RemoteViews rv, int index)
	{
		for (int i = 0; i < 3; i++)
		{
			rv.setViewVisibility (task_color[i][index], View.GONE);
		}
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
			setPriorityColor (updateViews, i, uncompletedTasks.get (i).getPriority ( ));
		}
		
		/*
		 * Clear the data for the unused widget items
		 */
		for (int i = Math.min (WIDGET_ITEMS_COUNT, uncompletedTasks.size ( )); i < WIDGET_ITEMS_COUNT; i++ )
		{
			updateViews.setTextViewText (task_names[i], "");
			clearColor(updateViews, i);
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
