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

        assertEquals("Please provide exactly one task number.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_extraArgument_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTaskNumber("mark 1 now"));

        assertEquals("The task number must be a whole number.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_nonInteger_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTaskNumber("mark 1.5"));

        assertEquals("The task number must be a whole number.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_numberOutsideIntegerRange_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTaskNumber("mark 2147483648"));

        assertEquals("The task number must be a whole number.", exception.getMessage());
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

        assertEquals("A todo must have a non-empty description.", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithoutByValue_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTask("deadline return book /by"));

        assertEquals("A deadline must have a non-empty /by value.", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithWrongMarker_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTask("deadline return book /before 2019-12-02"));

        assertEquals("A deadline must have a non-empty /by value.", exception.getMessage());
    }

    @Test
    public void parseTask_eventWithoutToValue_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTask("event meeting /from 2019-12-03 /to"));

        assertEquals("An event must have non-empty /from and /to values.",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventWithReversedMarkers_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTask(
                        "event meeting /to 2019-12-04 /from 2019-12-03"));

        assertEquals("An event must have non-empty /from and /to values.",
                exception.getMessage());
    }

    @Test
    public void parseTask_unknownTaskType_throwsJonkException() {
        JonkException exception = assertThrows(JonkException.class,
                () -> Parser.parseTask("reminder call home"));

        assertEquals("Sorry, I don't know what that means", exception.getMessage());
    }

    @Test
    public void parseTask_invalidDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class,
                () -> Parser.parseTask("deadline return book /by 2019-02-29"));
    }
}
