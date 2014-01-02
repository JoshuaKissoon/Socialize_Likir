/**
 * @author Joshua Kissoon
 * @desc Grabs and displays the home feed
 * @created 20140102
 */
package jk.socialize.theme;

import java.util.HashMap;
import javax.swing.JPanel;
import unito.likir.NodeId;

public class HomeFeed extends JPanel
{

    /* Main UI Components */
    private JPanel mainPanel;

    /* Main Variables */
    private HashMap<String, NodeId> posts = new HashMap<>();

    public HomeFeed()
    {

    }

    /**
     * @desc Create the feeds panel GUI
     */
    public void createGUI()
    {

    }

    /**
     * @return JPanel with the feeds
     */
    public JPanel getFeeds()
    {
        return mainPanel;
    }

    /**
     * @desc Loads the posts of connections
     */
    public void loadConnectionsPosts()
    {

    }
}
