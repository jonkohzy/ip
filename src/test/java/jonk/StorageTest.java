package jonk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests loading and saving tasks through {@link Storage}.
 */
public class StorageTest {

    @TempDir
    private Path tempDirectory;

    @Test
    public void load_missingDataFile_returnsEmptyList() throws JonkException {
        Storage storage = new Storage(tempDirectory.resolve("missing/jonk.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_validData_recreatesTasksAndIgnoresBlankLines()
            throws IOException, JonkException {
        Storage storage = createStorageWithData("""
                T | 1 | write \\| report
                D | 0 | return notes \\\\ room | 2019-10-15

                E | 1 | project demo | 2019-10-16 | 2019-10-17
                """);

        List<Task> tasks = storage.load();

        assertEquals(3, tasks.size());
        assertInstanceOf(Todo.class, tasks.get(0));
        assertInstanceOf(Deadline.class, tasks.get(1));
        assertInstanceOf(Event.class, tasks.get(2));
        assertEquals(List.of(
                "T | 1 | write \\| report",
                "D | 0 | return notes \\\\ room | 2019-10-15",
                "E | 1 | project demo | 2019-10-16 | 2019-10-17"),
                tasks.stream().map(Task::toFileString).toList());
    }

    @Test
    public void load_unknownTaskType_throwsJonkExceptionWithLineNumber() throws IOException {
        Storage storage = createStorageWithData("X | 0 | unknown task\n");

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("invalid data at line 1"));
        assertTrue(exception.getMessage().contains("unknown task type 'X'"));
    }

    @Test
    public void load_wrongFieldCount_throwsJonkException() throws IOException {
        Storage storage = createStorageWithData("D | 0 | return book\n");

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("task type D requires 4 fields, but found 3"));
    }

    @Test
    public void load_invalidCompletionStatus_throwsJonkException() throws IOException {
        Storage storage = createStorageWithData("T | yes | read book\n");

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("completion status must be 0 or 1"));
    }

    @Test
    public void load_emptyTaskDetails_throwsJonkException() throws IOException {
        Storage storage = createStorageWithData("T | 0 |   \n");

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("task details cannot be empty"));
    }

    @Test
    public void load_invalidDate_throwsJonkExceptionWithLineNumber() throws IOException {
        Storage storage = createStorageWithData(
                "D | 0 | return book | 2019-02-29\n");

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("invalid data at line 1"));
        assertInstanceOf(java.time.format.DateTimeParseException.class, exception.getCause());
    }

    @Test
    public void save_tasks_createsParentDirectoryAndWritesEscapedData()
            throws IOException, JonkException {
        Path dataFile = tempDirectory.resolve("nested/data/jonk.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("write | report \\ draft");
        todo.markAsDone();

        storage.save(List.of(todo, new Deadline("return book", "2019-12-02")));

        assertEquals("""
                T | 1 | write \\| report \\\\ draft
                D | 0 | return book | 2019-12-02
                """, Files.readString(dataFile));
    }

    @Test
    public void save_emptyList_createsEmptyFile() throws IOException, JonkException {
        Path dataFile = tempDirectory.resolve("data/jonk.txt");
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of());

        assertTrue(Files.exists(dataFile));
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    public void save_existingData_replacesFileContents() throws IOException, JonkException {
        Path dataFile = tempDirectory.resolve("data/jonk.txt");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "old data\n");
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of(new Todo("new task")));

        assertEquals("T | 0 | new task\n", Files.readString(dataFile));
    }

    @Test
    public void save_parentPathIsFile_throwsJonkExceptionAndPreservesBlocker()
            throws IOException {
        Path blocker = tempDirectory.resolve("blocker");
        Files.writeString(blocker, "keep me");
        Storage storage = new Storage(blocker.resolve("jonk.txt").toString());

        JonkException exception = assertThrows(JonkException.class,
                () -> storage.save(List.of(new Todo("new task"))));

        assertTrue(exception.getMessage().contains("Could not save tasks"));
        assertFalse(Files.isDirectory(blocker));
        assertEquals("keep me", Files.readString(blocker));
    }

    /**
     * Creates a storage instance whose data file contains the supplied test fixture.
     *
     * @param data Exact data-file contents.
     * @return Storage backed by the created data file.
     * @throws IOException If the fixture cannot be written.
     */
    private Storage createStorageWithData(String data) throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        Files.writeString(dataFile, data);
        return new Storage(dataFile.toString());
    }
}
