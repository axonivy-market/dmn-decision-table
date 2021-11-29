[Ivy]
15C82BA28CB7DA31 9.3.1 #module
>Proto >Proto Collection #zClass
Ts0 TaxesDialogProcess Big #zClass
Ts0 RD #cInfo
Ts0 #process
Ts0 @TextInP .type .type #zField
Ts0 @TextInP .processKind .processKind #zField
Ts0 @AnnotationInP-0n ai ai #zField
Ts0 @MessageFlowInP-0n messageIn messageIn #zField
Ts0 @MessageFlowOutP-0n messageOut messageOut #zField
Ts0 @TextInP .xml .xml #zField
Ts0 @TextInP .responsibility .responsibility #zField
Ts0 @UdInit f0 '' #zField
Ts0 @UdProcessEnd f1 '' #zField
Ts0 @PushWFArc f2 '' #zField
Ts0 @UdEvent f3 '' #zField
Ts0 @UdExitEnd f4 '' #zField
Ts0 @PushWFArc f5 '' #zField
>Proto Ts0 Ts0 TaxesDialogProcess #zField
Ts0 f0 guid 15C82BA28D361599 #txt
Ts0 f0 method start(com.axonivy.ivy.supplements.rule.beans.demos.Data) #txt
Ts0 f0 inParameterDecl '<com.axonivy.ivy.supplements.rule.beans.demos.Data data> param;' #txt
Ts0 f0 inParameterMapAction 'out.data=param.data;
' #txt
Ts0 f0 outParameterDecl '<> result;' #txt
Ts0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>start(Data)</name>
    </language>
</elementInfo>
' #txt
Ts0 f0 83 51 26 26 -35 15 #rect
Ts0 f1 211 51 26 26 0 12 #rect
Ts0 f2 expr out #txt
Ts0 f2 109 64 211 64 #arcP
Ts0 f3 guid 15C82BA28E738744 #txt
Ts0 f3 actionTable 'out=in;
' #txt
Ts0 f3 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>close</name>
    </language>
</elementInfo>
' #txt
Ts0 f3 83 147 26 26 -16 12 #rect
Ts0 f4 211 147 26 26 0 12 #rect
Ts0 f5 expr out #txt
Ts0 f5 109 160 211 160 #arcP
>Proto Ts0 .type com.axonivy.ivy.supplements.rule.beans.demos.TaxesDialog.TaxesDialogData #txt
>Proto Ts0 .processKind HTML_DIALOG #txt
>Proto Ts0 -8 -8 16 16 16 26 #rect
Ts0 f0 mainOut f2 tail #connect
Ts0 f2 head f1 mainIn #connect
Ts0 f3 mainOut f5 tail #connect
Ts0 f5 head f4 mainIn #connect
