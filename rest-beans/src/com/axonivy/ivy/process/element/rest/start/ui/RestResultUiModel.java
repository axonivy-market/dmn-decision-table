package com.axonivy.ivy.process.element.rest.start.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import ch.ivyteam.ivy.datawrapper.scripting.IvyScriptInscriptionModel;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.model.UiModel;
import ch.ivyteam.ivy.process.config.element.thirdPartyProgramStart.ThirdPartyProgramStartConfigurator;
import ch.ivyteam.ivy.process.model.element.event.start.ThirdPartyProgramStart;
import ch.ivyteam.ivy.process.model.element.value.Mappings;
import ch.ivyteam.ivy.scripting.types.IIvyClass;
import ch.ivyteam.ivy.scripting.util.Variable;
import ch.ivyteam.ivy.ui.model.UiMappingTableModel;
import ch.ivyteam.ivy.ui.model.UiMappingTreeTableModel;
import ch.ivyteam.swt.table.Row;

public class RestResultUiModel extends UiModel<ThirdPartyProgramStart, ThirdPartyProgramStartConfigurator>
{
  public final IvyScriptInscriptionModel outputParamsScriptModel;
  public final IvyScriptInscriptionModel dataToOutputMappingScriptModel;

  public final UiMappingTableModel<Row> outputParams;
  public UiMappingTreeTableModel dataToOutputParamMapping;

  @SuppressWarnings("restriction")
  public RestResultUiModel(ThirdPartyProgramStartConfigurator configurator)
  {
    super(configurator);

    outputParamsScriptModel = IvyScriptInscriptionModel
            .create(configurator.project, configurator.processElement)
            .toModel();
    outputParams = create().mappingTable(
            this::getOutputParams,
            this::setOutputParams,
            outputParamsScriptModel)
            .withNameValuesSupplier(() -> {return new TreeSet<>();})
            .withDefaultValue(Arrays.asList(new Row()));
    tab.addChild(outputParams);

    dataToOutputMappingScriptModel = IvyScriptInscriptionModel
            .create(configurator.project, configurator.processElement)
            .outputVariablesSupplier(this::getMappingVariables)
            .toModel();
    dataToOutputParamMapping = create().mappingTreeTable(
              this::getParameterMappings,
              this::setParameterMappings,
              dataToOutputMappingScriptModel)
            .dependsOnValueOf(outputParams);
    tab.addChild(dataToOutputParamMapping);
  }

  private void setRestServiceModel(RestServiceModel dataModel)
  {
    model.setUserConfig(RestServiceModel.store(dataModel));
  }

  private RestServiceModel getRestServiceModel()
  {
    return RestServiceModel.load(model.getUserConfig());
  }
  
  private List<Variable> getMappingVariables()
  {
    RestServiceModel dataModel = getRestServiceModel();
    
    List<Variable> mappingVariables = new ArrayList<>();
    dataModel.outputParams.forEach((paramName, paramType) -> {
      IIvyClass<?> ivyClass = configurator.project.getIvyScriptClassRepository().getIvyClassForName(paramType);
      mappingVariables.add(new Variable(paramName, ivyClass));
    });
    
    return mappingVariables;
  }

  private List<Row> getOutputParams()
  {
    RestServiceModel dataModel = getRestServiceModel();
    if (dataModel.outputParams == null)
    {
      return Collections.emptyList();
    }
    List<Row> rows = new ArrayList<>();
    for(Entry<String, String> entry : dataModel.outputParams.entrySet())
    {
      rows.add(new Row(entry.getKey(), entry.getValue()));
    }
    return rows;
  }

  private void setOutputParams(List<Row> rows)
  {
    RestServiceModel dataModel = getRestServiceModel();
    dataModel.outputParams = new HashMap<>();
    rows.stream().forEach(row -> dataModel.outputParams.put(row.name, row.value));
    setRestServiceModel(dataModel);
  }

  private Mappings getParameterMappings()
  {
    return RestServiceUtil.getMappings(getRestServiceModel().dataToOutputMappings);
  }

  private void setParameterMappings(Mappings uiMappings)
  {
    RestServiceModel dataModel = getRestServiceModel();
    dataModel.dataToOutputMappings = new HashMap<>();
    uiMappings.forEach(mapping -> dataModel.dataToOutputMappings.put(mapping.getLeftSide(), mapping.getRightSide()));
    System.err.println("set result attr: "+dataModel.dataToOutputMappings);
    setRestServiceModel(dataModel);
  }
}
