package jonk;

import java.util.List;
import java.util.Scanner;

/**
 * Handles all interactions between Jonk and the user.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = """
             _  ___  _   _ _  __
                | |/ _ \\| \\ | | |/ /
             _  | | | | |  \\| | ' /\s
            | |_| | |_| | |\\  | . \\\s
             \\___/ \\___/|_| \\_|_|\\_\\""";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Jonk's banner and greeting.
     */
    public void showWelcome() {
        String greeting = "Hello! I'm Jonk.\nWhat can I do for you?";
        System.out.println("\t" + SEPARATOR + "\n\t" + BANNER + "\n\t" + greeting
                + "\n\t" + SEPARATOR);
    }

    /**
     * Reads the user's next complete command.
     *
     * @return Command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a separator between command responses.
     */
    public void showSeparator() {
        System.out.println("\t" + SEPARATOR);
    }

    /**
     * Displays every task with its one-based task number.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Displays tasks that match a find command.
     *
     * @param matchingTasks Tasks whose descriptions contain the search keyword.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println("Here are the matching tasks in your list:");
        showNumberedTasks(matchingTasks);
    }

    /**
     * Displays the supplied tasks with one-based numbers.
     *
     * @param tasks Tasks to display.
     */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("\t" + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task's completion status changed.
     *
     * @param task Task whose status changed.
     * @param isDone Whether the task is now complete.
     */
    public void showTaskStatusUpdated(Task task, boolean isDone) {
        if (isDone) {
            System.out.println("Nice! I've marked this task as done:");
        } else {
            System.out.println("OK, I've marked this task as not done yet:");
        }
        System.out.println("\t" + task);
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("\t" + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of tasks after the deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("\t" + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays a user-friendly error message.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Displays Jonk's farewell message.
     */
    public void showGoodbye() {
        String farewell = "Bye. Hope to see you again soon!";
        System.out.println("\t" + SEPARATOR + "\n\t" + farewell + "\n\t" + SEPARATOR);
    }
}
