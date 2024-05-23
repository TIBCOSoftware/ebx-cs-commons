# README #

This repository hosts the CS Commons Library for TIBCO EBX®.
This library includes utility methods and components destined to speed up a TIBCO EBX® project implementation and is based on the experience of Professional Services and Customer Success teams of TIBCO Orchestra Networks (part of Cloud Software Group).

Three main versions of CS Commons Library are hosted:
* branch "main" - EBX 6.0 and 6.1 compatible version based on Javax Servlet API
* branch "jakarta-6.2" - EBX 6.2+ compatible version based on Jakarta Servlet API
* branch "ebx-5.9" - EBX 5.9 compatible version


## Built With

* [Java](https://www.java.com)
* [Maven](https://maven.apache.org/)
* [TIBCO EBX®](https://docs.tibco.com/pub/ebx/latest/doc/html/en/index.html)

## Integration ##

To add CS Commons Library into your project you need to add its jars and war as dependency.
Note that you only need the war file deployed if you want to use the Java Beans generator that the CS Commons Library provide.

## Third-party libraries ##

CS Commons Library project relies on the following third-party dependencies: 

- [Apache Commons Lang3](https://commons.apache.org/proper/commons-lang/)
- [Apache Commons Validator](https://commons.apache.org/proper/commons-validator/)
- [Apache Commons Exec](https://commons.apache.org/proper/commons-exec/)
- [Javax JMS](https://mvnrepository.com/artifact/javax.jms)
- [Javax Servlet API](https://mvnrepository.com/artifact/javax.servlet/javax.servlet-api)


## Project structure ##

The project is structured into 7 maven modules hosting different types of utilities:
- ebx-cs-commons-addons - the module hosting utilities method extending functionalities of the official add-ons for TIBCO EBX®.
- ebx-cs-commons-beans - the module hosting Java Beans parent classes and definitions.
- ebx-cs-commons-lib - the module hostring common utilities methods to work with TIBCO EBX® objects.
- ebx-cs-commons-components - the module hosting different data model components, such as Triggers, ValueFunction, Constraints, AccessRules etc.
- ebx-cs-commons-query - the module implementing utility methods to simplify writing TIBCO EBX® SQL Queries in your Java code.
- ebx-cs-commons-ui - the module hosting UI extensions such as Widget, UIForm, UserService.
- ebx-cs-commons-web - the web application of the CS Commons Library including Java Beans generator.

All projects have Javadoc available to help you use CS Commons Library utilities in your projects.

### ebx-cs-commons-addons ###

In this module you can find utilities for two TIBCO EBX® add-ons: 

- Digital Asset manager - DamaUtils.java - simplifies accessing Digital Assets from Java API
- Information Search - SearchAjaxComponent - provides a visual Ajax component to display results of TESE search. 

### ebx-cs-commons-beans ###

This module include parent classes and model for two approaches to the Java Bean generation for TIBCO EBX® data models.
1. com.tibco.ebx.cs.commons.beans.adapter package and its subpackages provides an implementation for annotations based Java beans that load data from the EBX object with an eager loader. 
2. com.tibco.ebx.cs.commons.beans.generator package and its subpackages provides implementation related to the CS Commons Java Bean Generator where all layers of Java accessors are generated per TIBCO EBX® table based on the configuration done inside CS Commons Library technical dataset.

### ebx-cs-commons-components ###

Different components (data model and workflow model) are gathered inside this package. They can either be directly used in your models or might be extended or implemented if required to add custom behaviour. 
More details are available in the Javadocs.

### ebx-cs-commons-lib ###

The lib module includes utility methods and frameworks covering the following topics:

- Specific exceptions framework (com.tibco.ebx.cs.commons.lib.exception)
- Messages framework for easier internationalization of your custom code (com.tibco.ebx.cs.commons.lib.message)
- Common procedures to create/update/delete records (com.tibco.ebx.cs.commons.procedure)
- Repository utils to retrieve dataspaces, datasets and records through TIBCO EBX® native links (com.tibco.ebx.cs.commons.lib.repository, com.tibco.ebx.cs.commons.lib.utils)

### ebx-cs-commons-query ###

This module provides a Java framework to easily write TIBCO EBX® Queries.
In your project you can initiate the EBXQueryBuilder object and add there conditions and fields to build the full query. 
Note that this module is only available for EBX 6.0/6.1/6.2+ versions of the CS Commons Library.

### ebx-cs-commons-ui ###

The UI module groups several visual components that you can use in TIBCO EBX®: widgets, labels, user services. 
You can either directly reference those in your custom module or you can also extend the existing classes to add customization. 

### ebx-cs-commons-web ###

This is the main web application for the CS Commons Library. 
This package is needed if you want to use the Java accessors genarator service to create Java accessor layers for your data model to work with Java native objects rather than with TIBCO EBX® internal API. 

In other cases you don't need to deploy the war file for the module - jar files are sufficient for API usage.
