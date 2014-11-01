package dime.android.todo.ui;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import dime.android.todo.R;
import dime.android.todo.ToDo;
import dime.android.todo.logic.Task;

public class NewEditToDo extends ActionBarActivity implements OnClickListener {
    private EditText txtName;
    private ToggleButton[] priorityButtons;
    private Task task;
    private ActionBar actionBar;

    /* Action bar elements */
    private ImageButton cancelButton;
    private ImageButton saveButton;


    private void setUpTheActionBar() {
         /* Set up the custom action bar */
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        View actionBarView = getLayoutInflater().inflate(R.layout.new_edit_action_bar, null);
        actionBar.setCustomView(actionBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        // Register the click actions
        cancelButton = (ImageButton) actionBarView.findViewById(R.id.cancel);
        saveButton = (ImageButton) actionBarView.findViewById(R.id.save);

        cancelButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_todo);

        /* Set up the action bar */
        setUpTheActionBar();

        txtName = (EditText) findViewById(R.id.txt_name);

        priorityButtons = new ToggleButton[3];
        priorityButtons[Task.PRIORITY_LOW] = (ToggleButton) findViewById(R.id.btn_low_priority);
        priorityButtons[Task.PRIORITY_NORMAL] = (ToggleButton) findViewById(R.id.btn_normal_priority);
        priorityButtons[Task.PRIORITY_HIGH] = (ToggleButton) findViewById(R.id.btn_high_priority);

        priorityButtons[Task.PRIORITY_LOW].setOnClickListener(this);
        priorityButtons[Task.PRIORITY_NORMAL].setOnClickListener(this);
        priorityButtons[Task.PRIORITY_HIGH].setOnClickListener(this);

        task = ((ToDo) getApplication()).getTaskToEdit();

        if (task != null) {
            txtName.setText(task.getName());
            txtName.setSelection(txtName.getText().toString().length());

            selectPriority(task.getPriority());
        } else {
            selectPriority(Task.PRIORITY_NORMAL);
        }
    }


    private void selectPriority(int priority) {
        for (int i = 0; i < priorityButtons.length; i++) {
            if (i == priority) {
                priorityButtons[i].setChecked(true);
            } else {
                priorityButtons[i].setChecked(false);
            }
        }
    }


    private int getPriority() {
        for (int i = 0; i < priorityButtons.length; i++) {
            if (priorityButtons[i].isChecked()) {
                return i;
            }
        }

        return Task.PRIORITY_LOW;
    }


    private void displayError() {
        Toast.makeText(this.getApplicationContext(), getString(R.string.name_cannot_be_empty),
                Toast.LENGTH_SHORT).show();
    }


    public void onClick(View v) {
        if (v == saveButton) {
            if (task == null) {
                /*
                 * We are adding a new task
				 */
                if (txtName.getText().length() == 0) {
                    displayError();
                } else {
                    task = new Task(-1, txtName.getText().toString(), getPriority(), false);

                    ((ToDo) getApplication()).dbHelper.addTask(task);
                    ((ToDo) getApplication()).setIsValidData(false);
                    finish();
                }
            } else {
				/*
				 * We are editing some task
				 */
                if (txtName.getText().length() == 0) {
                    displayError();
                } else {
                    task.setName(txtName.getText().toString());
                    task.setPriority(getPriority());
                    ((ToDo) getApplication()).dbHelper.updateTask(task);
                    ((ToDo) getApplication()).setIsValidData(false);
                    finish();
                }
            }
        } else if (v == cancelButton) {
            finish();
        } else if (v == priorityButtons[Task.PRIORITY_LOW]) {
            selectPriority(Task.PRIORITY_LOW);
        } else if (v == priorityButtons[Task.PRIORITY_NORMAL]) {
            selectPriority(Task.PRIORITY_NORMAL);
        } else if (v == priorityButtons[Task.PRIORITY_HIGH]) {
            selectPriority(Task.PRIORITY_HIGH);
        }

    }
}
