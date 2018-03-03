package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

public class OpenWithOutlook extends SAction {
    // public OpenWithOutlook()
    // {
    // }
    //
    public void actionPerformed(
            ActionEvent e) {
        // try
        // {
        // String startValue = getStartValue();
        // Runtime.getRuntime().exec(startValue);
        // }
        // catch(Exception ex)
        // {
        // ex.printStackTrace();
        // }
    }
    //
    // /**
    // * 搜索注册表信息，取出默认邮件客户端的启动信息
    // * @return
    // */
    // private String getStartValue() throws Exception
    // {
    // String startValue = PIMUtility.EMPTYSTR;
    // /**
    // * ================================
    // * 系统固有目录，不用判断
    // * ================================
    // */
    // String keyName = "SOFTWARE\\CLIENTS\\MAIL";
    // RegistryKey key = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE, keyName);
    //
    // if (key.exists())
    // {
    // String primaryKeyName = PIMUtility.EMPTYSTR;
    // if (key.hasValue(null))
    // {
    // RegistryValue value = key.getValue(null); //默认客户端信息
    // primaryKeyName = value.getData().toString();
    // }
    //
    // if (key.hasSubkey(primaryKeyName)) //存在默认客户端注册信息
    // {
    // keyName = keyName + "\\" + primaryKeyName;
    // key = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE, keyName);
    // if (key.hasSubkey("shell\\open\\command")) //存在打开客户端的信息
    // {
    // keyName = keyName.concat("\\shell\\open\\command");
    // key = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE, keyName);
    // if (key.hasValue(null))
    // {
    // RegistryValue openValue = key.getValue(null); //默认邮件客户端启动参数
    // startValue = openValue.getData().toString();
    // }
    // }
    // else if (key.hasSubkey("Protocols\\mailTo\\shell\\open\\command")) //第一步容错处理（从发送菜单信息中找）
    // {
    // keyName = keyName.concat("\\Protocols\\mailTo\\shell\\open\\command");
    // key = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE, keyName);
    // if (key.hasValue(null))
    // {
    // RegistryValue openValue = key.getValue(null); //默认邮件客户端启动参数
    // startValue = openValue.getData().toString();
    // startValue = startValue.indexOf("\"") == 1 ? startValue.substring(1) : startValue;
    // startValue = startValue.indexOf("\"") != -1 ? startValue.substring(0, startValue.indexOf("\"")) : startValue;
    // }
    // }
    // }
    // /**
    // * =============================================================================
    // * 第二步容错（如找不到默认客户端的启动信息，则启动系统自带的邮件客户端的系统Outlook Express）
    // * =============================================================================
    // */
    // else if (key.hasSubkey("Outlook Excepress"))
    // {
    // keyName = keyName.concat("\\Outlook Excepress");
    // key = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE, keyName);
    // if (key.hasSubkey("shell\\open\\command")) //存在打开客户端的信息
    // {
    // keyName = keyName.concat("\\shell\\open\\command");
    // key = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE, keyName);
    // if (key.hasValue(null))
    // {
    // RegistryValue openValue = key.getValue(null); //Outlook Express启动参数
    // startValue = openValue.getData().toString();
    // }
    // }
    // else if (key.hasSubkey("Protocols\\mailTo\\shell\\open\\command")) //第三步容错处理（从发送菜单信息中找）
    // {
    // keyName = keyName.concat("\\Protocols\\mailTo\\shell\\open\\command");
    // key = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE, keyName);
    // if (key.hasValue(null))
    // {
    // RegistryValue openValue = key.getValue(null); //Outlook Excepress启动参数
    // startValue = openValue.getData().toString();
    // startValue = startValue.indexOf("\"") == 1 ? startValue.substring(1) : startValue;
    // startValue = startValue.indexOf("\"") != -1 ? startValue.substring(0, startValue.indexOf("\"")) : startValue;
    // }
    // }
    // }
    //
    // /*if (key.hasSubkey(primaryKeyName)) //存在默认的邮件客户端注册信息
    // {
    // Iterator iterator = key.subkeys();
    // while(iterator.hasNext())
    // {
    // RegistryKey outLookKey = (RegistryKey)iterator.next();
    // if (outLookKey != null && outLookKey.getName().equalsIgnoreCase(primaryKeyName)) //搜索到默认客户端的子键
    // {
    // if (outLookKey.hasSubkey("shell")) //这个子键中存有打开客户端的参数
    // {
    // Iterator subOne = outLookKey.subkeys();
    // while(subOne.hasNext())
    // {
    // RegistryKey shellKey = (RegistryKey)subOne.next();
    // if (shellKey.getName().equalsIgnoreCase("shell")) //取得shell子键
    // {
    // if (shellKey.hasSubkey("open"))
    // {
    // Iterator subTwo = shellKey.subkeys();
    // while(subTwo.hasNext())
    // {
    // RegistryKey openKey = (RegistryKey)subTwo.next();
    // if (openKey.getName().equalsIgnoreCase("open")) //取得open子键
    // {
    // if (openKey.hasSubkey("command"))
    // {
    // Iterator subThree = openKey.subkeys();
    // while(subThree.hasNext())
    // {
    // RegistryKey commandKey = (RegistryKey)subThree.next();
    // if (commandKey.getName().equalsIgnoreCase("command")) //取得command子键
    // {
    // if (commandKey.hasValue(null))
    // {
    // openValue = commandKey.getValue(null);
    // hasValue = true;
    // break;
    // }
    // }
    // }
    // }
    // }
    // }
    // }
    // }
    // }
    // }
    //
    // if (!hasValue) //第一步容错处理
    // {
    // if(outLookKey.hasSubkey("Protocols")) //这个子键中也带有打开客户端的参数，不过得经过处理
    // {
    // Iterator subOne = outLookKey.subkeys();
    // while(subOne.hasNext())
    // {
    // RegistryKey protocolsKey = (RegistryKey)subOne.next();
    // if (protocolsKey.getName().equalsIgnoreCase("Protocols")) //取得Protocols子键
    // {
    //
    // }
    // }
    // }
    // }
    // }
    // }
    // }
    //
    // if (!hasValue) //（第二步容错处理）不存在，此时则取系统自带的OE客户端
    // {
    //
    // }*/
    // }
    // return startValue;
    // }
}
