package com.axonivy.ivy.process.element.rule.script;

import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.ivy.process.element.rule.model.ActionColumn;
import com.axonivy.ivy.process.element.rule.model.ColumnType;
import com.axonivy.ivy.process.element.rule.model.ConditionCell;
import com.axonivy.ivy.process.element.rule.model.ConditionColumn;
import com.axonivy.ivy.process.element.rule.model.Operator;
import com.axonivy.ivy.process.element.rule.model.Row;
import com.axonivy.ivy.process.element.rule.model.RulesModel;
import com.axonivy.ivy.process.element.rule.model.ValueCell;

public class RulesModelParser {

  private String rulesScript;
  private int pos = 0;
  private int line = 1;
  private int column = 1;

  private RulesModel model = new RulesModel();
  private String nextToken;

  public RulesModelParser(String rulesScript) {
    this.rulesScript = rulesScript;
  }

  public RulesModel toModel() throws ParseException {
    if (StringUtils.isNotBlank(rulesScript)) {
      do {
        Row row = new Row();
        assertNextToken("if");
        assertNextToken("(");
        parseRowCondition(row);
        assertNextToken(")");
        assertNextToken("{");
        parseRowAction(row);
        model.addRow(row);
      } while (nextTokenIs("else"));
    }
    return model;
  }

  private void parseRowAction(Row row) throws ParseException {
    do {
      String attributeName = nextToken();
      assertNextToken("=");
      String value = nextToken();
      row.addCell(new ValueCell(value));
      ActionColumn col = new ActionColumn(attributeName, ColumnType.String);
      if (!model.getActionColumns().contains(col)) {
        model.addColumn(col);
      }
      assertNextToken(";");
    } while (!nextTokenIs("}"));
  }

  private void parseRowCondition(Row row) {
    do {
      String attributeName = nextToken();
      String operatorSign = nextToken();
      String value = nextToken();
      row.addCell(new ConditionCell(Operator.valueOfScriptToken(operatorSign), value));
      ConditionColumn col = new ConditionColumn(attributeName, ColumnType.String);
      if (!model.getConditionColumns().contains(col)) {
        model.addColumn(col);
      }
    } while (nextTokenIs("&&"));
  }

  private boolean nextTokenIs(String token) {
    boolean result = StringUtils.equals(token, peekNextToken());
    if (result) {
      consumeToken();
    }
    return result;
  }

  private boolean assertNextToken(String token) throws ParseException {
    if (!nextTokenIs(token)) {
      throw new ParseException(
              "Expected token '" + token + "' but was " + nextToken + " (" + line + ", " + column + ")", pos);
    }
    consumeToken();
    return true;
  }

  private void consumeToken() {
    nextToken = null;
  }

  private String nextToken() {
    String token = peekNextToken();
    consumeToken();
    return token;
  }

  private String peekNextToken() {
    if (nextToken != null) {
      return nextToken;
    }
    nextToken = parseNextToken();
    return nextToken;
  }

  private String parseNextToken() {
    StringBuilder token = new StringBuilder();
    while (pos < rulesScript.length()) {
      char ch = rulesScript.charAt(pos++);
      column++;
      if (ch == '\n') {
        column = 1;
        line++;
      }
      if (Character.isWhitespace(ch)) {
        if (token.length() > 0) {
          return token.toString();
        }
      } else {
        token.append(ch);
      }
    }
    return token.toString();
  }
}
