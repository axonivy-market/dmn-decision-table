package com.axonivy.ivy.process.element.blockchain.ui;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import ch.ivyteam.ivy.datawrapper.scripting.IvyScriptInscriptionModel;
import ch.ivyteam.ivy.designer.process.ui.inscription.model.UiModel;
import ch.ivyteam.ivy.process.config.activity.pi.ThirdPartyProgramInterfaceConfigurator;
import ch.ivyteam.ivy.process.model.element.activity.ThirdPartyProgramInterface;
import ch.ivyteam.ivy.process.model.element.value.Mappings;
import ch.ivyteam.ivy.scripting.types.IIvyClass;
import ch.ivyteam.ivy.scripting.util.Variable;
import ch.ivyteam.ivy.ui.model.UiMappingTreeTableModel;
import ch.ivyteam.ivy.ui.model.UiScriptableTableModel;
import ch.ivyteam.swt.table.Row;
import ch.ivyteam.ui.model.UiComboModel;

public class BlockchainRequestUiModel extends UiModel<ThirdPartyProgramInterface, ThirdPartyProgramInterfaceConfigurator>
{
  private static final String BASE_CONTRACT_CLASS = "org.web3j.tx.Contract";
  public final IvyScriptInscriptionModel propertiesMappingScriptModel;
  public final IvyScriptInscriptionModel parameterMappingScriptModel;

  public final UiComboModel<String> contracts;
  public final UiComboModel<Method> functions;
  public final UiScriptableTableModel<Row> properties;
  public final UiMappingTreeTableModel parameters;

  public BlockchainRequestUiModel(ThirdPartyProgramInterfaceConfigurator configurator)
  {
    super(configurator);

    contracts = create().combo(
              ()->getEthereumModel().contract,
              this::setContract,
              this::getContracts)
              .withDefaultValue("")
              .withDisplayTextProvider(config -> getContractDisplayText(config));
    tab.addChild(contracts);

    functions = create().combo(
              this::getFunction,
              this::setFunction,
              this::getFunctions)
            .withDefaultValue(null)
            .withDisplayTextProvider(function -> function.getName())
            .withEnabler(this::isContractSelected)
            .dependsOnValueOf(contracts);
    tab.addChild(functions);

    propertiesMappingScriptModel = IvyScriptInscriptionModel
            .create(configurator.project, configurator.getElement())
            .toModel();
    properties = create().scriptableTable(
            this::getProperties,
            this::setProperties,
            propertiesMappingScriptModel)
            .withNewRowSupplier(()-> new Row())
            .withDefaultValue(Arrays.asList(new Row()));
    tab.addChild(properties);

    parameterMappingScriptModel = IvyScriptInscriptionModel
            .create(configurator.project, configurator.getElement())
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
    IJavaProject javaProject = JavaCore.create(configurator.project.getProject());
    try
    {
      IType type = javaProject.findType(BASE_CONTRACT_CLASS);
      if (type == null)
      {
        return new String[0];
      }
      List<IType> subTypes = Arrays.asList(type.newTypeHierarchy(null).getAllSubtypes(type));
      subTypes.forEach(subType -> contractNames.add(subType.getFullyQualifiedName()));
      return contractNames.toArray(new String[contractNames.size()]);
    }
    catch (JavaModelException ex)
    {
      return new String[0];
    }
  }

  String getContractDisplayText(String config)
  {
    if (config == null)
    {
      return "<no blockchain contract>";
    }
    String[] parts = config.split("\\.");
    return parts[parts.length - 1];
  }

  private EthereumModel getEthereumModel()
  {
    return EthereumModel.load(model.getUserConfig());
  }

  private void setEthereumModel(EthereumModel etherum)
  {
    model.setUserConfig(EthereumModel.store(etherum));
  }

  private void setContract(String newContract)
  {
    EthereumModel etherumModel = getEthereumModel();
    etherumModel.contract = newContract;
    setEthereumModel(etherumModel);
  }

  private Method getFunction()
  {
    EthereumModel etherumModel = getEthereumModel();
    return BlockchainHelper.loadMethod(configurator.project, etherumModel.contract, etherumModel.function);
  }

  private void setFunction(Method newFunction)
  {
    EthereumModel etherumModel = getEthereumModel();
    etherumModel.function = newFunction.toString();
    setEthereumModel(etherumModel);
  }

  private List<Method> getFunctions()
  {
    if (!isContractSelected())
    {
      return Collections.emptyList();
    }
    String contract = contracts.getSelection();
    return BlockchainHelper.loadDeclaredMethods(configurator.project, contract);
  }

  private List<Row> getProperties()
  {
    EthereumModel ethereum = getEthereumModel();
    if (ethereum.properties == null)
    {
      return new ArrayList<>();
    }
    List<Row> rows = new ArrayList<>();
    for(Entry<String, String> entry : ethereum.properties.entrySet())
    {
      rows.add(new Row(entry.getKey(), entry.getValue()));
    }
    return rows;
  }

  private void setProperties(List<Row> rows)
  {
    EthereumModel ethereum = getEthereumModel();
    ethereum.properties = new HashMap<>();
    rows.stream().forEach(row -> ethereum.properties.put(row.name, row.value));
    setEthereumModel(ethereum);
  }

  private List<Variable> getParameterVariables()
  {
    Method chosenMethod = functions.getSelection();
    if (chosenMethod == null)
    {
      return Collections.emptyList();
    }
    List<Variable> parameterVariables = new ArrayList<>();
    Parameter[] methodParams = chosenMethod.getParameters();
    for (Parameter parameter : methodParams)
    {
      IIvyClass<?> ivyClass = configurator.getIvyScriptClassRepository().getIvyClassForName(parameter.getType().getName());
      parameterVariables.add(new Variable(parameter.getName(), ivyClass));
    }

    return parameterVariables;
  }

  private Mappings getParameterMappings()
  {
    return BlockchainHelper.getMappings(getEthereumModel().attributes);
  }

  private void setParameterMappings(Mappings uiMappings)
  {
    EthereumModel ethereum = getEthereumModel();
    ethereum.attributes = new HashMap<>();
    uiMappings.forEach(mapping -> ethereum.attributes.put(mapping.getLeftSide(), mapping.getRightSide()));
    System.err.println("set attr: "+ethereum.attributes);
    setEthereumModel(ethereum);
  }

  private boolean isContractSelected()
  {
    return !contracts.isDefault();
  }
}
