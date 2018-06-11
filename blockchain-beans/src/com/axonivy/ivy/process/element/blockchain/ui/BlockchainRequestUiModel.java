package com.axonivy.ivy.process.element.blockchain.ui;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ivyteam.ivy.datawrapper.scripting.IvyScriptInscriptionModel;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.model.UiModel;
import ch.ivyteam.ivy.process.config.element.pi.ThirdPartyProgramInterfaceConfigurator;
import ch.ivyteam.ivy.process.model.element.activity.ThirdPartyProgramInterface;
import ch.ivyteam.ivy.process.model.element.value.Mapping;
import ch.ivyteam.ivy.process.model.element.value.Mappings;
import ch.ivyteam.ivy.process.model.element.value.bean.UserConfig;
import ch.ivyteam.ivy.scripting.types.IIvyClass;
import ch.ivyteam.ivy.scripting.util.Variable;
import ch.ivyteam.ivy.ui.model.UiMappingTableModel;
import ch.ivyteam.ivy.ui.model.UiMappingTreeTableModel;
import ch.ivyteam.swt.table.Row;
import ch.ivyteam.ui.model.UiComboModel;

public class BlockchainRequestUiModel extends UiModel<ThirdPartyProgramInterface, ThirdPartyProgramInterfaceConfigurator>
{
  public final IvyScriptInscriptionModel propertiesMappingScriptModel;
  public final IvyScriptInscriptionModel parameterMappingScriptModel;

  public final UiComboModel<String> contracts;
  public final UiComboModel<String> functions;
  public final UiMappingTableModel<Row> properties;
  public  UiMappingTreeTableModel parameters;

  public BlockchainRequestUiModel(ThirdPartyProgramInterfaceConfigurator configurator)
  {
    super(configurator);

    contracts = create().combo(
              ()->getEtherumModel().contract,
              this::setContract,
              this::getContracts)
              .withDefaultValue(null)
              .withDisplayTextProvider(config -> config == null ? "<no blockchain contract>" : config);
    tab.addChild(contracts);

    functions = create().combo(
              ()->getEtherumModel().function,
              this::setFunction,
              this::getFunctions)
            .withDefaultValue(null)
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

    parameterMappingScriptModel = IvyScriptInscriptionModel
            .create(configurator.project, configurator.processElement)
            .outputVariablesSupplier(this::getParameterVariables)
            .toModel();
    parameters = create().mappingTreeTable(
              this::getParameterMappings,
              this::setParameterMappings,
              parameterMappingScriptModel)
            .dependsOnValueOf(contracts)
            .dependsOnValueOf(functions);
    tab.addChild(parameters);
  }

  private String[] getContracts()
  {
    List<String> contractNames = new ArrayList<>();
    String superclass = "org.web3j.tx.Contract";
    IJavaProject javaProject = JavaCore.create(configurator.project.getProject());
    try
    {
      IType type = javaProject.findType(superclass);
      List<IType> subTypes = Arrays.asList(type.newTypeHierarchy(null).getAllSubtypes(type));
      subTypes.forEach(subType -> contractNames.add(subType.getFullyQualifiedName()));
      return contractNames.toArray(new String[contractNames.size()]);
    }
    catch (JavaModelException ex)
    {
      return null;
    }
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

  private List<String> getFunctions()
  {
    if (!isContractSelected())
    {
      return Collections.emptyList();
    }
    List<String> result = new ArrayList<>();
    String contract = contracts.getSelection();
    loadDeclaredMethods(contract).forEach(method -> result.add(method.toString()));
    return result;
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
    System.err.println("get props:"+rows);
    return rows;
  }

  private void setProperties(List<Row> rows)
  {
    EthereumModel ethereum = getEtherumModel();
    ethereum.properties = new HashMap<>();
    rows.stream().forEach(row -> ethereum.properties.put(row.name, row.value));
    System.err.println("set props: "+ethereum.properties);
    setEtherumModel(ethereum);
  }

  private List<Variable> getParameterVariables()
  {
    Method chosenMethod = loadMethod();
    if (chosenMethod == null)
    {
      return Collections.emptyList();
    }
    List<Variable> parameterVariables = new ArrayList<>();
    Parameter[] methodParams = chosenMethod.getParameters();
    for (Parameter parameter : methodParams)
    {
      IIvyClass<?> ivyClass = configurator.project.getIvyScriptClassRepository().getIvyClassForName(parameter.getType().getName());
      parameterVariables.add(new Variable(parameter.getName(), ivyClass));
    }

    return parameterVariables;
  }

  private Method loadMethod()
  {
    String contract = contracts.getSelection();
    String function = functions.getSelection();
    if (contract != null && function != null)
    {
      List<Method> methods = loadDeclaredMethods(contract);
      for (Method method : methods)
      {
        if (method.toString().equals(function))
        {
          return method;
        }
      }
    }
    return null;
  }

  private List<Method> loadDeclaredMethods(String className)
  {
    Class<?> clazz;
    try
    {
      clazz = configurator.project.getProjectClassLoader().loadClass(className);
    }
    catch (ClassNotFoundException ex)
    {
      throw new RuntimeException(ex);
    }
    Method[] methods = clazz.getDeclaredMethods();
    return Arrays.asList(methods);
  }

  private Mappings getParameterMappings()
  {
    EthereumModel ethereum = getEtherumModel();
    if (ethereum.attributes == null)
    {
      return new Mappings();
    }
    List<Mapping> mappings = new ArrayList<>();
    for(Entry<String, String> entry : ethereum.attributes.entrySet())
    {
      mappings.add(new Mapping(entry.getKey(), entry.getValue()));
    }
    System.out.println("get attr:"+mappings);
    return new Mappings(mappings);
  }

  private void setParameterMappings(Mappings uiMappings)
  {
    EthereumModel ethereum = getEtherumModel();
    ethereum.attributes = new HashMap<>();
    uiMappings.forEach(mapping -> ethereum.attributes.put(mapping.getLeftSide(), mapping.getRightSide()));
    System.err.println("set attr: "+ethereum.attributes);
    setEtherumModel(ethereum);
  }

  private SortedSet<String> getPropertyNames()
  {
    SortedSet<String> allProps = new TreeSet<>();
    getProperties().stream().forEach(row -> allProps.add(row.name));
    return allProps;
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

    public Map<String, String> attributes;
    public Map<String, String> properties;
    public String contract = null;
    public String function = null;
  }

  private boolean isContractSelected()
  {
    return !contracts.isDefault();
  }
}
