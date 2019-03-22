package ch.ivyteam.ivy.ldap.beans.util;

import java.io.Serializable;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NameClassPair;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.apache.commons.lang3.StringUtils;

/**
 * Stores configuration information about a JNDI environement
 * 
 * @author Reto Weiss
 * @version pk 03.08.2004: added fields serverPort and serverName
 * @version pk 02.08.2004: added isPasswordDynamic field with getter and setter.
 *          added getServerName and getServerPort
 * @version ReW 8.4.2002 created
 */
public class JndiConfig implements Serializable, Cloneable
{
  private static final long serialVersionUID = -5023634903603456745L;

  public static final String AUTH_KIND_NONE = "none";
  public static final String AUTH_KIND_SIMPLE = "simple";
  
  private static final String LDAP_CONNECTION_POOL = "com.sun.jndi.ldap.connect.pool";
  
  private JndiProvider provider;
  private String url;
  private String authenticationKind;
  private String userName;
  private String password;
  private String defaultContext;
  private boolean useSsl;
  private boolean useLdapConnectionPool;
  
  public JndiConfig(JndiProvider provider, String url, String authenticationKind, String userName, String password, boolean useSsl, boolean useLdapConnectionPool, String defaultContext)
  {
    this.provider = provider;
    this.url = url;
    this.authenticationKind = authenticationKind;
    this.userName = userName;
    this.password = password;
    this.useSsl = useSsl;
    this.useLdapConnectionPool = useLdapConnectionPool;
    this.defaultContext = defaultContext;
  }

  public void setProvider(JndiProvider provider)
  {
    this.provider = provider;
  }

  public JndiProvider getProvider()
  {
    return provider;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public String getUrl()
  {
    return url;
  }

  public void setAuthenticationKind(String authenticationKind)
  {
    this.authenticationKind = authenticationKind;
  }

  public String getAuthenticationKind()
  {
    return authenticationKind;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getPassword()
  {
    return password;
  }

  public void setDefaultContext(String defaultContext)
  {
    this.defaultContext = defaultContext;
  }

  /**
   * <p>Gets the default jndi context name.</p>
   * <p>
   * Do not use this method since it is unsave to use with LDAP. See issue #25327 for details.
   * Never ever use strings in JNDI when using it with LDAP always use LdapName. In JNDI Strings are escaped according to JNDI and not LDAP which leads to conflict.
   * Instead of {@link NameClassPair#getName()} use {@link NameClassPair#getNameInNamespace()} which returns a correct LdapName
   * Instead of {@link SearchResult#getName()} use {@link SearchResult#getNameInNamespace()} which returns a correct LdapName
   * @return default jndi context name
   */
  public String getDefaultContext()
  {
    return defaultContext;
  }
  
  public LdapName getDefaultContextName() throws InvalidNameException
  {
    if (StringUtils.isBlank(defaultContext))
    {
      return new LdapName("");
    }
    return new LdapName(defaultContext);
  }

  public void setUseSsl(boolean useSsl)
  {
    this.useSsl = useSsl;
  }

  public boolean isUseSsl()
  {
    return useSsl;
  }
  
  /**
   * Gets the jndi environement property hashtable
   * @return jdni environement
   */
  public Hashtable<?,?> getEnvironement()
  {
    return createEnvironment();
  }

  Hashtable<?,?> createEnvironment()
  {
    Hashtable<String, Object> env = new Hashtable<String, Object>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, provider.getProviderClass());
    env.put(Context.PROVIDER_URL, url);
    if (authenticationKind.equals(AUTH_KIND_NONE))
    {
      env.put(Context.SECURITY_AUTHENTICATION, AUTH_KIND_NONE);
    }
    else if (authenticationKind.equals(AUTH_KIND_SIMPLE))
    {
      env.put(Context.SECURITY_AUTHENTICATION, AUTH_KIND_SIMPLE);
      env.put(Context.SECURITY_PRINCIPAL, userName);
      env.put(Context.SECURITY_CREDENTIALS, password);
    }
    else
    {
      env.put(Context.SECURITY_AUTHENTICATION, AUTH_KIND_NONE);
    }

    if (useSsl)
    {
      env.put(Context.SECURITY_PROTOCOL, "ssl");
    }
    if (useLdapConnectionPool)
    {
      env.put(LDAP_CONNECTION_POOL, "true");
    }
    
    env.put(Context.REFERRAL, "follow");
    
    if (JndiProvider.ACTIVE_DIRECTORY.equals(this.provider))
    {
      // fix for active directory bug. 
      // See more details in class ch.ivyteam.naming.ldap.ldapURLContextFactory
      env.put(Context.URL_PKG_PREFIXES, "ch.ivyteam.naming"); 
    }
    return env;
  }

  @Override
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      return null;
    }
  }

  @Override
  public boolean equals(Object obj)
  {
   JndiConfig jndiConfig;
   if (obj instanceof JndiConfig)
   {
     jndiConfig = (JndiConfig)obj;
     
     if (this==jndiConfig)
     {
       return true;
     }
     
     return ((url.equals(jndiConfig.url))&&
     				 (provider.equals(jndiConfig.provider))&&
     				 (authenticationKind.equals(jndiConfig.authenticationKind))&&
     				 (defaultContext.equals(jndiConfig.defaultContext))&&
     				 (userName.equals(jndiConfig.userName))&&
     				 (password.equals(jndiConfig.password))&&
     				 (useSsl==jndiConfig.useSsl));
   }
    return false;
  }

  @Override
  public String toString()
  {
    return "JndiConfig[url=" + url + ", "
            + "provider=" + provider + ", "
            + "authenticationKind=" + authenticationKind + ","
            + "useSSL=" + useSsl + ","
            + "defaultContext=" + defaultContext + "]";
  }

  @Override
  public int hashCode()
  {
    return getUrl().hashCode();
  }

  public boolean isUseLdapConnectionPool()
  {
    return useLdapConnectionPool;
  }

  public void setUseLdapConnectionPool(boolean _useLdapConnectionPool)
  {
    this.useLdapConnectionPool = _useLdapConnectionPool;
  }
}
