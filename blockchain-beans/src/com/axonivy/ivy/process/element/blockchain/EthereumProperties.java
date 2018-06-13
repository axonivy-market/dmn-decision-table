package com.axonivy.ivy.process.element.blockchain;

import java.util.HashSet;
import java.util.Set;

public class EthereumProperties
{
  public static final String NETWORK_URL = "NetworkUrl";
  public static final String CONTRACT_ADDRESS = "ContractAddress";
  public static final String CREDENTIALS = "Credentials";
  public static final String PASSWORD = "Password";

  public static final Set<String> ALL_PROPERTIES = getAllProperties();

  private static Set<String> getAllProperties()
  {
    Set<String> props = new HashSet<>();
    props.add(CONTRACT_ADDRESS);
    props.add(CREDENTIALS);
    props.add(NETWORK_URL);
    props.add(PASSWORD);
    return props;
  }
}
