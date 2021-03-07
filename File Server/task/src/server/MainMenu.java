package server;

import java.util.Scanner;

public class MainMenu {

    private enum Command {
        put,
        get,
        delete,
        exit
    }

    private final Scanner scanner = new Scanner(System.in);

    private final FileServer fileServer;

    MainMenu(FileServer fileServer) {
        this.fileServer = fileServer;
        show();
    }

    private void show() {
        /*String[] input = scanner.nextLine().split("\\s+");
        Command command = null;

        try {
            command = Command.valueOf(input[0]);
        } catch (Exception e) {
            System.out.println("Unknown command");
        }

        if (command != null) {
            try {
                switch (command) {
                    case put:
                        fileServer.put(input[1]);
                        break;
                    case get:
                        fileServer.get(input[1]);
                        break;
                    case delete:
                        fileServer.delete(input[1]);
                        break;
                    case exit:
                        return;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Incorrect arguments");
            }
        }

        show();

         */
    }
}
