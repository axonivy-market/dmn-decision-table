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

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
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
 * PI-Element to edit (add/remove) objects on LDAP-Servers. 
 * @version Marc Willaredt August2009 Updated to Ivy 4.1
 * @version Bruno Bütler 82.11.2005 created
 */
public class LdapObjectEditBean extends AbstractUserProcessExtension {

	/** Jndi Configuration */
	private JndiConfig jndiConfig;

	/** object name */
	private String objectName;

	/** attributes and values */
	private Hashtable attributes = new Hashtable();

	/** second hastable for attributes/values */
	private Hashtable moreAttributes = new Hashtable();

	/** operation code */
	private int operationCode;

	/**
	 * Configuration editor for the jndi attribute modifier bean
	 * 
	 * @author Reto Weiss
	 * @version pk 29.07.2004 pk: renamed some fields
	 * @version ReW 21.5.2002 created
	 */
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

		/** table with the attributes to add */
		private JTable addAttributeTable;

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
					.getString("mod_object_operation_add"));
			rbOperationAdd.setSelected(true);
			AWTUtil.constrain(modifyAttributePanel, rbOperationAdd, 1, 1, 1, 1,
					GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
					10, 10, 0, 10);

			rbOperationRemove = new JRadioButton(resBun
					.getString("mod_object_operation_remove"));
			AWTUtil.constrain(modifyAttributePanel, rbOperationRemove, 2, 1, 1,
					1, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0,
					0.0, 10, 10, 0, 10);

			operationButtons.add(rbOperationAdd);
			operationButtons.add(rbOperationRemove);

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

			// remove panel
			scrollPane = new JScrollPane();
			attributePanel.add("remove", scrollPane);

			AWTUtil.constrain(modifyAttributePanel, attributePanel, 1, 2, 3, 1,
					GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST, 1.0,
					1.0, 10, 10, 10, 10);

			tabbedPanel.add(resBun.getString("mod_object_edit_pane"),
					modifyAttributePanel);

			rbOperationAdd.addActionListener(new AddActionListener());
			rbOperationRemove.addActionListener(new RemoveActionListener());
			;
		}

		/**
		 * This method is called if the user presBunsed the OK button of the
		 * dialog containing this editor. The dialog is closed and the new
		 * configuration is used only if this methode return true
		 * 
		 * @return
		 */
		public boolean acceptInput() {
			return true;
		}

		/**
		 * @return a Component that will be embedded in a JDialog
		 */
		public Component getComponent() {
			return tabbedPanel;
		}

		/**
		 * @return a description of a configuration. This description will be
		 *         sent to the corresBunponding OuterProcessBean
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
	}

	public CompositeObject perform(IRequestId reqID, CompositeObject argument,
			IIvyScriptContext cont) throws Exception {
		DirContext context;
		String newObjectName;
		Enumeration enumeration;
		Enumeration enum2;
		String attribute;
		String attribute2;
		String valueStr;
		Object value;
		BasicAttributes jndiAttributes = new BasicAttributes();
		BasicAttribute jndiAttribute;

		if (objectName.trim().length() == 0) {
			return null;
		} else if (objectName.trim().startsWith("\"")
				&& (objectName.trim().endsWith("\""))) {
			newObjectName = objectName.substring(1, objectName.length() - 1);
		} else {
			newObjectName = (String) getVariable(objectName, cont);
			if (objectName == null) {
				newObjectName = objectName;
			}
		}

		if (operationCode == DirContext.ADD_ATTRIBUTE) {
			// Resolve the values
			enumeration = attributes.keys();
			while (enumeration.hasMoreElements()) {
				attribute = (String) enumeration.nextElement();
				String newAttribute = (String) getVariable(attribute, cont);
				if (newAttribute.indexOf("_") < 0) {
					value = null;
					valueStr = (String) attributes.get(attribute);
					valueStr = valueStr.trim();
					// if the value starts and ends with " I assume that this is
					// a constant string
					if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
						value = valueStr.substring(1, valueStr.length() - 1);
					} else if (valueStr.length() > 0) {
						// if the value does not start and ends with " I asssume
						// that this is the name of a
						// ivy attribute -> try to resBunolve the value as ivy
						// attribute
						if (getVariable(valueStr, cont) != null) {
							// ivyGrid argument found. Get it
							value = getVariable(valueStr, cont);
						} else {
							// no ivy attribute value --> do not set jndi
							// attribute
						}
					}

					if (value != null) {
						jndiAttribute = new BasicAttribute(newAttribute);

						if (value instanceof Vector) {
							for (int pos = 0; pos < ((Vector) value).size(); pos++) {
								jndiAttribute.add(((Vector) value)
										.elementAt(pos));
							}
						} else {
							jndiAttribute.add(value);
						}

						// more values for the same attribute? The the attribute
						// will have a postfix "_i"
						enum2 = moreAttributes.keys();
						while (enum2.hasMoreElements()) {
							attribute2 = (String) enum2.nextElement();
							if (attribute2.startsWith(newAttribute + "_")) {
								value = null;
								valueStr = (String) moreAttributes
										.get(attribute2);
								valueStr = valueStr.trim();
								// if the value starts and ends with " I assume
								// that this is a constant string
								if (valueStr.startsWith("\"")
										&& valueStr.endsWith("\"")) {
									value = valueStr.substring(1, valueStr
											.length() - 1);
								} else {
									// if the value does not start and ends with
									// " I asssume that this is the name of a
									// ivy attribute -> try to resBunolve the
									// value
									// as ivy attribute
									if (getVariable(valueStr, cont) != null) {
										// ivyGrid argument found. Get it
										value = getVariable(valueStr, cont);
									} else {
										// no ivy attribute found with the value
										// as name --> use the value string
										// itself
										// value = valueStr;
									}
								}
								if (value != null) {
									if (value instanceof Vector) {
										for (int pos = 0; pos < ((Vector) value)
												.size(); pos++) {
											jndiAttribute.add(((Vector) value)
													.elementAt(pos));
										}
									} else {
										jndiAttribute.add(value);
									}
								}
							}
						}
						jndiAttributes.put(jndiAttribute);
					}
				}
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
		// access the naming and directory service
		// context = new InitialDirContext(expandedJndiConfig.getEnvironement());  // this only works in Xivy version < 4.3.15
		context = JndiUtil.openDirContext(expandedJndiConfig);		
		try {
			if (operationCode == DirContext.ADD_ATTRIBUTE) { 
				// add object
				// with this
				// attributes
				context.bind(newObjectName, null, jndiAttributes);
			} else { 
				// remove object
				SearchControls searchControl = new SearchControls();
				searchControl.setSearchScope(SearchControls.ONELEVEL_SCOPE);
				removeObjectWithChilds(context, searchControl, newObjectName);
			}
		} finally {
			if (context != null) {
				try {
					context.close();
				} catch (NamingException ex) {
				}
			}
		}
		return argument;
	}

	/**
	 * Constructor
	 */
	public LdapObjectEditBean() {
		jndiConfig = new JndiConfig(JndiProvider.NOVELL_E_DIRECTORY, "ldap://",
				JndiConfig.AUTH_KIND_SIMPLE, "", "", false, false, "");
	}

	public void abort(IRequestId arg0) {
	}

	public String getAdditionalLogInfo(IRequestId arg0) {
		return null;
	}

	public void release() throws Exception {
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
						// add postfix index to handle attributes with several
						// values
						if (attributes.containsKey(attribute)) {
							moreAttributes.put(attribute + "_" + pos, value);
						} else {
							attributes.put(attribute, value);
						}
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
	}

	public void stop() throws Exception {
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
	
	public void setVariable(String name, Object value, CompositeObject argument) throws NoSuchFieldException {
		if(name.indexOf(".") < 0){
				argument.set(name, value);
		}else{
			String newArgumentName = name.substring(0, name.indexOf("."));
			String RemainingName = name.substring(name.indexOf(".") + 1, name.length());
				setVariable(RemainingName, value, (CompositeObject)argument.get(newArgumentName));
		}
	}

	public void ivyPrint(String msg, IIvyScriptContext cont) throws IvyScriptException, PersistencyException{
		String tmpStr = new String();
		for (char c : msg.toCharArray()) {
			if(c != "\"".toCharArray()[0])
			tmpStr += c;
		}
	}

	/**
	 * Remove this object and its children recoursively
	 * 
	 * @param context
	 *            the DirContext
	 * @param searchControl
	 *            "search on level"
	 * @param objectName
	 *            the object to be removed
	 * @throws Exception
	 */
	void removeObjectWithChilds(DirContext context,
			SearchControls searchControl, String objectName)
			throws NamingException {
		NamingEnumeration resultEnum = context.search(objectName,
				"(objectclass=*)", searchControl);
		if (resultEnum == null || !resultEnum.hasMoreElements()) {
			context.unbind(objectName);
		} else { // recursively delete child objects
			while (resultEnum.hasMoreElements()) {
				SearchResult searchResult = (SearchResult) resultEnum
						.nextElement();
				if (searchResult != null) {
					String subObjectName = searchResult.getName();
					if (subObjectName != null) {
						subObjectName = subObjectName.replaceAll("/", "\\\\/");
						if (subObjectName.startsWith("\"")
								&& subObjectName.endsWith("\"")) {
							subObjectName = subObjectName.substring(1,
									subObjectName.length() - 1);
						}
						subObjectName = subObjectName + "," + objectName;
						removeObjectWithChilds(context, searchControl,
								subObjectName);
					}
				}
			}
			context.unbind(objectName);
		}
	}

}
