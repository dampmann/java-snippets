/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javassh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_ENTER;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 *
 * @author peer
 */
public class MainWindow {
    private final JFrame mainWindow;
    private final JPanel mainPanel;
    private final JPanel controlPanel;
    private final JPanel screenPanel;
    private final JPanel commandPanel;
    private final JSch jsch;
    private final JButton connectButton;
    private final JButton closeButton;
    private final JTextField userField;
    private final JTextField hostField;
    private final JTextField commandField;
    private final JTextArea screen;
    private Session session;
    private Channel channel;
    private ByteArrayInputStream bin;
    private ByteArrayOutputStream bout; 
    private byte[] commandBuffer;
    private Timer displayTimer;
    
    public MainWindow() {
        jsch = new JSch();
        try {
            jsch.addIdentity("/Users/peer/.ssh/WEB-SERVICES-I.pem");
        } catch (JSchException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        bout = new ByteArrayOutputStream(256);
        commandBuffer = new byte[256];
        bin = new ByteArrayInputStream(commandBuffer);
        mainWindow = new JFrame();
        mainWindow.setSize(400, 400);
        mainWindow.setTitle("Java SSH Client");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel = new JPanel();
        userField = new JTextField();
        userField.setText("peer");
        controlPanel.add(userField);
        hostField = new JTextField();
        hostField.setText("mysysadmin.de");
        controlPanel.add(hostField);
        connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> connectToHost());
        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> disconnectFromHost());
        controlPanel.add(connectButton);
        controlPanel.add(closeButton);
        screenPanel = new JPanel(new BorderLayout());
        screen = new JTextArea();
        screen.setEditable(false);
        screen.setText("HUHU");
        screenPanel.add(screen, BorderLayout.CENTER);
        commandField = new JTextField();
        commandField.addKeyListener( new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                return;
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("key pressed "+ e.getKeyChar() + " " + e.getKeyCode() + " " + VK_ENTER);
                if(e.getKeyCode() == VK_ENTER) {
                    String command = commandField.getText();
                    System.out.println(command);
                    commandField.removeAll();
                    System.arraycopy(command.getBytes(), 0 , commandBuffer, 0, command.getBytes().length);
                    commandBuffer[command.length()] = '\n';
                    System.out.println(commandBuffer);
                    bin.reset();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                return;
            }
            
        });
        
        commandPanel = new JPanel(new BorderLayout());
        commandPanel.add(commandField, BorderLayout.NORTH);
        commandPanel.setBorder(BorderFactory.createTitledBorder("Enter commands"));        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(screenPanel, BorderLayout.CENTER);
        mainPanel.add(commandPanel, BorderLayout.SOUTH);
        mainWindow.setContentPane(mainPanel);
        //mainWindow.pack();
        mainWindow.setVisible(true);
    }
    
    public void connectToHost() {
        try {
            session = jsch.getSession(userField.getText(), hostField.getText(), 8010);
            session.setUserInfo(new MyUserInfo());
            session.setInputStream(bin);
            session.setOutputStream(bout);
            session.connect();
            channel = session.openChannel("shell");
            channel.connect();
            displayTimer = new Timer(500, e -> {
                screen.removeAll();
                try {
                    System.out.println(bout.size());
                    screen.setText(bout.toString("UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            displayTimer.setRepeats(true);
            displayTimer.start();
            
        } catch (JSchException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void disconnectFromHost() {
        if(session != null) {
            session.disconnect();
            if(displayTimer != null) {
                displayTimer.stop();
            }
            screen.removeAll();
        }
    }
    
    public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {

        @Override
        public String getPassphrase() {
            return("");
        }

        @Override
        public String getPassword() {
            return("");
        }

        @Override
        public boolean promptPassword(String string) {
            return(true);
        }

        @Override
        public boolean promptPassphrase(String string) {
            return(true);
        }

        @Override
        public boolean promptYesNo(String string) {
            return(true);
        }

        @Override
        public void showMessage(String string) {
            return;
        }

        @Override
        public String[] promptKeyboardInteractive(String string, String string1, String string2, String[] strings, boolean[] blns) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    }
}
