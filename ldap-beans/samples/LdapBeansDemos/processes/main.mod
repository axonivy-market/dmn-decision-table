[Ivy]
[>Created: Thu Jul 06 12:10:23 CEST 2017]
15D0DFF8BC5B24FD 3.20 #module
>Proto >Proto Collection #zClass
mn0 main Big #zClass
mn0 B #cInfo
mn0 #process
mn0 @TextInP .resExport .resExport #zField
mn0 @TextInP .type .type #zField
mn0 @TextInP .processKind .processKind #zField
mn0 @AnnotationInP-0n ai ai #zField
mn0 @MessageFlowInP-0n messageIn messageIn #zField
mn0 @MessageFlowOutP-0n messageOut messageOut #zField
mn0 @TextInP .xml .xml #zField
mn0 @TextInP .responsibility .responsibility #zField
mn0 @StartRequest f0 '' #zField
mn0 @EndTask f1 '' #zField
mn0 @ProgramInterface f3 '' #zField
mn0 @PushWFArc f4 '' #zField
mn0 @GridStep f5 '' #zField
mn0 @PushWFArc f6 '' #zField
mn0 @PushWFArc f2 '' #zField
>Proto mn0 mn0 main #zField
mn0 f0 outLink queryUserMail.ivp #txt
mn0 f0 type com.axonivy.ivy.supplements.Data #txt
mn0 f0 inParamDecl '<> param;' #txt
mn0 f0 actionDecl 'com.axonivy.ivy.supplements.Data out;
' #txt
mn0 f0 guid 15D0DFF8BD7CBDD4 #txt
mn0 f0 requestEnabled true #txt
mn0 f0 triggerEnabled false #txt
mn0 f0 callSignature queryUserMail() #txt
mn0 f0 persist false #txt
mn0 f0 startName 'queries E-Mail adresses of all users within an Organisation Unit' #txt
mn0 f0 taskData 'TaskTriggered.ROL=Everybody
TaskTriggered.EXTYPE=0
TaskTriggered.EXPRI=2
TaskTriggered.TYPE=0
TaskTriggered.PRI=2
TaskTriggered.EXROL=Everybody' #txt
mn0 f0 caseData businessCase.attach=true #txt
mn0 f0 showInStartList 1 #txt
mn0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>queryUserMail.ivp</name>
        <nameStyle>17,5,7
</nameStyle>
    </language>
</elementInfo>
' #txt
mn0 f0 @C|.responsibility Everybody #txt
mn0 f0 81 49 30 30 -54 17 #rect
mn0 f0 @|StartRequestIcon #fIcon
mn0 f1 type com.axonivy.ivy.supplements.Data #txt
mn0 f1 497 49 30 30 0 15 #rect
mn0 f1 @|EndIcon #fIcon
mn0 f3 type com.axonivy.ivy.supplements.Data #txt
mn0 f3 outerBean "ch.ivyteam.ivy.ldap.beans.LdapQueryBean" #txt
mn0 f3 timeout 0 #txt
mn0 f3 beanConfig '"#
#Thu Jul 06 12:10:02 CEST 2017
search_root_object=OU\\=IvyTeam Test-OU,DC\\=zugtstdomain,DC\\=wan
result_return=all
server_provider=Microsoft Active Directory
search_filter_value_0=user
search_filter_attribute_0=objectClass
server_authkind=simple
server_password=nimda
result_include_name2=true
search_scope=oneLevel
result_include_name=false
result_ivyGrid_attribute=result
server_context=
server_useSsl=false
result_ivyGrid_name_attribute=
result_attribute_attribute_0=mail
server_username=cn\\=admin,cn\\=Users,dc\\=zugtstdomain,dc\\=wan
server_url=ldap\\://zugtstdirads\\:389
"' #txt
mn0 f3 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>query ldap</name>
        <desc>queries E-Mail adresses of all users within an Organisation Unit</desc>
    </language>
</elementInfo>
' #txt
mn0 f3 168 42 112 44 -33 -7 #rect
mn0 f3 @|ProgramInterfaceIcon #fIcon
mn0 f4 expr out #txt
mn0 f4 111 64 168 64 #arcP
mn0 f5 actionDecl 'com.axonivy.ivy.supplements.Data out;
' #txt
mn0 f5 actionTable 'out=in;
' #txt
mn0 f5 actionCode 'ivy.log.info("Found mail adresses: "+in.result.getColumn("mail"));' #txt
mn0 f5 type com.axonivy.ivy.supplements.Data #txt
mn0 f5 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>log 
mail adresses</name>
        <nameStyle>18
</nameStyle>
    </language>
</elementInfo>
' #txt
mn0 f5 320 42 128 44 -41 -15 #rect
mn0 f5 @|StepIcon #fIcon
mn0 f6 expr out #txt
mn0 f6 280 64 320 64 #arcP
mn0 f2 expr out #txt
mn0 f2 448 64 497 64 #arcP
>Proto mn0 .type com.axonivy.ivy.supplements.Data #txt
>Proto mn0 .processKind NORMAL #txt
>Proto mn0 0 0 32 24 18 0 #rect
>Proto mn0 @|BIcon #fIcon
mn0 f0 mainOut f4 tail #connect
mn0 f4 head f3 mainIn #connect
mn0 f3 mainOut f6 tail #connect
mn0 f6 head f5 mainIn #connect
mn0 f5 mainOut f2 tail #connect
mn0 f2 head f1 mainIn #connect
