package jonk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task lookup, deletion, and rollback operations in {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void get_firstTaskNumber_returnsFirstTask() throws JonkException {
        Task firstTask = new Todo("first task");
        TaskList taskList = new TaskList(List.of(firstTask, new Todo("last task")));

        assertSame(firstTask, taskList.get(1));
    }

    @Test
    public void get_lastTaskNumber_returnsLastTask() throws JonkException {
        Task lastTask = new Todo("last task");
        TaskList taskList = new TaskList(List.of(new Todo("first task"), lastTask));

        assertSame(lastTask, taskList.get(2));
    }

    @Test
    public void get_emptyList_throwsJonkException() {
        TaskList taskList = new TaskList();

        JonkException exception = assertThrows(JonkException.class, () -> taskList.get(1));

        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    public void get_zeroTaskNumber_throwsJonkException() {
        TaskList taskList = new TaskList(List.of(new Todo("only task")));

        JonkException exception = assertThrows(JonkException.class, () -> taskList.get(0));

        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    public void get_negativeTaskNumber_throwsJonkException() {
        TaskList taskList = new TaskList(List.of(new Todo("only task")));

        JonkException exception = assertThrows(JonkException.class, () -> taskList.get(-1));

        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    public void get_taskNumberBeyondListSize_throwsJonkException() {
        TaskList taskList = new TaskList(List.of(new Todo("only task")));

        JonkException exception = assertThrows(JonkException.class, () -> taskList.get(2));

        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    public void delete_middleTask_returnsTaskAndRemovesIt() throws JonkException {
        Task firstTask = new Todo("first task");
        Task middleTask = new Todo("middle task");
        Task lastTask = new Todo("last task");
        TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask));

        Task deletedTask = taskList.delete(2);

        assertSame(middleTask, deletedTask);
        assertEquals(List.of(firstTask, lastTask), taskList.asList());
    }

    @Test
    public void delete_invalidTaskNumber_throwsJonkExceptionAndPreservesList() {
        Task onlyTask = new Todo("only task");
        TaskList taskList = new TaskList(List.of(onlyTask));

        JonkException exception = assertThrows(JonkException.class, () -> taskList.delete(2));

        assertEquals("That task number does not exist.", exception.getMessage());
        assertEquals(List.of(onlyTask), taskList.asList());
    }

    @Test
    public void removeLast_multipleTasks_removesFinalTask() {
        Task firstTask = new Todo("first task");
        TaskList taskList = new TaskList(List.of(firstTask, new Todo("last task")));

        taskList.removeLast();

        assertEquals(List.of(firstTask), taskList.asList());
    }

    @Test
    public void restore_deletedTask_reinsertsTaskAtFormerPosition() throws JonkException {
        Task firstTask = new Todo("first task");
        Task middleTask = new Todo("middle task");
        Task lastTask = new Todo("last task");
        TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask));
        Task deletedTask = taskList.delete(2);

        taskList.restore(2, deletedTask);

        assertEquals(List.of(firstTask, middleTask, lastTask), taskList.asList());
    }
}
