package com.app.androidweartaskmanagerapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.androidweartaskmanagerapp.R;
import com.app.androidweartaskmanagerapp.model.Task;
import com.app.androidweartaskmanagerapp.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WearActionReceiver extends BroadcastReceiver {
    // Add the date format to this class
    private static final SimpleDateFormat customDateFormat =
            new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(context.getString(R.string.wear_action))) {
                // Existing wear action logic
                Intent updateIntent = new Intent(context, UpcomingTasksActivity.class);
                updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                updateIntent.putExtra(context.getString(R.string.show_upcoming_within_one_hour), true);
                context.startActivity(updateIntent);
            }

            if (intent.getAction().equals(context.getString(R.string.check_notifications))) {
                AddTaskActivity.checkTriggerApproachingTaskNotifications(context);
            }

            // New action for snoozing
            if (intent.getAction().equals(context.getString(R.string.snooze_action))) {
                String taskId = intent.getStringExtra("TaskID");
                if (taskId != null) {
                    snoozeTask(context, taskId);
                }
            }
        }
    }

    // Method to snooze a task
    private void snoozeTask(Context context, String taskId) {
        List<Task> tasks = Constant.getAllTasks(context);
        for (Task task : tasks) {
            if (task.getTaskId().equals(taskId)) {
                // Check if task can be snoozed
                if (task.getSnoozeCount() < 5) {
                    // Increment snooze count
                    task.incrementSnoozeCount();

                    // Extend due time by 5 minutes
                    Calendar calendar = Calendar.getInstance();
                    try {
                        // Use the local customDateFormat instead of AddTaskActivity's
                        calendar.setTime(customDateFormat.parse(task.getDueDateTime()));
                        calendar.add(Calendar.MINUTE, 5);

                        // Update task due date
                        task.setDueDateTime(calendar.getTime().toString());

                        // Save updated task
                        Constant.saveTask(task, context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }
}