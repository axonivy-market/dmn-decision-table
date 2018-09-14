package com.axonivy.ivy.process.element.rest.start;

import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBeanRuntime;

public class RestStartElement extends AbstractProcessStartEventBean
{
  public RestStartElement()
  {
    super("RestStartEvent", "This Bean start a RESTful Service on the Engine.");
  }
  
  @Override
  public void initialize(IProcessStartEventBeanRuntime _eventRuntime, String _configuration)
  {
    //_eventRuntime.fireProcessStartEventRequest(session, firingReason, requestParameters)
    // TODO Auto-generated method stub
    super.initialize(_eventRuntime, _configuration);
  }
}
