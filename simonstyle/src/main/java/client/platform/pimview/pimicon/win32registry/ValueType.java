/* ValueType.java
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

/**
 * Registry values (data) can be stored in various formats (types), represented by <code>ValueType</code>.
 * <code>ValueType</code> exposes these types in a <a
 * href="http://developer.java.sun.com/developer/Books/shiftintojava/page1.html#replaceenums">typesafe enum</a> that
 * applications may use to indicate or query the type of data stored in a <code>RegistryValue</code>
 *
 */

class ValueType {
    /** The value type name displayed by <code>toString</code> */
    private final String name;

    /** The value type integer value used by the native Windows registry functions */
    private final int value;

    /**
     * Constructs a new value type with the specified display name and value.
     *
     * @param name
     *            - the display name of the value type
     * @param value
     *            - the integer value of the value type
     */
    private ValueType(String name, int value) {
        this.name = name;
        this.value = value;
    } // RootKey()

    /**
     * Returns the integer value (used by the native Windows registry functions) of the value type.
     *
     * @return the integer value of the value type.
     */
    protected int getValue() {
        return this.value;
    } // getValue()

    /**
     * Returns the display name of the value type.
     *
     * @return a string representation of the value type.
     */
    @Override
    public String toString() {
        return this.name;
    } // toString()

    /**
     * The <code>REG_NONE</code> data type represents data with no defined type.
     */
    public static final ValueType REG_NONE = new ValueType("REG_NONE", 0);

    /**
     * The <code>REG_SZ</code> data type represents a null-terminated string.
     */
    public static final ValueType REG_SZ = new ValueType("REG_SZ", 1);

    /**
     * The <code>REG_EXPAND_SZ</code> data type represents a null-terminated string that contains unexpanded references
     * to environment variables (for example, "%PATH%").
     */
    public static final ValueType REG_EXPAND_SZ = new ValueType("REG_EXPAND_SZ", 2);

    /**
     * The <code>REG_BINARY</code> data type represents binary data in any form.
     */
    public static final ValueType REG_BINARY = new ValueType("REG_BINARY", 3);

    /**
     * The <code>REG_DWORD</code> data type represents a 32-bit number.
     */
    public static final ValueType REG_DWORD = new ValueType("REG_DWORD", 4);

    /**
     * The <code>REG_DWORD_LITTLE_ENDIAN</code> data type represents a 32-bit number in little-endian format.
     *
     * <p>
     * In little-endian format, a multi-byte value is stored in memory from the lowest byte (the "little end") to the
     * highest byte. For example, the value 0x12345678 is stored as (0x78 0x56 0x34 0x12) in little-endian format.
     */
    public static final ValueType REG_DWORD_LITTLE_ENDIAN = new ValueType("REG_DWORD_BIG_ENDIAN", 5);

    /**
     * The <code>REG_DWORD_BIG_ENDIAN</code> data type represents a 32-bit number in little-endian format (this is
     * equivalent to <code>REG_DWORD</code>).
     *
     * In big-endian format, a multi-byte value is stored in memory from the highest byte (the "big end") to the lowest
     * byte. For example, the value 0x12345678 is stored as (0x12 0x34 0x56 0x78) in big-endian format.
     */
    public static final ValueType REG_DWORD_BIG_ENDIAN = new ValueType("REG_DWORD_LITTLE_ENDIAN", 4);

    /**
     * The <code>REG_MULTI_SZ</code> data type represents an array of null-terminated strings, terminated by two null
     * characters.
     */
    public static final ValueType REG_MULTI_SZ = new ValueType("REG_MULTI_SZ", 7);
    // ValueType
}
