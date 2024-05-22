import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
    public static final int PORT = 8080;
    public static LinkedList<ServerSomething> serverList = new LinkedList<>();
    public static Story story;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        story = new Story();
        System.out.println("server started");
        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new ServerSomething(socket));
                } catch (Exception e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}

class ServerSomething extends Thread{
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ServerSomething(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Server.story.printStory(out);
        start();
    }

    @Override
    public void run() {
        String word;
        try {
            word = in.readLine();
            try {
                out.write(word + "\n");
                out.flush();
            } catch (IOException e) {
            }
            try {
                while (true) {
                    word = in.readLine();
                    if (word.equals("exit")) {
                        this.downService();
                        break;
                    }
                    System.out.println("Echoing " + word);
                    Server.story.addStory(word);
                    for (ServerSomething sr : Server.serverList) {
                        sr.send(word);
                    }
                }
            } catch (NullPointerException e) {
            }
        } catch (IOException e) {
            this.downService();
        }
    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerSomething sr : Server.serverList) {
                    if (sr.equals(this)) {
                        sr.interrupt();
                    }
                    Server.serverList.remove(this);
                }
            }
        } catch (IOException e) {}
    }

    private void send(String word) {
        try {
            out.write(word + "\n");
            out.flush();
        } catch (IOException e) {
        }
    }
}
class Story {
    private LinkedList<String> story = new LinkedList<>();

    public void printStory(BufferedWriter writer) {
        if (story.size() > 0) {
            try {
                writer.write("History message " + "\n");
                for (String s : story) {
                    writer.write(s + "\n");
                }
                writer.write("/...." + "\n");
                writer.flush();
            } catch (IOException e) {}
        }
    }

    public void addStory(String word) {
        if (story.size() > 0) {
            story.removeFirst();
            story.add(word);
        } else {
            story.add(word);
        }
    }
}