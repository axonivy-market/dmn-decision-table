package com.axonivy.ivy.process.element.blockchain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.axonivy.ivy.process.element.blockchain.exec.EthereumExecutor;
import com.axonivy.ivy.process.element.blockchain.ui.EthereumModel;
import com.google.inject.Injector;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.ivy.bpm.exec.restricted.acl.scripting.IvyScriptCodeBuilder;
import ch.ivyteam.ivy.bpm.exec.restricted.scripting.BpmIvyScriptExecutor;
import ch.ivyteam.ivy.bpm.exec.restricted.scripting.BpmIvyScriptResult;
import ch.ivyteam.ivy.persistence.PersistencyException;
import ch.ivyteam.ivy.process.engine.IRequestId;
import ch.ivyteam.ivy.process.extension.impl.AbstractUserProcessExtension;
import ch.ivyteam.ivy.process.model.element.value.Mapping;
import ch.ivyteam.ivy.process.model.element.value.Mappings;
import ch.ivyteam.ivy.process.model.value.MappingCode;
import ch.ivyteam.ivy.scripting.exceptions.IvyScriptException;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;
import ch.ivyteam.ivy.scripting.restricted.execution.IvyScriptCode;
import ch.ivyteam.ivy.scripting.system.IIvyScriptClassRepository;
import ch.ivyteam.ivy.scripting.util.IvyScriptProcessVariables;

public class EthereumActivity extends AbstractUserProcessExtension
{

  @Override
  public CompositeObject perform(IRequestId requestId, CompositeObject in, IIvyScriptContext context) throws Exception
  {
    String configuration = getConfiguration();
    EthereumModel model = EthereumModel.load(configuration);
    EthereumExecutor executor = new EthereumExecutor(model.contract, model.function, mapRequestProperties(model, context));

    Injector injector  = DiCore.getGlobalInjector().getInstance(Injector.class);

    Object[] callParams = mapRequestParameters(model, executor, injector);
    Object callResult = executor.execute(callParams).send();
    return mapResponseParams(in, model, injector, callResult);
  }

  private Map<String, String> mapRequestProperties(EthereumModel model, IIvyScriptContext context)
  {
    Map<String, String> propMappings = new HashMap<>();
    EthereumProperties.ALL_PROPERTIES.forEach(key -> {
        try
        {
          propMappings.put(key, executeIvyScript(context, model.properties.getOrDefault(key, "")).toString());
        }
        catch (PersistencyException | IvyScriptException ex)
        {
          propMappings.put(key, "");
        }
      });
    return propMappings;
  }

  private Object[] mapRequestParameters(EthereumModel model, EthereumExecutor executor, Injector injector)
  {
    BpmIvyScriptExecutor inScriptExecutor = injector.getInstance(BpmIvyScriptExecutor.class);

    Mappings inMappings = new Mappings();
    for(Entry<String, String> mapping : model.attributes.entrySet())
    {
      inMappings = inMappings.add(new Mapping(mapping.getKey(), mapping.getValue()));
    }

    IIvyScriptClassRepository classRepo = injector.getInstance(IIvyScriptClassRepository.class);
    Arrays.stream(executor.getMethodParams()).forEach(param ->
        inScriptExecutor.withVariable(classRepo.getIvyClassForType(param.getType()), param.getName()));

    IvyScriptCode inMappingScript = new IvyScriptCodeBuilder().append(inMappings).toScript();
    BpmIvyScriptResult result = inScriptExecutor.forScript(inMappingScript).execute();
    Object[] callParams = Arrays.stream(executor.getMethodParams()).map(param ->
        result.getValueOfVariableName(param.getName())).toArray();
    return callParams;
  }

  private CompositeObject mapResponseParams(CompositeObject in, EthereumModel model, Injector injector, Object callResult)
  {
    BpmIvyScriptExecutor outScriptExecutor = injector.getInstance(BpmIvyScriptExecutor.class);
    outScriptExecutor.withValueForOutVariable(in);
    outScriptExecutor.withValueForVariable(callResult, IvyScriptProcessVariables.RESULT);

    Mappings outMappings = new Mappings();
    for(Entry<String, String> mapping : model.outputMappings.entrySet())
    {
      outMappings = outMappings.add(new Mapping(mapping.getKey(), mapping.getValue()));
    }

    MappingCode mappingCode = new MappingCode(outMappings, model.outputCode);
    IvyScriptCode outMappingScript = new IvyScriptCodeBuilder().append(mappingCode).toScript();
    CompositeObject outVariable = outScriptExecutor.forScript(outMappingScript).execute().getValueOfOutVariable();
    return outVariable;
  }
}
