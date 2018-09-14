package com.axonivy.ivy.process.element.rest.start.exec;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.ContainerRequest;

import com.axonivy.ivy.process.element.rest.start.ui.RestServiceModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;

import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.bpm.engine.restricted.IBpmEngine;
import ch.ivyteam.ivy.bpm.engine.restricted.IBpmEngineManager;
import ch.ivyteam.ivy.bpm.exec.restricted.acl.scripting.IvyScriptCodeBuilder;
import ch.ivyteam.ivy.bpm.exec.restricted.scripting.BpmIvyScriptExecutor;
import ch.ivyteam.ivy.bpm.exec.restricted.scripting.BpmIvyScriptResult;
import ch.ivyteam.ivy.java.IJavaConfiguration;
import ch.ivyteam.ivy.java.IJavaConfigurationManager;
import ch.ivyteam.ivy.process.model.element.event.start.ThirdPartyProgramStart;
import ch.ivyteam.ivy.process.model.element.value.Mapping;
import ch.ivyteam.ivy.process.model.element.value.Mappings;
import ch.ivyteam.ivy.project.IIvyProject;
import ch.ivyteam.ivy.project.IvyProjectNavigationUtil;
import ch.ivyteam.ivy.request.IProcessRequest;
import ch.ivyteam.ivy.request.IRequest;
import ch.ivyteam.ivy.request.IResponse;
import ch.ivyteam.ivy.request.RequestException;
import ch.ivyteam.ivy.request.RequestFactory;
import ch.ivyteam.ivy.request.ResponseFactory;
import ch.ivyteam.ivy.request.metadata.MetaData;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.language.IIvyScriptEngine;
import ch.ivyteam.ivy.scripting.language.IvyScriptContextFactory;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;
import ch.ivyteam.ivy.scripting.objects.Tuple;
import ch.ivyteam.ivy.scripting.restricted.execution.IvyScriptCode;
import ch.ivyteam.ivy.scripting.system.IIvyScriptClassRepository;
import ch.ivyteam.ivy.scripting.types.IIvyClass;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.workflow.IProcessStart;
import ch.ivyteam.ivy.workflow.ITask;
import ch.ivyteam.ivy.workflow.IWorkflowContext;
import ch.ivyteam.ivy.workflow.IWorkflowProcessModelVersion;
import ch.ivyteam.ivy.workflow.IWorkflowSession;
import ch.ivyteam.log.Logger;

@SuppressWarnings("deprecation")
public class RestProcessRequestHandler
  implements Inflector<ContainerRequestContext, String>
{
  private static final Logger LOGGER = Logger.getClassLogger(RestProcessRequestHandler.class);
  @Inject
  private Injector injector;
  private IProcessModelVersion pmv;
  private ThirdPartyProgramStart start;
  private RestServiceModel model;

  RestProcessRequestHandler configure(IProcessModelVersion thePmv, ThirdPartyProgramStart restStart,
          RestServiceModel restModel)
  {
    this.pmv = thePmv;
    this.start = restStart;
    this.model = restModel;
    return this;
  }

  @Override
  public String apply(ContainerRequestContext containerRequestContext)
  {
//    System.out.println("Hello World! this is Element "+start.getPid());
//    return "{\"driver\": \"Hello World! this is Element "+start.getPid() + "\"}";
    Map<String, Object> parameters = jsonToParams(containerRequestContext);
    Map<String, Object> mappedParams = mapInput(parameters);
    IProcessRequest request = createRestProcessRequest(mappedParams);
    MetaData.pushRequest(request);
    try
    {
      Tuple result = executeWithBpmnEngine(request);
      Map<String, Object> params = asMap(result, request.getTask().getEndProcessData());
      return asJson(params);
    }
    catch (Throwable exc)
    {
      exc.printStackTrace();
      throw exc;
    }
    finally
    {
      MetaData.popRequest();
    }
  }

  private Map<String, Object> mapInput(Map<String, Object> parameters)
  {
    //outScriptExecutor.withValueForInVariable(in);



    Map<String, Object> mapped = new HashMap<>();
    Mappings inMappings = new Mappings();
    for(Entry<String, String> mapping : model.inputToDataMappings.entrySet())
    {
      if (StringUtils.isNotBlank(mapping.getValue()))
      {
        String fieldName = StringUtils.substringAfter(mapping.getKey(),"out.");
        mapped.put(fieldName, execute(mapping.getValue(), parameters));
      }


//      inMappings = inMappings.add(new Mapping(mapping.getKey(), mapping.getValue()));
    }

    return mapped;

//    IIvyScriptClassRepository classRepo = injector.getInstance(IIvyScriptClassRepository.class);
//    model.outputParams.forEach((name, typeName) ->
//        outScriptExecutor.withVariable(classRepo.getIvyClassForName(typeName), name));
//
//    IvyScriptCode inMappingScript = new IvyScriptCodeBuilder().append(inMappings).toScript();
//    BpmIvyScriptResult result = outScriptExecutor.forScript(inMappingScript).execute();
//    model.dataToOutputMappings.keySet().forEach(param ->
//        outParams.put(param, result.getValueOfVariableName(param)));
//    // TODO Auto-generated method stub
//    return null;
  }

  private Object execute(String expression, Map<String, Object> scriptVars)
  {
    try
    {

    IIvyProject ivyProject = IvyProjectNavigationUtil.getIvyProject(pmv.getProject());
  IIvyScriptEngine scriptEngine = ivyProject.getIvyScriptEngine();
  IIvyScriptContext vars = IvyScriptContextFactory.createIvyScriptContext();



    //BpmIvyScriptExecutor outScriptExecutor = injector.getInstance(BpmIvyScriptExecutor.class);
    for(Entry<String, Object> entry : scriptVars.entrySet())
    {
      IIvyClass<?> ivyClassOf = ivyProject.getIvyScriptClassRepository().getIvyClassOf(entry.getValue());
      vars.declareVariable(entry.getKey(), ivyClassOf);
      vars.setObject(entry.getKey(), entry.getValue());
    }

    return scriptEngine.execute(expression, vars);
    }
    catch (Exception ex)
    {
      //ivy script: the source of all evil!
      return null;
    }
  }

  private Map<String, Object> jsonToParams(ContainerRequestContext containerRequestContext)
  {
    if (this.model.inputParams.isEmpty() || !(containerRequestContext instanceof ContainerRequest)) {
      return Collections.emptyMap();
    }
    ContainerRequest jerseyRequest = (ContainerRequest)containerRequestContext;
    IJavaConfiguration javaConfig = this.injector.getInstance(IJavaConfigurationManager.class).getJavaConfiguration(this.pmv);
    return new JsonParamTupleReader(javaConfig, this.model.inputParams).readJsonAsParams(jerseyRequest);
  }

  private IProcessRequest createRestProcessRequest(Map<String, Object> parameters)
  {
    IWorkflowContext wc = this.injector.getInstance(IWorkflowContext.class);
    IWorkflowSession wfSession = wc.getWorkflowSession(this.injector.getInstance(ISession.class));
    IWorkflowProcessModelVersion wfPmv = this.pmv.getAdapter(IWorkflowProcessModelVersion.class);
    String startRequestPath = this.start.getRequestPath().getLinkPath();
    IProcessStart deployedStart = wfPmv.findProcessStart(startRequestPath);
    ITask newTask = wfSession.createTaskAndCase(deployedStart);

//    parameters.put("inputString", "where great things start");

    return (IProcessRequest) RequestFactory.createRootProcessRequest(wfPmv, startRequestPath, parameters, newTask, wfSession);
  }

  private Tuple executeWithBpmnEngine(IRequest request)
  {
    IResponse response = ResponseFactory.createRootResponse();
    try
    {
      IBpmEngineManager bpmEngineManager = this.injector.getInstance(IBpmEngineManager.class);
      IBpmEngine bpmEngine = bpmEngineManager.getBpmEngine(this.pmv);
      bpmEngine.handleRequest(request, response);
    }
    catch (RequestException ex)
    {
      throw new RuntimeException("Failed to handle REST request", ex);
    }
    return (Tuple)response.getParameter("Response");
  }

  private Map<String, Object> asMap(Tuple result, CompositeObject in)
  {
    Map<String, Object> params = new HashMap<>();
    mapResponseParams(params, in);
    if (result == null || result.isEmpty())
    {
      return params;
    }
    String[] arrayOfString;
    int j = (arrayOfString = result.getMemberNames()).length;
    for (int i = 0; i < j; i++)
    {
      String member = arrayOfString[i];
      try
      {
        Object object = result.get(member);
        params.put(member, object);
      }
      catch (NoSuchFieldException localNoSuchFieldException) {}
    }
    return params;
  }

  private static String asJson(Map<String, Object> params)
  {
    StringWriter writer = new StringWriter();
    try
    {
      new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(writer, params);
    }
    catch (IOException ex)
    {
      LOGGER.error("Failed to serialize REST call response to JSON", ex);
    }
    return writer.toString();
  }

  public ThirdPartyProgramStart getHandledProcessElement()
  {
    return this.start;
  }

  public IProcessModelVersion getPmv()
  {
    return this.pmv;
  }

  private void mapResponseParams(Map<String, Object> outParams, CompositeObject in)
  {
    // pmv.getProject()
    BpmIvyScriptExecutor outScriptExecutor = injector.getInstance(BpmIvyScriptExecutor.class);
    outScriptExecutor.withValueForInVariable(in);

    Mappings outMappings = new Mappings();
    for(Entry<String, String> mapping : model.dataToOutputMappings.entrySet())
    {
      outMappings = outMappings.add(new Mapping(mapping.getKey(), mapping.getValue()));
    }

    IIvyScriptClassRepository classRepo = injector.getInstance(IIvyScriptClassRepository.class);
    model.outputParams.forEach((name, typeName) ->
        outScriptExecutor.withVariable(classRepo.getIvyClassForName(typeName), name));

    IvyScriptCode inMappingScript = new IvyScriptCodeBuilder().append(outMappings).toScript();
    BpmIvyScriptResult result = outScriptExecutor.forScript(inMappingScript).execute();
    model.dataToOutputMappings.keySet().forEach(param ->
        outParams.put(param, result.getValueOfVariableName(param)));
  }
}
