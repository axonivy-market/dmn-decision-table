package com.axonivy.ivy.process.element.rule.model;

import java.util.Arrays;

public enum Operator
{
  NO_CONDITION("", "", 0), 
  EQUAL("EQUAL TO", "==", 1),
  UNEQUAL("UNEQUAL TO", "!=", 1), 
  LESS("LESS THAN", "<", 1),
  GREATER("GREATER THAN", ">", 1),
  EQUAL_OR_GREATER ("EQUAL OR GREATER THAN", ">=", 1),
  EQUAL_OR_SMALLER ("EQUAL OR GREATER THAN", "<=", 1);
  
  private String sign;
  private String scriptToken;
  private int arguments;

  private Operator(String sign, String scriptToken, int arguments)
  {
    this.sign = sign;
    this.scriptToken = scriptToken;
    this.arguments = arguments;
  }
  
  public String getSign()
  {
    return sign;
  }
  
  public String getScriptToken()
  {
    return scriptToken;
  }
  
  public int getArguments()
  {
    return arguments;
  }

  public static Operator valueOfScriptToken(String scriptToken)
  {
    return Arrays.asList(values()).stream().filter(op -> op.getScriptToken().equals(scriptToken)).findAny().get();
  }
}
