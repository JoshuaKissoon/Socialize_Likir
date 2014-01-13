package jk.socialize.utilities;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * @author Joshua Kissoon
 * @desc A class that auto-generate grid bag layout values and returns them
 * @date 20131227
 */
public class JGridBagLayout
{

    public static GridBagConstraints getLabelConstraints(int x, int y)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.weightx = 0.0;
        c.weighty = 0.1;
        c.anchor = GridBagConstraints.WEST;
        
        return c;
    }

    public static GridBagConstraints getItemConstraints(int x, int y)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.WEST;

        return c;
    }
}
