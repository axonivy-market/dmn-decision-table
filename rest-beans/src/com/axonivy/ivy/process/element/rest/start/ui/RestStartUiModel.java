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
import ch.ivyteam.ui.model.UiTextModel;

public class RestStartUiModel extends UiModel<ThirdPartyProgramStart, ThirdPartyProgramStartConfigurator>
{
  public final IvyScriptInscriptionModel inputParamsScriptModel;
  public final IvyScriptInscriptionModel inputToDataMappingScriptModel;

  public final UiTextModel signatureName;
  public final UiMappingTableModel<Row> inputParams;
  public UiMappingTreeTableModel inputParamToDataMapping;

  @SuppressWarnings("restriction")
  public RestStartUiModel(ThirdPartyProgramStartConfigurator configurator)
  {
    super(configurator);

    signatureName = create().text(
            this::getSignatureName,
            this::setSignatureName)
            .withDefaultText("call");

    inputParamsScriptModel = IvyScriptInscriptionModel
            .create(configurator.project, configurator.processElement)
            .toModel();
    inputParams = create().mappingTable(
            this::getInputParams,
            this::setInputParams,
            inputParamsScriptModel)
            .withNameValuesSupplier(() -> {return new TreeSet<>();})
            .withDefaultValue(Arrays.asList(new Row()));
    tab.addChild(inputParams);

    inputToDataMappingScriptModel = IvyScriptInscriptionModel
            .create(configurator.project, configurator.processElement)
            .addDefaultOutputVariables()
            .additionalInputVariablesSupplier(this::getParameterVariables)
            .toModel();
    inputParamToDataMapping = create().mappingTreeTable(
              this::getParameterMappings,
              this::setParameterMappings,
              inputToDataMappingScriptModel);
    tab.addChild(inputParamToDataMapping);
  }

  private String getSignatureName()
  {
    RestServiceModel dataModel = getRestServiceModel();
    return dataModel.signature;
  }

  private void setSignatureName(String signatureName)
  {
    RestServiceModel dataModel = getRestServiceModel();
    dataModel.signature = signatureName;
    setRestServiceModel(dataModel);
  }

  private void setRestServiceModel(RestServiceModel dataModel)
  {
    model.setUserConfig(RestServiceModel.store(dataModel));
  }

  private RestServiceModel getRestServiceModel()
  {
    return RestServiceModel.load(model.getUserConfig());
  }

  private List<Variable> getParameterVariables()
  {
    List<Row> paramList = inputParams.getRows();
    if (paramList.isEmpty())
    {
      return Collections.emptyList();
    }
    List<Variable> parameterVariables = new ArrayList<>();
    for (Row paramRow : paramList)
    {
      IIvyClass<?> ivyClass = configurator.project.getIvyScriptClassRepository().getIvyClassForName(paramRow.value);
      parameterVariables.add(new Variable(paramRow.name, ivyClass));
    }
    return parameterVariables;
  }

  private Mappings getParameterMappings()
  {
    return RestServiceUtil.getMappings(getRestServiceModel().inputToDataMappings);
  }

  private void setParameterMappings(Mappings uiMappings)
  {
    RestServiceModel dataModel = getRestServiceModel();
    dataModel.inputToDataMappings = new HashMap<>();
    uiMappings.forEach(mapping -> dataModel.inputToDataMappings.put(mapping.getLeftSide(), mapping.getRightSide()));
    System.err.println("set start attr: "+dataModel.inputToDataMappings);
    setRestServiceModel(dataModel);
  }

  private List<Row> getInputParams()
  {
    RestServiceModel dataModel = getRestServiceModel();
    if (dataModel.inputParams == null)
    {
      return new ArrayList<>();
    }
    List<Row> rows = new ArrayList<>();
    for(Entry<String, String> entry : dataModel.inputParams.entrySet())
    {
      rows.add(new Row(entry.getKey(), entry.getValue()));
    }
    return rows;
  }

  private void setInputParams(List<Row> rows)
  {
    RestServiceModel dataModel = getRestServiceModel();
    dataModel.inputParams = new HashMap<>();
    rows.stream().forEach(row -> dataModel.inputParams.put(row.name, row.value));
    setRestServiceModel(dataModel);
  }
}
