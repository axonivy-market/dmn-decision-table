package ch.ivyteam.ivy.ldap.beans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil
{
  public static String toRawString(Properties props)
    {
      ByteArrayOutputStream baos = null;
      try
      {
        baos = new ByteArrayOutputStream();
        props.store(baos, "");
        return new String(baos.toByteArray());
      }
      catch (IOException ex)
      {
        return "";
      }
      finally
      {
        if (baos != null)
        {
          try
          {
            baos.close();
          }
          catch (IOException ex)
          {
          }
        }
      }
    }
}