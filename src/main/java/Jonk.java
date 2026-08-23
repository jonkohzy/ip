import java.util.Scanner;

public class Jonk {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line = "____________________________________________________________";
        String name = "Jonk";
        String greeting = "Hello! I'm " + name + ".\nWhat can I do for you?";
        String bye = "Bye. Hope to see you again soon!";
        String banner = "     _  ___  _   _ _  __\n"
                + "    | |/ _ \\| \\ | | |/ /\n"
                + " _  | | | | |  \\| | ' / \n"
                + "| |_| | |_| | |\\  | . \\ \n"
                + " \\___/ \\___/|_| \\_|_|\\_\\";

        System.out.println("\t" + line);
        System.out.println("\t" + banner);
        System.out.println("\t" + greeting);
        System.out.println("\t" + line);

        String input = scanner.nextLine();
        while (!input.equals("bye")) {
            System.out.println("\t" + line);
            System.out.println("\t" + input);
            System.out.println("\t" + line);
            input = scanner.nextLine();
        }

        System.out.println("\t" + line);
        System.out.println("\t" + bye);
        System.out.println("\t" + line);
    }
}
