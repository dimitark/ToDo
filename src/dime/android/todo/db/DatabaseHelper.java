package dime.android.todo.db;

import java.util.ArrayList;
import java.util.List;

import dime.android.todo.logic.Task;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static final int		DATABASE_VERSION	= 1;
	private static final String	DATABASE_NAME		= "todo.db";

	private static final String	TABLE_NAME			= "todo";

	private static final String	_ID					= "id";
	private static final String	_NAME					= "name";
	private static final String	_PRIORITY			= "priority";
	private static final String	_COMPLETED			= "completed";

	private static final String	CREATE_TABLE		= "CREATE TABLE " + TABLE_NAME + " (" + _ID
																			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + _NAME
																			+ " TEXT, " + _PRIORITY + " INTEGER, " + _COMPLETED
																			+ " INTEGER);";

	private static final String	DELETE_COMPLETED	= "DELETE FROM " + TABLE_NAME + " WHERE " + _COMPLETED
																			+ " = 1;";
	private static final String	DELETE_TASK			= "DELETE FROM " + TABLE_NAME + " WHERE " + _ID + " = ?;";
	private static final String	UPDATE_TASK			= "UPDATE " + TABLE_NAME + " SET " + _NAME + " = ?, "
																			+ _PRIORITY + " = ?, " + _COMPLETED
																			+ " = ? WHERE id = ?;";
	private static final String	INSERT_TASK			= "INSERT INTO " + TABLE_NAME + " (" + _NAME + ","
																			+ _PRIORITY + "," + _COMPLETED
																			+ ") VALUES (?, ?, ?);";


	public DatabaseHelper (Context context)
	{
		super (context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate (SQLiteDatabase db)
	{
		db.execSQL (CREATE_TABLE);
	}


	@Override
	public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// Do nothing
	}


	/**
	 * Adds the given task to the database
	 * 
	 * @param task
	 */
	public void addTask (Task task)
	{
		String bindArgs[] = {task.getName ( ), task.getPriority ( ) + "", (task.isCompleted ( ) ? "1" : "0")};
		getWritableDatabase ( ).execSQL (INSERT_TASK, bindArgs);
	}


	/**
	 * Update the given task in the database
	 * 
	 * @param task
	 */
	public void updateTask (Task task)
	{
		String bindArgs[] = {task.getName ( ), task.getPriority ( ) + "", (task.isCompleted ( ) ? "1" : "0"),
				task.getId ( ) + ""};
		getWritableDatabase ( ).execSQL (UPDATE_TASK, bindArgs);
	}


	/**
	 * Deletes the given task from the database
	 * 
	 * @param task
	 */
	public void deleteTask (Task task)
	{
		String bindArgs[] = {task.getId ( ) + ""};
		getWritableDatabase ( ).execSQL (DELETE_TASK, bindArgs);
	}


	/**
	 * Returns a list of all tasks stored in the database
	 */
	public List<Task> getAllTasks ( )
	{
		List<Task> allTasks = new ArrayList<Task> ( );

		SQLiteDatabase db = this.getReadableDatabase ( );
		Cursor cursor = db.query (TABLE_NAME, null, null, null, null, null, _COMPLETED + " ASC, " + _PRIORITY
				+ " DESC");

		if (cursor.moveToFirst ( ))
		{
			int _id_index = cursor.getColumnIndex (_ID);
			int _name_index = cursor.getColumnIndex (_NAME);
			int _priority_index = cursor.getColumnIndex (_PRIORITY);
			int _completed_index = cursor.getColumnIndex (_COMPLETED);

			do
			{
				int id = cursor.getInt (_id_index);
				String name = cursor.getString (_name_index);
				int priority = cursor.getInt (_priority_index);
				boolean completed = (cursor.getInt (_completed_index) == 0 ? false : true);

				allTasks.add (new Task (id, name, priority, completed));

			} while (cursor.moveToNext ( ));
		}

		cursor.close ( );

		return allTasks;
	}


	/**
	 * Deletes all the completed tasks from the database
	 */
	public void deleteCompleted ( )
	{
		getWritableDatabase ( ).execSQL (DELETE_COMPLETED);
	}

}
