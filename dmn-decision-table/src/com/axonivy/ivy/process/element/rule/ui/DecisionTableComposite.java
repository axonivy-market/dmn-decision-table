package com.axonivy.ivy.process.element.rule.ui;

import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.axonivy.ivy.process.element.rule.model.ActionColumn;
import com.axonivy.ivy.process.element.rule.model.Column;
import com.axonivy.ivy.process.element.rule.model.ColumnType;
import com.axonivy.ivy.process.element.rule.model.ConditionCell;
import com.axonivy.ivy.process.element.rule.model.ConditionColumn;
import com.axonivy.ivy.process.element.rule.model.Operator;
import com.axonivy.ivy.process.element.rule.model.Row;
import com.axonivy.ivy.process.element.rule.model.RulesModel;
import com.axonivy.ivy.process.element.rule.model.ValueCell;
import com.axonivy.ivy.process.element.rule.ui.cellEdit.BooleanConditionEditor;
import com.axonivy.ivy.process.element.rule.ui.cellEdit.NumberConditionEditor;
import com.axonivy.ivy.process.element.rule.ui.cellEdit.NumericValueVerifier;
import com.axonivy.ivy.process.element.rule.ui.cellEdit.StringConditionEditor;

import ch.ivyteam.swt.editors.TraverseKeyListener;
import ch.ivyteam.swt.table.AbstractTypedViewerColumn.Edited;
import ch.ivyteam.swt.table.TableComposite;
import ch.ivyteam.swt.table.TypedTableViewerColumn;

public class DecisionTableComposite extends TableComposite<Row> {

  public RulesModel model;

  private ToolItem upBtn;
  private ToolItem downBtn;

  public DecisionTableComposite(Composite parent, int style) {
    super(parent, style);
    model = new RulesModel();

    withNewRowSupplier(() -> addRow());
    withActionBar();

    addColumn("#", new ColumnWeightData(1), 0)
            .withTextProvider(row -> String.valueOf(model.getRows().indexOf(row) + 1));
  }

  public void setModel(RulesModel model) {
    this.model = model;
    initFromModel();
  }

  public RulesModel getModel() {
    return model;
  }

  @SuppressWarnings("unused")
  private Row addRow() {
    Row row = new Row();
    for (Column col : model.getConditionColumns()) {
      row.addCell(new ConditionCell(Operator.NO_CONDITION));
    }
    for (Column col : model.getActionColumns()) {
      row.addCell(ValueCell.NO_ASSIGNMENT);
    }
    return row;
  }

  private void initFromModel() {
    attachModelRows();
    setItems(model.getRows());
  }

  public void addConditionColumn(ConditionColumn column) {
    int indexOf = this.model.getConditionColumns().size();
    this.model.addColumn(column);
    addUiColumn(indexOf, column);
    setItems(model.getRows());
  }

  public void addActionColumn(ActionColumn column) {
    int indexOf = this.model.getColumns().size();
    this.model.addColumn(column);
    addUiColumn(indexOf, column);
    setItems(model.getRows());
  }

  private void attachModelRows() {
    for (int i = 0; i < model.getColumns().size(); i++) {
      Column column = model.getColumns().get(i);
      addUiColumn(i, column);
    }
  }

  private void addUiColumn(int i, Column column) {
    TypedTableViewerColumn<Row> tableCol = addColumn(column.getAttributeName(), new ColumnWeightData(4), i + 1)
                    .withTextProvider(row -> model.getCell(row, column).getText());
    tableCol.getColumn().setImage(getColumnImage(column));
    provideEditSupport(column, tableCol);
  }

  @SuppressWarnings("deprecation")
  private void provideEditSupport(Column column, TypedTableViewerColumn<Row> tableCol) {
    if (column instanceof ConditionColumn) {
      Function<Row, Object> conditionValueReader = row -> model.getCell(row, column);
      Consumer<Edited<Row>> conditionValueApplier = edit -> model.setCell(edit.element, column, (ConditionCell) edit.value);
      if (column.getType() == ColumnType.String) {
        StringConditionEditor editor = new StringConditionEditor(viewer.getTable(), SWT.NONE);
        TraverseKeyListener.install(editor.getControl(), cellNavigator);
        tableCol.withEditingSupport(editor,
                conditionValueReader, conditionValueApplier);
      } else if (column.getType() == ColumnType.Number) {
        tableCol.withEditingSupport(new NumberConditionEditor(viewer.getTable(), SWT.NONE),
                conditionValueReader, conditionValueApplier);
      } else if (column.getType() == ColumnType.Boolean) {
        tableCol.withEditingSupport(new BooleanConditionEditor(viewer.getTable(), SWT.NONE),
                conditionValueReader, conditionValueApplier);
      }
    }
    if (column instanceof ActionColumn) {
      var editor = createTextCellEditor();
      if (column.getType() == ColumnType.Number) {
        ((Text) editor.getControl()).addVerifyListener(new NumericValueVerifier());
      }
      tableCol.withEditingSupport(editor,
              row -> StringUtils.defaultIfBlank(model.getCell(row, column).getText(), ""),
              edit -> model.setCell(edit.element, column, new ValueCell((String) edit.value)));
    }
  }

  private Image getColumnImage(Column column) {
    if (column instanceof ConditionColumn) {
      return RuleBeansIcons.CONDITION_COLUMN.createImage();
    } else if (column instanceof ActionColumn) {
      return RuleBeansIcons.OUTPUT_COLUMN.createImage();
    }
    return null;
  }

  @Override
  protected void createActionBar(ToolBar actionBar) {
    super.createActionBar(actionBar);
    upBtn = createUpButton(actionBar);
    downBtn = createDownButton(actionBar);

    viewer.addSelectionChangedListener(event -> {
      updateRowMoveButtonEnabling();
    });
    updateRowMoveButtonEnabling(); // initial setting
  }

  private void updateRowMoveButtonEnabling() {
    int selectionIndex = viewer.getTable().getSelectionIndex();
    upBtn.setEnabled(selectionIndex > 0);
    downBtn.setEnabled(selectionIndex < viewer.getTable().getItemCount() - 1);
  }

}
