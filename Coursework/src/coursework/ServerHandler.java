/*
 * @author Tommy Godfrey, Tyler Knowles
 */
package coursework;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerHandler implements Runnable
{
    Socket client;
    ObjectInputStream inFromClient;
    ObjectOutputStream outToClient;
    public ArrayList<UserData> usersData = new ArrayList<>();
    public ArrayList<UserData> onlineData = new ArrayList<>();
    UserData clientsData;
    String fileNameUserData = "userdata.txt";
    String fileNameOnlineUsers = "onlineusers.txt";
    Runnable updater = () -> 
    {
        while(true)
        {
            try
            {
                updateReadData();
                updateReadOnlineUsers();
                Thread.sleep(2000);
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
        }
    };
    
    //Constructor sets up who client is and reads into data from file
    public ServerHandler(Socket c)
    {
        client = c;
        try
        {
            inFromClient = new ObjectInputStream(client.getInputStream());
            outToClient = new ObjectOutputStream(client.getOutputStream());
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        updateReadData();
    }
    
    //Takes input then does relevant function for output
    public Boolean redirector()
    {
        String command = null;
        UserData dataU = null;
        String dataS = null;
        Object in = null;
        try
        {
            in = inFromClient.readObject();
            command = (String) in;
            if ((command.equals("REGISTER"))||(command.equals("LOGIN")))
            {
                in = inFromClient.readObject();
                dataU = (UserData) in;
            }
            if (command.equals("GETDATA"))
            {
                in = inFromClient.readObject();
                dataS = (String) in;
            }
            System.out.println("In from client " + client.getInetAddress() + ": " + command);
        }
        catch (Exception e)
        {
            System.err.println("Error with redirect message: " +e.getMessage());
        }
       if (command.equals("LOGOUT"))
       {
           logoutUser();
           return true;
       }
       if (command.equals("GETALL"))
       {
           sendUsers(0);
       }
       if (command.equals("GETONLINE"))
       {
           sendUsers(1);
       }
       if (command.equals("GETFRIENDS"))
       {
           sendUsers(2);
       }
       if (command.equals("GETDATA"))
       {
           sendOneUser(dataS);
       }
       if (command.equals("LOGIN"))
       {
           loginClient(dataU);
       }
       if (command.equals("REGISTER"))
       {
           registerClient(dataU);
       }
       return false;
    }
    
    //Function to get user data from username
    public UserData userSearch(String desiredUsername)
    {
        UserData found = null;
        for (int i = 0; i < usersData.size(); i++)
        {
            if (usersData.get(i).username.equals(desiredUsername))
            {
                found = usersData.get(i);
            }
        }
        return found;
    }
    
    public void sendOneUser(String username)
    {
        UserData dataToSend = null;
        try
        {
            dataToSend = userSearch(username);
            outToClient.writeObject(dataToSend);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
    
    //Send list of users to client, 0 = all users, 1 = online
    public void sendUsers(int selection)
    {
        ArrayList<UserData> usersToSend = new ArrayList<>();
        if (selection == 0)
        {
            usersToSend = usersData;
        }
        if (selection == 1)
        {
            usersToSend = onlineData;
        }
        if (selection == 2)
        {
            //usersToSend = the users friends data...
        }
        try
        {
            outToClient.writeObject(usersToSend);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
    
    //Update all user data from database
    public void updateReadData()
    {
        String parser = null;
        try
        {
            FileReader fileReader = new FileReader(fileNameUserData);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            parser = bufferedReader.readLine();
            while (parser != null)
            {
                String[] dataLine = parser.split(",");
                UserData thisLine = new UserData();
                thisLine.ip = InetAddress.getByName(dataLine[0]);
                thisLine.username = dataLine[1];
                thisLine.password = dataLine[2];
                thisLine.placeOfBirth = dataLine[3];
                thisLine.dateOfBirth = dataLine[4];
                if (dataLine.length > 5)
                {
                    int index = 5;
                    String taste = dataLine[index];
                    while(taste.equals("Opera") || taste.equals("Rock") || taste.equals("Pop"))
                    {
                        thisLine.listOfTastes.add(taste);
                        index++;
                        if(dataLine.length > index)
                        {
                            taste = dataLine[index];
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                usersData.add(thisLine);
                parser = bufferedReader.readLine();
            }
            bufferedReader.close();
        }
        catch(Exception e) 
        {
            System.err.println(e.getMessage());                
        }
        
    }
    
    //Update online users from database, depends on all user data
    public void updateReadOnlineUsers()
    {
        String parser = null;
        try
        {
            FileReader fileReader = new FileReader(fileNameOnlineUsers);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            parser = bufferedReader.readLine();
            while (parser != null)
            {
                String userLine = parser;
                UserData thisLine = new UserData();
                thisLine = userSearch(userLine);
                onlineData.add(thisLine);
                parser = bufferedReader.readLine();
            }
            bufferedReader.close();
        }
        catch(Exception e) 
        {
            System.err.println(e.getMessage());              
        }
    }
    
    //Add an online user to database, updates local data structure
    public void updateWriteOnlineUser(String onlineUser, Boolean loggingIn)
    {
        //If true: log in, if false: log out
        if (loggingIn == true)
        {
            try
            {
                FileWriter fileWriter = new FileWriter(fileNameOnlineUsers, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(onlineUser);
                bufferedWriter.newLine();
                bufferedWriter.close();
                updateReadOnlineUsers();
            }
            catch (Exception e)
            {
                System.err.println("Error writing log in: " + e.getMessage());
            }
        }
        else
        {
            try
            {
                File oldOnline = new File(fileNameOnlineUsers);
                File tempOnline = new File("temponline.txt");
                BufferedReader bufferedReader = new BufferedReader(new FileReader(oldOnline));
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempOnline));
                String userToRemove = onlineUser;
                String parser = bufferedReader.readLine();
                while (parser != null)
                {
                    String trimmed = parser.trim();
                    if (trimmed.equals(userToRemove))
                    {
                        parser = bufferedReader.readLine();
                        continue;
                    }
                    bufferedWriter.write(parser + System.getProperty("line.separator"));
                    parser = bufferedReader.readLine();
                }
                bufferedWriter.close();
                bufferedReader.close();
                
                oldOnline.delete();
                tempOnline.renameTo(oldOnline);
            }
            catch (Exception e)
            {
                System.err.println("Error writing log out: " +e.getMessage());
            }
        }
    }
    
    //Logs off current client connected
    public void logoutUser() 
    {
        try
        {
            int x = 10;
            updateWriteOnlineUser(clientsData.username,false);
            client.close();
        }
        catch (Exception e)
        {
            System.err.println("Error logging out: " + e.getMessage());
        }
    }
    
    public void loginClient(UserData dataIn)
    {
        try
        {
            String usernameIn = dataIn.username;
            String passwordIn = dataIn.password;
            Boolean valid = false;
            for (int i = 0; i < usersData.size(); i++)
            {
                if ((usersData.get(i).username.equals(usernameIn))&&(usersData.get(i).password.equals(passwordIn)&&(usersData.get(i).ip.equals(client.getInetAddress()))))
                {
                    valid = true;
                }
            }
            if (valid == true)
            {
                outToClient.writeObject("SUCCESS");
                updateWriteOnlineUser(usernameIn,true);
                clientsData = userSearch(usernameIn);
            }
            else if (valid == false)
            {
                outToClient.writeObject("FAILURE");
            }
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
    
    public void registerClient(UserData dataIn)
    {
        try
        {
            FileWriter fileWriter = new FileWriter(fileNameUserData,true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(client.getInetAddress() + ",");
            bufferedWriter.write(dataIn.username + ",");
            bufferedWriter.write(dataIn.password + ",");
            bufferedWriter.write(dataIn.placeOfBirth + ",");
            bufferedWriter.write(dataIn.dateOfBirth + ",");
            for(int i = 0; i < dataIn.listOfTastes.size(); i++)
            {
                bufferedWriter.write(dataIn.listOfTastes.get(i) + ",");
            }
            bufferedWriter.newLine(); 
            outToClient.writeObject("SUCCESS"); 
            bufferedWriter.close();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        updateWriteOnlineUser(dataIn.username,true);
        clientsData = userSearch(dataIn.username);
    }
    
    //Run function for multithreading
    public void run()
    {
        //At the moment server functionality is useless and placeholder
        while (true)
        {
            try
            {
                if (redirector() == true)
                {
                    break;
                }
            }
            catch (Exception e)
            {
                System.err.println("Error redirecting: " +e.getMessage());
                break;
            } 
        }
    }
}

