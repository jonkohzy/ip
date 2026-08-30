package jonk;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Runs the Jonk chatbot and manages its task list and persistent storage.
 */
public class Jonk {
    private static final String FILE_PATH = "./data/jonk.txt";

    /**
     * Starts the chatbot, accepts commands, and displays responses until the user exits.
     *
     * @param args Command-line arguments, which Jonk does not use.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks;
        try {
            tasks = loadTasks();
        } catch (JonkException e) {
            System.out.println(e.getMessage());
            tasks = new ArrayList<>();
        }

        String separator = "____________________________________________________________";
        String botName = "Jonk";
        String greeting = "Hello! I'm " + botName + ".\nWhat can I do for you?";
        String farewell = "Bye. Hope to see you again soon!";
        String banner = """
                 _  ___  _   _ _  __
                    | |/ _ \\| \\ | | |/ /
                 _  | | | | |  \\| | ' /\s
                | |_| | |_| | |\\  | . \\\s
                 \\___/ \\___/|_| \\_|_|\\_\\""";

        System.out.println("\t" + separator + "\n\t" + banner + "\n\t" + greeting + "\n\t" + separator);

        String input = scanner.nextLine();
        while (!input.equals("bye")) {
            System.out.println("\t" + separator);

            if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println("\t" + (i + 1) + "." + tasks.get(i));
                }
            } else {
                try {
                    // Extract the task type.
                    String[] parts = input.trim().split("\\s+", 2);

                    switch (parts[0]) {
                        case "mark", "unmark" -> {
                            if (parts.length != 2) {
                                throw new JonkException("Please provide exactly one task number.");
                            }
                            int taskIndex = getTaskIndex(parts, tasks.size());
                            Task task = tasks.get(taskIndex);
                            boolean wasDone = task.isDone();

                            if (parts[0].equals("mark")) {
                                task.markAsDone();
                            } else {
                                task.markAsUndone();
                            }

                            try {
                                saveTasks(tasks);
                            } catch (JonkException e) {
                                if (wasDone) {
                                    task.markAsDone();
                                } else {
                                    task.markAsUndone();
                                }
                                throw e;
                            }

                            if (parts[0].equals("mark")) {
                                System.out.println("Nice! I've marked this task as done:");
                                System.out.println("\t" + task);
                            } else {
                                System.out.println("OK, I've marked this task as not done yet:");
                                System.out.println("\t" + task);
                            }

                        }
                        case "todo", "deadline", "event" -> {
                            if (parts.length < 2 || parts[1].isBlank()) {
                                if (parts[0].equals("todo")) {
                                    throw new JonkException("A todo must have a non-empty description.");
                                } else if (parts[0].equals("deadline")) {
                                    throw new JonkException("A deadline must have a non-empty /by value.");
                                } else {
                                    throw new JonkException("An event must have non-empty /from and /to values.");
                                }
                            }

                            // Split the details into the description and command details.
                            String[] details = parts[1].trim().split("\\s+(?=/)");

                            Task newTask;
                            if (parts[0].equals("todo")) {
                                if (details[0].isBlank()) {
                                    throw new JonkException("A todo must have a non-empty description.");
                                }
                                newTask = new Todo(details[0]);
                            } else if (parts[0].equals("deadline")) {
                                if (details.length != 2) {
                                    throw new JonkException("A deadline must have a non-empty /by value.");
                                }

                                String[] byDetails = details[1].trim().split("\\s+", 2);
                                if (byDetails.length != 2
                                        || !byDetails[0].equals("/by")
                                        || byDetails[1].isBlank()) {
                                    throw new JonkException("A deadline must have a non-empty /by value.");
                                }

                                String description = details[0];
                                String by = byDetails[1].trim();
                                newTask = new Deadline(description, by);
                            } else {
                                if (details.length != 3) {
                                    throw new JonkException("An event must have non-empty /from and /to values.");
                                }

                                String[] fromDetails = details[1].trim().split("\\s+", 2);
                                String[] toDetails = details[2].trim().split("\\s+", 2);
                                boolean hasValidFrom = fromDetails.length == 2
                                        && fromDetails[0].equals("/from")
                                        && !fromDetails[1].isBlank();
                                boolean hasValidTo = toDetails.length == 2
                                        && toDetails[0].equals("/to")
                                        && !toDetails[1].isBlank();

                                if (!hasValidFrom || !hasValidTo) {
                                    throw new JonkException("An event must have non-empty /from and /to values.");
                                }

                                String description = details[0];
                                String from = fromDetails[1].trim();
                                String to = toDetails[1].trim();
                                newTask = new Event(description, from, to);
                            }

                            tasks.add(newTask);
                            try {
                                saveTasks(tasks);
                            } catch (JonkException e) {
                                tasks.removeLast();
                                throw e;
                            }
                            System.out.println("Got it. I've added this task:");
                            System.out.println("\t" + newTask);
                            System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                        }
                        case "delete" -> {
                            if (parts.length != 2) {
                                throw new JonkException("Please provide exactly one task number.");
                            }
                            int taskIndex = getTaskIndex(parts, tasks.size());

                            Task removedTask = tasks.remove(taskIndex);
                            try {
                                saveTasks(tasks);
                            } catch (JonkException e) {
                                tasks.add(taskIndex, removedTask);
                                throw e;
                            }
                            System.out.println("Noted. I've removed this task:");
                            System.out.println("\t" + removedTask);
                            System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                        }
                        default -> throw new JonkException("Sorry, I don't know what that means");
                    }
                } catch (JonkException e) {
                    System.out.println(e.getMessage());
                } catch (DateTimeParseException e) {
                    System.out.println("Dates must be in yyyy-MM-dd format.");
                }
            }

            System.out.println("\t" + separator);
            input = scanner.nextLine();
        }

        System.out.println("\t" + separator + "\n\t" + farewell + "\n\t" + separator);
    }

    /**
     * Returns the zero-based index from a command containing a one-based task number.
     *
     * @param parts Command parts containing the operation and task number.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based task index.
     * @throws JonkException If the task number does not exist or is not a whole number.
     */
    private static int getTaskIndex(String[] parts, int taskCount) throws JonkException {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new JonkException("The task number must be a whole number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new JonkException("That task number does not exist.");
        }

        return taskNumber - 1;
    }

    /**
     * Rewrites the data file so it always represents the current task list.
     *
     * @param tasks Current tasks to save.
     * @throws JonkException If the data directory or file cannot be written.
     */
    private static void saveTasks(ArrayList<Task> tasks) throws JonkException {
        Path filePath = Path.of(FILE_PATH);
        Path temporaryFile = null;

        try {
            Files.createDirectories(filePath.getParent());
            temporaryFile = Files.createTempFile(filePath.getParent(), "jonk-", ".tmp");
            Files.write(temporaryFile, tasks.stream().map(Task::toFileString).toList(),
                    StandardCharsets.UTF_8);

            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            deleteTemporaryFile(temporaryFile);
            throw new JonkException("Could not save tasks to " + FILE_PATH
                    + ". Your latest change was not applied.", e);
        }
    }

    /**
     * Removes a temporary save file after a failed write without hiding the original error.
     *
     * @param temporaryFile Temporary file to remove, or {@code null} if none was created.
     */
    private static void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // The original save error is more useful to the user than a cleanup error.
        }
    }

    /**
     * Loads all tasks from the data file, or returns an empty list if no file exists yet.
     *
     * @return Tasks stored during the previous run.
     * @throws JonkException If the data file cannot be read or contains invalid task data.
     */
    private static ArrayList<Task> loadTasks() throws JonkException {
        ArrayList<Task> tasks = new ArrayList<>();
        Path filePath = Path.of(FILE_PATH);

        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException e) {
            throw new JonkException("Could not load tasks from " + FILE_PATH
                    + ". Starting with an empty task list.", e);
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(parseTask(line));
            } catch (JonkException | DateTimeParseException e) {
                throw new JonkException("Could not load tasks from " + FILE_PATH
                        + ": invalid data at line " + (i + 1) + " (" + e.getMessage()
                        + "). Starting with an empty task list.", e);
            }
        }
        return tasks;
    }

    /**
     * Recreates one task from its pipe-separated data-file representation.
     *
     * @param line Saved task data.
     * @return Recreated task.
     * @throws JonkException If any saved field is missing or invalid.
     */
    private static Task parseTask(String line) throws JonkException {
        List<String> fields = splitFileFields(line);
        String taskType = fields.getFirst();
        int expectedFieldCount = switch (taskType) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new JonkException("unknown task type '" + taskType + "'");
        };

        if (fields.size() != expectedFieldCount) {
            throw new JonkException("task type " + taskType + " requires " + expectedFieldCount
                    + " fields, but found " + fields.size());
        }

        String status = fields.get(1);
        if (!status.equals("0") && !status.equals("1")) {
            throw new JonkException("completion status must be 0 or 1");
        }

        for (int i = 2; i < fields.size(); i++) {
            if (fields.get(i).isBlank()) {
                throw new JonkException("task details cannot be empty");
            }
        }

        Task task = switch (taskType) {
            case "T" -> new Todo(fields.get(2));
            case "D" -> new Deadline(fields.get(2), fields.get(3));
            case "E" -> new Event(fields.get(2), fields.get(3), fields.get(4));
            default -> throw new AssertionError("Task type was validated above");
        };

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits saved data at unescaped pipe characters and restores escaped text.
     *
     * @param line Saved task data.
     * @return Decoded fields without separator padding.
     */
    private static List<String> splitFileFields(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '\\' && i + 1 < line.length()
                    && (line.charAt(i + 1) == '\\' || line.charAt(i + 1) == '|')) {
                currentField.append(line.charAt(++i));
            } else if (current == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(current);
            }
        }

        fields.add(currentField.toString().trim());
        return fields;
    }
}
