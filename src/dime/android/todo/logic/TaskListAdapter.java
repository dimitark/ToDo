package dime.android.todo.logic;

import dime.android.todo.R;
import dime.android.todo.ToDo;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter
{
	private ToDo				toDoApp;
	private LayoutInflater	mInflater;
	private Context			context;


	private void changeItemStyle (ViewHolder holder, Task task)
	{
		if (task.isCompleted ( ))
		{
			holder.icon.setImageDrawable (context.getResources ( ).getDrawable (R.drawable.tick));
			holder.task_name.setPaintFlags (holder.task_name.getPaintFlags ( ) | Paint.STRIKE_THRU_TEXT_FLAG);
			holder.task_name.setTextColor (context.getResources ( ).getColor (R.color.gray));
			holder.list_item_layout.setBackgroundResource (R.color.gray_transparent);
		}
		else
		{
			holder.icon.setImageDrawable (context.getResources ( ).getDrawable (R.drawable.alert));
			holder.task_name.setPaintFlags (holder.task_name.getPaintFlags ( ) & ~Paint.STRIKE_THRU_TEXT_FLAG);
			holder.task_name.setTextColor (context.getResources ( ).getColor (R.color.white));
			holder.list_item_layout.setBackgroundResource (R.color.transparent);
		}
	}


	public TaskListAdapter (Context context)
	{
		this.context = context;
		toDoApp = (ToDo) context.getApplicationContext ( );
		mInflater = LayoutInflater.from (context);
	}


	public int getCount ( )
	{
		return toDoApp.taskList.size ( );
	}


	public Object getItem (int position)
	{
		return toDoApp.taskList.get (position);
	}


	public long getItemId (int position)
	{
		return toDoApp.taskList.get (position).getId ( );
	}


	public View getView (int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if (convertView == null)
		{
			convertView = mInflater.inflate (R.layout.todo_list_item, null);

			holder = new ViewHolder ( );
			holder.list_item_layout = (LinearLayout) convertView.findViewById (R.id.list_item_layout);
			holder.icon = (ImageView) convertView.findViewById (R.id.task_check_icon);
			holder.task_name = (TextView) convertView.findViewById (R.id.task_name);

			convertView.setTag (holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag ( );
		}

		/*
		 * Bind the data
		 */
		Task task = toDoApp.taskList.get (position);
		holder.task_name.setText (task.getName ( ));

		// Change the icon and the text style based on the "completed" info
		changeItemStyle (holder, task);

		return convertView;
	}

	/**
	 * The ViewHolder class
	 */
	static class ViewHolder
	{
		LinearLayout	list_item_layout;
		ImageView		icon;
		TextView			task_name;
	}

}
