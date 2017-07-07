[Ivy]
[>Created: Thu Jul 06 16:26:16 CEST 2017]
14E1BDB6375A93CE 3.20 #module
>Proto >Proto Collection #zClass
Es0 ExcelBeans Big #zClass
Es0 B #cInfo
Es0 #process
Es0 @TextInP .resExport .resExport #zField
Es0 @TextInP .type .type #zField
Es0 @TextInP .processKind .processKind #zField
Es0 @AnnotationInP-0n ai ai #zField
Es0 @MessageFlowInP-0n messageIn messageIn #zField
Es0 @MessageFlowOutP-0n messageOut messageOut #zField
Es0 @TextInP .xml .xml #zField
Es0 @TextInP .responsibility .responsibility #zField
Es0 @StartRequest f0 '' #zField
Es0 @EndTask f1 '' #zField
Es0 @ProgramInterface f3 '' #zField
Es0 @PushWFArc f2 '' #zField
Es0 @GridStep f5 '' #zField
Es0 @PushWFArc f6 '' #zField
Es0 @PushWFArc f4 '' #zField
Es0 @ProcessException f7 '' #zField
Es0 @EndTask f8 '' #zField
Es0 @PushWFArc f9 '' #zField
Es0 @EndTask f10 '' #zField
Es0 @GridStep f11 '' #zField
Es0 @ProgramInterface f12 '' #zField
Es0 @EndTask f13 '' #zField
Es0 @ProcessException f14 '' #zField
Es0 @StartRequest f15 '' #zField
Es0 @PushWFArc f16 '' #zField
Es0 @PushWFArc f17 '' #zField
Es0 @PushWFArc f18 '' #zField
Es0 @PushWFArc f19 '' #zField
>Proto Es0 Es0 ExcelBeans #zField
Es0 f0 outLink writeExcel.ivp #txt
Es0 f0 type p.Data #txt
Es0 f0 inParamDecl '<> param;' #txt
Es0 f0 actionDecl 'p.Data out;
' #txt
Es0 f0 guid 14E1AD2A75A11DB6 #txt
Es0 f0 requestEnabled true #txt
Es0 f0 triggerEnabled false #txt
Es0 f0 callSignature writeExcel() #txt
Es0 f0 persist false #txt
Es0 f0 startName 'writes a Recordset into an Excel file' #txt
Es0 f0 taskData 'TaskTriggered.ROL=Everybody
TaskTriggered.EXTYPE=0
TaskTriggered.EXPRI=2
TaskTriggered.TYPE=0
TaskTriggered.PRI=2
TaskTriggered.EXROL=Everybody' #txt
Es0 f0 caseData businessCase.attach=false #txt
Es0 f0 showInStartList 1 #txt
Es0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>writeExcel.ivp</name>
        <nameStyle>14,5,7
</nameStyle>
    </language>
</elementInfo>
' #txt
Es0 f0 @C|.responsibility Everybody #txt
Es0 f0 81 49 30 30 -21 17 #rect
Es0 f0 @|StartRequestIcon #fIcon
Es0 f1 type p.Data #txt
Es0 f1 497 49 30 30 0 15 #rect
Es0 f1 @|EndIcon #fIcon
Es0 f3 type p.Data #txt
Es0 f3 outerBean "ch.ivy.beans.WriteExcelBean" #txt
Es0 f3 timeout 0 #txt
Es0 f3 beanConfig '"#
#Thu Sep 29 15:11:14 CEST 2016
rs=in.rs
demo=
filepath=in.f.getAbsolutePath()
"' #txt
Es0 f3 exceptionHandler 14E1BDB6375A93CE-f7-buffer #txt
Es0 f3 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>WriteExcelBean</name>
        <nameStyle>14
</nameStyle>
    </language>
</elementInfo>
' #txt
Es0 f3 328 42 112 44 -43 -8 #rect
Es0 f3 @|ProgramInterfaceIcon #fIcon
Es0 f2 expr out #txt
Es0 f2 440 64 497 64 #arcP
Es0 f5 actionDecl 'p.Data out;
' #txt
Es0 f5 actionTable 'out=in;
out.f=new File("ivy.xls");
out.s="my.xls";
' #txt
Es0 f5 actionCode 'out.rs= new Recordset();
out.rs.addColumn("a1",["ad","sg"]);
out.rs.addColumn("a2",[45,1234]);
out.rs.addColumn("a3",[new Date(), new DateTime()]);
' #txt
Es0 f5 type p.Data #txt
Es0 f5 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>init</name>
        <nameStyle>4
</nameStyle>
    </language>
</elementInfo>
' #txt
Es0 f5 168 42 112 44 -8 -8 #rect
Es0 f5 @|StepIcon #fIcon
Es0 f6 expr out #txt
Es0 f6 111 64 168 64 #arcP
Es0 f4 expr out #txt
Es0 f4 280 64 328 64 #arcP
Es0 f7 .resExport export #txt
Es0 f7 actionDecl 'p1.Data out;
' #txt
Es0 f7 actionTable 'out=in;
' #txt
Es0 f7 actionCode ivy.log.debug(exception); #txt
Es0 f7 type p.Data #txt
Es0 f7 errorCode unspecified #txt
Es0 f7 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>ex</name>
        <nameStyle>2
</nameStyle>
    </language>
</elementInfo>
' #txt
Es0 f7 369 81 30 30 -6 17 #rect
Es0 f7 @|ExceptionIcon #fIcon
Es0 f8 type p.Data #txt
Es0 f8 497 145 30 30 0 15 #rect
Es0 f8 @|EndIcon #fIcon
Es0 f9 expr out #txt
Es0 f9 384 111 497 160 #arcP
Es0 f9 1 384 160 #addKink
Es0 f9 1 0.24760903604235218 0 0 #arcLabel
Es0 f10 type p.Data #txt
Es0 f10 497 273 30 30 0 15 #rect
Es0 f10 @|EndIcon #fIcon
Es0 f11 actionDecl 'p.Data out;
' #txt
Es0 f11 actionTable 'out=in;
out.f=new File("ivy.xls");
out.s="my.xls";
' #txt
Es0 f11 actionCode 'out.rs= new Recordset();
out.rs.addColumn("a1",["ad","sg"]);
out.rs.addColumn("a2",[45,1234]);
out.rs.addColumn("a3",[new Date(), new DateTime()]);
' #txt
Es0 f11 type p.Data #txt
Es0 f11 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>init</name>
        <nameStyle>4
</nameStyle>
    </language>
</elementInfo>
' #txt
Es0 f11 168 266 112 44 -8 -8 #rect
Es0 f11 @|StepIcon #fIcon
Es0 f12 type p.Data #txt
Es0 f12 outerBean "ch.ivy.beans.ReadExcelBean" #txt
Es0 f12 timeout 0 #txt
Es0 f12 beanConfig '"#
#Thu Sep 29 15:11:20 CEST 2016
rs=in.rs
filepath=in.f.getAbsolutePath()
demo=
"' #txt
Es0 f12 exceptionHandler 14E1BDB6375A93CE-f14-buffer #txt
Es0 f12 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>ReadExcelBean</name>
        <nameStyle>13
</nameStyle>
    </language>
</elementInfo>
' #txt
Es0 f12 328 266 112 44 -44 -8 #rect
Es0 f12 @|ProgramInterfaceIcon #fIcon
Es0 f13 type p.Data #txt
Es0 f13 497 369 30 30 0 15 #rect
Es0 f13 @|EndIcon #fIcon
Es0 f14 .resExport export #txt
Es0 f14 actionDecl 'p1.Data out;
' #txt
Es0 f14 actionTable 'out=in;
' #txt
Es0 f14 actionCode ivy.log.debug(exception); #txt
Es0 f14 type p.Data #txt
Es0 f14 errorCode unspecified #txt
Es0 f14 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>ex2</name>
        <nameStyle>3
</nameStyle>
    </language>
</elementInfo>
' #txt
Es0 f14 369 305 30 30 -9 17 #rect
Es0 f14 @|ExceptionIcon #fIcon
Es0 f15 outLink readExcel.ivp #txt
Es0 f15 type p.Data #txt
Es0 f15 inParamDecl '<> param;' #txt
Es0 f15 actionDecl 'p.Data out;
' #txt
Es0 f15 guid 14E1B9278A7BA6CD #txt
Es0 f15 requestEnabled true #txt
Es0 f15 triggerEnabled false #txt
Es0 f15 callSignature readExcel() #txt
Es0 f15 persist false #txt
Es0 f15 startName 'reads a Recordset from an Excel file' #txt
Es0 f15 taskData 'TaskTriggered.ROL=Everybody
TaskTriggered.EXTYPE=0
TaskTriggered.EXPRI=2
TaskTriggered.TYPE=0
TaskTriggered.PRI=2
TaskTriggered.EXROL=Everybody' #txt
Es0 f15 caseData businessCase.attach=false #txt
Es0 f15 showInStartList 1 #txt
Es0 f15 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>readExcel.ivp</name>
        <nameStyle>13,5,7
</nameStyle>
    </language>
</elementInfo>
' #txt
Es0 f15 @C|.responsibility Everybody #txt
Es0 f15 81 273 30 30 -24 17 #rect
Es0 f15 @|StartRequestIcon #fIcon
Es0 f16 expr out #txt
Es0 f16 440 288 497 288 #arcP
Es0 f17 expr out #txt
Es0 f17 111 288 168 288 #arcP
Es0 f18 expr out #txt
Es0 f18 280 288 328 288 #arcP
Es0 f19 expr out #txt
Es0 f19 384 335 497 384 #arcP
Es0 f19 1 384 384 #addKink
Es0 f19 1 0.24760903604235218 0 0 #arcLabel
>Proto Es0 .type p.Data #txt
>Proto Es0 .processKind NORMAL #txt
>Proto Es0 0 0 32 24 18 0 #rect
>Proto Es0 @|BIcon #fIcon
Es0 f3 mainOut f2 tail #connect
Es0 f2 head f1 mainIn #connect
Es0 f0 mainOut f6 tail #connect
Es0 f6 head f5 mainIn #connect
Es0 f5 mainOut f4 tail #connect
Es0 f4 head f3 mainIn #connect
Es0 f7 mainOut f9 tail #connect
Es0 f9 head f8 mainIn #connect
Es0 f12 mainOut f16 tail #connect
Es0 f16 head f10 mainIn #connect
Es0 f15 mainOut f17 tail #connect
Es0 f17 head f11 mainIn #connect
Es0 f11 mainOut f18 tail #connect
Es0 f18 head f12 mainIn #connect
Es0 f14 mainOut f19 tail #connect
Es0 f19 head f13 mainIn #connect
