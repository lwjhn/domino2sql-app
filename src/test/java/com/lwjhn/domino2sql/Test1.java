package com.lwjhn.domino2sql;

import lotus.domino.*;
import org.junit.Test;

public class Test1 {
    @Test
    public void test() throws Exception {
        Session session = NotesFactory.createSession("192.168.210.153:9898", "Admin", "Fjsft_123");
        Database db = session.getDatabase("OA/SRV/FJSF", "names.nsf");
        System.out.println(db.isOpen());
        int count = db.getAllDocuments().getCount();
        System.out.println(count);
        Document doc = db.getDocumentByUNID("C3A23651E9C572E54825824A00342157");
        if (doc != null) {
            for (Object object : doc.getItems()) {
                Item item = (Item) object;
                String name = item.getName();
                String value = item.getText();
                System.out.printf("%s : %s\n", name, value);
            }
        }
    }
}
