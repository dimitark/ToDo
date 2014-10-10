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
import android.widget.ToggleButton;

public class NewEditToDo extends Activity implements OnClickListener
{
	private Button				saveBtn;
	private Button				cancelBtn;

	private EditText			txtName;

	private ToggleButton[]	priorityButtons;

	private Task				task;


	/** Called when the activity is first created. */
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.edit_todo);

		saveBtn = (Button) findViewById (R.id.btn_save);
		cancelBtn = (Button) findViewById (R.id.btn_cancel);

		txtName = (EditText) findViewById (R.id.txt_name);

		priorityButtons = new ToggleButton[3];
		priorityButtons[Task.PRIORITY_LOW] = (ToggleButton) findViewById (R.id.btn_low_priority);
		priorityButtons[Task.PRIORITY_NORMAL] = (ToggleButton) findViewById (R.id.btn_normal_priority);
		priorityButtons[Task.PRIORITY_HIGH] = (ToggleButton) findViewById (R.id.btn_high_priority);

		saveBtn.setOnClickListener (this);
		cancelBtn.setOnClickListener (this);

		priorityButtons[Task.PRIORITY_LOW].setOnClickListener (this);
		priorityButtons[Task.PRIORITY_NORMAL].setOnClickListener (this);
		priorityButtons[Task.PRIORITY_HIGH].setOnClickListener (this);

		task = ((ToDo) getApplication ( )).getTaskToEdit ( );

		if (task != null)
		{
			txtName.setText (task.getName ( ));
			txtName.setSelection (txtName.getText ( ).toString ( ).length ( ));

			selectPriority (task.getPriority ( ));
		}
		else
		{
			selectPriority (Task.PRIORITY_NORMAL);
		}
	}


	private void selectPriority (int priority)
	{
		for (int i = 0; i < priorityButtons.length; i++)
		{
			if (i == priority)
			{
				priorityButtons[i].setChecked (true);
			}
			else
			{
				priorityButtons[i].setChecked (false);
			}
		}
	}


	private int getPriority ( )
	{
		for (int i = 0; i < priorityButtons.length; i++)
		{
			if (priorityButtons[i].isChecked ( ))
			{
				return i;
			}
		}

		return Task.PRIORITY_LOW;
	}


	private void displayError ( )
	{
		Toast.makeText (this.getApplicationContext ( ), getString (R.string.name_cannot_be_empty),
				Toast.LENGTH_SHORT).show ( );
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
					task = new Task (-1, txtName.getText ( ).toString ( ), getPriority ( ), false);

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
					task.setPriority (getPriority ( ));
					((ToDo) getApplication ( )).dbHelper.updateTask (task);
					((ToDo) getApplication ( )).setIsValidData (false);
					finish ( );
				}
			}
		}
		else if (v == priorityButtons[Task.PRIORITY_LOW])
		{
			selectPriority (Task.PRIORITY_LOW);
		}
		else if (v == priorityButtons[Task.PRIORITY_NORMAL])
		{
			selectPriority (Task.PRIORITY_NORMAL);
		}
		else if (v == priorityButtons[Task.PRIORITY_HIGH])
		{
			selectPriority (Task.PRIORITY_HIGH);
		}

	}
}
