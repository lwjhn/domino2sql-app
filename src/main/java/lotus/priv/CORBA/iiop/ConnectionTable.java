//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lotus.priv.CORBA.iiop;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.net.ssl.SSLSocket;
import lotus.priv.CORBA.iiop.sslight.IIOPSSLConnection;
import lotus.priv.CORBA.iiop.sslight.KeyRingFileException;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Object;

public class ConnectionTable {
    protected ORB orb;
    protected Hashtable connectionCache = new Hashtable();
    protected Hashtable connectionAliases = new Hashtable();
    protected long globalCounter = 0L;
    private int MAX_SOCKET_RETRIES = 5;
    private ConnectionInterceptor connectionInterceptor;

    public ConnectionTable(ORB var1) {
        this.orb = var1;
        new DefaultConnectionInterceptor(var1, this);
    }

    private void dprint(String var1) {
        Utility.dprint(this, var1);
    }

    public static String createKey(String var0, int var1) {
        return var0 + ":" + var1;
    }

    public static String createKey(String var0, int var1, int var2, short var3) {
        String var4 = "";
        switch(var2) {
            case 0:
                return var0 + ":" + var1;
            case 1:
                return var0 + ":" + var1 + ":IIOPSSL:" + Short.toString(var3);
            case 2:
                return var0 + ":" + var1;
            case 3:
                return var0 + ":" + var1 + ":IIOPDCE";
            default:
                String var5 = var0 + ":" + var1;
                String[] var6 = new String[]{Integer.toString(var2), var5};
                String var7 = Utility.getMessage("createKey.unknownType", var6);
                return var5;
        }
    }

    public static String getHost(String var0) {
        int var1 = var0.indexOf(":");
        return var1 == -1 ? var0 : var0.substring(0, var1);
    }

    public static int getPort(String var0) {
        int var1 = var0.indexOf(":");
        int var2 = var0.indexOf(":", var1 + 1);
        return var2 == -1 ? Integer.parseInt(var0.substring(var1 + 1)) : Integer.parseInt(var0.substring(var1 + 1, var2));
    }

    public synchronized Connection get(Profile var1, String var2, Object var3) {
        byte var4 = 1;
        ConnectionDataCarrier var5 = new ConnectionDataCarrier();
        ConnectionData var6 = null;
        Connection var7 = null;
        String var8 = "";
        boolean var9 = false;
        String var10 = "";
        String var11;
        if (!this.connectionInterceptor.getConnectionKey(var1, 0, var2, (long)var4, var5, var3)) {
            var11 = Utility.getMessage("getConnectionKey.returnedFalse");
            throw new COMM_FAILURE(var11, 1, CompletionStatus.COMPLETED_NO);
        } else {
            var6 = var5.getConnectionData();
            if (var6 == null) {
                var11 = Utility.getMessage("getConnectionData.returnedNull");
                throw new COMM_FAILURE(var11, 1, CompletionStatus.COMPLETED_NO);
            } else {
                var10 = var6.getConnectionKey();
                var7 = this.get(var10);
                if (var7 != null) {
                    var7.setConnectionData(var6);
                    return var7;
                } else {
                    var8 = getHost(var10);
                    int var13 = getPort(var10);
                    switch((int)var6.getConnectionType()) {
                        case 0:
                            try {
                                var7 = this.orb.getSSLSecurity().createSSLConnection(this.orb, var1, true);
                                var7.setConnectionData(var6);
                                this.put(var8, var13, var7);
                                this.stampTime(var7);
                            } catch (Exception var12) {
                                var7 = this.get(var8, var13, var1);
                            }
                            break;
                        case 1:
                            var7 = this.createSSLConnection(var8, var13, (SSLConnectionData)var6);
                            break;
                        case 2:
                        case 3:
                            var7 = this.get(var8, var13, var1);
                            var7.setConnectionData(var6);
                            break;
                        default:
                            var11 = Utility.getMessage("ConnectionTable.unknownConnectionType", Long.toString(var6.getConnectionType()));
                            throw new COMM_FAILURE(var11, 1, CompletionStatus.COMPLETED_NO);
                    }

                    return var7;
                }
            }
        }
    }

    private synchronized Connection get(String var1, int var2, Profile var3) {
        String var6 = createKey(var1, var2);
        Connection var5 = this.get(var6);
        if (var5 != null) {
            return var5;
        } else {
            Socket var7 = null;
            InputStream var8 = null;
            OutputStream var9 = null;
            int var10 = 0;

            while(true) {
                try {
                    var7 = Connection.newSocket(this.orb, var1, var2);
                    var8 = var7.getInputStream();
                    var9 = var7.getOutputStream();
                    this.checkConnectionTable();
                    break;
                } catch (Exception var14) {
                    String var12 = var14.toString() + " Host: " + var1 + " Port: " + var2;
                    if (var10 == this.MAX_SOCKET_RETRIES || !this.cleanUp(var12)) {
                        COMM_FAILURE var13 = new COMM_FAILURE(var12, 1, CompletionStatus.COMPLETED_NO);
                        if (ORB.g_initCause) {
                            var13.initCause(var14);
                        }

                        throw var13;
                    }

                    ++var10;
                }
            }

            IIOPConnection var15 = new IIOPConnection(this.orb, var1, var2, var7, var8, var9, false, this);
            this.put(var1, var2, var15);
            this.stampTime(var15);
            return var15;
        }
    }

    public synchronized Connection get(Profile var1) {
        String var2 = var1.getHost();
        Connection var3 = null;
        int var7;
        if (this.orb.SSLSecurityIsEnabled()) {
            try {
                boolean var4 = false;
                var7 = this.orb.getSSLSecurity().getSSLPort(var1);
                String var8 = createKey(var2, var7);
                var3 = this.get(var8);
                if (var3 != null) {
                    return var3;
                }

                var3 = this.orb.getSSLSecurity().createSSLConnection(this.orb, var1, true);
                this.put(var2, var3.getSocket().getPort(), var3);
            } catch (Exception var6) {
                int var5 = var1.getPort();
                var3 = this.get(var2, var5, var1);
            }
        } else {
            var7 = var1.getPort();
            var3 = this.get(var2, var7, var1);
        }

        return var3;
    }

    public synchronized Connection get(Socket var1) {
        try {
            InputStream var2 = null;
            OutputStream var3 = null;

            try {
                var2 = var1.getInputStream();
                var3 = var1.getOutputStream();
            } catch (Exception var8) {
                COMM_FAILURE var5 = new COMM_FAILURE(var8.toString() + " " + var1.toString(), 1, CompletionStatus.COMPLETED_NO);
                if (ORB.g_initCause) {
                    var5.initCause(var8);
                }

                throw var5;
            }

            String var4 = var1.getInetAddress().getHostName();
            int var10 = var1.getPort();
            IIOPConnection var6 = new IIOPConnection(this.orb, var4, var10, var1, var2, var3, true, this);
            this.put(var4, var10, var6);
            this.stampTime(var6);
            return var6;
        } catch (Exception var9) {
            try {
                var1.close();
            } catch (Exception var7) {
            }

            return null;
        }
    }

    private synchronized Connection get(String var1) {
        Connection var2 = (Connection)this.connectionCache.get(var1);
        if (var2 == null) {
            try {
                int var3 = var1.indexOf(58);
                String var4 = var1.substring(0, var3);
                int var5 = Integer.parseInt(var1.substring(var3 + 1));
                InetAddress var6 = InetAddress.getByName(var4);
                String var7 = createKey(var6.getHostAddress(), var5);
                var2 = (Connection)this.connectionCache.get(var7);
                if (var2 != null) {
                    this.connectionCache.put(var1, var2);
                    Vector var8 = (Vector)this.connectionAliases.get(var7);
                    if (var8 != null) {
                        var8.addElement(var1);
                    }
                }
            } catch (Exception var9) {
            }
        }

        return var2;
    }

    private synchronized void put(String var1, int var2, Connection var3) {
        this.put(var1, var2, 2, (short)0, var3);
    }

    private synchronized void put(String var1, int var2, int var3, short var4, Connection var5) {
        Vector var6 = new Vector();
        String var7 = createKey(var1, var2, var3, var4);
        this.connectionCache.put(var7, var5);
        var6.addElement(var7);

        try {
            InetAddress var8 = InetAddress.getByName(var1);
            String var9 = createKey(var8.getHostAddress(), var2, var3, var4);
            this.connectionCache.put(var9, var5);
            var6.addElement(var9);
            String var10 = var8.getHostName();
            if (!var1.equals(var10)) {
                var7 = createKey(var10, var2, var3, var4);
                this.connectionCache.put(var7, var5);
                var6.addElement(var7);
            }

            this.connectionAliases.put(var9, var6);
        } catch (Exception var11) {
        }

    }

    public synchronized void put(String var1, Connection var2) {
        this.connectionCache.put(var1, var2);
    }

    public synchronized void deleteConn(String var1, int var2) {
        this.deleteConn(var1, var2, 2, (short)0);
    }

    public synchronized void deleteConn(String var1, int var2, int var3, short var4) {
        try {
            InetAddress var5 = InetAddress.getByName(var1);
            String var6 = var5.getHostAddress();
            String var7 = createKey(var6, var2, var3, var4);
            Vector var8 = (Vector)this.connectionAliases.get(var7);
            if (var8 == null) {
                this.connectionCache.remove(createKey(var1, var2, var3, var4));
            } else {
                Enumeration var9 = var8.elements();

                while(var9.hasMoreElements()) {
                    this.connectionCache.remove((String)var9.nextElement());
                }

                this.connectionAliases.remove(var7);
            }
        } catch (Exception var10) {
            this.connectionCache.remove(createKey(var1, var2, var3, var4));
        }

    }

    public void registerConnectionInterceptor(ConnectionInterceptor var1) {
        this.connectionInterceptor = var1;
    }

    private Connection createSSLConnection(String var1, int var2, SSLConnectionData var3) {
        SSLSocket var4 = null;
        InputStream var5 = null;
        OutputStream var6 = null;
        String var7 = null;
        int var8 = 0;

        while(true) {
            COMM_FAILURE var10;
            try {
                var4 = IIOPSSLConnection.createSSLSocket(this.orb, var1, var2, var3.getPerformQOP());
                var5 = var4.getInputStream();
                var6 = var4.getOutputStream();
                this.checkConnectionTable();
                break;
            } catch (SocketException var11) {
                var7 = var11.toString() + " Host: " + var1 + " Port: " + var2;
                if (var8 >= this.MAX_SOCKET_RETRIES) {
                    if (!(var11 instanceof BindException) && !(var11 instanceof ConnectException) && !(var11 instanceof NoRouteToHostException)) {
                        var3.setErrorCode(9);
                        var10 = new COMM_FAILURE(var7, 1, CompletionStatus.COMPLETED_NO);
                        if (ORB.g_initCause) {
                            var10.initCause(var11);
                        }

                        throw var10;
                    }

                    var3.setErrorCode(9);
                    var10 = new COMM_FAILURE(var7, 1, CompletionStatus.COMPLETED_NO);
                    if (ORB.g_initCause) {
                        var10.initCause(var11);
                    }

                    throw var10;
                }
            } catch (KeyRingFileException var12) {
                var7 = var12.toString() + " Host: " + var1 + " Port: " + var2;
                if (var8 >= this.MAX_SOCKET_RETRIES) {
                    var3.setErrorCode(2);
                    var10 = new COMM_FAILURE(var7, 1, CompletionStatus.COMPLETED_NO);
                    if (ORB.g_initCause) {
                        var10.initCause(var12);
                    }

                    throw var10;
                }
            } catch (Exception var13) {
                var7 = var13.toString() + " Host: " + var1 + " Port: " + var2;
                var3.setErrorCode(9);
                var10 = new COMM_FAILURE(var7, 1, CompletionStatus.COMPLETED_NO);
                if (ORB.g_initCause) {
                    var10.initCause(var13);
                }

                throw var10;
            }

            ++var8;
        }

        if (var4 == null) {
            if (var7 != null) {
                throw new COMM_FAILURE(var7, 1, CompletionStatus.COMPLETED_NO);
            } else {
                throw new COMM_FAILURE(1, CompletionStatus.COMPLETED_NO);
            }
        } else {
            IIOPSSLConnection var14 = null;
            var14 = new IIOPSSLConnection(this.orb, var1, var2, var4, var5, var6, false, this, var3);
            if (var14 != null) {
                this.put(var1, var2, 1, var3.getPerformQOP(), var14);
                this.stampTime(var14);
            }

            return var14;
        }
    }

    public boolean cleanUp(String var1) {
        while(true) {
            Connection var3;
            synchronized(this) {
                if (this.connectionCache.size() < 20) {
                    return false;
                }

                var3 = null;
                long var4 = 9223372036854775807L;
                Enumeration var2 = this.connectionCache.elements();

                while(true) {
                    if (!var2.hasMoreElements()) {
                        break;
                    }

                    Connection var7 = (Connection)var2.nextElement();
                    if (!var7.isBusy() && var7.timeStamp < var4) {
                        var3 = var7;
                        var4 = var7.timeStamp;
                    }
                }
            }

            try {
                if (var3 != null) {
                    var3.cleanUp(var1);
                    return true;
                }

                return false;
            } catch (Exception var9) {
            }
        }
    }

    public void checkConnectionTable() {
        boolean var1 = false;
        int var5;
        synchronized(this) {
            var5 = this.connectionCache.size();
        }

        if (var5 > 200) {
            this.cleanUp((String)null);
        }

    }

    public synchronized void stampTime(Connection var1) {
        var1.timeStamp = (long)(this.globalCounter++);
    }

    public synchronized void print() {
        System.out.println("***ConnectionTable***");
        System.out.println("  SIZE=" + this.connectionCache.size());
        Enumeration var1 = this.connectionCache.elements();

        while(var1.hasMoreElements()) {
            Connection var2 = (Connection)var1.nextElement();
            var2.print();
        }

    }

    public boolean connectionExists(String var1, int var2) {
        return this.connectionExists(var1, var2, 2, (short)0);
    }

    public boolean connectionExists(String var1, int var2, int var3, short var4) {
        Connection var5 = (Connection)this.connectionCache.get(createKey(var1, var2, var3, var4));
        return var5 != null;
    }

    public void release() {
        for(Enumeration var2 = this.connectionCache.elements(); var2.hasMoreElements(); var2 = this.connectionCache.elements()) {
            Connection var1 = (Connection)var2.nextElement();

            try {
                var1.cleanUp((String)null);
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }

    }
}