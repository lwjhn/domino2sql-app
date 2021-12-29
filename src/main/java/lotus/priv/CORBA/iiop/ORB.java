//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lotus.priv.CORBA.iiop;

import java.applet.Applet;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;
import lotus.priv.CORBA.iiop.ssl.SSLSecurity;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Current;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.Request;
import org.omg.CORBA.ServiceInformation;
import org.omg.CORBA.ServiceInformationHolder;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public class ORB extends org.omg.CORBA.ORB {
    private ORBCallback m_callback;
    static final boolean DOIMPL = false;
    static final int JAVAMAGIC = 1246577225;
    static final int CPPMAGIC = 1129136713;
    static final int TAG_FIREWALL_TRAVERSAL = 1229081859;
    private String servletURLString;
    protected SubcontractRegistryClass subcontractRegistry;
    protected int scClientIndex;
    protected int scServerIndex;
    protected int GenericSCID;
    protected int IBMSCID;
    protected int STANDARDSCID;
    private String[][] scNames;
    private int[] scIDs;
    private final int INITIAL_CAPACITY;
    private final int CAPACITY_INCREMENT;
    RequestInterceptor firstRequestInterceptor;
    Vector requestInterceptorList;
    Hashtable requestHolderTable;
    Hashtable NameServiceTable;
    Hashtable serviceInformationList;
    MessageInterceptor messageInterceptor;
    Hashtable currentCreators;
    private String[] ORBPropertyNames;
    private String[] ORBURLPropertyNames;
    protected ImplDefFactory implFactory;
    protected ImplDefRegistryClass implRegistry;
    private int transientServerId;
    protected boolean debug;
    protected ConnectionTable connectionTable;
    protected ListenerThread listenerThread;
    protected String appletHost;
    protected int appletPort;
    protected URL appletCodeBase;
    protected boolean isRunningInApplet;
    private static final int defaultInitialServicesPort = 900;
    private static final String defaultTunnelAgentName = "/servlet/IBMTunnel";
    protected Properties ORBState;
    ThreadGroup threadGroup;
    private Generic initialRep;
    private String[] cachedListOfInitialServices;
    protected ObjectResolver objectResolver;
    private int serverId;
    private boolean forceTunnel;
    private boolean forceTunnelVarSet;
    private boolean sslSecurityEnabled;
    private boolean sslSecurityVarSet;
    private boolean dceSecurityEnabled;
    private boolean dceSecurityVarSet;
    private boolean commTraceEnabled;
    private boolean commTraceVarSet;
    private boolean applicationOLTEnabled;
    private boolean applicationOLTVarSet;
    private String applicationOLTHome;
    private boolean applicationOLTHomeVarSet;
    protected SSLSecurity sslSecurityObject;
    private final ClientStyleProperties ClientStylePropertiesObject;
    private Properties cachedInitialReferences;
    private URL cachedInitialReferencesURL;
    protected static URLToObjectFacility urlToObjectFacility = null;
    private static Hashtable impls = new Hashtable();
    static final boolean g_initCause;

    void setDebug(boolean var1) {
        this.debug = var1;
        System.out.println(" ORB: Debugging enabled");
    }

    private void dprint(String var1) {
        Utility.dprint(this, var1);
    }

    public Properties getORBState() {
        return this.ORBState;
    }

    public ORB() {
        this.servletURLString = null;
        this.scClientIndex = 0;
        this.scServerIndex = 1;
        this.GenericSCID = 2;
        this.IBMSCID = 3;
        this.STANDARDSCID = 4;
        this.scNames = new String[][]{{"lotus.priv.CORBA.iiop.RepImpl", "lotus.priv.CORBA.iiop.GenericServerSC", "lotus.priv.CORBA.iiop.StandardServerSC"}};
        this.scIDs = new int[]{this.IBMSCID};
        this.INITIAL_CAPACITY = 4;
        this.CAPACITY_INCREMENT = 2;
        this.firstRequestInterceptor = null;
        this.messageInterceptor = null;
        this.currentCreators = new Hashtable();
        this.ORBPropertyNames = new String[]{"lotus.priv.CORBA.BootstrapHost", "lotus.priv.CORBA.BootstrapPort", "lotus.priv.CORBA.InitialReferencesURL", "lotus.priv.CORBA.ListenerPort", "lotus.priv.CORBA.TunnelAgentURL", "lotus.priv.CORBA.SSLPropertiesURL", "lotus.priv.CORBA.DCEPropertiesURL", "lotus.priv.CORBA.EnableSSLSecurity", "lotus.priv.CORBA.EnableDCESecurity", "lotus.priv.CORBA.ForceTunnel", "lotus.priv.CORBA.CommTrace", "lotus.priv.CORBA.EnableApplicationOLT", "lotus.priv.CORBA.ApplicationOLTHome", "lotus.priv.CORBA.ServerId", "lotus.priv.CORBA.EnableType1SSLSecurity", "lotus.priv.CORBA.ClientStyleImageURL"};
        this.ORBURLPropertyNames = new String[]{"lotus.priv.CORBA.InitialReferencesURL", "lotus.priv.CORBA.SSLPropertiesURL", "lotus.priv.CORBA.DCEPropertiesURL", "lotus.priv.CORBA.ClientStyleImageURL"};
        this.transientServerId = 0;
        this.debug = false;
        this.appletHost = "";
        this.appletPort = -1;
        this.appletCodeBase = null;
        this.isRunningInApplet = false;
        this.ORBState = new Properties();
        this.objectResolver = null;
        this.serverId = -1;
        this.forceTunnel = false;
        this.forceTunnelVarSet = false;
        this.sslSecurityEnabled = false;
        this.sslSecurityVarSet = false;
        this.dceSecurityEnabled = false;
        this.dceSecurityVarSet = false;
        this.commTraceEnabled = false;
        this.commTraceVarSet = false;
        this.applicationOLTEnabled = false;
        this.applicationOLTVarSet = false;
        this.applicationOLTHomeVarSet = false;
        this.ClientStylePropertiesObject = null;
        this.cachedInitialReferences = null;
        this.cachedInitialReferencesURL = null;
        this.threadGroup = Thread.currentThread().getThreadGroup();
        this.connectionTable = new ConnectionTable(this);
        this.subcontractRegistry = new SubcontractRegistryClass();
        Random var1 = new Random();
        long var2 = var1.nextLong();

        int var4;
        for(var4 = 0; var2 > 0L; var2 >>= 1) {
            if ((var2 & 1L) != 0L) {
                ++var4;
            }
        }

        for(var4 *= 50; var4 > 0; --var4) {
        }

        Date var5 = new Date();
        this.transientServerId = (int)(var5.getTime() / 1000L);
        this.registerSubcontracts(this.scNames, this.scIDs);
        this.subcontractRegistry.setDefaultSC(this.IBMSCID);
        this.requestInterceptorList = new Vector(4, 2);
        this.requestHolderTable = new Hashtable();
        this.NameServiceTable = new Hashtable();
        this.serviceInformationList = new Hashtable(2);
    }

    void setupObjectResolver() {
    }

    public ORB(String[] var1, Properties var2) {
        this();
        this.set_parameters(var1, var2);
    }

    public ORB(Applet var1) {
        this();
        this.set_parameters((Applet)var1, (Properties)null);
    }

    public ORB(Applet var1, Properties var2) {
        this();
        this.set_parameters(var1, var2);
    }

    protected void registerSubcontracts(String[][] var1, int[] var2) {
        for(int var3 = 0; var3 < var1.length; ++var3) {
            try {
                Server var4 = (Server)Class.forName(var1[var3][this.scServerIndex]).newInstance();
                var4.setORB(this);
                var4.setSCID(var2[var3]);
                this.subcontractRegistry.registerClient(var1[var3][this.scClientIndex], var2[var3]);
                this.subcontractRegistry.registerServer(var4, var2[var3]);
            } catch (ClassNotFoundException var5) {
            } catch (InstantiationException var6) {
            } catch (IllegalAccessException var7) {
            }
        }

    }

    protected void adjustProcessedORBProperties(Properties var1) {
        String var2 = var1.getProperty("lotus.priv.CORBA.ListenerPort");
        if (var2 != null) {
            int var3 = Integer.parseInt(var2) * -1;
            if (var3 < 0) {
                var1.put("lotus.priv.CORBA.ListenerPort", String.valueOf(var3));
            }
        }

        var2 = var1.getProperty("org.omg.CORBA.Debug");
        if (var2 != null) {
            this.setDebug(true);
        }

    }

    protected boolean singleParam(String var1) {
        if (var1.equals("-ORBDebug")) {
            return true;
        } else if (var1.equals("-ORBEnableSSLSecurity")) {
            return true;
        } else if (var1.equals("-ORBEnableDCESecurity")) {
            return true;
        } else if (var1.equals("-ORBForceTunnel")) {
            return true;
        } else if (var1.equals("-ORBCommTrace")) {
            return true;
        } else {
            return var1.equals("-ORBEnableApplicationOLT");
        }
    }

    protected void findPropertiesFromArgs(Properties var1, String[] var2) {
        if (var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
                if (!var2[var3].startsWith("-ORB")) {
                    if (var2[var3].equals("lotus.priv.CORBA.SSLDebug")) {
                        this.getSSLSecurity().setDebug(true);
                    }
                } else {
                    String var4;
                    if (!var2[var3].equals("-ORBClass") && !var2[var3].equals("-ORBTCFactoryClass")) {
                        var4 = "lotus.priv.CORBA.";
                    } else {
                        var4 = "org.omg.CORBA.";
                    }

                    if (var2[var3].startsWith("-ORBSSLKeystore=")) {
                        var1.put("domino.keystore", var2[var3].substring(16));
                    } else if (var2[var3].startsWith("-ORBSSLKeystorePassword=")) {
                        var1.put("domino.kspassword", var2[var3].substring(24));
                    } else if (this.singleParam(var2[var3])) {
                        var1.put(var4 + var2[var3].substring(4), "true");
                    } else {
                        var1.put(var4 + var2[var3].substring(4), var2[var3 + 1]);
                        ++var3;
                    }
                }
            }

        }
    }

    protected void findPropertiesFromApplet(Properties var1, Applet var2) {
        if (var2 != null) {
            for(int var3 = 0; var3 < this.ORBPropertyNames.length; ++var3) {
                String var4 = var2.getParameter(this.ORBPropertyNames[var3]);
                if (var4 != null) {
                    var1.put(this.ORBPropertyNames[var3], var4);
                }
            }

            String var8 = var1.getProperty("lotus.priv.CORBA.SSLDebug");
            if (var8 != null) {
                this.getSSLSecurity().setDebug(true);
            }

            for(int var9 = 0; var9 < this.ORBURLPropertyNames.length; ++var9) {
                String var5 = var1.getProperty(this.ORBURLPropertyNames[var9]);
                if (var5 != null) {
                    try {
                        URL var6 = new URL(var2.getDocumentBase(), var5);
                        var1.put(this.ORBURLPropertyNames[var9], var6.toExternalForm());
                    } catch (MalformedURLException var7) {
                    }
                }
            }

        }
    }

    protected void findPropertiesFromSystem(Properties var1) {
        for(int var2 = 0; var2 < this.ORBPropertyNames.length; ++var2) {
            String var3;
            try {
                var3 = System.getProperty(this.ORBPropertyNames[var2]);
            } catch (SecurityException var5) {
                var3 = null;
            }

            if (var3 != null) {
                var1.put(this.ORBPropertyNames[var2], var3);
            }
        }

    }

    protected void findPropertiesFromProperties(Properties var1, Properties var2) {
        if (var2 != null) {
            this.objectResolver = (ObjectResolver)var2.get("lotus.priv.CORBA.ObjectResolver");

            for(int var3 = 0; var3 < this.ORBPropertyNames.length; ++var3) {
                String var4 = var2.getProperty(this.ORBPropertyNames[var3]);
                if (var4 != null) {
                    var1.put(this.ORBPropertyNames[var3], var4);
                }
            }

        }
    }

    protected void set_parameters(Applet var1, Properties var2) {
        if (var1 != null) {
            this.appletHost = var1.getCodeBase().getHost();
            this.appletPort = var1.getCodeBase().getPort();
            if (this.appletPort == -1) {
                this.appletPort = 80;
            }

            this.appletCodeBase = FileLocator.appletCodeBase = var1.getCodeBase();
        }

        this.isRunningInApplet = true;
        this.findPropertiesFromProperties(this.ORBState, var2);
        this.findPropertiesFromApplet(this.ORBState, var1);
        this.checkAppletPropertyDefaults(this.ORBState);
        this.adjustProcessedORBProperties(this.ORBState);
    }

    protected void set_parameters(String[] var1, Properties var2) {
        this.isRunningInApplet = false;
        if (var2 == null || var2.get("org.omg.CORBA.TCFactoryClass") == null) {
            this.findPropertiesFromSystem(this.ORBState);
        }

        this.findPropertiesFromProperties(this.ORBState, var2);
        this.findPropertiesFromArgs(this.ORBState, var1);
        this.checkApplicationPropertyDefaults(this.ORBState);
        this.adjustProcessedORBProperties(this.ORBState);
        String var3 = this.ORBState.getProperty("lotus.priv.CORBA.ServerId");
        if (var3 != null) {
            this.serverId = Integer.valueOf(var3);
        }

        if (this.objectResolver != null) {
            if (this.serverId < 0) {
                String var4 = this.getBootstrapHost();
                int var5 = this.getListenerPort();
                String var6 = this.objectResolver.getClass().getName() + var4 + var5;
                this.serverId = CRC.getCRC(var6.getBytes());
            }

            try {
                Server var10 = (Server)Class.forName(this.scNames[0][2]).newInstance();
                var10.setORB(this);
                var10.setSCID(this.STANDARDSCID);
                this.subcontractRegistry.registerServer(var10, this.STANDARDSCID);
            } catch (ClassNotFoundException var7) {
            } catch (InstantiationException var8) {
            } catch (IllegalAccessException var9) {
            }

            this.setupObjectResolver();
        }

    }

    private void checkAppletPropertyDefaults(Properties var1) {
        String var2 = var1.getProperty("lotus.priv.CORBA.BootstrapHost");
        if (var2 == null || var2.equals("")) {
            var1.put("lotus.priv.CORBA.BootstrapHost", this.appletHost);
        }

        String var3 = var1.getProperty("lotus.priv.CORBA.TunnelAgentURL");

        try {
            if (var3 != null && var3 != "") {
                var1.put("lotus.priv.CORBA.TunnelAgentURL", (new URL(new URL("http", this.appletHost, this.appletPort, "/servlet/IBMTunnel"), var3)).toExternalForm());
            } else {
                var1.put("lotus.priv.CORBA.TunnelAgentURL", (new URL("http", this.appletHost, this.appletPort, "/servlet/IBMTunnel")).toExternalForm());
            }
        } catch (MalformedURLException var5) {
            var5.printStackTrace();
        } catch (Exception var6) {
            var6.printStackTrace();
        } catch (Throwable var7) {
            var7.printStackTrace();
        }

    }

    private void checkApplicationPropertyDefaults(Properties var1) {
        String var2 = var1.getProperty("lotus.priv.CORBA.BootstrapHost");
        if (var2 == null || var2.equals("")) {
            var1.put("lotus.priv.CORBA.BootstrapHost", Connection.getLocalHost());
        }

    }

    public final String getAppletHost() {
        return this.appletHost;
    }

    public final int getAppletPort() {
        return this.appletPort;
    }

    public final URL getAppletCodeBase() {
        return this.appletCodeBase;
    }

    final ObjectResolver getObjectResolver() {
        return this.objectResolver;
    }

    public final String getBootstrapHost() {
        return this.ORBState.getProperty("lotus.priv.CORBA.BootstrapHost");
    }

    public final int getBootstrapPort() {
        String var1 = "lotus.priv.CORBA.BootstrapPort";

        int var2;
        try {
            var2 = Integer.parseInt(this.ORBState.getProperty(var1));
        } catch (NumberFormatException var4) {
            if (this.debug) {
                this.dprint("caught exception while reading bootstrap server port number " + var4);
            }

            var2 = 900;
            this.ORBState.put(var1, String.valueOf(var2));
        }

        return var2;
    }

    public final URL getInitialReferencesURL() {
        URL var1 = null;
        String var2 = this.ORBState.getProperty("lotus.priv.CORBA.InitialReferencesURL");
        if (var2 != null) {
            try {
                var1 = new URL(var2);
            } catch (MalformedURLException var4) {
            }
        }

        return var1;
    }

    final synchronized int getListenerPort() {
        String var1 = "lotus.priv.CORBA.ListenerPort";
        int var2 = 0;
        String var3 = this.ORBState.getProperty(var1);
        if (var3 != null) {
            try {
                var2 = Integer.parseInt(var3);
            } catch (NumberFormatException var5) {
                if (this.debug) {
                    this.dprint("caught exception while reading listener port number " + var5);
                }

                var2 = 0;
            }
        }

        if (var2 <= 0) {
            this.listenerThread = this.createListener(-1 * var2);
            ServerSocket var4 = this.listenerThread.getSocket();
            var2 = var4.getLocalPort();
            this.ORBState.put(var1, String.valueOf(var2));
        }

        return var2;
    }

    final URL getTunnelAgentURL() {
        URL var1 = null;

        try {
            var1 = new URL(this.ORBState.getProperty("lotus.priv.CORBA.TunnelAgentURL"));
        } catch (MalformedURLException var3) {
        }

        return var1;
    }

    public final URL getSSLPropertiesURL() {
        URL var1 = null;

        try {
            var1 = new URL(this.ORBState.getProperty("lotus.priv.CORBA.SSLPropertiesURL"));
        } catch (MalformedURLException var3) {
        }

        return var1;
    }

    public final URL getDCEPropertiesURL() {
        URL var1 = null;

        try {
            var1 = new URL(this.ORBState.getProperty("lotus.priv.CORBA.DCEPropertiesURL"));
        } catch (MalformedURLException var3) {
        }

        return var1;
    }

    final boolean ForceTunnelIsEnabled() {
        if (!this.forceTunnelVarSet) {
            this.forceTunnelVarSet = true;
            String var1 = this.ORBState.getProperty("lotus.priv.CORBA.ForceTunnel");
            this.forceTunnel = var1 != null ? var1.equalsIgnoreCase("true") : false;
        }

        return this.forceTunnel;
    }

    public final boolean SSLSecurityIsEnabled() {
        if (!this.sslSecurityVarSet) {
            this.sslSecurityVarSet = true;
            String var1 = this.ORBState.getProperty("lotus.priv.CORBA.EnableSSLSecurity");
            this.sslSecurityEnabled = var1 != null ? var1.equalsIgnoreCase("true") : false;
        }

        return this.sslSecurityEnabled;
    }

    public final boolean DCESecurityIsEnabled() {
        if (!this.dceSecurityVarSet) {
            this.dceSecurityVarSet = true;
            String var1 = this.ORBState.getProperty("lotus.priv.CORBA.EnableDCESecurity");
            this.dceSecurityEnabled = var1 != null ? var1.equalsIgnoreCase("true") : false;
        }

        return this.dceSecurityEnabled;
    }

    final boolean CommTraceIsEnabled() {
        if (!this.commTraceVarSet) {
            this.commTraceVarSet = true;
            String var1 = this.ORBState.getProperty("lotus.priv.CORBA.CommTrace");
            this.commTraceEnabled = var1 != null ? var1.equalsIgnoreCase("true") : false;
        }

        return this.commTraceEnabled;
    }

    final boolean isRunningInApplet() {
        return this.isRunningInApplet;
    }

    final boolean ApplicationOLTIsEnabled() {
        if (!this.applicationOLTVarSet) {
            this.applicationOLTVarSet = true;
            String var1 = this.ORBState.getProperty("lotus.priv.CORBA.EnableApplicationOLT");
            this.applicationOLTEnabled = var1 != null ? var1.equalsIgnoreCase("true") : false;
        }

        return this.applicationOLTEnabled;
    }

    public final String getApplicationOLTHome() {
        if (!this.applicationOLTHomeVarSet) {
            this.applicationOLTHomeVarSet = true;
            String var1 = this.ORBState.getProperty("lotus.priv.CORBA.ApplicationOLTHome");
            this.applicationOLTHome = var1 != null ? var1 : "";
        }

        return this.applicationOLTHome;
    }

    public final void setServletURLString(String var1) {
        this.servletURLString = var1;
    }

    final String getServletURLString() {
        return this.servletURLString;
    }

    public final SSLSecurity getSSLSecurity() {
        if (this.sslSecurityObject == null) {
            this.sslSecurityObject = new SSLSecurity(this.ORBState);
        }

        return this.sslSecurityObject;
    }

    public final ClientStyleProperties getClientStyleProperties() {
        return this.ClientStylePropertiesObject;
    }

    public final ConnectionTable getConnectionTable() {
        return this.connectionTable;
    }

    public synchronized ListenerThread createListener(int var1) {
        try {
            ServerSocket var2 = new ServerSocket(var1);
            ListenerThread var3 = new ListenerThread(this, var2);
            var3.setDaemon(true);
            var3.start();
            return var3;
        } catch (Exception var5) {
            throw new INTERNAL(8, CompletionStatus.COMPLETED_NO);
        }
    }

    public final String[] list_initial_services() {
        return this.cachedServices();
    }

    String[] list_initial_services_remote(String[] var1) throws SystemException {
        String var4 = null;
        String[] var5 = null;
        Object var6 = null;

        for(int var7 = 0; var7 < var1.length; ++var7) {
            String var8 = null;
            boolean var9 = false;

            try {
                String var10 = var1[var7].substring(0, 7).toLowerCase();
                if (!var10.equals("iiop://")) {
                    var6 = new BAD_PARAM();
                } else {
                    var4 = var1[var7].substring(7);
                    int var11 = var4.indexOf(":");
                    int var13;
                    if (var11 < 0) {
                        var8 = var4;
                        var13 = this.getBootstrapPort();
                    } else if (var11 == 0) {
                        var8 = this.getBootstrapHost();
                        var13 = Integer.parseInt(var4.substring(1));
                    } else {
                        var8 = var4.substring(0, var11);
                        var13 = Integer.parseInt(var4.substring(var11 + 1));
                    }

                    var5 = this.getInitialServices(var8, var13);
                    if (var5 != null) {
                        break;
                    }
                }
            } catch (Exception var12) {
                var6 = var12;
            }
        }

        if (var5 != null) {
            return var5;
        } else if (var6 instanceof SystemException) {
            throw (SystemException)var6;
        } else {
            return null;
        }
    }

    private Properties getServicesFromURL(URL var1) throws SystemException {
        if (this.cachedInitialReferences == null | !var1.equals(this.cachedInitialReferencesURL)) {
            try {
                InputStream var2 = var1.openStream();
                Properties var5 = new Properties();
                var5.load(var2);
                this.cachedInitialReferences = var5;
                this.cachedInitialReferencesURL = var1;
            } catch (IOException var4) {
                if (this.debug) {
                    Utility.dprint("ORB", "IOException accessing services: " + var4);
                }

                COMM_FAILURE var3 = new COMM_FAILURE(var4.toString(), 4, CompletionStatus.COMPLETED_NO);
                if (g_initCause) {
                    var3.initCause(var4);
                }

                throw var3;
            }
        }

        return this.cachedInitialReferences;
    }

    private synchronized String[] cachedServices() {
        if (this.cachedListOfInitialServices == null) {
            String[] var1 = null;
            if (this.cachedListOfInitialServices == null && this.getInitialReferencesURL() != null) {
                Properties var2 = this.getServicesFromURL(this.getInitialReferencesURL());
                var1 = new String[var2.size()];
                Enumeration var3 = var2.keys();

                for(int var4 = 0; var3.hasMoreElements(); ++var4) {
                    var1[var4] = (String)var3.nextElement();
                }
            } else {
                var1 = this.getInitialServices(this.getBootstrapHost(), this.getBootstrapPort());
            }

            this.cachedListOfInitialServices = var1;
        }

        return this.cachedListOfInitialServices;
    }

    String[] getInitialServices(String var1, int var2) throws SystemException {
        try {
            Representation var3 = this.getInitialRep(var1, var2);
            InitialNameStub var4 = new InitialNameStub(var3);
            return var4.list_initial_services();
        } catch (SystemException var5) {
            throw var5;
        }
    }

    private Representation getInitialRep(String var1, int var2) throws SystemException {
        try {
            byte[] var4 = new byte[]{73, 78, 73, 84};
            IOR var5 = new IOR("", var1, var2, var4);
            RepImpl var3 = new RepImpl(var5);
            var3.setSCID(this.IBMSCID);
            var3.setORB(this);
            var3.setLocation(var5);
            return var3;
        } catch (SystemException var6) {
            throw var6;
        }
    }

    private Representation getInitialRep() throws SystemException {
        if (this.initialRep == null) {
            try {
                byte[] var1 = new byte[]{73, 78, 73, 84};
                IOR var2 = new IOR("", this.getBootstrapHost(), this.getBootstrapPort(), var1);
                this.initialRep = new RepImpl(var2);
                this.initialRep.setSCID(this.IBMSCID);
                this.initialRep.setORB(this);
                this.initialRep.setLocation(var2);
            } catch (SystemException var3) {
                throw var3;
            }
        }

        return this.initialRep;
    }

    public org.omg.CORBA.Object resolve_initial_references(String var1) throws InvalidName {
        return null;
    }

    public org.omg.CORBA.Object resolve_initial_references_remote(String var1, String[] var2) throws SystemException, InvalidName {
        return null;
    }

    public NVList create_list(int var1) {
        return new NVListImpl(this, var1);
    }

    public NamedValue create_named_value(String var1, Any var2, int var3) {
        return new NamedValueImpl(this, var1, var2, var3);
    }

    public ExceptionList create_exception_list() {
        return new ExceptionListImpl(this);
    }

    public ContextList create_context_list() {
        return new ContextListImpl(this);
    }

    public Context get_default_context() {
        throw new NO_IMPLEMENT();
    }

    public Environment create_environment() {
        return new EnvironmentImpl(this);
    }

    public void send_multiple_requests_oneway(Request[] var1) {
        throw new NO_IMPLEMENT();
    }

    public void send_multiple_requests_deferred(Request[] var1) {
        throw new NO_IMPLEMENT();
    }

    public boolean poll_next_response() {
        throw new NO_IMPLEMENT();
    }

    public Request get_next_response() {
        throw new NO_IMPLEMENT();
    }

    synchronized String objectToURL(org.omg.CORBA.Object var1, String var2) throws Exception {
        if (urlToObjectFacility == null) {
            urlToObjectFacility = new URLToObjectFacility(this);
        }

        return urlToObjectFacility.objectToURL(var2.toLowerCase(), var1);
    }

    public synchronized org.omg.CORBA.Object URLToObject(String var1) throws Exception {
        if (urlToObjectFacility == null) {
            urlToObjectFacility = new URLToObjectFacility(this);
        }

        return urlToObjectFacility.URLToObject(var1);
    }

    public String object_to_string(org.omg.CORBA.Object var1) {
        Representation var2 = null;

        try {
            if (var1 == null) {
                return (new IOR("", (Profile)null)).stringify();
            } else {
                ObjectImpl var3 = (ObjectImpl)var1;

                try {
                    var2 = (Representation)var3._get_delegate();
                } catch (Exception var5) {
                }

                if (var2 == null) {
                    this.connect(var1);
                    var2 = (Representation)var3._get_delegate();
                }

                return var2.stringify();
            }
        } catch (SystemException var6) {
            throw var6;
        }
    }

    private static int hexOf(char var0) throws SystemException {
        int var1 = var0 - 48;
        if (var1 >= 0 && var1 <= 9) {
            return var1;
        } else {
            var1 = var0 - 97 + 10;
            if (var1 >= 10 && var1 <= 15) {
                return var1;
            } else {
                var1 = var0 - 65 + 10;
                if (var1 >= 10 && var1 <= 15) {
                    return var1;
                } else {
                    throw new DATA_CONVERSION(1, CompletionStatus.COMPLETED_NO);
                }
            }
        }
    }

    public org.omg.CORBA.Object string_to_object(String var1) {
        if (!var1.startsWith("IOR:")) {
            throw new DATA_CONVERSION(3, CompletionStatus.COMPLETED_NO);
        } else if ((var1.length() & 1) != 0) {
            throw new DATA_CONVERSION(2, CompletionStatus.COMPLETED_NO);
        } else {
            byte[] var2 = new byte[(var1.length() - 4) / 2];
            int var4 = 4;

            for(int var5 = 0; var4 < var1.length(); ++var5) {
                var2[var5] = (byte)(hexOf(var1.charAt(var4)) << 4 & 240);
                var2[var5] |= (byte)(hexOf(var1.charAt(var4 + 1)) << 0 & 15);
                var4 += 2;
            }

            CDRInputStream var3 = new CDRInputStream(this, var2, var2.length);
            var3.consumeEndian();
            return var3.read_Object();
        }
    }

    protected org.omg.CORBA.Object iorToObjRef(IOR var1) throws SystemException {
        byte[] var2 = var1.getProfile().getObjectKey();
        String var3 = var1.getProfile().getHost();

        try {
            int var5 = Utility.bytesToInt(var2, 0);
            int var6 = Utility.bytesToInt(var2, 8);
            if ((var5 == 1246577225 || var5 == 1129136713) && (var6 == this.transientServerId || var6 > 0 && var6 == this.serverId)) {
                Server var7 = this.subcontractRegistry.lookupServerSubcontract(var2);
                org.omg.CORBA.Object var4 = var7.getServant(var2);
                if (var4 != null) {
                    return var4;
                }
            }
        } catch (Exception var8) {
        }

        CORBAObjectImpl var9 = new CORBAObjectImpl();
        Representation var10 = this.subcontractRegistry.getClientRepresentation(var2);
        var10.setIOR(var1);
        var10.setORB(this);
        ObjectImpl var11 = (ObjectImpl)var9;
        var11._set_delegate(var10);
        return var9;
    }

    public void connect(org.omg.CORBA.Object var1) {
    }

    void register(org.omg.CORBA.Object var1) {
        if (this.objectResolver == null) {
            System.out.println("No object resolver");
            throw new OBJ_ADAPTER();
        }
    }

    void unregister(org.omg.CORBA.Object var1) {
    }

    void internalConnect(org.omg.CORBA.Object var1) {
    }

    Connection acceptClientConnection(Socket var1) {
        return this.getConnectionTable().get(var1);
    }

    public void disconnect(org.omg.CORBA.Object var1) {
    }

    int getTransientServerId() {
        return this.transientServerId;
    }

    int getServerId() {
        return this.serverId;
    }

    public SubcontractRegistryClass getSubcontractRegistry() {
        return this.subcontractRegistry;
    }

    protected void registerServerSubcontract(Server var1, int var2) {
        this.subcontractRegistry.registerServer(var1, var2);
    }

    Server lookupServerSubcontract(int var1) {
        return this.subcontractRegistry.lookupServerSubcontract(var1);
    }

    protected void registerClientSubcontract(String var1, int var2) throws ClassNotFoundException {
        this.subcontractRegistry.registerClient(var1, var2);
    }

    protected void dispatch(IIOPInputStream var1, IIOPOutputStream var2) throws SystemException, ForwardException {
        RequestMessage var3 = (RequestMessage)var1.getMessage();
        Server var4 = this.subcontractRegistry.lookupServerSubcontract(var3.getObjectKey());
        if (var4 == null) {
            throw new OBJ_ADAPTER(1, CompletionStatus.COMPLETED_NO);
        } else {
            var4.dispatch(var1, var2);
        }
    }

    protected void locate(IIOPInputStream var1, IIOPOutputStream var2) throws ForwardException {
        LocateRequestMessage var3 = (LocateRequestMessage)var1.getMessage();
        byte[] var4 = var3.getObjectKey();
        Server var5 = this.subcontractRegistry.lookupServerSubcontract(var4);
        if (var5 == null) {
            throw new OBJ_ADAPTER(2, CompletionStatus.COMPLETED_NO);
        } else {
            var5.locate(var4);
        }
    }

    Server createDefaultServer() throws SystemException {
        Server var1 = this.subcontractRegistry.lookupServerSubcontract(this.IBMSCID);
        if (var1 == null) {
            throw new OBJ_ADAPTER(3, CompletionStatus.COMPLETED_NO);
        } else {
            return var1;
        }
    }

    public Server createServer(int var1) throws SystemException {
        return this.subcontractRegistry.lookupServerSubcontract(var1);
    }

    ImplDefFactory getImplDefFactory() {
        return this.implFactory;
    }

    protected void registerImplDef(ImplDef var1, String var2) throws Exception {
    }

    ImplDef lookupImplDef(int var1) throws SystemException {
        return null;
    }

    ImplDef lookupImplDef(String var1) throws SystemException {
        return null;
    }

    public synchronized void Wait() {
        try {
            this.wait();
        } catch (Exception var2) {
        }

    }

    public synchronized void timedWait(int var1) {
        try {
            this.wait((long)var1 * 1000L);
        } catch (Exception var3) {
        }

    }

    public synchronized void timedWait(long var1) {
        try {
            this.wait(var1);
        } catch (Exception var4) {
        }

    }

    public synchronized void releaseWait() {
        this.notifyAll();
    }

    void putRequestHolderTable(RequestHolder var1) {
        try {
            this.requestHolderTable.put(new Integer(var1.requestId()), var1);
        } catch (NullPointerException var3) {
            throw new INTERNAL(0, CompletionStatus.COMPLETED_NO);
        }
    }

    RequestHolder getRequestHolderTable(int var1) {
        try {
            return (RequestHolder)this.requestHolderTable.get(new Integer(var1));
        } catch (NullPointerException var3) {
            throw new INTERNAL(0, CompletionStatus.COMPLETED_NO);
        }
    }

    RequestHolder removeRequestHolderTable(int var1) {
        try {
            return (RequestHolder)this.requestHolderTable.remove(new Integer(var1));
        } catch (NullPointerException var3) {
            throw new INTERNAL(0, CompletionStatus.COMPLETED_NO);
        }
    }

    void addRequestInterceptor(RequestInterceptor var1, boolean var2) {
        if (var2) {
            if (this.firstRequestInterceptor != null) {
                throw new BAD_PARAM();
            } else {
                this.firstRequestInterceptor = var1;
            }
        } else {
            try {
                this.requestInterceptorList.insertElementAt(var1, 0);
            } catch (ArrayIndexOutOfBoundsException var4) {
                throw new BAD_PARAM();
            }
        }
    }

    void removeRequestInterceptor(RequestInterceptor var1) {
        if (this.firstRequestInterceptor == var1) {
            this.firstRequestInterceptor = null;
        } else {
            this.requestInterceptorList.removeElement(var1);
        }

    }

    void register_service_information(short var1, ServiceInformation var2) {
        this.serviceInformationList.put(new Short(var1), var2);
    }

    public boolean get_service_information(short var1, ServiceInformationHolder var2) {
        Short var3 = new Short(var1);
        boolean var4 = this.serviceInformationList.containsKey(var3);
        if (var4) {
            var2.value = (ServiceInformation)this.serviceInformationList.get(var3);
        }

        return var4;
    }

    public Current get_current(String var1) {
        CurrentCreator var2 = (CurrentCreator)this.currentCreators.get(var1);
        return var2 != null ? var2.create_current() : null;
    }

    final void registerCurrentCreator(String var1, CurrentCreator var2) {
        this.currentCreators.put(var1, var2);
    }

    public static void registerImpl(String var0, Class var1) {
        Class var2 = (Class)impls.get(var0);
        if (var2 == null) {
            impls.put(var0, var1);
        }

    }

    public static Object getImpl(String var0) {
        Class var1 = (Class)impls.get(var0);
        if (var1 == null) {
            int var3 = var0.lastIndexOf(46);
            String var2;
            if (var3 < 0) {
                var2 = '_' + var0 + "Impl";
            } else {
                var2 = var0.substring(0, var3) + "._" + var0.substring(var3 + 1) + "Impl";
            }

            try {
                var1 = Class.forName(var2);
            } catch (Exception var6) {
                var1 = null;
            }
        }

        if (var1 == null) {
            return null;
        } else {
            try {
                return var1.newInstance();
            } catch (Exception var5) {
                return null;
            }
        }
    }

    public OutputStream create_output_stream() {
        return new CDROutputStream(this);
    }

    public TypeCode get_primitive_tc(TCKind var1) {
        return null;
    }

    public TypeCode create_struct_tc(String var1, String var2, StructMember[] var3) {
        return null;
    }

    public TypeCode create_union_tc(String var1, String var2, TypeCode var3, UnionMember[] var4) {
        return null;
    }

    public TypeCode create_enum_tc(String var1, String var2, String[] var3) {
        return null;
    }

    public TypeCode create_alias_tc(String var1, String var2, TypeCode var3) {
        return null;
    }

    public TypeCode create_exception_tc(String var1, String var2, StructMember[] var3) {
        return null;
    }

    public TypeCode create_interface_tc(String var1, String var2) {
        return null;
    }

    public TypeCode create_string_tc(int var1) {
        return null;
    }

    public TypeCode create_wstring_tc(int var1) {
        return null;
    }

    public TypeCode create_sequence_tc(int var1, TypeCode var2) {
        return null;
    }

    public TypeCode create_recursive_sequence_tc(int var1, int var2) {
        return null;
    }

    public TypeCode create_array_tc(int var1, TypeCode var2) {
        return null;
    }

    public Any create_any() {
        return null;
    }

    public void release() {
        if (this.connectionTable != null) {
            this.connectionTable.release();
        }

    }

    public void shutdown(boolean var1) {
    }

    public void setCallback(ORBCallback var1) {
        if (this.m_callback != null) {
            throw new INTERNAL(1, CompletionStatus.COMPLETED_NO);
        } else {
            this.m_callback = var1;
        }
    }

    ORBCallback getCallback() {
        return this.m_callback;
    }

    static {
        Class var0 = ORB.class;
        Class[] var1 = new Class[]{var0};
        boolean var2 = false;

        try {
            var0.getMethod("initCause", var1);
            var2 = true;
        } catch (NoSuchMethodException var4) {
            var2 = false;
        }

        g_initCause = var2;
    }
}