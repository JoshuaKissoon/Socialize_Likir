package jk.socialize.theme;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import jk.socialize.system.core.content.Connections;
import jk.socialize.system.core.content.Profile;
import jk.socialize.system.core.content.UserData;
import jk.socialize.utilities.JGridBagLayout;

/**
 * @author Joshua Kissoon
 * @date 20131227
 * @desc JFrame that allows a user to manage his/her connections
 */
public class ManageConnections extends JFrame implements ActionListener
{

    /* Variable Declarations */
    private final JFrame frame = this;
    private final Profile cUserProfile;
    private final Connections cConnections;

    /* JFrame Components */
    private JPanel mainPanel, userPanel;
    private JLabel lbl;
    private JScrollPane scrollPane;
    private JButton btn;

    /* Layout Manager */
    private GridBagConstraints gbc;

    public ManageConnections(Profile p)
    {
        /* Save the profile reference locally */
        this.cUserProfile = p;

        /* Retrieve the User Data Object */
        this.cConnections = this.cUserProfile.getConnections();

        /* Create the UI */
        this.createGUI();
    }

    /**
     * @description Here we create the search frame GUI
     * Our Frame GUI will basically contain a search box and a panel area to display results
     */
    private void createGUI()
    {
        /* Setting up the main panel */
        mainPanel = new JPanel(new GridBagLayout());

        /**
         * @section Here we load the different connections of this user
         * 1. For each entry within the user's connections
         * 1.1. Load the profile for the user that relationship object is pointing to
         * 1.2. Load the user data object from that profile
         * 1.3. We then display that user's information from his/her UserData Object
         * @todo - This can be improved by storing the user's UserData object reference in the relationship object along with the profile
         */
        HashMap<String, String> connections = this.cConnections.getConnections();
        Integer count = 0;
        for (Map.Entry<String, String> entry : connections.entrySet())
        {
            /* 1.1. Loading the connection's Profile */
            String connectionUid = entry.getKey();
            Profile connProfile = new Profile(this.cUserProfile.getNode(), connectionUid);

            if (!connProfile.profileExists())
            {
                /* If no profile exist on the DHT, then ignore displaying friend */
                break;
            }

            connProfile.loadProfile();

            /* 1.2. Load the user data object from this profile */
            UserData connUserData = connProfile.getUserData();

            /* 1.3. Display the user's information */
            userPanel = new JPanel(new GridBagLayout());

            /* User's Name */
            lbl = new JLabel("Name: " + connUserData.getData(UserData.DATA_NAME));
            gbc = JGridBagLayout.getLabelConstraints(0, 0);
            userPanel.add(lbl, gbc);

            /* View Profile Button */
            btn = new JButton("View Profile");
            btn.setName(connectionUid);
            btn.setActionCommand("viewProfile");
            btn.addActionListener(this);
            gbc = JGridBagLayout.getItemConstraints(1, 0);
            userPanel.add(btn, gbc);
            
            /* Add the user Panel to the main panel */
            gbc = JGridBagLayout.getItemConstraints(0, count);
            mainPanel.add(userPanel, gbc);
            count++;
        }

        /* Add the Main panel to the frame */
        scrollPane = new JScrollPane(mainPanel);
        frame.getContentPane().add(scrollPane);
    }

    /**
     * @description Call the necessary methods to display
     */
    public void showGUI()
    {
        frame.pack();
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        System.out.println(event);
        JButton src = (JButton) event.getSource();
        System.out.println("ID: " + src.getName());
        switch (event.getActionCommand())
        {
            case "viewProfile":
            /* View the profile of a user */
//                this.cUserData.putData(UserData.DATA_NAME, nameTF.getText());
//                this.cUserData.putData(UserData.DATA_SOCIALIZE_USERNAME, usernameTF.getText());
//                this.cUserData.putData(UserData.DATA_DOB, dobTF.getText());
//                try
//                {
//                    cUserProfile.getNode().storeLocallyAndUniversally(this.cUserData);
//                }
//                catch (IOException | InterruptedException | ExecutionException ioe)
//                {
//                    ioe.printStackTrace();
//                }
//                break;
        }
    }
}
