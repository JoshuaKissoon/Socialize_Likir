package jk.socialize.content;

import com.google.gson.Gson;
import java.util.HashMap;
import unito.likir.NodeId;

/**
 * @author Joshua Kissoon
 * @description A simple class that stores a connection request from one user to the other
 * @date 20131025
 */
public class ConnectionRequest implements SocializeContent
{

    /* Attributes */
    private String uid;     // The DHT user's ID that owns this object
    private String requestee;   // The person who the request is for
    private NodeId key;
    private final String type = "ConnectionRequest";
    private final long ttl = 999999999999l;
    private Integer status = 0;

    /* Static variables */
    public static final Integer CR_STATUS_PENDING = 0;
    public static final Integer CR_STATUS_APPROVED = 1;

    public ConnectionRequest(String iOwnerUid, String iRequesteeUid)
    {
        /* Set the owner of this relationship */
        this.uid = iOwnerUid;
        this.requestee = iRequesteeUid;

        /* Generate a key for this relationship */
        this.generateKey();
    }

    /**
     * @description Method that generates the key for this content type
     * - The key is the current timestamp (10 characters) + _ + first 9 characters of a user's uid
     */
    private void generateKey()
    {
        /* First 10 characters are the timestamp */
        String keyValue = String.valueOf((System.currentTimeMillis() / 1000L));

        /* Append an underscore */
        keyValue += "_";

        /* Last 9 characters are from the UID */
        keyValue += uid.substring(0, Math.min(uid.length(), 9));

        /* If the string still does not meet the required length, pad it with "R"s */
        Integer strLength = keyValue.length();
        if (strLength < NodeId.LENGTH)
        {
            for (Integer t = 0; t < NodeId.LENGTH - strLength; t++)
            {
                keyValue += "K";
            }
        }

        /* Set this key to a new NodeId object */
        this.key = new NodeId(keyValue.getBytes());
    }

    /**
     * @return the DHT user ID of the user that owns this relationship
     */
    public String getOwnerUid()
    {
        return this.uid;
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
        return this.type;
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
            this.requestee = data.get("requestee").toString();
            this.uid = data.get("uid").toString();
            this.key = new NodeId(data.get("key").toString().getBytes());
            return true;
        }
        catch (Exception e)
        {
            System.err.println("Unable to load data for the connection request from it's json object.");
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
        data.put("requestee", this.requestee);
        data.put("key", new String(this.key.getId()));
        data.put("uid", this.uid);
        return new Gson().toJson(data);
    }
    
    @Override
    public long getTtl()
    {
        return this.ttl;
    }
}
