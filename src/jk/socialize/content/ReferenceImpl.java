package jk.socialize.content;

import com.google.gson.Gson;
import java.util.HashMap;
import unito.likir.NodeId;

/**
 * @author Joshua Kissoon
 * @date 20131023
 * @description A class that holds references to other objects
 */
public class ReferenceImpl implements SocializeContent, Reference
{

    /* Attributes */
    public static Integer MAX_REFERENCES = 100;

    private HashMap<String, NodeId> references = new HashMap<String, NodeId>();
    private Integer referencesCount = 0;
    private String uid;
    private final String type = "references";
    private NodeId key;

    public ReferenceImpl(String iUid)
    {
        this.uid = iUid;

        this.generateKey();
    }

    public void putReference(String iRefKey, NodeId iRefNid) throws ArrayIndexOutOfBoundsException
    {
        /* Puts a reference into the refrences set */
        this.references.put(iRefKey, iRefNid);
    }

    /* THE OVERRIDDEN SOCIALIZE CONTENT METHODS */
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

        /* If the string still does not meet the required length, pad it with zeroes */
        Integer strLength = keyValue.length();
        if (strLength < NodeId.LENGTH)
        {
            for (Integer t = 0; t < NodeId.LENGTH - strLength; t++)
            {
                keyValue += "0";
            }
        }

        /* Set this key to a new NodeId object */
        this.key = new NodeId(keyValue.getBytes());
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
            HashMap<String, String> data = gson.fromJson(jsonString, HashMap.class);
            this.references = gson.fromJson(data.get("references").toString(), HashMap.class);
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
        HashMap<String, String> data = new HashMap(3);
        Gson gson = new Gson();
        data.put("references", gson.toJson(this.references));
        data.put("uid", this.uid);
        data.put("type", this.type);
        return gson.toJson(data);
    }

}
