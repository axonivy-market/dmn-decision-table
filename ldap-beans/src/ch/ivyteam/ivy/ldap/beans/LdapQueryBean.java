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
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
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
import ch.ivyteam.ivy.scripting.objects.List;
import ch.ivyteam.ivy.scripting.objects.Recordset;
import ch.ivyteam.naming.JndiConfig;
import ch.ivyteam.naming.JndiProvider;
import ch.ivyteam.naming.JndiUtil;

/**
 * PI-Element to query LDAP-Servers
 * @author Reto Weiss
 * @version MarcWillaredt August2009 Updated to Ivy 4.1
 * @version bb 22.11.2005 bb: allow ivy attributes ("in.xx") to set url, name,
 *          password
 * @version pk 29.07.2004 pk: renamed from JndiQueryBean to LdapQueryBean,
 *          renamed some fields
 * @version ReW 16.5.2002 created
 */
public class LdapQueryBean extends AbstractUserProcessExtension
{

  /** Jndi server configuration */
  private JndiConfig jndiConfig;

  /** Maps the resulting jndi attribute names to ivyGrid attribute names */
  private Hashtable<String, String> resultAttributesHashtable = new Hashtable<>();

  /** Filter attribute names */
  private Hashtable<String, String> filterAttributesHashtable = new Hashtable<>();

  /** Jndi search control */
  private SearchControls searchControl = new SearchControls();

  /** ivyGrid Attribute to store results in */
  private String ivyGridAttribute = null;

  /** The root object to begin search for */
  private String rootObjectName;

  /** include jndi name to result */
  private boolean includeName;

  /** ivyGrid attribute to store the jndi name in */
  private String ivyGridNameAttribute;

  /** To have an ordered list of the attribut names */
  private Vector<String> resultAttributesKeys = new Vector<>();

  /**
   * Configuration editor for jndi query bean
   * 
   * @author Reto Weiss
   * @version pk 29.07.2004 pk: renamed some fields
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

    /** result table */
    private JTable resultTable;

    /** Return panel */
    private JPanel returnPanel;

    /** card layout of the return panel */
    private CardLayout returnLayout;

    /** ivy grid attribute to store the results in */
    private JTextField tfIvyGridAttribute;

    /** table to configure the jndi attribute to query */
    private JTable resultAttributeTable;

    /** return all results radio button */
    private JRadioButton dbReturnAll;

    /** return only first result redio button */
    private JRadioButton rbReturnOnlyFirst;

    /** the name of the root object to start the search at */
    private JTextField tfRootObjectName;

    /** should jndi name be included in the result */
    private JCheckBox cbIncludeName;

    /** should jndi name be included in the result */
    private JCheckBox rbIncludeName2;

    /** ivy Grid Attribute to store the name in */
    private JTextField tfIvyGridNameAttribute;

    /** Jndi config panel */
    JndiConfigPanel jndiConfigPanel;

    /**
     * Constructor for the Editor object
     */
    public Editor()
    {

      ResourceBundle resBun = ResourceBundle.getBundle("TextResource",
              new Locale("de"));

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
              GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0.0,
              0.0, 10, 10, 10, 10);

      filterTable = new JTable(20, 0);
      ((DefaultTableModel) filterTable.getModel()).addColumn(resBun
              .getString("search_filter_attribute"));
      ((DefaultTableModel) filterTable.getModel()).addColumn(resBun
              .getString("search_filter_value"));
      filterTable.setPreferredScrollableViewportSize(new Dimension(300,
              200));
      JScrollPane scrollPane = new JScrollPane(filterTable);
      AWTUtil.constrain(searchPanel, scrollPane, 1, 2, 3, 1,
              GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0,
              10, 10, 10, 10);

      tabbedPanel
              .add(resBun.getString("search_filter_pane"), searchPanel);

      // Jndi Result panel
      JPanel resultPanel = new JPanel(new GridBagLayout());

      label = new JLabel(resBun.getString("search_result_return"));
      AWTUtil.constrain(resultPanel, label, 0, 0, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              10, 10, 0, 10);

      ButtonGroup resultButtons = new ButtonGroup();
      rbReturnOnlyFirst = new JRadioButton(resBun
              .getString("search_result_return_only_first_result"));
      AWTUtil.constrain(resultPanel, rbReturnOnlyFirst, 1, 0, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0.0,
              0.0, 10, 10, 0, 10);

      dbReturnAll = new JRadioButton(resBun
              .getString("search_result_return_all_results"));
      AWTUtil.constrain(resultPanel, dbReturnAll, 1, 1, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0.0,
              0.0, 0, 10, 0, 10);

      resultButtons.add(rbReturnOnlyFirst);
      resultButtons.add(dbReturnAll);

      label = new JLabel(resBun.getString("search_result"));
      AWTUtil.constrain(resultPanel, label, 0, 2, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0.0,
              0.0, 10, 10, 0, 10);

      returnLayout = new CardLayout();
      returnPanel = new JPanel(returnLayout);

      // result for only the first result
      JPanel onlyFirstResultPane = new JPanel(new GridBagLayout());

      cbIncludeName = new JCheckBox(resBun
              .getString("search_result_include_name"));
      AWTUtil.constrain(onlyFirstResultPane, cbIncludeName, 0, 0, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              0, 0, 0, 0);

      tfIvyGridNameAttribute = new JTextField(20);
      AWTUtil.constrain(onlyFirstResultPane, tfIvyGridNameAttribute, 1,
              0, 1, 1, GridBagConstraints.HORIZONTAL,
              GridBagConstraints.WEST, 1.0, 0.0, 0, 10, 0, 0);

      resultTable = new JTable(20, 0);
      ((DefaultTableModel) resultTable.getModel()).addColumn(resBun
              .getString("search_result_attribute"));
      ((DefaultTableModel) resultTable.getModel()).addColumn(resBun
              .getString("search_result_value"));
      resultTable.setPreferredScrollableViewportSize(new Dimension(300,
              200));
      scrollPane = new JScrollPane(resultTable);

      AWTUtil.constrain(onlyFirstResultPane, scrollPane, 0, 1, 2, 1,
              GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0,
              10, 0, 0, 0);

      returnPanel.add("onlyFirst", onlyFirstResultPane);

      // result for all results
      JPanel allResultPanel = new JPanel(new GridBagLayout());
      label = new JLabel(resBun.getString("search_result_value"));

      AWTUtil.constrain(allResultPanel, label, 0, 0, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              0, 0, 0, 10);

      tfIvyGridAttribute = new JTextField(30);
      AWTUtil.constrain(allResultPanel, tfIvyGridAttribute, 1, 0, 1, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              0, 0, 0, 0);

      rbIncludeName2 = new JCheckBox(resBun
              .getString("search_result_include_name"));
      AWTUtil.constrain(allResultPanel, rbIncludeName2, 0, 1, 2, 1,
              GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
              10, 0, 0, 0);

      resultAttributeTable = new JTable(20, 0);
      ((DefaultTableModel) resultAttributeTable.getModel())
              .addColumn(resBun.getString("search_result_attribute"));
      resultAttributeTable
              .setPreferredScrollableViewportSize(new Dimension(300, 200));
      scrollPane = new JScrollPane(resultAttributeTable);
      AWTUtil.constrain(allResultPanel, scrollPane, 0, 2, 2, 1,
              GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0,
              10, 0, 0, 0);
      returnPanel.add("all", allResultPanel);

      AWTUtil.constrain(resultPanel, returnPanel, 1, 2, 2, 1,
              GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0,
              10, 10, 10, 10);

      tabbedPanel
              .add(resBun.getString("search_result_pane"), resultPanel);

      dbReturnAll.addActionListener(new AllActionListener());
      rbReturnOnlyFirst.addActionListener(new OnlyFirstActionListener());
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
      cellEditor = resultTable.getCellEditor();
      if (cellEditor != null)
      {
        cellEditor.stopCellEditing();
      }
      cellEditor = resultAttributeTable.getCellEditor();
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

      model = resultTable.getModel();
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
          props.setProperty("result_table_attribute_" + pos,
                  attribute.trim());
          props
                  .setProperty("result_table_value_" + pos, value
                          .trim());
        }
        else
        {
          break;
        }
      }

      model = resultAttributeTable.getModel();
      for (int pos = 0; pos < model.getRowCount(); pos++)
      {
        attribute = (String) model.getValueAt(pos, 0);
        if (attribute != null)
        {
          props.setProperty("result_attribute_attribute_" + pos,
                  attribute.trim());
        }
        else
        {
          break;
        }
      }

      props.setProperty("result_ivyGrid_attribute", tfIvyGridAttribute
              .getText().trim());
      if (dbReturnAll.isSelected())
      {
        props.setProperty("result_return", "all");
      }
      else
      {
        props.setProperty("result_return", "onylFirst");
      }

      props.setProperty("result_include_name", new Boolean(cbIncludeName
              .isSelected()).toString());
      props.setProperty("result_include_name2", new Boolean(
              rbIncludeName2.isSelected()).toString());
      props.setProperty("result_ivyGrid_name_attribute",
              tfIvyGridNameAttribute.getText().trim());

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

        pos = 0;
        model = new DefaultTableModel(resultTable.getModel()
                .getRowCount(), resultTable.getModel().getColumnCount());
        cols = new Vector<>();
        for (int i = 0; i < resultTable.getColumnCount(); i++)
        {
          cols.add(resultTable.getColumnName(i));
        }
        model.setColumnIdentifiers(cols);
        do
        {
          attribute = props.getProperty("result_table_attribute_"
                  + pos, null);
          value = props
                  .getProperty("result_table_value_" + pos, null);
          if ((attribute != null) && (value != null))
          {
            model.insertRow(pos, new Object[] {attribute, value});
            pos++;
          }
        } while ((attribute != null) && (value != null));
        resultTable.setModel(model);

        pos = 0;
        model = new DefaultTableModel(resultAttributeTable.getModel()
                .getRowCount(), resultAttributeTable.getModel()
                .getColumnCount());
        cols = new Vector<>();
        for (int i = 0; i < resultAttributeTable.getColumnCount(); i++)
        {
          cols.add(resultAttributeTable.getColumnName(i));
        }
        model.setColumnIdentifiers(cols);
        do
        {
          attribute = props.getProperty("result_attribute_attribute_"
                  + pos, null);
          if ((attribute != null))
          {
            model.insertRow(pos, new Object[] {attribute});
            pos++;
          }
        } while (attribute != null);
        resultAttributeTable.setModel(model);

        tfIvyGridAttribute.setText(props.getProperty(
                "result_ivyGrid_attribute", ""));
        if ("all".equals(props.getProperty("result_return")))
        {
          dbReturnAll.setSelected(true);
          returnLayout.show(returnPanel, "all");
        }
        else
        {
          rbReturnOnlyFirst.setSelected(true);
          returnLayout.show(returnPanel, "onlyFirst");
        }

        cbIncludeName.setSelected(new Boolean(props.getProperty(
                "result_include_name", Boolean.FALSE.toString()))
                .booleanValue());
        rbIncludeName2.setSelected(new Boolean(props.getProperty(
                "result_include_name2", Boolean.FALSE.toString()))
                .booleanValue());
        tfIvyGridNameAttribute.setText(props.getProperty(
                "result_ivyGrid_name_attribute", ""));
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

    class AllActionListener implements ActionListener
    {
      /**
       * Invoked when an action occurs.
       * 
       * @param e
       */
      @Override
      public void actionPerformed(ActionEvent e)
      {
        returnLayout.show(returnPanel, "all");
      }
    }

    class OnlyFirstActionListener implements ActionListener
    {
      /**
       * Invoked when an action occurs.
       * 
       * @param e
       */
      @Override
      public void actionPerformed(ActionEvent e)
      {
        returnLayout.show(returnPanel, "onlyFirst");
      }
    }

  }

  /**
   * Constructor
   */
  public LdapQueryBean()
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
    NamingEnumeration<SearchResult> resultEnum = null;
    DirContext dirContext = null;
    Enumeration<String> attrEnum;
    String attribute, value;
    String filter = "";
    String objectName;
    SearchResult searchResult;
    Vector<Vector<Object>> result = null;
    Vector<Object> row = null;

    // Build search filter
    attrEnum = filterAttributesHashtable.keys();
    while (attrEnum.hasMoreElements())
    {
      String oldAttribute = attrEnum.nextElement();
      String newAttribute = (String) getVariable(oldAttribute, cont);
      value = filterAttributesHashtable.get(oldAttribute);
      value = (String) getVariable(value, cont);
      value = value.trim();
      // if the filter value starts and ends with " I assume that this is
      // a constant string
      if (value.startsWith("\"") && value.endsWith("\""))
      {
        value = value.substring(1, value.length() - 1);
      }
      if (filter.length() == 0)
      {
        filter += "(&";
      }
      filter += "(";
      filter += newAttribute + "=" + value;
      filter += ")";
    }

    if (filter.length() > 0)
    {
      filter += ")";
    }

    if (rootObjectName.trim().startsWith("\"")
            && (rootObjectName.trim().endsWith("\"")))
    {
      objectName = rootObjectName.substring(1,
              rootObjectName.length() - 1);
    }
    else
    {
      objectName = (String) getVariable(rootObjectName, cont);
      if (objectName == null)
      {
        objectName = rootObjectName;
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

    try
    {
      // query the naming and directory service
      // dirContext = new
      // InitialDirContext(expandedJndiConfig.getEnvironement()); // this only
      // works in Xivy version < 4.3.15
      dirContext = JndiUtil.openDirContext(expandedJndiConfig);

      resultEnum = dirContext.search(objectName, filter, searchControl);

      if (ivyGridAttribute != null)
      {
        result = new Vector<>();
      }

      // read the result and assign them to the ivyGrid arguments
      if (!resultEnum.hasMoreElements())
      {
        // no result found! --> set output to null
        if (ivyGridAttribute == null)
        {
          /*
           * enumeration = resultAttributesHashtable.keys(); while
           * (enumeration.hasMoreElements()) { attribute =
           * (String)enumeration.nextElement(); argument.set(attribute, null); }
           * if (includeName) { argument.set(ivyGridNameAttribute, null); }
           */
        }
        else
        {
          setVariable(ivyGridAttribute, null, argument, cont);
        }
        // must return to prevent nullpointer exception
        return argument;
      }
      else
      {
        List<String> tableKeys = List.create(String.class);
        attrEnum = resultAttributesKeys.elements();
        while (attrEnum.hasMoreElements())
        {
          attribute = attrEnum.nextElement();
          tableKeys.add(attribute);
        }

        boolean onlyOneDone = false;
        while (resultEnum.hasMoreElements() && !onlyOneDone)
        {
          searchResult = resultEnum.nextElement();
          onlyOneDone = handleResult(searchResult, result, row, objectName, argument, cont);
        }
        Recordset resultRS = toRecordset(result, tableKeys);
        if (ivyGridAttribute != null)
        {
          setVariable(ivyGridAttribute, resultRS, argument, cont);
        }
      }
    }
    finally
    {
      if (resultEnum != null)
      {
        try
        {
          resultEnum.close();
        }
        catch (NamingException ex)
        {
        }
      }
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

  private boolean handleResult(SearchResult searchResult, Vector<Vector<Object>> result, Vector<Object> row,
          String objectName, CompositeObject argument, IIvyScriptContext cont) throws PersistencyException,
          NoSuchFieldException, IvyScriptException, NamingException
  {
    Attributes jndiAttributes;
    Attribute jndiAttribute;
    if (searchResult != null)
    {
      if (ivyGridAttribute != null)
      {
        row = new Vector<>();
        result.add(row);
      }

      // include jndi name in result if it is selected
      if (includeName)
      {
        if (ivyGridAttribute != null)
        {
          if (rootObjectName.trim().equals(""))
          {
            row.add(searchResult.getName());
          }
          else
          {
            row.add(searchResult.getName() + "," + objectName);
          }
        }
        else
        {
          if (rootObjectName.trim().equals(""))
          {
            setVariable(ivyGridNameAttribute, searchResult.getName(), argument, cont);
          }
          else
          {
            setVariable(ivyGridNameAttribute, searchResult.getName() + "," + objectName, argument, cont);
          }
        }
      }
      jndiAttributes = searchResult.getAttributes();
      Enumeration<String> attrEnum = resultAttributesHashtable.keys();

      while (attrEnum.hasMoreElements())
      {
        String attribute = attrEnum.nextElement();
        if (jndiAttributes != null)
        {
          jndiAttribute = jndiAttributes.get(attribute);
        }
        else
        {
          jndiAttribute = null;
        }
        if (jndiAttribute != null)
        {
          if (jndiAttribute.size() == 1)
          {
            if (ivyGridAttribute != null)
            {
              row.add(jndiAttribute.get());
            }
            else
            {
              // argument.set((String) resultAttributesHashtable.get(attribute),
              // jndiAttribute.get());
              setVariable(resultAttributesHashtable.get(attribute).toString(), jndiAttribute.get(), argument,
                      cont);
              return true;
            }
          }
          else if (jndiAttribute.size() > 1)
          {
            if (ivyGridAttribute != null)
            {
              row.add(jndiAttribute.getAll());
            }
            else
            {
              // argument.set((String) resultAttributesHashtable.get(attribute),
              // jndiAttribute.getAll());
              setVariable(resultAttributesHashtable.get(attribute).toString(), jndiAttribute.getAll(),
                      argument, cont);
            }
          }
        }
      }
    }
    return false;
  }

  private Recordset toRecordset(Vector<Vector<Object>> result, List<String> tableKeys)
  {
    List<String> tmpList = List.create(String.class);
    if (includeName)
    {
      tmpList.add("JNDIName");
    }
    for (int i = tableKeys.size() - 1; i >= 0; i--)
    {
      tmpList.add(tableKeys.get(i));
    }
    Recordset resultRS = new Recordset(tmpList);
    result = result != null ? result : new Vector<>();
    for (int i = 0; i < result.size(); i++)
    {
      Vector<?> tmpRow = result.get(i);
      List<Object> valueList = List.create();
      for (Object valueObject : tmpRow)
      {
        valueList.add(valueObject);
      }
      resultRS.add(valueList);
    }
    return resultRS;
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

      if ("all".equals(props.getProperty("result_return")))
      {
        ivyGridAttribute = props.getProperty(
                "result_ivyGrid_attribute", "");
        if (ivyGridAttribute.startsWith("in."))
        {
          ivyGridAttribute = ivyGridAttribute.substring(3);
        }
        pos = 0;
        do
        {
          attribute = props.getProperty("result_attribute_attribute_"
                  + pos, null);
          if ((attribute != null) && (!attribute.equals("")))
          {
            if (attribute.startsWith("in."))
            {
              attribute = attribute.substring(3);
            }
            resultAttributesHashtable.put(attribute, "");
            resultAttributesKeys.add(attribute);
          }
          pos++;
        } while (attribute != null);
      }
      else
      {
        pos = 0;
        do
        {
          attribute = props.getProperty("result_table_attribute_"
                  + pos, null);
          value = props
                  .getProperty("result_table_value_" + pos, null);
          if ((attribute != null) && (value != null)
                  && (!attribute.equals("")))
          {
            if (value.startsWith("in."))
            {
              value = value.substring(3);
            }
            resultAttributesHashtable.put(attribute, value);
            resultAttributesKeys.add(attribute);
          }
          pos++;
        } while ((attribute != null) && (value != null));
      }
      if (ivyGridAttribute == null)
      {
        includeName = new Boolean(props.getProperty(
                "result_include_name", Boolean.FALSE.toString()))
                .booleanValue();
      }
      else
      {
        includeName = new Boolean(props.getProperty(
                "result_include_name2", Boolean.FALSE.toString()))
                .booleanValue();
      }
      ivyGridNameAttribute = props.getProperty(
              "result_ivyGrid_name_attribute", "");
      if (ivyGridNameAttribute.startsWith("in."))
      {
        ivyGridNameAttribute = ivyGridNameAttribute.substring(3);
      }

      searchControl
              .setReturningAttributes(resultAttributesHashtable
                      .keySet().toArray(new String[0]));

      // 31.10.2006 bb: set returningObjFlag to false. If it is true, the
      // dircontext.close() method
      // does not close and the dircontext remains in memory until the GC
      // finally sweeps it out.
      // The number of nativ system threads will grow and grow what can
      // result in an out of memory exception! // The number of nativ
      // system threads will grow and grow what can result in an out of
      // memory exception!
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

  public void setVariable(String name, Object value, CompositeObject argument, IIvyScriptContext cont)
          throws NoSuchFieldException, IvyScriptException, PersistencyException
  {
    try
    {
      if (name.startsWith("in."))
      {
        name = name.substring("in.".length());
      }
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
    catch (NullPointerException e)
    {
      ivyError(
              "Ein Attribut ist entweder nicht vorhanden oder nicht initialisiert. Bitte dieses erstellen bzw. initialisieren. Das betroffene Attribut heisst: "
                      + name + ".\n\nFreundliche Grüsse\nIhre LdapQueryBean", cont);
    }
  }

  public void ivyPrint(String msg, IIvyScriptContext cont) throws IvyScriptException, PersistencyException
  {
    String tmpStr = new String();
    for (char c : msg.toCharArray())
    {
      if (c != "\"".toCharArray()[0])
        tmpStr += c;
    }
    this.executeIvyScript(cont, "ivy.log.info(\"" + tmpStr + "\")");
  }

  public void ivyError(String msg, IIvyScriptContext cont) throws IvyScriptException, PersistencyException
  {
    String tmpStr = new String();
    for (char c : msg.toCharArray())
    {
      if (c != "\"".toCharArray()[0])
        tmpStr += c;
    }
    this.executeIvyScript(cont, "ivy.log.error(\"" + tmpStr + "\")");
  }

}
