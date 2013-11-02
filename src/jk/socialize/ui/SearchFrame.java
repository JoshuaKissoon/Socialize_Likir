package jk.socialize.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import jk.socialize.content.Profile;
import jk.socialize.workers.Searcher;
import unito.likir.Node;
import unito.likir.storage.StorageEntry;

/**
 * @author Joshua Kissoon
 * @description A class that generates a search frame to allow the user to search for content
 * @date 20131025
 */
public class SearchFrame extends JFrame implements ActionListener
{

    /* Main objects */
    private final SearchFrame frame = this;
    private Node node;

    /* Frame Components */
    private JPanel mainPanel, resultsPanel;
    private JScrollPane scrollPane;

    /* Form Components */
    private JTextField keywordTF;
    private JLabel label;

    public SearchFrame(Node iNode)
    {
        /* Set the node to the input node */
        this.node = iNode;

        /* Create the UI */
        this.createGUI();
    }

    /**
     * @description Here we create the search frame GUI
     * Our Frame GUI will basically contain a search box and a panel area to display results
     */
    private void createGUI()
    {
        /* Setting up the main panel */
        mainPanel = new JPanel(new BorderLayout());

        /* Adding the search keyword TF to the main panel */
        keywordTF = new JTextField();
        keywordTF.addKeyListener(new KeyListener()
        {
            /* Adding a key listener to the search keyword text field*/
            @Override
            public void keyTyped(KeyEvent e)
            {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {

            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    /* Call the search method */
                    frame.search();
                }
            }
        });
        mainPanel.add(keywordTF, BorderLayout.NORTH);

        /* Adding the Results Panel to the main panel */
        resultsPanel = new JPanel();
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

        /* Add the Main panel to the frame */
        scrollPane = new JScrollPane(mainPanel);
        frame.getContentPane().add(scrollPane);
    }

    /**
     * @description Call the necessary methods to display
     */
    public void showGUI()
    {
        frame.pack();
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setVisible(true);
    }

    /**
     * @description Method to do a search and populate the results panel with the search result
     */
    public void search()
    {
        /* Create a new Searcher object and use it to do the searches for us */
        System.out.println("Search Started");
        Searcher searcher = new Searcher(this.node);
        searcher.addFilter("keyword", keywordTF.getText());
        searcher.addFilter("type", Profile.type);

        Collection<StorageEntry> results = null;
        try
        {
            results = node.get(Profile.generateKey(keywordTF.getText()), Profile.type, null, true, 5).get();
        }
        catch (InterruptedException | ExecutionException ie)
        {
            System.err.println("Searcher Interrupted");
        }

        /* Now we display these results onto the results panel */
        for (StorageEntry e : results)
        {
            String display = "";
            display += "Owner Id: " + e.getOwnerId() + "; ";
            display += "Key: " + e.getKey() + "; ";
            display += "Content Type: " + e.getContent().getType() + "; ";
            display += "Content Value: " + new String(e.getContent().getValue()) + "; ";
            System.out.println(display);
            label = new JLabel(display);

            resultsPanel.add(label);
        }
        System.out.println("Search Ended");
    }

    /* LISTENERS */
    @Override
    public void actionPerformed(ActionEvent aE)
    {

    }
}
