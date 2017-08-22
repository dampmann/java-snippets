/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javassh;

import javax.swing.SwingUtilities;

/**
 *
 * @author peer
 */
public class JavaSSH {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JavaSSH().startUp());
    }
    
    public void startUp() {
        MainWindow w = new MainWindow();
    }
    
}
