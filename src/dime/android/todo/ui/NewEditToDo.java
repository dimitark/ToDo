package dime.android.todo.ui;

import dime.android.todo.ToDo;
import dime.android.todo.R;
import dime.android.todo.logic.Task;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewEditToDo extends Activity implements OnClickListener
{
	private Button		saveBtn;
	private Button		cancelBtn;

	private EditText	txtName;

	private Task		task;


	/** Called when the activity is first created. */
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.edit_todo);

		saveBtn = (Button) findViewById (R.id.btn_save);
		cancelBtn = (Button) findViewById (R.id.btn_cancel);

		txtName = (EditText) findViewById (R.id.txt_name);

		saveBtn.setOnClickListener (this);
		cancelBtn.setOnClickListener (this);

		task = ((ToDo) getApplication ( )).getTaskToEdit ( );

		if (task != null)
		{
			txtName.setText (task.getName ( ));
		}
	}


	private void displayError ( )
	{
		Toast.makeText (this.getApplicationContext ( ), getString (R.string.name_cannot_be_empty), Toast.LENGTH_SHORT).show ( );
	}


	public void onClick (View v)
	{
		if (v == cancelBtn)
		{
			finish ( );
		}
		else if (v == saveBtn)
		{
			if (task == null)
			{
				/*
				 * We are adding a new task
				 */
				if (txtName.getText ( ).length ( ) == 0)
				{
					displayError ( );
				}
				else
				{
					task = new Task (-1, txtName.getText ( ).toString ( ), 0, false);

					((ToDo) getApplication ( )).dbHelper.addTask (task);
					((ToDo) getApplication ( )).setIsValidData (false);
					finish ( );
				}
			}
			else
			{
				/*
				 * We are editing some task
				 */
				if (txtName.getText ( ).length ( ) == 0)
				{
					displayError ( );
				}
				else
				{
					task.setName (txtName.getText ( ).toString ( ));
					((ToDo) getApplication ( )).dbHelper.updateTask (task);
					((ToDo) getApplication ( )).setIsValidData (false);
					finish ( );
				}
			}
		}

	}
}
