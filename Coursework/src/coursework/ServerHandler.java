package coursework;

import java.net.*;
import java.io.*;

public class ServerHandler implements Runnable
{
    DataInputStream inFromClient;
    DataOutputStream outToClient;
    String message = "";
    Socket client;
    //Constructor that sets up input and output streams
    public ServerHandler(Socket c)
    {
        client = c;
        try
        {
            inFromClient = new DataInputStream(client.getInputStream());
            outToClient = new DataOutputStream(client.getOutputStream());
        }
        catch (IOException e)
        {
            System.err.println("Error, can't connect! - " + e.getMessage());
        }
    }
    //Run function for multithreading
    public void run()
    {
        //At the moment server functionality is useless and placeholder
        while (true)
        {
            try
            {
                message = inFromClient.readUTF();
                System.out.println("In from client " + client.getInetAddress() + ": " + message);
                outToClient.writeUTF(message.toUpperCase());
            }
            catch (IOException e)
            {
                System.err.println("Client " + client.getInetAddress() + " has disconnected");
                break;
            } 
        }
    }
}

