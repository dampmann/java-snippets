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
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;

/**
 *
 * @author peer
 */
public class MainWindow {
    private final JFrame mainWindow;
    private final JPanel mainPanel;
    private final JPanel controlPanel;
    private final JPanel screenPanel;
    private final JSch jsch;
    private final JButton connectButton;
    private final JButton closeButton;
    private final JTextField userField;
    private final JTextField hostField;
    private final JTextField statusField;
    private final JTextArea screen;
    private Session session;
    private Channel channel;
    private ByteArrayOutputStream bout; 
    private Timer displayTimer;
    private PipedInputStream pin;
    private PipedOutputStream pout;
    
    public MainWindow() {
        jsch = new JSch();
        try {
            jsch.addIdentity("/Users/peer/.ssh/PEER-AWS-Cloudian.pem");
        } catch (JSchException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        bout = new ByteArrayOutputStream(256);
        pout = new PipedOutputStream();
        try {
            pin = new PipedInputStream(pout);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

        mainWindow = new JFrame();
        mainWindow.setSize(400, 400);
        mainWindow.setTitle("Java SSH Client");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userField = new JTextField();
        userField.setText("ec2-user");
        controlPanel.add(userField);
        hostField = new JTextField();
        hostField.setText("13.59.62.96");
        controlPanel.add(hostField);
        connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> connectToHost());
        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> disconnectFromHost());
        controlPanel.add(connectButton);
        controlPanel.add(closeButton);
        screenPanel = new JPanel(new BorderLayout());
        screen = new JTextArea();
        screen.setEditable(true);
        screen.addKeyListener( new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume();
                return;
            }

            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    pout.write(e.getKeyChar());
                    e.consume();
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                return;
            }
            
        });
        screenPanel.add(screen, BorderLayout.CENTER);
        statusField = new JTextField();
        statusField.setEditable(false);
        statusField.setBackground(UIManager.getColor("TextField.disabledBackground"));
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(screenPanel), BorderLayout.CENTER);
        mainPanel.add(statusField, BorderLayout.SOUTH);
        mainWindow.setContentPane(mainPanel);
        mainWindow.setVisible(true);
    }
    
    public void connectToHost() {
        try {
            session = jsch.getSession(userField.getText(), hostField.getText(), 22);
            session.setUserInfo(new MyUserInfo());
            session.setInputStream(pin);
            session.setOutputStream(bout);
            session.connect();
            channel = session.openChannel("shell");
            channel.connect();
            displayTimer = new Timer(500, e -> {
                //screen.removeAll();
                try {
                    //System.out.println(bout.size());
                    screen.append(bout.toString("UTF-8"));
                    bout.reset();
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
