package com.abstratt.kirra.rest.resources;

import java.net.URI;

public class URITranslator {
    /**
     * Translates URIs so they are in a form that the application wants to
     * expose.
     * 
     * Subclasses to override.
     * 
     * @param toTranslate
     *            the URI to translate
     * @return the (potentially translated) URI
     */
    public URI toExternalURI(URI toTranslate) {
        return toTranslate;
    }
}
