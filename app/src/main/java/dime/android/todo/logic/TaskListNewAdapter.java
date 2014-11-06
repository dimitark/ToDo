package dime.android.todo.logic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

        ViewHolder vh = new ViewHolder(v, position);
        v.setTag(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Task task = app.taskList.get(position);
        viewHolder.position = position;
        viewHolder.task_name.setText(task.getName());
        viewHolder.priorityImage.setAlpha(alpha[task.getPriority()]);
        viewHolder.priorityImage.setColorFilter(viewHolder.itemView.getResources().getColor(colors[task.getPriority()]));
//        viewHolder.foregroundLayer.setBackgroundResource(colors[task.getPriority()]);
    }

    @Override
    public int getItemCount() {
        return app.taskList.size();
    }

    @Override
    public void onClick(View v) {
        if (clickResponder != null) {
            ViewHolder vh = (ViewHolder) v.getTag();
            clickResponder.onClick(vh.position);
        }
    }

    /**
     * The view holder class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public int position;
        public TextView task_name;
        public View foregroundLayer;
        public ImageView priorityImage;

        /**
         * Default constructor
         *
         * @param itemView
         */
        public ViewHolder(View itemView, int position) {
            super(itemView);

            /* Save the position */
            this.position = position;

            /* get references to the views */
            task_name = (TextView) itemView.findViewById(R.id.task_name);
            foregroundLayer = itemView.findViewById(R.id.list_item_layout);
            priorityImage = (ImageView) itemView.findViewById(R.id.priority_image);
        }
    }


    public interface ClickResponder {
        public void onClick(int position);
    }
}
