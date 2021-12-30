//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lotus.priv.CORBA.iiop;

import org.omg.CORBA.ContextList;
import org.omg.CORBA.Object;

public class DefaultConnectionInterceptor extends ConnectionInterceptor {
    private static final String SCCSID = "@(#) 1.1 src/javaorb/com/ibm/CORBA/iiop/DefaultConnectionInterceptor.java, javaorb, boss, m9828.12 4/30/98 10:48:24 [7/20/98 10:24:13]";

    public DefaultConnectionInterceptor(ORB var1, ConnectionTable var2) {
        super(var1, var2);
    }

    public boolean getConnectionKey(Profile var1, int var2, String var3, long var4, ConnectionDataCarrier var6, Object var7) {
        String var10 = var1.getHost();
        int var8;
        byte var9;
        if (this.theOrb.SSLSecurityIsEnabled()) {
            var8 = this.theOrb.getSSLSecurity().getSSLPort(var1);
            var9 = 0;
        } else {
            var8 = var1.getPort();
            var9 = 2;
        }

        String var11 = ConnectionTable.createKey(var10, var8);
        if (var4 == 1L) {
            var6.setConnectionData(new ConnectionData(var11, (long)var9, (ContextList)null));
        } else {
            var6.getConnectionData().setConnectionKey(var11);
            var6.getConnectionData().setConnectionType((long)var9);
        }

        return true;
    }

    public boolean getServerConnectionData(ConnectionData var1) {
        return true;
    }
}