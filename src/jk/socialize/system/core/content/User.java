package jk.socialize.system.core.content;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import org.bouncycastle.crypto.tls.HashAlgorithm;
import unito.likir.NodeId;

/**
 * @author Joshua Kissoon
 * @date 20131024
 * @description A Content type that stores the data of a user
 */
public class User implements SocializeContent
{

    /* Attributes */
    private NodeId key;
    private String uid;
    public static final String type = "UserData";
    private final long ttl = 999999999999l;
    private String hashedPassword;

    /* Storage Objects */
    HashMap<String, Object> userData = new HashMap<>();

    /* Constants that represent the different data stored */
    public static String DATA_SOCIALIZE_USERNAME = "uid";
    public static String DATA_NAME = "name";
    public static String DATA_DOB = "dob";
    public static String DATA_PHOTO = "photo";

    /**
     * @desc A blank constructor in case data is loaded from the DHT
     */
    public User()
    {
        
    }
    
    public User(String iOwnerUid)
    {
        /* Set the userId */
        this.uid = iOwnerUid;
        putData(DATA_SOCIALIZE_USERNAME, this.uid);

        /* Setup the status key here */
        this.generateKey();
    }
    
    public void setPassword(String ipassword)
    {
        this.hashedPassword = this.hashPassword(ipassword);
    }
    
    public Boolean isUserPassword(String ipassword)
    {
        return this.hashedPassword.equals(this.hashPassword(ipassword));
    }
    
    private String hashPassword(String ipassword)
    {
        String hash = "";
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String text = "This is some text";
            md.update(ipassword.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            hash = new String(md.digest());
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            
        }
        return hash;
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
        if (this.userData.containsKey(iDataKey))
        {
            return this.userData.get(iDataKey);
        }
        else
        {
            return "";
        }
    }
    
    public Object putData(String iDataKey, Object iData)
    {
        return this.userData.put(iDataKey, iData);
    }

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
        return User.type;
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
            this.userData = gson.fromJson(data.get("userData").toString(), HashMap.class);
            this.uid = data.get("uid").toString();
            this.hashedPassword = data.get("hashedPassword").toString();
            this.key = new NodeId(data.get("key").toString().getBytes());
            return true;
        }
        catch (JsonSyntaxException e)
        {
            System.err.println("Unable to load data for the UserData Object from it's json object. Error: " + e.getMessage());
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
        Gson gson = new Gson();
        HashMap<String, String> data = new HashMap(3);
        data.put("userData", gson.toJson(this.userData));
        data.put("uid", this.uid);
        data.put("type", Connections.type);
        data.put("hashedPassword", this.hashedPassword);
        data.put("key", new String(this.key.getId()));
        return gson.toJson(data);
    }
    
    @Override
    public long getTtl()
    {
        return this.ttl;
    }

    /**
     * @desc Implementation of toString method
     */
    @Override
    public String toString()
    {
        String data = "************ PRINTING UserData START ************** \n ";
        
        data += "Key: " + new String(this.key.getId()) + "\n";
        data += "Connections: " + this.userData + "\n";
        
        data += "************ PRINTING UserData END ************** \n\n";
        
        return data;
    }
}
