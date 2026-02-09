import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Handles all file-related operations for the Task Tracker application, including reading and
 * writing tasks to a JSON file. This class abstracts away the file I/O logic from the main
 * application flow, ensuring separation of concerns and easier maintenance.
 */
public class FileHandler {

  // Constants for file paths and names
  private static final String DIRECTORY_PATH = "data";
  private static final String FILE_NAME = "tasks.json";
  // Full path to the tasks file
  private static final Path FILE_PATH = Paths.get(DIRECTORY_PATH, FILE_NAME);

  /**
   * Ensures that the data directory exists.
   */
  private static void ensureDataDirectoryExists() {
    try {
      java.nio.file.Files.createDirectories(FILE_PATH.getParent());
    } catch (java.io.IOException e) {
      System.err.println("Error creating data directory: " + e.getMessage());
    }
  }

  /**
   * Saves the list of tasks to a JSON file.
   *
   * @param tasks The list of Task objects to be saved.
   */
  public void saveTasks(java.util.List<Task> tasks) {
    try {
      // If the directory doesn't exist, create it
      ensureDataDirectoryExists();

      // If there are no tasks, write an empty JSON array to the file
      if (tasks.isEmpty()) {
        Files.writeString(FILE_PATH, "[]");
        return;
      }

      // Create a JSON array string from the list of tasks
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      for (int i = 0; i < tasks.size(); i++) {
        sb.append(tasks.get(i).toJson());
        // Son eleman değilse virgül ekle
        if (i < tasks.size() - 1) {
          sb.append(",");
        }
      }
      sb.append("]");

      // Write the JSON string to the file, creating or overwriting as needed
      Files.writeString(FILE_PATH, sb.toString(), StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);

    } catch (IOException e) {
      System.err.println("Error saving tasks: " + e.getMessage());
    }
  }

  /**
   * Loads the list of tasks from the JSON file.
   *
   * @return A list of Task objects loaded from the file, or an empty list if the file is empty or
   *         doesn't exist.
   */
  public java.util.List<Task> loadTasks() {
    java.util.List<Task> tasks = new java.util.ArrayList<>();

    try {
      // Check if file exists
      if (!Files.exists(FILE_PATH)) {
        return tasks; // Return empty list if file doesn't exist
      }

      // Read the entire file as a single string
      String jsonContent = Files.readString(FILE_PATH);

      // Handle empty array
      if (jsonContent.trim().equals("[]")) {
        return tasks;
      }

      // Remove opening and closing brackets
      jsonContent = jsonContent.trim();
      if (jsonContent.startsWith("[")) {
        jsonContent = jsonContent.substring(1);
      }
      if (jsonContent.endsWith("]")) {
        jsonContent = jsonContent.substring(0, jsonContent.length() - 1);
      }

      // Split by "},{" to separate individual task objects
      String[] taskStrings = jsonContent.split("\\}\\s*,\\s*\\{");

      // Reconstruct and parse each task
      for (String taskStr : taskStrings) {
        // Add back the curly braces if they were removed by split
        if (!taskStr.startsWith("{")) {
          taskStr = "{" + taskStr;
        }
        if (!taskStr.endsWith("}")) {
          taskStr = taskStr + "}";
        }

        // Parse the JSON object and create a Task
        try {
          int id = Integer.parseInt(extractValue(taskStr, "id"));
          String description = extractValue(taskStr, "description");
          // Unescape quotes in description
          description = description.replace("\\\"", "\"");
          String statusStr = extractValue(taskStr, "status");
          Task.Status status = Task.Status.valueOf(statusStr);
          String createdAtStr = extractValue(taskStr, "createdAt");
          String updatedAtStr = extractValue(taskStr, "updatedAt");

          java.time.LocalDateTime createdAt = java.time.LocalDateTime.parse(createdAtStr);
          java.time.LocalDateTime updatedAt = java.time.LocalDateTime.parse(updatedAtStr);

          Task task = new Task(id, description, status, createdAt, updatedAt);
          tasks.add(task);
        } catch (Exception e) {
          System.err.println("Error parsing task JSON: " + e.getMessage());
        }
      }
    } catch (IOException e) {
      System.err.println("Error loading tasks: " + e.getMessage());
    }

    return tasks;
  }

  /**
   * Extracts a value associated with a key from a JSON object string.
   *
   * @param json The JSON object string.
   * @param key The key whose value should be extracted.
   * @return The value as a string, or null if the key is not found.
   */
  private String extractValue(String json, String key) {
    String searchKey = "\"" + key + "\":\"";
    int startIndex = json.indexOf(searchKey);

    if (startIndex == -1) {
      // Key might not have a quoted value (e.g., numbers, booleans)
      searchKey = "\"" + key + "\":";
      startIndex = json.indexOf(searchKey);
      if (startIndex == -1) {
        return null;
      }

      // Extract unquoted value (number, boolean, etc.)
      int valueStart = startIndex + searchKey.length();
      int valueEnd = json.indexOf(",", valueStart);
      if (valueEnd == -1) {
        valueEnd = json.indexOf("}", valueStart);
      }
      return json.substring(valueStart, valueEnd).trim();
    }

    // For quoted values
    int valueStart = startIndex + searchKey.length();
    int valueEnd = valueStart;

    // Find the closing quote, handling escaped quotes
    while (valueEnd < json.length()) {
      if (json.charAt(valueEnd) == '"' && json.charAt(valueEnd - 1) != '\\') {
        break;
      }
      valueEnd++;
    }

    return json.substring(valueStart, valueEnd);
  }
}
