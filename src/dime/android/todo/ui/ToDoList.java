package dime.android.todo.ui;

import dime.android.todo.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ToDoList extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.todo_list);
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		MenuInflater inflater = getMenuInflater ( );
		inflater.inflate (R.layout.menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		switch (item.getItemId ( ))
		{
			case R.id.remove_completed:
				// TODO: Clean the completed tasks
				return true;
			default:
				return super.onOptionsItemSelected (item);
		}
	}
}