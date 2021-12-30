package com.lwjhn.domino2sql;

import com.lwjhn.domino2sql.config.DominoQuery;
import lotus.domino.*;
import lotus.priv.CORBA.iiop.IOR;
import lotus.priv.CORBA.iiop.Profile;
import lotus.priv.CORBA.iiop.Representation;
import lotus.priv.CORBA.portable.ObjectImpl;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

public class Test1 {
    @Test
    public void test() throws Exception {
        String[] diiopArgs = new String[]{"-ORBBootstrapHost","192.168.210.153","-ORBBootstrapPort", "9898"};
        Session session = NotesFactory.createSession("192.168.210.153:9898", diiopArgs, "Admin", "Fjsft_123");
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

    public String fixIOR(String iorString, String host, int port){
        lotus.priv.CORBA.iiop.ORB orb = new lotus.priv.CORBA.iiop.ORB();
        ObjectImpl object = (ObjectImpl) orb.string_to_object(iorString);
        Representation representation = (Representation) object._get_delegate();
        if (representation == null) {
            orb.connect(object);
            representation = (Representation)object._get_delegate();
        }
        lotus.priv.CORBA.iiop.IOR ior = representation.getIOR();
        Profile profile = ior.getProfile();

        try {
            Field field = profile.getClass().getDeclaredField("host");
            field.setAccessible(true);
            field.set(profile, host);

            field = profile.getClass().getDeclaredField("port");
            field.setAccessible(true);
            field.set(profile, port);

            field = profile.getClass().getDeclaredField("data");
            field.setAccessible(true);
            field.set(profile, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            Method method = IOR.class.getDeclaredMethod("putProfile", Profile.class);
            method.setAccessible(true);
            method.invoke(ior, profile);

            method = IOR.class.getDeclaredMethod("stringify");
            method.setAccessible(true);
            return (String) method.invoke(ior);
        }catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testIor() throws Exception {
        String ior = NotesFactory.getIOR("192.168.210.153:9898", "Admin", "Fjsft_123");
        ior = "IOR:000000000000002949444c3a6c6f7475732f646f6d696e6f2f636f7262612f494f626a6563745365727665723a312e300000000000000001000000000000007400010100000000103139322e3136382e3231302e3135330026aa0000000000310438353235363531612d656336382d313036632d656565302d303037653264323233336235004c6f7475734e4f4901000100000000000001000000010000001401016a0001000105000000000001010000000000";
        System.out.println(ior);
        String newIor = fixIOR(ior,"192.168.210.153", 9898);
        System.out.println(newIor);
    }
}
