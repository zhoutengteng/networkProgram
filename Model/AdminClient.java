import java.net.*;
import java.io.*;
import java.util.*;

public class AdminClient {
    private String host = "localhost";
    private int port = 8001;
    private Socket socket;

    public AdminClient() throws IOException {
        
        socket = new Socket(host, port);
    }
    public static void main(String args[]) throws IOException {
        new AdminClient().talk();
    }
    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream socketOut = socket.getOutputStream();
        return new PrintWriter(socketOut, true);
    }
    private BufferedReader getReader(Socket socket) throws IOException {
        InputStream socketIn = socket.getInputStream();
        return  new BufferedReader(new InputStreamReader(socketIn));
    }
    public void talk() throws IOException {
        try{
            BufferedReader br = getReader(socket);
            PrintWriter pw = getWriter(socket);
            BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;
            while ((msg = localReader.readLine()) != null) {
                pw.println(msg);
                if (msg.equals("shutdown")) break;
            }
            while ((msg = br.readLine()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) { 
                    socket.close();
                }
            } catch (IOException e) {e.printStackTrace();}
        }

    }

}

