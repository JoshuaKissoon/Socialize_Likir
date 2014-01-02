package jk.socialize.system.core.content;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import unito.likir.NodeId;

/**
 * @author Joshua Kissoon
 * @date 20131023
 * @description The status content type
 */
public class Status implements SocializeContent
{

    /* Attributes */
    private String status;
    private NodeId key;
    private String uid;
    private final String type = "status";
    private final long ttl = 999999999999l;

    public Status()
    {
        
    }
    
    public Status(String iStatus, String iUserId)
    {
        /* Set the status value */
        this.status = iStatus;

        /* Set the userId */
        this.uid = iUserId;

        /* Setup the status key here */
        this.generateKey();
    }
    
    public String getStatus()
    {
        return this.status;
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
        keyValue += "_status";

        /* If the string still does not meet the required length, pad it with zeroes */
        Integer strLength = keyValue.length();
        if (strLength < NodeId.LENGTH)
        {
            for (Integer t = 0; t < NodeId.LENGTH - strLength; t++)
            {
                keyValue += "S";
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
            HashMap data = new Gson().fromJson(jsonString, HashMap.class);
            this.status = data.get("status").toString();
            this.uid = data.get("uid").toString();
            this.key = new NodeId(data.get("key").toString().getBytes());
            return true;
        }
        catch (JsonSyntaxException e)
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
        data.put("status", this.status);
        data.put("uid", this.uid);
        data.put("key", new String(this.key.getId()));
        return new Gson().toJson(data);
    }
    
    @Override
    public long getTtl()
    {
        return this.ttl;
    }

    @Override
    public String toString()
    {
        String data = "************ PRINTING Status START ************** \n ";

        data += "Key: " + new String(this.key.getId()) + "\n";
        data += "Status: " + this.status + "\n";

        data += "************ PRINTING Status END ************** \n\n";

        return data;
    }
}
