package jk.socialize.system.core.content;

import unito.likir.NodeId;
import unito.likir.storage.Content;

/**
 * @author Joshua Kissoon
 * @description An interface that declares the methods and objects in any socialize content type
 * @date 20131023
 */
public interface SocializeContent extends Content
{

    /**
     * @return Returns the DHT key for this content type
     */
    public NodeId getKey();

    /**
     * @param jsonString A Json string with the data of this object
     * @return Returns whether the data was successfully loaded or not
     * @description Here we load data from the DHT into the object
     */
    public boolean loadData(String jsonString);
    
    /**
     * @param data A byte array of data returned from the DHT
     * @return Returns whether the data was successfully loaded or not
     * @description Here we load the data from the DHT into the object
     */
    public boolean loadData(byte[] data);

    /**
     * @return Returns the data needed to be stored encoded in Json form
     */
    public String getJsonEncodedData();
    
    /** 
     * @desc method that tells how long a content should live on the DHT
     * @return Returns how long this content will live for
     */
    public long getTtl();
}
