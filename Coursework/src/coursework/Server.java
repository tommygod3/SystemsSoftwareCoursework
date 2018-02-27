/*
 * @author Tommy Godfrey, Tyler Knowles
 */
package coursework;

import java.net.*;
import java.io.*;

public class Server 
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket server = new ServerSocket(9090);
        while (true)
        {
            //Wait for clients to connect
            System.out.println("Waiting...");
            Socket client = server.accept();
            //Client connected, inform and show address
            System.out.println("Connected to " + client.getInetAddress());
            //Assign each client to a thread
            ServerHandler t = new ServerHandler(client);
            Thread th = new Thread(t);
            th.start();
        }
    }
}
