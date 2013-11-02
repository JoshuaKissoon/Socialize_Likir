package jk.socialize.workers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import unito.likir.Node;
import unito.likir.storage.StorageEntry;

/**
 * @author Joshua Kissoon
 * @date 20131025
 * @description A class that does searching on the DHT
 */
public class Searcher
{

    /* Main Objects */
    private final HashMap<String, String> filters = new HashMap<>();
    private final Node node;

    public Searcher(Node iNode)
    {
        /* Setup the node object */
        this.node = iNode;

        /* Initialize some default filters */
        this.filters.put("numResults", "10");
        this.filters.put("type", null);
    }

    /* Search Commands that returns resultsets */
    public Collection<StorageEntry> search()
    {
        /* Here we run the search and give the results */
        Collection<StorageEntry> results = null;
        try
        {
            Integer numResults = Integer.parseInt(filters.get("numResults"));
            results = node.get(filters.get("keyword"), filters.get("type"), node.getUserId(), false, numResults).get();
        }
        catch (InterruptedException | ExecutionException | IOException ie)
        {
            System.err.println("Searcher Interrupted");
        }
        return results;
    }

    /* FILTERS HANDLING */
    /**
     * @description Adds a filter to the filters
     * @param iName  The name of the parameter
     * @param iValue The value of the parameter to match the given name
     */
    public void addFilter(String iName, String iValue)
    {
        this.filters.put(iName, iValue);
    }
}
