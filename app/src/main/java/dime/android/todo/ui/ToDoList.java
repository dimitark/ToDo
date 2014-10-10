package dime.android.todo.ui;

import dime.android.todo.ToDo;
import dime.android.todo.R;
import dime.android.todo.logic.Task;
import dime.android.todo.logic.TaskListAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ToDoList extends Activity implements OnClickListener, OnItemClickListener
{
	private ToDo				toDoApp;
	private ListView			taskList;
	private TaskListAdapter	listAdapter;
	private LinearLayout		newTaskLayout;


	private void removeCompleted ( )
	{
		toDoApp.dbHelper.deleteCompleted ( );
		refreshUI ( );
	}
	
	private void openPreferencesActivity()
	{
		Intent intent = new Intent(this, Preferences.class);
		startActivityForResult(intent, 0);
	}


	/** Called when the activity is first created. */
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.todo_list);

		toDoApp = (ToDo) getApplication ( );

		taskList = (ListView) findViewById (R.id.task_list);

		listAdapter = new TaskListAdapter (this);
		taskList.setAdapter (listAdapter);
		taskList.setOnItemClickListener (this);

		registerForContextMenu (taskList);

		newTaskLayout = (LinearLayout) findViewById (R.id.new_task_layout);
		newTaskLayout.setOnClickListener (this);
	}


	@Override
	public void onResume ( )
	{
		super.onResume ( );

		if (!toDoApp.isDataValid ( ))
		{
			toDoApp.reloadFromDb ( );
			listAdapter.notifyDataSetChanged ( );
		}
	}
	
	@Override
	public void onPause ( )
	{
		super.onPause ( );
		toDoApp.dbHelper.close ( );
	}
	
	@Override
	public void onStop ( )
	{
		super.onStop ( );
		toDoApp.dbHelper.close ( );
	}
	
	
	@Override 
	public void onDestroy ( )
	{
		super.onDestroy ( );
		toDoApp.dbHelper.close ( );
	}


	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		MenuInflater inflater = getMenuInflater ( );
		inflater.inflate (R.menu.menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		switch (item.getItemId ( ))
		{
			case R.id.remove_completed:
				removeCompleted ( );
				return true;
			case R.id.settings:
				openPreferencesActivity();
				return true;
			default:
				return super.onOptionsItemSelected (item);
		}
	}


	@Override
	public void onCreateContextMenu (ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu (menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater ( );
		inflater.inflate (R.menu.list_long_menu, menu);
	}


	@Override
	public boolean onContextItemSelected (MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo ( );

		switch (item.getItemId ( ))
		{
			case R.id.edit_todo:
				editTask (info.position);
				return true;
			case R.id.delete_todo:
				deleteTask (info.position);
				return true;
			default:
				return super.onContextItemSelected (item);
		}
	}


	public void refreshUI ( )
	{
		toDoApp.reloadFromDb ( );
		listAdapter.notifyDataSetChanged ( );
	}


	public void showNewEditTodo ( )
	{
		Intent myIntent = new Intent (this, NewEditToDo.class);
		startActivityForResult (myIntent, 0);
	}


	private void deleteTask (int position)
	{
		Task task = toDoApp.taskList.get (position);
		toDoApp.dbHelper.deleteTask (task);
		refreshUI ( ); 
	}


	private void editTask (int position)
	{
		Task task = toDoApp.taskList.get (position);
		toDoApp.setTaskToEdit (task);
		showNewEditTodo ( ); 
	}


	private void toggleCompleted (Task task)
	{
		task.setCompleted (!task.isCompleted ( ));
		toDoApp.dbHelper.updateTask (task);
		refreshUI ( );
	}


	public void onClick (View v)
	{
		if (v == newTaskLayout)
		{
			toDoApp.setTaskToEdit (null);
			showNewEditTodo ( );
		}
	}


	public void onItemClick (AdapterView<?> parentView, View childView, int position, long id)
	{
		Task task = toDoApp.taskList.get (position);
		toggleCompleted (task);
	}

}