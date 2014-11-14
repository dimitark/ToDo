package dime.android.todo.logic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dime.android.todo.R;
import dime.android.todo.ToDo;
import dime.android.todo.ui.RecyclerViewSwipeToRemove;

/**
 * Created by dime on 31/10/14.
 */
public class TaskListNewAdapter extends RecyclerView.Adapter<TaskListNewAdapter.ViewHolder>
        implements View.OnClickListener {
    public static final int colors[] = {R.color.low_priority, R.color.normal_priority, R.color.high_priority};
    public static final float alpha[] = {0.25f, 0.5f, 1.0f};

    private ToDo app;
    private ClickResponder clickResponder;

    /**
     * Default constructor
     *
     * @param app
     */
    public TaskListNewAdapter(ToDo app, ClickResponder clickResponder) {
        this.app = app;
        this.clickResponder = clickResponder;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item, parent, false);
        v.setOnClickListener(this);

        ViewHolder vh = new ViewHolder(v, this);
        v.setTag(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Task task = app.taskList.get(position);
        viewHolder.task_name.setText(task.getName());
        viewHolder.priorityImage.setAlpha(alpha[task.getPriority()]);
        viewHolder.priorityImage.setColorFilter(viewHolder.itemView.getResources().getColor(colors[task.getPriority()]));
        viewHolder.refreshUI();
    }

    @Override
    public int getItemCount() {
        return app.taskList.size();
    }

    @Override
    public void onClick(View v) {
        if (clickResponder != null) {
            ViewHolder vh = (ViewHolder) v.getTag();
            clickResponder.onClick(vh.getPosition());
        }
    }

    /**
     * The view holder class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ToDo app;
        private TaskListNewAdapter adapter;

        public Task task;

        public TextView task_name;
        public View foregroundLayer;
        public CheckBox checkBox;
        public ImageView priorityImage;
        public View doneLayer;

        private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Get the task
                Task task = app.taskList.get(getPosition());

                // Change the DB record
                checkChanged(task);

                // Refresh UI
                refreshUI();

                // Get the new sorting order
                List<Task> newOrder = app.dbHelper.getAllTasks();
                adapter.notifyItemMoved(getPosition(), newOrder.indexOf(task));

                // Reload
                app.setTaskList(newOrder);
            }
        };

        /**
         * Default constructor
         *
         * @param itemView
         */
        public ViewHolder(View itemView, final TaskListNewAdapter adapter) {
            super(itemView);

            /* Get the app */
            app = (ToDo) itemView.getContext().getApplicationContext();
            this.adapter = adapter;

            /* get references to the views */
            task_name = (TextView) itemView.findViewById(R.id.task_name);
            checkBox = (CheckBox) itemView.findViewById(R.id.done_checkbox);
            checkBox.bringToFront();
            foregroundLayer = itemView.findViewById(R.id.list_item_layout);
            priorityImage = (ImageView) itemView.findViewById(R.id.priority_image);
            doneLayer = itemView.findViewById(R.id.done_layer);
            doneLayer.setAlpha(0.2f);
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        }

        public void checkChanged(Task task) {
            task.setCompleted(checkBox.isChecked());
            app.dbHelper.updateTask(task);
        }

        public void refreshUI() {
            Task task = app.taskList.get(getPosition());

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(task.isCompleted());
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);

            doneLayer.setVisibility(task.isCompleted() ? View.VISIBLE : View.GONE);
            task_name.setAlpha(task.isCompleted() ? 0.2f : 1.0f);
            priorityImage.setAlpha(task.isCompleted() ? 0.2f : alpha[task.getPriority()]);
        }
    }


    public interface ClickResponder {
        public void onClick(int position);
    }
}
