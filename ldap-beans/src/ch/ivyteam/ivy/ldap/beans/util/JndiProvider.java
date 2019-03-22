package ch.ivyteam.ivy.ldap.beans.util;

import java.io.Serializable;

/**
 * Holds information about an Jndi Provider
 * @author Reto Weiss
 * @version ReW 8.4.2002 created
 */
public class JndiProvider implements Serializable
{
  private static final long serialVersionUID = 640813072598743556L;

  public static final JndiProvider ACTIVE_DIRECTORY = new JndiProvider("Microsoft Active Directory", "com.sun.jndi.ldap.LdapCtxFactory");
  public static final JndiProvider NOVELL_E_DIRECTORY = new JndiProvider("Novell eDirectory", "com.sun.jndi.ldap.LdapCtxFactory");
  public static final JndiProvider LDAP = new JndiProvider("LDAP", "com.sun.jndi.ldap.LdapCtxFactory");

  public static final JndiProvider[] PROVIDERS = {ACTIVE_DIRECTORY, NOVELL_E_DIRECTORY, LDAP};

  private final String providerName;
  private final String providerClass;

  public JndiProvider(String providerName, String providerClass)
  {
    this.providerName = providerName;
    this.providerClass = providerClass;
  }

  public String getProviderName()
  {
    return providerName;
  }

  public String getProviderClass()
  {
    return providerClass;
  }

  @Override
  public String toString()
  {
    return providerName;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj != null)
    {
      if (obj instanceof JndiProvider)
      {
        return providerName.equals(((JndiProvider) obj).providerName);
      }
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return providerName.hashCode();
  }

}