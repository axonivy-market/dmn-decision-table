[Ivy]
15C82B07277A094A 7.5.0 #module
>Proto >Proto Collection #zClass
Gs0 GatherPersonDataProcess Big #zClass
Gs0 RD #cInfo
Gs0 #process
Gs0 @TextInP .type .type #zField
Gs0 @TextInP .processKind .processKind #zField
Gs0 @AnnotationInP-0n ai ai #zField
Gs0 @MessageFlowInP-0n messageIn messageIn #zField
Gs0 @MessageFlowOutP-0n messageOut messageOut #zField
Gs0 @TextInP .xml .xml #zField
Gs0 @TextInP .responsibility .responsibility #zField
Gs0 @UdInit f0 '' #zField
Gs0 @UdProcessEnd f1 '' #zField
Gs0 @UdEvent f3 '' #zField
Gs0 @UdExitEnd f4 '' #zField
Gs0 @PushWFArc f5 '' #zField
Gs0 @GridStep f6 '' #zField
Gs0 @PushWFArc f7 '' #zField
Gs0 @PushWFArc f2 '' #zField
>Proto Gs0 Gs0 GatherPersonDataProcess #zField
Gs0 f0 guid 15C82B07284E411B #txt
Gs0 f0 method start() #txt
Gs0 f0 inParameterDecl '<> param;' #txt
Gs0 f0 outParameterDecl '<com.axonivy.ivy.supplements.rule.beans.demos.Data data> result;' #txt
Gs0 f0 outParameterMapAction 'result.data=in.data;
' #txt
Gs0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>start()</name>
    </language>
</elementInfo>
' #txt
Gs0 f0 83 51 26 26 -20 15 #rect
Gs0 f0 @|UdInitIcon #fIcon
Gs0 f1 339 51 26 26 0 12 #rect
Gs0 f1 @|UdProcessEndIcon #fIcon
Gs0 f3 guid 15C82B0729B4C2B9 #txt
Gs0 f3 actionTable 'out=in;
' #txt
Gs0 f3 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>close</name>
    </language>
</elementInfo>
' #txt
Gs0 f3 83 147 26 26 -16 12 #rect
Gs0 f3 @|UdEventIcon #fIcon
Gs0 f4 211 147 26 26 0 12 #rect
Gs0 f4 @|UdExitEndIcon #fIcon
Gs0 f5 expr out #txt
Gs0 f5 109 160 211 160 #arcP
Gs0 f6 actionTable 'out=in;
out.data.isGoldMember=true;
out.data.person.age=30;
out.data.person.firstname="Peter";
out.data.person.lastname="Stöckli";
out.data.yearlyIncomeDollars=100000;
' #txt
Gs0 f6 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>sample data</name>
        <nameStyle>11
</nameStyle>
    </language>
</elementInfo>
' #txt
Gs0 f6 168 42 112 44 -39 -7 #rect
Gs0 f6 @|StepIcon #fIcon
Gs0 f7 expr out #txt
Gs0 f7 109 64 168 64 #arcP
Gs0 f2 expr out #txt
Gs0 f2 280 64 339 64 #arcP
>Proto Gs0 .type com.axonivy.ivy.supplements.rule.beans.demos.GatherPersonData.GatherPersonDataData #txt
>Proto Gs0 .processKind HTML_DIALOG #txt
>Proto Gs0 -8 -8 16 16 16 26 #rect
>Proto Gs0 '' #fIcon
Gs0 f3 mainOut f5 tail #connect
Gs0 f5 head f4 mainIn #connect
Gs0 f0 mainOut f7 tail #connect
Gs0 f7 head f6 mainIn #connect
Gs0 f6 mainOut f2 tail #connect
Gs0 f2 head f1 mainIn #connect
