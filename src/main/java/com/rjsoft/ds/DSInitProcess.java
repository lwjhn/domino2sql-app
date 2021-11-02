package com.rjsoft.ds;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

public class DSInitProcess {

    private Vector allDS = new Vector();
    private String imp = "";

    public DSInitProcess() {
    }

    public void parse(InputStream is) throws Exception {
        try {
            allDS = new Vector();
            SAXReader saxReader = new SAXReader();
            org.dom4j.Document xmldoc = saxReader.read(is);
            Element root = xmldoc.getRootElement();
            this.imp = root.attributeValue("implement");

            for (Iterator i = root.elementIterator(); i.hasNext();) {
                Element item = (Element) i.next();

                DSItem ds = new DSItem();

                for (Iterator j = item.elementIterator(); j.hasNext();) {
                    Element field = (Element) j.next();
                    String fieldName = field.getName();

                    if ("name".equalsIgnoreCase(fieldName)) {
                        ds.setName(field.getTextTrim());
                    } else if ("desc".equalsIgnoreCase(fieldName)) {
                        ds.setDesc(field.getTextTrim());
                    } else if ("value".equalsIgnoreCase(fieldName)) {
                        ds.setValue(field.getTextTrim());
                    } else {
                        System.out.println("fieldName=" + fieldName);
                    }
                }
                String attr = item.attributeValue("opinionmap");
                if (attr != null) ds.setOpinionMap(attr);

                attr = item.attributeValue("delima");
                if (attr != null) ds.setDelima(attr);

                attr = item.attributeValue("format");
                if (attr != null) ds.setFormat(attr);

                allDS.addElement(ds);
            }
        } catch (Exception e) {
            System.err.println("DSInitProcess Error:" + e.getMessage());
            e.printStackTrace();
            throw new Exception("DSPreProcess错误：无法解析XML");
        }
    }

    public Vector getDataSourcese() {
        return this.allDS;
    }

    public String getImplement() {
        return this.imp;
    }
}
