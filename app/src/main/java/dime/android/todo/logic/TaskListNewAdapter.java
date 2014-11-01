package dime.android.todo.logic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dime.android.todo.R;
import dime.android.todo.ToDo;
import dime.android.todo.ui.SwipeDetector;

/**
 * Created by dime on 31/10/14.
 */
public class TaskListNewAdapter extends RecyclerView.Adapter<TaskListNewAdapter.ViewHolder> {
    public static final int colors[] = {R.color.low_priority, R.color.normal_priority, R.color.high_priority};

    private ToDo app;

    /**
     * Default constructor
     *
     * @param app
     */
    public TaskListNewAdapter(ToDo app) {
        this.app = app;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Task task = app.taskList.get(position);
        viewHolder.task_name.setText(task.getName());
        // viewHolder.priorityColor.setBackgroundResource(colors[task.getPriority()]);
    }

    @Override
    public int getItemCount() {
        return app.taskList.size();
    }

    /**
     * The view holder class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView task_name;
        public View priorityColor;

        /**
         * Default constructor
         *
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);

            /* Register a swipe listener */
            itemView.setOnTouchListener(new SwipeDetector());

            /* get references to the views */
            task_name = (TextView) itemView.findViewById(R.id.task_name);
            priorityColor = itemView.findViewById(R.id.priority_color);
        }
    }
}
