[Ivy]
[>Created: Fri Jul 07 09:41:16 CEST 2017]
15D1BF74903D053E 3.20 #module
>Proto >Proto Collection #zClass
Td0 Timed Big #zClass
Td0 B #cInfo
Td0 #process
Td0 @TextInP .resExport .resExport #zField
Td0 @TextInP .type .type #zField
Td0 @TextInP .processKind .processKind #zField
Td0 @AnnotationInP-0n ai ai #zField
Td0 @MessageFlowInP-0n messageIn messageIn #zField
Td0 @MessageFlowOutP-0n messageOut messageOut #zField
Td0 @TextInP .xml .xml #zField
Td0 @TextInP .responsibility .responsibility #zField
Td0 @EndTask f1 '' #zField
Td0 @StartEvent f3 '' #zField
Td0 @GridStep f0 '' #zField
Td0 @PushWFArc f2 '' #zField
Td0 @PushWFArc f4 '' #zField
>Proto Td0 Td0 Timed #zField
Td0 f1 type time.start.event.beans.Data #txt
Td0 f1 337 49 30 30 0 15 #rect
Td0 f1 @|EndIcon #fIcon
Td0 f3 outerBean "ch.ivy.beans.TimedStartEventBean" #txt
Td0 f3 beanConfig '"#TimedStartEventBeanProperties
#Fri Jul 07 09:41:15 CEST 2017
hour=03
immediately=false
day=2
interval=36000
minute=52
"' #txt
Td0 f3 outLink eventLink.ivp #txt
Td0 f3 type time.start.event.beans.Data #txt
Td0 f3 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>batch convert</name>
        <nameStyle>13
</nameStyle>
    </language>
</elementInfo>
' #txt
Td0 f3 @C|.responsibility Everybody #txt
Td0 f3 81 49 30 30 -43 17 #rect
Td0 f3 @|StartEventIcon #fIcon
Td0 f0 actionDecl 'time.start.event.beans.Data out;
' #txt
Td0 f0 actionTable 'out=in;
' #txt
Td0 f0 actionCode 'ivy.log.info("process started by timed interval");' #txt
Td0 f0 type time.start.event.beans.Data #txt
Td0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>read files</name>
        <nameStyle>10
</nameStyle>
    </language>
</elementInfo>
' #txt
Td0 f0 168 42 112 44 -29 -7 #rect
Td0 f0 @|StepIcon #fIcon
Td0 f2 expr out #txt
Td0 f2 111 64 168 64 #arcP
Td0 f4 expr out #txt
Td0 f4 280 64 337 64 #arcP
>Proto Td0 .type time.start.event.beans.Data #txt
>Proto Td0 .processKind NORMAL #txt
>Proto Td0 0 0 32 24 18 0 #rect
>Proto Td0 @|BIcon #fIcon
Td0 f3 mainOut f2 tail #connect
Td0 f2 head f0 mainIn #connect
Td0 f0 mainOut f4 tail #connect
Td0 f4 head f1 mainIn #connect
