/**
 * @author Joshua Kissoon
 * @desc Grabs and displays the home feed
 * @created 20140102
 */
package jk.socialize.theme;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jk.socialize.system.core.content.Connections;
import jk.socialize.system.core.content.PostsReference;
import jk.socialize.system.core.content.Profile;
import jk.socialize.system.core.content.Status;
import jk.socialize.utilities.JGridBagLayout;
import unito.likir.NodeId;

public class HomeFeed extends JPanel
{

    /* Main UI Components */
    private JPanel mainPanel;
    private JPanel postPanel;
    private JLabel lbl;
    private GridBagConstraints gbc;

    /* Main Variables */
    private TreeMap<String, PostItemData> posts = new TreeMap<>();
    private final Profile cUserProfile;

    /* Some constants */
    private static final Integer MAX_POSTS_PER_CONNECTION = 10;

    public HomeFeed(Profile iP)
    {
        /* Get the Profile */
        this.cUserProfile = iP;

        /* Load the user's connections */
        this.loadConnectionsPosts();

        /* Create the GUI */
        this.createGUI();
    }

    /**
     * @desc Create the feeds panel GUI
     */
    public void createGUI()
    {
        mainPanel = new JPanel(new GridBagLayout());

        /* For each post here, add them to the main panel */
        Integer counter = 0;
        for (Map.Entry<String, PostItemData> entry : this.posts.entrySet())
        {
            String timestamp = entry.getKey();
            PostItemData itemData = entry.getValue();
            NodeId statusNid = new NodeId(itemData.postNodeId.getBytes());

            /* Load the status */
            Status st = new Status();
            st = (Status) this.cUserProfile.getNode().getContent(statusNid, itemData.ownerUid, st);

            String date = "";
            try
            {
                Date temp = new Date((long) Integer.parseInt(timestamp) * 1000L); // *1000 is to convert seconds to milliseconds
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(temp); // the format of your date
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            postPanel = new JPanel(new GridBagLayout());
            lbl = new JLabel("     " + itemData.ownerUid + " posted on " + date, JLabel.LEFT);
            gbc = JGridBagLayout.getItemConstraints(0, 0);
            gbc.ipadx = 10;
            postPanel.add(lbl, gbc);

            lbl = new JLabel("     " + st.getStatus());
            gbc = JGridBagLayout.getItemConstraints(0, 1);
            gbc.ipadx = 10;
            postPanel.add(lbl, gbc);

            postPanel.setPreferredSize(new Dimension(530, 50));
            postPanel.setBackground(Color.LIGHT_GRAY);

            /* Adding the post to the main panel */
            gbc = JGridBagLayout.getItemConstraints(0, counter);
            gbc.insets = new Insets(10, 0, 0, 0);
            mainPanel.add(postPanel, gbc);
            mainPanel.setBackground(Color.WHITE);
            counter++;
        }
    }

    /**
     * @return JPanel with the feeds
     */
    public JPanel getFeeds()
    {
        return mainPanel;
    }

    /**
     * @desc Loads the posts of connections. Here we implement the feed lookup Algorithm
     */
    public void loadConnectionsPosts()
    {
        /* 1. Get the user's connections */
        Connections cUserConnections = this.cUserProfile.getConnections();

        for (Map.Entry<String, String> entry : cUserConnections.getConnections().entrySet())
        {
            /* 2. For each of ther user's connections, Load their Profile */
            String connectionUid = entry.getKey();
            Profile connProfile = new Profile(this.cUserProfile.getNode(), connectionUid);
            if (!connProfile.profileExists())
            {
                /* If no profile exist on the DHT, then ignore displaying friend */
                break;
            }

            connProfile.loadProfile();

            /* 3. Load the PostsReference Object for the connection */
            PostsReference connectionPR = connProfile.getPostsReference();
            if (connectionPR.getNumberOfPosts() > 0)
            {
                /* Add a Maximum of MAX_POSTS_PER_CONNECTION to the posts Hashmap */
                Integer counter = 0;
                for (Map.Entry<String, String> postEntry : connectionPR.getReferences().entrySet())
                {
                    String timestamp = postEntry.getKey();
                    String postNid = postEntry.getValue();

                    this.posts.put(timestamp, new PostItemData(postNid, connectionUid));

                    counter++;
                    if (counter == MAX_POSTS_PER_CONNECTION)
                    {
                        break;
                    }
                }
            }
        }

        /* 4. Now we need to sort the list of posts in chronological order */
        this.posts = new TreeMap<>(this.posts.descendingMap());
    }

    /**
     * @desc A simple data structure to store the data of a post item
     */
    public class PostItemData
    {

        public String postNodeId;
        public String ownerUid;

        public PostItemData(String ipostNodeId, String iownerUid)
        {
            this.postNodeId = ipostNodeId;
            this.ownerUid = iownerUid;
        }
    }
}
