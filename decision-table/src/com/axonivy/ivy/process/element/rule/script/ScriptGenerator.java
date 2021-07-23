package com.axonivy.ivy.process.element.rule.script;

import com.axonivy.ivy.process.element.rule.model.ActionColumn;
import com.axonivy.ivy.process.element.rule.model.ConditionCell;
import com.axonivy.ivy.process.element.rule.model.ConditionColumn;
import com.axonivy.ivy.process.element.rule.model.Operator;
import com.axonivy.ivy.process.element.rule.model.Row;
import com.axonivy.ivy.process.element.rule.model.RulesModel;
import com.axonivy.ivy.process.element.rule.model.ValueCell;

public class ScriptGenerator
{
  private RulesModel model;
  private StringBuilder builder = new StringBuilder();

  public ScriptGenerator(RulesModel model)
  {
    this.model = model;
  }
  
  public String toScript()
  {
    for(Row row : model.getRows())
    {
      toScript(row);
    }
    return builder.toString();
  }

  private void toScript(Row row)
  {
    if (builder.length() > 0)
    {
      builder.append("else ");
    }
    builder.append("if ( ");
    toConditionScript(row);
    builder.append(" )\n");
    builder.append("{\n");
    toActionScript(row);
    builder.append("}\n");
  }

  private void toActionScript(Row row)
  {
    for (ActionColumn action : model.getActionColumns())
    {
      ValueCell cell = (ValueCell)row.getCells().get(model.getColumns().indexOf(action));
      if (!cell.isNoAssignment())
      {
        builder.append("  ");
        builder.append(action.getAttributeName());
        builder.append(" = ");
        builder.append(cell.getValue());
        builder.append(" ;\n");
      }
    }
  }

  private void toConditionScript(Row row)
  {
    boolean first = true; 
    for (ConditionColumn condition : model.getConditionColumns())
    {
      ConditionCell cell = (ConditionCell)row.getCells().get(model.getColumns().indexOf(condition));
      first = toScript(first, condition, cell);
    }
  }

  private boolean toScript(boolean first, ConditionColumn condition, ConditionCell cell)
  {
    if (cell.getOperator() != Operator.NO_CONDITION)
    {
      if (!first)
      {
        builder.append(" && ");
      }
      builder.append(condition.getAttributeName());
      builder.append(" ");
      builder.append(cell.getOperator().getScriptToken());
      builder.append(" ");
      builder.append(cell.getArguments().get(0));
      first = false;
    }
    return first;
  }
}
