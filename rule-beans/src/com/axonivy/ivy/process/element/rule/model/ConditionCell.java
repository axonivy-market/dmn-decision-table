package com.axonivy.ivy.process.element.rule.model;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ConditionCell extends Cell
{
  public static final ConditionCell NO_CONDITION = new ConditionCell(Operator.NO_CONDITION);
  private Operator operator;
  private List<String> arguments;
  
  public ConditionCell(
          @JsonProperty("operator") Operator operator,
          @JsonProperty("arguments") String... arguments)
  {    
    this.operator = operator;
    this.arguments = Arrays.asList(arguments);
    if (this.arguments.size() != operator.getArguments())
    {
      throw new IllegalArgumentException("Operator "+operator.getSign()+" expects "+operator.getArguments()+" but provided were "+this.arguments.size());
    }
  }

  @JsonIgnore
  public String getFirstArgument()
  {
    if (arguments.isEmpty())
    {
      return StringUtils.EMPTY;
    }
    return arguments.get(0);
  }

  @Override
  @JsonIgnore
  public String getText()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(operator.getSign());
    for (int argument = 0; argument < operator.getArguments(); argument++)
    {
      builder.append(" ");
      String value = formatNumber(arguments.get(argument));
      builder.append(value);
    }
    return builder.toString();
  }

  private static String formatNumber(String value)
  {
    if (NumberUtils.isCreatable(value))
    {
      try
      {
        double amount = Double.parseDouble(value);
        value = NumberFormat.getInstance(new Locale("DE", "ch")).format(amount);
      }
      catch (Exception ex)
      {
        // ignore;
      }
    }
    return value;
  }

  public Operator getOperator()
  {
    return operator;
  }

  public List<String> getArguments()
  {
    return arguments;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (obj.getClass() != ConditionCell.class)
    {
      return false;
    }
    ConditionCell other = (ConditionCell) obj;
    return new EqualsBuilder()
            .append(operator, other.operator)
            .append(arguments, other.arguments)
            .isEquals();
  }
  
  @Override
  public int hashCode()
  {
    return new HashCodeBuilder()
           .append(operator)
           .append(arguments)
           .toHashCode();
  }
}
