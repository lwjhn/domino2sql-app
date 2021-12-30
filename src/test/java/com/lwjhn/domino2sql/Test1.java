package com.lwjhn.domino2sql;

import lotus.domino.*;
import lotus.priv.CORBA.iiop.FixIOR;
import lotus.priv.CORBA.iiop.IOR;
import lotus.priv.CORBA.iiop.ORB;
import lotus.priv.CORBA.iiop.Profile;
import org.junit.Test;

//ORB(String[] var1, Properties var2)
public class Test1 {
    @Test
    public void test() throws Exception {
        String username =  "Admin";
        String password = "Fjsft_123";
        String host = Profile.PROXY_HOST = "192.168.210.153";
        int port = Profile.PROXY_PORT = 9898;
        String origin = host + ":" + port;
        String[] diiopArgs = new String[]{"-ORBClassBootstrapHost", host, "-ORBClassBootstrapPort", String.valueOf(port)};

        Session session = NotesFactory.createSession(origin, diiopArgs, username, password);
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

    public String fixIOR(String iorString, String host, int port) {
        return FixIOR.stringify(FixIOR.parse(iorString, host, port));
    }

    @Test
    public void testIor() throws Exception {
        String ior = NotesFactory.getIOR("192.168.210.153:9898", "Admin", "Fjsft_123");
        System.out.println(ior);
        String newIor = fixIOR(ior, "192.168.210.153", 9898);
        System.out.println(newIor);

        IOR temp = FixIOR.parse(new ORB(), newIor);
        System.out.println(FixIOR.stringify(temp));
    }
}
