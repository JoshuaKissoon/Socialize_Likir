/**
 * @author Joshua Kissoon
 * @desc Grabs and displays the home feed
 * @created 20140102
 */
package jk.socialize.theme;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
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
    private final TreeMap<String, PostItemData> posts = new TreeMap<>();
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
            PostItemData itemData = entry.getValue();
            NodeId statusNid = new NodeId(itemData.postNodeId.getBytes());

            /* Load the status */
            Status st = new Status();
            st = (Status) this.cUserProfile.getNode().getContent(statusNid, itemData.ownerUid, st);
            System.out.println(st);

            postPanel = new JPanel(new GridBagLayout());
            lbl = new JLabel(st.getStatus());
            gbc = JGridBagLayout.getLabelConstraints(0, 0);
            postPanel.add(lbl, gbc);
            
            /* Adding the post to the main panel */
            gbc = JGridBagLayout.getItemConstraints(0, counter);
            mainPanel.add(postPanel, gbc);
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
            System.out.println("Connection Uid: " + connectionUid);
            Profile connProfile = new Profile(this.cUserProfile.getNode(), connectionUid);
            if (!connProfile.profileExists())
            {
                /* If no profile exist on the DHT, then ignore displaying friend */
                break;
            }

            connProfile.loadProfile();
            System.out.println(connProfile);

            /* 3. Load the PostsReference Object for the connection */
            PostsReference connectionPR = connProfile.getPostsReference();
            System.out.println(connectionPR);
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
        /* The list will automatically be sorted since it's in a treemap */
        System.out.println(this.posts);
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
