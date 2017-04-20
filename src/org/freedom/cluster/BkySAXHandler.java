package org.freedom.cluster;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by chaolin on 2017/4/20.
 */
public class BkySAXHandler extends DefaultHandler {
    private AiaProject resultMaster;

    public BkySAXHandler(AiaProject resultMaster) {
        this.resultMaster = resultMaster;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName){
            case "mutation":
                String componentType = attributes.getValue("component_type");
                String eventName = attributes.getValue("event_name");
                if (componentType != null){
                    resultMaster.addMutation(componentType + " "+ eventName);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

    }
}
