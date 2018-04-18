
import java.io.*;
import java.net.*;
import java.awt.event.*;


public class Client extends javax.swing.JFrame{

    private DataOutputStream output;
    private DataInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;
    

    public Client(String host){
                setTitle("Client");
                initComponents();
		serverIP = host;
		userText.addActionListener(
				new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try{
                                            output.writeUTF("CLIENT - " + e.getActionCommand());
                                            output.flush();
                                            chatWindow.append("\nCLIENT - " + e.getActionCommand());
                                        }catch(IOException ioe){
                                            chatWindow.append("\nOops! Something went wrong!");
                                        }
                                        
					userText.setText("");
				}
			}
		);
		setVisible(true);
	}    

        
	public void startRunning(){
		try{
                        //connect
			chatWindow.append("Attempting connection... \n");
                        connection = new Socket(InetAddress.getByName(serverIP), 9090);
                        chatWindow.append("Connection Established! Connected to: " + connection.getInetAddress().getHostName());
                        //
                        //streams
			output = new DataOutputStream(connection.getOutputStream());
                        output.flush();
                        input = new DataInputStream(connection.getInputStream());
                        //
			//chat
                        do{
                            message = (String) input.readUTF();
                            chatWindow.append("\n" + message);
                        }while(!message.equals("SERVER - END"));	
                        //
                        
		}catch(EOFException e){
			chatWindow.append("\nClient terminated the connection");
		}catch(IOException ioe){
			ioe.printStackTrace();
		}finally{
			chatWindow.append("\nClosing the connection!");
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
	

        
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        userText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatWindow = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        userText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userTextActionPerformed(evt);
            }
        });

        chatWindow.setColumns(20);
        chatWindow.setRows(5);
        jScrollPane1.setViewportView(chatWindow);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .addComponent(userText))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(userText, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTextActionPerformed


    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea chatWindow;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField userText;
    // End of variables declaration//GEN-END:variables
}
