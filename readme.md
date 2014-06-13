---
layout: default
---
Kirra API
===

The Kirra API is a minimalistic API specification to access functionality of a business application in a business and technology agnostic way. 

This reference implementation happens to use the Java language, but the intent is that it could be easily translated to other programming languages (or computing domains, such as network-oriented APIs).

The goal is that by detaching view technologies from model technologies, we can mix and match generic clients and domain-specific applications, no matter what particular technology (within and across language silos) for domain-driven development they are built on.

The generic object model
-----------------
Business entity instances in the application are accessed via the Instance class, which includes values for properties and optionally related/child instances.

Entity metadata is available via the Entity class. Further metadata can be obtained via related objects, such as Properties, Relationships, Operations etc.

The InstanceManagement API gives CRUD access to entity instances, including relationship traversal, and provides a mechanism for invoking operations (including actions and queries).  

The SchemaManagement API gives access to the schema of the application.

The Repository merges both instance and schema-related features. 

This API specification has no dependencies. 


Implementations
----------------

#### Cloudfier
Cloudfier implements the Kirra API to expose the funcionality of business applications running off of executable UML models. Cloudfier can generate fully functional clients based on the schema exposed by the Kirra API. Status: [released](http://cloudfier.com/doc).

#### Kirra Android Client
A generic client that provides access to any business application that provides a Kirra compliant API. Status: [TBD](https://bitbucket.org/abstratt/kirra-android).

###Ideas

#### EJB/JPA

Implement the Kirra API over the object model of a EJB/JPA application.

#### Apache ISIS
Apache ISIS seems to have a generic API for accessing the functionality of Isis applications but it is far from minimalistic. It is unclear if the current level of functionality ISIS provides could be a offered via a simpler API (if not as simple as Kirra, hopefully not nearly as complex as Isis').

Isis has several viewer implementations, it would be great of they could be detached from the Isis runtime implementation and rely on an implementation-agnostic API like Kirra. 

#### OpenXava
OpenXava is similar to Apache Isis, but does not have a metamodel API. 

#### Grails
Implement the Kirra API over the object model of a Grails application, including domain and service classes.

#### Restful Objects
Implement RO on top of the Kirra API, allowing any business application that is exposed via a Kirra-compatible API to have a fully functional REST API for free.

In a way, the Kirra API has similar goals to Restful Objects, but aims to achieve them within the application, without requiring a network hop.
