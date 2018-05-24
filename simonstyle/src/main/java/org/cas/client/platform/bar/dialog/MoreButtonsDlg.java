package org.cas.client.platform.bar.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.cas.client.platform.cascustomize.CustOpts;

public class MoreButtonsDlg extends JFrame implements ActionListener, WindowFocusListener{
	SalesPanel barGeneralPanel;

    public MoreButtonsDlg(SalesPanel general) {
    	super();
    	setTitle(BarFrame.consts.MORE);
        barGeneralPanel = general;
        initPanel();
    }

    /**
     * Invoked when an action occurs. NOTE:PIM的绝大多数用于新建和编辑的对话盒，对于确定事件的处理，采用如下规则：
     * 即：先出发监听器事件，监听器根据IPIMDialog接口的方法getContent（）取出对话盒中的 记录。监听器负责将记录存入Model，监听器最后负责将对话盒释放。
     * 目的是让所有对话盒只认识一个叫Record的东西，不认识别的。
     * 
     * @param e
     *            动作事件
     */
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == btnLine_3_1) {

        } else if (o == btnLine_3_2) {
        	
        } else if (o == btnLine_3_3) {
        	
        } else if (o == btnLine_3_4) {
        	
        } else if (o == btnLine_3_5) {
        	
        } else if (o == btnLine_3_6) {
        	
        }
//        else if (o == btnLine_3_7) {
//        	
//        } else if (o == btnLine_3_8) {
//        	
//        } else if (o == btnLine_3_9) {
//        	
//        }
    }
    
	public void show(JButton btnMore) {
		reLayout(btnMore);
		this.setVisible(true);
	}
	
	private void reLayout(JButton btnMore) {
		int x = btnMore.getX();
		int y = btnMore.getY();
		int width = btnMore.getWidth();
		int height = btnMore.getHeight();
		
		btnLine_3_1.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width, height);
		btnLine_3_2.setBounds(btnLine_3_1.getX(), btnLine_3_1.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		btnLine_3_3.setBounds(btnLine_3_1.getX(), btnLine_3_2.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		btnLine_3_4.setBounds(btnLine_3_1.getX(), btnLine_3_3.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		btnLine_3_5.setBounds(btnLine_3_1.getX(), btnLine_3_4.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		btnLine_3_6.setBounds(btnLine_3_1.getX(), btnLine_3_5.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		//btnLine_3_7.setBounds(btnLine_3_1.getX(), btnLine_3_6.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		//btnLine_3_8.setBounds(btnLine_3_1.getX(), btnLine_3_7.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		//btnLine_3_9.setBounds(btnLine_3_1.getX(), btnLine_3_8.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
        
		int panelHeight = height * 6 + CustOpts.VER_GAP * 7;
		setBounds(x, y - panelHeight, 
				width + CustOpts.HOR_GAP * 2 + CustOpts.SIZE_EDGE * 2 + 10,
				panelHeight + CustOpts.SIZE_EDGE * 2 + 40);
	}
	
	private void initPanel() {
		// 初始化－－－－－－－－－－－－－－－－
		btnLine_3_1 = new JButton(BarFrame.consts.SEND);
		btnLine_3_2 = new JButton(BarFrame.consts.PAY);
		btnLine_3_3 = new JButton(BarFrame.consts.PRINT_BILL);
		btnLine_3_4 = new JButton(BarFrame.consts.QUICK_OPEN);
		btnLine_3_5 = new JButton(BarFrame.consts.VOID_ORDER);
		btnLine_3_6 = new JButton(BarFrame.consts.MODIFY);
		//btnLine_3_7 = new JButton(BarFrame.consts.DISC_VOLUMN);
		//btnLine_3_8 = new JButton(BarFrame.consts.LOGOUT);
		//btnLine_3_9 = new JButton(BarFrame.consts.MORE);

		// 属性设置－－－－－－－－－－－－－－
		btnLine_3_1.setMargin(new Insets(0, 0, 0, 0));
		btnLine_3_2.setMargin(btnLine_3_1.getMargin());
		btnLine_3_3.setMargin(btnLine_3_1.getMargin());
		btnLine_3_4.setMargin(btnLine_3_1.getMargin());
		btnLine_3_5.setMargin(btnLine_3_1.getMargin());
		btnLine_3_6.setMargin(btnLine_3_1.getMargin());
		//btnLine_3_7.setMargin(btnLine_3_1.getMargin());
		//btnLine_3_8.setMargin(btnLine_3_1.getMargin());
		//btnLine_3_9.setMargin(btnLine_3_1.getMargin());
		
		// 布局---------------
		setLayout(null);
		
		// 搭建－－－－－－－－－－－－－
		add(btnLine_3_1);
		add(btnLine_3_2);
		add(btnLine_3_3);
		add(btnLine_3_4);
		add(btnLine_3_5);
		add(btnLine_3_6);
		//add(btnLine_3_7);
		//add(btnLine_3_8);
		//add(btnLine_3_9);

		// 加监听器－－－－－－－－
		btnLine_3_1.addActionListener(this);
		btnLine_3_2.addActionListener(this);
		btnLine_3_3.addActionListener(this);
		btnLine_3_4.addActionListener(this);
		btnLine_3_5.addActionListener(this);
		btnLine_3_6.addActionListener(this);
		//btnLine_3_7.addActionListener(this);
		//btnLine_3_8.addActionListener(this);
		//btnLine_3_9.addActionListener(this);
		
		this.addWindowFocusListener(this);
	}
	
	private JButton btnLine_3_1;
	private JButton btnLine_3_2;
	private JButton btnLine_3_3;
	private JButton btnLine_3_4;
	private JButton btnLine_3_5;
	private JButton btnLine_3_6;
//	private JButton btnLine_3_7;
//	private JButton btnLine_3_8;
//	private JButton btnLine_3_9;

	@Override
	public void windowGainedFocus(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		// TODO Auto-generated method stub
		dispose();
	}
}
