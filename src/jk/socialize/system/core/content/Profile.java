package jk.socialize.system.core.content;

import com.google.gson.Gson;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import jk.socialize.system.abstraction.SocializeNode;
import unito.likir.NodeId;
import unito.likir.storage.StorageEntry;

/**
 * @author Joshua Kissoon
 * @date 20131023
 * @description A content type that's basically the profile of a user; it contains all objects related to this user
 */
public class Profile implements SocializeContent
{

    /* Attributes */
    private NodeId key;
    private String uid = "";
    public static final String type = "profile";
    private final long ttl = 999999999999l;

    /* Main Objects */
    private final SocializeNode node;

    /* Reference objects references */
    private NodeId connectionsRefNid;      // The Node Id of the Connections Object with references to this user's connections
    private NodeId postsRefNid;   // The Node Id of the References Object with references to this user's posts
    private NodeId userDataRefNid;         // The Node Id of the User Data Object
    private NodeId connectionRequestsRefNid;         // The Node Id of the ConnectionRequests Object

    /* Reference objects */
    private PostsReference postsReference = null;
    private ConnectionRequests connectionRequests = null;
    private Connections connections = null;
    private UserData userData = null;

    /**
     * @desc Constructor for a profile
     * @param iNode   Just some node on the network used within this profile to get data
     * @param iUserId The user Id which this profile is for
     */
    public Profile(SocializeNode iNode, String iUserId)
    {
        /* Set the node */
        this.node = iNode;

        /* Set the userId */
        this.uid = iUserId;

        /* Setup the profile key here */
        this.generateKey();
    }

    public Profile(SocializeNode iNode, byte[] iData)
    {
        /* Set the node */
        this.node = iNode;

        /* Here we load the profile from it's json data */
        this.loadData(iData);
    }
    /* METHODS FOR CHECKING PROFILE EXISTENCE AND CREATING A NEW PROFILE */

    /**
     * @description Checks if a profile exists on the network for this node
     * @return true if a profile exists on the network for this node
     */
    public Boolean nodeProfileExists()
    {
        try
        {
            System.out.println("Node \"" + node.getUserId() + "\" Checking if a profile exists; Profile ID " + this.key + "\n");

            Collection<StorageEntry> results = node.get(this.getKey(), this.type, this.uid, false, 5).get();

            if (results.size() == 0)
            {
                System.out.println("No Profile Found! \n");
                return false;
            }
            else
            {
                System.out.println("Profile Found: \n");
                for (StorageEntry e : results) //print the found values
                {
                    System.out.println("--> Owner: " + e.getOwnerId() + "; Type: " + e.getContent().getType() + "; Value: " + new String(e.getContent().getValue()) + " \n");
                }
                return true;
            }
        }
        catch (InterruptedException | ExecutionException ie)
        {
            System.err.println("Interrupted");
            return false;
        }
    }

    /**
     * @description Creates a new profile for this node
     * @return whether the profile was successfully created
     */
    public Boolean createProfile()
    {
        System.out.println(" *********************** Creating a new profile ************************ ");
        /* 1. Create and store a connections object */
        try
        {
            System.out.println("Creating & Storing Connections Object.");

            Connections connections = new Connections(this.uid);

            int replica = node.storeLocallyAndUniversally(connections);
            System.out.println("Connection Object Stored at " + replica + " Replicas \n");
            this.connectionsRefNid = connections.getKey();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /* 2. Create and store a posts references object */
        try
        {
            System.out.println("Creating & Storing Posts Reference Object.");

            PostsReference pReferences = new PostsReference(this.uid);
            int replica = node.storeLocallyAndUniversally(pReferences);
            System.out.println("Post References Object Stored at " + replica + " Replicas \n");
            this.postsRefNid = pReferences.getKey();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /* 3. Create and store a user data object */
        try
        {
            System.out.println("Creating & Storing User Data Object.");

            UserData uData = new UserData(this.uid);
            int replica = node.storeLocallyAndUniversally(uData);
            System.out.println("User Data Object Stored at " + replica + " Replicas \n");
            this.userDataRefNid = uData.getKey();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /* 4. Create and store a ConnectionRequestsObject object */
        try
        {
            System.out.println("Creating & Storing User ConnectionsRequest Object.");

            ConnectionRequests connRequests = new ConnectionRequests(this.uid);
            System.out.println(connRequests);
            int replica = node.storeLocallyAndUniversally(connRequests);
            System.out.println("ConnectionRequests Object Stored at " + replica + " Replicas \n");
            this.connectionRequestsRefNid = connRequests.getKey();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /* 5. Store the profile object on the DHT */
        try
        {
            System.out.println("Storing the profile Object on the DHT. Key: " + this.getKey().toString());

            int replica = node.storeLocallyAndUniversally(this);
            System.out.println("User Profile Object Stored at " + replica + " Replicas \n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    /* Method that loads the user's profile from the network */
    public void loadProfile()
    {
        try
        {
            System.out.println("Node \"" + node.getUserId() + "\" Loading Profile:" + this.key + "\n");

            /* Get 5 of this user's profile and choose the most recent */
            Collection<StorageEntry> results = node.get(this.getKey(), this.type, this.uid, true, 5).get();

            if (results.size() > 0)
            {
                long recency = 0;
                for (StorageEntry e : results) //print the found values
                {
                    if (e.getSubmissionTime() > recency)
                    {
                        recency = e.getSubmissionTime();

                        /* Load/update the profile from this entry */
                        this.loadData(e.getContent().getValue());
                    }
                }
            }
        }
        catch (InterruptedException | ExecutionException ie)
        {
            System.err.println("Profile loading Interrupted");
        }

        /* Testing profile values loading */
        System.out.println(new String(this.connectionsRefNid.getId()));
        System.out.println(new String(this.postsRefNid.getId()));
        System.out.println(new String(this.userDataRefNid.getId()));
        System.out.println(new String(this.connectionRequestsRefNid.getId()));
    }

    /**
     * @description Method that generates the key for this user's profile
     * - The key is the user's uid concatenated with 'P' to a length of 20 characters
     */
    private void generateKey()
    {
        /* First 12 characters of the user's uid */
        String keyValue = this.uid.substring(0, Math.min(this.uid.length(), 12));

        /* The key contains the words profile */
        keyValue += "_profile";

        /* If the string still does not meet the required length, pad it with P's */
        Integer strLength = keyValue.length();
        if (strLength < NodeId.LENGTH)
        {
            for (Integer t = 0; t < NodeId.LENGTH - strLength; t++)
            {
                keyValue += "P";
            }
        }

        /* Set this key to a new NodeId object */
        this.key = new NodeId(keyValue.getBytes());
    }

    public static NodeId generateKey(String iUid)
    {
        /* First 12 characters of the user's uid */
        String keyValue = iUid.substring(0, Math.min(iUid.length(), 12));

        /* The key contains the words profile */
        keyValue += "_profile";

        /* If the string still does not meet the required length, pad it with P's */
        Integer strLength = keyValue.length();
        if (strLength < NodeId.LENGTH)
        {
            for (Integer t = 0; t < NodeId.LENGTH - strLength; t++)
            {
                keyValue += "P";
            }
        }

        /* Set this key to a new NodeId object */
        return new NodeId(keyValue.getBytes());
    }

    /* PROFILE GETTERS AND SETTERS */
    public void setNode()
    {

    }

    public SocializeNode getNode()
    {
        return this.node;
    }

    public PostsReference getPostsReference()
    {
        if (this.postsReference == null)
        {
            /* We need to load the posts reference */
            try
            {
                System.out.println("Node \"" + node.getUserId() + "\" Loading Profile:" + this.key + "\n");

                /* Get 5 of this user's profile and choose the most recent */
                Collection<StorageEntry> results = node.get(this.getKey(), this.type, this.uid, true, 5).get();

                if (results.size() > 0)
                {
                    long recency = 0;
                    for (StorageEntry e : results) //print the found values
                    {
                        if (e.getSubmissionTime() > recency)
                        {
                            recency = e.getSubmissionTime();

                            /* Load/update the profile from this entry */
                            postsReference = new PostsReference(this.uid);
                            postsReference.loadData(e.getContent().getValue());
                        }
                    }
                }
            }
            catch (InterruptedException | ExecutionException ie)
            {
                System.err.println("Posts References loading Interrupted");
            }
        }

        return this.postsReference;
    }
    
    public UserData getUserData()
    {
        if (this.userData == null)
        {
            /* We need to load the userData object */
            try
            {
                System.out.println("Node \"" + node.getUserId() + "\" Loading UserData:" + this.key + "\n");

                /* Get 5 of this user's profile and choose the most recent */
                Collection<StorageEntry> results = node.get(this.userDataRefNid, UserData.type, this.uid, true, 5).get();

                if (results.size() > 0)
                {
                    long recency = 0;
                    for (StorageEntry e : results) //print the found values
                    {
                        if (e.getSubmissionTime() > recency)
                        {
                            recency = e.getSubmissionTime();

                            /* Load/update the profile from this entry */
                            userData = new UserData();
                            userData.loadData(e.getContent().getValue());
                        }
                    }
                }
            }
            catch (InterruptedException | ExecutionException ie)
            {
                System.err.println("Posts References loading Interrupted");
            }
        }

        return this.userData;
    }

    public ConnectionRequests getConnectionRequests()
    {
        if (this.connectionRequests == null)
        {
            /* We need to load the connection requests */
            try
            {
                System.out.println("Node \"" + node.getUserId() + "\" Loading Connections request object \n");

                /* Get 5 of this user's profile and choose the most recent */
                Collection<StorageEntry> results = node.get(this.connectionRequestsRefNid, ConnectionRequests.type, this.uid, true, 5).get();

                if (results.size() > 0)
                {
                    long recency = 0;
                    for (StorageEntry e : results) //print the found values
                    {
                        if (e.getSubmissionTime() > recency)
                        {
                            recency = e.getSubmissionTime();

                            /* Load/update the profile from this entry */
                            connectionRequests = new ConnectionRequests();
                            connectionRequests.loadData(e.getContent().getValue());
                        }
                    }
                }
            }
            catch (InterruptedException | ExecutionException ie)
            {
                System.err.println("Posts References loading Interrupted");
            }
        }

        return this.connectionRequests;
    }
    
    public Connections getConnections()
    {
        if (this.connections == null)
        {
            /* We need to load the connection requests */
            try
            {
                System.out.println("Node \"" + node.getUserId() + "\" Loading Connections object \n");

                /* Get 5 of this user's profile and choose the most recent */
                Collection<StorageEntry> results = node.get(this.connectionsRefNid, Connections.type, this.uid, true, 5).get();

                if (results.size() > 0)
                {
                    long recency = 0;
                    for (StorageEntry e : results) //print the found values
                    {
                        if (e.getSubmissionTime() > recency)
                        {
                            recency = e.getSubmissionTime();

                            /* Load/update the profile from this entry */
                            connections = new Connections();
                            connections.loadData(e.getContent().getValue());
                        }
                    }
                }
            }
            catch (InterruptedException | ExecutionException ie)
            {
                System.err.println("Posts References loading Interrupted");
            }
        }

        return this.connections;
    }

    /**
     * @return The user id of the user that owns this profile
     */
    public String getUid()
    {
        return this.uid;
    }
    /* METHODS FOR THE "SocializeContent" INTERFACE */

    /**
     * @return Returns the data (the raw bytes) of the content
     *
     * @description Here we return a byte array of the data to be stored on the DHT
     */
    @Override
    public byte[] getValue()
    {
        return this.getJsonEncodedData().getBytes();
    }

    /**
     * @return Returns the type of the content data.
     */
    @Override
    public String getType()
    {
        return Profile.type;
    }

    /**
     * @return Returns the size of the data payload in byte
     */
    @Override
    public int size()
    {
        return this.getJsonEncodedData().length();
    }

    /**
     * @return Returns the DHT key for this content type
     */
    @Override
    public NodeId getKey()
    {
        return this.key;
    }

    /**
     * @param jsonString A Json string with the data of this object
     *
     * @return Returns whether the data was successfully loaded or not
     *
     * @description Here we load data from the DHT into the object
     */
    @Override
    public boolean loadData(String jsonString)
    {
        try
        {
            HashMap data = new Gson().fromJson(jsonString, HashMap.class);
            this.uid = data.get("uid").toString();
            this.connectionsRefNid = new NodeId(data.get("connectionsRefNid").toString().getBytes());
            this.postsRefNid = new NodeId(data.get("postsRefNid").toString().getBytes());
            this.userDataRefNid = new NodeId(data.get("userDataRefNid").toString().getBytes());
            this.connectionRequestsRefNid = new NodeId(data.get("connectionRequestsRefNid").toString().getBytes());
            return true;
        }
        catch (Exception e)
        {
            System.err.println("Unable to load data for the profile from it's json object.");
            return false;
        }
    }

    /**
     * @param data A byte array of data returned from the DHT
     *
     * @return Returns whether the data was successfully loaded or not
     *
     * @description Here we load the data from the DHT into the object
     */
    @Override
    public boolean loadData(byte[] data)
    {
        return this.loadData(new String(data));
    }

    /**
     * @return Returns the data needed to be stored encoded in Json form
     *
     * @description Here we build a Hash Map with this object's data and then return the Json version
     */
    @Override
    public String getJsonEncodedData()
    {
        HashMap<String, String> data = new HashMap(3);
        data.put("uid", this.uid);
        data.put("connectionsRefNid", new String(this.connectionsRefNid.getId()));
        data.put("postsRefNid", new String(this.postsRefNid.getId()));
        data.put("userDataRefNid", new String(this.userDataRefNid.getId()));
        data.put("connectionRequestsRefNid", new String(this.connectionRequestsRefNid.getId()));
        return new Gson().toJson(data);
    }

    @Override
    public long getTtl()
    {
        return this.ttl;
    }

    /* JAVA COMMON METHODS */
    @Override
    public String toString()
    {
        String data = "************ PRINTING DATA START ************** ";

        data += "uid: " + this.uid + "\n";
        data += "connectionsRefNid: " + new String(this.connectionsRefNid.getId()) + "\n";
        data += "postsRefNid: " + new String(this.postsRefNid.getId()) + "\n";
        data += "userDataRefNid: " + new String(this.userDataRefNid.getId()) + "\n";
        data += "connectionRequestsRefNid: " + new String(this.connectionRequestsRefNid.getId()) + "\n";

        data += "************ PRINTING DATA END ************** ";

        return data;
    }
}
