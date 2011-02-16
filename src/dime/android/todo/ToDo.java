package dime.android.todo;

import dime.android.todo.db.DatabaseHelper;
import android.app.Application;
import android.content.res.Configuration;

public class ToDo extends Application
{

	public DatabaseHelper	dbHelper;


	@Override
	public void onCreate ( )
	{
		super.onCreate ( );

		dbHelper = new DatabaseHelper (getApplicationContext ( ));
	}


	@Override
	public void onTerminate ( )
	{
		super.onTerminate ( );
	}


	@Override
	public void onConfigurationChanged (Configuration newConfig)
	{
		super.onConfigurationChanged (newConfig);
	}
}
