package coursework;

import java.net.*;
import java.io.*;

public class Client 
{
    public static void main(String[] args) throws IOException
    {
        BufferedReader kbd = new BufferedReader(new InputStreamReader(System.in));
        Socket server = new Socket("localhost",9090);
        DataInputStream inFromServer = new DataInputStream(server.getInputStream());
        DataOutputStream outToServer = new DataOutputStream(server.getOutputStream());
        while(true)
        {
            String input = kbd.readLine();
            String reply = null;
            outToServer.writeUTF(input);
            if (input.equals("Quit"))
            {
                break;
            }
            else
            {
                reply = inFromServer.readUTF();
                System.out.print(reply);
            }
        }
        server.close();
    }
}
