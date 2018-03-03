/* RootKey.java
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
 * The Windows operating system defines standard registry keys ("root keys", represented by <code>RootKey</code>) that
 * are always open. <code>RootKey</code> exposes these predefined keys in a <a
 * href="http://developer.java.sun.com/developer/Books/shiftintojava/page1.html#replaceenums">typesafe enum</a> that
 * applications may use as entry points to the registry.
 *
 * <p>
 * The system provides two predefined keys at the root of the registry:
 *
 * <ul>
 * <li><code>HKEY_LOCAL_MACHINE</code>, and
 * <li><code>HKEY_USERS</code>
 * </ul>
 *
 * <p>
 * In addition, the system defines <code>HKEY_CURRENT_CONFIG</code> (a subkey of <code>HKEY_LOCAL_MACHINE</code>),
 * <code>HKEY_CURRENT_USER</code> (a subkey of <code>HKEY_USERS</code>), and <code>HKEY_CLASSES_ROOT</code> (a subkey
 * that merges information from <code>HKEY_LOCAL_MACHINE</code> and <code>HKEY_CURRENT_USER</code>). These registry keys
 * are valid for all Windows implementations of the registry. In addition, other predefined keys have been defined for
 * specific platforms.
 */

public final class RootKey {
    /** The root key name displayed by <code>toString</code> */
    private final String name;

    /** The root key value used by the native Windows registry functions */
    private final int value;

    /**
     * Constructs a new root key with the specified display name and value.
     *
     * @param name
     *            - the display name of the root key
     * @param value
     *            - the integer value of the root key
     */
    private RootKey(String name, int value) {
        this.name = name;
        this.value = value;
    } // RootKey()

    /**
     * Returns the integer value (used by the native Windows registry functions) of the root key.
     *
     * @return the integer value of the root key.
     */
    protected int getValue() {
        return this.value;
    } // getValue()

    /**
     * Returns the display name of the root key.
     *
     * @return a string representation of the root key.
     */
    @Override
    public String toString() {
        return this.name;
    } // toString()

    /**
     * Registry entries subordinate to this key define types (or classes) of documents and the properties associated
     * with those types. Shell and COM applications use the information stored under this key. File viewers and user
     * longerface extensions store their OLE class identifiers in <code>HKEY_CLASSES_ROOT</code>, and in-process servers
     * are registered in this key.
     */
    public static final RootKey HKEY_CLASSES_ROOT = new RootKey("HKEY_CLASSES_ROOT", 0x80000000);

    /**
     * Registry entries subordinate to this key define the preferences of the current user. These preferences include
     * the settings of environment variables, data about program groups, colors, printers, network connections, and
     * application preferences.
     */
    public static final RootKey HKEY_CURRENT_USER = new RootKey("HKEY_CURRENT_USER", 0x80000001);

    /**
     * Registry entries subordinate to this key define the physical state of the computer, including data about the bus
     * type, system memory, and installed hardware and software. It contains subkeys that hold current configuration
     * data, including Plug and Play information (the Enum branch, which includes a complete list of all hardware that
     * has ever been on the system), network logon preferences, network security information, software-related
     * information (such as server names and the location of the server), and other system information.
     */
    public static final RootKey HKEY_LOCAL_MACHINE = new RootKey("HKEY_LOCAL_MACHINE", 0x80000002);

    /**
     * Registry entries subordinate to this key define the default user configuration for new users on the local
     * computer and the user configuration for the current user.
     */
    public static final RootKey HKEY_USERS = new RootKey("HKEY_USERS", 0x80000003);

    /**
     * Registry entries subordinate to this key contain information about the current hardware profile of the local
     * computer system. The information under <code>HKEY_CURRENT_CONFIG</code> describes only the differences between
     * the current hardware configuration and the standard configuration.
     */
    public static final RootKey HKEY_CURRENT_CONFIG = new RootKey("HKEY_CURRENT_CONFIG", 0x80000005);

    /**
     * Registry entries subordinate to this key allow you to access performance data. The data is not actually stored in
     * the registry; the registry functions cause the system to collect the data from its source.
     *
     * <p>
     * NOTE: HKEY_PERFORMANCE_DATA is defined only for Windows NT 4.0, Windows 2000, and Windows XP operating systems.
     */
    public static final RootKey HKEY_PERFORMANCE_DATA = new RootKey("HKEY_PERFORMANCE_DATA", 0x80000004);

    /**
     * Registry entries subordinate to this key allow you to collect performance data.
     *
     * <p>
     * NOTE: HKEY_DYN_DATA is defined only for Windows 95, Windows 98, and Windows ME operating systems.
     */
    public static final RootKey HKEY_DYN_DATA = new RootKey("HKEY_DYN_DATA", 0x80000006);
    // RootKey
}
