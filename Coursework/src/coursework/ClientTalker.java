package coursework;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientTalker 
{
    Socket server;
    ObjectOutputStream outToServer;
    ObjectInputStream inFromServer;
    
    public ClientTalker()
    {
        try
        {
            server = new Socket("localhost",9090);
            outToServer = new ObjectOutputStream(server.getOutputStream());
            inFromServer = new ObjectInputStream(server.getInputStream());
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
    
    //Tell server client is logging out
    public void logOut()
    {
        String command = "LOGOUT";
        try
        {
            outToServer.writeObject(command);
            server.close();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
    
    //Returns list of user data, 0 = all users, 1 = online, 2 = friends
    public ArrayList<UserData> clientGetUsers(int selection)
    {
        ArrayList<UserData> onlineUsers = null;
        String command = null;
        Object reply = null;
        if (selection == 0)
        {
            command = "GETALL";
        }
        if (selection == 1)
        {
            command = "GETONLINE";
        }
        if (selection == 2)
        {
            command = "GETFRIENDS";
        }
        try
        {
            outToServer.writeObject(command);
            reply = inFromServer.readObject();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        onlineUsers = (ArrayList<UserData>) reply;
        return onlineUsers;
    }
    
    //Sends user data to either log in or register to server, false = register, true = log in
    public Boolean clientRegister(UserData theirData, Boolean existing)
    {
        Object in = null;
        String reply = null;
        String command = null;
        if (existing == false)
        {
            command = "REGISTER";
        }
        if (existing == true)
        {
            command = "LOGIN";
        }
        try
        {
            outToServer.writeObject(command);
            outToServer.writeObject(theirData);
            in = inFromServer.readObject();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        reply = (String) in;
        if(reply.equals("SUCCESS"))
        {
            return true;
        }
        if (reply.equals("FAILURE"))
        {
            return false;
        }
        return false;
    }
    
    //Send username to server and recieve user data
    public UserData getUserdata(String username)
    {
        UserData desiredData = null;
        Object reply = null;
        try
        {
            outToServer.writeObject("GETDATA");
            outToServer.writeObject(username);
            reply = inFromServer.readObject();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        desiredData = (UserData) reply;
        return desiredData;
    }
}
