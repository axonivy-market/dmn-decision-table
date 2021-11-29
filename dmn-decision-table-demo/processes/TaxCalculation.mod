[Ivy]
15C82A89FA49E8E6 9.3.1 #module
>Proto >Proto Collection #zClass
Tn0 TaxCalculation Big #zClass
Tn0 B #cInfo
Tn0 #process
Tn0 @TextInP .type .type #zField
Tn0 @TextInP .processKind .processKind #zField
Tn0 @AnnotationInP-0n ai ai #zField
Tn0 @MessageFlowInP-0n messageIn messageIn #zField
Tn0 @MessageFlowOutP-0n messageOut messageOut #zField
Tn0 @TextInP .xml .xml #zField
Tn0 @TextInP .responsibility .responsibility #zField
Tn0 @StartRequest f0 '' #zField
Tn0 @EndTask f1 '' #zField
Tn0 @DecisionActivity f3 '' #zField
Tn0 @UserDialog f5 '' #zField
Tn0 @PushWFArc f6 '' #zField
Tn0 @RestClientCall f7 '' #zField
Tn0 @PushWFArc f8 '' #zField
Tn0 @UserDialog f9 '' #zField
Tn0 @PushWFArc f10 '' #zField
Tn0 @PushWFArc f2 '' #zField
Tn0 @PushWFArc f4 '' #zField
>Proto Tn0 Tn0 TaxCalculation #zField
Tn0 f0 outLink startDecisionTable.ivp #txt
Tn0 f0 inParamDecl '<> param;' #txt
Tn0 f0 requestEnabled true #txt
Tn0 f0 triggerEnabled false #txt
Tn0 f0 callSignature startDecisionTable() #txt
Tn0 f0 persist false #txt
Tn0 f0 taskData 'TaskTriggered.EXPRI=2
TaskTriggered.EXROL=Everybody
TaskTriggered.EXTYPE=0
TaskTriggered.PRI=2
TaskTriggered.ROL=Everybody
TaskTriggered.TYPE=0' #txt
Tn0 f0 caseData 'businessCase.attach=true
customFields.STRING.ProcessCategoryCode="table"' #txt
Tn0 f0 showInStartList 1 #txt
Tn0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>startDecisionTable</name>
        <nameStyle>18,5,7
</nameStyle>
    </language>
</elementInfo>
' #txt
Tn0 f0 @C|.responsibility Everybody #txt
Tn0 f0 105 97 30 30 -25 17 #rect
Tn0 f1 993 97 30 30 0 15 #rect
Tn0 f3 @AbstractThirdPartyProgramInterface|type com.axonivy.ivy.supplements.rule.beans.demos.Data #txt
Tn0 f3 @AbstractThirdPartyProgramInterface|timeout 0 #txt
Tn0 f3 @AbstractThirdPartyProgramInterface|beanConfig '"{
  ""conditionColumns"" : [ {
    ""attributeName"" : ""in.yearlyIncomeDollars"",
    ""type"" : ""Number""
  }, {
    ""attributeName"" : ""in.yearlyIncomeDollars"",
    ""type"" : ""Number""
  }, {
    ""attributeName"" : ""in.person.gender"",
    ""type"" : ""String""
  } ],
  ""actionColumns"" : [ {
    ""attributeName"" : ""out.taxrate"",
    ""type"" : ""Number""
  } ],
  ""rows"" : [ {
    ""cells"" : [ {
      ""conditionCell"" : {
        ""operator"" : ""EQUAL_OR_GREATER"",
        ""arguments"" : [ ""0"" ]
      }
    }, {
      ""conditionCell"" : {
        ""operator"" : ""LESS"",
        ""arguments"" : [ ""50000"" ]
      }
    }, {
      ""conditionCell"" : {
        ""operator"" : ""NO_CONDITION"",
        ""arguments"" : [ ]
      }
    }, {
      ""valueCell"" : {
        ""value"" : ""0""
      }
    } ]
  }, {
    ""cells"" : [ {
      ""conditionCell"" : {
        ""operator"" : ""EQUAL_OR_GREATER"",
        ""arguments"" : [ ""50000"" ]
      }
    }, {
      ""conditionCell"" : {
        ""operator"" : ""LESS"",
        ""arguments"" : [ ""250000"" ]
      }
    }, {
      ""conditionCell"" : {
        ""operator"" : ""EQUAL"",
        ""arguments"" : [ ""male"" ]
      }
    }, {
      ""valueCell"" : {
        ""value"" : ""15""
      }
    } ]
  }, {
    ""cells"" : [ {
      ""conditionCell"" : {
        ""operator"" : ""EQUAL_OR_GREATER"",
        ""arguments"" : [ ""250000"" ]
      }
    }, {
      ""conditionCell"" : {
        ""operator"" : ""NO_CONDITION"",
        ""arguments"" : [ ]
      }
    }, {
      ""conditionCell"" : {
        ""operator"" : ""EQUAL"",
        ""arguments"" : [ ""male"" ]
      }
    }, {
      ""valueCell"" : {
        ""value"" : ""30""
      }
    } ]
  }, {
    ""cells"" : [ {
      ""conditionCell"" : {
        ""operator"" : ""EQUAL_OR_GREATER"",
        ""arguments"" : [ ""50000"" ]
      }
    }, {
      ""conditionCell"" : {
        ""operator"" : ""LESS"",
        ""arguments"" : [ ""250000"" ]
      }
    }, {
      ""conditionCell"" : {
        ""operator"" : ""EQUAL"",
        ""arguments"" : [ ""female"" ]
      }
    }, {
      ""valueCell"" : {
        ""value"" : ""10""
      }
    } ]
  }, {
    ""cells"" : [ {
      ""conditionCell"" : {
        ""operator"" : ""EQUAL_OR_GREATER"",
        ""arguments"" : [ ""250000"" ]
      }
    }, {
      ""conditionCell"" : {
        ""operator"" : ""NO_CONDITION"",
        ""arguments"" : [ ]
      }
    }, {
      ""conditionCell"" : {
        ""operator"" : ""EQUAL"",
        ""arguments"" : [ ""female"" ]
      }
    }, {
      ""valueCell"" : {
        ""value"" : ""20""
      }
    } ]
  } ]
}"' #txt
Tn0 f3 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>evaluate taxes with decision table</name>
    </language>
</elementInfo>
' #txt
Tn0 f3 556 84 216 56 -91 -8 #rect
Tn0 f5 dialogId com.axonivy.ivy.supplements.rule.beans.demos.GatherPersonData #txt
Tn0 f5 startMethod start() #txt
Tn0 f5 requestActionDecl '<> param;' #txt
Tn0 f5 responseActionDecl 'com.axonivy.ivy.supplements.rule.beans.demos.Data out;
' #txt
Tn0 f5 responseMappingAction 'out=result.data;
' #txt
Tn0 f5 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>data gathering</name>
        <nameStyle>14,7
</nameStyle>
    </language>
</elementInfo>
' #txt
Tn0 f5 208 90 112 44 -39 -8 #rect
Tn0 f6 expr out #txt
Tn0 f6 135 112 208 112 #arcP
Tn0 f6 0 0.6169741941115735 0 0 #arcLabel
Tn0 f7 clientId 9b19d52e-3e7a-4bec-9fae-9fdeee8b2535 #txt
Tn0 f7 queryParams 'name=in.person.firstname;
' #txt
Tn0 f7 resultType com.fasterxml.jackson.databind.JsonNode #txt
Tn0 f7 responseCode 'out.person.gender = result.get("gender").asText();' #txt
Tn0 f7 clientErrorCode ivy:error:rest:client #txt
Tn0 f7 statusErrorCode '>> Ignore status' #txt
Tn0 f7 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>evaluate gender</name>
    </language>
</elementInfo>
' #txt
Tn0 f7 368 90 112 44 -51 -7 #rect
Tn0 f8 expr out #txt
Tn0 f8 320 112 368 112 #arcP
Tn0 f9 dialogId com.axonivy.ivy.supplements.rule.beans.demos.TaxesDialog #txt
Tn0 f9 startMethod start(com.axonivy.ivy.supplements.rule.beans.demos.Data) #txt
Tn0 f9 requestActionDecl '<com.axonivy.ivy.supplements.rule.beans.demos.Data data> param;' #txt
Tn0 f9 requestMappingAction 'param.data=in;
' #txt
Tn0 f9 responseActionDecl 'com.axonivy.ivy.supplements.rule.beans.demos.Data out;
' #txt
Tn0 f9 responseMappingAction 'out=in;
' #txt
Tn0 f9 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>display taxes</name>
        <nameStyle>13
</nameStyle>
    </language>
</elementInfo>
' #txt
Tn0 f9 832 90 112 44 -40 -7 #rect
Tn0 f10 expr out #txt
Tn0 f10 772 112 832 112 #arcP
Tn0 f10 0 0.6898527322635233 0 0 #arcLabel
Tn0 f2 expr out #txt
Tn0 f2 944 112 993 112 #arcP
Tn0 f4 480 112 556 112 #arcP
>Proto Tn0 .type com.axonivy.ivy.supplements.rule.beans.demos.Data #txt
>Proto Tn0 .processKind NORMAL #txt
>Proto Tn0 0 0 32 24 18 0 #rect
>Proto Tn0 @|BIcon #fIcon
Tn0 f0 mainOut f6 tail #connect
Tn0 f6 head f5 mainIn #connect
Tn0 f5 mainOut f8 tail #connect
Tn0 f8 head f7 mainIn #connect
Tn0 f3 @AbstractThirdPartyProgramInterface|mainOut f10 tail #connect
Tn0 f10 head f9 mainIn #connect
Tn0 f9 mainOut f2 tail #connect
Tn0 f2 head f1 mainIn #connect
Tn0 f7 mainOut f4 tail #connect
Tn0 f4 head f3 @AbstractThirdPartyProgramInterface|mainIn #connect
