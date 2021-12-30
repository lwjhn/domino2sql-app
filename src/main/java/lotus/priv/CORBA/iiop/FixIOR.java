package lotus.priv.CORBA.iiop;

import lotus.priv.CORBA.portable.ObjectImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

@SuppressWarnings("unused")
public class FixIOR {
    public static IOR parse(ORB orb, String iorString){
        ObjectImpl object = (ObjectImpl) orb.string_to_object(iorString);
        Representation representation = (Representation) object._get_delegate();
        if (representation == null) {
            orb.connect(object);
            representation = (Representation)object._get_delegate();
        }
        return representation.getIOR();
    }

    public static IOR parse(String iorString, String host, int port){
        return parse(iorString, host, port, null);
    }

    public static IOR parse(String iorString, String host, int port, ORB orb){
        IOR ior = parse(orb==null ? (new ORB()) : orb, iorString);
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

            Method method = IOR.class.getDeclaredMethod("putProfile", Profile.class);
            method.setAccessible(true);
            method.invoke(ior, profile);

            return ior;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String stringify(IOR ior){
        return ior.stringify();
    }

    public static String stringify(String iorString, String host, int port){
        return parse(iorString, host, port).stringify();
    }

    public static String stringify(String iorString, String host, int port, ORB orb){
        return parse(iorString, host, port,orb).stringify();
    }

    public static String stringify(String iorString, String host, int port, String[] args){
        return parse(iorString, host, port, createORB(args, null)).stringify();
    }

    public static String stringify(String iorString, String host, int port, Properties properties){
        return parse(iorString, host, port, createORB(null, properties)).stringify();
    }

    public static ORB createORB(String[] args, Properties properties){
        return new ORB(args, properties);
    }
}
