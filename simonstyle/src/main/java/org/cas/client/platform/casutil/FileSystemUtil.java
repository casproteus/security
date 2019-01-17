package org.cas.client.platform.casutil;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileView;

import org.cas.client.platform.cascustomize.CustOpts;

import sun.awt.shell.ShellFolder;

public abstract class FileSystemUtil {
    static FileSystemUtil windowsFileSystemView;
    static FileSystemUtil unixFileSystemView;
    static FileSystemUtil genericFileSystemView;

    /**
     * 得到文件系统
     * 
     * @return 文件系统
     */
    public static FileSystemUtil getFileSystemView() {
        if (File.separatorChar == '\\') {
            if (windowsFileSystemView == null) {
                windowsFileSystemView = new WindowsFileSystemView();
            }
            return windowsFileSystemView;
        }

        if (File.separatorChar == CustOpts.BIAS) {
            if (unixFileSystemView == null) {
                unixFileSystemView = new UnixFileSystemView();
            }
            return unixFileSystemView;
        }

        // if(File.separatorChar == ':') {
        // if(macFileSystemView == null) {
        // macFileSystemView = new MacFileSystemView();
        // }
        // return macFileSystemView;
        // }

        if (genericFileSystemView == null) {
            genericFileSystemView = new GenericFileSystemView();
        }
        return genericFileSystemView;
    }

    /**
     * Determines if the given file is a root in the navigatable tree(s). Examples: Windows 98 has one root, the Desktop
     * folder. DOS has one root per drive letter, <code>C:\</code>, <code>D:\</code>, etc. Unix has one root, the
     * <code>"/"</code> directory.
     *
     * The default implementation gets information from the <code>ShellFolder</code> class.
     *
     * @param f
     *            a <code>File</code> object representing a directory
     * @return <code>true</code> if <code>f</code> is a root in the navigatable tree.
     * @see #isFileSystemRoot
     */
    public boolean isRoot(
            File f) {
        if (f == null || !f.isAbsolute()) {
            return false;
        }

        File[] roots = getRoots();
        for (int i = 0; i < roots.length; i++) {
            if (roots[i].equals(f)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the file (directory) can be visited. Returns false if the directory cannot be traversed.
     *
     * @param f
     *            the <code>File</code>
     * @return <code>true</code> if the file/directory can be traversed, otherwise <code>false</code>
     * @see JFileChooser#isTraversable
     * @see FileView#isTraversable
     */
    public Boolean isTraversable(
            File f) {
        return Boolean.valueOf(f.isDirectory());
    }

    /**
     * Name of a file, directory, or folder as it would be displayed in a system file browser. Example from Windows: the
     * "M:\" directory displays as "CD-ROM (M:)"
     *
     * The default implementation gets information from the ShellFolder class.
     *
     * @param f
     *            a <code>File</code> object
     * @return the file name as it would be displayed by a native file chooser
     * @see JFileChooser#getName
     */
    public String getSystemDisplayName(
            File f) {
        String name = null;
        if (f != null) {
            name = f.getName();
            if (!name.equals("..") && f.exists()) {
                name = getShellFolder(f).getDisplayName();
                if (name == null || name.length() == 0) {
                    name = f.getPath(); // e.g. "/"
                }
            }
        }
        return name;
    }

    /**
     * Type description for a file, directory, or folder as it would be displayed in a system file browser. Example from
     * Windows: the "Desktop" folder is desribed as "Desktop".
     *
     * Override for platforms with native ShellFolder implementations.
     *
     * @param f
     *            a <code>File</code> object
     * @return the file type description as it would be displayed by a native file chooser or null if no native
     *         information is available.
     * @see JFileChooser#getTypeDescription
     */
    public String getSystemTypeDescription(
            File f) {
        return null;
    }

    /**
     * Icon for a file, directory, or folder as it would be displayed in a system file browser. Example from Windows:
     * the "M:\" directory displays a CD-ROM icon.
     *
     * The default implementation gets information from the ShellFolder class.
     *
     * @return an icon as it would be displayed by a native file chooser
     * @see JFileChooser#getIcon
     * @param iconType
     *            图标类型
     * @param f
     *            a <code>File</code> object
     */
    public Icon getSystemIcon(
            File f,
            boolean iconType) {
        if (f != null) {
            ShellFolder sf = getShellFolder(f);
            if (sf == null) {
                return null;
                // TODO:writeErrorLog()
            }
            Image img = sf.getIcon(iconType);
            if (img != null) {
                return new ImageIcon(img, sf.getFolderType());
            } else {
                return UIManager.getIcon(f.isDirectory() ? "FileView.directoryIcon" : "FileView.fileIcon");
            }
        } else {
            return null;
        }
    }

    /**
     * On Windows, a file can appear in multiple folders, other than its parent directory in the filesystem. Folder
     * could for example be the "Desktop" folder which is not the same as file.getParentFile().
     *
     * @param folder
     *            a <code>File</code> object repesenting a directory or special folder
     * @param file
     *            a <code>File</code> object
     * @return <code>true</code> if <code>folder</code> is a directory or special folder and contains <code>file</code>.
     */
    public boolean isParent(
            File folder,
            File file) {
        if (folder == null || file == null) {
            return false;
        } else if (folder instanceof ShellFolder) {
            File[] children = getFiles(folder, false);
            for (int i = 0; i < children.length; i++) {
                if (file.equals(children[i])) {
                    return true;
                }
            }
            return false;
        } else {
            return folder.equals(file.getParentFile());
        }
    }

    /**
     * 得到子文件
     * 
     * @param parent
     *            a <code>File</code> object repesenting a directory or special folder
     * @param fileName
     *            a name of a file or folder which exists in <code>parent</code>
     * @return a File object. This is normally constructed with <code>new
     * File(parent, fileName)</code> except when parent and child are both special folders, in which case the
     *         <code>File</code> is a wrapper containing a <code>ShellFolder</code> object.
     */
    public File getChild(
            File parent,
            String fileName) {
        if (parent instanceof ShellFolder) {
            File[] children = getFiles(parent, false);
            for (int i = 0; i < children.length; i++) {
                if (children[i].getName().equals(fileName)) {
                    return children[i];
                }
            }
        }
        return createFileObject(parent, fileName);
    }

    /*
     * Checks if <code>f</code> represents a real directory or file as opposed to a special folder such as
     * <code>"Desktop"</code>. Used by UI classes to decide if a folder is selectable when doing directory choosing.
     * @param f a <code>File</code> object
     * @return <code>true</code> if <code>f</code> is a real file or directory.
     */
    /**
     * 是不是一个文件系统
     * 
     * @return 是否为文件系统
     * @param f
     *            一个文件
     */
    public boolean isFileSystem(
            File f) {
        if (f instanceof ShellFolder) {
            return ((ShellFolder) f).isFileSystem();
        } else {
            return true;
        }
    }

    /**
     * Creates a new folder with a default folder name.
     * 
     * @return 新文件夹
     * @param containingDir
     *            一个文件
     * @throws IOException
     *             文件异常
     */
    public abstract File createNewFolder(
            File containingDir) throws IOException;

    /**
     * Returns whether a file is hidden or not.
     * 
     * @return 是否为隐藏文件
     * @param f
     *            一个文件
     */
    public boolean isHiddenFile(
            File f) {
        return f.isHidden();
    }

    /*
     * Is dir the root of a tree in the file system, such as a drive or partition. Example: Returns true for "C:\" on
     * Windows 98.
     * @See also isRoot
     */
    /**
     * 判断一个文件是否为根目录
     * 
     * @return 是否为根目录
     * @param dir
     *            一个文件
     */
    public boolean isFileSystemRoot(
            File dir) {
        return ShellFolder.isFileSystemRoot(dir);
    }

    /*
     * Used by UI classes to decide whether to display a special icon for drives or partitions, e.g. a "hard disk" icon.
     * The default implementation has no way of knowing, so always returns false.
     * @param dir a directory
     * @return <code>false</code> always
     */
    /**
     * 是否为一个驱动器
     * 
     * @return 判断结果
     * @param dir
     *            一个文件
     */
    public boolean isDrive(
            File dir) {
        return false;
    }

    /*
     * Used by UI classes to decide whether to display a special icon for a floppy disk. Implies isDrive(dir). The
     * default implementation has no way of knowing, so always returns false.
     * @param dir a directory
     * @return <code>false</code> always
     */
    /**
     * 是否为一个软驱
     * 
     * @return 判断结果
     * @param dir
     *            一个文件
     */
    public boolean isFloppyDrive(
            File dir) {
        return false;
    }

    /*
     * Used by UI classes to decide whether to display a special icon for a computer node, e.g. "My Computer" or a
     * network server. The default implementation has no way of knowing, so always returns false.
     * @param dir a directory
     * @return <code>false</code> always
     */
    /**
     * 是否为一个计算机节点
     * 
     * @param dir
     *            一个文件
     * @return 判断结果
     */
    public boolean isComputerNode(
            File dir) {
        return ShellFolder.isComputerNode(dir);
    }

    /**
     * Returns all root partitions on this system. For example, on Windows, this would be the "Desktop" folder, while on
     * DOS this would be the A: through Z: drives.
     * 
     * @return 所有根目录
     */
    public File[] getRoots() {
        // Don't cache this array, because filesystem might change
        File[] roots = (File[]) ShellFolder.get("roots");

        for (int i = 0; i < roots.length; i++) {
            if (isFileSystemRoot(roots[i])) {
                roots[i] = createFileSystemRoot(roots[i]);
            }
        }
        return roots;
    }

    // Providing default implementations for the remaining methods
    // because most OS file systems will likely be able to use this
    // code. If a given OS can't, override these methods in its
    // implementation.

    /**
     * 返回主目录
     * 
     * @return 主目录
     */
    public File getHomeDirectory() {
        return createFileObject(System.getProperty("user.home"));
    }

    /**
     * Return the user's default starting directory for the file chooser.
     *
     * @return a <code>File</code> object representing the default starting folder
     */
    public File getDefaultDirectory() {
        File f = (File) ShellFolder.get("fileChooserDefaultFolder");
        if (isFileSystemRoot(f)) {
            f = createFileSystemRoot(f);
        }
        return f;
    }

    /**
     * Returns a File object constructed in dir from the given filename.
     * 
     * @return 文件对象
     * @param dir
     *            一个文件
     * @param filename
     *            文件名
     */
    public File createFileObject(
            File dir,
            String filename) {
        if (dir == null) {
            return new File(filename);
        } else {
            return new File(dir, filename);
        }
    }

    /**
     * Returns a File object constructed from the given path string.
     * 
     * @return 文件对象
     * @param path
     *            一个文件名
     */
    public File createFileObject(
            String path) {
        File f = new File(path);
        if (isFileSystemRoot(f)) {
            f = createFileSystemRoot(f);
        }
        return f;
    }
    
    /**
     * 保存文件路径
     * 
     * @param files
     *            :文件表
     * @param v
     *            ,保存相对路径
     * @param length
     *            :初始路径长度
     */
    public static void getAllFilesIntoArray(
            File[] files,
            ArrayList<String> v,
            int length) {
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory()) {
                String path = files[i].getPath();
                String spath = path.substring(length);
                v.add(spath);
            } else {
                getAllFilesIntoArray(files[i].listFiles(), v, length);
            }
        }
    }
    
    /**
     * Gets the list of shown (i.e. not hidden) files.
     * 
     * @return 所有文件
     * @param dir
     *            一个文件
     * @param useFileHiding
     *            是否包括隐藏
     */
    public File[] getFiles(
            File dir,
            boolean useFileHiding) {
        Vector files = new Vector();

        // add all files in dir
        File[] names = dir.listFiles();

        File f;

        int nameCount = (names == null) ? 0 : names.length;
        for (int i = 0; i < nameCount; i++) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            f = names[i];
            if (!(f instanceof ShellFolder)) {
                if (isFileSystemRoot(f)) {
                    f = createFileSystemRoot(f);
                }
                try {
                    f = ShellFolder.getShellFolder(f);
                } catch (FileNotFoundException e) {
                    // Not a valid file (wouldn't show in native file chooser)
                    // Example: C:\pagefile.sys
                    continue;
                } catch (InternalError e) {
                    // Not a valid file (wouldn't show in native file chooser)
                    // Example C:\Winnt\Profiles\joe\history\History.IE5
                    continue;
                }
            }
            if (!useFileHiding || !isHiddenFile(f)) {
                files.addElement(f);
            }
        }

        return (File[]) files.toArray(new File[files.size()]);
    }

    /**
     * Returns the parent directory of <code>dir</code>.
     * 
     * @param dir
     *            the <code>File</code> being queried
     * @return the parent directory of <code>dir</code>, or <code>null</code> if <code>dir</code> is <code>null</code>
     */
    public File getParentDirectory(
            File dir) {
        if (dir != null && dir.exists()) {
            ShellFolder sf = getShellFolder(dir);
            File psf = sf.getParentFile();
            if (psf != null) {
                if (isFileSystem(psf)) {
                    File f = psf;
                    if (f != null && !f.exists()) {
                        // This could be a node under "Network Neighborhood".
                        File ppsf = psf.getParentFile();
                        if (ppsf == null || !isFileSystem(ppsf)) {
                            // We're mostly after the exists() override for windows below.
                            f = createFileSystemRoot(f);
                        }
                    }
                    return f;
                } else {
                    return psf;
                }
            }
        }
        return null;
    }

    ShellFolder getShellFolder(
            File f) {
        if (!(f instanceof ShellFolder) && !(f instanceof FileSystemRoot) && isFileSystemRoot(f)) {

            f = createFileSystemRoot(f);
        }
        try {
            return ShellFolder.getShellFolder(f);
        } catch (FileNotFoundException e) {
            // @TODO:改为写道输出问件.
            // System.err.println("FileSystem.getShellFolder: f="+f);
            // e.printStackTrace();ErrorUtil.writeErrorLog(
            return null;
        } catch (InternalError e) {
            // @TODO:改为写道输出问件.
            // System.err.println("FileSystem.getShellFolder: f="+f);
            // e.printStackTrace();ErrorUtil.writeErrorLog(
            return null;
        }
    }

    /**
     * Creates a new <code>File</code> object for <code>f</code> with correct behavior for a file system root directory.
     *
     * @param f
     *            a <code>File</code> object representing a file system root directory, for example "/" on Unix or "C:\"
     *            on Windows.
     * @return a new <code>File</code> object
     */
    protected File createFileSystemRoot(
            File f) {
        return new FileSystemRoot(f);
    }

    /**
     * 好象是根目录的处理
     */
    public static class FileSystemRoot extends File {
        /**
         * 构建器
         * 
         * @param f
         *            一个文件
         */
        public FileSystemRoot(File f) {
            super(f, CASUtility.EMPTYSTR);
        }

        /**
         * 构建器
         * 
         * @param s
         *            一个代表文件的字符串
         */
        public FileSystemRoot(String s) {
            super(s);
        }

        /**
         * 是否为一个目录
         * 
         * @return 判断结果
         */
        @Override
		public boolean isDirectory() {
            return true;
        }

        /**
         * 得到名字
         * 
         * @return 名字
         */
        @Override
		public String getName() {
            return getPath();
        }
    }
}

/**
 * FileSystem that handles some specific unix-isms.
 */
class UnixFileSystemView extends FileSystemUtil {

    private static final String newFolderString = UIManager.getString("FileChooser.other.newFolder");
    private static final String newFolderNextString = UIManager.getString("FileChooser.other.newFolder.subsequent");

    /**
     * Creates a new folder with a default folder name.
     * 
     * @param containingDir
     *            一个文件
     * @throws IOException
     *             IO异常
     * @return 新文件夹
     */
    @Override
	public File createNewFolder(
            File containingDir) throws IOException {
        if (containingDir == null) {
            throw new IOException("Containing directory is null:");
        }
        File newFolder = null;
        // Unix - using OpenWindows' default folder name. Can't find one for Motif/CDE.
        newFolder = createFileObject(containingDir, newFolderString);
        int i = 1;
        while (newFolder.exists() && (i < 100)) {
            newFolder =
                    createFileObject(containingDir,
                            MessageFormat.format(newFolderNextString, new Object[] { PIMPool.pool.getKey(i) }));
            i++;
        }

        if (newFolder.exists()) {
            throw new IOException("Directory already exists:" + newFolder.getAbsolutePath());
        } else {
            newFolder.mkdirs();
        }

        return newFolder;
    }

    /**
     * 是否为系统根目录
     * 
     * @return 判断结果
     * @param dir
     *            一个文件
     */
    @Override
	public boolean isFileSystemRoot(
            File dir) {
        return (dir != null && dir.getAbsolutePath().equals("/"));
    }

    /**
     * 是否为一个驱动器
     * 
     * @return 判断结果
     * @param dir
     *            一个文件
     */
    @Override
	public boolean isDrive(
            File dir) {
        if (isFloppyDrive(dir)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否为一个软驱
     * 
     * @return 判断结果
     * @param dir
     *            一个文件
     */
    @Override
	public boolean isFloppyDrive(
            File dir) {
        // Could be looking at the path for Solaris, but wouldn't be reliable.
        // For example:
        // return (dir != null && dir.getAbsolutePath().toLowerCase().startsWith("/floppy"));
        return false;
    }

    /**
     * 是否为一个计算机节点
     * 
     * @param dir
     *            一个文件
     * @return 判断结果
     */
    @Override
	public boolean isComputerNode(
            File dir) {
        if (dir != null) {
            String parent = dir.getParent();
            if (parent != null && parent.equals("/net")) {
                return true;
            }
        }
        return false;
    }
}

/**
 * FileSystem that handles some specific windows concepts.
 */
class WindowsFileSystemView extends FileSystemUtil {

    private static final String newFolderString = UIManager.getString("FileChooser.win32.newFolder");
    private static final String newFolderNextString = UIManager.getString("FileChooser.win32.newFolder.subsequent");

    /**
     * 该文件是否可移动
     * 
     * @return 判断结果
     * @param f
     *            一个文件
     */
    @Override
	public Boolean isTraversable(
            File f) {
        return Boolean.valueOf(isFileSystemRoot(f) || isComputerNode(f) || f.isDirectory());
    }

    /**
     * 得到目录下文件
     * 
     * @param parent
     *            父文件夹
     * @param fileName
     *            文件名
     * @return 目录下文件
     */
    @Override
	public File getChild(
            File parent,
            String fileName) {
        if (fileName.startsWith("\\") && !(fileName.startsWith("\\\\")) && isFileSystem(parent)) {

            // Path is relative to the root of parent's drive
            String path = parent.getAbsolutePath();
            if (path.length() >= 2 && path.charAt(1) == ':' && Character.isLetter(path.charAt(0))) {

                return createFileObject(path.substring(0, 2) + fileName);
            }
        }
        return super.getChild(parent, fileName);
    }

    /**
     * Type description for a file, directory, or folder as it would be displayed in a system file browser. Example from
     * Windows: the "Desktop" folder is desribed as "Desktop".
     *
     * The Windows implementation gets information from the ShellFolder class.
     * 
     * @param f
     *            一个文件
     * @return 系统类型描述
     */
    @Override
	public String getSystemTypeDescription(
            File f) {
        if (f != null) {
            return getShellFolder(f).getFolderType();
        } else {
            return null;
        }
    }

    /**
     * 得到用户主目录
     * 
     * @return the Desktop folder.
     */
    @Override
	public File getHomeDirectory() {
        return getRoots()[0];
    }

    /**
     * Creates a new folder with a default folder name.
     * 
     * @return 新文件夹
     * @param containingDir
     *            上级文件夹
     * @throws IOException
     *             文件异常
     */
    @Override
	public File createNewFolder(
            File containingDir) throws IOException {
        if (containingDir == null) {
            throw new IOException("Containing directory is null:");
        }
        File newFolder = null;
        // Using NT's default folder name
        newFolder = createFileObject(containingDir, newFolderString);
        int i = 2;
        while (newFolder.exists() && (i < 100)) {
            newFolder =
                    createFileObject(containingDir,
                            MessageFormat.format(newFolderNextString, new Object[] { PIMPool.pool.getKey(i) }));
            i++;
        }

        if (newFolder.exists()) {
            throw new IOException("Directory already exists:" + newFolder.getAbsolutePath());
        } else {
            newFolder.mkdirs();
        }

        return newFolder;
    }

    /**
     * 是否为一个驱动器
     * 
     * @return 判断结果
     * @param dir
     *            一个文件
     */
    @Override
	public boolean isDrive(
            File dir) {
        return isFileSystemRoot(dir);
    }

    /**
     * 是否为一个软驱
     * 
     * @return 判断结果
     * @param dir
     *            一个文件
     */
    @Override
	public boolean isFloppyDrive(
            File dir) {
        String path = dir.getAbsolutePath();
        return (path != null && (path.equals("A:\\") || path.equals("B:\\")));
    }

    /**
     * Returns a File object constructed from the given path string.
     * 
     * @param path
     *            文件名
     * @return 文件对象
     */
    @Override
	public File createFileObject(
            String path) {
        // Check for missing backslash after drive letter such as "C:" or "C:filename"
        if (path.length() >= 2 && path.charAt(1) == ':' && Character.isLetter(path.charAt(0))) {
            if (path.length() == 2) {
                path += "\\";
            } else if (path.charAt(2) != '\\') {
                path = path.substring(0, 2) + "\\" + path.substring(2);
            }
        }
        return super.createFileObject(path);
    }

    @Override
	protected File createFileSystemRoot(
            File f) {
        // Problem: Removable drives on Windows return false on f.exists()
        // Workaround: Override exists() to always return true.
        return new ExistFileSystem(f);
    }

    /**
     * 这是一定存在的文件系统
     */
    public static class ExistFileSystem extends FileSystemRoot {
        /**
         * constructor
         * 
         * @param prmFile
         *            本类引用
         */
        public ExistFileSystem(File prmFile) {
            super(prmFile);
        }

        /**
         * 重载
         * 
         * @return 存在
         */
        @Override
		public boolean exists() {
            return true;
        }
    }
}

/**
 * Fallthrough FileSystem in case we can't determine the OS.
 */
class GenericFileSystemView extends FileSystemUtil {

    private static final String newFolderString = UIManager.getString("FileChooser.other.newFolder");

    /**
     * Creates a new folder with a default folder name.
     * 
     * @return 新文件对象
     * @param containingDir
     *            一个文件
     * @throws IOException
     *             文件异常
     */
    @Override
	public File createNewFolder(
            File containingDir) throws IOException {
        if (containingDir == null) {
            throw new IOException("Containing directory is null:");
        }
        File newFolder = null;
        // Using NT's default folder name
        newFolder = createFileObject(containingDir, newFolderString);

        if (newFolder.exists()) {
            throw new IOException("Directory already exists:" + newFolder.getAbsolutePath());
        } else {
            newFolder.mkdirs();
        }

        return newFolder;
    }
}
