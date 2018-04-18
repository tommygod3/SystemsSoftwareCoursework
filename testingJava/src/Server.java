import java.io.*;
import java.net.*;
import java.awt.event.*;


public class Server extends javax.swing.JFrame {

    private DataOutputStream output;
    private DataInputStream input;
    private ServerSocket server;
    private Socket connection;
        
    public Server() {
        setTitle("Server");
        initComponents();
        userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
                                        try{
                                            output.writeUTF("SERVER - " + e.getActionCommand());
                                            output.flush();
                                            chatWindow.append("\nSERVER - " + e.getActionCommand());
                                        }catch(IOException ioe){
                                            chatWindow.append("\n ERROR: CANNOT SEND MESSAGE, PLEASE RETRY");
                                        }
                                        userText.setText("");
                                }
			}
		);
        setVisible(true);
    
    }

    public void startRunning(){
		try{
			server = new ServerSocket(9090, 100); 
			while(true){
				try{
					//connect
					chatWindow.append("Waiting for someone to connect... \n");
                                        connection = server.accept();
                                        chatWindow.append("Now connected to " + connection.getInetAddress().getHostName());
                                        //
					//streams
                                        output = new DataOutputStream(connection.getOutputStream());
                                        output.flush();
                                        //
                                        input = new DataInputStream(connection.getInputStream());
					//chat
                                        String message;
                                        try{
                                            output.writeUTF("SERVER - You are now connected! ");
                                            output.flush();
                                            chatWindow.append("SERVER - You are now connected!");
                                        }catch(IOException ioe){
                                            chatWindow.append("\nERROR: CANNOT SEND MESSAGE, PLEASE RETRY");
                                        }
                                        do{
                                            message = input.readUTF();
                                            chatWindow.append("\n" + message);
                                        }while(!message.equals("CLIENT - END"));
                                        //
				}catch(EOFException e){
					chatWindow.append("\nServer ended the connection! ");
				} finally{
					chatWindow.append("\nClosing Connections... \n");
                                        try{
                                            //close streams and connection
                                                output.close();
                                                input.close();
                                                connection.close();
                                                //
                                        }catch(IOException ioe){
                                                ioe.printStackTrace();
                                        }
				}
			}
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        userText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatWindow = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        chatWindow.setColumns(20);
        chatWindow.setRows(5);
        jScrollPane1.setViewportView(chatWindow);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addComponent(userText))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(userText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea chatWindow;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField userText;
    // End of variables declaration//GEN-END:variables
}
