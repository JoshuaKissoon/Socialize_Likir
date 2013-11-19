/**
 * @author Joshua Kissoon
 * @date 20131119
 * @desc A node class that is an extension of the Likir Node to provide Socialize functionality
 */
package jk.socialize.objects;

import java.io.File;
import java.io.IOException;
import jk.socialize.content.SocializeContent;
import unito.likir.Node;
import unito.likir.NodeId;
import unito.likir.io.ObservableFuture;
import unito.likir.messages.dht.RPCMessage;

public class SocializeNode extends Node
{

    /**
     * @desc Method to call the Likir Node constructor
     * @param userId 
     */
    public SocializeNode(String userId)
    {
        super(userId);
    }

    /**
     * @desc Method to call the Likir Node constructor
     * @param f 
     * @throws IOException
     */
    public SocializeNode(File f) throws IOException
    {
        super(f);
    }
    
    /**
     * @desc Store a value locally
     * @return The task that is being ran to store the content locally
     * @param key The key which to store the content at
     * @param content The actual content to store
     * @param type The type of content
     * @param ttl How long should the content live
     * @throws IOException
     */
    public ObservableFuture<RPCMessage> storeLocally(NodeId key, byte[] content, String type, long ttl) throws IOException
    {
        System.out.println("Storing content locally");
        return store(this.getNodeId(), key, content, type, ttl);
    }
    
    /**
     * @desc Method that puts a socialize content by calling the Likir Node put method
     * @return 
     * @param content Some socialize content to put on the network
     */
    public synchronized ObservableFuture<Integer> put(SocializeContent content)
    {
        return put(content.getKey(), content.getValue(), content.getType(), content.getTtl());
    }
}
