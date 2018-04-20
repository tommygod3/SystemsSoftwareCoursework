/*
 * @author Tommy Godfrey, Tyler Knowles
 */
package coursework;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerHandler implements Runnable
{
    String message = "";
    Socket client;
    public ArrayList<UserData> usersData = new ArrayList<>();
    public ArrayList<UserData> onlineData = new ArrayList<>();
    UserData clientsData = new UserData();
    String fileNameUserData = "userdata.txt";
    String fileNameOnlineUsers = "onlineusers.txt";
    
    //Constructor that sets up input and output streams
    //Also loads in users data
    public ServerHandler(Socket c)
    {
        client = c;
        updateReadData();
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
    
    //Send current online users
    public void sendOnline()
    {
        try
        {
            ObjectOutputStream outObject = new ObjectOutputStream(client.getOutputStream());
            outObject.writeObject(onlineData);
        }
        catch (IOException e)
        {
            
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
        catch(FileNotFoundException e) 
        {
            System.err.println("Unable to open file: " + fileNameUserData);                
        }
        catch(IOException e) 
        {
            System.err.println("Error reading file: "+ fileNameUserData);                  
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
        catch(FileNotFoundException e) 
        {
            System.err.println("Unable to open file: " + fileNameOnlineUsers);                
        }
        catch(IOException e) 
        {
            System.err.println("Error reading file: "+ fileNameOnlineUsers);                  
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
            catch (IOException e)
            {
                System.err.println("Unable to write to file " + fileNameOnlineUsers);
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
            catch (IOException e)
            {
                System.err.println("Unable to write to file " + fileNameOnlineUsers);
            }
        }
    }
    
    //Logs off current client connected
    public void removeUser() throws IOException
    {
        updateWriteOnlineUser(clientsData.username,false);
        client.close();
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
                if (message.equals("LOGOUT"))
                {
                    removeUser();
                }
                if (message.equals("REGISTER"))
                {
                    FileWriter fileWriter = new FileWriter(fileNameUserData,true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(client.getInetAddress().getHostAddress() + ",");
                    String registeredUsername = inFromClient.readUTF();
                    bufferedWriter.write(registeredUsername + ",");
                    bufferedWriter.write(inFromClient.readUTF() + ",");
                    bufferedWriter.write(inFromClient.readUTF() + ",");
                    bufferedWriter.write(inFromClient.readUTF() + ",");
                    int numOfTastes = Integer.parseInt(inFromClient.readUTF());
                    for (int i = 0; i < numOfTastes; i++)
                    {
                        bufferedWriter.write(inFromClient.readUTF() + ",");
                    }
                    bufferedWriter.newLine();
                    outToClient.writeUTF("SUCCESS");
                    bufferedWriter.close();
                    updateReadData();
                    updateWriteOnlineUser(registeredUsername,true);
                    clientsData = userSearch(registeredUsername);
                }
                if (message.equals("LOGIN"))
                {
                    int valid = 0;
                    InetAddress clientIP = client.getInetAddress();
                    String logUsername = inFromClient.readUTF();
                    String logPassword = inFromClient.readUTF();
                    for (int i = 0; i < usersData.size(); i++)
                    {
                        if ((usersData.get(i).ip.equals(clientIP))&&(usersData.get(i).username.equals(logUsername))&&(usersData.get(i).password.equals(logPassword)))
                        {
                            valid = 1;
                        }
                    }
                    if (valid == 1)
                    {
                        outToClient.writeUTF("SUCCESS");
                        updateWriteOnlineUser(logUsername,true);
                        clientsData = userSearch(logUsername);
                    }
                    else if (valid == 0)
                    {
                        outToClient.writeUTF("FAILURE");
                    }
                    updateReadData();
                }
                if (message.equals("UPDATEONLINE"))
                {
                    sendOnline();
                }
            }
            catch (IOException e)
            {
                System.err.println("Client " + client.getInetAddress() + " has disconnected");
                try
                {
                    removeUser();
                }
                catch (IOException w)
                {
                    System.err.println("Client " + client.getInetAddress() + " already disconnected");
                }
                break;
            } 
        }
    }
}

