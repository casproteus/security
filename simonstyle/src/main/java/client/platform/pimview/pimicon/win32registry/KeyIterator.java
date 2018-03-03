/* KeyIterator.java
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
import java.util.NoSuchElementException;

/**
 * <code>KeyIterator</code> provides methods to iterate through the subkeys of a particular registry key.
 *
 * @see ca.beq.util.win32.registry.RegistryKey
 * @see ca.beq.util.win32.registry.RootKey
 * @see java.util.Iterator
 *
 */

class KeyIterator implements Iterator {
    private RegistryKey key;

    // // the following variables are used by the native library
    // private int index = -1;
    // private int hkey;
    // private int maxsize;
    // private int count;

    /**
     * Constructs a new <code>KeyIterator</code> to enumerate subkeys from the specified <code>RegistryKey</code>.
     *
     * @param key
     *            The registry key from which to enumerate subkeys.
     */
    public KeyIterator(RegistryKey key) {
        this.key = key;
    } // KeyIterator()

    /**
     * Returns <code>true</code> if the iteration contains more <code>RegistryKey</code> elements. (In other words,
     * returns <code>true</code> if <code>next</code> would return a <code>RegistryKey</code> element rather than
     * throwing an exception).
     *
     * @return <code>true</code> if the iterator has more <code>RegistryKey</code> elements.
     */
    @Override
    public native boolean hasNext();

    /**
     * Returns the next <code>RegistryKey</code> element in the iteration.
     *
     * @return the next <code>RegistryKey</code> element in the iteration.
     *
     * @throws NoSuchElementException
     *             if the iteration contains no more <code>RegistryKey</code> elements.
     */
    @Override
    public Object next() {
        return new RegistryKey(key.getRootKey(), key.getPath() + "\\" + getNext());
    } // next()

    /**
     * Returns the next <code>RegistryKey</code> element in the iteration.
     *
     * @return the name of the next <code>RegistryKey</code> element in the iteration.
     *
     * @throws NoSuchElementException
     *             if the iteration contains no more <code>RegistryKey</code> elements.
     */
    private native String getNext();

    /**
     * The optional <code>remove</code> operation is not supported by this Iterator.
     *
     * @throws java.lang.UnsupportedOperationException
     *             if the remove operation is not supported by this Iterator.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("The remove operation is not supported by this Iterator");
    } // remove()
} // KeyIterator
