package coursework;

import java.net.*;
import java.io.*;

public class Client 
{
    public static UserData data;
    public static void main(String[] args) throws IOException
    {
        Socket server = new Socket("localhost",9090);
        login window = new login(data);
        window.setVisible(true);
        data.ip = InetAddress.getLocalHost();
        DataInputStream inFromServer = new DataInputStream(server.getInputStream());
        DataOutputStream outToServer = new DataOutputStream(server.getOutputStream());
        
        String reply = null;
            
        String check = data.username;
        
      
        while (check == null)
        {
            check = data.username;
            System.out.println("Waiting");
            
        }
        
        
        outToServer.writeUTF(data.username);

        reply = inFromServer.readUTF();
        System.out.println(reply);
            
        
        server.close();
    }
}
