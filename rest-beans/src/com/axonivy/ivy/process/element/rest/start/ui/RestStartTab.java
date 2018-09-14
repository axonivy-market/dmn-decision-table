package com.axonivy.ivy.process.element.rest.start.ui;

import org.eclipse.swt.widgets.Composite;

import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.fw.AbstractUiModelSwtInscriptionTab;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.ui.model.swt.IvySwtBinder;

@SuppressWarnings("restriction")
public class RestStartTab extends AbstractUiModelSwtInscriptionTab<RestStartUiModel> implements IInscriptionEditorTab
{
  protected RestStartTab(RestStartUiModel model)
  {
    super(model);
  }

  @Override
  public String getTabName()
  {
    return "Start";
  }

  @Override
  protected Composite createUiAndBindToModel(Composite parent, IvySwtBinder ivySwtBinder)
  {
    RestStartComposite composite = new RestStartComposite(parent);
    ivySwtBinder.bind(model.signatureName).to(composite.nameText);
    ivySwtBinder.bind(model.inputParams).to(composite.inputParamsTable);
    ivySwtBinder.bind(model.inputParamToDataMapping).to(composite.parameterMapping);
    composite.propertySelector.setExpanded(!model.inputParams.isDefault());
    return composite;
  }
}
