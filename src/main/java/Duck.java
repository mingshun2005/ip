import java.util.Scanner;

/**
 * A simple chatbot that greets the user and echoes commands until the user exits.
 */
public class Duck {
    /**
     * Starts the chatbot, then reads and responds to user commands.
     *
     * @param args command line arguments, currently unused
     */
    public static void main(String[] args) {
        String line = "____________________________________________________________";
        String banner = " ____             _    \n"
                + "|  _ \\ _   _  ___| | __\n"
                + "| | | | | | |/ __| |/ /\n"
                + "| |_| | |_| | (__|   < \n"
                + "|____/ \\__,_|\\___|_|\\_\\\n";
        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hello! I'm Duck. Quack~");
        System.out.println("What can I do for you?");
        System.out.println(line);
        Task[] task = new Task[100];
        int no = -1;

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(line);
            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            } else if (input.equals("list")) {

                int track = no;
                int id = 1;

                System.out.println("Here are the tasks in your list:");

                while (track >= 0) {

                    System.out.println(id + "." + task[id - 1]);
                    id++;
                    track--;
                }
                System.out.println(line);

            } else if (input.startsWith("mark ")) {
                int rankComplete = Integer.parseInt(input.substring(5));
                task[rankComplete - 1].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + task[rankComplete -  1].toString());
                System.out.println(line);



             } else if (input.startsWith("unmark ")) {
                int rankComplete = Integer.parseInt(input.substring(7));
                task[rankComplete - 1].markAsUndone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + task[rankComplete - 1].toString());
                System.out.println(line);



            } else {
                no++;
                task[no] = new Task(input);


                System.out.println("added: " + input);
                System.out.println(line);

            }
        }
    }
}
