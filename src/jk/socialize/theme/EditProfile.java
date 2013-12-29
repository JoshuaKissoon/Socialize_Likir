package jk.socialize.theme;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import jk.socialize.system.core.content.Profile;
import jk.socialize.system.core.content.UserData;
import jk.socialize.utilities.JGridBagLayout;

/**
 * @author Joshua Kissoon
 * @date 20131227
 * @desc JFrame that allows a user to edit his/her own profile
 */
public class EditProfile extends JFrame implements ActionListener
{

    /* Variable Declarations */
    private final JFrame frame = this;
    private final Profile cUserProfile;
    private final UserData cUserData;

    /* JFrame Components */
    private JPanel mainPanel;
    private JLabel lbl;
    private JScrollPane scrollPane;
    private JTextField nameTF, usernameTF, dobTF;
    private JButton btn;

    /* Layout Manager */
    private GridBagConstraints gbc;

    public EditProfile(Profile p)
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
        nameTF = new JTextField(this.cUserData.getData(UserData.DATA_NAME).toString(), 20);
        gbc = JGridBagLayout.getItemConstraints(1, 0);
        mainPanel.add(nameTF, gbc);

        /* User's socialize username */
        lbl = new JLabel("Socialize Username: ");
        gbc = JGridBagLayout.getLabelConstraints(0, 1);
        mainPanel.add(lbl, gbc);
        usernameTF = new JTextField(this.cUserData.getData(UserData.DATA_SOCIALIZE_USERNAME).toString(), 15);
        gbc = JGridBagLayout.getItemConstraints(1, 1);
        mainPanel.add(usernameTF, gbc);

        /* User's dob */
        lbl = new JLabel("DOB: ");
        gbc = JGridBagLayout.getLabelConstraints(0, 2);
        mainPanel.add(lbl, gbc);
        dobTF = new JTextField(this.cUserData.getData(UserData.DATA_DOB).toString(), 20);
        gbc = JGridBagLayout.getItemConstraints(1, 2);
        mainPanel.add(dobTF, gbc);

        /* Adding buttons */
        btn = new JButton("Save");
        btn.addActionListener(this);
        btn.setActionCommand("save");
        gbc = JGridBagLayout.getItemConstraints(0, 4);
        mainPanel.add(btn, gbc);

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
        switch (event.getActionCommand())
        {
            case "save":
                /* Update the UserData values on the DHT */
                this.cUserData.putData(UserData.DATA_NAME, nameTF.getText());
                this.cUserData.putData(UserData.DATA_SOCIALIZE_USERNAME, usernameTF.getText());
                this.cUserData.putData(UserData.DATA_DOB, dobTF.getText());
                try
                {
                    cUserProfile.getNode().storeLocallyAndUniversally(this.cUserData);
                }
                catch (IOException | InterruptedException | ExecutionException ioe)
                {
                    ioe.printStackTrace();
                }
                break;
        }
    }
}
