import java.util.Scanner;

public class Jonk {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task[] listOfItems = new Task[100];
        int nextEmptyIndex = 0;

        String line = "____________________________________________________________";
        String name = "Jonk";
        String greeting = "Hello! I'm " + name + ".\nWhat can I do for you?";
        String bye = "Bye. Hope to see you again soon!";
        String banner = "     _  ___  _   _ _  __\n"
                + "    | |/ _ \\| \\ | | |/ /\n"
                + " _  | | | | |  \\| | ' / \n"
                + "| |_| | |_| | |\\  | . \\ \n"
                + " \\___/ \\___/|_| \\_|_|\\_\\";

        System.out.println("\t" + line + "\n\t" + banner + "\n\t" + greeting + "\n\t" + line);

        String input = scanner.nextLine();
        while (!input.equals("bye")) {
            System.out.println("\t" + line);

            if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < nextEmptyIndex; i++) {
                    System.out.println("\t" + (i + 1) + "." + listOfItems[i]);
                }
            } else {
                try {
                    // extract task type
                    String[] parts = input.trim().split("\\s+", 2);

                    if (parts[0].equals("mark") || parts[0].equals("unmark")) {
                        if (parts.length != 2) {
                            throw new JonkException("Please provide exactly one task number.");
                        }
                        int taskIndex = getTaskIndex(parts, nextEmptyIndex);

                        if (parts[0].equals("mark")) {
                            listOfItems[taskIndex].markAsDone();
                            System.out.println("Nice! I've marked this task as done:");
                            System.out.println("\t" + listOfItems[taskIndex]);
                        } else {
                            listOfItems[taskIndex].markAsUndone();
                            System.out.println("OK, I've marked this task as not done yet:");
                            System.out.println("\t" + listOfItems[taskIndex]);
                        }

                    } else if (parts[0].equals("todo") || parts[0].equals("deadline") || parts[0].equals("event")) {
                        if (parts.length < 2 || parts[1].isBlank()) {
                            if (parts[0].equals("todo")) {
                                throw new JonkException("A todo must have a non-empty description.");
                            } else if (parts[0].equals("deadline")) {
                                throw new JonkException("A deadline must have a non-empty /by value.");
                            } else {
                                throw new JonkException("An event must have non-empty /from and /to values.");
                            }
                        }

                        // Split the details into the description and command details.
                        String[] details = parts[1].trim().split("\\s+(?=/)");

                        if (parts[0].equals("todo")) {
                            if (details[0].isBlank()) {
                                throw new JonkException("A todo must have a non-empty description.");
                            }
                            listOfItems[nextEmptyIndex] = new Todo(details[0]);
                        } else if (parts[0].equals("deadline")) {
                            if (details.length != 2) {
                                throw new JonkException("A deadline must have a non-empty /by value.");
                            }

                            String[] byDetails = details[1].trim().split("\\s+", 2);
                            if (byDetails.length != 2 || !byDetails[0].equals("/by") || byDetails[1].isBlank()) {
                                throw new JonkException("A deadline must have a non-empty /by value.");
                            }

                            String description = details[0];
                            String by = byDetails[1].trim();
                            listOfItems[nextEmptyIndex] = new Deadline(description, by);
                        } else {
                            if (details.length != 3) {
                                throw new JonkException("An event must have non-empty /from and /to values.");
                            }

                            String[] fromDetails = details[1].trim().split("\\s+", 2);
                            String[] toDetails = details[2].trim().split("\\s+", 2);
                            boolean hasValidFrom = fromDetails.length == 2 && fromDetails[0].equals("/from") && !fromDetails[1].isBlank();
                            boolean hasValidTo = toDetails.length == 2 && toDetails[0].equals("/to") && !toDetails[1].isBlank();

                            if (!hasValidFrom || !hasValidTo) {
                                throw new JonkException("An event must have non-empty /from and /to values.");
                            }

                            String description = details[0];
                            String from = fromDetails[1].trim();
                            String to = toDetails[1].trim();
                            listOfItems[nextEmptyIndex] = new Event(description, from, to);
                        }

                        System.out.println("Got it. I've added this task:");
                        System.out.println("\t" + listOfItems[nextEmptyIndex]);
                        nextEmptyIndex++;
                        System.out.println("Now you have " + nextEmptyIndex + " tasks in the list.");
                    } else {
                        throw new JonkException("Sorry, I don't know what that means");
                    }
                } catch (JonkException e) {
                    System.out.println(e.getMessage());
                }
            }

            System.out.println("\t" + line);
            input = scanner.nextLine();
        }

        System.out.println("\t" + line +"\n\t" + bye + "\n\t" + line);
    }

    private static int getTaskIndex(String[] parts, int nextEmptyIndex) throws JonkException {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new JonkException("The task number must be a whole number.");
        }

        if (taskNumber < 1 || taskNumber > nextEmptyIndex) {
            throw new JonkException("That task number does not exist.");
        }

		return taskNumber - 1;
    }
}
