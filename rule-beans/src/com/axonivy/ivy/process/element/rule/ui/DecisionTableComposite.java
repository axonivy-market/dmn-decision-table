package com.axonivy.ivy.process.element.rule.ui;

import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import ch.ivyteam.icons.Size;
import ch.ivyteam.swt.editors.AbstractTableViewerNavigator.Cell;
import ch.ivyteam.swt.editors.FastTextCellEditor;
import ch.ivyteam.swt.icons.IconFactory;
import ch.ivyteam.swt.table.TableComposite;
import ch.ivyteam.swt.table.TypedViewerColumn;
import ch.ivyteam.swt.table.TypedViewerColumn.Edited;

public class DecisionTableComposite extends TableComposite<Row>
{
  public RulesModel model;
  
  private ToolItem upBtn;
  private ToolItem downBtn;

  public DecisionTableComposite(Composite parent, int style)
  {
    super(parent, style);
    model = new RulesModel();
    
    withNewRowSupplier(()->addRow());
    withActionBar();
    
    
    addColumn("#", new ColumnWeightData(1), 0)
      .withTextProvider(row -> String.valueOf(model.getRows().indexOf(row)+1));
  }

  public void setModel(RulesModel model)
  {
    this.model = model;
    initFromModel();
  }

  public RulesModel getModel()
  {
    return model;
  }

  @SuppressWarnings("unused")
  private Row addRow()
  {
    Row row = new Row();
    for(Column col : model.getConditionColumns())
    {
      row.addCell(new ConditionCell(Operator.NO_CONDITION));
    }
    for(Column col : model.getActionColumns())
    {
      row.addCell(ValueCell.NO_ASSIGNMENT);
    }
    return row;
  }
  
  private void initFromModel()
  {
    attachModelRows();
    setItems(model.rows);
  }

  public void addConditionColumn(ConditionColumn column)
  {
    int indexOf = this.model.getConditionColumns().size();
    this.model.addColumn(column);
    addUiColumn(indexOf, column);
  }
  
  public void addActionColumn(ActionColumn column)
  {
    int indexOf = this.model.getColumns().size();
    this.model.addColumn(column);
    addUiColumn(indexOf, column);
  }
  
  private void attachModelRows()
  {
    for(int i=0; i<model.getColumns().size(); i++)
    {
      Column column = model.getColumns().get(i);
      addUiColumn(i, column);
    }
  }

  private void addUiColumn(int i, Column column)
  {
    final int t = i;
    TypedViewerColumn<Row> tableCol = addColumn(column.getAttributeName(), new ColumnWeightData(4), i+1)
            .withTextProvider(row -> row.getCells().get(t).getText());
    tableCol.getColumn().setImage(getColumnImage(column));
    provideEditSupport(column, i, tableCol);
  }

  private void provideEditSupport(Column column, final int columnIndex, TypedViewerColumn<Row> tableCol)
  {
    if (column instanceof ConditionColumn)
    {
      Function<Row, Object> conditionValueReader = row -> row.getCells().get(columnIndex);
      Consumer<Edited<Row>> conditionValueApplier = edit -> edit.element.getCells().set(columnIndex, (ConditionCell)edit.value);
      if (column.getType() == ColumnType.String)
      {
        tableCol.withEditingSupport(new StringConditionEditor(viewer.getTable(), SWT.NONE), 
                conditionValueReader, conditionValueApplier);
      }
      else if (column.getType() == ColumnType.Number)
      {
        tableCol.withEditingSupport(new NumberConditionEditor(viewer.getTable(), SWT.NONE), 
                conditionValueReader, conditionValueApplier);
      }
      else if (column.getType() == ColumnType.Boolean)
      {
        tableCol.withEditingSupport(new BooleanConditionEditor(viewer.getTable(), SWT.NONE), 
                conditionValueReader, conditionValueApplier);
      }
    }
    if (column instanceof ActionColumn)
    {
      FastTextCellEditor editor = new FastTextCellEditor(viewer.getTable(), SWT.NONE);
      if (column.getType() == ColumnType.Number)
      {
        ((Text)editor.getControl()).addVerifyListener(new NumericValueVerifier());
      }
      tableCol.withEditingSupport(editor, 
              row -> StringUtils.defaultIfBlank(row.getCells().get(columnIndex).getText(), ""), 
              edit -> edit.element.getCells().set(columnIndex, new ValueCell((String)edit.value)));
    }
  }
  
  private Image getColumnImage(Column column)
  {
    if (column instanceof ConditionColumn)
    {
      return IconFactory.get(this).getArrowRightGreen(Size.SIZE_16);
    }
    else if (column instanceof ActionColumn)
    {
      return IconFactory.get(this).getArrowRightBlue(Size.SIZE_16);
    }
    return null;
  }
  
  @Override
  protected void createActionBar(ToolBar actionBar)
  {
    super.createActionBar(actionBar);
    createUpButton(actionBar);
    createDownButton(actionBar);
    
    viewer.addSelectionChangedListener(event -> {
      updateRowMoveButtonEnabling();
    });
    updateRowMoveButtonEnabling(); // initial setting
  }

  private void createUpButton(ToolBar actionBar)
  {
    upBtn = new ToolItem(actionBar, SWT.PUSH);
    upBtn.setImage(IconFactory.get(actionBar).getArrowUpBlue(Size.SIZE_16));
    upBtn.setToolTipText("move row up");
    upBtn.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        int selectionIndex = viewer.getTable().getSelectionIndex();
        model.rows.add(selectionIndex-1, model.rows.remove(selectionIndex));
        viewer.refresh();
        tableCursor.setEditCell(new Cell(selectionIndex-1, 0));
      }
    });
  }

  private void createDownButton(ToolBar actionBar)
  {
    downBtn = new ToolItem(actionBar, SWT.PUSH);
    downBtn.setImage(IconFactory.get(actionBar).getArrowDownBlue(Size.SIZE_16));
    downBtn.setToolTipText("move row down");
    downBtn.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        int selectionIndex = viewer.getTable().getSelectionIndex();
        model.rows.add(selectionIndex, model.rows.remove(selectionIndex+1));
        viewer.refresh();
        tableCursor.setEditCell(new Cell(selectionIndex+1, 0));
      }
    });
  }
  
  private void updateRowMoveButtonEnabling()
  {
    int selectionIndex = viewer.getTable().getSelectionIndex();
    upBtn.setEnabled(selectionIndex > 0);
    downBtn.setEnabled(selectionIndex < viewer.getTable().getItemCount()-1);
  }
  
}
