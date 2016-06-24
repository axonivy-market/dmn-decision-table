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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import ch.ivyteam.awtExt.AWTUtil;
import ch.ivyteam.naming.JndiConfig;
import ch.ivyteam.naming.JndiProvider;
 
/**
 * This panel can be used to configure JNDI environement information needed to
 * connect to a naming and directory server
 * @author Reto Weiss
 * @version ReW 4.4.2002 created
 */

public class JndiConfigPanel extends JPanel
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/** JNDI environment properties */
  private JndiConfig config;

  /** Resource Bundle */
  private ResourceBundle resBun;
  
  /** Provider combo box */
  private JComboBox cbProvider;

  /** Url text field*/
  private JTextField tfUrl;

  /** authentication type combo box */
  private JComboBox cbAuthType;

  /** user text field */
  private JTextField tfUser;

  /** password text field */
  private JPasswordField tfPassword;

  /** ssl activation check box */
  private JCheckBox cbSsl;

  /** default context */
  private JTextField tfContext;

 /**
   * Constructor
   * @param config Jndi configuration
   */
  public JndiConfigPanel(JndiConfig config, Locale locale)
  {
    this.config=config;
    createGui(true);
  }

  /**
   * Constructor
   * @param config Jndi configuration
   * @param isDefaultContextVisible Flag to indicate whether the default context field should be shown or not
   */
  public JndiConfigPanel(JndiConfig config, boolean isDefaultContextVisible)
  {
    this(config, isDefaultContextVisible, Locale.getDefault());
  }

  /**
   * Constructor
   * @param config Jndi configuration
   * @param isDefaultContextVisible Flag to indicate whether the default context field should be shown or not
   * @param locale The locale used for resBunources
   */
  public JndiConfigPanel(JndiConfig config, boolean isDefaultContextVisible,
                         Locale locale)
  {
    this.config=config;
    createGui(isDefaultContextVisible);
  }
  /**
   * Constructor
   * @param env The environment properties
   */
  public JndiConfigPanel(Locale locale)
  {
    this(new JndiConfig(), locale);    
  }


  /**
   * Creates the gui
   * @param isDefaultContextVisible Flag to indicate if default content field is visible or not
   */
  private void createGui(boolean isDefaultContextVisible)
  {
    resBun = ResourceBundle.getBundle("TextResConfigPanel", new Locale("de"));   
    int ypos=0;
    setLayout(new GridBagLayout());

    JLabel label = new JLabel(resBun.getString("jndi_provider"));
    AWTUtil.constrain(this, label,
      0,ypos,1,1,
      GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
      10,10,0,0
    );

    cbProvider = new JComboBox(JndiProvider.PROVIDERS);
    AWTUtil.constrain(this, cbProvider,
      1,ypos++,1,1,
      GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1.0, 0.0,
      10,10,0,10
    );

    label = new JLabel(resBun.getString("jndi_url"));
    AWTUtil.constrain(this, label,
      0,ypos,1,1,
      GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
      10,10,0,0
    );

    tfUrl = new JTextField();
    AWTUtil.constrain(this, tfUrl,
      1,ypos++,1,1,
      GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1.0, 0.0,
      10,10,0,10
    );

    label = new JLabel(resBun.getString("jndi_auth_type"));
    AWTUtil.constrain(this, label,
      0,ypos,1,1,
      GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
      10,10,0,0
    );

    cbAuthType = new JComboBox(
        new String[]{
            resBun.getString("jndi_auth_type_none"),
            resBun.getString("jndi_auth_type_simple")
        }
    );
    cbAuthType.addActionListener(new AuthTypeActionListener());
    AWTUtil.constrain(this, cbAuthType,
      1,ypos++,1,1,
      GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1.0, 0.0,
      10,10,0,10
    );

    label = new JLabel(resBun.getString("jndi_user"));
    AWTUtil.constrain(this, label,
      0,ypos,1,1,
      GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
      10,10,0,0
    );

    tfUser = new JTextField();
    AWTUtil.constrain(this, tfUser,
      1,ypos++,1,1,
      GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1.0, 0.0,
      10,10,0,10
    );

    label = new JLabel(resBun.getString("jndi_password"));
    AWTUtil.constrain(this, label,
      0,ypos,1,1,
      GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
      10,10,0,0
    );

    tfPassword = new JPasswordField();

    AWTUtil.constrain(this, tfPassword,
      1,ypos++,1,1,
      GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1.0, 0.0,
      10,10,0,10
    );

    label = new JLabel(resBun.getString("jndi_ssl"));
    AWTUtil.constrain(this, label,
      0,ypos,1,1,
      GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
      10,10,0,0
    );

    cbSsl = new JCheckBox();

    AWTUtil.constrain(this, cbSsl,
      1,ypos++,1,1,
      GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1.0, 0.0,
      10,10,0,10
    );

    tfContext = new JTextField();

    if (isDefaultContextVisible)
    {
      label = new JLabel(resBun.getString("jndi_context"));
      AWTUtil.constrain(this, label,
        0,ypos,1,1,
        GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0,
        10,10,0,0
      );

      AWTUtil.constrain(this, tfContext,
        1,ypos++,1,1,
        GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1.0, 0.0,
        10,10,00,10
      );
    }

//    JButton btTest = new JButton(resBun.getString("jndi_test"));
//    btTest.addActionListener(new TestListener());
//    AWTUtil.constrain(this, btTest,
//      1,ypos,1,1,
//      GridBagConstraints.NONE, GridBagConstraints.EAST, 0.0, 0.0,
//      10,10,10,10
//    );
  }

  /**
   * Save the data in the dialog to the model
   */
  public void saveModel()
  {
    config.setProvider((JndiProvider)cbProvider.getSelectedItem());
    config.setUrl(tfUrl.getText().trim());
    switch(cbAuthType.getSelectedIndex())
    {
    case 0:
      config.setAuthenticationKind(JndiConfig.AUTH_KIND_NONE);
      config.setUserName(null);
      config.setPassword(null);
      break;
    case 1:
      config.setAuthenticationKind(JndiConfig.AUTH_KIND_SIMPLE);
      config.setUserName(tfUser.getText().trim());
      config.setPassword(tfPassword.getText());
      break;
    default:
      config.setAuthenticationKind(JndiConfig.AUTH_KIND_NONE);
      config.setUserName(null);
      config.setPassword(null);
      break;
    }

    config.setUseSsl(cbSsl.isSelected());

    config.setDefaultContext(tfContext.getText());
  }

  /**
   * Load the data from the model to the dialog
   */
  public void loadModel()
  {
    tfUrl.setText(config.getUrl());
    if (config.getAuthenticationKind().equals(JndiConfig.AUTH_KIND_NONE))
    {
      tfUser.setText("");
      tfPassword.setText("");
      cbAuthType.setSelectedIndex(0);
    }
    else if (config.getAuthenticationKind().equals(JndiConfig.AUTH_KIND_SIMPLE))
    {
      tfUser.setText(config.getUserName());
      tfPassword.setText(config.getPassword());
      cbAuthType.setSelectedIndex(1);
    }
    else
    {
      tfUser.setText("");
      tfPassword.setText("");
      cbAuthType.setSelectedIndex(0);
    }

    cbSsl.setSelected(config.isUseSsl());

    tfContext.setText(config.getDefaultContext());

    for (int pos=0; pos < JndiProvider.PROVIDERS.length; pos++)
    {
      if (JndiProvider.PROVIDERS[pos].equals(config.getProvider()))
      {
        cbProvider.setSelectedIndex(pos);
        break;
      }
    }
  }

  /**
   * Testprogramm
   */
  public static void main(String args[])
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Throwable th)
    {};
    JFrame dialog = new JFrame();
    dialog.getContentPane().add(new JndiConfigPanel(Locale.GERMAN));
    dialog.pack();
    dialog.setVisible(true);
  }

  /**
   * Gets the model
   * @return model
   */
  public Hashtable getModel()
  {
    return config.getEnvironement();
  }

  /**
   * Gets the configuration
   * @return configuration
   */
  public JndiConfig getConfiguration()
  {
    return config;
  }

  /**
   * Holds information about the JDNI Provider
   */
  static class JNDIProvider
  {
    /** The initial context factory class of the provider */
    private String providerClass;
    /** The name of the provider */
    private String providerName;

    /**
     * Constructor
     * @param providerName the name of the provider
     * @param providerClass the initial context factory class of the provider
     */
    public JNDIProvider(String providerName, String providerClass)
    {
      this.providerClass = providerClass;
      this.providerName = providerName;
    }

    /**
     * Gets a represBunenting string
     * @return string
     */
    public String toString()
    {
      return providerName;
    }

    /**
     * Gets the provider name
     * @return provider name
     */
    public String getProviderName()
    {
      return providerName;
    }

    /**
     * Gets the provider class
     * @return provider class
     */
    public String getProviderClass()
    {
      return providerClass;
    }
  }

  /**
   * Action Listener on the auth type combo box
   */
  class AuthTypeActionListener implements ActionListener
  {
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
      tfUser.setEnabled(cbAuthType.getSelectedIndex()!=0);
      tfUser.setOpaque(cbAuthType.getSelectedIndex()!=0);
      tfPassword.setEnabled(cbAuthType.getSelectedIndex()!=0);
      tfPassword.setOpaque(cbAuthType.getSelectedIndex()!=0);
    }

  }

//  /**
//   * Test Listener
//   */
//  class TestListener implements ActionListener
//  {
//    /**
//     * Invoked when an action occurs.
//     */
//    public void actionPerformed(ActionEvent e)
//    {
//      try
//      {
//        WaitDialog.doAndShowWaitDialog(
//            JndiConfigPanel.this,
//            resBun.getString("jndi_test_title"),
//            resBun.getString("jndi_test_msg"),
//            new TestWorker());
//        CommonDialogs.messageMonolog(
//          JndiConfigPanel.this,
//          resBun.getString("jndi_test_title"),
//          resBun.getString("jndi_test_success"),
//          locale);
//      }
//      catch(Throwable th)
//      {
//        CommonDialogs.messageMonolog(
//            JndiConfigPanel.this,
//            res.getString("jndi_test_title"),
//            res.getString("jndi_test_failed")+"\n" +
//                ExceptionUtil.getLocalizedMessageOf(th),
//            locale
//        );
//      }
//    }
//  }
//
//  /**
//   * Test worker class
//   */
//  class TestWorker extends GuiWorker
//  {
//    protected void work() throws Throwable
//    {
//      test();
//    }
//  }
}