/*
 * @author Tommy Godfrey, Tyler Knowles
 */
package coursework;

import java.net.*;
import java.io.*;
import java.util.*;

public class Client
{
    public UserData myData = new UserData();
    Socket server;
    DataInputStream inFromServer;
    DataOutputStream outToServer;
    MainWindow mainWindow;
    String reply = null;
    public ArrayList<UserData> usersData = new ArrayList<>();
    public ArrayList<UserData> onlineData = new ArrayList<>();
    Runnable updater = () -> 
    {
        while(true)
        {
            try
            {
                updateOnlineData();
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                
            }
        }
    };
    
    //Update online list
    public void updateOnlineData()
    {
        try
        {
            outToServer.writeUTF("UPDATEONLINE");
            ObjectInputStream in = new ObjectInputStream(server.getInputStream());
            Object a = in.readObject();
            ArrayList<UserData> b = (ArrayList<UserData>) a;
            onlineData = b ;
            mainWindow.setOnlineUsers(onlineData);
        }
        catch (IOException e)
        {
            
        }
        catch (ClassNotFoundException e)
        {
            
        }
    }
    
    public Client() throws IOException
    {
        server = new Socket("localhost",9090);
        inFromServer = new DataInputStream(server.getInputStream());
        outToServer = new DataOutputStream(server.getOutputStream());
        
        
        login loginWindow = new login(myData);
        loginWindow.setVisible(true);
        
        myData.ip = InetAddress.getLocalHost();
        //Wait for login form to be completed
        String check = null;
        while (check == null)
        {
            check = myData.username;
            System.out.println("Waiting for login");
        }
        Boolean success = false;
        //Login:
        if (myData.placeOfBirth == null)
        {
            success = logIn();
        }
        //Register:
        else
        {
            success = register();
        }
        if (success)
        {
            new Thread(updater).start();
            mainWindow = new MainWindow(this);
            mainWindow.setVisible(true);
        }
    }
    
    public static void main(String[] args) throws IOException
    {
        Client me = new Client();
    }
    
    public Boolean logIn() throws IOException
    {
        System.out.println("Attempting to log in as: " + myData.username);
            
        outToServer.writeUTF("LOGIN");
        outToServer.writeUTF(myData.username);
        outToServer.writeUTF(myData.password);
        reply = inFromServer.readUTF();
        if(reply.equals("SUCCESS"))
        {
            
            System.out.println("Logged in as " + myData.username);
            return true;
        }
        if(reply.equals("FAILURE"))
        {
            System.out.println("Failed to log in");
        }
        return false;
    }

    public Boolean register() throws IOException
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
            return true;
        }
        if(reply.equals("FAILURE"))
        {
            System.out.println("Cannot make new user");
        }
        return false;
    }
    
    public void logOut() throws IOException
    {
        outToServer.writeUTF("LOGOUT");
        System.out.println("Logging out");
        server.close();
    }
}
