# Jonk GUI Test Plan

## Session configuration

- Run the application with Java 25 using `./gradlew run`.
- Before starting, replace `./data/jonk.txt` with the startup fixture below and restore the original file afterward.

**Startup data-file fixture:**

```text
T | 1 | write report
D | 0 | return notes | 2019-10-15
E | 1 | project demo | 2019-10-16 | 2019-10-17
```

## GUI-01: Open the chatbot window

**Aim:** Verify that the application starts with all controls needed to use the chatbot.

**Inputs:** Launch the application.

**Expected output:** A non-resizable window titled `Jonk` displays Jonk's greeting, a command field, and a `Send`
button.

## GUI-02: Submit a command with Enter

**Aim:** Verify that the command field accepts input and displays both sides of the conversation.

**Inputs:** Type `list` in the command field and press Enter.

**Expected output:** A right-aligned `list` bubble and a left-aligned Jonk response bubble display all three startup
tasks with their completion states and dates.

## GUI-03: Submit a command with the button

**Aim:** Verify that the Send button processes a command and persistent task changes still work.

**Inputs:** Type `todo GUI smoke test` and click `Send`.

**Expected output:** The conversation displays the user command and Jonk's task-added response with a total of four
tasks. The data file ends with `T | 0 | GUI smoke test`.

## GUI-04: Exit using the bye command

**Aim:** Verify that the CLI-style exit command remains available in the GUI.

**Inputs:** Type `bye` and press Enter.

**Expected output:** Jonk's farewell appears, input controls become disabled, and the window closes after a brief
delay.
