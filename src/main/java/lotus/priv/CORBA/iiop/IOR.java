//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lotus.priv.CORBA.iiop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class IOR implements Cloneable {
    private String typeId;
    private int[] profileTags;
    private byte[][] profileData;
    private Profile iop;
    public static final int InternetIOPTag = 0;
    private byte[] tmpKey;
    boolean headless = false;

    IOR() {
    }

    IOR(byte[] var1, String var2) {
        this.typeId = var2;
        this.tmpKey = var1;
        this.headless = true;
    }

    public IOR(String var1, String var2, int var3, byte[] var4) throws SystemException {
        Profile var5 = new Profile(var2, var3, var4);
        this.typeId = var1;
        if (var1.length() == 0 && var5 == null) {
            this.profileTags = new int[0];
        } else {
            this.putProfile(var5);
        }
    }

    public IOR(String var1, Profile var2) throws SystemException {
        this.typeId = var1;
        if (var1.length() == 0 && var2 == null) {
            this.profileTags = new int[0];
        } else {
            this.putProfile(var2);
        }
    }

    void capitate(String var1, int var2, String var3) throws SystemException {
        Profile var4 = new Profile(var1, var2, this.tmpKey, var3);
        if (this.typeId.length() == 0 && var4 == null) {
            this.profileTags = new int[0];
        } else {
            this.putProfile(var4);
        }
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException var2) {
            return null;
        }
    }

    public void read(InputStream var1) throws SystemException {
        this.typeId = var1.read_string();
        int var2 = var1.read_long();
        this.profileTags = new int[var2];
        this.profileData = new byte[var2][];

        for(int var3 = 0; var3 < var2; ++var3) {
            this.profileTags[var3] = var1.read_long();
            this.profileData[var3] = new byte[var1.read_long()];
            var1.read_octet_array(this.profileData[var3], 0, this.profileData[var3].length);
        }

    }

    public void write(OutputStream var1) throws SystemException {
        var1.write_string(this.typeId);
        var1.write_long(this.profileTags.length);

        for(int var2 = 0; var2 < this.profileTags.length; ++var2) {
            var1.write_long(this.profileTags[var2]);
            var1.write_long(this.profileData[var2].length);
            var1.write_octet_array(this.profileData[var2], 0, this.profileData[var2].length);
        }

    }

    public synchronized Profile getProfile() throws SystemException {
        if (this.iop != null) {
            return this.iop;
        } else {
            for(int var1 = 0; var1 < this.profileTags.length; ++var1) {
                if (this.profileTags[var1] == 0) {
                    this.iop = new Profile(this.profileData[var1]);
                    return this.iop;
                }
            }

            throw new INV_OBJREF(1, CompletionStatus.COMPLETED_NO);
        }
    }

    void putProfile(Profile var1) throws SystemException {
        int var4 = 0;
        if (this.profileTags != null) {
            var4 = this.profileTags.length;
        }

        int var5;
        for(var5 = 0; var5 < var4; ++var5) {
            if (this.profileTags[var5] == 0) {
                this.profileData[var5] = var1.getEncapsulation();
                this.iop = var1;
                return;
            }
        }

        int[] var2 = new int[var4 + 1];
        byte[][] var3 = new byte[var4 + 1][];

        for(var5 = 0; var5 < var4; ++var5) {
            var2[var5] = this.profileTags[var5];
            var3[var5] = this.profileData[var5];
        }

        var2[var5] = 0;
        var3[var5] = var1.getEncapsulation();
        this.profileTags = var2;
        this.profileData = var3;
        this.iop = var1;
    }

    public String getTypeId() {
        return this.typeId;
    }

    String stringify() throws SystemException {
        CDROutputStream var1 = new CDROutputStream((ORB)null);
        var1.putEndian();
        this.write(var1);
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();

        try {
            var1.writeTo(new HexOutputStream(var2));
        } catch (IOException var4) {
            throw new INTERNAL(10, CompletionStatus.COMPLETED_NO);
        }

        return "IOR:" + var2;
    }

    boolean is_nil() {
        return this.profileTags.length == 0;
    }

    boolean isEquivalent(IOR var1) throws SystemException {
        return this.getProfile().isEquivalent(var1.getProfile());
    }
}