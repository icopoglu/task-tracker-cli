import java.util.List;

/**
 * TaskTracker - CLI interface for managing tasks. This class serves as the main entry point and
 * handles user commands. It acts as the steering wheel that controls the FileHandler engine.
 */
public class TaskTracker {
  private static FileHandler fileHandler = new FileHandler();

  /**
   * Main entry point for the Task Tracker CLI application.
   *
   * @param args Command-line arguments where args[0] is the command (add, list, delete, update)
   */
  public static void main(String[] args) {
    // Argument check: display help if no command provided
    if (args.length < 1) {
      displayHelp();
      return;
    }

    // Load existing tasks from disk
    List<Task> tasks = fileHandler.loadTasks();

    // Parse and execute the command
    String command = args[0].toLowerCase();

    switch (command) {
      case "add":
        handleAdd(tasks, args);
        break;
      case "list":
        // Check if a status filter was provided
        String filter = args.length > 1 ? args[1].toLowerCase() : null;
        handleList(tasks, filter);
        break;
      case "delete":
        handleDelete(tasks, args);
        break;
      case "update":
        handleUpdate(tasks, args);
        break;
      case "mark-in-progress":
        handleMarkInProgress(tasks, args);
        break;
      case "mark-done":
        handleMarkDone(tasks, args);
        break;
      default:
        System.out.println("Unknown command: " + command);
        displayHelp();
    }
  }

  /**
   * Handles the ADD command: creates a new task and saves it to disk.
   *
   * @param tasks The current list of tasks in memory.
   * @param args Command-line arguments where args[1+] is the task description.
   */
  private static void handleAdd(List<Task> tasks, String[] args) {
    // Validate that a description was provided
    if (args.length < 2) {
      System.out.println("Error: Please provide a task description.");
      return;
    }

    // Build description from all arguments after "add"
    StringBuilder description = new StringBuilder();
    for (int i = 1; i < args.length; i++) {
      description.append(args[i]);
      if (i < args.length - 1) {
        description.append(" ");
      }
    }

    // Create a new task with the next available ID
    int newId = tasks.isEmpty() ? 1 : tasks.get(tasks.size() - 1).getId() + 1;
    Task newTask = new Task(newId, description.toString());
    tasks.add(newTask);

    // Save the updated list to disk
    fileHandler.saveTasks(tasks);
    System.out.println("Task added: [" + newId + "] " + description);
  }

  /**
   * Handles the LIST command: displays all tasks currently in the system. Optionally filters tasks
   * by status.
   *
   * @param tasks The current list of tasks in memory.
   * @param statusFilter Optional status filter ("todo", "in-progress", "done").
   */
  private static void handleList(List<Task> tasks, String statusFilter) {
    if (tasks.isEmpty()) {
      System.out.println("No tasks found.");
      return;
    }

    // Filter tasks by status if a filter was provided
    List<Task> displayTasks = tasks;
    if (statusFilter != null) {
      displayTasks = new java.util.ArrayList<>();
      for (Task task : tasks) {
        String taskStatus = task.getStatus().name().toLowerCase().replace("_", "-");
        if (taskStatus.equals(statusFilter)) {
          displayTasks.add(task);
        }
      }

      if (displayTasks.isEmpty()) {
        System.out.println("No tasks found with status: " + statusFilter);
        return;
      }
    }

    System.out.println("\n--- Tasks ---");
    for (Task task : displayTasks) {
      System.out.println("[" + task.getId() + "] " + task.getDescription() + " - Status: "
          + task.getStatus() + " (Created: " + task.getCreatedAt() + ")");
    }
    System.out.println();
  }

  /**
   * Handles the DELETE command: removes a task by ID and saves changes to disk.
   *
   * @param tasks The current list of tasks in memory.
   * @param args Command-line arguments where args[1] is the task ID to delete.
   */
  private static void handleDelete(List<Task> tasks, String[] args) {
    // Validate that an ID was provided
    if (args.length < 2) {
      System.out.println("Error: Please provide a task ID to delete.");
      return;
    }

    try {
      int idToDelete = Integer.parseInt(args[1]);

      // Find the task with the given ID
      Task foundTask = null;
      for (Task task : tasks) {
        if (task.getId() == idToDelete) {
          foundTask = task;
          break;
        }
      }

      // Check if task was found
      if (foundTask == null) {
        System.out.println("Task not found with ID: " + idToDelete);
        return;
      }

      // Remove the task and save to disk
      tasks.remove(foundTask);
      fileHandler.saveTasks(tasks);
      System.out.println("Task deleted: [" + idToDelete + "] " + foundTask.getDescription());

    } catch (NumberFormatException e) {
      System.out.println("Error: Invalid task ID. Please provide a numeric ID.");
    }
  }

  /**
   * Handles the UPDATE command: modifies a task's description by ID and saves changes to disk.
   *
   * @param tasks The current list of tasks in memory.
   * @param args Command-line arguments where args[1] is the task ID and args[2+] is the new
   *        description.
   */
  private static void handleUpdate(List<Task> tasks, String[] args) {
    // Validate that an ID and new description were provided
    if (args.length < 3) {
      System.out.println("Error: Please provide a task ID and new description.");
      return;
    }

    try {
      int idToUpdate = Integer.parseInt(args[1]);

      // Build the new description from all arguments after the ID
      StringBuilder newDescription = new StringBuilder();
      for (int i = 2; i < args.length; i++) {
        newDescription.append(args[i]);
        if (i < args.length - 1) {
          newDescription.append(" ");
        }
      }

      // Find the task with the given ID
      Task foundTask = null;
      for (Task task : tasks) {
        if (task.getId() == idToUpdate) {
          foundTask = task;
          break;
        }
      }

      // Check if task was found
      if (foundTask == null) {
        System.out.println("Task not found with ID: " + idToUpdate);
        return;
      }

      // Update the task and save to disk
      foundTask.setDescription(newDescription.toString());
      fileHandler.saveTasks(tasks);
      System.out.println("Task updated: [" + idToUpdate + "] " + newDescription);

    } catch (NumberFormatException e) {
      System.out.println("Error: Invalid task ID. Please provide a numeric ID.");
    }
  }

  /**
   * Handles the MARK-IN-PROGRESS command: sets a task's status to IN_PROGRESS.
   *
   * @param tasks The current list of tasks in memory.
   * @param args Command-line arguments where args[1] is the task ID.
   */
  private static void handleMarkInProgress(List<Task> tasks, String[] args) {
    // Validate that an ID was provided
    if (args.length < 2) {
      System.out.println("Error: Please provide a task ID.");
      return;
    }

    try {
      int idToUpdate = Integer.parseInt(args[1]);

      // Find the task with the given ID
      Task foundTask = null;
      for (Task task : tasks) {
        if (task.getId() == idToUpdate) {
          foundTask = task;
          break;
        }
      }

      // Check if task was found
      if (foundTask == null) {
        System.out.println("Task not found with ID: " + idToUpdate);
        return;
      }

      // Update status and save to disk
      foundTask.setStatus(Task.Status.IN_PROGRESS);
      fileHandler.saveTasks(tasks);
      System.out.println(
          "Task marked as in-progress: [" + idToUpdate + "] " + foundTask.getDescription());

    } catch (NumberFormatException e) {
      System.out.println("Error: Invalid task ID. Please provide a numeric ID.");
    }
  }

  /**
   * Handles the MARK-DONE command: sets a task's status to DONE.
   *
   * @param tasks The current list of tasks in memory.
   * @param args Command-line arguments where args[1] is the task ID.
   */
  private static void handleMarkDone(List<Task> tasks, String[] args) {
    // Validate that an ID was provided
    if (args.length < 2) {
      System.out.println("Error: Please provide a task ID.");
      return;
    }

    try {
      int idToUpdate = Integer.parseInt(args[1]);

      // Find the task with the given ID
      Task foundTask = null;
      for (Task task : tasks) {
        if (task.getId() == idToUpdate) {
          foundTask = task;
          break;
        }
      }

      // Check if task was found
      if (foundTask == null) {
        System.out.println("Task not found with ID: " + idToUpdate);
        return;
      }

      // Update status and save to disk
      foundTask.setStatus(Task.Status.DONE);
      fileHandler.saveTasks(tasks);
      System.out.println("Task marked as done: [" + idToUpdate + "] " + foundTask.getDescription());

    } catch (NumberFormatException e) {
      System.out.println("Error: Invalid task ID. Please provide a numeric ID.");
    }
  }

  /**
   * Displays the help menu with usage instructions and examples.
   */
  private static void displayHelp() {
    System.out.println("\n==== Task Tracker CLI ====");
    System.out.println("Usage: java TaskTracker <command> [arguments]\n");
    System.out.println("Commands:");
    System.out.println("  add <description>              - Add a new task");
    System.out.println("  list [status]                  - List all tasks or filter by status");
    System.out.println("  delete <id>                    - Delete a task by ID");
    System.out.println("  update <id> <description>      - Update a task's description");
    System.out.println("  mark-in-progress <id>          - Mark a task as in-progress");
    System.out.println("  mark-done <id>                 - Mark a task as done\n");
    System.out.println("Status filters: todo, in-progress, done\n");
    System.out.println("Examples:");
    System.out.println("  java TaskTracker add Buy groceries");
    System.out.println("  java TaskTracker list");
    System.out.println("  java TaskTracker list done");
    System.out.println("  java TaskTracker mark-in-progress 1");
    System.out.println("  java TaskTracker mark-done 1");
    System.out.println("  java TaskTracker delete 1");
    System.out.println("  java TaskTracker update 1 Buy milk and bread\n");
  }
}
