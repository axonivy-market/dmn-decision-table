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
import ch.ivyteam.ivy.scripting.objects.List;
import ch.ivyteam.ivy.scripting.objects.Recordset;
import ch.ivyteam.naming.JndiConfig;
import ch.ivyteam.naming.JndiProvider;
import ch.ivyteam.naming.JndiUtil;

/**
 * PI-Element to query LDAP-Servers A variant from LdapQueryBean that returns
 * the values as Recordset
 * 
 * @author Bruno Bütler
 * @version MarcWillaredt August2009 Updated to Ivy 4.1
 * @version bb 16.11.2005 improved (allow "in.x" as ivy attribute names) and
 *          extended (anyFilter)
 * @version bb 2.11.2005 created.
 */
public class LdapQueryBeanRS extends AbstractUserProcessExtension {

	/** Jndi server configuration */
	private JndiConfig jndiConfig;

	/** Maps the resulting jndi attribute names to ivyGrid attribute names */
	private Hashtable resultAttributesHashtable = new Hashtable();

	/** To have an ordered list of the attribut names */
	private Vector resultAttributesKeys = new Vector();

	/** Filter attribute names */
	private Hashtable filterAttributesHashtable = new Hashtable();

	/** Jndi search control */
	private SearchControls searchControl = new SearchControls();

	/** ivyGrid Attribute to store results in */
	private String ivyGridAttribute = null;

	/** The root object to begin search for */
	private String rootObjectName;

	/** include jndi name to result */
	private boolean includeName;

	/** any filter as text */
	private String anyFilterText = null;

	/** ivyGrid attribute to store the jndi name in */
	private String ivyGridNameAttribute;

	/** attribute name to sort the result */
	private String sortByAttribute;

	/** sort the result descending (and not ascending) */
	private boolean descendingSort;

	/**
	 * Constructor
	 * 
	 * @exception Exception
	 */
	public LdapQueryBeanRS() throws Exception {
		jndiConfig = new JndiConfig(JndiProvider.NOVELL_E_DIRECTORY, "ldap://",
				JndiConfig.AUTH_KIND_SIMPLE, "", "", false, false, "");
	}

	/**
	 * Sets a configuration string. This configuration string is produced by the
	 * configuration editor of the element.
	 * 
	 * @param configuration
	 *            string
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
			jndiConfig.setDefaultContext(props
					.getProperty("server_context", ""));

			rootObjectName = props.getProperty("search_root_object", "");
			if (rootObjectName.startsWith("in.")) {
				rootObjectName = rootObjectName.substring(3);
			}

			if ("subTree".equals(props.getProperty("search_scope", ""))) {
				searchControl.setSearchScope(SearchControls.SUBTREE_SCOPE);
			} else if ("oneLevel".equals(props.getProperty("search_scope", ""))) {
				searchControl.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			} else {
				searchControl.setSearchScope(SearchControls.OBJECT_SCOPE);
			}

			pos = 0;
			do {
				attribute = props.getProperty("search_filter_attribute_" + pos,
						null);
				value = props.getProperty("search_filter_value_" + pos, null);
				if ((attribute != null) && (value != null)
						&& (!attribute.equals(""))) {
					if (value.startsWith("in.")) {
						value = value.substring(3);
					}
					filterAttributesHashtable.put(attribute, value);
				}
				pos++;
			} while ((attribute != null) && (value != null));

			if ("all".equals(props.getProperty("result_return"))) {
				ivyGridAttribute = props.getProperty(
						"result_ivyGrid_attribute", "");
				if (ivyGridAttribute.startsWith("in.")) {
					ivyGridAttribute = ivyGridAttribute.substring(3);
				}
				pos = 0;
				do {
					attribute = props.getProperty("result_attribute_attribute_"
							+ pos, null);
					if ((attribute != null) && (!attribute.equals(""))) {
						resultAttributesHashtable.put(attribute, "");
						resultAttributesKeys.add(attribute);
					}
					pos++;
				} while (attribute != null);
			} else {
				pos = 0;
				do {
					attribute = props.getProperty("result_table_attribute_"
							+ pos, null);
					value = props
							.getProperty("result_table_value_" + pos, null);
					if ((attribute != null) && (value != null)
							&& (!attribute.equals(""))) {
						resultAttributesHashtable.put(attribute, value);
						resultAttributesKeys.add(attribute);
					}
					pos++;
				} while ((attribute != null) && (value != null));
			}
			if (ivyGridAttribute == null) {
				includeName = new Boolean(props.getProperty(
						"result_include_name", Boolean.FALSE.toString()))
						.booleanValue();
			} else {
				includeName = new Boolean(props.getProperty(
						"result_include_name2", Boolean.FALSE.toString()))
						.booleanValue();
			}

			if ("filterText".equals(props.getProperty("search_filter_format"))) {
				anyFilterText = props.getProperty("search_filter_text");
			}

			ivyGridNameAttribute = props.getProperty(
					"result_ivyGrid_name_attribute", "");
			if (ivyGridNameAttribute.startsWith("in.")) {
				ivyGridNameAttribute = ivyGridNameAttribute.substring(3);
			}

			sortByAttribute = props.getProperty("result_sort_attribute", "");

			if ("descending".equals(props.getProperty("result_sort_order"))) {
				descendingSort = true;
			} else {
				descendingSort = false;
			}

			searchControl
					.setReturningAttributes((String[]) resultAttributesHashtable
							.keySet().toArray(new String[0]));

			// 31.10.2006 bb: set returningObjFlag to false. If it is true, the
			// dircontext.close() method
			// does not close and the dircontext remains in memory until the GC
			// finally sweeps it out.
			// The number of nativ system threads will grow and grow what can
			// result in an out of memory exception!
			searchControl.setReturningObjFlag(false);
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

	/**
	 * Called if the simulation starts
	 * 
	 * @exception Exception
	 */
	public void start() throws Exception {
	}

	/**
	 * Called if the simulation stopps
	 * 
	 * @exception Exception
	 */
	public void stop() throws Exception {
	}

	public CompositeObject perform(IRequestId reqID, CompositeObject argument,
			IIvyScriptContext cont) throws Exception {
		NamingEnumeration resultEnum = null;
		DirContext dirContext = null;
		Enumeration attrEnum;
		String attribute, value;
		String filter = "";
		String objectName;
		SearchResult searchResult;
		Attributes jndiAttributes;
		Attribute jndiAttribute;
		Vector result = null;
		Vector row = null;
		Vector colNames = new Vector();

		// Build search filter
		if (anyFilterText != null) {
			// expand ivy attributtes "in.x.." in the filter string
			StringBuffer sb = new StringBuffer();
			int at = anyFilterText.indexOf("in.");
			int to = 0;
			while (at >= 0) {
				sb.append(anyFilterText.substring(to, at));
				to = anyFilterText.indexOf(")", at);
				String attr = anyFilterText.substring(at + 3, to);
				if (getVariable(attr, cont) != null) {
					// ivyGrid argument found. Get it an make string out of it
					sb.append(getVariable(attr, cont).toString());
				}
				at = anyFilterText.indexOf("in.", to);
			}
			sb.append(anyFilterText.substring(to));
			filter = sb.toString();
		} else {
			attrEnum = filterAttributesHashtable.keys();
			while (attrEnum.hasMoreElements()) {
				String oldAttribute = (String) attrEnum.nextElement();
				String newAttribute = (String) getVariable(oldAttribute, cont);
				value = (String) filterAttributesHashtable.get(oldAttribute);
				value = (String) getVariable(value, cont);
				value = value.trim();
				// if the filter value starts and ends with " I assume that this
				// is a constant string
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length() - 1);
				} else {
					// if the filter value does not start and ends with " I
					// assume that this is the name of a
					// ivyGrid argument -> try to resBunolve the value as
					// ivyGrid
					// argument
					if (getVariable(value, cont) != null) {
						// ivyGrid argument found. Get it an make string out of
						// it
						value = getVariable(value, cont).toString();
					}
					// no ivyGrid argument found with the value as name --> use
					// the value string itself
				}
				if (filter.length() == 0) {
					filter += "(&";
				}
				filter += "(";
				filter += newAttribute + "=" + value;
				filter += ")";
			}

			if (filter.length() > 0) {
				filter += ")";
			}
		}

		if (rootObjectName.trim().startsWith("\"")
				&& (rootObjectName.trim().endsWith("\""))) {
			objectName = rootObjectName.substring(1,
					rootObjectName.length() - 1);
		} else {
			objectName = (String) getVariable(rootObjectName, cont);
			if (objectName == null) {
				objectName = rootObjectName;
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

		try {
			// query the naming and directory service
			// dirContext = new InitialDirContext(expandedJndiConfig.getEnvironement());  // this only works in Xivy version < 4.3.15
			dirContext = JndiUtil.openDirContext(expandedJndiConfig);
			resultEnum = dirContext.search(objectName, filter, searchControl);

			if (ivyGridAttribute != null) {
				result = new Vector();
			}

			// read the result and assign them to the ivyGrid arguments
			if (resultEnum == null || !resultEnum.hasMoreElements()) {
				// no result found! --> set output to null
				if (ivyGridAttribute == null) {
					attrEnum = resultAttributesKeys.elements();
					while (attrEnum.hasMoreElements()) {
						attribute = (String) attrEnum.nextElement();
						setVariable((String) resultAttributesHashtable
								.get(attribute), null, argument);
					}
					if (includeName) {
						setVariable(ivyGridNameAttribute, null, argument);
					}
				}
			} else {
				if (includeName) {
					colNames.add("JNDIName");
				}
				attrEnum = resultAttributesKeys.elements();
				while (attrEnum.hasMoreElements()) {
					attribute = (String) attrEnum.nextElement();
					colNames.add(attribute);
				}

				while (resultEnum.hasMoreElements()) {
					searchResult = (SearchResult) resultEnum.nextElement();
					if (searchResult != null) {
						if (ivyGridAttribute != null) {
							row = new Vector();
						}

						// include jndi name in result if it is selected
						if (includeName) {
							String resultObjectName = searchResult.getName();
							if (resultObjectName != null) {
								resultObjectName = resultObjectName.replaceAll(
										"/", "\\\\/");
								if (resultObjectName.startsWith("\"")
										&& resultObjectName.endsWith("\"")) {
									resultObjectName = resultObjectName
											.substring(1, resultObjectName
													.length() - 1);
								}
							}

							if (ivyGridAttribute != null) {
								if (objectName.trim().equals("")) {
									row.add(resultObjectName);
								} else {
									row
											.add(resultObjectName + ","
													+ objectName);
								}
							} else {
								if (objectName.trim().equals("")) {
									setVariable(ivyGridNameAttribute,
											resultObjectName, argument);
								} else {
									setVariable(ivyGridNameAttribute,
													resultObjectName + ","
															+ objectName, argument);
								}
							}
						}
						jndiAttributes = searchResult.getAttributes();

						attrEnum = resultAttributesKeys.elements();
						while (attrEnum.hasMoreElements()) {
							attribute = (String) attrEnum.nextElement();
							if (jndiAttributes != null) {
								jndiAttribute = jndiAttributes.get(attribute);
							} else {
								jndiAttribute = null;
							}
							if (jndiAttribute != null) {
								if (jndiAttribute.size() == 1) {
									if (ivyGridAttribute != null) {
										row.add(jndiAttribute.get());
									} else {
										setVariable(
														(String) resultAttributesHashtable
																.get(attribute),
														jndiAttribute.get(), argument);
									}
								} else if (jndiAttribute.size() > 1) {
									if (ivyGridAttribute != null) {
										NamingEnumeration<?> tmpEnum = jndiAttribute.getAll();
										List l = List.create(String.class);
										while(tmpEnum.hasMoreElements()){
											String tmpStr = (String) tmpEnum.nextElement();
											l.add(tmpStr);
										}
										row.add(l);
									} else {
										setVariable(
														(String) resultAttributesHashtable
																.get(attribute),
														jndiAttribute.getAll(), argument);
									}
								}
							} else if (ivyGridAttribute != null) {
								row.add("");
							}
						}
					}
					if (ivyGridAttribute == null) {
						return argument;
					}
					result.add(row);
				}
			}

			if (ivyGridAttribute != null
					&& Recordset.class.equals(getVariable(ivyGridAttribute,
							cont).getClass())) {
				if (result.size() == 0) {
					setVariable(ivyGridAttribute, null, argument);
				} else {
					int sortCol = 0;
					if(getVariable(sortByAttribute, cont) != null){
						sortByAttribute = getVariable(sortByAttribute, cont).toString();
					}
					if (sortByAttribute != null) {
						for (int c = 0; c < colNames.size(); c++) {
							if (sortByAttribute.equals(colNames.elementAt(c))) {
								sortCol = c;
								break;
							}
						}
					}
					Object[][] data = new Object[result.size()][colNames.size()];
					for (int r = 0; r < result.size(); r++) {
						Vector aRow = (Vector) result.elementAt(r);
						int insertAt = 0;
						if (sortByAttribute == null) {
							insertAt = r;
						} else if (descendingSort) {
							while (insertAt < r
									&& aRow.elementAt(sortCol) != null
									&& data[insertAt][sortCol].toString()
											.compareToIgnoreCase(
													aRow.elementAt(sortCol)
															.toString()) > 0) {
								insertAt++;
							}
						} else { // ascending
							while (insertAt < r
									&& aRow.elementAt(sortCol) != null
									&& data[insertAt][sortCol].toString()
											.compareToIgnoreCase(
													aRow.elementAt(sortCol)
															.toString()) < 0) {
								insertAt++;
							}
						}
						for (int c = 0; c < colNames.size(); c++) {
							for (int shift = 0; insertAt < (r - shift); shift++) {
								data[r - shift][c] = data[r - shift - 1][c];
							}
							data[insertAt][c] = aRow.elementAt(c);
						}
					}

					List keyList = List.create();
					for (Object object : colNames) {
						keyList.add(object);
					}
					Recordset returnRS = new Recordset(keyList);
					for (int j = 0; j < data.length; j++){
						List<Object> valueList = List.create();
						for (Object val : data[j]) {
							if(val != null)
								valueList.add(val);
						}
						returnRS.add(valueList);
					}
					setVariable(ivyGridAttribute, returnRS, argument);
				}
			} else if (ivyGridAttribute != null) { // return as list[list]
				setVariable(ivyGridAttribute, result, argument);
			}
		} finally {
			if (resultEnum != null) {
				try {
					resultEnum.close();
				} catch (NamingException ex) {
				}
			}
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
		// TODO Auto-generated method stub

	}

	public String getAdditionalLogInfo(IRequestId arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void release() throws Exception {
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
	
	public void setVariable(String name, Object value, CompositeObject argument) throws NoSuchFieldException {
		if(name.startsWith("in.")){
			name = name.substring("in.".length());
		}
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
		this.executeIvyScript(cont, "ivy.log.info(\""+tmpStr+"\")");
	}

	
	/**
	 * An editor that is called from the PI-inscription mask used to set
	 * configuration parameters for the PI-Bean. Provides the configuration as
	 * string
	 * 
	 * @author Peter Koch
	 * @created 19. November 2001
	 */
	@SuppressWarnings("serial")
	public static class Editor extends AbstractProcessExtensionConfigurationEditor {

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

		/** result table */
		private JTable resultTable;

		/** Search filter panel */
		private JPanel filterPanel;

		/** card layout of the filter panel */
		private CardLayout filterLayout;

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

		/** sort result ascending radio button */
		private JRadioButton rbSortAscending;

		/** sort result descending radio button */
		private JRadioButton rbSortDescending;

		/** the name of the attribute object to use for sorting */
		private JTextField tfSortAttributeName;

		/**
		 * Constructor for the Editor object
		 */
		public Editor() {

			ResourceBundle resBun = ResourceBundle.getBundle("TextResource",
					new Locale("de"));

			jndiConfig = new JndiConfig(JndiProvider.NOVELL_E_DIRECTORY,
					"ldap://", JndiConfig.AUTH_KIND_SIMPLE, "", "", false,
					false, "");

			tabbedPanel = new JTabbedPane();

			// Jndi Server panel
			jndiConfigPanel = new JndiConfigPanel(jndiConfig, false);
			tabbedPanel.add("Jndi Server", jndiConfigPanel);

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

			label = new JLabel(resBun.getString("search_result_return"));
			AWTUtil.constrain(resultPanel, label, 0, 0, 1, 1,
					GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
					10, 10, 0, 10);

			ButtonGroup resultButtons = new ButtonGroup();
			rbReturnOnlyFirst = new JRadioButton(resBun
					.getString("search_result_return_only_first_result"));
			rbReturnOnlyFirst.setSelected(true);
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
					.getString("search_result_value_rs"));
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
					0, 0, 0, 0);

			tfIvyGridAttribute = new JTextField(20);
			AWTUtil.constrain(allResultPanel, tfIvyGridAttribute, 1, 0, 2, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					0.0, 0.0, 0, 0, 0, 0);

			// result for all results
			label = new JLabel(resBun
					.getString("search_result_sort_attribute_name"));
			AWTUtil.constrain(allResultPanel, label, 0, 1, 1, 1,
					GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
					0, 0, 0, 10);

			tfSortAttributeName = new JTextField(20);
			AWTUtil.constrain(allResultPanel, tfSortAttributeName, 1, 1, 2, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					0.0, 0.0, 0, 0, 0, 0);

			ButtonGroup sortButtons = new ButtonGroup();
			rbSortAscending = new JRadioButton(resBun
					.getString("search_result_sort_ascending"));
			rbSortAscending.setSelected(true);
			AWTUtil.constrain(allResultPanel, rbSortAscending, 1, 2, 1, 1,
					GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0.0,
					0.0, 0, 2, 0, 2);

			rbSortDescending = new JRadioButton(resBun
					.getString("search_result_sort_descending"));
			AWTUtil.constrain(allResultPanel, rbSortDescending, 2, 2, 1, 1,
					GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0.0,
					0.0, 0, 2, 0, 2);

			sortButtons.add(rbSortAscending);
			sortButtons.add(rbSortDescending);

			rbIncludeName2 = new JCheckBox(resBun
					.getString("search_result_include_name"));
			AWTUtil.constrain(allResultPanel, rbIncludeName2, 0, 3, 3, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					0.0, 0.0, 10, 0, 0, 0);

			resultAttributeTable = new JTable(20, 0);
			((DefaultTableModel) resultAttributeTable.getModel())
					.addColumn(resBun.getString("search_result_attribute"));
			resultAttributeTable
					.setPreferredScrollableViewportSize(new Dimension(200, 200));
			scrollPane = new JScrollPane(resultAttributeTable);
			AWTUtil.constrain(allResultPanel, scrollPane, 0, 4, 3, 1,
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
		 * Sets the configuration
		 * 
		 * @param config
		 *            the configuration as an String
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

				tfRootObjectName.setText(props.getProperty(
						"search_root_object", ""));

				if ("subTree".equals(props.getProperty("search_scope", ""))) {
					rbSubTree.setSelected(true);
				} else if ("oneLevel".equals(props.getProperty("search_scope",
						""))) {
					rbOneLevel.setSelected(true);
				} else {
					rbOneObject.setSelected(true);
				}

				if ("filterText".equals(props
						.getProperty("search_filter_format"))) {
					rbFilterText.setSelected(true);
					filterLayout.show(filterPanel, "filterText");
				} else {
					rbFilterTable.setSelected(true);
					filterLayout.show(filterPanel, "filterTable");
				}
				filterText.setText(props.getProperty("search_filter_text"));

				pos = 0;
				model = new DefaultTableModel(filterTable.getModel().getRowCount(), filterTable.getModel().getColumnCount());
				Vector cols = new Vector();
				for (int i = 0; i < filterTable.getColumnCount(); i++) {
					cols.add(filterTable.getColumnName(i));
				}
				model.setColumnIdentifiers(cols);
				do {
					attribute = props.getProperty("search_filter_attribute_"
							+ pos, null);
					value = props.getProperty("search_filter_value_" + pos,
							null);
					if ((attribute != null) && (value != null)) {
						model.insertRow(pos, new Object[] { attribute, value });
						pos++;
					}
				} while ((attribute != null) && (value != null));
				filterTable.setModel(model);

				pos = 0;
				model = new DefaultTableModel(resultTable.getModel().getRowCount(), resultTable.getModel().getColumnCount());
				cols = new Vector();
				for (int i = 0; i < resultTable.getColumnCount(); i++) {
					cols.add(resultTable.getColumnName(i));
				}
				model.setColumnIdentifiers(cols);
				do {
					attribute = props.getProperty("result_table_attribute_"
							+ pos, null);
					value = props
							.getProperty("result_table_value_" + pos, null);
					if ((attribute != null) && (value != null)) {
						model.insertRow(pos, new Object[] { attribute, value });
						pos++;
					}
				} while ((attribute != null) && (value != null));
				resultTable.setModel(model);

				pos = 0;
				model = new DefaultTableModel(resultAttributeTable.getModel().getRowCount(), resultAttributeTable.getModel().getColumnCount());
				cols = new Vector();
				for (int i = 0; i < resultAttributeTable.getColumnCount(); i++) {
					cols.add(resultAttributeTable.getColumnName(i));
				}
				model.setColumnIdentifiers(cols);
				do {
					attribute = props.getProperty("result_attribute_attribute_"
							+ pos, null);
					if ((attribute != null)) {
						model.insertRow(pos, new Object[] { attribute });
						pos++;
					}
				} while (attribute != null);
				resultAttributeTable.setModel(model);

				tfIvyGridAttribute.setText(props.getProperty(
						"result_ivyGrid_attribute", ""));

				tfSortAttributeName.setText(props.getProperty(
						"result_sort_attribute", ""));

				if ("all".equals(props.getProperty("result_return"))) {
					dbReturnAll.setSelected(true);
					returnLayout.show(returnPanel, "all");
				} else {
					rbReturnOnlyFirst.setSelected(true);
					returnLayout.show(returnPanel, "onlyFirst");
				}

				if ("descending".equals(props.getProperty("result_sort_order"))) {
					rbSortDescending.setSelected(true);
				} else {
					rbSortAscending.setSelected(true);
				}

				cbIncludeName.setSelected(new Boolean(props.getProperty(
						"result_include_name", Boolean.FALSE.toString()))
						.booleanValue());
				rbIncludeName2.setSelected(new Boolean(props.getProperty(
						"result_include_name2", Boolean.FALSE.toString()))
						.booleanValue());
				tfIvyGridNameAttribute.setText(props.getProperty(
						"result_ivyGrid_name_attribute", ""));
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

		/**
		 * Gets the component attribute of the Editor object
		 * 
		 * @return this
		 */
		public Component getComponent() {
			tabbedPanel.setSize(300, 500);
			return tabbedPanel;
		}

		/**
		 * Gets the configuration
		 * 
		 * @return The configuration as an String
		 */
		public String getConfiguration() {
			String attribute, value;
			ByteArrayOutputStream baos = null;
			Properties props = new Properties();

			TableCellEditor cellEditor;
			cellEditor = filterTable.getCellEditor();
			if (cellEditor != null) {
				cellEditor.stopCellEditing();
			}
			cellEditor = resultTable.getCellEditor();
			if (cellEditor != null) {
				cellEditor.stopCellEditing();
			}
			;
			cellEditor = resultAttributeTable.getCellEditor();
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

			props.setProperty("search_root_object", tfRootObjectName.getText());

			if (rbOneObject.isSelected()) {
				props.setProperty("search_scope", "oneObject");
			} else if (rbOneLevel.isSelected()) {
				props.setProperty("search_scope", "oneLevel");
			} else if (rbSubTree.isSelected()) {
				props.setProperty("search_scope", "subTree");
			}

			if (rbFilterText.isSelected()) {
				props.setProperty("search_filter_format", "filterText");
			}
			props.setProperty("search_filter_text", filterText.getText());

			TableModel model = filterTable.getModel();
			for (int pos = 0; pos < model.getRowCount(); pos++) {
				attribute = (String) model.getValueAt(pos, 0);
				value = (String) model.getValueAt(pos, 1);
				if ((attribute != null) && (value == null)) {
					value = "";
				} else if ((attribute == null) && (value != null)) {
					attribute = "";
				}
				if ((attribute != null) && (value != null)) {
					props.setProperty("search_filter_attribute_" + pos,
							attribute.trim());
					props.setProperty("search_filter_value_" + pos, value
							.trim());
				} else {
					break;
				}
			}

			model = resultTable.getModel();
			for (int pos = 0; pos < model.getRowCount(); pos++) {
				attribute = (String) model.getValueAt(pos, 0);
				value = (String) model.getValueAt(pos, 1);
				if ((attribute != null) && (value == null)) {
					value = "";
				} else if ((attribute == null) && (value != null)) {
					attribute = "";
				}
				if ((attribute != null) && (value != null)) {
					props.setProperty("result_table_attribute_" + pos,
							attribute.trim());
					props
							.setProperty("result_table_value_" + pos, value
									.trim());
				} else {
					break;
				}
			}

			model = resultAttributeTable.getModel();
			for (int pos = 0; pos < model.getRowCount(); pos++) {
				attribute = (String) model.getValueAt(pos, 0);
				if (attribute != null) {
					props.setProperty("result_attribute_attribute_" + pos,
							attribute.trim());
				} else {
					break;
				}
			}

			props.setProperty("result_ivyGrid_attribute", tfIvyGridAttribute
					.getText().trim());

			props.setProperty("result_sort_attribute", tfSortAttributeName
					.getText().trim());

			if (dbReturnAll.isSelected()) {
				props.setProperty("result_return", "all");
			} else {
				props.setProperty("result_return", "onylFirst");
			}

			if (rbSortDescending.isSelected()) {
				props.setProperty("result_sort_order", "descending");
			} else {
				props.setProperty("result_sort_order", "ascending");
			}

			props.setProperty("result_include_name", new Boolean(cbIncludeName
					.isSelected()).toString());
			props.setProperty("result_include_name2", new Boolean(
					rbIncludeName2.isSelected()).toString());
			props.setProperty("result_ivyGrid_name_attribute",
					tfIvyGridNameAttribute.getText().trim());

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

		public boolean acceptInput() {
			return true;
		}

		class AllActionListener implements ActionListener {
			/**
			 * Invoked when an action occurs.
			 * 
			 * @param e
			 */
			public void actionPerformed(ActionEvent e) {
				returnLayout.show(returnPanel, "all");
			}
		}

		class OnlyFirstActionListener implements ActionListener {
			/**
			 * Invoked when an action occurs.
			 * 
			 * @param e
			 */
			public void actionPerformed(ActionEvent e) {
				returnLayout.show(returnPanel, "onlyFirst");
			}
		}

		class FilterTableActionListener implements ActionListener {
			/**
			 * Invoked when an action occurs.
			 * 
			 * @param e
			 */
			public void actionPerformed(ActionEvent e) {
				filterLayout.show(filterPanel, "filterTable");
			}
		}

		class FilterTextActionListener implements ActionListener {
			/**
			 * Invoked when an action occurs.
			 * 
			 * @param e
			 */
			public void actionPerformed(ActionEvent e) {
				filterLayout.show(filterPanel, "filterText");
			}
		}

	}
}