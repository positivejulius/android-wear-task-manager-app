package com.app.androidweartaskmanagerapp.utils;

import static com.app.androidweartaskmanagerapp.activity.UpcomingTasksActivity.customDateFormat;

import android.content.Context;
import android.content.SharedPreferences;
import com.app.androidweartaskmanagerapp.model.Task;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Utility class for constant values and task management
public class Constant {

    // Constant for action add
    public static final int ACTION_ADD = 9001;

    // Save a task to SharedPreferences
    public static void saveTask(Task task, Context context) {
        if (task != null) {
            SharedPreferences preferences = context.getSharedPreferences("task_details", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            // Save task with additional snooze count
            editor.putString(task.getTaskId(),
                    task.getTaskName() + "|" +
                            task.getDueDateTime() + "|" +
                            task.getSnoozeCount());
            editor.apply();
        }
    }

    // Get all tasks from SharedPreferences
    public static List<Task> getAllTasks(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("task_details", Context.MODE_PRIVATE);
        List<Task> taskList = new ArrayList<>();
        Map<String, ?> map = sharedPref.getAll();
        Set set = map.entrySet();
        Iterator itr = set.iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            String savedTask = (String) entry.getValue();
            if (savedTask != null) {
                String[] taskDetails = savedTask.split("\\|");
                if (taskDetails.length >= 2) {
                    // Handle different lengths to maintain backward compatibility
                    int snoozeCount = taskDetails.length > 2 ? Integer.parseInt(taskDetails[2]) : 0;
                    Task task = new Task(
                            entry.getKey().toString(),
                            taskDetails[0],
                            taskDetails[1],
                            snoozeCount
                    );
                    taskList.add(task);
                }
            }
        }
        return taskList;
    }

    // Delete a task by its ID
    public static void deleteTask(String taskId, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("task_details", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(taskId);
        editor.apply();
    }

    // Update an existing task
    public static void updateTask(Task task, Context context) {
        saveTask(task, context);
    }

    // Save a list of tasks to SharedPreferences
    public static void saveAllTasks(List<Task> taskList, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("task_details", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        for (Task task : taskList) {
            editor.putString(task.getTaskId(),
                    task.getTaskName() + "|" +
                            task.getDueDateTime() + "|" +
                            task.getSnoozeCount());
        }

        editor.apply();
    }

    // Get upcoming tasks within one hour from the provided list
    public static List<Task> getUpcomingTasksWithinOneHour(List<Task> allTasks) {
        List<Task> filteredTasks = new ArrayList<>();

        Calendar currentTime = Calendar.getInstance();
        Calendar oneHourFromNow = Calendar.getInstance();
        oneHourFromNow.add(Calendar.HOUR_OF_DAY, 1);

        for (Task task : allTasks) {
            try {
                Date dueDate = customDateFormat.parse(task.getDueDateTime());

                if (dueDate.after(currentTime.getTime()) && dueDate.before(oneHourFromNow.getTime())) {
                    filteredTasks.add(task);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return filteredTasks;
    }
}