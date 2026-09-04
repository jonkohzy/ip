package jonk;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
