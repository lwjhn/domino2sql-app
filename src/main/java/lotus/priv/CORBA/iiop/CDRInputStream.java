//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lotus.priv.CORBA.iiop;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;

public class CDRInputStream extends InputStream {
    protected boolean littleEndian;
    protected int index;
    protected int size;
    protected byte[] buf;
    protected ORB orb;

    public CDRInputStream(ORB var1, byte[] var2, int var3) {
        this(var1, var2, var3, false);
    }

    public CDRInputStream(ORB var1, byte[] var2, int var3, boolean var4) {
        this.orb = var1;
        this.littleEndian = var4;
        this.size = var3;
        this.buf = var2;
    }

    public CDRInputStream(CDRInputStream var1) {
        this.littleEndian = var1.littleEndian;
        this.index = var1.index;
        this.size = var1.size;
        this.buf = var1.buf;
        this.orb = var1.orb;
    }

    public CDRInputStream dup() {
        return new CDRInputStream(this);
    }

    private final void alignAndCheck(int var1, int var2) {
        int var3 = this.index - 1 + var1 & ~(var1 - 1);
        if (var3 + var2 <= this.size) {
            this.index = var3;
        } else {
            this.grow(var1, var2);
        }
    }

    protected void grow(int var1, int var2) {
        throw new MARSHAL();
    }

    public void setOffset(int var1) {
        this.index = var1;
    }

    public final void consumeEndian() {
        this.littleEndian = this.read_boolean();
    }

    public final boolean read_boolean() {
        boolean var1 = this.read_octet() != 0;
        return var1;
    }

    public final char read_char() {
        this.alignAndCheck(1, 1);
        char var1 = (char)(this.buf[this.index++] & 255);
        if (var1 > 255) {
            throw new DATA_CONVERSION(6, CompletionStatus.COMPLETED_NO);
        } else {
            return var1;
        }
    }

    public final char read_wchar() {
        this.alignAndCheck(2, 2);
        int var1;
        int var2;
        if (this.littleEndian) {
            var2 = this.buf[this.index++] << 0 & 255;
            var1 = this.buf[this.index++] << 8 & '\uff00';
        } else {
            var1 = this.buf[this.index++] << 8 & '\uff00';
            var2 = this.buf[this.index++] << 0 & 255;
        }

        return (char)(var1 | var2);
    }

    public final byte read_octet() {
        this.alignAndCheck(1, 1);
        return this.buf[this.index++];
    }

    public final short read_short() {
        this.alignAndCheck(2, 2);
        int var1;
        int var2;
        if (this.littleEndian) {
            var2 = this.buf[this.index++] << 0 & 255;
            var1 = this.buf[this.index++] << 8 & '\uff00';
        } else {
            var1 = this.buf[this.index++] << 8 & '\uff00';
            var2 = this.buf[this.index++] << 0 & 255;
        }

        return (short)(var1 | var2);
    }

    public final short read_ushort() {
        return this.read_short();
    }

    public final int read_long() {
        this.alignAndCheck(4, 4);
        int var1;
        int var2;
        int var3;
        int var4;
        if (this.littleEndian) {
            var4 = this.buf[this.index++] << 0 & 255;
            var3 = this.buf[this.index++] << 8 & '\uff00';
            var2 = this.buf[this.index++] << 16 & 16711680;
            var1 = this.buf[this.index++] << 24 & -16777216;
        } else {
            var1 = this.buf[this.index++] << 24 & -16777216;
            var2 = this.buf[this.index++] << 16 & 16711680;
            var3 = this.buf[this.index++] << 8 & '\uff00';
            var4 = this.buf[this.index++] << 0 & 255;
        }

        return var1 | var2 | var3 | var4;
    }

    public final int read_ulong() {
        return this.read_long();
    }

    public final long read_longlong() {
        this.alignAndCheck(8, 8);
        long var1;
        long var3;
        if (this.littleEndian) {
            var3 = (long)this.read_long() & 4294967295L;
            var1 = (long)this.read_long() << 32;
        } else {
            var1 = (long)this.read_long() << 32;
            var3 = (long)this.read_long() & 4294967295L;
        }

        return var1 | var3;
    }

    public final long read_ulonglong() {
        return this.read_longlong();
    }

    public final float read_float() {
        return Float.intBitsToFloat(this.read_long());
    }

    public final double read_double() {
        return Double.longBitsToDouble(this.read_longlong());
    }

    public final String read_string() {
        int var3 = this.read_long();
        if (var3 == 0) {
            return null;
        } else {
            byte[] var1 = new byte[var3];
            this.read_octet_array(var1, 0, var3);
            String var2 = new String(var1, 0, 0, var1.length - 1);

            for(int var4 = 0; var4 < var2.length(); ++var4) {
                if (var2.charAt(var4) > 255) {
                    throw new DATA_CONVERSION(6, CompletionStatus.COMPLETED_MAYBE);
                }
            }

            return var2;
        }
    }

    public final String read_wstring() {
        int var2 = this.read_long();
        if (var2 == 0) {
            return null;
        } else {
            char[] var1 = new char[var2 - 1];
            this.read_wchar_array(var1, 0, var2 - 1);
            this.index += 2;
            return new String(var1);
        }
    }

    public final String peek_string() {
        this.alignAndCheck(4, 4);
        int var4;
        int var5;
        int var6;
        int var7;
        if (this.littleEndian) {
            var7 = this.buf[this.index] << 0 & 255;
            var6 = this.buf[this.index + 1] << 8 & '\uff00';
            var5 = this.buf[this.index + 2] << 16 & 16711680;
            var4 = this.buf[this.index + 3] << 24 & -16777216;
        } else {
            var4 = this.buf[this.index] << 24 & -16777216;
            var5 = this.buf[this.index + 1] << 16 & 16711680;
            var6 = this.buf[this.index + 2] << 8 & '\uff00';
            var7 = this.buf[this.index + 3] << 0 & 255;
        }

        int var3 = var4 | var5 | var6 | var7;
        if (var3 == 0) {
            return "";
        } else {
            byte[] var1 = new byte[var3];
            int var8 = this.index + 4;
            this.alignAndCheck(1, 1);
            int var9 = this.size - var8;
            if (var3 > var9) {
                throw new INTERNAL(6, CompletionStatus.COMPLETED_MAYBE);
            } else {
                System.arraycopy(this.buf, var8, var1, 0, var3);
                String var2 = new String(var1, 0, 0, var1.length - 1);

                for(int var10 = 0; var10 < var2.length(); ++var10) {
                    if (var2.charAt(var10) > 255) {
                        throw new DATA_CONVERSION(6, CompletionStatus.COMPLETED_MAYBE);
                    }
                }

                return var2;
            }
        }
    }

    public final void read_octet_array(byte[] var1, int var2, int var3) {
        if (var1 == null) {
            throw new BAD_PARAM();
        } else {
            int var6;
            for(int var4 = var2; var4 < var3 + var2; var4 += var6) {
                this.alignAndCheck(1, 1);
                int var5 = this.size - this.index;
                int var7 = var3 + var2 - var4;
                var6 = var7 < var5 ? var7 : var5;
                System.arraycopy(this.buf, this.index, var1, var4, var6);
                this.index += var6;
            }

        }
    }

    public final Principal read_Principal() {
        int var1 = this.read_long();
        byte[] var2 = new byte[var1];
        this.read_octet_array(var2, 0, var1);
        PrincipalImpl var3 = new PrincipalImpl();
        var3.name(var2);
        return var3;
    }

    public final TypeCode read_TypeCode() {
        TypeCodeImpl var1 = new TypeCodeImpl(this.orb);
        var1.read_value(this);
        return var1;
    }

    public final Any read_any() {
        Any var1 = this.orb.create_any();
        TypeCodeImpl var2 = new TypeCodeImpl(this.orb);
        var2.read_value(this);
        var1.read_value(this, var2);
        return var1;
    }

    public final Object read_Object() {
        try {
            IOR var1 = new IOR();
            var1.read(this);
            return var1.is_nil() ? null : this.orb.iorToObjRef(var1);
        } catch (Exception var2) {
            var2.printStackTrace();
            throw new MARSHAL(4, CompletionStatus.COMPLETED_NO);
        }
    }

    public final Serializable read_serializableObject() {
        try {
            int var1 = this.read_long();
            ObjectInputStream var2 = new ObjectInputStream(new ByteArrayInputStream(this.buf, this.index, var1));
            java.lang.Object var3 = var2.readObject();
            this.index += var1;
            return (Serializable)var3;
        } catch (Exception var4) {
            throw new MARSHAL(4, CompletionStatus.COMPLETED_NO);
        }
    }

    public final void read_boolean_array(boolean[] var1, int var2, int var3) {
        for(int var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = this.read_boolean();
        }

    }

    public final void read_char_array(char[] var1, int var2, int var3) {
        for(int var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = this.read_char();
        }

    }

    public final void read_wchar_array(char[] var1, int var2, int var3) {
        for(int var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = this.read_wchar();
        }

    }

    public final void read_short_array(short[] var1, int var2, int var3) {
        for(int var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = this.read_short();
        }

    }

    public final void read_ushort_array(short[] var1, int var2, int var3) {
        this.read_short_array(var1, var2, var3);
    }

    public final void read_long_array(int[] var1, int var2, int var3) {
        for(int var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = this.read_long();
        }

    }

    public final void read_ulong_array(int[] var1, int var2, int var3) {
        this.read_long_array(var1, var2, var3);
    }

    public final void read_longlong_array(long[] var1, int var2, int var3) {
        for(int var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = this.read_longlong();
        }

    }

    public final void read_ulonglong_array(long[] var1, int var2, int var3) {
        this.read_longlong_array(var1, var2, var3);
    }

    public final void read_float_array(float[] var1, int var2, int var3) {
        for(int var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = this.read_float();
        }

    }

    public final void read_double_array(double[] var1, int var2, int var3) {
        for(int var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = this.read_double();
        }

    }

    public final void rewind() {
        this.index = 0;
    }

    public final boolean isAtEnd() {
        return this.index == this.size;
    }
}