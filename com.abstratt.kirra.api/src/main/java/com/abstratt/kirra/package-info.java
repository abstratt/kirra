/**
 * The Kirra API is a minimalistic API specification to access functionality of a business application in a business and technology agnostic way.
 * <p>
 * This reference implementation happens to use the Java language, but the intent is that it could be easily translated to other programming languages (or computing domains, such as network-oriented APIs).
 * <p>
 * The goal is that by detaching view technologies from model technologies, we can mix and match generic clients and domain-specific applications, no matter what particular technology (within and across language silos) for domain-driven development they are built on.
 * <p>
 * <h2>The generic object model</h2>
 * Business entity instances in the application are accessed via the {@link Instance} class, which includes values for properties and optionally related/child instances.
 * <p>
 * Entity metadata is available via the {@link Entity} class. Further metadata can be obtained via related objects, such as {@link Property}, {@link Relationship}, {@link Operation} etc.
 * <p>
 * The {@link InstanceManagement} API gives CRUD access to entity instances, including relationship traversal, and provides a mechanism for invoking operations (including actions and queries).
 * <p>
 * The {@link SchemaManagement} API gives access to the schema of the application.
 * <p>
 * The {@link Repository} merges both instance and schema-related features.
 */

package com.abstratt.kirra;
