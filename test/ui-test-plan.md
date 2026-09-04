# Jonk UI Test Plan

## Session configuration

- Run the test cases in the listed order in one fresh Jonk process. The task list is therefore shared between cases.
- Compile and run with the Java version required by `AGENTS.md`.
- Start the command-line test process with `java -cp build/classes/java/main jonk.Jonk`; `./gradlew run` starts
  the JavaFX application.
- Use `test/gui-test-plan.md` for the JavaFX-specific interaction checks.
- A line whose trimmed content is only Jonk's underscores frames each command response. Record it in the session transcript, but exclude it from the expected-output comparison.
- Before comparison, convert CRLF line endings to LF and expand each tab to four spaces in both actual and expected output. Do not otherwise trim or ignore whitespace or output lines.
- Before starting the session, replace `./data/jonk.txt` with the exact startup fixture below. After each case that specifies expected data-file contents, read the file and compare it exactly after converting CRLF line endings to LF.
- Stop the session immediately after the first failure.

**Startup data-file fixture:**

```text
T | 1 | write \| report
D | 0 | return notes \\ room | 2019-10-15

E | 1 | project demo | 2019-10-16 | 2019-10-17
```

## UI-LOAD-01: List loaded tasks

**Aim:** Verify that startup loading recreates all task types and completion states, ignores blank lines, and decodes escaped pipe and backslash characters.

**Inputs:**

```text
list
```

**Expected output:**

```text
Here are the tasks in your list:
    1.[T][X] write | report
    2.[D][ ] return notes \ room (by: Oct 15 2019)
    3.[E][X] project demo (from: Oct 16 2019 to: Oct 17 2019)
```

## UI-LOAD-02: Delete the loaded event

**Aim:** Verify that the loaded event behaves like a normal task and remove it before the existing add-task cases.

**Inputs:**

```text
delete 3
```

**Expected output:**

```text
Noted. I've removed this task:
    [E][X] project demo (from: Oct 16 2019 to: Oct 17 2019)
Now you have 2 tasks in the list.
```

**Expected data file after command:**

```text
T | 1 | write \| report
D | 0 | return notes \\ room | 2019-10-15
```

## UI-LOAD-03: Delete the loaded deadline

**Aim:** Verify that the loaded deadline behaves like a normal task and remove it before the existing add-task cases.

**Inputs:**

```text
delete 2
```

**Expected output:**

```text
Noted. I've removed this task:
    [D][ ] return notes \ room (by: Oct 15 2019)
Now you have 1 tasks in the list.
```

**Expected data file after command:**

```text
T | 1 | write \| report
```

## UI-LOAD-04: Delete the loaded todo

**Aim:** Verify that the loaded todo behaves like a normal task and leave an empty list for the existing cases.

**Inputs:**

```text
delete 1
```

**Expected output:**

```text
Noted. I've removed this task:
    [T][X] write | report
Now you have 0 tasks in the list.
```

**Expected data file after command:** The file is empty (zero bytes).

## UI-01: Add a todo

**Aim:** Verify that a todo with a non-empty description is added as the first task.

**Inputs:**

```text
todo read book
```

**Expected output:**

```text
Got it. I've added this task:
    [T][ ] read book
Now you have 1 tasks in the list.
```

**Expected data file after command:**

```text
T | 0 | read book
```

## UI-02: Add a deadline

**Aim:** Verify that a deadline accepts an ISO date and displays it in a human-readable format.

**Inputs:**

```text
deadline return book /by 2019-12-02
```

**Expected output:**

```text
Got it. I've added this task:
    [D][ ] return book (by: Dec 2 2019)
Now you have 2 tasks in the list.
```

**Expected data file after command:**

```text
T | 0 | read book
D | 0 | return book | 2019-12-02
```

## UI-03: Add an event

**Aim:** Verify that an event accepts ISO dates and displays them in a human-readable format.

**Inputs:**

```text
event project meeting /from 2019-12-03 /to 2019-12-04
```

**Expected output:**

```text
Got it. I've added this task:
    [E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)
Now you have 3 tasks in the list.
```

**Expected data file after command:**

```text
T | 0 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-04: Reject an empty todo description

**Aim:** Verify that a todo without a description is rejected and not counted.

**Inputs:**

```text
todo
```

**Expected output:**

```text
A todo must have a non-empty description.
```

## UI-05: Reject an empty deadline value

**Aim:** Verify that a deadline without a `/by` value is rejected and not counted.

**Inputs:**

```text
deadline return book /by
```

**Expected output:**

```text
A deadline must have a non-empty /by value.
```

## UI-06: Reject an empty event value

**Aim:** Verify that an event with an empty `/from` value is rejected and not counted.

**Inputs:**

```text
event project meeting /from /to 2019-12-04
```

**Expected output:**

```text
An event must have non-empty /from and /to values.
```

## UI-DATE-01: Reject an impossible deadline date

**Aim:** Verify that an invalid calendar date is rejected without terminating the chatbot or adding a task.

**Inputs:**

```text
deadline invalid date /by 2019-02-29
```

**Expected output:**

```text
Dates must be in yyyy-MM-dd format.
```

## UI-DATE-02: Reject an event date in the wrong format

**Aim:** Verify that a non-ISO event date is rejected without terminating the chatbot or adding a task.

**Inputs:**

```text
event invalid date /from 2019/12/03 /to 2019-12-04
```

**Expected output:**

```text
Dates must be in yyyy-MM-dd format.
```

## UI-07: List tasks after invalid inputs

**Aim:** Verify that the five invalid tasks were not added to the task list.

**Inputs:**

```text
list
```

**Expected output:**

```text
Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Dec 2 2019)
    3.[E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)
```

## UI-FIND-01: Find multiple matching tasks

**Aim:** Verify that `find` displays only tasks whose descriptions contain the keyword and numbers the matches
from one in their original order.

**Inputs:**

```text
find book
```

**Expected output:**

```text
Here are the matching tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Dec 2 2019)
```

## UI-FIND-02: Find no matching tasks

**Aim:** Verify that `find` displays an empty matching-task list when no description contains the keyword.

**Inputs:**

```text
find library
```

**Expected output:**

```text
Here are the matching tasks in your list:
```

## UI-FIND-03: Reject find without a keyword

**Aim:** Verify that `find` requires a non-empty keyword and does not terminate the chatbot when it is missing.

**Inputs:**

```text
find
```

**Expected output:**

```text
Please provide a keyword to find.
```

## UI-08: Mark a task

**Aim:** Verify that the first task can be marked as done.

**Inputs:**

```text
mark 1
```

**Expected output:**

```text
Nice! I've marked this task as done:
    [T][X] read book
```

**Expected data file after command:**

```text
T | 1 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-09: Unmark a task

**Aim:** Verify that the first task can be marked as not done again.

**Inputs:**

```text
unmark 1
```

**Expected output:**

```text
OK, I've marked this task as not done yet:
    [T][ ] read book
```

**Expected data file after command:**

```text
T | 0 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-10: Reject an unknown command

**Aim:** Verify that an unrecognized command is rejected without terminating the chatbot.

**Inputs:**

```text
blah
```

**Expected output:**

```text
Sorry, I don't know what that means
```

## UI-11: Reject blank input

**Aim:** Verify that pressing Enter without a command is rejected without terminating the chatbot.

**Inputs:** Press Enter without typing anything; the fenced block below intentionally contains an empty line.

```text

```

**Expected output:**

```text
Sorry, I don't know what that means
```

## UI-12: Reject mark without a task number

**Aim:** Verify that `mark` requires a task number and does not terminate the chatbot when it is missing.

**Inputs:**

```text
mark
```

**Expected output:**

```text
Please provide exactly one task number.
```

## UI-13: Reject a non-numeric mark task number

**Aim:** Verify that `mark` rejects a task number that is not a whole number.

**Inputs:**

```text
mark abc
```

**Expected output:**

```text
The task number must be a whole number.
```

## UI-14: Reject mark task number zero

**Aim:** Verify that `mark` rejects zero because task numbers start from one.

**Inputs:**

```text
mark 0
```

**Expected output:**

```text
That task number does not exist.
```

## UI-15: Reject mark beyond the list size

**Aim:** Verify that `mark` rejects task number four when the list contains only three tasks.

**Inputs:**

```text
mark 4
```

**Expected output:**

```text
That task number does not exist.
```

## UI-16: Reject unmark without a task number

**Aim:** Verify that `unmark` requires a task number and does not terminate the chatbot when it is missing.

**Inputs:**

```text
unmark
```

**Expected output:**

```text
Please provide exactly one task number.
```

## UI-17: Reject a non-numeric unmark task number

**Aim:** Verify that `unmark` rejects a task number that is not a whole number.

**Inputs:**

```text
unmark abc
```

**Expected output:**

```text
The task number must be a whole number.
```

## UI-18: Reject unmark task number zero

**Aim:** Verify that `unmark` rejects zero because task numbers start from one.

**Inputs:**

```text
unmark 0
```

**Expected output:**

```text
That task number does not exist.
```

## UI-19: Reject unmark beyond the list size

**Aim:** Verify that `unmark` rejects task number four when the list contains only three tasks.

**Inputs:**

```text
unmark 4
```

**Expected output:**

```text
That task number does not exist.
```

## UI-20: Mark the last task

**Aim:** Verify that the highest valid task number can be marked as done.

**Inputs:**

```text
mark 3
```

**Expected output:**

```text
Nice! I've marked this task as done:
    [E][X] project meeting (from: Dec 3 2019 to: Dec 4 2019)
```

**Expected data file after command:**

```text
T | 0 | read book
D | 0 | return book | 2019-12-02
E | 1 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-21: Unmark the last task

**Aim:** Verify that the highest valid task number can be marked as not done again.

**Inputs:**

```text
unmark 3
```

**Expected output:**

```text
OK, I've marked this task as not done yet:
    [E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)
```

**Expected data file after command:**

```text
T | 0 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-22: Delete a task

**Aim:** Verify that a valid task number deletes the corresponding task and updates the task count.

**Inputs:**

```text
delete 2
```

**Expected output:**

```text
Noted. I've removed this task:
    [D][ ] return book (by: Dec 2 2019)
Now you have 2 tasks in the list.
```

**Expected data file after command:**

```text
T | 0 | read book
E | 0 | project meeting | 2019-12-03 | 2019-12-04
```

## UI-23: List tasks after deletion

**Aim:** Verify that the deleted task is absent and later tasks are renumbered.

**Inputs:**

```text
list
```

**Expected output:**

```text
Here are the tasks in your list:
    1.[T][ ] read book
    2.[E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)
```

## UI-24: Reject delete without a task number

**Aim:** Verify that `delete` requires a task number and does not terminate the chatbot when it is missing.

**Inputs:**

```text
delete
```

**Expected output:**

```text
Please provide exactly one task number.
```

## UI-25: Reject a non-numeric delete task number

**Aim:** Verify that `delete` rejects a task number that is not a whole number.

**Inputs:**

```text
delete abc
```

**Expected output:**

```text
The task number must be a whole number.
```

## UI-26: Reject delete task number zero

**Aim:** Verify that `delete` rejects zero because task numbers start from one.

**Inputs:**

```text
delete 0
```

**Expected output:**

```text
That task number does not exist.
```

## UI-27: Reject delete beyond the list size

**Aim:** Verify that `delete` rejects task number three when the list contains only two tasks.

**Inputs:**

```text
delete 3
```

**Expected output:**

```text
That task number does not exist.
```

## UI-28: Exit the program

**Aim:** Verify that the program exits with its farewell message.

**Inputs:**

```text
bye
```

**Expected output:**

```text
    Bye. Hope to see you again soon!
```
