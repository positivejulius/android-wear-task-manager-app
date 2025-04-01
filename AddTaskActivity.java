package com.app.androidweartaskmanagerapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.androidweartaskmanagerapp.R;
import com.app.androidweartaskmanagerapp.databinding.ActivityAddTaskBinding;
import com.app.androidweartaskmanagerapp.model.Task;
import com.app.androidweartaskmanagerapp.utils.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener {

    // View binding
    ActivityAddTaskBinding binding;

    // List to store tasks
    List<Task> list = new ArrayList<>();

    // Calendar to manage date and time
    private final Calendar selectedDateTime = Calendar.getInstance();

    // Speech recognition request code
    private static final int SPEECH_REQUEST_CODE = 123;

    // Notification channel ID
    private static final String CHANNEL_ID = "CHANNEL_ID";

    // Interval for periodic checks in milliseconds
    private static final int CHECK_NOTIFICATIONS_INTERVAL = 60 * 1000;

    // For editing existing task
    private String editingTaskId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if editing an existing task
        Intent intent = getIntent();
        if (intent != null) {
            editingTaskId = intent.getStringExtra("EDIT_TASK_ID");
            if (editingTaskId != null) {
                loadTaskForEditing(editingTaskId);
            }
        }

        // Initialize the activity
        init();
    }

    // Load task details for editing
    private void loadTaskForEditing(String taskId) {
        List<Task> tasks = Constant.getAllTasks(this);
        for (Task task : tasks) {
            if (task.getTaskId().equals(taskId)) {
                binding.editTextTaskName.setText(task.getTaskName());

                try {
                    // Parse the existing due date
                    Date existingDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
                            .parse(task.getDueDateTime());

                    // Set the calendar to the existing date
                    selectedDateTime.setTime(existingDate);

                    // Enable the add task button
                    binding.buttonAddTask.setEnabled(true);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    // Initialize activity components
    private void init() {
        bindView(); // Bind views
        createNotificationChannel(); // Create notification channel
        schedulePeriodicChecks(); // Schedule periodic checks for approaching tasks
    }

    // Bind views and set listeners
    private void bindView() {
        // Disable the "Add Task" button initially
        binding.buttonAddTask.setEnabled(false);

        // Set click listeners for buttons
        binding.buttonAddTask.setOnClickListener(this);
        binding.buttonDueDate.setOnClickListener(this);
        binding.buttonViewTaskList.setOnClickListener(this);
        binding.microphoneIcon.setOnClickListener(this);
    }

    // Modify the initTask method to support editing
    private void initTask() {
        // Get task name from the EditText
        String text = binding.editTextTaskName.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            Task task;
            if (editingTaskId != null) {
                // Update existing task
                task = createTask(editingTaskId, text, selectedDateTime.getTime().toString());
            } else {
                // Create a new task
                task = createTask(null, text, selectedDateTime.getTime().toString());
            }

            updateTask(task);
            Toast.makeText(AddTaskActivity.this,
                    editingTaskId != null ? R.string.task_updated : R.string.task_saved,
                    Toast.LENGTH_SHORT).show();

            // Clear the EditText and reset editing state
            binding.editTextTaskName.setText("");
            binding.buttonAddTask.setEnabled(false);
            editingTaskId = null;

            // Optional: Finish the activity or return to task list
            finish();
        } else {
            // Display a message if the task name is empty
            Toast.makeText(AddTaskActivity.this, R.string.please_set_due_date_time, Toast.LENGTH_SHORT).show();
        }
    }

    // Update task in the list and save to storage
    private void updateTask(Task task) {
        Constant.saveTask(task, this);
        list.addAll(Constant.getAllTasks(this));
    }

    // Create a new task with the given parameters
    private Task createTask(String id, String task, String duetime) {
        if (id == null) {
            id = String.valueOf(System.currentTimeMillis());
        }
        return new Task(id, task, duetime);
    }

    // Create a notification channel for Android Oreo and higher
    private void createNotificationChannel() {
        // Check for Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel
            String channelName = getString(R.string.channel_name);
            String channelDescription = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Handle button clicks
    @Override
    public void onClick(View view) {
        // Check the ID of the clicked view
        if (view.getId() == R.id.buttonAddTask) {
            initTask(); // Initialize the task
        }
        if (view.getId() == R.id.buttonDueDate) {
            showDateTimePicker();
        }
        if (view.getId() == R.id.buttonViewTaskList) {
            startActivity(new Intent(AddTaskActivity.this, ListTaskActivity.class));
        }
        if (view.getId() == R.id.microphoneIcon) {
            startVoiceInput();
        }
    }

    public static void sendNotification(Context context, Task task) {
        // Check if task has exceeded max snoozes
        if (task.getSnoozeCount() >= 5) {
            return; // Stop notifying
        }

        long randomNumber = System.currentTimeMillis();
        int requestCode = (int) randomNumber;

        // Create main notification intent
        Intent intent = new Intent(context, NotificationdetailsActivity.class);
        intent.putExtra("TaskID", task.getTaskId());
        intent.putExtra("TaskName", task.getTaskName());
        intent.putExtra("FROM_NOTIFICATION", true);

        // Create a task-specific pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create a snooze intent that will be handled directly in the notification details activity
        Intent snoozeIntent = new Intent(context, NotificationdetailsActivity.class);
        snoozeIntent.putExtra("TaskID", task.getTaskId());
        snoozeIntent.putExtra("SNOOZE_ACTION", true);
        snoozeIntent.putExtra("FROM_NOTIFICATION", true);

        // Create a pending intent for the snooze action
        PendingIntent snoozePendingIntent = PendingIntent.getActivity(
                context,
                requestCode + 1,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle(context.getString(R.string.do_not_forget));
        builder.setContentText(task.getTaskName());
        builder.setSmallIcon(R.drawable.baseline_notifications_24);
        builder.setContentIntent(pendingIntent);

        // Add snooze action directly to the notification
        builder.addAction(
                R.drawable.baseline_notifications_24,
                context.getString(R.string.snooze_5_minutes),
                snoozePendingIntent
        );

        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 5);
            return;
        }
        managerCompat.notify(2, builder.build());
    }

    // Start voice input for speech recognition
    private void startVoiceInput() {
        // Intent to start speech recognition
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your task name");
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // Show date and time picker dialogs
    private void showDateTimePicker() {
        // DatePickerDialog logic
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Show TimePickerDialog after selecting the date
                        showTimePicker();
                    }
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Handle the result of voice recognition
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String voiceInput = matches.get(0);
                binding.editTextTaskName.setText(String.valueOf(voiceInput));
            }
        }
    }

    private void schedulePeriodicChecks() {
        // AlarmManager logic for periodic checks
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, WearActionReceiver.class);
        intent.setAction(getString(R.string.check_notifications));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long currentTimeMillis = System.currentTimeMillis();
        long triggerTimeMillis = currentTimeMillis + CHECK_NOTIFICATIONS_INTERVAL;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Use setExactAndAllowWhileIdle on Android M and later for exact timing
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
        }
    }

    private static boolean isTaskDueApproaching(Task task, int approachingTimeInMinutes) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            Date dueDate = sdf.parse(task.getDueDateTime());
            Calendar dueCalendar = Calendar.getInstance();
            dueCalendar.setTime(dueDate);

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.add(Calendar.MINUTE, approachingTimeInMinutes);

            return currentCalendar.after(dueCalendar);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check and trigger notifications for approaching tasks
    public static void checkTriggerApproachingTaskNotifications(Context context) {
        List<Task> list = Constant.getAllTasks(context);
        for (Task task : list) {
            if (isTaskDueApproaching(task, 1)) { // 1 minute approaching time
                sendNotification(context, task);
            }
        }
    }

    // Show time picker dialog
    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDateTime.set(Calendar.MINUTE, minute);

                        binding.buttonAddTask.setEnabled(true);
                    }
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true // 24-hour format
        );
        timePickerDialog.show();
    }
}