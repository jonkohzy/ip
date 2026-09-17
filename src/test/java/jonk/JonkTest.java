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
        assertEquals("Flight plan, coming right up:", jonk.getResponse("list"));
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

        assertEquals("Extra signal detected. Type help on its own to open the mission guide.",
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

        assertTrue(jonk.getResponse("help").startsWith("Mission guide online. Here's what Jonk can do:"));
        assertEquals(savedData, Files.readString(dataFile));
    }

    @Test
    public void getWelcomeMessage_normalStartup_includesHelpHint() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Systems online! I'm Jonk, your mission-control copilot."
                + "\nWhat shall we put on the flight plan?"
                + "\nType help to see the available commands.",
                jonk.getWelcomeMessage());
    }

    @Test
    public void getResponse_addThenList_returnsResponsesAndSavesTask() throws IOException {
        Path dataFile = tempDirectory.resolve("data/jonk.txt");
        Jonk jonk = new Jonk(dataFile.toString());

        String addResponse = jonk.getResponse("todo read book");
        String listResponse = jonk.getResponse("list");

        assertEquals("Mission logged:\n\t[T][ ] read book"
                + "\nFlight plan now holds 1 mission.", addResponse);
        assertEquals("Flight plan, coming right up:\n\t1.[T][ ] read book", listResponse);
        assertEquals("T | 0 | read book\n", Files.readString(dataFile));
    }

    @Test
    public void getResponse_missionLifecycle_usesThemedResponsesAndPreservesTaskBehavior() throws IOException {
        Path dataFile = tempDirectory.resolve("data/jonk.txt");
        Jonk jonk = new Jonk(dataFile.toString());
        jonk.getResponse("todo read book");

        assertEquals("Mission logged:\n\t[T][ ] return book\nFlight plan now holds 2 missions.",
                jonk.getResponse("todo return book"));
        assertEquals("Touchdown! Mission complete:\n\t[T][X] read book", jonk.getResponse("mark 1"));
        assertEquals("Course corrected. Mission active again:\n\t[T][ ] read book",
                jonk.getResponse("unmark 1"));
        assertEquals("Scanner results—matching missions:\n\t1.[T][ ] read book\n\t2.[T][ ] return book",
                jonk.getResponse("find book"));
        assertEquals("Mission scrubbed from the flight plan:\n\t[T][ ] read book"
                + "\nFlight plan now holds 1 mission.",
                jonk.getResponse("delete 1"));
        assertEquals("Mission scrubbed from the flight plan:\n\t[T][ ] return book"
                + "\nFlight plan now holds 0 missions.",
                jonk.getResponse("delete 1"));
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorWithoutThrowing() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Signal unclear. Type help to open the mission guide.", jonk.getResponse("blah"));
    }

    @Test
    public void getResponse_invalidDate_returnsFriendlyError() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Navigation dates must use yyyy-MM-dd format.",
                jonk.getResponse("deadline invalid /by 2019-02-29"));
    }

    @Test
    public void getResponse_bye_returnsFarewell() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Mission control signing off. Clear skies, explorer!", jonk.getResponse("bye"));
    }

    @Test
    public void getWelcomeMessage_invalidStoredData_includesLoadError() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        Files.writeString(dataFile, "X | 0 | invalid task\n");
        Jonk jonk = new Jonk(dataFile.toString());

        String welcomeMessage = jonk.getWelcomeMessage();

        assertTrue(welcomeMessage.startsWith("Systems online! I'm Jonk, your mission-control copilot."));
        assertTrue(welcomeMessage.contains("invalid data at line 1"));
    }
}
