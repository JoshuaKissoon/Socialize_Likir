/**
 * @author Joshua
 * @date 20131112
 * @description An interface that serves as a set of rules for different abstraction services used within the system
 */
package jk.socialize.system.core.external;

import java.util.HashMap;

public interface AuthenticationService
{

    /**
     * @desc Connect to the authentication service
     * @return Whether the connection was successful or not
     */
    public Boolean connect();

    /**
     * @desc Disconnect from the authentication service
     * @return Whether the disconnection was successful or not
     */
    public Boolean disconnect();

    /**
     * @desc Authenticate a user through the authentication service
     * @return Whether the authentication was successful
     * @param credentials A HashMap with the credentials of the user to authenticate
     */
    public Boolean authenticate(HashMap<String, String> credentials);

}
