# Decision Table

![CI Build](https://github.com/axonivy-market/decision-table/actions/workflows/ci.yml/badge.svg)
Enables you to apply rules and decisions on your data without scripting effort.

## Decision Table Activity
Simple and expressive decision table element. 
![Process with Decision Table](decision-table-demo/screenshots/decisionTableInAction.png)

### DMN
Exposes the decision in standard [DMN](http://www.omg.org/spec/DMN/) format so that it can be run and edited in any DMN engine.
![DMN XML](decision-table-demo/screenshots/decisionActivity_dmnTab.png)

### Edit
Convenient condition editing with zero scripting
![Condition Editing](decision-table-demo/screenshots/decisionTable_editCondition.png)

## Setup
1. Download the `supplement.rules.beans-X.Y.Z-SNAPSHOT.jar` from the [latest release](https://github.com/ivy-supplements/bpm-beans/releases/latest)
2. Copy the downloaded JAR into the `dropins` directory of your Axon Ivy Designer
4. Open a process and use the additional beans provided in the `Rules` drawer of Process Editor palette
