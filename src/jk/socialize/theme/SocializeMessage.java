package jk.socialize.theme;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * @author Joshua Kissoon
 * @desc A message to be displayed on the screen
 * @created 20140115
 */
public class SocializeMessage extends JPanel
{

    /* Constants */
    public static Integer MESSAGE_TYPE_SUCCESS = 1;
    public static Integer MESSAGE_TYPE_WARNING = 2;
    public static Integer MESSAGE_TYPE_ERROR = 3;

    /* Panel Components */
    private final JLabel lbl;

    /**
     * @desc Class constructor that creates the message
     * @param imsg  The message to be shown
     * @param itype The type of message
     */
    public SocializeMessage(String imsg, Integer itype)
    {
        /* Set the panel's background color based on the message type */
        if (itype == MESSAGE_TYPE_SUCCESS)
        {
            this.setBackground(new Color(67, 172, 106));
        }
        else if (itype == MESSAGE_TYPE_WARNING)
        {
            this.setBackground(Color.ORANGE);
        }
        else if (itype == MESSAGE_TYPE_ERROR)
        {
            this.setBackground(Color.RED);
        }
        else
        {
            this.setBackground(Color.BLUE);
        }

        lbl = new JLabel("<html><body style='width: 100px;'>" + imsg + "</body></html>");
        lbl.setMaximumSize(new Dimension(140, 50));
        lbl.setForeground(Color.WHITE);
        this.add(lbl);
        this.setMaximumSize(new Dimension(140, 50));
        this.setBorder(new EmptyBorder(0, 0, 10, 0));
    }
}
