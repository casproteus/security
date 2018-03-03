/* RegistryValue.java
 *
 * jRegistryKey - A JNI wrapper of the Windows Registry functions.
 * Copyright (c) 2001, BEQ Technologies Inc.
 * #205, 3132 Parsons Road
 * Edmonton, Alberta
 * T6N 1L6 Canada
 * (780) 430-0056
 * (780) 437-6121 (fax)
 * http://www.beq.ca
 *
 * Original Author: Joe Robinson <joe@beq.ca>
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package client.platform.pimview.pimicon.win32registry;

import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.PIMPool;

/**
 * A representation of registry values. A registry value is defined as a collection of the following properties:
 * <code>name</code>, <code>type</code>, and <code>data</code>.
 *
 * <p>
 * A <code>name</code> consists of a string of one or more printable characters, excluding the backslash (\). A
 * <code>type</code> may be any of the defined <code>ValueType</code>'s. <code>Data</code> is any object, consistent
 * with the <code>ValueType</code>.
 */

public class RegistryValue {
    private String name = CASUtility.EMPTYSTR;
    private ValueType type = ValueType.REG_SZ;
    private Object data;

    /**
     * Constructs a new, empty, <code>RegistryValue</code>.
     */
    public RegistryValue() {
    } // RegistryValue()

    /**
     * Constructs a new <code>RegistryValue</code> with the specified data. The type is defaulted to
     * <code>ValueType.REG_SZ</code>. The <code>name</code> defaults to PIMUtility.EMPTYSTR; the default value name.
     *
     * @param data
     *            the <code>RegistryValue</code>'s data
     */
    public RegistryValue(Object data) {
        this.data = data;
    } // RegistryValue()

    /**
     * Constructs a new <code>RegistryValue</code> with the specified name and data. The type is defaulted to
     * <code>ValueType.REG_SZ</code>.
     *
     * @param name
     *            the <code>RegistryValue</code>'s name
     * @param data
     *            the <code>RegistryValue</code>'s data
     */
    public RegistryValue(String name, Object data) {
        this.name = name;
        this.data = data;
    } // RegistryValue()

    /**
     * Constructs a new <code>RegistryValue</code> with the specified name, type, and data.
     *
     * @param name
     *            the <code>RegistryValue</code>'s name
     * @param type
     *            the <code>RegistryValue</code>'s type
     * @param data
     *            the <code>RegistryValue</code>'s data
     */
    public RegistryValue(String name, ValueType type, Object data) {
        this.name = name;
        this.type = type;
        this.data = data;
    } // RegistryValue()

    /**
     * Constructs a new <code>RegistryValue</code> with the specified name, and data. The type is
     * <code>ValueType.REG_DWORD</code>.
     *
     * @param name
     *            the <code>RegistryValue</code>'s name
     * @param data
     *            the <code>RegistryValue</code>'s data
     */
    public RegistryValue(String name, boolean data) {
        this(name, ValueType.REG_DWORD, data ? Boolean.TRUE : Boolean.FALSE);
    } // RegistryValue()

    /**
     * Constructs a new <code>RegistryValue</code> with the specified name, and data. The type is
     * <code>ValueType.REG_BINARY</code>.
     *
     * @param name
     *            the <code>RegistryValue</code>'s name
     * @param data
     *            the <code>RegistryValue</code>'s data
     */
    public RegistryValue(String name, byte data) {
        this(name, ValueType.REG_BINARY, new Byte(data));
    } // RegistryValue()

    /**
     * Constructs a new <code>RegistryValue</code> with the specified name, and data. The type is
     * <code>ValueType.REG_DWORD</code>.
     *
     * @param name
     *            the <code>RegistryValue</code>'s name
     * @param data
     *            the <code>RegistryValue</code>'s data
     */
    public RegistryValue(String name, int data) {
        this(name, ValueType.REG_DWORD, PIMPool.pool.getKey(data));
    } // RegistryValue()

    /**
     * Constructs a new <code>RegistryValue</code> with the specified name, and data. The type is
     * <code>ValueType.REG_DWORD</code>.
     *
     * @param name
     *            the <code>RegistryValue</code>'s name
     * @param data
     *            the <code>RegistryValue</code>'s data
     */
    public RegistryValue(String name, long data) {
        this(name, ValueType.REG_DWORD, new Long(data));
    } // RegistryValue()

    /**
     * Constructs a new <code>RegistryValue</code> with the specified name, and data. The type is
     * <code>ValueType.REG_BINARY</code>.
     *
     * @param name
     *            the <code>RegistryValue</code>'s name
     * @param data
     *            the <code>RegistryValue</code>'s data
     */
    public RegistryValue(String name, float data) {
        this(name, ValueType.REG_BINARY, new Float(data));
    } // RegistryValue()

    /**
     * Constructs a new <code>RegistryValue</code> with the specified name, and data. The type is
     * <code>ValueType.REG_BINARY</code>.
     *
     * @param name
     *            the <code>RegistryValue</code>'s name
     * @param data
     *            the <code>RegistryValue</code>'s data
     */
    public RegistryValue(String name, double data) {
        this(name, ValueType.REG_BINARY, new Double(data));
    } // RegistryValue()

    /**
     * Returns this <code>RegistryValue</code>'s name.
     *
     * @return the <code>RegistryValue</code>'s name
     */
    public String getName() {
        return this.name;
    } // getName()

    /**
     * Sets this <code>RegistryValue</code>'s name.
     *
     * @param name
     *            the <code>RegistryValue</code>'s name
     */
    public void setName(
            String name) {
        this.name = name;
    } // setName()

    /**
     * Returns this <code>RegistryValue</code>'s <code>ValueType</code>.
     *
     * @return the <code>RegistryValue</code>'s type
     */
    public ValueType getType() {
        return this.type;
    } // getType()

    /**
     * Sets this <code>RegistryValue</code>'s <code>ValueType</code>.
     *
     * @param name
     *            the <code>RegistryValue</code>'s type
     */
    public void setType(
            ValueType type) {
        this.type = type;
    } // setType()

    /**
     * Returns this <code>RegistryValue</code>'s data.
     *
     * @return the <code>RegistryValue</code>'s data
     */
    public Object getData() {
        return this.data;
    } // getData()

    /**
     * Sets this <code>RegistryValue</code>'s data.
     *
     * @param name
     *            the <code>RegistryValue</code>'s data
     */
    public void setData(
            Object data) {
        this.data = data;
    } // setData()

    /**
     * Sets this <code>RegistryValue</code>'s data.
     *
     * @param name
     *            the <code>RegistryValue</code>'s data
     */
    public void setData(
            byte data) {
        setData(new Byte(data));
    } // setData()

    /**
     * Sets this <code>RegistryValue</code>'s data.
     *
     * @param name
     *            the <code>RegistryValue</code>'s data
     */
    public void setData(
            boolean data) {
        setData(data ? Boolean.TRUE : Boolean.FALSE);
    } // setData()

    /**
     * Sets this <code>RegistryValue</code>'s data.
     *
     * @param name
     *            the <code>RegistryValue</code>'s data
     */
    public void setData(
            int data) {
        setData(PIMPool.pool.getKey(data));
    } // setData()

    /**
     * Sets this <code>RegistryValue</code>'s data.
     *
     * @param name
     *            the <code>RegistryValue</code>'s data
     */
    public void setData(
            long data) {
        setData(new Long(data));
    } // setData()

    /**
     * Sets this <code>RegistryValue</code>'s data.
     *
     * @param name
     *            the <code>RegistryValue</code>'s data
     */
    public void setData(
            float data) {
        setData(new Float(data));
    } // setData()

    /**
     * Sets this <code>RegistryValue</code>'s data.
     *
     * @param name
     *            the <code>RegistryValue</code>'s data
     */
    public void setData(
            double data) {
        setData(new Double(data));
    } // setData()

    /**
     * Returns a string representation of this <code>RegistryValue</code>.
     *
     * @return the <code>RegistryValue</code> as a String
     *
     * @throws NullPointerException
     *             if the <code>name</code> or <code>data</code> properties are null.
     */
    @Override
    public String toString() {
        if (this.name == null || this.data == null) {
            throw new NullPointerException("Neither name not data may be null");
        } // if

        String dataval = "<no data>";

        if (this.type == ValueType.REG_SZ || this.type == ValueType.REG_EXPAND_SZ
                || this.type == ValueType.REG_MULTI_SZ) {
            dataval = data.toString();
        } // if
        else if (this.type == ValueType.REG_DWORD || this.type == ValueType.REG_DWORD_LITTLE_ENDIAN
                || this.type == ValueType.REG_DWORD_BIG_ENDIAN) {
            dataval = ((Integer) data).toString();
        } // else if
        else if (this.type == ValueType.REG_NONE || this.type == ValueType.REG_BINARY) {
            StringBuffer sb = new StringBuffer();
            byte[] b = (byte[]) data;

            // data.toString().getBytes();
            for (int index = 0; index < b.length; index++) {
                sb.append(CASUtility.SPACE + Byte.toString(b[index]));
            } // for
            dataval = sb.toString();
        } // else if

        return (this.name + ":" + type.toString() + ":" + dataval);
    } // toString()
} // RegistryValue
