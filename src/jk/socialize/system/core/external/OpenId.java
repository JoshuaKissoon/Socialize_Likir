/**
 * @author Joshua Kissoon
 * @date 20131113
 * @description The abstraction class for the OpenId authentication service
 */
package jk.socialize.system.core.external;

import java.util.HashMap;

public class OpenId implements AuthenticationService
{

    @Override
    public Boolean connect()
    {
        return true;
    }

    @Override
    public Boolean disconnect()
    {
        return true;
    }

    @Override
    public Boolean authenticate(HashMap<String, String> credentials)
    {
        return true;
    }
}
