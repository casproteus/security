package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.cas.client.platform.bar.uibeans.ColorChooserButton;
import org.cas.client.platform.bar.uibeans.ColorChooserButton.ColorChangedListener;
import org.cas.client.platform.cascustomize.CustOpts;

public class SettingColorDlg extends JDialog implements ActionListener, ComponentListener {

    /**
     * 创建一个 Category 的实例
     * 
     * @param prmParent
     *            父窗体
     * @param prmCategoryInfo
     *            逗号分隔的字符串
     */
    public SettingColorDlg(Frame prmParent) {
        super(prmParent, true);
        initComponent(); // 组件初始化并布局
    }

    /** Invoked when the component's size changes. */
    @Override
	public void componentResized(ComponentEvent e) {
        reLayout();
    }

    /** Invoked when the component's position changes. */
    @Override
	public void componentMoved(ComponentEvent e) {}

    /** Invoked when the component has been made visible. */
    @Override
	public void componentShown(ComponentEvent e) {}

    /** Invoked when the component has been made invisible. */
    @Override
	public void componentHidden(ComponentEvent e) {}

    /** 初始化并布局; */
    private void initComponent() {
        setTitle(BarFrame.consts.Color()); // 设置标题
        getContentPane().setLayout(null);
        setBounds((CustOpts.SCRWIDTH - 350) / 2, (CustOpts.SCRHEIGHT - 440) / 2, 350, 440); // 对话框的默认尺寸。
        setResizable(true);

        // init--------------------------
        lblLoginBackGround = new JLabel(BarFrame.consts.LoginBK());
        ccbLogin = new ColorChooserButton(BarOption.getBK("Login"));
        lblTablePanelBackGround = new JLabel(BarFrame.consts.TablePanelBK());
        ccbTablePanel = new ColorChooserButton(BarOption.getBK("TablePanel"));
        lblBillBackGround = new JLabel(BarFrame.consts.BillPanelBK());
        ccbBill = new ColorChooserButton(BarOption.getBK("Bill"));
        lblSalesBackGround = new JLabel(BarFrame.consts.SalesPanelBK());
        ccbSales = new ColorChooserButton(BarOption.getBK("Sales"));
        lblSettingsBackGround = new JLabel(BarFrame.consts.SettingsPanelBK());
        ccbSetting = new ColorChooserButton(BarOption.getBK("Setting"));
        lblCategoryBackGround = new JLabel(BarFrame.consts.CategoryBtnBK());
        ccbCategory = new ColorChooserButton(BarOption.getBK("Category"));
        lblDishBackGround = new JLabel(BarFrame.consts.DishBtnBK());
        ccbDish = new ColorChooserButton(BarOption.getBK("Dish"));
        lblFunctionBackGround = new JLabel(BarFrame.consts.FunctionBtnBK());
        ccbFunctionBtn = new ColorChooserButton(BarOption.getBK("Function"));
        lblTableBackGround = new JLabel(BarFrame.consts.TableBtnBK());
        ccbTable = new ColorChooserButton(BarOption.getBK("Table"));
        lblSelectedTableBackGround = new JLabel(BarFrame.consts.SelectedTableBtnBK());
        ccbTableSelected = new ColorChooserButton(BarOption.getBK("TableSelected"));
        lblNumBtnBackGround = new JLabel(BarFrame.consts.NumberBtnBk());
        ccbNumBtn = new ColorChooserButton(BarOption.getBK("NumBtn"));
		lblArrowBtnBackGround = new JLabel(BarFrame.consts.ArrowBtnBk());
		ccbArrowBtn = new ColorChooserButton(BarOption.getBK("Arrow"));

        btnOK = new JButton(BarFrame.consts.OK()); // 设置Cancel按钮

        // layout---------------------------
        reLayout();

        // build----------------------------
        add(lblLoginBackGround);
        add(ccbLogin);
        add(lblLoginBackGround);
        add(ccbLogin);
        add(lblTablePanelBackGround);
        add(ccbTablePanel);
        add(lblBillBackGround);
        add(ccbBill);
        add(lblSalesBackGround);
        add(ccbSales);
        add(lblSettingsBackGround);
        add(ccbSetting);
        add(lblCategoryBackGround);
        add(ccbCategory);
        add(lblDishBackGround);
        add(ccbDish);
        add(lblFunctionBackGround);
        add(ccbFunctionBtn);
        add(lblTableBackGround);
        add(ccbTable);
        add(lblSelectedTableBackGround);
        add(ccbTableSelected);
        add(lblNumBtnBackGround);
        add(ccbNumBtn);
		add(lblArrowBtnBackGround);
		add(ccbArrowBtn);
        
        add(btnOK);

        // listeners------------------------
        btnOK.addActionListener(this);
        ccbLogin.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Login");
            }
        });
        ccbTablePanel.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"TablePanel");
                 ((TablesPanel)BarFrame.instance.panels[0]).initComponent();
            }
        });
        ccbBill.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Bill");
                 ((BillListPanel)BarFrame.instance.panels[1]).initComponent();
            }
        });
        ccbSales.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Sales");
                 ((SalesPanel)BarFrame.instance.panels[2]).initComponent();
            }
        });
        ccbSetting.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Setting");
                 initComponent();
                 add(BarFrame.menuPanel);
            }
        });
        ccbCategory.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Category");
                 BarFrame.menuPanel.initComponent();
            }
        });
        ccbDish.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Dish");
                 BarFrame.menuPanel.initComponent();
            }
        });
        ccbFunctionBtn.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Function");
                 ((TablesPanel)BarFrame.instance.panels[0]).initComponent();
                 ((BillListPanel)BarFrame.instance.panels[1]).initComponent();
                 ((SalesPanel)BarFrame.instance.panels[2]).initComponent();
                 //this will cause the dialog can not recoginze the OK button.  initComponent();
                 add(BarFrame.menuPanel);
            }
        });
        ccbTable.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Table");
                 ((TablesPanel)BarFrame.instance.panels[0]).initComponent();
            }
        });
        ccbTableSelected.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"TableSelected");
                 ((TablesPanel)BarFrame.instance.panels[0]).initComponent();
            }
        });
        ccbNumBtn.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"NumBtn");
                 BarFrame.numberPanelDlg.initComponent();
                 BarFrame.payDlg.initComponent();
            }
        });
		ccbArrowBtn.addColorChangedListener(new ColorChangedListener() {
			@Override
			public void colorChanged(Color newColor) {
				BarOption.setBK(newColor, "Arrow");
			}
		});
        getContentPane().addComponentListener(this);
        
        reLayout();
    }

    private void reLayout() {
        btnOK.setBounds(getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.BTN_WIDTH - 2 * CustOpts.HOR_GAP, 
        		getHeight() - CustOpts.BTN_HEIGHT - CustOpts.SIZE_EDGE - CustOpts.SIZE_TITLE - 2 * CustOpts.VER_GAP,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        
        lblLoginBackGround.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
        		lblLoginBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbLogin.setBounds(lblLoginBackGround.getX() + lblLoginBackGround.getWidth() + CustOpts.HOR_GAP, lblLoginBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        
        lblTablePanelBackGround.setBounds(CustOpts.HOR_GAP, lblLoginBackGround.getY() + lblLoginBackGround.getHeight() + CustOpts.VER_GAP,
        		lblTablePanelBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbTablePanel.setBounds(lblTablePanelBackGround.getX() + lblTablePanelBackGround.getWidth() + CustOpts.HOR_GAP, lblTablePanelBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblBillBackGround.setBounds(CustOpts.HOR_GAP, lblTablePanelBackGround.getY() + lblTablePanelBackGround.getHeight() + CustOpts.VER_GAP,
        		lblBillBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbBill.setBounds(lblBillBackGround.getX() + lblBillBackGround.getWidth() + CustOpts.HOR_GAP, lblBillBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblSalesBackGround.setBounds(CustOpts.HOR_GAP, lblBillBackGround.getY() + lblBillBackGround.getHeight() + CustOpts.VER_GAP,
        		lblSalesBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbSales.setBounds(lblSalesBackGround.getX() + lblSalesBackGround.getWidth() + CustOpts.HOR_GAP, lblSalesBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblSettingsBackGround.setBounds(CustOpts.HOR_GAP, lblSalesBackGround.getY() + lblSalesBackGround.getHeight() + CustOpts.VER_GAP,
        		lblSettingsBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbSetting.setBounds(lblSettingsBackGround.getX() + lblSettingsBackGround.getWidth() + CustOpts.HOR_GAP, lblSettingsBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblCategoryBackGround.setBounds(CustOpts.HOR_GAP, lblSettingsBackGround.getY() + lblSettingsBackGround.getHeight() + CustOpts.VER_GAP,
        		lblCategoryBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbCategory.setBounds(lblCategoryBackGround.getX() + lblCategoryBackGround.getWidth() + CustOpts.HOR_GAP, lblCategoryBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblDishBackGround.setBounds(CustOpts.HOR_GAP, lblCategoryBackGround.getY() + lblCategoryBackGround.getHeight() + CustOpts.VER_GAP,
        		lblDishBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbDish.setBounds(lblDishBackGround.getX() + lblDishBackGround.getWidth() + CustOpts.HOR_GAP, lblDishBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblFunctionBackGround.setBounds(CustOpts.HOR_GAP, lblDishBackGround.getY() + lblDishBackGround.getHeight() + CustOpts.VER_GAP,
        		lblFunctionBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbFunctionBtn.setBounds(lblFunctionBackGround.getX() + lblFunctionBackGround.getWidth() + CustOpts.HOR_GAP, lblFunctionBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblTableBackGround.setBounds(CustOpts.HOR_GAP, lblFunctionBackGround.getY() + lblFunctionBackGround.getHeight() + CustOpts.VER_GAP,
        		lblTableBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbTable.setBounds(lblTableBackGround.getX() + lblTableBackGround.getWidth() + CustOpts.HOR_GAP, lblTableBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblSelectedTableBackGround.setBounds(CustOpts.HOR_GAP, lblTableBackGround.getY() + lblTableBackGround.getHeight() + CustOpts.VER_GAP,
        		lblSelectedTableBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbTableSelected.setBounds(lblSelectedTableBackGround.getX() + lblSelectedTableBackGround.getWidth() + CustOpts.HOR_GAP, lblSelectedTableBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblNumBtnBackGround.setBounds(CustOpts.HOR_GAP, lblSelectedTableBackGround.getY() + lblSelectedTableBackGround.getHeight() + CustOpts.VER_GAP,
        		lblNumBtnBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbNumBtn.setBounds(lblNumBtnBackGround.getX() + lblNumBtnBackGround.getWidth() + CustOpts.HOR_GAP, lblNumBtnBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
		lblArrowBtnBackGround.setBounds(CustOpts.HOR_GAP,
				lblNumBtnBackGround.getY() + lblNumBtnBackGround.getHeight() + CustOpts.VER_GAP,
				lblArrowBtnBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		ccbArrowBtn.setBounds(lblArrowBtnBackGround.getX() + lblArrowBtnBackGround.getWidth() + CustOpts.HOR_GAP,
				lblArrowBtnBackGround.getY(), 100, CustOpts.BTN_HEIGHT);
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    @Override
	public void actionPerformed( ActionEvent e) {
         if (e.getSource() == btnOK) {
        	this.setVisible(false);
            dispose();
        }
    }

    // 以下为本类的变量声明
    private JButton btnOK;
    JLabel lblLoginBackGround;
    ColorChooserButton ccbLogin;
    JLabel lblTablePanelBackGround;
    ColorChooserButton ccbTablePanel;
    JLabel lblBillBackGround;
    ColorChooserButton ccbBill;
    JLabel lblSalesBackGround;
    ColorChooserButton ccbSales;
    JLabel lblSettingsBackGround;
    ColorChooserButton ccbSetting;
    JLabel lblCategoryBackGround;
    ColorChooserButton ccbCategory;
    JLabel lblDishBackGround;
    ColorChooserButton ccbDish;
    JLabel lblFunctionBackGround;
    ColorChooserButton ccbFunctionBtn;
    JLabel lblTableBackGround;
    ColorChooserButton ccbTable;
    JLabel lblSelectedTableBackGround;
    ColorChooserButton ccbTableSelected;
    JLabel lblNumBtnBackGround;
    ColorChooserButton ccbNumBtn;
	JLabel lblArrowBtnBackGround;
	ColorChooserButton ccbArrowBtn;
}
