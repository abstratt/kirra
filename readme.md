---
---
Kirra API
===

[![Build Status](https://textuml.ci.cloudbees.com/buildStatus/icon?job=kirra-api)](https://textuml.ci.cloudbees.com/job/kirra-api/)

The Kirra API is a minimalistic API specification to access functionality of a business application in a business and technology agnostic way. 

This reference implementation happens to use the Java language, but the intent is that it could be easily translated to other programming languages (or computing domains, such as network-oriented APIs).

The goal is that by detaching view technologies from model technologies, we can mix and match generic clients and domain-specific applications, no matter what particular technology (within and across language silos) for domain-driven development they are built on.

[![Built on DEV@Cloud](https://www.cloudbees.com/sites/default/files/styles/large/public/Button-Built-on-CB-1.png)](https://textuml.ci.cloudbees.com/job/kirra-api/)


The generic object model
-----------------
Business entity instances in the application are accessed via the Instance class, which includes values for properties and optionally related/child instances.

Entity metadata is available via the Entity class. Further metadata can be obtained via related objects, such as Properties, Relationships, Operations etc.

The InstanceManagement API gives CRUD access to entity instances, including relationship traversal, and provides a mechanism for invoking operations (including actions and queries).  

The SchemaManagement API gives access to the schema of the application.

The Repository merges both instance and schema-related features. 


Implementations
----------------

#### Java Model
A Java-based specification of the Kirra metamodel. This specification has no dependencies and can be used in any Java-based application. Status: [released](http://github.com/abstratt/kirra/tree/master/com.abstratt.kirra.api).

#### JAX-RS-based REST API implementation
JAX-RS based implementation of Kirra as a REST API. Status: [in progress](http://github.com/abstratt/kirra/tree/master/com.abstratt.kirra.rest.resources).

#### Java-based REST API Client
Implements the SchemaManagement and InstanceManagement protocols as a proxy to any REST-based implementation of the Kirra API. Status: [in progress](http://github.com/abstratt/kirra/tree/master/com.abstratt.kirra.rest.client)

#### Cloudfier
Cloudfier implements the Kirra API to expose the funcionality of business applications running off of executable UML models. Cloudfier can generate fully functional clients based on the schema exposed by the Kirra API. Status: [released](http://cloudfier.com/doc).

#### HTML5 mobile-styled client
A generic mobile-styled HTML5 client that allows accessing any business application that provides a Kirra compliant API. Status: [in progress](http://github.com/abstratt/kirra/tree/master/kirra_qooxdoo) ([demo](http://develop.cloudfier.com/kirra-api/kirra_qooxdoo/build/?app-path=/services/api-v2/demo-cloudfier-examples-taxi-fleet)).

#### EBUI - Email-Based User Interface
An email-based front-end for your Kirra-compliant business application. Status: [in progress](http://github.com/abstratt/ebui).

###Ideas

#### EJB/JPA
Implement the Kirra API over the object model of a EJB/JPA application.

#### Apache ISIS
Apache ISIS seems to have a generic API for accessing the functionality of Isis applications but it is far from minimalistic. It is unclear if the current level of functionality ISIS provides could be a offered via a simpler API (if not as simple as Kirra, hopefully not nearly as complex as Isis').

Isis has several viewer implementations, it would be great of they could be detached from the Isis runtime implementation and rely on an implementation-agnostic API like Kirra. 

#### Swagger

Automatic generation of a Swagger-compatible documentation based on any Kirra-compliant implementation.

#### EMF/ECore
Implement the Kirra API on top of EMF's ECore API.

#### OpenXava
OpenXava is similar to Apache Isis, but does not have a metamodel API. 

#### Grails
Implement the Kirra API over the object model of a Grails application, including domain and service classes.

#### Restful Objects
Implement RO on top of the Kirra API, allowing any business application that is exposed via a Kirra-compatible API to have a fully functional REST API for free.

In a way, the Kirra API has similar goals to Restful Objects, but aims to achieve them within the application, without requiring a network hop.

{% include ribbon.html %}
