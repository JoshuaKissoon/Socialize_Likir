package jk.socialize.content;

import com.google.gson.Gson;
import java.util.HashMap;
import unito.likir.NodeId;

/**
 * @author Joshua Kissoon
 * @date 20131024
 * @description A Content type that stores the data of a user
 */
public class UserData implements SocializeContent
{

    /* Attributes */
    private String status;
    private NodeId key;
    private String uid;
    private final String type = "UserData";
    private final long ttl = 999999999999l;

    /* Storage Objects */
    HashMap<String, Object> userData = new HashMap<>();

    /* Constants that represent the different data stored */
    public static String DATA_USERNAME = "username";
    public static String DATA_FIRST_NAMW = "first_name";
    
    public UserData(String iOwnerUid)
    {
        /* Set the userId */
        this.uid = iOwnerUid;

        /* Setup the status key here */
        this.generateKey();
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
        keyValue += "_user_data";

        /* If the string still does not meet the required length, pad it with Ds */
        Integer strLength = keyValue.length();
        if (strLength < NodeId.LENGTH)
        {
            for (Integer t = 0; t < NodeId.LENGTH - strLength; t++)
            {
                keyValue += "D";
            }
        }

        /* Set this key to a new NodeId object */
        this.key = new NodeId(keyValue.getBytes());
    }

    /* GETTER AND SETTER METHODS TO MANIPULATE DATA */
    public Object getData(String iDataKey)
    {
        return this.userData.get(iDataKey);
    }

    public Object putData(String iDataKey, Object iData)
    {
        return this.userData.put(iDataKey, iData);
    }

    /**
     * @return Returns the data (the raw bytes) of the content
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
     * @return Returns whether the data was successfully loaded or not
     * @description Here we load data from the DHT into the object
     */
    @Override
    public boolean loadData(String jsonString)
    {
        try
        {
            Gson gson = new Gson();
            HashMap data = gson.fromJson(jsonString, HashMap.class);
            this.userData = gson.fromJson(data.get("data").toString(), HashMap.class);
            this.uid = data.get("uid").toString();
            return true;
        }
        catch (Exception e)
        {
            System.err.println("Unable to load data for the status from it's json object.");
            return false;
        }
    }

    /**
     * @param data A byte array of data returned from the DHT
     * @return Returns whether the data was successfully loaded or not
     * @description Here we load the data from the DHT into the object
     */
    @Override
    public boolean loadData(byte[] data)
    {
        return this.loadData(new String(data));
    }

    /**
     * @return Returns the data needed to be stored encoded in Json form
     * @description Here we build a Hash Map with this status object's data and then return the Json version
     */
    @Override
    public String getJsonEncodedData()
    {
        Gson gson = new Gson();
        HashMap<String, String> data = new HashMap(3);
        data.put("data", gson.toJson(this.userData));
        data.put("uid", this.uid);
        return gson.toJson(data);
    }
    
    @Override
    public long getTtl()
    {
        return this.ttl;
    }
}
