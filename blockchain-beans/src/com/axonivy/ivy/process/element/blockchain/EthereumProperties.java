package com.axonivy.ivy.process.element.blockchain;

import java.util.ArrayList;
import java.util.List;

public class EthereumProperties
{
  public static final String NETWORK_URL = "NetworkUrl";
  public static final String CONTRACT_ADDRESS = "ContractAddress";
  public static final String CREDENTIALS = "Credentials";
  public static final String PASSWORD = "Password";

  public static final List<String> ALL_PROPERTIES = getAllProperties();

  private static List<String> getAllProperties()
  {
    List<String> props = new ArrayList<>();
    props.add(NETWORK_URL);
    props.add(CREDENTIALS);
    props.add(PASSWORD);
    props.add(CONTRACT_ADDRESS);
    return props;
  }
}
