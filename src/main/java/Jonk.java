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
                // extract task type
                String[] parts = input.trim().split("\\s+", 2);

                if (parts[0].equals("mark") || parts[0].equals("unmark")) {
                    //command
                    int taskNumber = Integer.parseInt(parts[1]);
                    int taskIndex = taskNumber - 1;

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
                    // task
                    // parts[1] contains "[name] /[command] [command detail]"
                    String[] details = parts[1].trim().split("\\s+/(?:from|to|by)\\s+");

                    if (parts[0].equals("todo")) { listOfItems[nextEmptyIndex] = new Todo(details[0]); }
                    else if (parts[0].equals("deadline")) { listOfItems[nextEmptyIndex] = new Deadline(details[0], details[1]); }
                    else { listOfItems[nextEmptyIndex] = new Event(details[0], details[1], details[2]); }

                    System.out.println("Got it. I've added this task:");
                    System.out.println("\t" + listOfItems[nextEmptyIndex]);
                    nextEmptyIndex++;
                    System.out.println("Now you have " + nextEmptyIndex + " tasks in the list.");
                }
            }

            System.out.println("\t" + line);
            input = scanner.nextLine();
        }

        System.out.println("\t" + line +"\n\t" + bye + "\n\t" + line);
    }
}
