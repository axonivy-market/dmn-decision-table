package ch.ivy.beans;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBean;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBeanRuntime;
import ch.ivyteam.ivy.process.extension.IProcessExtensionConfigurationEditorEnvironment;
import ch.ivyteam.ivy.process.extension.impl.AbstractProcessExtensionConfigurationEditor;
import ch.ivyteam.ivy.request.RequestException;
import ch.ivyteam.ivy.service.ServiceException;

/**
 * An Eventbean which is triggered after a given date or interval
 * 
 * @version 29.09.2016 bb Axon.ivy6.3
 */
public class TimedStartEventBean implements IProcessStartEventBean
{

  /** Property Key for the start Day */
  private final static String START_DAY_OF_WEEK = "day";

  /** Property Key for the start hour */
  private final static String START_HOUR_OF_DAY = "hour";

  /** Property Key for the start minute */
  private final static String START_MINUTE_OF_HOUR = "minute";

  /** Property Key for immediately */
  private final static String START_IMMEDIATELY = "immediately";

  /** Property Key for the interval */
  private final static String INTERVAL_IN_SECONDS = "interval";

  /** Reference to the eventRuntime */
  public IProcessStartEventBeanRuntime eventRuntime = null;

  /** Configuration */
  private Properties properties = new Properties();

  /** description of this bean instance */
  private String fDescr;

  /** name of this bean */
  private String fBeanName;

  private Boolean immediate;
  private Boolean started;
  private Boolean running;
  private int interval;

  public TimedStartEventBean()
  {
    fBeanName = "ch.ivy.beans.TimedStarteEventBean";
    fDescr = "Version AxonIvy6.3 2016-09-29";
  }

  @Override
  public String getDescription()
  {
    return fDescr;
  }

  @Override
  public String getName()
  {
    return fBeanName;
  }

  @Override
  public void initialize(IProcessStartEventBeanRuntime _eventRuntime,
          String configuration)
  {
    this.eventRuntime = _eventRuntime;
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
            configuration.getBytes());
    try
    {
      properties.load(byteArrayInputStream);
    }
    catch (IOException ex)
    {
      eventRuntime.getRuntimeLogLogger().error(
              "TimedStartEventBean initialize: IOException!");
    }
    eventRuntime.getRuntimeLogLogger().debug(
            "TimedStartEventBean initialized!");
    immediate = isStartImmediately(properties);
    interval = getInterval(properties);
    running = false;
    started = false;
  }

  @Override
  public boolean isMoreThanOneInstanceSupported()
  {
    return false;
  }

  @Override
  public boolean isRunning()
  {
    return running;
  }

  @Override
  public void poll()
  {
    try
    {
      eventRuntime.fireProcessStartEventRequest(null,
              "Event after interval polled!", null);
    }
    catch (RequestException e)
    {
      eventRuntime.getRuntimeLogLogger().error(
              "FireProcessStartEventRequest failed");
    }
    if (!started)
    {
      started = true;
      eventRuntime.setPollTimeInterval(interval * 1000L);
      eventRuntime.getRuntimeLogLogger().debug(
              "Ordinary Poll Interval [in secs] set: " + interval);
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public void start(IProgressMonitor monitor) throws ServiceException
  {
    if (!running)
    {
      running = true;
      if (immediate)
      {
        started = true;
        try
        {
          eventRuntime.fireProcessStartEventRequest(null,
                  "Event immediatly polled!", null);
          eventRuntime.setPollTimeInterval(interval * 1000L);
          eventRuntime.getRuntimeLogLogger().debug(
                  "Immediately Fired and Poll Interval [in secs] set: " + interval);
        }
        catch (RequestException e)
        {
          eventRuntime.getRuntimeLogLogger().error(
                  "FireProcessStartEventRequest failed");
        }
      }
      else
      {
        int day = getStartDay(properties);
        int hour = getStartHour(properties);
        int minute = getStartMinute(properties);
        Date startDate = new Date();
        startDate.setHours(hour);
        startDate.setMinutes(minute);
        startDate.setSeconds(0);
        Date today = new Date();

        if (day == 0)
        {
          // first possible day
          if (today.after(startDate))
          { // next day
            startDate.setDate(startDate.getDate() + 1);
          }
          eventRuntime.getRuntimeLogLogger().debug(
                  "Calculated StartDate (first possible day): "
                          + startDate);
        }
        else
        {
          while (today.after(startDate)
                  || startDate.getDay() != (day - 1))
          {
            // a day next week
            startDate.setDate(startDate.getDate() + 1);
          }
          eventRuntime.getRuntimeLogLogger().debug(
                  "Calculated StartDate: " + startDate);
        }

        Long diff = startDate.getTime() - today.getTime();

        eventRuntime.setPollTimeInterval(diff);
        eventRuntime.getRuntimeLogLogger().debug(
                "Start Poll Interval [in secs] set: " + diff / 1000L);
      }
    }
    else
    {
      throw new ServiceException("Event Bean " + getName()
              + " is already running");
    }
    eventRuntime.getRuntimeLogLogger()
            .debug("TimedStartEventBean started!");
  }

  @Override
  public void stop(IProgressMonitor monitor) throws ServiceException
  {
    running = false;
    started = false;
    eventRuntime.getRuntimeLogLogger()
            .debug("TimedStartEventBean stopped!");
  }

  /**
   * Get the start day value out of a property
   * 
   * @param prop
   * @return start day
   */
  static int getStartDay(Properties prop)
  {
    try
    {
      String value = prop.getProperty(START_DAY_OF_WEEK).trim();
      return Integer.parseInt(value);
    }
    catch (Exception e)
    {
      return -1;
    }
  }

  /**
   * Get the start hour value out of a property
   * 
   * @param prop
   * @return start hour
   */
  static int getStartHour(Properties prop)
  {
    try
    {
      String value = prop.getProperty(START_HOUR_OF_DAY).trim();
      return Integer.parseInt(value);
    }
    catch (Exception e)
    {
      return -1;
    }
  }

  /**
   * Get the start minute value out of a property
   * 
   * @param prop
   * @return start minute
   */
  static int getStartMinute(Properties prop)
  {
    try
    {
      String value = prop.getProperty(START_MINUTE_OF_HOUR).trim();
      return Integer.parseInt(value);
    }
    catch (Exception e)
    {
      return -1;
    }
  }

  /**
   * Get the immediately value out of a property
   * 
   * @param prop
   * @return immediately
   */
  static boolean isStartImmediately(Properties prop)
  {
    try
    {
      String value = prop.getProperty(START_IMMEDIATELY);
      return Boolean.valueOf(value).booleanValue();
    }
    catch (Exception e)
    {
      return false;
    }
  }

  /**
   * Get the interval in seconds value out of a property
   * 
   * @param prop
   * @return interval
   */
  static int getInterval(Properties prop)
  {
    try
    {
      String value = prop.getProperty(INTERVAL_IN_SECONDS);
      return Integer.parseInt(value);
    }
    catch (Exception e)
    {
      return -1;
    }
  }

  /**
   * An editor that is called from the Start Event Bean - inscription mask used
   * to set configuration parameters for the PI-Bean. Provides the configuration
   * as string
   */
  public static class Editor extends
          AbstractProcessExtensionConfigurationEditor
  {
    private JComboBox<String> dayComboBox = null;

    private JTextField hourField = new JTextField(
            new IntValidDocument(2), "", 2);

    private JTextField minuteField = new JTextField(
            new IntValidDocument(2), "", 2);

    private JCheckBox immediatelyCheckBox = new JCheckBox(
            "start immediately");

    private JTextField intervalSecondsField = new JTextField(
            new IntValidDocument(2), "", 2);

    private JTextField intervalMinutesField = new JTextField(
            new IntValidDocument(2), "", 2);

    private JTextField intervalHoursField = new JTextField(
            new IntValidDocument(2), "", 2);

    private JTextField intervalDaysField = new JTextField(
            new IntValidDocument(4), "", 4);

    private JLabel messageLabel = new JLabel(" ");

    private Properties properties = new Properties();

    private JTextArea explainTxt = new JTextArea(1, 30);

    @Override
    protected void createEditorPanelContent(
            Container editorPanel,
            IProcessExtensionConfigurationEditorEnvironment editorEnvironment)
    {

      immediatelyCheckBox.addChangeListener(new ChangeListener()
        {
          @Override
          public void stateChanged(ChangeEvent e)
          {
            boolean enable = !immediatelyCheckBox.isSelected();
            hourField.setEnabled(enable);
            minuteField.setEnabled(enable);
            dayComboBox.setEnabled(enable);
          }
        });
      GridBagLayout hauptGBL = new GridBagLayout();
      GridBagLayout startGBL = new GridBagLayout();
      GridBagLayout intervalGBL = new GridBagLayout();
      GridBagConstraints gbc;
      editorPanel.setLayout(hauptGBL);

      // START PANEL
      JPanel startPanel = new JPanel();
      startPanel.setBorder(BorderFactory.createTitledBorder("Start"));
      startPanel.setLayout(startGBL);
      gbc = createGirdBagConstraints(0, 0, 1, 1);
      hauptGBL.setConstraints(startPanel, gbc);
      editorPanel.add(startPanel);
      gbc = createGirdBagConstraints(1, 0, 10, 1);
      gbc.weightx = 1000;
      // editorPanel.add(new JLabel(" "), gbc);

      // 1. row
      gbc = createGirdBagConstraints(0, 0, 1, 1);
      dayComboBox = getDayComboBox();
      startGBL.setConstraints(dayComboBox, gbc);
      startPanel.add(dayComboBox);

      gbc = createGirdBagConstraints(1, 0, 1, 1);
      startGBL.setConstraints(hourField, gbc);
      startPanel.add(hourField);

      gbc = createGirdBagConstraints(2, 0, 1, 1);
      JLabel splitLabel = new JLabel(":");
      startGBL.setConstraints(splitLabel, gbc);
      startPanel.add(splitLabel);

      gbc = createGirdBagConstraints(3, 0, 1, 1);
      startGBL.setConstraints(minuteField, gbc);
      startPanel.add(minuteField);

      gbc = createGirdBagConstraints(4, 0, 1, 1);
      JLabel label = new JLabel("[h:m]");
      startGBL.setConstraints(label, gbc);
      startPanel.add(label);

      // 2. row
      gbc = createGirdBagConstraints(0, 1, 5, 1);
      startGBL.setConstraints(immediatelyCheckBox, gbc);
      startPanel.add(immediatelyCheckBox);

      // INTERVAL PANEL
      JPanel intervalPanel = new JPanel();
      intervalPanel.setBorder(BorderFactory
              .createTitledBorder("Interval"));
      intervalPanel.setLayout(intervalGBL);

      gbc = createGirdBagConstraints(0, 1, 1, 1);
      hauptGBL.setConstraints(intervalPanel, gbc);
      editorPanel.add(intervalPanel);

      gbc = createGirdBagConstraints(0, 0, 1, 1);
      intervalGBL.setConstraints(intervalDaysField, gbc);
      intervalPanel.add(intervalDaysField);

      gbc = createGirdBagConstraints(1, 0, 1, 1);
      JLabel splitLabel2 = new JLabel(";");
      intervalGBL.setConstraints(splitLabel2, gbc);
      intervalPanel.add(splitLabel2);

      gbc = createGirdBagConstraints(2, 0, 1, 1);
      intervalGBL.setConstraints(intervalHoursField, gbc);
      intervalPanel.add(intervalHoursField);

      gbc = createGirdBagConstraints(3, 0, 1, 1);
      JLabel splitLabel3 = new JLabel(":");
      intervalGBL.setConstraints(splitLabel3, gbc);
      intervalPanel.add(splitLabel3);

      gbc = createGirdBagConstraints(4, 0, 1, 1);
      intervalGBL.setConstraints(intervalMinutesField, gbc);
      intervalPanel.add(intervalMinutesField);

      gbc = createGirdBagConstraints(5, 0, 1, 1);
      JLabel splitLabel4 = new JLabel(":");
      intervalGBL.setConstraints(splitLabel4, gbc);
      intervalPanel.add(splitLabel4);

      gbc = createGirdBagConstraints(6, 0, 1, 1);
      intervalGBL.setConstraints(intervalSecondsField, gbc);
      intervalPanel.add(intervalSecondsField);

      gbc = createGirdBagConstraints(7, 0, 1, 1);
      JLabel intervalFormatLabel = new JLabel("[d;h:m:s]   ");
      intervalGBL.setConstraints(intervalFormatLabel, gbc);
      intervalPanel.add(intervalFormatLabel);

      // MESSAGE-LABEL
      gbc = createGirdBagConstraints(0, 3, 1, 1);
      gbc.insets = new Insets(0, 0, 0, 0);
      messageLabel.setForeground(Color.RED);
      hauptGBL.setConstraints(messageLabel, gbc);
      editorPanel.add(messageLabel);

      explainTxt.setText("\nThis start event bean fires periodically according to the specified parameters.");
      explainTxt.setEditable(false);
      gbc = createGirdBagConstraints(0, 4, 1, 1);
      hauptGBL.setConstraints(explainTxt, gbc);
      editorPanel.add(explainTxt);
    }

    /**
     * Returns a new JComboBox with week-days in it
     * 
     * @return JComboBox
     */
    private JComboBox<String> getDayComboBox()
    {

      JComboBox<String> comboBox = new JComboBox<String>();
      DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();

      comboBox.addItem("First possible day");

      for (int i = 0; i < dateFormatSymbols.getWeekdays().length; i++)
      {
        comboBox.addItem(dateFormatSymbols.getWeekdays()[i]);
      }

      // Remove empty item which might come from getWeekdays()
      try
      {
        comboBox.removeItem("");
      }
      catch (Exception ex)
      {
      }

      return comboBox;
    }

    /**
     * Creates a GridBagConstraints
     * 
     * @param x x-position
     * @param y y-position
     * @param width
     * @param height number of cells in a column
     * @return number of cells in a row
     */
    private GridBagConstraints createGirdBagConstraints(int x, int y,
            int width, int height)
    {
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = x;
      gbc.gridy = y;
      gbc.gridwidth = width;
      gbc.gridheight = height;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(2, 2, 2, 2);
      return gbc;
    }

    private void saveConfiguration()
    {
      properties.put(START_DAY_OF_WEEK,
              String.valueOf(dayComboBox.getSelectedIndex()));
      properties.put(START_HOUR_OF_DAY, hourField.getText());
      properties.put(START_MINUTE_OF_HOUR, minuteField.getText());
      properties.put(START_IMMEDIATELY,
              new Boolean(immediatelyCheckBox.isSelected()).toString());
      properties.put(INTERVAL_IN_SECONDS,
              String.valueOf(getIntervalSeconds()));
    }

    /**
     * Gets the configuration
     * 
     * @return The configuration as an String
     */
    @Override
    public String getConfiguration()
    {
      saveConfiguration();
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try
      {
        properties.store(byteArrayOutputStream,
                "TimedStartEventBeanProperties");
        return byteArrayOutputStream.toString();
      }
      catch (Exception ex)
      {
        return "";
      }
    }

    /**
     * Set the configuration into the editor
     */
    @Override
    public void setConfiguration(String configurationString)
    {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
              configurationString.getBytes());
      try
      {
        properties.load(byteArrayInputStream);
        setConfiguration();
      }
      catch (IOException ex)
      {
      }
    }

    /**
     * set the configuration in the properties to the gui
     */
    private void setConfiguration()
    {
      boolean isImmediately = isStartImmediately(properties);
      immediatelyCheckBox.setSelected(isImmediately);

      int day = getStartDay(properties);
      if ((day < 0) || (day > 7))
      {
        dayComboBox.setSelectedIndex(0);
      }
      else
      {
        dayComboBox.setSelectedIndex(day);
      }

      int hour = getStartHour(properties);
      if (hour >= 0)
      {
        hourField.setText(addLeadingZero(hour));
      }
      else
      {
        hourField.setText("00");
      }

      int minute = getStartMinute(properties);
      if (minute >= 0)
      {
        minuteField.setText(addLeadingZero(minute));
      }
      else
      {
        minuteField.setText("00");
      }

      int interval = getInterval(properties);
      if (interval > 0)
      {
        setIntervalTime(interval);

      }
      else
      {
        intervalSecondsField.setText("00");
        intervalMinutesField.setText("00");
        intervalHoursField.setText("00");
        intervalDaysField.setText("0");
      }
    }

    /**
     * @see ch.ivyteam.ivy.process.extension.IProcessExtensionConfigurationEditorEx#acceptInput()
     */
    @Override
    public boolean acceptInput()
    {
      saveConfiguration();
      int hour = getStartHour(properties);
      int minute = getStartMinute(properties);
      boolean immediately = isStartImmediately(properties);
      int interval = getInterval(properties);

      boolean accept = interval > 0
              && (immediately || (hour >= 0 && minute >= 0));

      messageLabel.setText("  ");

      if ((hour > 23) || (minute > 59))
      {
        accept = false;
        messageLabel
                .setText("<html><b>&nbsp;Wrong start time format!</b></html>");
      }

      if (interval < 1)
      {
        accept = false;
        messageLabel
                .setText("<html><b>&nbsp;The interval can't be 0!</b></html>");
      }

      if (!accept)
      {
        setConfiguration();
      }
      return accept;
    }

    /**
     * Calculates the seconds from the day-, hour-, minute- and second-Field
     * 
     * @return Seconds (int)
     */
    private int getIntervalSeconds()
    {
      int seconds = getIntFromField(intervalSecondsField);
      seconds = seconds + (getIntFromField(intervalMinutesField) * 60);
      seconds = seconds + (getIntFromField(intervalHoursField) * 3600);
      seconds = seconds + (getIntFromField(intervalDaysField) * 86400);

      return seconds;
    }

    /**
     * Extracs int's from JTextFields. Empty fields will return 0
     * 
     * @param textField JTextField from which the int should be extracted
     * @return Int
     */
    private int getIntFromField(JTextField textField)
    {
      int number = 0;
      try
      {
        number = Integer.parseInt(textField.getText().trim());

      }
      catch (NumberFormatException ex)
      {
        number = 0;
      }
      return number;
    }

    /**
     * Convertes seconds to days, hours, minutes and seconds and writes them
     * into the appropriate JTextFields
     * 
     * @param totalSeconds Seconds to be converted
     */
    private void setIntervalTime(int totalSeconds)
    {
      int days, hours, minutes, seconds;

      days = totalSeconds / 86400;
      hours = totalSeconds % 86400;
      hours = hours / 3600;
      minutes = totalSeconds % 3600;
      minutes = minutes / 60;
      seconds = totalSeconds % 60;

      intervalSecondsField.setText(addLeadingZero(seconds));
      intervalMinutesField.setText(addLeadingZero(minutes));
      intervalHoursField.setText(addLeadingZero(hours));
      intervalDaysField.setText(String.valueOf(days));
    }

    /**
     * Adds a leading zero to an integer if necessary (int <10)
     * 
     * @param number Integer, which has to be formated
     * @return Formatted String
     */
    private String addLeadingZero(int number)
    {
      String formatedNumber;
      if (number < 10)
      {
        formatedNumber = "0" + String.valueOf(number);
      }
      else
      {
        formatedNumber = String.valueOf(number);
      }

      return formatedNumber;
    }

    /**
     * This is a validating document used in the text fields.
     * 
     */
    static class IntValidDocument extends PlainDocument
    {
      private static final long serialVersionUID = 1L;

      /** Max length of TextField */
      private int limit;

      /**
       * Constructor
       * 
       * @param max Max length of TextField
       */
      IntValidDocument(int max)
      {
        super();
        this.limit = max;
      }

      /**
       * A string gets inserted. Exclude all invalid characters.
       * 
       * @param offs
       * @param str
       * @param a
       * @throws BadLocationException
       */
      @Override
      public void insertString(int offs, String str, AttributeSet a)
              throws BadLocationException
      {
        char[] source = str.toCharArray();
        char[] result = new char[source.length];
        int j = 0;

        for (int i = 0; i < result.length; i++)
        {
          if ((Character.isDigit(source[i]) && (getLength() + str
                  .length()) <= limit))
          {
            result[j++] = source[i];
          }
          else
          {
            Toolkit.getDefaultToolkit().beep();
          }
        }
        super.insertString(offs, new String(result, 0, j), a);
      }
    }
  }

}
