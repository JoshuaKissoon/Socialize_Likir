package jk.socialize.theme;

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
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import jk.socialize.system.core.content.Profile;
import jk.socialize.system.abstraction.SocializeNode;
import jk.socialize.system.core.Session;
import jk.socialize.system.core.content.ConnectionRequests;
import jk.socialize.system.core.content.User;
import jk.socialize.utilities.JGridBagLayout;
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
    private JPanel mainPanel, content, sidebar, homeFeedsPanel;
    private JPanel panel;
    private SocializeNode node;
    private Profile cUserProfile;
    private javax.swing.Timer timer;

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
    private Boolean frameVisible = true;

    public Socialize(String iPassword)
    {
        /* Here we connect the node to the network */
        this.initializeNode(iPassword);

        /* Create and Load the GUI */
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                createGUI();
            }
        });
    }

    private void initializeNode(String iPassword)
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
            System.err.println("Error in node initialization. Error: " + ioe.getMessage());
            System.exit(0);
        }

        /* Lets load the profile for this node */
        cUserProfile = new Profile(node, node.getUserId());

        /* Creates a new profile if the profile does not exist */
        if (!cUserProfile.profileExists())
        {
            System.out.println("No profile Exists; lets create a new profile");
            cUserProfile.createProfile(iPassword);
        }
        else
        {
            /* Profile Exist, load it */
            System.out.println("Profile Found, lets load it");
            cUserProfile.loadProfile();
            /* Lets get the connection requests again and see if it's updated */
            ConnectionRequests userCr = cUserProfile.getConnectionRequests();
            User user = cUserProfile.getUserData();
            if(!user.isUserPassword(iPassword))
            {
                JOptionPane.showMessageDialog(null, "Incorrect password");
                this.frameVisible = false;
                new Login();
            }
            System.out.println(userCr);
        }

        /* Schedule the node to update it's content every 2 minutes */
        scheduledExecutor.schedule(node.getStorageCleaner(), 2, TimeUnit.MINUTES);

        /* Schedule the updating of the user home feed every minute */
        timer = new Timer(6000, new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("********************** Scheduled home feed update started. *********************************************\n\n");
                homeFeedsPanel.removeAll();
                HomeFeed homeFeed = new HomeFeed(cUserProfile);
                homeFeedsPanel.add(homeFeed.getFeeds());
                homeFeedsPanel.revalidate();
                homeFeedsPanel.repaint();
                System.out.println("*****************************Scheduled home feed update ended. ********************************************\n\n");
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void createGUI()
    {
        /* 1. MAIN MENU BAR */
        menuBar = new JMenuBar();
        menuBar.add(Box.createRigidArea(new Dimension(0, 35)));

        /* Home Menu */
        menu = new JMenu("Home");
        menu.setIcon(new ImageIcon("images/home.png"));

        menuItem = new JMenuItem("Home");
        menuItem.setIcon(new ImageIcon("images/home.png"));
        menu.add(menuItem);

        menu.setMargin(new Insets(10, 10, 10, 10));
        menuBar.add(menu);
        /* Profile Menu */
        menu = new JMenu("Profile");
        menu.setIcon(new ImageIcon("images/profile.png"));

        menuItem = new JMenuItem("View Profile");
        menuItem.setIcon(new ImageIcon("images/profile.png"));
        menuItem.addActionListener(this);
        menuItem.setActionCommand("viewProfile");
        menu.add(menuItem);

        menuItem = new JMenuItem("Edit Profile");
        menuItem.setIcon(new ImageIcon("images/edit.png"));
        menuItem.addActionListener(this);
        menuItem.setActionCommand("editProfile");
        menu.add(menuItem);

        menu.setMargin(new Insets(10, 10, 10, 10));
        menuBar.add(menu);

        /* Friends Menu */
        menu = new JMenu("Connections");
        menu.setIcon(new ImageIcon("images/connections.png"));

        menuItem = new JMenuItem("View Connections");
        menuItem.setIcon(new ImageIcon("images/connections.png"));
        menuItem.addActionListener(this);
        menuItem.setActionCommand("manageConnections");
        menu.add(menuItem);

        menuItem = new JMenuItem("Find Connections");
        menuItem.setIcon(new ImageIcon("images/search.png"));
        menuItem.addActionListener(this);
        menuItem.setActionCommand("connectionSearch");
        menu.add(menuItem);

        menu.setMargin(new Insets(10, 10, 10, 10));
        menuBar.add(menu);

        /* Help Menu */
        menu = new JMenu("Help");
        menu.setIcon(new ImageIcon("images/help.png"));

        menuItem = new JMenuItem("Print Node Storage");
        menuItem.setIcon(new ImageIcon("images/print.png"));
        menuItem.addActionListener(this);
        menuItem.setActionCommand("printNodeStorage");
        menu.add(menuItem);

        menu.setMargin(new Insets(10, 10, 10, 10));
        menuBar.add(menu);

        /* 2. MAIN PANEL SETUP */
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        /* 4. CONTENT */
        content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(null);

        /* 4.1. Put the "Add Status Form" at the top of the content area */
        panel = new StatusAddForm(cUserProfile, this);
        gbc = JGridBagLayout.getItemConstraints(5, 5);
        content.add(panel, gbc);

        /* 4.2. Adding the home feed to the main panel */
        HomeFeed homeFeed = new HomeFeed(cUserProfile);
        gbc = JGridBagLayout.getItemConstraints(5, 10);
        gbc.gridheight = 10;
        homeFeedsPanel = new JPanel();
        homeFeedsPanel.setBackground(Color.WHITE);
        homeFeedsPanel.add(homeFeed.getFeeds());
        content.add(homeFeedsPanel, gbc);

        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        /* Add the content to the main panel */
        scrollPane = new JScrollPane(content);
        scrollPane.setMinimumSize(new Dimension(600, 400));
        scrollPane.setPreferredSize(new Dimension(600, 600));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        /* 6. SIDEBAR */
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.LIGHT_GRAY);

        /* Add the sidebar to the main panel */
        scrollPane = new JScrollPane(sidebar);
        scrollPane.setMinimumSize(new Dimension(200, 400));
        scrollPane.setPreferredSize(new Dimension(200, 600));
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
        frame.setVisible(this.frameVisible);
    }
    
    public void addSidebarMessage(SocializeMessage message)
    {
        sidebar.add(message);
        sidebar.revalidate();
        sidebar.repaint();
    }
    
    @Override
    public void actionPerformed(ActionEvent event)
    {
        switch (event.getActionCommand())
        {
            case "manageConnections":
                ManageConnections mc = new ManageConnections(cUserProfile);
                mc.showGUI();
                break;
            case "connectionSearch":
                FriendSearch sf = new FriendSearch(cUserProfile);
                sf.showGUI();
                break;
            case "viewProfile":
                ViewProfile vop = new ViewProfile(cUserProfile);
                vop.showGUI();
                break;
            case "editProfile":
                EditProfile ep = new EditProfile(cUserProfile);
                ep.showGUI();
                break;
            case "printNodeStorage":
                System.out.println("\n*****************************  Printing Node Storage  ***************************************\n");
                System.out.println(this.node.getStorage().toString());
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
}
