/*
 * Copyright (C) 2016 AXON IVY AG
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

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import ch.ivyteam.awtExt.AWTUtil;
import ch.ivyteam.ivy.persistence.PersistencyException;
import ch.ivyteam.ivy.process.engine.IRequestId;
import ch.ivyteam.ivy.process.extension.impl.AbstractProcessExtensionConfigurationEditor;
import ch.ivyteam.ivy.process.extension.impl.AbstractUserProcessExtension;
import ch.ivyteam.ivy.scripting.exceptions.IvyScriptException;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;
import ch.ivyteam.naming.JndiConfig;
import ch.ivyteam.naming.JndiProvider;
import ch.ivyteam.naming.JndiUtil;

/**
 * PI-Element to query LDAP-Servers Copies an LDAP object.
 * @author Bruno Bütler
 * @version MarcWillaredt August2009 Updated to Ivy 4.1
 * @version bb 24.05.2006 created.
 */
public class LdapCopyBean extends AbstractUserProcessExtension
{

  /** Jndi server configuration */
  private JndiConfig jndiConfig;

  /** Filter attribute names */
  private Hashtable<String, String> filterAttributesHashtable = new Hashtable<>();

  /** Jndi search control */
  private SearchControls searchControl = new SearchControls();

  /** ivy Attribute with the cn of the copied element */
  private String newObjectName = null;

  /** The root object to begin search for */
  private String rootObjectName;

  /** any filter as text */
  String anyFilterText = null;

  /**
   * Configuration editor for jndi query bean
   * 
   * @author Reto Weiss
   * @version bb 02.11.2005 bb: a variant of LDAPQueryBean that returns a
   *          recordset
   * @version ReW 14.5.2002 created
   */
  public static class Editor extends AbstractProcessExtensionConfigurationEditor
  {
    /**
     * The configuration panel
     */
    private JTabbedPane tabbedPanel;

    /** Jndi server configuration */
    private JndiConfig jndiConfig;

    /** search scope one object */
    private JRadioButton rbOneObject;

    /** search scope one level */
    private JRadioButton rbOneLevel;

    /** search scope sub tree */
    private JRadioButton rbSubTree;

    /** search filter */
    private JTable filterTable;

    /** search filter as text */
    private JTextArea filterText;

    /** search filter as table */
    private JRadioButton rbFilterTable;

    /** search filter as text */
    private JRadioButton rbFilterText;

    /** Search filter panel */
    private JPanel filterPanel;

    /** card layout of the filter panel */
    private CardLayout filterLayout;

    /** Return panel */
    private JPanel returnPanel;

    /** card layout of the return panel */
    private CardLayout returnLayout;

    /** ivy attribute with the cn of the copied object */
    private JTextField tfNewObjectName;

    /** the name of the root object to start the search at */
    private JTextField tfRootObjectName;

    /** Jndi config panel */
    JndiConfigPanel jndiConfigPanel;

    /**
     * Constructor for the Editor object
     */
    public Editor()
    {

      ResourceBundle resBun = ResourceBundle
              .getBundle("TextResource", new Locale("de"));

      jndiConfig = new JndiConfig(JndiProvider.NOVELL_E_DIRECTORY,
              "ldap://", JndiConfig.AUTH_KIND_SIMPLE, "", "", false,
              false, "");

      tabbedPanel = new JTabbedPane();

      // Jndi Server panel
      jndiConfigPanel = new JndiConfigPanel(jndiConfig, false);
      tabbedPanel.add(resBun.getString("search_server_pane"),
              jndiConfigPanel);

      // Jndi Search panel
      JPanel searchPanel = new JPanel(new GridBagLayout());

      // root object
      JLabel label = new JLabel(resBun.getString("search_root_object"));
      AWTUtil.constrain(searchPanel, label, 0, 0, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              10, 10, 0, 10);

      tfRootObjectName = new JTextField(40);
      AWTUtil.constrain(searchPanel, tfRootObjectName, 1, 0, 3, 1,
              GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
              1.0, 0.0, 10, 10, 0, 10);

      // Search scope
      label = new JLabel(resBun.getString("search_scope"));
      AWTUtil.constrain(searchPanel, label, 0, 1, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              10, 10, 0, 10);

      ButtonGroup scopeButtons = new ButtonGroup();
      rbOneObject = new JRadioButton(resBun
              .getString("search_scope_oneObject"));
      rbOneObject.setSelected(true);
      AWTUtil.constrain(searchPanel, rbOneObject, 1, 1, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              10, 10, 0, 10);

      rbOneLevel = new JRadioButton(resBun
              .getString("search_scope_oneLevel"));
      AWTUtil.constrain(searchPanel, rbOneLevel, 2, 1, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              10, 10, 0, 10);

      rbSubTree = new JRadioButton(resBun
              .getString("search_scope_subTree"));
      AWTUtil.constrain(searchPanel, rbSubTree, 3, 1, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              10, 10, 0, 10);

      scopeButtons.add(rbOneObject);
      scopeButtons.add(rbOneLevel);
      scopeButtons.add(rbSubTree);
      label = new JLabel(resBun.getString("search_filter"));
      AWTUtil.constrain(searchPanel, label, 0, 2, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              10, 10, 10, 10);

      ButtonGroup filterButtons = new ButtonGroup();
      rbFilterTable = new JRadioButton(resBun
              .getString("search_filter_table"));
      rbFilterTable.setSelected(true);
      AWTUtil.constrain(searchPanel, rbFilterTable, 1, 2, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              0, 10, 0, 10);

      rbFilterText = new JRadioButton(resBun
              .getString("search_filter_text"));
      rbFilterText.setSelected(true);
      AWTUtil.constrain(searchPanel, rbFilterText, 2, 2, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              0, 10, 0, 10);
      filterButtons.add(rbFilterTable);
      filterButtons.add(rbFilterText);

      rbFilterTable.addActionListener(new FilterTableActionListener());
      rbFilterText.addActionListener(new FilterTextActionListener());

      filterLayout = new CardLayout();
      filterPanel = new JPanel(filterLayout);

      // filter table
      filterTable = new JTable(20, 0);
      ((DefaultTableModel) filterTable.getModel()).addColumn(resBun
              .getString("search_filter_attribute"));
      ((DefaultTableModel) filterTable.getModel()).addColumn(resBun
              .getString("search_filter_value"));
      filterTable.setPreferredScrollableViewportSize(new Dimension(400,
              300));
      JScrollPane scrollPane = new JScrollPane(filterTable);
      filterPanel.add("filterTable", scrollPane);

      // filter text
      filterText = new JTextArea(10, 80);
      JScrollPane scrollPane2 = new JScrollPane(filterText);
      filterPanel.add("filterText", scrollPane2);

      AWTUtil.constrain(searchPanel, filterPanel, 1, 3, 3, 1,
              GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0,
              10, 10, 10, 10);

      tabbedPanel
              .add(resBun.getString("search_filter_pane"), searchPanel);

      // Jndi Result panel
      JPanel resultPanel = new JPanel(new GridBagLayout());

      label = new JLabel(resBun.getString("copy_object"));
      AWTUtil.constrain(resultPanel, label, 0, 0, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              10, 10, 10, 10);

      returnLayout = new CardLayout();
      returnPanel = new JPanel(returnLayout);

      // result for all results
      label = new JLabel(resBun.getString("copy_object_cn"));

      AWTUtil.constrain(resultPanel, label, 0, 1, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0.0,
              0.0, 0, 10, 0, 10);

      tfNewObjectName = new JTextField(50);
      AWTUtil.constrain(resultPanel, tfNewObjectName, 1, 1, 1, 1,
              GridBagConstraints.HORIZONTAL,
              GridBagConstraints.NORTHWEST, 1.0, 0.0, 0, 0, 0, 0);

      JPanel dummyPanel = new JPanel(new GridBagLayout());
      dummyPanel.setPreferredSize(new Dimension(400, 300));
      AWTUtil.constrain(resultPanel, dummyPanel, 0, 3, 2, 2,
              GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0.0,
              0.0, 0, 0, 0, 0);

      tabbedPanel
              .add(resBun.getString("search_result_pane"), resultPanel);

    }

    /**
     * @return a Component that will be embedded in a JDialog
     */
    @Override
    public Component getComponent()
    {
      return tabbedPanel;
    }

    /**
     * @return a description of a configuration. This description will be sent
     *         to the corresponding OuterProcessBean
     */
    // @SuppressWarnings({"null", "deprecation"})
    @Override
    public String getConfiguration()
    {
      String attribute, value;
      Properties props = new Properties();

      TableCellEditor cellEditor;
      cellEditor = filterTable.getCellEditor();
      if (cellEditor != null)
      {
        cellEditor.stopCellEditing();
      }
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
      @SuppressWarnings("deprecation")
      String defaultContext = jndiConfig.getDefaultContext();
      props.setProperty("server_context", defaultContext);

      props.setProperty("search_root_object", tfRootObjectName.getText());

      if (rbOneObject.isSelected())
      {
        props.setProperty("search_scope", "oneObject");
      }
      else if (rbOneLevel.isSelected())
      {
        props.setProperty("search_scope", "oneLevel");
      }
      else if (rbSubTree.isSelected())
      {
        props.setProperty("search_scope", "subTree");
      }

      if (rbFilterText.isSelected())
      {
        props.setProperty("search_filter_format", "filterText");
      }
      props.setProperty("search_filter_text", filterText.getText());

      TableModel model = filterTable.getModel();
      for (int pos = 0; pos < model.getRowCount(); pos++)
      {
        attribute = (String) model.getValueAt(pos, 0);
        value = (String) model.getValueAt(pos, 1);
        if ((attribute != null) && (value == null))
        {
          value = "";
        }
        else if ((attribute == null) && (value != null))
        {
          attribute = "";
        }
        if ((attribute != null) && (value != null))
        {
          props.setProperty("search_filter_attribute_" + pos,
                  attribute.trim());
          props.setProperty("search_filter_value_" + pos, value
                  .trim());
        }
        else
        {
          break;
        }
      }

      props.setProperty("new_object", tfNewObjectName.getText().trim());

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
      String attribute, value;
      DefaultTableModel model;
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

        tfRootObjectName.setText(props.getProperty(
                "search_root_object", ""));

        if ("subTree".equals(props.getProperty("search_scope", "")))
        {
          rbSubTree.setSelected(true);
        }
        else if ("oneLevel".equals(props.getProperty("search_scope",
                "")))
        {
          rbOneLevel.setSelected(true);
        }
        else
        {
          rbOneObject.setSelected(true);
        }

        if ("filterText".equals(props
                .getProperty("search_filter_format")))
        {
          rbFilterText.setSelected(true);
          filterLayout.show(filterPanel, "filterText");
        }
        else
        {
          rbFilterTable.setSelected(true);
          filterLayout.show(filterPanel, "filterTable");
        }
        filterText.setText(props.getProperty("search_filter_text"));

        pos = 0;
        model = new DefaultTableModel(filterTable.getModel()
                .getRowCount(), filterTable.getModel().getColumnCount());
        Vector<String> cols = new Vector<>();
        for (int i = 0; i < filterTable.getColumnCount(); i++)
        {
          cols.add(filterTable.getColumnName(i));
        }
        model.setColumnIdentifiers(cols);
        do
        {
          attribute = props.getProperty("search_filter_attribute_"
                  + pos, null);
          value = props.getProperty("search_filter_value_" + pos,
                  null);
          if ((attribute != null) && (value != null))
          {
            model.insertRow(pos, new Object[] {attribute, value});
            pos++;
          }
        } while ((attribute != null) && (value != null));
        filterTable.setModel(model);

        tfNewObjectName.setText(props.getProperty("new_object", ""));

        returnLayout.show(returnPanel, "all");

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

    /**
     * This method is called if the user pressed the OK button of the dialog
     * containing this editor. The dialog is closed and the new configuration is
     * used only if this methode return true
     * 
     * @return true
     */
    @Override
    public boolean acceptInput()
    {
      return true;
    }

    class FilterTableActionListener implements ActionListener
    {
      /**
       * Invoked when an action occurs.
       * 
       * @param e
       */
      @Override
      public void actionPerformed(ActionEvent e)
      {
        filterLayout.show(filterPanel, "filterTable");
      }
    }

    class FilterTextActionListener implements ActionListener
    {
      /**
       * Invoked when an action occurs.
       * 
       * @param e
       */
      @Override
      public void actionPerformed(ActionEvent e)
      {
        filterLayout.show(filterPanel, "filterText");
      }
    }

  }

  /**
   * Constructor
   */
  public LdapCopyBean()
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
    NamingEnumeration<SearchResult> resultEnum;
    Enumeration<?> attrEnum;
    DirContext dirContext;
    String attribute, value;
    String filter = "";
    String objectName;
    SearchResult searchResult;
    Attributes jndiAttributes;
    String copiedObjectName = new String();

    // Build search filter
    if (anyFilterText != null)
    {
      // expand ivy attributtes "in.x.." in the filter string
      StringBuffer sb = new StringBuffer();
      int at = anyFilterText.indexOf("in.");
      int to = 0;
      while (at >= 0)
      {
        sb.append(anyFilterText.substring(to, at));
        to = anyFilterText.indexOf(")", at);
        String attr = anyFilterText.substring(at + 3, to);
        if (getVariable(attr, cont) != null)
        {
          // ivyGrid argument found. Get it an make string out of it
          sb.append(getVariable(attr, cont).toString());
        }
        at = anyFilterText.indexOf("in.", to);
      }
      sb.append(anyFilterText.substring(to));
      filter = sb.toString();
    }
    else
    {
      attrEnum = filterAttributesHashtable.keys();
      while (attrEnum.hasMoreElements())
      {
        String oldAttribute = (String) attrEnum.nextElement();
        attribute = (String) getVariable(oldAttribute, cont);
        value = filterAttributesHashtable.get(oldAttribute);
        value = value.trim();
        value = (String) getVariable(value, cont);
        if (filter.length() == 0)
        {
          filter += "(&";
        }
        filter += "(";
        filter += attribute + "=" + value;
        filter += ")";
      }

      if (filter.length() > 0)
      {
        filter += ")";
      }
    }
    objectName = (String) getVariable(rootObjectName, cont);
    if (!objectName.startsWith("\""))
    {
      objectName = "\"" + objectName;
    }
    if (!objectName.endsWith("\""))
    {
      objectName += "\"";
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

    // name of new object
    if (newObjectName.trim().length() == 0)
    {
      return null;
    }
    else if (newObjectName.trim().startsWith("\"")
            && (newObjectName.trim().endsWith("\"")))
    {
      copiedObjectName = newObjectName.substring(1, newObjectName
              .length() - 1);
    }
    else
    {
      copiedObjectName = (String) getVariable(newObjectName, cont);
      if (copiedObjectName == null)
      {
        copiedObjectName = newObjectName;
      }
    }

    // query the naming and directory service
    // dirContext = new InitialDirContext(expandedJndiConfig.getEnvironement());
    // // this only works in Xivy version < 4.3.15
    dirContext = JndiUtil.openDirContext(expandedJndiConfig);
    try
    {
      // read the first result
      resultEnum = dirContext.search(objectName, filter, searchControl);
      if (resultEnum == null || !resultEnum.hasMoreElements())
      {
        // no result found! --> set ivy attribute new objectName to null
        setVariable(copiedObjectName, null, argument);
      }
      else
      {
        searchResult = resultEnum.nextElement();
        if (searchResult == null)
        {
          // no Result found! --> set ivy attribute new objectName to
          // null
          setVariable(copiedObjectName, null, argument);
        }
        else
        {
          jndiAttributes = searchResult.getAttributes();

          jndiAttributes.remove("cn"); // remove the cn, will be
          // set via copiedObjectName

          // write newobject with this attributes
          dirContext.bind(copiedObjectName, null, jndiAttributes);
        }
      }
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
    // TODO Auto-generated method stub

  }

  @Override
  public String getAdditionalLogInfo(IRequestId arg0)
  {
    // TODO Auto-generated method stub
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
    String attribute, value;
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
      jndiConfig.setDefaultContext(props
              .getProperty("server_context", ""));

      rootObjectName = props.getProperty("search_root_object", "");
      if (rootObjectName.startsWith("in."))
      {
        rootObjectName = rootObjectName.substring(3);
      }

      if ("subTree".equals(props.getProperty("search_scope", "")))
      {
        searchControl.setSearchScope(SearchControls.SUBTREE_SCOPE);
      }
      else if ("oneLevel".equals(props.getProperty("search_scope", "")))
      {
        searchControl.setSearchScope(SearchControls.ONELEVEL_SCOPE);
      }
      else
      {
        searchControl.setSearchScope(SearchControls.OBJECT_SCOPE);
      }

      pos = 0;
      do
      {
        attribute = props.getProperty("search_filter_attribute_" + pos,
                null);
        value = props.getProperty("search_filter_value_" + pos, null);
        if ((attribute != null) && (value != null)
                && (!attribute.equals("")))
        {
          if (value.startsWith("in."))
          {
            value = value.substring(3);
          }
          filterAttributesHashtable.put(attribute, value);
        }
        pos++;
      } while ((attribute != null) && (value != null));

      newObjectName = props.getProperty("new_object", "");
      if (newObjectName.startsWith("in."))
      {
        newObjectName = newObjectName.substring(3);
      }

      if ("filterText".equals(props.getProperty("search_filter_format")))
      {
        anyFilterText = props.getProperty("search_filter_text");
      }

      searchControl.setReturningAttributes(null); // return all attributes

      // 31.10.2006 bb: set returningObjFlag to false. If it is true, the
      // dircontext.close() method
      // does not close and the dircontext remains in memory until the GC
      // finally sweeps it out.
      // The number of nativ system threads will grow and grow what can
      // Result in an out of memory exception!
      searchControl.setReturningObjFlag(false);

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
  }

  @Override
  public void stop() throws Exception
  {
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

  public void setVariable(String name, Object value, CompositeObject argument) throws NoSuchFieldException
  {
    if (name.indexOf(".") < 0)
    {
      argument.set(name, value);
    }
    else
    {
      String newArgumentName = name.substring(0, name.indexOf("."));
      String RemainingName = name.substring(name.indexOf(".") + 1, name.length());
      setVariable(RemainingName, value, (CompositeObject) argument.get(newArgumentName));
    }
  }

}
