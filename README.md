# task-tracker-cli

A simple command-line interface (CLI) tool to track and manage your tasks.
Built with **pure Java**, using no external libraries. This project is part of the [roadmap.sh backend projects](https://roadmap.sh/projects/task-tracker).

## Features

- **Add** new tasks.
- **Update** and **Delete** tasks.
- **List** all tasks or filter by status (`todo`, `in-progress`, `done`).
- **Mark** tasks as in-progress or done.
- **Persistent Storage:** Tasks are saved in a JSON file (`data/tasks.json`).
- **Native Implementation:** Zero dependencies (No Jackson/Gson), custom JSON parser.

## Prerequisites

- Java Development Kit (JDK) 11 or higher.

## Installation & Usage

### 1. Clone the Repository
```bash
git clone https://github.com/icopoglu/task-tracker-cli.git
cd task-tracker-cli
```

### 2. Compile the Code
```bash
javac src/*.java
```

### 3. Run the Application
```bash
java -cp src TaskTracker add "Buy groceries"

// List tasks
java -cp src TaskTracker list
```

### 4. Create an Alias (Optional)
```bash
alias task-cli="java -cp src TaskTracker"
```

### Project Structure
```text
task-tracker-cli/
├── src/
│   ├── TaskTracker.java  # Main entry point (CLI logic)
│   ├── Task.java         # Data model
│   └── FileHandler.java  # File I/O and JSON parsing
├── data/                 # Stores tasks.json (Auto-generated at runtime)
├── .gitignore
└── README.md
```
### Command Referance

| Command | Usage | Description |
|---------|-------|-------------|
| `add` | `add <description>` | Create a new task. |
| `list` | `list` | List all tasks. |
| `list` | `list [status]` | Filter tasks by status (todo, done, in-progress). |
| `update` | `update <id> <desc>` | Update a task's description. |
| `delete` | `delete <id>` | Remove a task. |
| `mark-in-progress` | `mark-in-progress <id>` | Change status to IN_PROGRESS. |
| `mark-done` | `mark-done <id>` | Change status to DONE. |