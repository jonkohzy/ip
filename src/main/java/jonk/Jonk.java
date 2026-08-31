package jonk;

import java.time.format.DateTimeParseException;

/**
 * Coordinates user interaction, command parsing, task management, and storage.
 */
public class Jonk {
    private static final String FILE_PATH = "./data/jonk.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a Jonk chatbot backed by the specified data file.
     *
     * @param filePath Path of the task data file.
     */
    public Jonk(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (JonkException e) {
            ui.showError(e.getMessage());
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     */
    public void run() {
        ui.showWelcome();

        String input = ui.readCommand();
        while (!input.equals("bye")) {
            ui.showSeparator();
            try {
                executeCommand(input);
            } catch (JonkException e) {
                ui.showError(e.getMessage());
            } catch (DateTimeParseException e) {
                ui.showError("Dates must be in yyyy-MM-dd format.");
            }
            ui.showSeparator();
            input = ui.readCommand();
        }

        ui.showGoodbye();
    }

    /**
     * Starts Jonk using its default task data file.
     *
     * @param args Command-line arguments, which Jonk does not use.
     */
    public static void main(String[] args) {
        new Jonk(FILE_PATH).run();
    }

    /**
     * Delegates one user command to the appropriate application operation.
     *
     * @param input Complete command entered by the user.
     * @throws JonkException If the command is invalid or a storage operation fails.
     */
    private void executeCommand(String input) throws JonkException {
        String commandWord = Parser.parseCommandWord(input);

        switch (commandWord) {
            case "list" -> {
                if (!input.equals("list")) {
                    throw new JonkException("Sorry, I don't know what that means");
                }
                ui.showTaskList(tasks.asList());
            }
            case "mark" -> updateTaskStatus(input, true);
            case "unmark" -> updateTaskStatus(input, false);
            case "todo", "deadline", "event" -> addTask(input);
            case "delete" -> deleteTask(input);
            case "find" -> findTasks(input);
            default -> throw new JonkException("Sorry, I don't know what that means");
        }
    }

    /**
     * Finds and displays tasks whose descriptions contain the supplied keyword.
     *
     * @param input Find command entered by the user.
     * @throws JonkException If the command does not contain a keyword.
     */
    private void findTasks(String input) throws JonkException {
        String keyword = Parser.parseKeyword(input);
        ui.showMatchingTasks(tasks.find(keyword));
    }

    /**
     * Updates a task's completion status and restores it if saving fails.
     *
     * @param input Mark or unmark command entered by the user.
     * @param isDone New completion status.
     * @throws JonkException If the task number is invalid or saving fails.
     */
    private void updateTaskStatus(String input, boolean isDone) throws JonkException {
        int taskNumber = Parser.parseTaskNumber(input);
        Task task = tasks.get(taskNumber);
        boolean previousStatus = task.isDone();

        setTaskStatus(task, isDone);
        try {
            storage.save(tasks.asList());
        } catch (JonkException e) {
            setTaskStatus(task, previousStatus);
            throw e;
        }

        ui.showTaskStatusUpdated(task, isDone);
    }

    /**
     * Adds a parsed task and removes it again if saving fails.
     *
     * @param input Add-task command entered by the user.
     * @throws JonkException If the command is invalid or saving fails.
     */
    private void addTask(String input) throws JonkException {
        Task newTask = Parser.parseTask(input);
        tasks.add(newTask);

        try {
            storage.save(tasks.asList());
        } catch (JonkException e) {
            tasks.removeLast();
            throw e;
        }

        ui.showTaskAdded(newTask, tasks.size());
    }

    /**
     * Deletes a task and restores it to its former position if saving fails.
     *
     * @param input Delete command entered by the user.
     * @throws JonkException If the task number is invalid or saving fails.
     */
    private void deleteTask(String input) throws JonkException {
        int taskNumber = Parser.parseTaskNumber(input);
        Task removedTask = tasks.delete(taskNumber);

        try {
            storage.save(tasks.asList());
        } catch (JonkException e) {
            tasks.restore(taskNumber, removedTask);
            throw e;
        }

        ui.showTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Applies a completion status using the behavior provided by {@link Task}.
     *
     * @param task Task whose status should change.
     * @param isDone Whether the task should be complete.
     */
    private void setTaskStatus(Task task, boolean isDone) {
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
    }
}
