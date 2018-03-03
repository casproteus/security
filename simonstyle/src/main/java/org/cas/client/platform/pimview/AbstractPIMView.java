package org.cas.client.platform.pimview;

import java.awt.Graphics;

import javax.swing.JPanel;

import org.cas.client.platform.cascontrol.IApplication;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMViewInfo;

public abstract class AbstractPIMView extends JPanel implements IView {
    /** Creates a new instance of AbstractPIMView */
    public AbstractPIMView() {
        setLayout(null);
    }

    /**
     * 返回应用,该方法将被取消，View通过Control可以得到Active的App，用于实现弹出菜单等功能，自身不需要再维护。
     * 
     * @return IApplication
     */
    public IApplication getApplication() {
        return application;
    }

    /**
     * 设置应用,如果TableView已经存在的话，理论上只要重新设置应用，和视图规格。 设置视图规格方法中导致视图自动更新所显示的内容。
     * 
     * @param prmApplication
     *            几大应用的接口
     */
    public void setApplication(
            IApplication prmApplication) {
        application = prmApplication;
    }

    /**
     * 更新视图风格即意味着所显示的数据需要更新。 但为了强制加入到PIM系统的所有的视图的风格具有一定的一致性，在他们的超类里统一进行了顶部的Icon和Title设置。
     */
    public void setViewInfo(
            PIMViewInfo prmViewInfo) {
        if (prmViewInfo == null) {
            currentViewInfo = prmViewInfo;
            return;
        } else {
            int tmpAppIndex = prmViewInfo.getAppIndex();
            boolean tmpAppChanged = currentViewInfo == null || currentViewInfo.getAppIndex() != tmpAppIndex;

            if (tmpAppIndex >= 0 && tmpAppIndex < CustOpts.custOps.APPCapsVec.size())
                setIconAndTitle(application.getAppIcon(false), (String) CustOpts.custOps.APPCapsVec.get(tmpAppIndex));
            // 设置标题头的信息设置结束-----------------------------

            currentViewInfo = prmViewInfo;// 所有的view都要维护对当前视图信息的维护。TODO:逐步取消View对ActiveViewinfo的维护，因为App中已经有维护。

            init(tmpAppChanged);// SwingUtilities.invokeLater(this);//new
                                // Thread(this).start();@NOTE：不可以线程中调以前的updatePIMUI（）或后来的init()，因为这样做会经常导致table绘制2～9遍，原因尚不明。
        }
    }

    public void paintComponent(
            Graphics g) {
    }

    /**
     * 该方法被调用时，将根据已经设置好的viewInfo的值，从model中搜索出符合条件的内容，并将内容 更新到View上面的各个组件上。
     */
    public void init(
            boolean prmAppChanged) {
    }

    private IApplication application; // 维护当前的应用

    /**
     * @deprecated
     */
    protected PIMViewInfo currentViewInfo;// 当前视图信息 @本变量为所有子类都要有的一个变量，本来准备作为静态的，但是因为完全可以通过
    // 控制activeView的唯一性（即activeView变量已经被AbstractApp控制为静态。所以此处不需控制，不过的确过于“绕”了。
    // TODO:逐步取消View对ActiveViewinfo的维护，因为App中已经有维护。
}
