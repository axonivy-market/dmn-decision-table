package com.axonivy.ivy.process.element.rule.model;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
      builder.append(arguments.get(argument));
    }
    return builder.toString();
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
    return new HashCodeBuilder().append(operator).append(arguments).toHashCode();
  }
}
