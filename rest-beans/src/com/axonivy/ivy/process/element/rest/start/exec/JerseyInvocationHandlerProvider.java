package com.axonivy.ivy.process.element.rest.start.exec;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;

import com.axonivy.ivy.process.element.rest.start.RestStartElement;
import com.axonivy.ivy.process.element.rest.start.ui.RestServiceModel;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.di.restricted.DiInjector;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.process.IProcess;
import ch.ivyteam.ivy.process.IProcessManager;
import ch.ivyteam.ivy.process.ProcessNavigationUtil;
import ch.ivyteam.ivy.process.model.element.event.start.ThirdPartyProgramStart;
import ch.ivyteam.ivy.security.exec.Sudo;
import ch.ivyteam.ivy.webserver.internal.rest.RestResourceConfig;
import ch.ivyteam.ivy.webserver.rest.RestConfigurer;

public class JerseyInvocationHandlerProvider implements RestConfigurer
{
  @Inject
  private DiInjector injector;
  
  @Override
  public void configure(RestResourceConfig config, ServiceLocator locator) throws WebApplicationException
  {
    IProcessManager manager = DiCore.getGlobalInjector().getInstance(IProcessManager.class);
    Sudo.exec(() -> {
      try
      { 
        manager.getProjectDataModels().stream().forEach(processManager -> processManager.search().type(ThirdPartyProgramStart.class)
                .and(start -> RestStartElement.class.getName().equals(start.getJavaClass().getClassName()))
                .findDeep().forEach(start -> registerRestResource(start, config)));
      }
      catch (Exception ex)
      {
        throw new WebApplicationException(ex);
      }
    });
  }

  private ResourceConfig registerRestResource(ThirdPartyProgramStart start, RestResourceConfig config)
  {
    return config.registerResources(restStartResource(start));
  }

  private Resource restStartResource(ThirdPartyProgramStart start)
  {
    Resource.Builder resourceBuilder = Resource.builder();
    resourceBuilder.path("designer/process/"+start.getName());
    buildMethod(resourceBuilder, start);
    return resourceBuilder.build();
  }
  
  private void buildMethod(Resource.Builder resourceBuilder, ThirdPartyProgramStart start)
  {
    RestServiceModel model = RestServiceModel.load(start.getUserConfig());
    Resource.Builder child = resourceBuilder.addChildResource(model.signature);
    ResourceMethod.Builder method = method(child, model);
    if (!model.inputParams.isEmpty()) {
      method.consumes(new MediaType[] { MediaType.APPLICATION_JSON_TYPE });
    }
    if (!model.outputParams.isEmpty()) {
      method.produces(new MediaType[] { MediaType.APPLICATION_JSON_TYPE });
    }
      
    IProcess process = ProcessNavigationUtil.getProcess(start);
    IProcessModelVersion pmv = ProcessNavigationUtil.getProcessModelVersion(process);
    method.handledBy(this.injector.getInstance(RestProcessRequestHandler.class).configure(pmv, start, model));
  }
  
  private ResourceMethod.Builder method(Resource.Builder methodResource, RestServiceModel model)
  {
    if (model.inputParams.isEmpty()) {
      return methodResource.addMethod("GET");
    }
    return methodResource.addMethod("POST");
  }
}
