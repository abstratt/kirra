package com.abstratt.kirra.rest.client.testdriver;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Schema;
import com.abstratt.kirra.rest.resources.Page;
import com.abstratt.kirra.sampledata.Expenses;

public class Demo {
 
    public static void main(String[] args) throws Exception {
    	Schema schema = new Expenses().getSchema();
    	Map<String, Object> properties = new HashMap<String, Object>(2);
        properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        properties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, false);
        
        JAXBContext jc = JAXBContext.newInstance(Page.class, Entity.class);
        jc.
 
        JAXBElement<Page> je2 = new JAXBElement<Page>(new QName("entity"), Page.class, new Page(schema.getAllEntities()));
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(je2, System.out);
    }
 
}