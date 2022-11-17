package com.axonivy.ivy.process.element.rule.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ch.ivyteam.ivy.designer.inscription.ui.masks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.designer.inscription.ui.model.AbstractUiModelSwtInscriptionTab;
import ch.ivyteam.ivy.resource.validation.restricted.IvyValidationEvent;
import ch.ivyteam.ivy.resource.validation.restricted.IvyValidationResult;
import ch.ivyteam.ivy.scripting.types.IVariable;
import ch.ivyteam.ivy.ui.model.swt.IvySwtBinder;

public class DmnDecisionTab extends AbstractUiModelSwtInscriptionTab<DmnTableUiModel> implements IInscriptionEditorTab {

  public DmnDecisionTab(DmnTableUiModel model) {
    super(model);
  }

  @Override
  public String getTabName() {
    return "Decision";
  }

  DmnTableUiModel getRequestTabUiModel() {
    return model;
  }

  @Override
  protected Composite createUiAndBindToModel(Composite parent, IvySwtBinder ivySwtBinder) {
    var decisionEditor = new DecisionTableEditor(parent, SWT.NONE);
    GridLayout gridLayout = (GridLayout) decisionEditor.getLayout();
    gridLayout.marginHeight = 0; // margin already provided by tab
    gridLayout.marginWidth = 0;
    var configurator = model.configurator;
    decisionEditor.table.setModel(model.loadRulesModel());
    decisionEditor.setDataVariables(configurator.scriptModel.getInputVariables().stream().toArray(IVariable[]::new));
    decisionEditor.setProject(configurator.project);
    decisionEditor.tabs.setSelection(0); // select table mode
    return decisionEditor;
  }

  @Override
  protected void doValidate(IvyValidationEvent event, IvyValidationResult result) {
    model.storeRulesModel();
  }
}
