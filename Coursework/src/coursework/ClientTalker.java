package coursework;

import java.io.*;
import java.net.*;

public class ClientTalker 
{
    Socket server;
    
    public ClientTalker()
    {
        try
        {
            server = new Socket("localhost",9090);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
    
    public Boolean clientRegister(UserData theirData, Boolean existing)
    {
        Object in = null;
        String reply = null;
        String command = null;
        try
        {
            ObjectInputStream inFromServer = new ObjectInputStream(server.getInputStream());
            ObjectOutputStream outToServer = new ObjectOutputStream(server.getOutputStream());
            if (existing == false)
            {
                command = "REGISTER";
            }
            if (existing == true)
            {
                command = "SIGNIN";
            }
            outToServer.writeObject(command);
            outToServer.writeObject(theirData);
            in = inFromServer.readUTF();
            reply = (String) in;
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
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
}
