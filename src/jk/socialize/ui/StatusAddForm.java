package jk.socialize.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import jk.socialize.content.Profile;
import jk.socialize.content.Status;
import unito.likir.Node;
import unito.likir.NodeId;

/**
 * @author Joshua Kissoon
 * @description A class that creates a form to add a status
 * @date 20131023
 */
public class StatusAddForm extends JPanel implements ActionListener
{

    /* Main Components */
    private final JPanel form = this;
    private Profile profile;
    private Node node;

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
                System.out.println("Storing " + status.getKey().getId() + ": " + statusTA.getText());
                int replica = node.put(status.getKey(), status.getValue(), status.getType(), 360000000).get();
                System.out.println("CONTENT STORED AT " + replica + " REPLICAS \n");

                /* Now we update this user's posts reference object */
                System.out.println(profile.getPostsReference().getReferences());
                /* Current timestamp: String.valueOf((System.currentTimeMillis() / 1000L)) */
                profile.getPostsReference().addReference(String.valueOf((System.currentTimeMillis() / 1000L)), status.getKey());
                System.out.println(profile.getPostsReference().getReferences());

                /* Update the post references object in the DHT */
                int replicas = node.put( profile.getPostsReference().getKey(),  profile.getPostsReference().getValue(),  profile.getPostsReference().getType(), 360000000).get();
                System.out.println("CONTENT Posts Reference object AT " + replicas + " REPLICAS \n");

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
