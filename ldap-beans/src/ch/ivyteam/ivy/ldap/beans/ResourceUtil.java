package ch.ivyteam.ivy.ldap.beans;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceUtil
{

  public static ResourceBundle getBundle(String resource)
  {
    try
    {
      return ResourceBundle.getBundle(resource, new Locale("de"));
    }
    catch (MissingResourceException ex)
    {
      return new ResourceBundle(){

        @Override
        protected Object handleGetObject(String key)
        {
          return key;
        }

        @Override
        public Enumeration<String> getKeys()
        {
          return Collections.emptyEnumeration();
        }};
    }
  }
  
}
