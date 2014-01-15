package jk.socialize.theme;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
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
    private final Socialize mainFrame;

    /* Form Components */
    private final JTextArea statusTA;
    private final JButton btn;
    private final JScrollPane scrollPane;

    public StatusAddForm(Profile iProfile, Socialize imainFrame)
    {
        this.profile = iProfile;
        this.node = this.profile.getNode();
        this.mainFrame = imainFrame;

        form.setLayout(new BorderLayout());
        form.setBorder(new EmptyBorder(30, 30, 30, 30));

        statusTA = new JTextArea(5, 40);
        statusTA.setWrapStyleWord(true);
        statusTA.setLineWrap(true);
        scrollPane = new JScrollPane(statusTA);
        form.add(scrollPane, BorderLayout.CENTER);

        btn = new JButton("Post");
        btn.setActionCommand("postStatus");
        btn.addActionListener(this);
        form.add(btn, BorderLayout.EAST);
        form.setPreferredSize(new Dimension(540, 100));
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
                refs.addReference(String.valueOf((System.currentTimeMillis() / 1000L)), new String(status.getKey().getId()));

                /* Update the post references object in the DHT */
                int replicas = node.storeLocallyAndUniversally(refs);
                System.out.println("Posts Reference object stored AT " + replicas + " REPLICAS \n");

                /* Recheck the post references */
                PostsReference refss = profile.getPostsReference();
                System.out.println(refss);
                statusTA.setText("");
                this.mainFrame.addSidebarMessage(new SocializeMessage("Your Status update was successfully posted", SocializeMessage.MESSAGE_TYPE_SUCCESS));
            }
            catch (IOException | InterruptedException | ExecutionException e)
            {
                System.out.println("Error while trying to post status, error: " + e.getMessage());
            }
        }
    }
}
