package com.app.androidweartaskmanagerapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

import com.app.androidweartaskmanagerapp.R;
import com.app.androidweartaskmanagerapp.activity.AddTaskActivity;
import com.app.androidweartaskmanagerapp.databinding.TaskItemBinding;
import com.app.androidweartaskmanagerapp.model.Task;
import com.app.androidweartaskmanagerapp.utils.Constant;

import java.util.List;

public class TaskAdapter extends WearableRecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private final Context context;
    private List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TaskItemBinding binding = TaskItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task currentTask = taskList.get(position);

        String formattedTaskName = context.getString(R.string.b_taskname_b) + currentTask.getTaskName();
        String formattedTaskID = context.getString(R.string.b_taskid_b) + currentTask.getTaskId();
        String formattedDtimeDate = context.getString(R.string.b_duetime_date_b) + currentTask.getDueDateTime();

        holder.binding.textViewTaskId.setText(Html.fromHtml(formattedTaskID));
        holder.binding.textViewTaskName.setText(Html.fromHtml(formattedTaskName));
        holder.binding.textViewDueDateTime.setText(Html.fromHtml(formattedDtimeDate));

        // Edit on click of the entire item
        holder.binding.getRoot().setOnClickListener(v -> {
            Intent editIntent = new Intent(context, AddTaskActivity.class);
            editIntent.putExtra("EDIT_TASK_ID", currentTask.getTaskId());
            context.startActivity(editIntent);
        });

        // Delete button implementation
        holder.binding.buttonDelete.setOnClickListener(v -> {
            Constant.deleteTask(currentTask.getTaskId(), context);

            // Remove from local list
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());

            Toast.makeText(context, R.string.task_deleted, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TaskItemBinding binding;

        public ViewHolder(@NonNull TaskItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}