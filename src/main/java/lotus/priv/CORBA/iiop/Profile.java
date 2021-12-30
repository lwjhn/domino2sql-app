//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lotus.priv.CORBA.iiop;

import com.lwjhn.domino2sql.DiiopApplication;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.SystemException;

public final class Profile {
    private String host;
    private int port;
    private byte major;
    private byte minor;
    private byte[] objectKey;
    private byte[] data;
    private int[] componentTags;
    private byte[][] componentData;

    private void dprint(String var1) {
        Utility.dprint(this, var1);
    }

    Profile(byte[] var1) throws SystemException {
        CDRInputStream var2 = new CDRInputStream((ORB)null, var1, var1.length);
        var2.consumeEndian();
        this.major = var2.read_octet();
        this.minor = var2.read_octet();
        this.host = var2.read_string();
        //this.host = DiiopApplication.host;
        this.port = var2.read_short() & '\uffff';
        //this.port = DiiopApplication.port;
        this.objectKey = new byte[var2.read_long()];
        var2.read_octet_array(this.objectKey, 0, this.objectKey.length);
        if (this.minor > 0) {
            int var3 = var2.read_long();
            if (var3 > 0) {
                this.componentTags = new int[var3];
                this.componentData = new byte[var3][];

                for(int var4 = 0; var4 < var3; ++var4) {
                    this.componentTags[var4] = var2.read_long();
                    int var5 = var2.read_long();
                    this.componentData[var4] = new byte[var5];
                    var2.read_octet_array(this.componentData[var4], 0, var5);
                }
            }
        }

        this.data = var1;
    }

    Profile(String var1, int var2, byte[] var3) {
        this.host = var1;
        this.port = var2;
        this.objectKey = var3;
        this.major = 1;
        this.minor = 0;
    }

    Profile(String var1, int var2, byte[] var3, String var4) {
        this.host = var1;
        this.port = var2;
        this.objectKey = var3;
        this.major = 1;
        this.minor = 1;
        if (var4 != null) {
            this.componentTags = new int[1];
            this.componentTags[0] = 1229081859;
            this.componentData = new byte[1][];
            this.componentData[0] = new byte[var4.length()];
            this.componentData[0] = var4.getBytes();
        }

    }

    byte[] getEncapsulation() throws SystemException {
        if (this.data != null) {
            return this.data;
        } else {
            CDROutputStream var1 = new CDROutputStream((ORB)null);
            var1.putEndian();
            var1.write_octet(this.major);
            var1.write_octet(this.minor);
            var1.write_string(this.host);
            var1.write_short((short)this.port);
            var1.write_long(this.objectKey.length);
            var1.write_octet_array(this.objectKey, 0, this.objectKey.length);
            if (this.minor > 0) {
                if (this.componentTags != null) {
                    int var2 = this.componentTags.length;
                    var1.write_long(var2);

                    for(int var3 = 0; var3 < var2; ++var3) {
                        var1.write_long(this.componentTags[var3]);
                        var1.write_long(this.componentData[var3].length);
                        var1.write_octet_array(this.componentData[var3], 0, this.componentData[var3].length);
                    }
                } else {
                    var1.write_long(0);
                }
            }

            this.data = var1.toByteArray();
            return this.data;
        }
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public byte[] getObjectKey() {
        return this.objectKey;
    }

    public byte[] getTaggedComponent(int var1) throws SystemException {
        if (this.minor > 0 && this.componentTags != null) {
            for(int var2 = 0; var2 < this.componentTags.length; ++var2) {
                if (this.componentTags[var2] == var1) {
                    return this.componentData[var2];
                }
            }
        }

        throw new INV_OBJREF(2, CompletionStatus.COMPLETED_NO);
    }

    boolean isEquivalent(Profile var1) {
        if (this.getPort() == var1.getPort() && this.getHost().equalsIgnoreCase(var1.getHost()) && this.getObjectKey().length == var1.getObjectKey().length) {
            byte[] var2 = var1.getObjectKey();

            for(int var3 = 0; var3 < this.objectKey.length; ++var3) {
                if (this.objectKey[var3] != var2[var3]) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}