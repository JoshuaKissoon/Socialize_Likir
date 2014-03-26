/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jk.socialize.theme;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import jk.socialize.system.core.Session;

/**
 *
 * @author Joshua Kissoon
 * @description A class that takes care of logging in the user
 * @date 20131023
 */
public class Login extends JFrame implements ActionListener
{

    /* Main Components */
    private final JFrame frame = this;
    private final JPanel form;

    /* Form Items */
    private final JTextField userNameTF;
    private final JPasswordField passwordTF;
    private final JButton btn;
    private JLabel label;

    /* Layout Manager Components */
    private GridBagConstraints gbc;
    
    public Login()
    {
        form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        label = new JLabel("Username: ");
        gbc = getLabelConstraints(4, 4);
        form.add(label, gbc);
        
        userNameTF = new JTextField(20);
        gbc = getItemConstraints(5, 4);
        form.add(userNameTF, gbc);
        
        label = new JLabel("Password: ");
        gbc = getLabelConstraints(4, 5);
        form.add(label, gbc);
        
        passwordTF = new JPasswordField(20);
        gbc = getItemConstraints(5, 5);
        form.add(passwordTF, gbc);
        
        btn = new JButton("Login");
        btn.setActionCommand("login");
        btn.addActionListener(this);
        gbc = getItemConstraints(4, 8);
        form.add(btn, gbc);

        /* Adding the form to the frame */
        //form.setPreferredSize(new Dimension());
        frame.getContentPane().add(form);
        
        frame.setTitle("Socialize: Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand().equals("login"))
        {
            /* Login the user */
            Session.userId = userNameTF.getText();
            Session.password = new String(passwordTF.getPassword());
            Socialize soc = new Socialize(Session.password);
            frame.dispose();
        }
    }
    
    private GridBagConstraints getLabelConstraints(int x, int y)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.weightx = 0.0;
        c.weighty = 0.1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        
        return c;
    }
    
    private GridBagConstraints getItemConstraints(int x, int y)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;
        
        return c;
    }
}
