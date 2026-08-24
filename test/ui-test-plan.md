# Jonk UI Test Plan

## Session configuration

- Run the test cases in the listed order in one fresh Jonk process. The task list is therefore shared between cases.
- Compile and run with the Java version required by `AGENTS.md`.
- A line whose trimmed content is only Jonk's underscores frames each command response. Record it in the session transcript, but exclude it from the expected-output comparison.
- Before comparison, convert CRLF line endings to LF and expand each tab to four spaces in both actual and expected output. Do not otherwise trim or ignore whitespace or output lines.
- Stop the session immediately after the first failure.

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

## UI-02: Add a deadline

**Aim:** Verify that a deadline with a non-empty `/by` value is added.

**Inputs:**

```text
deadline return book /by Sunday
```

**Expected output:**

```text
Got it. I've added this task:
    [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
```

## UI-03: Add an event

**Aim:** Verify that an event with non-empty `/from` and `/to` values is added.

**Inputs:**

```text
event project meeting /from 2pm /to 3pm
```

**Expected output:**

```text
Got it. I've added this task:
    [E][ ] project meeting (from: 2pm to: 3pm)
Now you have 3 tasks in the list.
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
event project meeting /from /to 3pm
```

**Expected output:**

```text
An event must have non-empty /from and /to values.
```

## UI-07: List tasks after invalid inputs

**Aim:** Verify that the three invalid tasks were not added to the task list.

**Inputs:**

```text
list
```

**Expected output:**

```text
Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Sunday)
    3.[E][ ] project meeting (from: 2pm to: 3pm)
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
    [E][X] project meeting (from: 2pm to: 3pm)
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
    [E][ ] project meeting (from: 2pm to: 3pm)
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
    [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
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
    2.[E][ ] project meeting (from: 2pm to: 3pm)
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
