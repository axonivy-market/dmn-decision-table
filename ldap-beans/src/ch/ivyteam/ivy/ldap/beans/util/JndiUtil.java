package ch.ivyteam.ivy.ldap.beans.util;

import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * Utility class for jndi
 * @author rwei
 * @since 07.01.2008
 */
public class JndiUtil
{
  
  /**
   * Gets the jndi directory context to make some operations on the naming and
   * directory server.
   * @param environment the environment
   * @return jndi context
   * @throws NamingException if context could not be created
   */
  private static DirContext openDirContext(Hashtable<?,?> environment) throws NamingException
  {
    return new InitialDirContext(environment);
  }
  
  /**
   * Gets the jndi directory context to make some operations on the naming and
   * directory server.
   * @param jndiConfig the jndi configuration
   * @return jndi context
   * @throws NamingException if context could not be created
   */
  public static DirContext openDirContext(JndiConfig jndiConfig) throws NamingException
  {
    DirContext context = openDirContext(jndiConfig.createEnvironment());
    return context;
  }

}
