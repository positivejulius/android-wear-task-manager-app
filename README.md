 Android Wear Task Manager

A lightweight, user-friendly task management application designed specifically for Android Wear OS. This application helps users manage their tasks efficiently on their smartwatch with features like task creation, scheduling, notifications, and more.

 Features

- **Task Creation**: Easily create tasks with a title and due date/time
- **Voice Input**: Use voice commands to create tasks hands-free
- **Task Management**: View, edit, and delete tasks directly from your wrist
- **Notifications**: Receive timely reminders for approaching tasks
- **Task Snoozing**: Snooze notifications for tasks you want to be reminded about later
- **Upcoming Tasks View**: Quickly see tasks due within the next hour

 Screenshots

*[Add screenshots of your application here]*

 Requirements

- Android Wear OS 2.0 or higher
- Android Studio 4.0+
- Gradle 7.0+

 Installation

1. Clone this repository
   ```
   git clone https://github.com/yourusername/android-wear-task-manager.git
   ```

2. Open the project in Android Studio

3. Connect your Android Wear device or use an emulator

4. Build and run the project
   ```
   ./gradlew installDebug
   ```

 Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/com/app/androidweartaskmanagerapp/
│       │   ├── activity/
│       │   │   ├── AddTaskActivity.java           Add/edit tasks
│       │   │   ├── ListTaskActivity.java          List all tasks
│       │   │   ├── NotificationdetailsActivity.java   View notification details
│       │   │   ├── UpcomingTasksActivity.java     View upcoming tasks
│       │   │   └── WearActionReceiver.java        Handle wear actions
│       │   ├── adapter/
│       │   │   └── TaskAdapter.java               Adapter for task list
│       │   ├── model/
│       │   │   └── Task.java                      Task data model
│       │   └── utils/
│       │       └── Constant.java                  Utility methods
│       └── res/
│           ├── drawable/
│           ├── layout/
│           │   ├── activity_add_task.xml
│           │   ├── activity_list_task.xml
│           │   ├── activity_notificationdetails.xml
│           │   ├── activity_upcoming_tasks.xml
│           │   └── task_item.xml
│           └── values/
│               ├── colors.xml
│               ├── dimens.xml
│               ├── strings.xml
│               └── theme.xml
```

 Usage Guide

 Creating a Task

1. Open the application on your smartwatch
2. Enter a task name using the keyboard or tap the microphone icon for voice input
3. Tap "Select Due Date" to set when the task is due
4. Choose a date and time from the pickers
5. Tap "Add Task" to save your task

 Managing Tasks

1. Tap "View Task List" to see all your tasks
2. Tap on any task to edit it
3. Tap the delete button on a task to remove it

 Notifications

- You'll receive notifications as tasks approach their due time
- You can tap on a notification to view task details
- You can snooze a task for 5 minutes by tapping the snooze action

 Data Storage

The application uses SharedPreferences to store task data locally on the device. Tasks are saved with the following information:
- Task ID (unique identifier)
- Task name
- Due date and time
- Snooze count (for notification management)

 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

