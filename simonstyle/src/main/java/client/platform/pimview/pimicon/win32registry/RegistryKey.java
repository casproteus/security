/* RegistryKey.java
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

import java.util.Iterator;

import org.cas.client.platform.casutil.CASUtility;

/**
 * A representation of system registry keys, <code>RegistryKey</code> is the principle class of the
 * <code>ca.beq.util.win32.registry</code> package.
 *
 * <p>
 * A <code>RegistryKey</code> is composed of two components (specified at creation):
 *
 * <ol>
 * <li>A <i>root key</i>, and
 * <li>A <i>path</i>
 * </ol>
 *
 * <p>
 * The root key component is defined to be any valid <code>RootKey</code> object. The path component is similar to a
 * file-system pathname; it is a series of zero or more string <i>names</i> separated by backslahes ("\\"). Unlike
 * file-system pathnames there is no "optional prefix", and each name defines a registry key. For example,
 * <i>Software\\BEQ Technologies\\Test</i> is a valid registry key, whereas <i>\\Software\\BEQ Technologies\\Test</i> is
 * not.
 *
 * <p>
 * The following code snippet demonstrates creating, reading, and deleting values in the registry using
 * <code>RegistryKey</code>:
 *
 * <pre>
 * // create a new key, &quot;Test&quot;, under HKLM
 * RegistryKey r = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE, &quot;Test&quot;);
 * if (!r.exists()) {
 *     r.create();
 * } // if
 * 
 * // create value entries
 * RegistryValue v = new RegistryValue(&quot;aString&quot;, ValueType.REG_SZ, &quot;test&quot;);
 * r.setValue(v);
 * 
 * v.setName(&quot;aDword&quot;);
 * v.setType(ValueType.REG_DWORD);
 * v.setData(new Integer(0x1001001));
 * r.setValue(v);
 * 
 * // read value entries
 * Iterator i = r.values();
 * while (i.hasNext()) {
 *     v = (RegistryValue) i.next();
 * } // while
 * 
 * // delete registry key
 * r.delete();
 * </pre>
 */

public class RegistryKey {
    // loads the jRegistryKey(.dll) library (must be in the system PATH)
    static {
        System.loadLibrary("jRegistryKey");
    } // static

    private RootKey root = RootKey.HKEY_CURRENT_USER;
    private String path = CASUtility.EMPTYSTR;

    /**
     * Constructs a new <code>RegistryKey</code> referencing the root path of the <code>RootKey.HKEY_CURRENT_USER</code>
     * root key.
     */
    public RegistryKey() {
    } // RegistryKey()

    /**
     * Constructs a new <code>RegistryKey</code> referencing the root path of the specified <code>RootKey</code>.
     *
     * @param root
     *            the root key of this <code>RegistryKey</code>
     */
    public RegistryKey(RootKey root) {
        this.root = root;
    } // RegistryKey()

    /**
     * Constructs a new <code>RegistryKey</code> referencing the specified path of the
     * <code>RootKey.HKEY_CURRENT_USER</code> root key.
     *
     * @param path
     *            the path of this <code>RegistryKey</code>
     */
    public RegistryKey(String path) {
        this.path = path;
    } // RegistryKey()

    /**
     * Constructs a new <code>RegistryKey</code> referencing the specified path of the specified <code>RootKey</code>.
     *
     * @param root
     *            the root key of this <code>RegistryKey</code>
     * @param path
     *            the path of this <code>RegistryKey</code>
     */
    public RegistryKey(RootKey root, String path) {
        this.root = root;
        this.path = path;
    } // RegistryKey

    /**
     * Returns this <code>RegistryKey</code>'s root key.
     *
     * @return the root key
     */
    public RootKey getRootKey() {
        return root;
    } // getRootKey()

    /**
     * Returns this <code>RegistryKey</code>'s <code>path</code>.
     *
     * @return the <code>path</code>
     */
    public String getPath() {
        return path;
    } // getPath()

    /**
     * Returns this <code>RegistryKey</code>'s <code>name</code>. The name is last portion of a <code>path</code>. For
     * example, for the <code>path</code> <i>Software\\BEQ Technologies\\TestKey</i>, the <code>name</code> would be
     * <i>TestKey</i>.
     *
     * @return the <code>name</code>
     */
    public String getName() {
        return path.substring(1 + path.lastIndexOf("\\"));
    } // getName()

    /**
     * Tests if this registry key, as defined by the <code>RootKey</code> and <code>path</code>, currently exists in the
     * system registry.
     *
     * @return <code>true</code> if this key exists in the registry
     */
    public native boolean exists();

    /**
     * Creates this registry key in the system registry.
     *
     * @throws RegistryException
     *             if this registry key already exists in the registry
     */
    public native void create();

    /**
     * Creates the specified subkey.
     *
     * @param name
     *            name the subkey to be created
     *
     * @return the created subkey
     *
     * @throws RegistryException
     *             if this registry key does not exist in the registry, or if the specified subkey name already exists.
     */
    public RegistryKey createSubkey(
            String name) {
        RegistryKey r = new RegistryKey(this.root, this.path + "\\" + name);
        r.create();
        return r;
    } // createSubkey()

    /**
     * Deletes this registry key and any subkeys or values from the system registry.
     *
     * <p>
     * <b>WARNING: This method can potentially cause catastrophic damage to the system registry. USE WITH EXTREME
     * CARE!</b>
     *
     * @throws RegistryException
     *             if this registry key does not already exist in the registry
     */
    public native void delete();

    /**
     * Tests if this registry key possesses subkeys. Use <code>subkeys</code> to retrieve an iterator for available
     * subkeys.
     *
     * @return <code>true</code> if this registry key possesses subkeys
     *
     * @throws RegistryException
     *             if this registry key does not already exist in the registry
     */
    public native boolean hasSubkeys();

    /**
     * Tests if this registry key possesses the specified subkey.
     *
     * @param name
     *            the subkey to test for
     *
     * @return <code>true</code> if this registry key possesses subkeys
     *
     * @throws RegistryException
     *             if this registry key does not already exist in the registry
     */
    public boolean hasSubkey(
            String name) {
        RegistryKey r = new RegistryKey(this.root, this.path + "\\" + name);
        return r.exists();
    } // hasSubkey()

    /**
     * Returns an iterator for available subkeys.
     *
     * @return an iterator of <code>RegistryKey</code>'s
     *
     * @throws RegistryException
     *             if this registry key does not already exist in the registry
     */
    public Iterator subkeys() {
        return new KeyIterator(this);
    } // subkeys()

    /**
     * Returns an iterator for available values.
     *
     * @return an iterator of <code>RegistryValue</code>'s
     *
     * @throws RegistryException
     *             if this registry key does not already exist in the registry
     */
    public Iterator values() {
        return new ValueIterator(this);
    } // values()

    /**
     * Tests if this registry key possess the specified value.
     *
     * @param name
     *            the name of the value to be tested
     *
     * @return <code>true</code> if this registry keys possess the specified value
     *
     * @throws RegistryException
     *             if this registry key does not already exist in the registry
     */
    public native boolean hasValue(
            String name);

    /**
     * Tests if this registry key possess any values.
     *
     * @return <code>true</code> if this registry keys possess any values
     *
     * @throws RegistryException
     *             if this registry key does not already exist in the registry
     */
    public native boolean hasValues();

    /**
     * Returns a <code>RegistryValue</code> representing the specified value.
     *
     * @param name
     *            the name of the value to be retreived
     *
     * @return the RegistryValue of the specified value name
     *
     * @throws RegistryException
     *             if this registry key does not already exist in the registry, or if this registry key does not possess
     *             the specified value
     */
    public native RegistryValue getValue(
            String name);

    /**
     * Sets the properties of a registry value according to the properties of the specified <code>RegistryValue</code>.
     * If the specified value exists, it will be modified; if not, it will be created.
     *
     * @param value
     *            the <code>RegistryValue</code>
     *
     * @throws RegistryException
     *             if this registry key does not already exist in the registry
     */
    public native void setValue(
            RegistryValue value);

    /**
     * Deletes the specified value from this registry key.
     *
     * @param name
     *            the name of the value to be deleted
     *
     * @throws RegistryException
     *             if this registry key does not already exist in the registry, or if the specified value is not possess
     *             by this registry key
     */
    public native void deleteValue(
            String name);

    /**
     * Returns a string representation of this <code>RegistryKey</code>.
     *
     * @return a string representation of this <code>RegistryKey</code>
     */
    @Override
    public String toString() {
        return (root.toString() + "\\" + this.path);
    } // toString()
      // RegistryKey
}
