# README #

This repository host a CS Commons library for TIBCO EBX.
This library includes utility methods and components destined to speed up an EBX project implementation and is based on the experience of Professional Services and Customer Success teams of TIBCO Orchestra Networks (part of Cloud Software Group).

## Built With

* [Java](https://www.java.com)
* [Maven](https://maven.apache.org/)
* [EBX](https://docs.tibco.com/pub/ebx/latest/doc/html/en/index.html)

### Project structure ###

The project is structured into 7 maven modules hosting different types of utilities:
- ebx-cs-commons-addons - the module hosting utilities method extending functionalities of the official add-ons for TIBCO EBX.
- ebx-cs-commons-beans - the module hosting Java Beans parent classes and definitions.
- ebx-cs-commons-lib - the module hostring common utilities methods to work with EBX objects.
- ebx-cs-commons-components - the module hosting different data model components, such as Triggers, ValueFunction, Constraints, AccessRules etc.
- ebx-cs-commons-query - the module implementing utility methods to simplify writing EBX SQL Queries in your Java code.
- ebx-cs-commons-ui - the module hosting UI extensions such as Widget, UIForm, UserService.
- ebx-cs-commons-web - the web application of the CS Commons including Java Beans generator.

### Integration ###

To add EBX CS Commons library into your project you need to add its jars and war as dependency.
Note that you only need the war file deployed if you want to use the Java Beans generator that the CS Commons provide.
