import java.time.LocalDateTime;

/**
 * Represents a single task in the Task Tracker system. This class serves as the data model, holding
 * the state and validation logic for tasks.
 */
public class Task {

  private int id;
  private String description;
  private Status status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  /**
   * Defines the possible lifecycle states of a task.
   */
  public enum Status {
    /** Task is created but not started. */
    TODO,
    /** Task is currently being worked on. */
    IN_PROGRESS,
    /** Task is completed. */
    DONE
  }

  // --- CONSTRUCTORS ---

  /**
   * Creates a new task with a given description. The status is set to TODO and timestamps are set
   * to the current time by default.
   *
   * @param id The unique identifier for the task.
   * @param description The details of the task (cannot be null or empty).
   */
  public Task(int id, String description) {
    setId(id);
    setDescription(description);

    this.status = Status.TODO; // Varsayılan

    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  /**
   * Reconstructs an existing task from storage (e.g., JSON file). Used when loading data to
   * preserve original timestamps and status.
   *
   * @param id The unique identifier.
   * @param description The task description.
   * @param status The current status of the task.
   * @param createdAt The original creation timestamp.
   * @param updatedAt The last update timestamp.
   */
  public Task(int id, String description, Status status, LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.description = description;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // --- GETTER METHODS ---

  /**
   * Gets the unique ID of the task.
   *
   * @return The task ID.
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the description of the task.
   *
   * @return The description text.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the current status of the task.
   *
   * @return The status enum (TODO, IN_PROGRESS, DONE).
   */
  public Status getStatus() {
    return status;
  }

  /**
   * Gets the date and time when the task was created.
   *
   * @return The creation timestamp.
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Gets the date and time when the task was last updated.
   *
   * @return The last update timestamp.
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  // --- SETTER METHODS ---

  /**
   * Sets the ID of the task.
   *
   * @param id The unique identifier.
   */
  public void setId(int id) {
    if (id < 0) {
      throw new IllegalArgumentException("ID cannot be negative");
    }
    this.id = id;
  }

  /**
   * Sets the description of the task and updates the 'updatedAt' timestamp.
   *
   * @param description The new description (cannot be null or empty).
   * @throws IllegalArgumentException if the description is null or empty.
   */
  public void setDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("Description cannot be empty");
    }
    this.description = description;

    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Sets the status of the task and updates the 'updatedAt' timestamp.
   *
   * @param status The new status (cannot be null).
   * @throws IllegalArgumentException if the status is null.
   */
  public void setStatus(Status status) {
    if (status == null) {
      throw new IllegalArgumentException("Status cannot be null");
    }
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  /**
   * Converts the task to a JSON string representation.
   *
   * @return A JSON string representation of this task.
   */
  public String toJson() {
    String jsonTemplate = "{\"id\":%d,\"description\":\"%s\",\"status\":\"%s\","
        + "\"createdAt\":\"%s\"," + "\"updatedAt\":\"%s\"}";

    return String.format(jsonTemplate, id, description.replace("\"", "\\\""), status.name(),
        createdAt, updatedAt);
  }
}
