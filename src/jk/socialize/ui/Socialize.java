package jk.socialize.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import jk.socialize.content.Profile;
import jk.socialize.objects.SocializeNode;
import jk.socialize.system.Session;
import unito.likir.settings.PropFinder;
import unito.likir.settings.Settings;

/**
 * @author Joshua Kissoon
 * @description The main UI for the Socialize SN
 */
public class Socialize extends JFrame implements WindowListener, ActionListener
{

    /* Main Components */
    private final JFrame frame = this;
    private JPanel mainPanel, content, sidebar;
    private JPanel panel;
    private SocializeNode node;
    private Profile cUserProfile;

    /* Menus */
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;

    /* Layouts */
    private GridBagConstraints gbc;

    /* Form Components */
    private JButton btn;

    /* Thread Management */
    private final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(20);

    /* Other frame components */
    private JScrollPane scrollPane;

    public Socialize()
    {
        /* Here we connect the node to the network */
        this.initializeNode();

        /* Create and Load the GUI */
        this.createGUI();
    }

    private void initializeNode()
    {
        /* Create and Initialize the node */
        String userId = Session.userId;
        File f = new File(PropFinder.get(Settings.NODE_PERSISTENCE_PATH) + File.separator + userId.toLowerCase() + ".state");
        if (f.exists())
        {
            try
            {
                node = new SocializeNode(f);
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
                node = new SocializeNode(userId);
            }
        }
        else
        {
            node = new SocializeNode(userId);
        }

        try
        {
            node.init(); //the new node is initialized (startup + bootstrap)
        }
        catch (IOException ioe)
        {
            System.err.println("Error in node initialization");
            System.exit(0);
        }

        /* Lets load the profile for this node */
        cUserProfile = new Profile(node, node.getUserId());

        /* Creates a new profile if the profile does not exist */
        if (!cUserProfile.nodeProfileExists())
        {
            System.out.println("No profile Exists; lets create a new profile");
            cUserProfile.createProfile();
        }
        else
        {
            /* Profile Exist, load it */
            System.out.println("Profile Found, lets load it");
            cUserProfile.loadProfile();
        }

        /* Schedule the node to update it's content every 5 minutes */
        scheduledExecutor.schedule(node.getStorageCleaner(), 2, TimeUnit.MINUTES);
    }

    private void createGUI()
    {
        /* 1. MAIN MENU BAR */
        menuBar = new JMenuBar();
        menuBar.add(Box.createRigidArea(new Dimension(0, 35)));

        /* Home Menu */
        menu = new JMenu("Home");

        menuItem = new JMenuItem("Home");
        menu.add(menuItem);

        menuBar.add(menu);

        /* Profile Menu */
        menu = new JMenu("Profile");

        menuItem = new JMenuItem("View Profile");
        menu.add(menuItem);

        menuBar.add(menu);

        /* Friends Menu */
        menu = new JMenu("Friends");

        menuItem = new JMenuItem("Find Friends");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("friendSearch");
        menu.add(menuItem);

        menuBar.add(menu);

        /* 2. MAIN PANEL SETUP */
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        /* 4. CONTENT */
        content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(null);

        /* 4.1. Put the "Add Status Form" at the top of the content area */
        panel = new StatusAddForm(cUserProfile);
        gbc = getItemConstraints(5, 5);
        content.add(panel, gbc);

        /* Add the content to the main panel */
        scrollPane = new JScrollPane(content);
        scrollPane.setMinimumSize(new Dimension(600, 400));
        scrollPane.setPreferredSize(new Dimension(800, 800));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        /* 6. SIDEBAR */
        sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(Color.LIGHT_GRAY);

        /* Add the sidebar to the main panel */
        scrollPane = new JScrollPane(sidebar);
        scrollPane.setMinimumSize(new Dimension(200, 400));
        scrollPane.setPreferredSize(new Dimension(200, 800));
        mainPanel.add(scrollPane, BorderLayout.EAST);

        /* Adding the main Panel to the frame */
        scrollPane = new JScrollPane(mainPanel);
        //scrollPane.setMinimumSize(new Dimension(800, 400));
        //scrollPane.setPreferredSize(new Dimension(1000, 800));        
        frame.getContentPane().add(scrollPane);

        frame.addWindowListener(this);
        frame.setTitle("Socialize: ---  " + node.getUserId() + " --- " + node.getAddress().toString());
        frame.setJMenuBar(menuBar);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        switch (event.getActionCommand())
        {
            case "friendSearch":
                SearchFrame sf = new SearchFrame(cUserProfile);
                sf.showGUI();
                break;
        }
    }

    @Override
    public void windowClosing(WindowEvent wEvent)
    {
        /* When the window is closing, we save the node's state */
        node.exit(true);
    }

    @Override
    public void windowClosed(WindowEvent wEvent)
    {
    }

    @Override
    public void windowOpened(WindowEvent wEvent)
    {
        System.out.println("\n\n ******************* Node Storage Printing Starting ******************* \n\n");
        System.out.println(node.getStorage().toString());
        System.out.println("\n\n ******************* Node Storage Printing Ended ******************* \n\n");
    }

    @Override
    public void windowIconified(WindowEvent wEvent)
    {
    }

    @Override
    public void windowDeiconified(WindowEvent wEvent)
    {
    }

    @Override
    public void windowActivated(WindowEvent wEvent)
    {
    }

    @Override
    public void windowDeactivated(WindowEvent wEvent)
    {
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
