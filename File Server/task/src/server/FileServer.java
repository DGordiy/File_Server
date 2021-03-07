package server;

import Service.Request;
import Service.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileServer {

    private final int PORT = 8080;
    private final String SERVER_ADDR = "127.0.0.1";
    private final String FILE_PATH = "C:\\Users\\Denis\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data";
    private final String FILE_PATH_TO_LOGS = "C:\\Users\\Denis\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\logs";

    private final Map<Integer, String> filesId = new HashMap<>();

    FileServer() {
        start();
    }

    public Response put(Request request) {
        Path path = Path.of(FILE_PATH, request.getFileName());
        boolean isError = true;
        if (Files.notExists(path)) {
            try {
                Files.write(path, request.getData());
                isError = false;
            } catch (IOException e) {}
        }

        int id = updateFilesId(request.getFileName());
        if (id == 0) {
            isError = true;
        }

        if (!isError) {
            Response response = new Response(200);
            response.setMessage("ID = " + id);
            return response;
        } else {
            return new Response(403);
        }
    }

    public Response get(Request request) {
        String filename = request.getFileName() != null ? request.getFileName() : filesId.getOrDefault(request.getFileId(), null);
        if (filename == null) {
            return new Response(404);
        }

        Path path = Path.of(FILE_PATH, filename);
        byte[] data = null;
        if (Files.exists(path)) {
            try {
                data = Files.readAllBytes(path);
            } catch (IOException e) {}
        }

        if (data != null) {
            return new Response(200, data);
        } else {
            return new Response(404);
        }
    }

    public Response delete(Request request) {
        String filename = request.getFileName() != null ? request.getFileName() : filesId.getOrDefault(request.getFileId(), null);
        if (filename == null) {
            return new Response(404);
        }

        Path path = Path.of(FILE_PATH, filename);
        boolean isError = true;
        if (Files.exists(path)) {
            try {
                Files.delete(path);
                isError = false;
            } catch (IOException e) {}
        }

        if (!isError) {
            return new Response(200);
        } else {
            return new Response(404);
        }
    }

    private boolean isCorrectFileName(String fileName) {
        return fileName.matches("file(\\d|10)");
    }

    private void start() {

        readFilesId();

        System.out.println("Server started!");

        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(SERVER_ADDR))) {
            Request request;
            boolean stopServer = false;
            do {
                Socket socket = serverSocket.accept();
                ObjectInputStream inputStream = new ObjectInputStream(new DataInputStream(socket.getInputStream()));
                ObjectOutputStream outputStream = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));

                //Reading and writing data
                try {
                    request = (Request) inputStream.readObject();

                    switch (request.getMethod()) {
                        case get:
                            outputStream.writeObject(get(request));
                            break;
                        case put:
                            outputStream.writeObject(put(request));
                            break;
                        case delete:
                            outputStream.writeObject(delete(request));
                            break;
                        case exit:
                            stopServer = true;
                            break;
                        default:
                            outputStream.writeObject(new Response(400));
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } while (!stopServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int updateFilesId(String fileName) {
        if (!filesId.containsValue(fileName)) {
            try (PrintWriter pw = new PrintWriter(Path.of(FILE_PATH_TO_LOGS, "filesId.svs").toFile())) {
                pw.println(fileName);
                filesId.put(filesId.size() + 1, fileName);
                return filesId.size();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    private void readFilesId() {
        filesId.clear();

        Path path = Path.of(FILE_PATH_TO_LOGS, "filesId.svs");
        if (Files.exists(path)) {
            try (Scanner scanner = new Scanner(new FileInputStream(path.toFile()))) {
                while (scanner.hasNextLine()) {
                    filesId.put(filesId.size() + 1, scanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
