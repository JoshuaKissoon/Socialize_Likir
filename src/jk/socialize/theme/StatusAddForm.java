package jk.socialize.theme;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import jk.socialize.system.abstraction.SocializeNode;
import jk.socialize.system.core.content.PostsReference;
import jk.socialize.system.core.content.Profile;
import jk.socialize.system.core.content.Status;

/**
 * @author Joshua Kissoon
 * @description A class that creates a form to add a status
 * @date 20131023
 */
public class StatusAddForm extends JPanel implements ActionListener
{

    /* Main Components */
    private final JPanel form = this;
    private final Profile profile;
    private final SocializeNode node;

    /* Form Components */
    private final JTextArea statusTA;
    private final JButton btn;

    public StatusAddForm(Profile iProfile)
    {
        this.profile = iProfile;
        this.node = this.profile.getNode();

        form.setLayout(new BorderLayout());
        form.setBorder(new EmptyBorder(30, 30, 30, 30));

        statusTA = new JTextArea(5, 40);
        form.add(statusTA, BorderLayout.CENTER);

        btn = new JButton("Post");
        btn.setActionCommand("postStatus");
        btn.addActionListener(this);
        form.add(btn, BorderLayout.EAST);
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand().equals("postStatus"))
        {
            /* The user wants to post the status */
            try
            {
                Status status = new Status(statusTA.getText().toString(), node.getUserId());
                int replica = node.storeLocallyAndUniversally(status);
                System.out.println("Status STORED AT " + replica + " REPLICAS \n");

                /* Now we update this user's posts reference object */
                PostsReference refs = profile.getPostsReference();

                /* Current timestamp: String.valueOf((System.currentTimeMillis() / 1000L)) */
                refs.addReference(String.valueOf((System.currentTimeMillis() / 1000L)), status.getKey());

                /* Update the post references object in the DHT */
                int replicas = node.storeLocallyAndUniversally(refs);
                System.out.println("Posts Reference object stored AT " + replicas + " REPLICAS \n");

                /* Recheck the post references */
                PostsReference refss = profile.getPostsReference();
            }
            catch (IOException | InterruptedException | ExecutionException e)
            {
                System.out.println("Error while trying to post status, error: " + e.getMessage());
            }
        }
    }
}
