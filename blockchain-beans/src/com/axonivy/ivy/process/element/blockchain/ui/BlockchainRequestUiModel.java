package com.axonivy.ivy.process.element.blockchain.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.StdConverter;

import ch.ivyteam.ivy.datawrapper.scripting.IvyScriptInscriptionModel;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.model.UiModel;
import ch.ivyteam.ivy.process.config.element.pi.ThirdPartyProgramInterfaceConfigurator;
import ch.ivyteam.ivy.process.model.element.activity.ThirdPartyProgramInterface;
import ch.ivyteam.ivy.process.model.element.value.Mappings;
import ch.ivyteam.ivy.process.model.element.value.bean.UserConfig;
import ch.ivyteam.ivy.ui.model.UiMappingTableModel;
import ch.ivyteam.ivy.ui.model.UiMappingTreeTableModel;
import ch.ivyteam.swt.table.Row;
import ch.ivyteam.ui.model.UiComboModel;

public class BlockchainRequestUiModel extends UiModel<ThirdPartyProgramInterface, ThirdPartyProgramInterfaceConfigurator>
{
  public final IvyScriptInscriptionModel propertiesMappingScriptModel;

  public final UiComboModel<String> contracts;
  public final UiComboModel<String> functions;
  public  UiMappingTableModel<Row> properties;
  public  UiMappingTreeTableModel parameters;

  public BlockchainRequestUiModel(ThirdPartyProgramInterfaceConfigurator configurator)
  {
    super(configurator);

    contracts = create().combo(
              ()->getEtherumModel().contract,
              this::setContract,
              new String[] {"aSolidity", "anotherSolidity"})
              .withDefaultValue(null)
              .withDisplayTextProvider(config -> config == null ? "<no blockchain contract>" : config);
    tab.addChild(contracts);

    functions = create().combo(
              ()->getEtherumModel().function,
              this::setFunction,
              new String[] {"aFunction", "anotherFunction"})
            .withDisplayTextProvider(function -> function)
            .withEnabler(this::isContractSelected)
            .dependsOnValueOf(contracts);
    tab.addChild(functions);


    propertiesMappingScriptModel = IvyScriptInscriptionModel
            .create(configurator.project, configurator.processElement)
            .toModel();
    properties = create().mappingTable(
            this::getProperties,
            this::setProperties,
            propertiesMappingScriptModel)
       //     .dependsOnValueOf(contracts)
            .withNameValuesSupplier(this::getPropertyNames)
            .withDefaultValue(Arrays.asList(new Row()));
    tab.addChild(properties);

//    parameters = create().mappingTreeTable(
//              this::getParameterMappings,
//              this::setParameterMappings,
//              configurator.parameterMappingScriptModel)
//            .dependsOnValueOf(contracts)
//            .dependsOnValueOf(operations);
  }

  private EthereumModel getEtherumModel()
  {
    return EthereumModel.load(model.getUserConfig());
  }

  private void setEtherumModel(EthereumModel etherum)
  {
    model.setUserConfig(EthereumModel.store(etherum));
  }

  private void setContract(String newContract)
  {
    EthereumModel etherumModel = getEtherumModel();
    etherumModel.contract = newContract;
    setEtherumModel(etherumModel);
  }

  private void setFunction(String newFunction)
  {
    EthereumModel etherumModel = getEtherumModel();
    etherumModel.function = newFunction;
    setEtherumModel(etherumModel);
  }

  private List<Row> getProperties()
  {
    EthereumModel ethereum = getEtherumModel();
    if (ethereum.properties == null)
    {
      return new ArrayList<>();// Arrays.asList(new Row("", ""));
    }
    List<Row> rows = new ArrayList<>();
    for(Entry<String, String> entry : ethereum.properties.entrySet())
    {
      rows.add(new Row(entry.getKey(), entry.getValue()));
    }
    System.out.println("get props:"+rows);
    return rows;
  }

  private void setProperties(List<Row> rows)
  {
    EthereumModel etherum = getEtherumModel();
    etherum.properties = new HashMap<>();
    rows.stream().forEach(row -> etherum.properties.put(row.name, row.value));
    System.err.println("set props: "+etherum.properties);
    setEtherumModel(etherum);
  }

  private Mappings getParameterMappings()
  {
//    Mappings rawMappings = model.getOperation().getParameters();
//    Mappings uiMappings = configurator.fromRawToUiMappings(rawMappings);
//    return uiMappings;
    return new Mappings();
  }

  private void setParameterMappings(Mappings uiMappings)
  {
//    Mappings rawMappings = configurator.fromUiToRawMappings(uiMappings);
//    Operation operation = model.getOperation();
//    operation = operation.setParameters(rawMappings);
//    model.setOperation(operation);
  }

  private SortedSet<String> getPropertyNames()
  {

    SortedSet<String> allProps = new TreeSet<>();
    getProperties().stream().forEach(row -> allProps.add(row.name));

    return allProps;
    /*Set<String> alreadySetProps = model.getProperties().asList().stream()
            .map(mapping -> mapping.getLeftSide())
            .collect(Collectors.toSet());
    */
    //allProps.addAll(alreadySetProps);
    //EtherumModel model = EtherumModel.load(model.getUserConfig());
    //return allProps;
  }

  public static class EthereumModel
  {

    public static final ObjectMapper mapper = new ObjectMapper();

    public static EthereumModel load(UserConfig config)
    {
      if (config.isEmpty())
      {
        return new EthereumModel();
      }
      try
      {
        return mapper.readerFor(EthereumModel.class).readValue(config.getRawValue());
      }
      catch (IOException ex)
      {
       return new EthereumModel();
      }
    }

    public static UserConfig store(EthereumModel model)
    {
      try
      {
        String json = mapper.writerFor(EthereumModel.class).writeValueAsString(model);
        return new UserConfig(json);
      }
      catch (JsonProcessingException ex)
      {
        return new UserConfig("");
      }
    }

    public Map<String, String> properties;
    public String contract = "";
    public String function = "";
  }

  public static class MappingsToMap2 extends StdConverter<Mappings, Map<String, String>>
  {
    @Override
    public Map<String, String> convert(Mappings mappings)
    {
      Map<String, String> map = new HashMap<>();
      mappings.asList().stream().forEach(mapping -> map.put(mapping.getLeftSide(), mapping.getRightSide()));
      return map;
    }
  }

  public static class MappingsToMap extends JsonSerializer<Map> {

    @Override
    public void serialize(Map tmpInt,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
                          throws IOException, JsonProcessingException {
        jsonGenerator.writeObject(tmpInt.toString());
    }
  }

  private boolean isContractSelected()
  {
    return !contracts.isDefault();
  }
}
