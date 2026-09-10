package jonk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command responses shared by Jonk's command-line and graphical interfaces.
 */
public class JonkTest {

    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_help_returnsCommandReferenceWithoutCreatingDataFile() {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        Jonk jonk = new Jonk(dataFile.toString());

        String response = jonk.getResponse("help");

        for (String command : new String[]{"help", "list", "todo DESCRIPTION", "deadline DESCRIPTION /by DATE",
                "event DESCRIPTION /from DATE /to DATE", "mark NUMBER", "unmark NUMBER", "delete NUMBER",
                "find KEYWORD", "bye"}) {
            assertTrue(response.contains("\n" + command + " - "), command);
        }
        assertTrue(response.contains("yyyy-MM-dd"));
        assertTrue(response.contains("Use task numbers from list, starting at 1"));
        assertTrue(response.contains("Find matches are case-sensitive"));
        assertEquals("Here are the tasks in your list:", jonk.getResponse("list"));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void getResponse_helpWithSurroundingWhitespace_returnsHelp() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals(jonk.getResponse("help"), jonk.getResponse(" \thelp \t"));
    }

    @Test
    public void getResponse_helpWithArguments_returnsUsageError() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("The help command takes no arguments. Type help to see all commands.",
                jonk.getResponse("help todo"));
    }

    @Test
    public void getResponse_helpWithExistingTasks_preservesTasksAndDataFile() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        String savedData = "T | 1 | read book\n\nD | 0 | return book | 2026-09-15\n";
        Files.writeString(dataFile, savedData);
        Jonk jonk = new Jonk(dataFile.toString());
        String originalList = jonk.getResponse("list");

        jonk.getResponse("help");
        jonk.getResponse("help todo");

        assertEquals(originalList, jonk.getResponse("list"));
        assertEquals(savedData, Files.readString(dataFile));
    }

    @Test
    public void getResponse_helpAfterLoadFailure_preservesInvalidData() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        String savedData = "X | 0 | invalid task\n";
        Files.writeString(dataFile, savedData);
        Jonk jonk = new Jonk(dataFile.toString());

        assertTrue(jonk.getResponse("help").startsWith("Here's what you can do with Jonk:"));
        assertEquals(savedData, Files.readString(dataFile));
    }

    @Test
    public void getWelcomeMessage_normalStartup_includesHelpHint() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Hello! I'm Jonk.\nWhat can I do for you?\nType help to see the available commands.",
                jonk.getWelcomeMessage());
    }

    @Test
    public void getResponse_addThenList_returnsResponsesAndSavesTask() throws IOException {
        Path dataFile = tempDirectory.resolve("data/jonk.txt");
        Jonk jonk = new Jonk(dataFile.toString());

        String addResponse = jonk.getResponse("todo read book");
        String listResponse = jonk.getResponse("list");

        assertEquals("Got it. I've added this task:\n\t[T][ ] read book"
                + "\nNow you have 1 tasks in the list.", addResponse);
        assertEquals("Here are the tasks in your list:\n\t1.[T][ ] read book", listResponse);
        assertEquals("T | 0 | read book\n", Files.readString(dataFile));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorWithoutThrowing() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Sorry, I don't know what that means", jonk.getResponse("blah"));
    }

    @Test
    public void getResponse_invalidDate_returnsFriendlyError() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Dates must be in yyyy-MM-dd format.",
                jonk.getResponse("deadline invalid /by 2019-02-29"));
    }

    @Test
    public void getResponse_bye_returnsFarewell() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Bye. Hope to see you again soon!", jonk.getResponse("bye"));
    }

    @Test
    public void getWelcomeMessage_invalidStoredData_includesLoadError() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        Files.writeString(dataFile, "X | 0 | invalid task\n");
        Jonk jonk = new Jonk(dataFile.toString());

        String welcomeMessage = jonk.getWelcomeMessage();

        assertTrue(welcomeMessage.startsWith("Hello! I'm Jonk."));
        assertTrue(welcomeMessage.contains("invalid data at line 1"));
    }
}
