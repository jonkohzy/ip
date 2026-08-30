package jonk;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the user's tasks and provides operations on the task list.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Tasks to initially store.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes the task with the specified one-based task number.
     *
     * @param taskNumber One-based number of the task to delete.
     * @return Deleted task.
     * @throws JonkException If the task number does not exist.
     */
    public Task delete(int taskNumber) throws JonkException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Returns the task with the specified one-based task number.
     *
     * @param taskNumber One-based number of the task to return.
     * @return Task with the specified number.
     * @throws JonkException If the task number does not exist.
     */
    public Task get(int taskNumber) throws JonkException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return Number of stored tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot of the stored tasks.
     *
     * @return Snapshot of the task list.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Removes the final task to roll back an addition whose save failed.
     */
    void removeLast() {
        tasks.removeLast();
    }

    /**
     * Restores a deleted task after its updated list could not be saved.
     *
     * @param taskNumber Former one-based number of the task.
     * @param task Task to restore.
     */
    void restore(int taskNumber, Task task) {
        tasks.add(taskNumber - 1, task);
    }

    /**
     * Converts a valid one-based task number into a list index.
     *
     * @param taskNumber One-based task number.
     * @return Zero-based list index.
     * @throws JonkException If the task number does not exist.
     */
    private int toIndex(int taskNumber) throws JonkException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new JonkException("That task number does not exist.");
        }
        return taskNumber - 1;
    }
}
