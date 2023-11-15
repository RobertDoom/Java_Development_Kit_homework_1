import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private static List<String> chatHistory = new ArrayList<>();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            sendChatHistory();
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received message: " + message);
                chatHistory.add(message);
                broadcastMessage(message);
                saveChatHistory();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendChatHistory() throws IOException {
        for (String message : chatHistory) {
            writer.write(message + "\n");
            writer.flush();
        }
    }

    private void broadcastMessage(String message) throws IOException {
        for (ClientHandler client : Server.clients) {
            if (client != this) {
                client.sendMessage(message);
            }
        }
    }

    private void sendMessage(String message) throws IOException {
        writer.write(message + "\n");
        writer.flush();
    }

    private void saveChatHistory() {
        try (BufferedWriter historyWriter = new BufferedWriter(new FileWriter("chat_history.txt"))) {
            for (String message : chatHistory) {
                historyWriter.write(message + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
