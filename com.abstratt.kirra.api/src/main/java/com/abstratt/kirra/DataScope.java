package com.abstratt.kirra;

import java.util.List;

/**
 * A protocol for objects that providing a scope for data (properties).
 */
public interface DataScope extends NameScope {
    List<Property> getProperties();

    Property getProperty(String name);
}
