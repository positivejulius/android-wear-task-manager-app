package com.app.androidweartaskmanagerapp.model;

// Task class representing a model for tasks
public class Task {

    // Properties of the Task class
    private String taskId;
    private String taskName;
    private String dueDateTime;
    private int snoozeCount; // New field to track snooze attempts

    // Constructor to initialize a Task object with provided values
    public Task(String taskId, String taskName, String dueDateTime) {
        this(taskId, taskName, dueDateTime, 0); // Default snooze count to 0
    }

    // Constructor with snooze count
    public Task(String taskId, String taskName, String dueDateTime, int snoozeCount) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.dueDateTime = dueDateTime;
        this.snoozeCount = snoozeCount;
    }

    // Getter and setter methods for existing fields
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(String dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    // New methods for snooze count
    public int getSnoozeCount() {
        return snoozeCount;
    }

    public void setSnoozeCount(int snoozeCount) {
        this.snoozeCount = snoozeCount;
    }

    public void incrementSnoozeCount() {
        this.snoozeCount++;
    }
}