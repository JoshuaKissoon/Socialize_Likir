package jk.socialize.theme;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import jk.socialize.system.core.content.Profile;
import jk.socialize.system.core.content.UserData;
import jk.socialize.utilities.JGridBagLayout;

/**
 * @author Joshua Kissoon
 * @date 20131227
 * @desc JFrame that allows a user to view his/her own profile
 */
public class ViewOwnProfile extends JFrame
{

    /* Variable Declarations */
    private final JFrame frame = this;
    private final Profile cUserProfile;
    private final UserData cUserData;

    /* JFrame Components */
    private JPanel mainPanel;
    private JLabel lbl;
    private JScrollPane scrollPane;

    /* Layout Manager */
    private GridBagConstraints gbc;

    public ViewOwnProfile(Profile p)
    {
        /* Save the profile reference locally */
        this.cUserProfile = p;
        
        /* Retrieve the User Data Object */
        this.cUserData = this.cUserProfile.getUserData();
        
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

        /* User's Name */
        lbl = new JLabel("Name: ");
        gbc = JGridBagLayout.getLabelConstraints(0, 0);
        mainPanel.add(lbl, gbc);

        lbl = new JLabel(this.cUserData.getData(UserData.DATA_NAME).toString());
        gbc = JGridBagLayout.getLabelConstraints(1, 0);
        mainPanel.add(lbl, gbc);

        /* User's socialize username */
        lbl = new JLabel("Socialize Username: ");
        gbc = JGridBagLayout.getLabelConstraints(0, 1);
        mainPanel.add(lbl, gbc);
        lbl = new JLabel(this.cUserData.getData(UserData.DATA_SOCIALIZE_USERNAME).toString());
        gbc = JGridBagLayout.getLabelConstraints(1, 1);
        mainPanel.add(lbl, gbc);

        /* User's dob */
        lbl = new JLabel("DOB: ");
        gbc = JGridBagLayout.getLabelConstraints(0, 2);
        mainPanel.add(lbl, gbc);
        lbl = new JLabel(this.cUserData.getData(UserData.DATA_DOB).toString());
        gbc = JGridBagLayout.getLabelConstraints(1, 2);
        mainPanel.add(lbl, gbc);

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
}
