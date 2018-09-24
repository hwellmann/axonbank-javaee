# Axon Bank Example for Java EE 8

This is a clone or rather a rewrite of the [Axon Bank](https://github.com/AxonFramework/AxonBank) example, for getting familiar with the Axon Framework on a vanilla Java EE 8 platform.

This version contains none of
* Spring, Spring Boot, Spring Cloud, Spring Data
* Distribution
* STOMP
* Websockets
* Docker
* AngularJS

The only non-Java EE and non-Axon dependency is DeltaSpike Data for implementing JPA repositories.

At the moment, the sample only has a REST API and no UI.

The master branch uses programmatic configuration of the Axon framwork.

The cdi-2.0 branch uses my own heavily hacked fork of the [axon-cdi](https://github.com/hwellmann/cdi/tree/cdi-2.0) project.

Tested on WildFly 14.0.0.Final.
