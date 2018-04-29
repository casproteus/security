package org.cas.client.platform.cascustomize;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

import org.cas.client.platform.casutil.ErrorUtil;

/**
 * ＠Note：只有Config.ini也即hash中的内容需要保存，其他内容是只读的。
 */

class DefaultConfigInfoWriter {
    /** Creates new DefaultModelWriter */
    DefaultConfigInfoWriter() {
        configFilePath = DefaultConfigInfoLoader.getConfigFilePath();
    }

    /**
     * Save data.
     */
    void saveData(
            Hashtable prmHash) {
        try {
			Files.write(Paths.get(configFilePath), prmHash.toString().getBytes("UTF-8"));
        } catch (IOException e) {
        	ErrorUtil.write(e);
        }
    }

    // variables--------------------------------------------------------------------------------------------------------------
    protected FileOutputStream fileOutput;
    private String configFilePath;
}

// methods------------------------------------------------------
/**
 * Save navigator tree.
 *
 * private void writeNavigateTree() { hash = custOptions.getPropertyTable(FileCustOptions.NAVIGATE_TABLE); }
 *
 * /** Save screen layout information.
 *
 * private void writeLayoutInformation() { hash = custOptions.getPropertyTable(FileCustOptions.LAYOUT_INFO);
 * writeData(LAYOUT_LIST); }
 *
 * /** Save option.
 *
 * private void writeOptionInformation() { hash = custOptions.getPropertyTable(FileCustOptions.OPTION_INFO);
 * writeData(OPTION_LIST); }
 *
 * /** Save day view information.
 *
 * private void writeDayViewInformation() { hash = custOptions.getPropertyTable(FileCustOptions.DAY_VIEW_INFO);
 * writeData(DAY_VIEW_LIST); }
 *
 * /** Save week view information.
 *
 * private void writeWeekViewInformation() { hash = custOptions.getPropertyTable(FileCustOptions.WEEK_VIEW_INFO);
 * writeData(WEEK_VIEW_LIST); }
 *
 * /** Save month view information.
 *
 * void writeMonthViewInformation() { hash = custOptions.getPropertyTable(FileCustOptions.MONTH_VIEW_INFO);
 * writeData(MONTH_VIEW_LIST); }
 */

/*
 * private void writeData(String dataArray[]) { try { String section, key, value, context; int len = dataArray.length;
 * for (int i = 0; i < len; ++i) { if (i == 0) { section = dataArray[0] + ENTER_STRING;
 * fileOutput.write(section.getBytes()); } else { key = dataArray[i]; value = getSaveDataString(key); if (value != null)
 * { context = key + LINK_STRING + value + ENTER_STRING; fileOutput.write(context.getBytes()); } } } int listLen =
 * INFORMATION_LIST.length; if (INFORMATION_LIST[listLen - 1] != dataArray) { fileOutput.write(ENTER_STRING.getBytes());
 * } } catch(IOException e) { } } private String getSaveDataString(String key) { Object obj = hash.get(key); if (obj !=
 * null) { return getObjectToString(obj); } return null; } private String getObjectToString(Object obj) { if (obj
 * instanceof String) { return STRING_TYPE + (String)obj; } else if (obj instanceof Boolean) { if
 * (((Boolean)obj).booleanValue()) { return BOOLEAN_TYPE + BOOLEAN_TRUE; } else { return BOOLEAN_TYPE + BOOLEAN_FALSE; }
 * } else if (obj instanceof Integer) { return INTEGER_TYPE + ((Integer)obj).toString(); } else { return null; } }
 */
