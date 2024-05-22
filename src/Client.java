import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    public static String ip = "localhost";
    public static int port = 8080;

    public static void main(String[] args) {
        new ClientSomething(ip, port);
    }
}
class ClientSomething {
    private Socket clientSocket;
    private BufferedReader inputUser;
    private BufferedReader in;
    private BufferedWriter out;
    private String ip;
    private int port;
    private String nickname;
    private Date time;
    private String dtime;
    private SimpleDateFormat dt;

    public ClientSomething(String ip, int port) {
        this.port = port;
        this.ip = ip;

        try {
            this.clientSocket = new Socket(ip, port);
        } catch (IOException e) {
            System.out.println("Socket failed");
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.pressNickname();
            new ReadMsg().start();
            new WriteMsg().start();
        } catch (IOException e) {
            ClientSomething.this.downService();
        }
    }

    private void pressNickname() {
        System.out.println("Enter your nickname");
        try {
            nickname = inputUser.readLine();
            out.write("Hello " + nickname + "\n");
            out.flush();
        } catch (IOException e) {
        }
    }

    private class ReadMsg extends Thread {
        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = in.readLine();
                    if (str.equals("exit")) {
                        ClientSomething.this.downService();
                        break;
                    }
                    System.out.println(str);
                }
            } catch (IOException e) {
                ClientSomething.this.downService();
            }
        }
    }

    private void downService() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {

        }
    }

    public class WriteMsg extends Thread {
        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    time = new Date();
                    dt = new SimpleDateFormat("HH:mm:ss");
                    dtime = dt.format(time);
                    userWord = inputUser.readLine();
                    if (userWord.equals("exit")) {
                        out.write("exit" + "\n");
                        ClientSomething.this.downService();
                        break;
                    } else {
                        out.write("(" + dtime + ") " + nickname + ": " + userWord + "\n");
                    }
                    out.flush();
                } catch (IOException e) {

                }
            }
        }
    }
}

