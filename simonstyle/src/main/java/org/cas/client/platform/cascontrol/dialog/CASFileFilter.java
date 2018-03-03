package org.cas.client.platform.cascontrol.dialog;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

/**
 * A convenience implementation of FileFilter that filters out all files except for those type extensions that it knows
 * about.
 * 
 * Extensions are of the type ".foo", which is typically found on Windows and Unix boxes, but not on Macinthosh. Case is
 * ignored.
 * 
 * Example - create a new filter that filerts out all files but gif and jpg image files:
 * 
 * JFileChooser chooser = new JFileChooser(); ExampleFileFilter filter = new ExampleFileFilter( new String{"gif",
 * "jpg"}, "JPEG & GIF Images") chooser.addChoosableFileFilter(filter); chooser.showOpenDialog(this);
 */
public class CASFileFilter extends FileFilter {
    /**
     * Creates a file filter that accepts files with the given extension. Example: new ExampleFileFilter("jpg");
     * 
     * @see #addExtension
     */
    public CASFileFilter(String prmExtension) {
        this(prmExtension, null);
    }

    /**
     * Creates a file filter that accepts the given file type. Example: new ExampleFileFilter("jpg",
     * "JPEG Image Images");
     *
     * Note that the "." before the extension is not needed. If provided, it will be ignored.
     *
     * @see #addExtension
     */
    public CASFileFilter(String prmExtension, String prmDescription) {
        filters = new Hashtable();
        if (prmExtension != null) {
            /*
             * Adds a filetype "dot" extension to filter against. For example: the following code will create a filter
             * that filters out all files except those that end in ".jpg" and ".tif": ExampleFileFilter filter = new
             * ExampleFileFilter(); filter.addExtension("jpg"); filter.addExtension("tif"); Note that the "." before the
             * extension is not needed and will be ignored.
             */
            if (filters == null) {
                filters = new Hashtable(5);
            }
            filters.put(prmExtension.toLowerCase(), this);
        }
        if (prmDescription != null) {
            /*
             * Sets the human readable description of this filter. For example:
             * filter.setDescription("Gif and JPG Images");
             * @see setDescription
             * @see setExtensionListInDescription
             * @see isExtensionListInDescription
             */
            description = prmDescription;
            fullDescription = null;
        }
    }

    /**
     * Return true if this file should be shown in the directory pane, false if it shouldn't.
     * 
     * Files that begin with "." are ignored.
     * 
     * @see #getExtension
     * @see FileFilter#accepts
     */
    @Override
    public boolean accept(
            File prmFile) {
        if (prmFile != null) {
            if (prmFile.isDirectory())
                return true;

            /*
             * Return the extension portion of the file's name .
             * @see #getExtension
             * @see FileFilter#accept
             */
            String tmpFilename = prmFile.getName();
            int i = tmpFilename.lastIndexOf('.');
            if (i > 0 && i < tmpFilename.length() - 1) {
                String extension = tmpFilename.substring(i + 1).toLowerCase();
                if (extension != null && filters.get(extension) != null)
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns the human readable description of this filter. For example: "JPEG and GIF Image Files (*.jpg, *.gif)"
     * 
     * @see setDescription
     * @see setExtensionListInDescription
     * @see FileFilter#getDescription
     */
    @Override
    public String getDescription() {
        if (fullDescription == null) {
            fullDescription = description == null ? "(" : description + " (";
            // build the description from the extension list
            Enumeration tmpExtensions = filters.keys();
            if (tmpExtensions != null) {
                fullDescription += "." + (String) tmpExtensions.nextElement();
                while (tmpExtensions.hasMoreElements()) {
                    fullDescription += ", ." + (String) tmpExtensions.nextElement();
                }
            }
            fullDescription += ")";
        }
        return fullDescription;
    }

    private Hashtable filters = null;
    private String description = null;
    private String fullDescription = null;
}
