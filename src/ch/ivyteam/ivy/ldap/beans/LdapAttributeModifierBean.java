package ch.ivyteam.ivy.ldap.beans;



import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.swing.ButtonGroup;
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
import ch.ivyteam.naming.JndiConfig;
import ch.ivyteam.naming.JndiProvider;
import ch.ivyteam.naming.JndiUtil;

/**
 * Bean to modify jndi attributes of a jndi object
 * 
 * @author Reto Weiss
 * @version MarcWillaredt August2009 Updated to Ivy 4.1
 * @version bb 22.11.2005 bb: allow ivy attributes ("in.xx") to set url, name,
 *          password
 * @version pk 29.07.2004 pk: renamed from JndiAttributeModifierBean to
 *          LdapAttributeModifierBean, renamed some fields
 * @version ReW 21.5.2002 created
 */
public class LdapAttributeModifierBean extends AbstractUserProcessExtension {

	/** Jndi Configuration */
	private JndiConfig jndiConfig;

	/** object name */
	private String objectName;

	/** attributes and values */
	private Hashtable attributes = new Hashtable();

	/** operation code */
	private int operationCode;

	/**
	 * Configuration editor for the jndi attribute modifier bean
	 * 
	 * @author Reto Weiss
	 * @version pk 29.07.2004 pk: renamed some fields
	 * @version ReW 21.5.2002 created
	 */
	@SuppressWarnings("serial")
	public static class Editor extends AbstractProcessExtensionConfigurationEditor {

		/**
		 * The configuration panel
		 */
		private JTabbedPane tabbedPanel;

		/** Jndi server configuration */
		private JndiConfig jndiConfig;

		/** modify operation add */
		private JRadioButton rbOperationAdd;

		/** modify operation remove */
		private JRadioButton rbOperationRemove;

		/** modify operation replace */
		private JRadioButton rbOperationReplace;

		/** table with the attributes to add */
		private JTable addAttributeTable;

		/** table with the attributes to replace */
		private JTable replaceAttributeTable;

		/** table with the attributes to remove */
		private JTable removeAttributeTable;

		/** the name of the object to modify */
		private JTextField tfObjectName;

		/** Jndi config panel */
		JndiConfigPanel jndiConfigPanel;

		/** attribute panel */
		private JPanel attributePanel;

		/** card layout of the attribute panel */
		private CardLayout attributeLayout;

		/**
		 * Constructor for the Editor object
		 */
		public Editor() {

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

			// Jndi Modify Attribute panel
			JPanel modifyAttributePanel = new JPanel(new GridBagLayout());

			// root object
			JLabel label = new JLabel(resBun.getString("mod_attr_object_name"));
			AWTUtil.constrain(modifyAttributePanel, label, 0, 0, 1, 1,
					GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
					10, 10, 0, 10);

			tfObjectName = new JTextField(40);
			AWTUtil.constrain(modifyAttributePanel, tfObjectName, 1, 0, 3, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1.0, 0.0, 10, 10, 0, 10);

			// Modify operation
			label = new JLabel(resBun.getString("mod_attr_operation"));
			AWTUtil.constrain(modifyAttributePanel, label, 0, 1, 1, 1,
					GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
					10, 10, 0, 10);

			ButtonGroup operationButtons = new ButtonGroup();
			rbOperationAdd = new JRadioButton(resBun
					.getString("mod_attr_operation_add"));
			rbOperationAdd.setSelected(true);
			AWTUtil.constrain(modifyAttributePanel, rbOperationAdd, 1, 1, 1, 1,
					GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
					10, 10, 0, 10);

			rbOperationRemove = new JRadioButton(resBun
					.getString("mod_attr_operation_remove"));
			AWTUtil.constrain(modifyAttributePanel, rbOperationRemove, 2, 1, 1,
					1, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0,
					0.0, 10, 10, 0, 10);

			rbOperationReplace = new JRadioButton(resBun
					.getString("mod_attr_operation_replace"));
			AWTUtil.constrain(modifyAttributePanel, rbOperationReplace, 3, 1,
					1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
					0.0, 0.0, 10, 10, 0, 10);

			operationButtons.add(rbOperationAdd);
			operationButtons.add(rbOperationRemove);
			operationButtons.add(rbOperationReplace);

			label = new JLabel(resBun.getString("mod_attr_attributes"));
			AWTUtil.constrain(modifyAttributePanel, label, 0, 2, 1, 1,
					GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0.0,
					0.0, 10, 10, 10, 10);

			attributePanel = new JPanel();
			attributeLayout = new CardLayout();
			attributePanel.setLayout(attributeLayout);

			// add table
			addAttributeTable = new JTable(20, 0);
			((DefaultTableModel) addAttributeTable.getModel()).addColumn(resBun
					.getString("mod_attr_jndi_attribute"));
			((DefaultTableModel) addAttributeTable.getModel()).addColumn(resBun
					.getString("mod_attr_new_value"));
			addAttributeTable.setPreferredScrollableViewportSize(new Dimension(
					300, 200));
			JScrollPane scrollPane = new JScrollPane(addAttributeTable);
			attributePanel.add("add", scrollPane);

			// remove table
			removeAttributeTable = new JTable(20, 0);
			((DefaultTableModel) removeAttributeTable.getModel())
					.addColumn(resBun.getString("mod_attr_jndi_attribute"));
			removeAttributeTable
					.setPreferredScrollableViewportSize(new Dimension(300, 200));
			scrollPane = new JScrollPane(removeAttributeTable);
			attributePanel.add("remove", scrollPane);

			// replace table
			replaceAttributeTable = new JTable(20, 0);
			((DefaultTableModel) replaceAttributeTable.getModel())
					.addColumn(resBun.getString("mod_attr_jndi_attribute"));
			((DefaultTableModel) replaceAttributeTable.getModel())
					.addColumn(resBun.getString("mod_attr_new_value"));
			replaceAttributeTable
					.setPreferredScrollableViewportSize(new Dimension(300, 200));
			scrollPane = new JScrollPane(replaceAttributeTable);
			attributePanel.add("replace", scrollPane);

			AWTUtil.constrain(modifyAttributePanel, attributePanel, 1, 2, 3, 1,
					GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST, 1.0,
					1.0, 10, 10, 10, 10);

			tabbedPanel.add(resBun.getString("mod_attr_modify_pane"),
					modifyAttributePanel);

			rbOperationAdd.addActionListener(new AddActionListener());
			rbOperationRemove.addActionListener(new RemoveActionListener());
			rbOperationReplace.addActionListener(new ReplaceActionListener());
		}

		/**
		 * This method is called if the user pressed the OK button of the dialog
		 * containing this editor. The dialog is closed and the new
		 * configuration is used only if this methode return true
		 * 
		 * @return
		 */
		public boolean acceptInput() {
			return true;
		}

		public Component getComponent() {
			return tabbedPanel;
		}

		/**
		 * @return a description of a configuration. This description will be
		 *         sent to the corresponding OuterProcessBean
		 */
		public String getConfiguration() {
			String attribute, value;
			ByteArrayOutputStream baos = null;
			Properties props = new Properties();

			TableCellEditor cellEditor;
			cellEditor = addAttributeTable.getCellEditor();
			if (cellEditor != null) {
				cellEditor.stopCellEditing();
			}
			cellEditor = removeAttributeTable.getCellEditor();
			if (cellEditor != null) {
				cellEditor.stopCellEditing();
			}
			;
			cellEditor = replaceAttributeTable.getCellEditor();
			if (cellEditor != null) {
				cellEditor.stopCellEditing();
			}
			;
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
			props.setProperty("server_context", jndiConfig.getDefaultContext());

			props.setProperty("mod_attr_object_name", tfObjectName.getText()
					.trim());

			if (rbOperationAdd.isSelected()) {
				props.setProperty("mod_attr_operation", "add");
			} else if (rbOperationRemove.isSelected()) {
				props.setProperty("mod_attr_operation", "remove");
			} else if (rbOperationReplace.isSelected()) {
				props.setProperty("mod_attr_operation", "replace");
			}

			TableModel model = addAttributeTable.getModel();
			for (int pos = 0; pos < model.getRowCount(); pos++) {
				attribute = (String) model.getValueAt(pos, 0);
				value = (String) model.getValueAt(pos, 1);
				if ((attribute != null) && (value == null)) {
					value = "";
				} else if ((attribute == null) && (value != null)) {
					attribute = "";
				}
				if ((attribute != null) && (value != null)) {
					props.setProperty("mod_attr_add_attribute_" + pos,
							attribute.trim());
					props
							.setProperty("mod_attr_add_value_" + pos, value
									.trim());
				} else {
					break;
				}
			}

			model = replaceAttributeTable.getModel();
			for (int pos = 0; pos < model.getRowCount(); pos++) {
				attribute = (String) model.getValueAt(pos, 0);
				value = (String) model.getValueAt(pos, 1);
				if ((attribute != null) && (value == null)) {
					value = "";
				} else if ((attribute == null) && (value != null)) {
					attribute = "";
				}
				if ((attribute != null) && (value != null)) {
					props.setProperty("mod_attr_replace_attribute_" + pos,
							attribute.trim());
					props.setProperty("mod_attr_replace_value_" + pos, value
							.trim());
				} else {
					break;
				}
			}

			model = removeAttributeTable.getModel();
			for (int pos = 0; pos < model.getRowCount(); pos++) {
				attribute = (String) model.getValueAt(pos, 0);
				if (attribute != null) {
					props.setProperty("mod_attr_remove_attribute_" + pos,
							attribute.trim());
				} else {
					break;
				}
			}

			try {
				baos = new ByteArrayOutputStream();
				props.store(baos, "");
			} catch (IOException ex) {
				return "";
			} finally {
				if (baos != null) {
					try {
						baos.close();
					} catch (IOException ex) {
					}
					;
				}
			}
			return new String(baos.toByteArray());
		}

		/**
		 * Initialize the editor with an older configuration
		 * 
		 * @param configString
		 *            The new configuration value
		 */
		public void setConfiguration(String configString) {
			ByteArrayInputStream bais = null;
			Properties props = new Properties();
			String attribute, value;
			DefaultTableModel model;
			int pos;

			if (configString == null) {
				return;
			}

			try {
				bais = new ByteArrayInputStream(configString.getBytes());
				props.load(bais);

				if (props.get("server_provider") != null) {
					for (pos = 0; pos < JndiProvider.PROVIDERS.length; pos++) {
						if (JndiProvider.PROVIDERS[pos].getProviderName()
								.equals(props.get("server_provider"))) {
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

				tfObjectName.setText(props.getProperty("mod_attr_object_name",
						""));

				if ("add".equals(props.getProperty("mod_attr_operation", ""))) {
					rbOperationAdd.setSelected(true);
					attributeLayout.show(attributePanel, "add");
				} else if ("remove".equals(props.getProperty(
						"mod_attr_operation", ""))) {
					rbOperationRemove.setSelected(true);
					attributeLayout.show(attributePanel, "remove");
				} else {
					rbOperationReplace.setSelected(true);
					attributeLayout.show(attributePanel, "replace");
				}

				pos = 0;
				model = new DefaultTableModel(addAttributeTable.getModel().getRowCount(), addAttributeTable.getModel().getColumnCount());
				Vector cols = new Vector();
				for (int i = 0; i < addAttributeTable.getColumnCount(); i++) {
					cols.add(addAttributeTable.getColumnName(i));
				}
				model.setColumnIdentifiers(cols);
				do {
					attribute = props.getProperty("mod_attr_add_attribute_"
							+ pos, null);
					value = props
							.getProperty("mod_attr_add_value_" + pos, null);
					if ((attribute != null) && (value != null)) {
						model.insertRow(pos, new Object[] { attribute, value });
						pos++;
					}
				} while ((attribute != null) && (value != null));
				addAttributeTable.setModel(model);

				pos = 0;
				model = new DefaultTableModel(removeAttributeTable.getModel().getRowCount(), removeAttributeTable.getModel().getColumnCount());
				cols = new Vector();
				for (int i = 0; i < removeAttributeTable.getColumnCount(); i++) {
					cols.add(removeAttributeTable.getColumnName(i));
				}
				model.setColumnIdentifiers(cols);
				do {
					attribute = props.getProperty("mod_attr_remove_attribute_"
							+ pos, null);
					if (attribute != null) {
						model.insertRow(pos, new Object[] { attribute });
						pos++;
					}
				} while (attribute != null);
				removeAttributeTable.setModel(model);

				pos = 0;
				model = new DefaultTableModel(replaceAttributeTable.getModel().getRowCount(), replaceAttributeTable.getModel().getColumnCount());
				cols = new Vector();
				for (int i = 0; i < replaceAttributeTable.getColumnCount(); i++) {
					cols.add(replaceAttributeTable.getColumnName(i));
				}
				model.setColumnIdentifiers(cols);
				do {
					attribute = props.getProperty("mod_attr_replace_attribute_"
							+ pos, null);
					value = props.getProperty("mod_attr_replace_value_" + pos,
							null);
					if ((attribute != null) && (value != null)) {
						model.insertRow(pos, new Object[] { attribute, value });
						pos++;
					}
				} while ((attribute != null) && (value != null));
				replaceAttributeTable.setModel(model);
			} catch (IOException ex) {
			} finally {
				if (bais != null) {
					try {
						bais.close();
					} catch (IOException ex) {
					}
					;
				}
			}
		}

		class AddActionListener implements ActionListener {
			/**
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				attributeLayout.show(attributePanel, "add");
			}
		}

		class RemoveActionListener implements ActionListener {
			/**
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				attributeLayout.show(attributePanel, "remove");
			}
		}

		class ReplaceActionListener implements ActionListener {
			/**
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				attributeLayout.show(attributePanel, "replace");
			}
		}
	}

	/**
	 * Constructor
	 */
	public LdapAttributeModifierBean() {
		jndiConfig = new JndiConfig(JndiProvider.NOVELL_E_DIRECTORY, "ldap://",
				JndiConfig.AUTH_KIND_SIMPLE, "", "", false, false, "");
	}

	/**
	 * This method is the program performed in a program interface element every
	 * time a token comes to the element.
	 * 
	 * @param argument
	 *            a wrapper for the token value.
	 * @throws Exception
	 */
	public CompositeObject perform(IRequestId reqID, CompositeObject argument,
			IIvyScriptContext cont) throws Exception {
		Enumeration enumeration;
		DirContext dirContext;
		String attribute;
		String valueStr;
		Object value;
		String modifyObjectName;
		BasicAttributes jndiAttributes = new BasicAttributes();
		BasicAttribute jndiAttribute;

		// Resolve the values
		enumeration = attributes.keys();
		while (enumeration.hasMoreElements()) {
			attribute = (String) enumeration.nextElement();
			String newAttribute = (String) getVariable(attribute, cont);
			value = null;
			if (operationCode != DirContext.REMOVE_ATTRIBUTE) {
				valueStr = (String) attributes.get(attribute);
				valueStr = (String) getVariable(valueStr, cont);
				valueStr = valueStr.trim();
				value = valueStr;
				if (value != null) {
					jndiAttribute = new BasicAttribute(newAttribute);
					if (value instanceof Vector) {
						for (int pos = 0; pos < ((Vector) value).size(); pos++) {
							jndiAttribute.add(((Vector) value).elementAt(pos));
						}
					} else {
						jndiAttribute.add(value);
					}
					jndiAttributes.put(jndiAttribute);
				}
			} else // DirContext.REMOVE_ATTRIBUTE
			{
				jndiAttribute = new BasicAttribute(newAttribute);
				jndiAttributes.put(jndiAttribute);
			}
		}

		if (objectName.trim().startsWith("\"")
				&& (objectName.trim().endsWith("\""))) {
			modifyObjectName = objectName.substring(1, objectName.length() - 1);
		} else {
			modifyObjectName = (String) getVariable(objectName, cont);
			if (modifyObjectName == null) {
				modifyObjectName = objectName;
			}
		}

		JndiConfig expandedJndiConfig = (JndiConfig) jndiConfig.clone();
		// try to expand url, name and password fields
		String propStr = expandedJndiConfig.getUrl();
		if (propStr != null) {
			if (getVariable(propStr, cont) != null) {
				propStr = (String) getVariable(propStr, cont);
				expandedJndiConfig.setUrl(propStr);
			}
		}
		propStr = expandedJndiConfig.getUserName();
		if (propStr != null) {
			if (getVariable(propStr, cont) != null) {
				propStr = (String) getVariable(propStr, cont);
				expandedJndiConfig.setUserName(propStr);
			}
		}
		propStr = expandedJndiConfig.getPassword();
		if (propStr != null) {
			if (getVariable(propStr, cont) != null) {
				propStr = (String) getVariable(propStr, cont);
				expandedJndiConfig.setPassword(propStr);
			}
		}

		// query the naming and directory service
		// dirContext = new InitialDirContext(expandedJndiConfig.getEnvironement());  // this only works in Xivy version < 4.3.15
		dirContext = JndiUtil.openDirContext(expandedJndiConfig);
		try {
			dirContext.modifyAttributes(modifyObjectName, operationCode,
					jndiAttributes);
		} finally {
			if (dirContext != null) {
				try {
					dirContext.close();
				} catch (NamingException ex) {
				}
			}
		}

		return argument;
	}

	public void abort(IRequestId arg0) {
	}

	public String getAdditionalLogInfo(IRequestId arg0) {
		return null;
	}

	public void release() throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets a configuration string. This configuration string is usually
	 * produced by the configuration editor of the element.
	 * 
	 * @param configuration
	 */
	public void setConfiguration(String configuration) {
		ByteArrayInputStream bais = null;
		Properties props = new Properties();
		String attribute, value;
		int pos;

		if (configuration == null) {
			return;
		}

		try {
			bais = new ByteArrayInputStream(configuration.getBytes());
			props.load(bais);

			if (props.get("server_provider") != null) {
				for (pos = 0; pos < JndiProvider.PROVIDERS.length; pos++) {
					if (JndiProvider.PROVIDERS[pos].getProviderName().equals(
							props.get("server_provider"))) {
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

			objectName = props.getProperty("mod_attr_object_name", "");
			if (objectName.startsWith("in.")) {
				objectName = objectName.substring(3);
			}

			if ("add".equals(props.getProperty("mod_attr_operation", ""))) {
				operationCode = DirContext.ADD_ATTRIBUTE;
				pos = 0;
				do {
					attribute = props.getProperty("mod_attr_add_attribute_"
							+ pos, null);
					value = props
							.getProperty("mod_attr_add_value_" + pos, null);
					if ((attribute != null) && (value != null)
							&& (!attribute.equals(""))) {
						if (value.startsWith("in.")) {
							value = value.substring(3);
						}
						attributes.put(attribute, value);
					}
					pos++;
				} while ((attribute != null) && (value != null));
			} else if ("remove".equals(props.getProperty("mod_attr_operation",
					""))) {
				operationCode = DirContext.REMOVE_ATTRIBUTE;
				pos = 0;
				do {
					attribute = props.getProperty("mod_attr_remove_attribute_"
							+ pos, null);
					if ((attribute != null) && (!attribute.equals(""))) {
						attributes.put(attribute, "");
					}
					pos++;
				} while (attribute != null);
			} else {
				operationCode = DirContext.REPLACE_ATTRIBUTE;
				pos = 0;
				do {
					attribute = props.getProperty("mod_attr_replace_attribute_"
							+ pos, null);
					value = props.getProperty("mod_attr_replace_value_" + pos,
							null);
					if ((attribute != null) && (value != null)
							&& (!attribute.equals(""))) {
						if (value.startsWith("in.")) {
							value = value.substring(3);
						}
						attributes.put(attribute, value);
					}
					pos++;
				} while ((attribute != null) && (value != null));
			}
		} catch (IOException ex) {
		} finally {
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException ex) {
				}
				;
			}
		}

	}

	public void start() throws Exception {
		// TODO Auto-generated method stub

	}

	public void stop() throws Exception {
		// TODO Auto-generated method stub

	}
	
	public Object getVariable(String name, IIvyScriptContext cont) {
		try {
			if (name.contains("in."))
				return this.executeIvyScript(cont, name);
			else
				return this.executeIvyScript(cont, "in." + name);
		} catch (IvyScriptException e) {
			return name;
		} catch (PersistencyException e) {
			return name;
		}
	}

	public void setVariable(String name, Object value,
			CompositeObject argument, IIvyScriptContext cont)
			throws NoSuchFieldException, IvyScriptException,
			PersistencyException {
		if (name.indexOf(".") < 0) {
			argument.set(name, value);
		} else {
			String newArgumentName = name.substring(0, name.indexOf("."));
			String RemainingName = name.substring(name.indexOf(".") + 1, name
					.length());
			setVariable(RemainingName, value, (CompositeObject) argument
					.get(newArgumentName), cont);
		}
	}
	
	public void ivyPrint(String msg, IIvyScriptContext cont) throws IvyScriptException, PersistencyException{
		for (char c : msg.toCharArray()) {
			if(c != "\"".toCharArray()[0]) {
			}
		}
	}

}
