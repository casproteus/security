package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.cas.client.platform.cascontrol.frame.CASMainFrame;

public class OnlineRegister extends SAction {
    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        JOptionPane.showMessageDialog(CASMainFrame.mainFrame, "感谢您通过注册本软件支持正德海神软件工作室(CAS)的运作。\n"
                // .concat("请确保机器与Internet正确连接，以便系统自动将您的用户名及序列号信息上传到CAS的VIP数据库。\n")
                // .concat("(用户名和序列号信息作为注册用户的身份标识，可以随时从帮助菜单下的“关于”项中找到)\n")
                .concat("通过注册，您将会及时获得最新版本的升级服务。同时，作为CAS的VIP用户，\n").concat("您对CAS关于软件方面的任何咨询以及功能定制或扩展要求都将是受欢迎的。\n")
                .concat("再次感谢您的支持！剩余的步骤请登录CAS的主页，并参照完成。\n" + "http://hi.baidu.com/cashelper。"));
    }
}
