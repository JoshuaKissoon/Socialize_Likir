package jk.socialize.system.core.content;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import unito.likir.NodeId;

/**
 * @author Joshua Kissoon
 * @date 20131025
 * @description A class that holds references to all Connection Requests of a user
 */
public class ConnectionRequests implements SocializeContent, Reference
{

    /* Class Attributes */
    public static String type = "ConnectionRequests";
    private String uid;
    private NodeId key;
    private final long ttl = 999999999999l;

    /* Main Objects */
    HashMap<String, NodeId> requests = new HashMap<>();  // An Arraylist to manage the NodeIds of the Relationship Objects

    /**
     * @desc Allow initialization of blank object if it's filled from data
     */
    public ConnectionRequests()
    {

    }

    public ConnectionRequests(String iOwnerUid)
    {
        /* Set the uid of the owner of this content */
        this.uid = iOwnerUid;

        /* Generate a key for this content */
        this.generateKey();
    }

    /**
     * @desc Add a new connection request for this user
     * @param userId          The user Id of the user that sent this request
     * @param relationshipNid The node if of the relationship object for this user
     */
    public void addConnectionRequest(String userId, NodeId relationshipNid)
    {
        /* Add a new connection request for this user */
        this.requests.put(userId, relationshipNid);
    }

    /**
     * @description Method that generates the key for this content type
     * - The key is the current timestamp (10 characters) + _ + first 9 characters of a user's uid
     */
    private void generateKey()
    {
        /* First 10 characters of the user's uid */
        String keyValue = this.uid.substring(0, Math.min(this.uid.length(), 10));

        /* The key contains the words user data */
        keyValue += "_conn_refs";

        /* If the string still does not meet the required length, pad it with Ds */
        Integer strLength = keyValue.length();
        if (strLength < NodeId.LENGTH)
        {
            for (Integer t = 0; t < NodeId.LENGTH - strLength; t++)
            {
                keyValue += "C";
            }
        }

        /* Set this key to a new NodeId object */
        this.key = new NodeId(keyValue.getBytes());
    }

    /* CREATING THE NECESSARY METHODS SPECIFIED BY THE 'SocializeContent' INTERFACE */
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
        return ConnectionRequests.type;
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
            Gson gson = new Gson();
            HashMap data = gson.fromJson(jsonString, HashMap.class);
            this.requests = gson.fromJson(data.get("requests").toString(), HashMap.class);
            System.out.println("Current Requests: " + this.requests);
            this.uid = data.get("uid").toString();
            this.key = new NodeId(data.get("key").toString().getBytes());
            return true;
        }
        catch (JsonSyntaxException e)
        {
            System.err.println("Unable to load the connection requests for this connection requests object. Error: " + e.getMessage());
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
     * @description Here we build a Hash Map with this status object's data and then return the Json version
     */
    @Override
    public String getJsonEncodedData()
    {
        HashMap<String, String> data = new HashMap(3);
        Gson gson = new Gson();
        data.put("requests", gson.toJson(this.requests));
        data.put("uid", this.uid);
        data.put("type", ConnectionRequests.type);
        data.put("key", new String(this.key.getId()));
        return gson.toJson(data);
    }

    @Override
    public long getTtl()
    {
        return this.ttl;
    }

    @Override
    public String toString()
    {
        String data = "************ PRINTING Connection Requests START ************** \n ";

        data += "Key: " + new String(this.key.getId()) + "\n";
        data += "Requests: " + this.requests.toString() + "\n";

        data += "************ PRINTING DATA END ************** \n\n";

        return data;
    }
}
