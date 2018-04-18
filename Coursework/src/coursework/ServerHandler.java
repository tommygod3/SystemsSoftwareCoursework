package coursework;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerHandler implements Runnable
{
    DataInputStream inFromClient;
    DataOutputStream outToClient;
    String message = "";
    Socket client;
    public ArrayList<UserData> usersData = new ArrayList<>();
    String fileNameUserData = "userdata.txt";
    //Constructor that sets up input and output streams
    //Also loads in users data
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
        updateReadData();
    }
    
    //Update data structure
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
                    client.close();
                }
                if (message.equals("REGISTER"))
                {
                    FileWriter fileWriter = new FileWriter(fileNameUserData,true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(client.getInetAddress().getHostAddress() + ",");
                    bufferedWriter.write(inFromClient.readUTF() + ",");
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
                    }
                    else if (valid == 0)
                    {
                        outToClient.writeUTF("FAILURE");
                    }
                }
                updateReadData();
            }
            catch (IOException e)
            {
                System.err.println("Client " + client.getInetAddress() + " has disconnected");
                break;
            } 
        }
    }
}

