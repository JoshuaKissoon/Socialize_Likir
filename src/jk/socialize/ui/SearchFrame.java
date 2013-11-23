package jk.socialize.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import jk.socialize.content.ConnectionRequests;
import jk.socialize.content.Profile;
import jk.socialize.content.Relationship;
import unito.likir.NodeId;
import unito.likir.storage.StorageEntry;

/**
 * @author Joshua Kissoon
 * @description A class that generates a search frame to allow the user to search for content
 * @date 20131025
 */
public class SearchFrame extends JFrame implements ActionListener
{

    /* Main objects */
    private final SearchFrame frame = this;
    private Profile cUserProfile;          // The node of the currently logged in user

    /* Frame Components */
    private JPanel mainPanel, resultsPanel;
    private JScrollPane scrollPane;

    /* Form Components */
    private JTextField keywordTF;
    private JLabel label;

    public SearchFrame(Profile iProfile)
    {
        /* Set the node to the input node */
        this.cUserProfile = iProfile;

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
        mainPanel = new JPanel(new BorderLayout());

        /* Adding the search keyword TF to the main panel */
        keywordTF = new JTextField();
        keywordTF.addKeyListener(new KeyListener()
        {
            /* Adding a key listener to the search keyword text field*/
            @Override
            public void keyTyped(KeyEvent e)
            {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {

            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    /* Call the search method */
                    frame.search();
                }
            }
        });
        mainPanel.add(keywordTF, BorderLayout.NORTH);

        /* Adding the Results Panel to the main panel */
        resultsPanel = new JPanel();
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

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
        frame.setVisible(true);
    }

    /**
     * @description Method to do a search and populate the results panel with the search result
     */
    public void search()
    {
        /* Search for the specified user */

        Collection<StorageEntry> results = null;
        try
        {
            results = cUserProfile.getNode().get(Profile.generateKey(keywordTF.getText()), Profile.type, null, true, 5).get();
        }
        catch (InterruptedException | ExecutionException ie)
        {
            System.err.println("Searcher Interrupted");
        }

        /* Now we display the result onto the result panel */
        StorageEntry profileSE = null;
        long recency = 0;
        for (StorageEntry e : results)
        {
            /* Select the most recent result */
            if (e.getSubmissionTime() > recency)
            {
                recency = e.getSubmissionTime();
                profileSE = e;
            }
        }

        /* Add this friend's profile to the frame */
        resultsPanel.add(new UserDisplay(profileSE));

        /* Refresh the frame */
        frame.repaint();
        frame.revalidate();
    }

    /**
     * @author Joshua Kissoon
     * @date 20131103
     * @desc An Inner class that displays the person details on search result
     */
    private class UserDisplay extends JPanel
    {

        /* GUI Components */
        private final JPanel userPanel = this;
        private JLabel lbl;
        private JButton btn;
        private GridBagConstraints gbc;

        /* Main Components */
        private Profile userProfile = null;

        private UserDisplay(StorageEntry iProfileSE)
        {
            if (iProfileSE != null)
            {
                userProfile = new Profile(cUserProfile.getNode(), iProfileSE.getContent().getValue());

                this.buildGUI();
            }

        }

        /**
         * @desc Method that puts together the ui
         */
        private void buildGUI()
        {
            userPanel.setLayout(new GridBagLayout());

            /* User's uid */
            lbl = new JLabel(userProfile.getUid());
            gbc = getGBConstraints(0, 0);
            userPanel.add(lbl, gbc);

            /* Add Connection Button */
            btn = new JButton("Connect");
            btn.addActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    /**
                     * When a user click connect to connect to another user:
                     * 1. We create a new Relationship Object and store it on the DHT
                     * 3. Append o to the connections of the requester
                     * 2. Append o to the connection requests object of the requestee
                     */
                    Relationship r = new Relationship(cUserProfile.getNode().getUserId(), userProfile.getUid());
                    cUserProfile.getNode().put(r);

                    /* Get the connections request object of the requestee */
                    ConnectionRequests userCr = userProfile.getConnectionRequests();
                    System.out.println(userCr);
                    userCr.addConnectionRequest(cUserProfile.getUid(), r.getKey());

                    try
                    {
                        cUserProfile.getNode().storeLocallyAndUniversally(userCr);
                    }
                    catch (IOException | InterruptedException | ExecutionException ioe)
                    {
                        ioe.printStackTrace();
                    }

                    /* Lets get the connection requests again and see if it's updated */
                    ConnectionRequests userCr2 = userProfile.getConnectionRequests();
                    System.out.println(userCr2);
                }
            });
            gbc = getGBConstraints(0, 1);
            userPanel.add(btn, gbc);
        }
    }

    private GridBagConstraints getGBConstraints(int x, int y)
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

    /* LISTENERS */
    @Override
    public void actionPerformed(ActionEvent aE)
    {

    }
}
