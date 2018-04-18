package coursework;

import java.net.*;
import java.io.*;

public class Client
{
    public UserData myData = new UserData();
    Socket server;
    DataInputStream inFromServer;
    DataOutputStream outToServer;
    String reply = null;
    
    public Client() throws IOException
    {
        server = new Socket("localhost",9090);
        inFromServer = new DataInputStream(server.getInputStream());
        outToServer = new DataOutputStream(server.getOutputStream());
        
        login window = new login(myData);
        window.setVisible(true);
        
        myData.ip = InetAddress.getLocalHost();
        //Wait for login form to be completed
        String check = null;
        while (check == null)
        {
            check = myData.username;
            System.out.println("Waiting for login");
        }
        //Login:
        if (myData.placeOfBirth == null)
        {
            logIn();
        }
        //Register:
        else
        {
            register();
        }
        
        //reply = inFromServer.readUTF();
        //System.out.println(reply);
        
        logOut();
    }
    
    public static void main(String[] args) throws IOException
    {
        Client me = new Client();
    }
    
    public void logIn() throws IOException
    {
        System.out.println("Attempting to log in as: " + myData.username);
            
        outToServer.writeUTF("LOGIN");
        outToServer.writeUTF(myData.username);
        outToServer.writeUTF(myData.password);
        reply = inFromServer.readUTF();
        if(reply.equals("SUCCESS"))
        {
            System.out.println("Logged in as " + myData.username);
        }
        if(reply.equals("FAILURE"))
        {
            System.out.println("Failed to log in");
        }
    }

    public void register() throws IOException
    {
        System.out.println("Attempting to make new user: " + myData.username);
        
        outToServer.writeUTF("REGISTER");
        outToServer.writeUTF(myData.username);
        outToServer.writeUTF(myData.password);
        outToServer.writeUTF(myData.placeOfBirth);
        outToServer.writeUTF(myData.dateOfBirth);
        String numOfTastes = Integer.toString(myData.listOfTastes.size());
        outToServer.writeUTF(numOfTastes);
        for (int i = 0; i < myData.listOfTastes.size(); i++)
        {
            outToServer.writeUTF(myData.listOfTastes.get(i));
        }
        reply = inFromServer.readUTF();
        if(reply.equals("SUCCESS"))
        {
            System.out.println("New user made: " + myData.username);
        }
        if(reply.equals("FAILURE"))
        {
            System.out.println("Cannot make new user");
        }
    }
    
    public void logOut() throws IOException
    {
        outToServer.writeUTF("LOGOUT");
        server.close();
    }
}
