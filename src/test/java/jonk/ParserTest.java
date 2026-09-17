package jonk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests command parsing and validation performed by {@link Parser}.
 */
public class ParserTest {

    @Test
    public void parseCommandWord_commandWithArguments_returnsCommandWord() {
        assertEquals("deadline", Parser.parseCommandWord("deadline return book /by 2019-12-02"));
    }

    @Test
    public void parseCommandWord_surroundingWhitespace_returnsCommandWord() {
        assertEquals("list", Parser.parseCommandWord("   list   "));
    }

    @Test
    public void parseCommandWord_blankInput_returnsEmptyString() {
        assertEquals("", Parser.parseCommandWord("   "));
    }

    @Test
    public void parseTaskNumber_validNumber_returnsNumber() throws JonkException {
        assertEquals(12, Parser.parseTaskNumber("mark 12"));
    }

    @Test
    public void parseTaskNumber_surroundingWhitespace_returnsNumber() throws JonkException {
        assertEquals(2, Parser.parseTaskNumber("  delete   2  "));
    }

    @Test
    public void parseTaskNumber_missingNumber_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTaskNumber("mark"));

        assertEquals("Mission control needs exactly one task number.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_extraArgument_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTaskNumber("mark 1 now"));

        assertEquals("Task coordinates must be a whole number.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_nonInteger_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTaskNumber("mark 1.5"));

        assertEquals("Task coordinates must be a whole number.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_numberOutsideIntegerRange_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTaskNumber("mark 2147483648"));

        assertEquals("Task coordinates must be a whole number.", exception.getMessage());
    }

    @Test
    public void parseKeyword_validKeyword_returnsKeyword() throws JonkException {
        assertEquals("book", Parser.parseKeyword("find book"));
    }

    @Test
    public void parseKeyword_multiWordKeyword_returnsCompleteKeyword() throws JonkException {
        assertEquals("project meeting", Parser.parseKeyword("find project meeting"));
    }

    @Test
    public void parseKeyword_missingKeyword_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseKeyword("find"));

        assertEquals("Send a keyword for Jonk to scan.", exception.getMessage());
    }

    @Test
    public void parseTask_validTodo_returnsTodo() throws JonkException {
        Task task = Parser.parseTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseTask_validDeadline_returnsDeadline() throws JonkException {
        Task task = Parser.parseTask("deadline return book /by 2019-12-02");

        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Dec 2 2019)", task.toString());
    }

    @Test
    public void parseTask_validEvent_returnsEvent() throws JonkException {
        Task task = Parser.parseTask(
                "event project meeting /from 2019-12-03 /to 2019-12-04");

        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)",
                task.toString());
    }

    @Test
    public void parseTask_todoWithoutDescription_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTask("todo"));

        assertEquals("A todo mission needs a description.", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithoutByValue_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTask("deadline return book /by"));

        assertEquals("A deadline mission needs a non-empty /by date.", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithWrongMarker_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTask("deadline return book /before 2019-12-02"));

        assertEquals("A deadline mission needs a non-empty /by date.", exception.getMessage());
    }

    @Test
    public void parseTask_eventWithoutToValue_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTask("event meeting /from 2019-12-03 /to"));

        assertEquals("An event mission needs non-empty /from and /to dates.",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventWithReversedMarkers_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTask(
                        "event meeting /to 2019-12-04 /from 2019-12-03"));

        assertEquals("An event mission needs non-empty /from and /to dates.",
                exception.getMessage());
    }

    @Test
    public void parseTask_unknownTaskType_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTask("reminder call home"));

        assertEquals("Signal unclear. Type help to open the mission guide.", exception.getMessage());
    }

    @Test
    public void parseTask_invalidDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class,
                () -> Parser.parseTask("deadline return book /by 2019-02-29"));
    }
}
