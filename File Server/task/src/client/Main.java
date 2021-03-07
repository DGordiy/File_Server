package client;

import Service.Request;
import Service.RequestMethod;
import Service.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    private final static String FILE_PATH = "C:\\Users\\Denis\\IdeaProjects\\File Server\\File Server\\task\\src\\client\\data";

    private final static Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        final int PORT = 8080;
        final String SERVER_ADDR = "127.0.0.1";

        try (Socket socket = new Socket(InetAddress.getByName(SERVER_ADDR), PORT);
             ObjectOutputStream outputStream = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
             ObjectInputStream inputStream = new ObjectInputStream(new DataInputStream(socket.getInputStream()))) {

            //Writing and reading a data
            Request request = getCommand();
            while (request == null) {
                request = getCommand();
            }

            //outputStream.writeUTF(commandLine);
            outputStream.writeObject(request);
            System.out.println("The request was sent.");

            Response response;
            if (!request.getMethod().equals(RequestMethod.exit)) {
                response = (Response) inputStream.readObject();
                switch (request.getMethod()) {
                    case get:
                        if (response.getStatus() == 200) {
                            System.out.println("The file was downloaded! Specify a name for it:");
                            String fileName = SCANNER.nextLine();

                            Path path = Path.of(FILE_PATH, fileName);
                            Files.write(path, response.getData());

                            System.out.println("File saved on the hard drive!");
                        } else {
                            System.out.println("The response says that this file is not found!");
                        }
                        break;
                    case put:
                        if (response.getStatus() == 200) {
                            System.out.println("Response says that file is saved! " + response.getMessage());
                        } else {
                            System.out.println("The response says that creating the file was forbidden!");
                        }
                        break;
                    case delete:
                        if (response.getStatus() == 200) {
                            System.out.println("The response says that this file was deleted successfully!");
                        } else {
                            System.out.println("The response says that this file is not found!");
                        }
                        break;
                    default:
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Request getCommand() throws IOException {
        System.out.println("Enter action: (1 - get a file, 2 - save a file, 3 - delete a file)");
        String command = SCANNER.nextLine();
        switch (command.toLowerCase()) {
            case "1":
                return getFileRequest();
            case "2":
                return createFileRequest();
            case "3":
                return deleteFileRequest();
            case "exit":
                return new Request(RequestMethod.exit);
            default:
                System.out.println("invalid command");
        }

        return null;
    }

    private static Request getFileRequest() {
        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id)");
        String command = SCANNER.nextLine().trim();
        switch (command) {
            case "1":
                String name = getStringFromUser("Enter name of the file");
                return new Request(RequestMethod.get, name, 0);
            case "2":
                int id = Integer.parseInt(getStringFromUser("Enter id of the file"));
                return new Request(RequestMethod.get, null, id);
            default:
                System.out.println("Invalid command");
        }

        return getFileRequest();
    }

    private static Request createFileRequest() {
        String fileName = getStringFromUser("Enter name of the file");

        Path path = Path.of(FILE_PATH, fileName);
        if (Files.exists(path)) {
            String targetFileName = getStringFromUser("Enter name of the file to be saved on server");
            Request request = new Request(RequestMethod.put, targetFileName.isEmpty() ? fileName : targetFileName, 0);
            try {
                request.setData(Files.readAllBytes(path));
                return request;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File not found!");
        }

        return null;
    }

    private static Request deleteFileRequest() {
        System.out.println("Do you want to delete the file by name or by id (1 - name, 2 - id)");
        String command = SCANNER.nextLine().trim();
        switch (command) {
            case "1":
                String name = getStringFromUser("Enter name of the file");
                return new Request(RequestMethod.delete, name, 0);
            case "2":
                int id = Integer.parseInt(getStringFromUser("Enter id of the file"));
                return new Request(RequestMethod.delete, null, id);
            default:
                System.out.println("Invalid command");
        }

        return deleteFileRequest();
    }

    private static String getStringFromUser(String title) {
        System.out.println(title + ":");
        return SCANNER.nextLine();
    }

}
