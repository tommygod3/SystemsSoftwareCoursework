/*
 * @author Tommy Godfrey, Tyler Knowles
 */
package coursework;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ChatServerHandler implements Runnable
{
    Socket client;
    ObjectInputStream inFromClient;
    ObjectOutputStream outToClient;
    UserData clientData;
    UserData toData;
    public ArrayList<String> messageHistory = new ArrayList<>();
    String fileNameChatStart= "chat/";
    String fileNameChatEnd= "chat.txt";
    String fileNameChat;
    
    Runnable updater = () -> 
    {
        while(true)
        {
            try
            {
                updateReadChat();
                Thread.sleep(500);
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
        }
    };
    
    //Constructor sets up who client is and reads into data from file
    public ChatServerHandler(Socket c)
    {
        client = c;
        try
        {
            Object i = null;
            inFromClient = new ObjectInputStream(client.getInputStream());
            outToClient = new ObjectOutputStream(client.getOutputStream());
            i = inFromClient.readObject();
            clientData = (UserData) i;
            i = inFromClient.readObject();
            toData = (UserData) i;
            try
            {
                String middle;
                if (clientData.username.compareTo(toData.username) < 0)
                {
                    middle = clientData.username + toData.username;
                }
                else
                {
                    middle = toData.username + clientData.username;
                }
                fileNameChat = fileNameChatStart + middle + fileNameChatEnd;
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
            
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        new Thread(updater).start();
    }

    public Boolean redirecor()
    {
        Object in = null;
        String command = null;
        String message = null;
        try
        {
            in = inFromClient.readObject();
            command = (String) in;
            System.out.println(command);
            if (command.equals("SEND"))
            {
                in = inFromClient.readObject();
                message = (String) in;
                updateWriteChat(message);
            }
            if (command.equals("GET"))
            {
                outToClient.writeObject(messageHistory);
            }
            if (command.equals("LEAVE"))
            {
                client.close();
                return true;
            }
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        return false;
    }
    
    
    public void updateReadChat()
    {
        
        messageHistory.clear();
        ArrayList<String> messagesIn = new ArrayList<>();
        File file = new File(fileNameChat);
        if(!file.exists()) 
        { 
            try
            {
                file.createNewFile();
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
        }
        else
        {
            try
            {
                FileReader fileReader = new FileReader(fileNameChat);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String parser = bufferedReader.readLine();
                while (parser != null)
                {
                    messagesIn.add(parser);
                    parser = bufferedReader.readLine();
                }
                bufferedReader.close();
            }
            catch(Exception e) 
            {
                System.err.println(e.getMessage());                
            }
            messageHistory = messagesIn;
        }
    }
    
    public void updateWriteChat(String message)
    {
        try
        {
            FileWriter fileWriter = new FileWriter(fileNameChat, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(clientData.username + "," + message);
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
        catch (Exception e)
        {
            System.err.println("Error writing chat: " + e.getMessage());
        }
    }
    
    public void run()
    {
        while (true)
        {
            if (redirecor() == true)
            {
                break;
            }
        }
    }
}