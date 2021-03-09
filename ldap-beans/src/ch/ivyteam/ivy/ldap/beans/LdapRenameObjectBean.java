/*
 * Copyright (C) 2016 Axon Ivy AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ch.ivyteam.ivy.ldap.beans;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import ch.ivyteam.awtExt.AWTUtil;
import ch.ivyteam.ivy.ldap.beans.util.JndiConfig;
import ch.ivyteam.ivy.ldap.beans.util.JndiProvider;
import ch.ivyteam.ivy.ldap.beans.util.JndiUtil;
import ch.ivyteam.ivy.persistence.PersistencyException;
import ch.ivyteam.ivy.process.engine.IRequestId;
import ch.ivyteam.ivy.process.extension.impl.AbstractProcessExtensionConfigurationEditor;
import ch.ivyteam.ivy.process.extension.impl.AbstractUserProcessExtension;
import ch.ivyteam.ivy.scripting.exceptions.IvyScriptException;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;

/**
 * Bean to change the common name (cn) of a jndi object Extended copy of Reto
 * Weiss' LdapAttributeModifierBean:
 * @author Reto Weiss
 * @version bb 29.09.2016 bb: fix parameter extraction for LdapRenameObjectBean
 * @version Ken Iseli November2012
 * @version Marc Willaredt August2009 Updated to Ivy 4.1
 * @version bb 22.11.2005 bb: allow ivy attributes ("in.xx") to set url, name,
 *          password
 * @version pk 29.07.2004 pk: renamed from JndiAttributeModifierBean to
 *          LdapAttributeModifierBean, renamed some fields
 * @version ReW 21.5.2002 created
 */
public class LdapRenameObjectBean extends AbstractUserProcessExtension
{

  /** Jndi Configuration */
  private JndiConfig jndiConfig;

  /** object name */
  private String objectName;

  /** new object name */
  private String newObjectName;

  /**
   * Configuration editor for the jndi attribute modifier bean
   * 
   * @author Reto Weiss
   * @version pk 29.07.2004 pk: renamed some fields
   * @version ReW 21.5.2002 created
   */
  public static class Editor extends AbstractProcessExtensionConfigurationEditor
  {

    /**
     * The configuration panel
     */
    private JTabbedPane tabbedPanel;

    /** Jndi server configuration */
    private JndiConfig jndiConfig;

    /** the name of the object to modify */
    private JTextField tfObjectName;

    /** the name of the new object */
    private JTextField tfNewObjectName;

    /** Jndi config panel */
    JndiConfigPanel jndiConfigPanel;

    /**
     * Constructor for the Editor object
     */
    public Editor()
    {

      ResourceBundle resBun = ResourceBundle.getBundle(
              "TextResource", new Locale("de"));

      jndiConfig = new JndiConfig(JndiProvider.NOVELL_E_DIRECTORY,
              "ldap://", JndiConfig.AUTH_KIND_SIMPLE, "", "", false,
              false, "");

      tabbedPanel = new JTabbedPane();

      // Jndi Server panel
      jndiConfigPanel = new JndiConfigPanel(jndiConfig, false);
      tabbedPanel.add(resBun.getString("search_server_pane"),
              jndiConfigPanel);

      // Jndi Rename Object panel
      JPanel modifyAttributePanel = new JPanel(new GridBagLayout());

      // root object
      JLabel label = new JLabel(resBun.getString("rename_name_oldObject"));
      AWTUtil.constrain(modifyAttributePanel, label, 0, 0, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              10, 10, 0, 10);

      tfObjectName = new JTextField(40);
      AWTUtil.constrain(modifyAttributePanel, tfObjectName, 1, 0, 3, 1,
              GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
              1.0, 0.0, 10, 10, 0, 10);

      JLabel newObjectNamelabel = new JLabel(resBun.getString("rename_name_newObject"));
      AWTUtil.constrain(modifyAttributePanel, newObjectNamelabel, 0, 1, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              10, 10, 0, 10);

      tfNewObjectName = new JTextField(40);
      AWTUtil.constrain(modifyAttributePanel, tfNewObjectName, 1, 1, 3, 1,
              GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
              1.0, 0.0, 10, 10, 0, 10);

      tabbedPanel.add(resBun.getString("rename_pane_renaming"), modifyAttributePanel);
    }

    /**
     * This method is called if the user pressed the OK button of the dialog
     * containing this editor. The dialog is closed and the new configuration is
     * used only if this method return true
     * 
     * @return true
     */
    @Override
    public boolean acceptInput()
    {
      return true;
    }

    @Override
    public Component getComponent()
    {
      return tabbedPanel;
    }

    /**
     * @return a description of a configuration. This description will be sent
     *         to the corresponding OuterProcessBean
     */
    @Override
    public String getConfiguration()
    {
      Properties props = new Properties();

      jndiConfigPanel.saveModel();
      props.setProperty("server_provider", jndiConfig.getProvider()
              .getProviderName());
      props.setProperty("server_url", jndiConfig.getUrl());
      props.setProperty("server_authkind", jndiConfig
              .getAuthenticationKind());
      props.setProperty("server_username", jndiConfig.getUserName());
      props.setProperty("server_password", jndiConfig.getPassword());
      props.setProperty("server_useSsl", new Boolean(jndiConfig
              .isUseSsl()).toString());
      String defaultContext = jndiConfig.getDefaultContext();
      props.setProperty("server_context", defaultContext);

      props.setProperty("rename_name_oldObject", tfObjectName.getText().trim());

      props.setProperty("rename_name_newObject", tfNewObjectName.getText().trim());

      return PropertyUtil.toRawString(props);
    }

    /**
     * Initialize the editor with an older configuration
     * 
     * @param configString The new configuration value
     */
    @Override
    public void setConfiguration(String configString)
    {
      ByteArrayInputStream bais = null;
      Properties props = new Properties();
      int pos;

      if (configString == null)
      {
        return;
      }

      try
      {
        bais = new ByteArrayInputStream(configString.getBytes());
        props.load(bais);

        if (props.get("server_provider") != null)
        {
          for (pos = 0; pos < JndiProvider.PROVIDERS.length; pos++)
          {
            if (JndiProvider.PROVIDERS[pos].getProviderName()
                    .equals(props.get("server_provider")))
            {
              jndiConfig.setProvider(JndiProvider.PROVIDERS[pos]);
              break;
            }
          }
        }
        jndiConfig.setUrl(props.getProperty("server_url", jndiConfig
                .getUrl()));
        jndiConfig.setAuthenticationKind(props.getProperty(
                "server_authkind", jndiConfig.getAuthenticationKind()));
        jndiConfig
                .setUserName(props.getProperty("server_username", ""));
        jndiConfig
                .setPassword(props.getProperty("server_password", ""));
        jndiConfig.setUseSsl(new Boolean(props.getProperty(
                "server_useSsl", Boolean.FALSE.toString()))
                .booleanValue());
        jndiConfig.setDefaultContext(props.getProperty(
                "server_context", ""));
        jndiConfigPanel.loadModel();

        tfObjectName.setText(props.getProperty("rename_name_oldObject", ""));

        tfNewObjectName.setText(props.getProperty("rename_name_newObject", ""));

      }
      catch (IOException ex)
      {
      }
      finally
      {
        if (bais != null)
        {
          try
          {
            bais.close();
          }
          catch (IOException ex)
          {
          }
        }
      }
    }
  }

  /**
   * Constructor
   */
  public LdapRenameObjectBean()
  {
    jndiConfig = new JndiConfig(JndiProvider.NOVELL_E_DIRECTORY, "ldap://",
            JndiConfig.AUTH_KIND_SIMPLE, "", "", false, false, "");
  }

  /**
   * This method is the program performed in a program interface element every
   * time a token comes to the element.
   * 
   * @param argument a wrapper for the token value.
   * @throws Exception Exception
   */
  @Override
  public CompositeObject perform(IRequestId reqID, CompositeObject argument,
          IIvyScriptContext cont) throws Exception
  {
    DirContext dirContext;
    String modifyObjectName;
    String newName;

    if (objectName.trim().startsWith("\"")
            && (objectName.trim().endsWith("\"")))
    {
      modifyObjectName = objectName.substring(1, objectName.length() - 1);
    }
    else
    {
      modifyObjectName = (String) getVariable(objectName, cont);
      if (modifyObjectName == null)
      {
        modifyObjectName = objectName;
      }
    }

    if (newObjectName.trim().startsWith("\"") && newObjectName.trim().endsWith("\""))
    {
      newName = newObjectName.substring(1, newObjectName.length() - 1);
    }
    else
    {
      newName = (String) getVariable(newObjectName, cont);
      if (newName == null)
      {
        newName = newObjectName;
      }
    }

    JndiConfig expandedJndiConfig = (JndiConfig) jndiConfig.clone();
    // try to expand url, name and password fields
    String propStr = expandedJndiConfig.getUrl();
    if (propStr != null)
    {
      if (getVariable(propStr, cont) != null)
      {
        propStr = (String) getVariable(propStr, cont);
        expandedJndiConfig.setUrl(propStr);
      }
    }
    propStr = expandedJndiConfig.getUserName();
    if (propStr != null)
    {
      if (getVariable(propStr, cont) != null)
      {
        propStr = (String) getVariable(propStr, cont);
        expandedJndiConfig.setUserName(propStr);
      }
    }
    propStr = expandedJndiConfig.getPassword();
    if (propStr != null)
    {
      if (getVariable(propStr, cont) != null)
      {
        propStr = (String) getVariable(propStr, cont);
        expandedJndiConfig.setPassword(propStr);
      }
    }

    // query the naming and directory service
    // dirContext = new InitialDirContext(expandedJndiConfig.getEnvironement());
    // // this only works in Xivy version < 4.3.15
    dirContext = JndiUtil.openDirContext(expandedJndiConfig);
    try
    {
      /*
       * objectName =
       * "cn=Lager_AG_004,ou=Drucker,ou=Infrastruktur,ou=Informatik,DC=SNB,DC=CH"
       * ; newObjectName =
       * "cn=Lager_AG_020,ou=Drucker,ou=Infrastruktur,ou=Informatik,DC=SNB,DC=CH"
       * ;
       */
      dirContext.rename(modifyObjectName, newName);
    }
    finally
    {
      if (dirContext != null)
      {
        try
        {
          dirContext.close();
        }
        catch (NamingException ex)
        {
        }
      }
    }

    return argument;
  }

  @Override
  public void abort(IRequestId arg0)
  {
  }

  @Override
  public String getAdditionalLogInfo(IRequestId arg0)
  {
    return null;
  }

  @Override
  public void release() throws Exception
  {
    // TODO Auto-generated method stub

  }

  /**
   * Sets a configuration string. This configuration string is usually produced
   * by the configuration editor of the element.
   * 
   * @param configuration the configuration
   */
  @Override
  public void setConfiguration(String configuration)
  {
    ByteArrayInputStream bais = null;
    Properties props = new Properties();
    int pos;

    if (configuration == null)
    {
      return;
    }

    try
    {
      bais = new ByteArrayInputStream(configuration.getBytes());
      props.load(bais);

      if (props.get("server_provider") != null)
      {
        for (pos = 0; pos < JndiProvider.PROVIDERS.length; pos++)
        {
          if (JndiProvider.PROVIDERS[pos].getProviderName().equals(
                  props.get("server_provider")))
          {
            jndiConfig.setProvider(JndiProvider.PROVIDERS[pos]);
            break;
          }
        }
      }
      jndiConfig.setUrl(props.getProperty("server_url", jndiConfig
              .getUrl()));
      jndiConfig.setAuthenticationKind(props.getProperty(
              "server_authkind", jndiConfig.getAuthenticationKind()));
      jndiConfig.setUserName(props.getProperty("server_username", ""));
      jndiConfig.setPassword(props.getProperty("server_password", ""));
      jndiConfig.setUseSsl(new Boolean(props.getProperty("server_useSsl",
              Boolean.FALSE.toString())).booleanValue());

      objectName = props.getProperty("rename_name_oldObject", "");
      if (objectName.startsWith("in."))
      {
        objectName = objectName.substring(3);
      }

      newObjectName = props.getProperty("rename_name_newObject", "");
      if (newObjectName.startsWith("in."))
      {
        newObjectName = newObjectName.substring(3);
      }

    }
    catch (IOException ex)
    {
    }
    finally
    {
      if (bais != null)
      {
        try
        {
          bais.close();
        }
        catch (IOException ex)
        {
        }
      }
    }
  }

  @Override
  public void start() throws Exception
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void stop() throws Exception
  {
    // TODO Auto-generated method stub

  }

  public Object getVariable(String name, IIvyScriptContext cont)
  {
    try
    {
      if (name.contains("in."))
        return this.executeIvyScript(cont, name);
      else
        return this.executeIvyScript(cont, "in." + name);
    }
    catch (IvyScriptException e)
    {
      return name;
    }
    catch (PersistencyException e)
    {
      return name;
    }
  }

  public void setVariable(String name, Object value,
          CompositeObject argument, IIvyScriptContext cont)
          throws NoSuchFieldException, IvyScriptException,
          PersistencyException
  {
    if (name.indexOf(".") < 0)
    {
      argument.set(name, value);
    }
    else
    {
      String newArgumentName = name.substring(0, name.indexOf("."));
      String RemainingName = name.substring(name.indexOf(".") + 1, name
              .length());
      setVariable(RemainingName, value, (CompositeObject) argument
              .get(newArgumentName), cont);
    }
  }
}
