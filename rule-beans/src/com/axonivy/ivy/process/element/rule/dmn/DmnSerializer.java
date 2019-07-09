package com.axonivy.ivy.process.element.rule.dmn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.axonivy.dmn.specification.ObjectFactory;
import com.axonivy.dmn.specification.TDecision;
import com.axonivy.dmn.specification.TDecisionRule;
import com.axonivy.dmn.specification.TDecisionTable;
import com.axonivy.dmn.specification.TDefinitions;
import com.axonivy.dmn.specification.THitPolicy;
import com.axonivy.dmn.specification.TInputClause;
import com.axonivy.dmn.specification.TLiteralExpression;
import com.axonivy.dmn.specification.TOutputClause;
import com.axonivy.dmn.specification.TUnaryTests;
import com.axonivy.ivy.process.element.rule.model.ActionColumn;
import com.axonivy.ivy.process.element.rule.model.Cell;
import com.axonivy.ivy.process.element.rule.model.Column;
import com.axonivy.ivy.process.element.rule.model.ColumnType;
import com.axonivy.ivy.process.element.rule.model.ConditionCell;
import com.axonivy.ivy.process.element.rule.model.ConditionColumn;
import com.axonivy.ivy.process.element.rule.model.Row;
import com.axonivy.ivy.process.element.rule.model.RulesModel;
import com.axonivy.ivy.process.element.rule.model.ValueCell;
import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

public class DmnSerializer
{
  private static final THitPolicy DEFAULT_HIT_POLICY = THitPolicy.FIRST;
  private static final String DMN_XML_NAMESPACE = "http://www.omg.org/spec/DMN/20151101/dmn.xsd";
  private int idCounter = 1;
  private RulesModel model;
  private static final ObjectFactory factory = new ObjectFactory();

  public DmnSerializer(RulesModel model)
  {
    this.model = model;
  }

  public InputStream serialize()
  {
    TDecisionTable decisionTable = createDecisionTable();
    TDecision decision = createDecision(decisionTable);
    TDefinitions definitions = createDefinition(decision);
    return toInputStream(definitions);
  }

  private TDecisionTable createDecisionTable()
  {
    TDecisionTable decisionTable = factory.createTDecisionTable();
    decisionTable.setId("decisionTable");
    decisionTable.setHitPolicy(DEFAULT_HIT_POLICY);
    decisionTable.getInput().addAll(createInputs());
    decisionTable.getOutput().addAll(createOutputs());
    decisionTable.getRule().addAll(createRules());
    return decisionTable;
  }

  private List<TInputClause> createInputs()
  {
    List<TInputClause> inputs = new ArrayList<>();
    for (ConditionColumn column : model.getConditionColumns())
    {
      TLiteralExpression expression = factory.createTLiteralExpression();
      expression.setId("inputExpression_" + idCounter++);
      expression.setText(column.getAttributeName());
      expression.setTypeRef(toTypeRef(column));

      TInputClause inputClause = factory.createTInputClause();
      inputClause.setId("input_" + idCounter++);
      inputClause.setLabel(column.getAttributeName());
      inputClause.setInputExpression(expression);

      inputs.add(inputClause);
    }
    return inputs;
  }

  private List<TOutputClause> createOutputs()
  {
    List<TOutputClause> outputs = new ArrayList<>();
    for (ActionColumn column : model.getActionColumns())
    {
      TOutputClause outputClause = factory.createTOutputClause();
      outputClause.setId("output_" + idCounter++);
      outputClause.setName(column.getAttributeName());
      outputClause.setLabel(column.getAttributeName());
      outputClause.setTypeRef(toTypeRef(column));
      outputs.add(outputClause);
    }
    return outputs;
  }

  private List<TDecisionRule> createRules()
  {
    List<TDecisionRule> rules = new ArrayList<>();
    for (Row row : model.getRows())
    {
      TDecisionRule rule = factory.createTDecisionRule();
      rule.setId("rule_" + idCounter++);
      for (int i = 0; i < row.getCells().size(); i++)
      {
        Cell cell = row.getCells().get(i);
        if (isInputEntry(i))
        {
          ColumnType type = model.getConditionColumns().get(i).getType();
          TUnaryTests inputEntry = createInputEntry(cell, type);
          rule.getInputEntry().add(inputEntry);
        }
        else
        {
          ColumnType type = model.getActionColumns().get(i - model.getConditionColumns().size()).getType();
          TLiteralExpression outputEntry = createOutputEntry(cell, type);
          rule.getOutputEntry().add(outputEntry);
        }
      }
      rules.add(rule);
    }
    return rules;
  }

  private boolean isInputEntry(int columnIndex)
  {
    return model.getConditionColumns().size() > columnIndex;
  }

  private TUnaryTests createInputEntry(Cell cell, ColumnType type)
  {
    TUnaryTests inputEntry = factory.createTUnaryTests();
    inputEntry.setId("inputEntry_" + idCounter++);
    if (cell instanceof ConditionCell)
    {
      ConditionCell c = (ConditionCell) cell;
      FeelBuilder builder = FeelBuilder.create();
      switch (c.getOperator())
      {
        case NO_CONDITION:
          break;
        case EQUAL:
          if (type == ColumnType.String)
          {
            builder.cdata().appendEscapedText(c.getFirstArgument());
          }
          else
          {
            builder.appendText(c.getFirstArgument());
          }
          break;
        case UNEQUAL:
          if (type == ColumnType.String)
          {
            builder.cdata().not().appendEscapedText(c.getFirstArgument());
          }
          else
          {
            builder.not().appendText(c.getFirstArgument());
          }
          break;
        case GREATER:
          ensureNumberColumn(type);
          builder.cdata().appendText("> " + c.getFirstArgument());
          break;
        case EQUAL_OR_GREATER:
          ensureNumberColumn(type);
          builder.cdata().appendText(">= " + c.getFirstArgument());
          break;
        case LESS:
          ensureNumberColumn(type);
          builder.cdata().appendText("< " + c.getFirstArgument());
          break;
        case EQUAL_OR_SMALLER:
          ensureNumberColumn(type);
          builder.cdata().appendText("<= " + c.getFirstArgument());
          break;
        default:
          throw new IllegalArgumentException("operator " + c.getOperator() + " is not supported");
      }
      inputEntry.setText(builder.build());
    }
    else
    {
      throw new IllegalArgumentException(
              "cell of type " + cell.getClass().getSimpleName() + " is not supported as input");
    }
    return inputEntry;
  }

  private void ensureNumberColumn(ColumnType type)
  {
    if (type != ColumnType.Number)
    {
      throw new IllegalStateException("column type must be number instead of " + type.name());
    }
  }

  private TLiteralExpression createOutputEntry(Cell cell, ColumnType type)
  {
    TLiteralExpression outputEntry = factory.createTLiteralExpression();
    outputEntry.setId("outputEntry_" + idCounter++);
    if (cell instanceof ValueCell)
    {
      ValueCell c = (ValueCell) cell;
      FeelBuilder builder = FeelBuilder.create();
      switch (type)
      {
        case Boolean:
        case Number:
          builder = builder.appendText(c.getValue());
          break;
        case String:
          builder = builder.cdata().appendEscapedText(c.getValue());
          break;
        default:
          throw new IllegalArgumentException("type " + type.name() + " is currently not supported");
      }
      outputEntry.setText(builder.build());
    }
    else
    {
      throw new IllegalArgumentException("cell of type " + cell.getClass().getSimpleName() + " is not supported as output");
    }
    return outputEntry;
  }

  private TDecision createDecision(TDecisionTable decisionTable)
  {
    TDecision decision = factory.createTDecision();
    decision.setId("decision");
    decision.setName("decision");
    decision.setExpression(factory.createDecisionTable(decisionTable));
    return decision;
  }

  private TDefinitions createDefinition(TDecision decision)
  {
    TDefinitions definitions = factory.createTDefinitions();
    definitions.setId("definitions");
    definitions.setName("definitions");
    definitions.setNamespace(DMN_XML_NAMESPACE);
    definitions.getDrgElement().add(factory.createDecision(decision));
    return definitions;
  }

  private QName toTypeRef(Column column)
  {
    return new QName(DMN_XML_NAMESPACE, toType(column.getType()));
  }

  private static String toType(ColumnType type)
  {
    switch (type)
    {
      case String:
        return "string";
      case Boolean:
        return "boolean";
      case Number:
        return "double";
      default:
        throw new IllegalArgumentException("type " + type.name() + " is currently not supported");
    }
  }

  private InputStream toInputStream(TDefinitions definitions)
  {
    try
    {
      Marshaller jaxbMarshaller = JAXBContext.newInstance(TDefinitions.class).createMarshaller();
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      jaxbMarshaller.setProperty("com.sun.xml.bind.marshaller.CharacterEscapeHandler", new InsecureCharacterEscapeHandler());
      
      JAXBElement<TDefinitions> rootElement = new JAXBElement<>(new QName(DMN_XML_NAMESPACE, "definitions"), TDefinitions.class, definitions);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      jaxbMarshaller.marshal(rootElement, outputStream);
      return new ByteArrayInputStream(outputStream.toByteArray());
    }
    catch (JAXBException ex)
    {
      throw new IllegalStateException("could not serialize", ex);
    }
  }

  private static class InsecureCharacterEscapeHandler implements CharacterEscapeHandler
  {
    @Override
    public void escape(char[] ac, int i, int j, boolean flag, Writer writer) throws IOException
    {
      writer.write(ac, i, j);
    }
  }
}
