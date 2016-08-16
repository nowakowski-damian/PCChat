
package com.thirteendollars;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import com.sun.security.auth.module.JndiLoginModule;

public abstract class GUI extends javax.swing.JFrame {


	private static final long serialVersionUID = 1L;
    private final String MODE_SERVER="Server";
    private final String MODE_CLIENT="Client";
	private ArrayList<String> mUsersOnline = new ArrayList<>( ChatServer.MAX_CLIENTS_NUMBER );
	private boolean mIsShiftPressed=false;
	private Preferences mPreferences;
	private final String NICK_PREF_ID="nick_pref_id";
	private final String IP_PREF_ID="ip_pref_id";
	private final String PORT_PREF_ID="port_pref_id";
	private final String CHECKBOX_PREF_ID="checkbox_pref_id";

	
	private javax.swing.JTextArea jChatContentArea;
    private javax.swing.JTextArea jChatMessageArea;
    private javax.swing.JButton jConnectButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox<String> jModeBox;
    private javax.swing.JTextArea jOnlineUsersField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton jSendButton;
    private javax.swing.JCheckBox jSendCheckbox;
    private javax.swing.JTextField jServerAdressField;
    private javax.swing.JTextField jServerPortField;
    private javax.swing.JTextField jusernameField;
    

    public GUI() {
    	mPreferences = Preferences.userNodeForPackage(com.thirteendollars.GUI.class);
        initComponents();
        
        this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
		        mPreferences.put(NICK_PREF_ID, jusernameField.getText().trim() );
		        mPreferences.put(IP_PREF_ID, jServerAdressField.getText().trim() );
		        mPreferences.put(PORT_PREF_ID, jServerPortField.getText().trim() );
		        mPreferences.putBoolean(CHECKBOX_PREF_ID, jSendCheckbox.isSelected());
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				onWindowClosed();
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
    }
    
    
    public abstract void onWindowClosed();
	public abstract void onSend( String text);
	public abstract void onConnectAsClient(String username, String ipAddress, int portNumber);
	public abstract void onConnectAsServer( int portNumber);
	
	public void updateChatTextWindow(String text){
		jChatContentArea.setText(jChatContentArea.getText()+"\n"+text );
	}
	
	public void changeUserOnlineStatus(String user,boolean isOnline){
		if(isOnline){
			mUsersOnline.add(user);
			updateChatTextWindow(user+" is online.");
		}
		else{
			mUsersOnline.remove(user);
			updateChatTextWindow(user+" is offline.");
		}
		String usersList=new String();
		for( String userName : mUsersOnline ){
			usersList+=userName+"\n";
		}
		jOnlineUsersField.setText(usersList);
	}
	
	public void clearUsersOnlineStatus(){
		mUsersOnline.clear();
		jOnlineUsersField.setText("");
	}
	
	public void clearChatWindow(){
		jChatContentArea.setText("You are offline");
	}
                        
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jServerAdressField = new javax.swing.JTextField();
        jServerPortField = new javax.swing.JTextField();
        jModeBox = new javax.swing.JComboBox<>();
        jConnectButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jOnlineUsersField = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jChatContentArea = new javax.swing.JTextArea();
        jSendButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jChatMessageArea = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jusernameField = new javax.swing.JTextField();
        jSendCheckbox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 400));
        

        jLabel1.setText("Mode");

        jServerAdressField.setText( mPreferences.get(IP_PREF_ID,"192.168.13.111") );
        
        jServerPortField.setText( mPreferences.get(PORT_PREF_ID,"7000") );
        
        jusernameField.setText( mPreferences.get(NICK_PREF_ID,"Nick") );

        jModeBox.setMaximumRowCount(2);
        jModeBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { MODE_CLIENT, MODE_SERVER }));
        jModeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onModeChange( jModeBox.getSelectedItem().toString() );
			}
		});
        
        jConnectButton.setText("Connect");
        jConnectButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onConnect(evt);
            }
        });

        jOnlineUsersField.setEditable(false);
        jOnlineUsersField.setColumns(17);
        jOnlineUsersField.setRows(5);
        jScrollPane1.setViewportView(jOnlineUsersField);

        jChatContentArea.setEditable(false);
        jChatContentArea.setColumns(20);
        jChatContentArea.setRows(5);
        jChatContentArea.setText("You are offline");
        jScrollPane2.setViewportView(jChatContentArea);

        jSendButton.setText("Send");
        jSendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSend( jChatMessageArea.getText() );
				jChatMessageArea.setText("");
				
			}
		});
        
        jSendCheckbox.setText("Send on Enter");
        jSendCheckbox.setBorder(null);
        jSendCheckbox.setSelected( mPreferences.getBoolean(CHECKBOX_PREF_ID, true) );

        jChatMessageArea.setColumns(20);
        jChatMessageArea.setRows(5);
        jChatMessageArea.addKeyListener(new KeyListener() {
			
              	
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				


				if( jSendCheckbox.isSelected() && e.getKeyCode()==KeyEvent.VK_ENTER ){
				
					if(mIsShiftPressed){
						jChatMessageArea.setText(jChatMessageArea.getText()+"\n");
					}
					else if( !jChatMessageArea.getText().trim().isEmpty() ){
						jChatMessageArea.setText(jChatMessageArea.getText().trim());
						jSendButton.doClick();
					}
				}
				
				if( e.getKeyCode()==KeyEvent.VK_SHIFT ){
					mIsShiftPressed=false;
				}
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_SHIFT){
					mIsShiftPressed=true;
				}
				
			}
		});
        jScrollPane3.setViewportView(jChatMessageArea);

        jLabel2.setText("Server IP Address");
        jLabel3.setText("Server Port");
        jLabel4.setText("Username");
        


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                            .addComponent(jModeBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(30, 30, 30)
                        .addComponent(jConnectButton, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jSendCheckbox)
                                .addGap(45, 45, 45)
                                .addComponent(jSendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(8, 8, 8))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jusernameField)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(0, 112, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jServerAdressField, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jServerPortField, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))))
                .addGap(35, 35, 35))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jServerAdressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jServerPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jModeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jConnectButton)
                    .addComponent(jusernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSendButton)
                        .addComponent(jSendCheckbox))))
                .addGap(50, 50, 50))
        );

        pack();
    }                    


	private void onConnect(java.awt.event.ActionEvent evt) {                                               
		if( jModeBox.getSelectedItem().toString()==MODE_SERVER ){
			onConnectAsServer( Integer.parseInt( jServerPortField.getText() ) );
		}
		else{
			String username= jusernameField.getText();
			String ipAddress= jServerAdressField.getText();
			int portNumber=Integer.parseInt( jServerPortField.getText() );
			onConnectAsClient(username,ipAddress,portNumber);
		}
    	
    }  
    


	private void onModeChange(String mode) {
		if(mode==MODE_SERVER){
			jServerAdressField.setEditable(false);
			jusernameField.setEditable(false);
		}
		else{
			jServerAdressField.setEditable(true);
			jusernameField.setEditable(true);
		}
	}

                                            
                 

                 
}
