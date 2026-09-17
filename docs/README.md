# Jonk User Guide

Jonk is a warm, precise mission-control copilot that helps you keep track of todos, deadlines, and events. Enter
commands in the transmission field and press Enter or click Transmit. The same commands work in the command-line
interface.

Jonk treats your tasks as missions and your task list as a flight plan. Expect encouraging confirmations such as
`Mission logged:` and `Touchdown! Mission complete:`. The midnight-blue GUI pairs cyan Jonk messages and a star
avatar with violet user messages and a rocket avatar. This theme does not change command syntax or saved tasks.

## Viewing help

Type `help` to display the command reference inside Jonk. The startup greeting reminds you of this command.
You can open help at any time, including when your task list is empty. It does not change or save your tasks.

The help page contains:

```text
Mission guide online. Here's what Jonk can do:
help - Show this help page.
list - Show all tasks and their task numbers.
todo DESCRIPTION - Add a task without a date.
  Example: todo read book
deadline DESCRIPTION /by DATE - Add a task with a due date.
  Example: deadline return book /by 2026-09-15
event DESCRIPTION /from DATE /to DATE - Add an event with start and end dates.
  Example: event project meeting /from 2026-09-16 /to 2026-09-17
mark NUMBER - Mark a task as done. Example: mark 1
unmark NUMBER - Mark a task as not done. Example: unmark 1
delete NUMBER - Delete a task. Example: delete 1
find KEYWORD - Find tasks containing the keyword. Example: find book
bye - Close mission control.

Replace uppercase placeholders with your own values; do not type the placeholders.
Dates use yyyy-MM-dd, for example 2026-09-15.
Use task numbers from list, starting at 1, for mark, unmark, and delete.
Find matches are case-sensitive; their displayed numbers are not task numbers from list.
Descriptions and keywords may contain spaces and must not be empty.
Command words are lowercase. Type help, list, and bye without arguments.
Help does not change your tasks.
```

Use lowercase `help` without arguments. Surrounding whitespace is accepted. For example, `help todo` returns:

```text
Extra signal detected. Type help on its own to open the mission guide.
```

To get started, enter `todo read book`, then `list`. Enter `mark 1` when you finish it.
Tasks are saved automatically after additions, deletions, and changes to completion status.
