package jk.socialize;

import javax.swing.UIManager;
import jk.socialize.theme.Login;
import jk.socialize.theme.Socialize;

/**
 * @author Joshua Kissoon
 * @project The implementation of "Socialize", A distributed social network to test the designs in my M.Tech Project
 * @date 20131023
 * @file The main file of the project
 */
public class Main
{

    /*
     * @description Here we initialize our Network
     */
    public Main()
    {
        /* Startup the GUI */
        //Socialize ui = new Socialize();
        Login loginFrame = new Login();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        new Main();
    }

}
