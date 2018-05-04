package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.cas.client.platform.bar.beans.TableButton;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.menuaction.SaveContentsAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.contact.dialog.ContactDlg;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.resource.international.DlgConst;

public class SettingTabbleDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, MouseListener{
	
	Color bg = new Color(132,34, 22);

    private TableButton btnPressed;
    private boolean isDragged;
    private int xGap, yGap;
    
    static ArrayList<TableButton> btnTables = new ArrayList<TableButton>();
    
    public SettingTabbleDlg(JFrame pParent) {
        super(pParent, false);
        initDialog();
    }

    @Override
    public PIMRecord getContents() {
        return null;
    }

    @Override
    public boolean setContents(
            PIMRecord prmRecord) {
        return true;
    }

    @Override
    public void makeBestUseOfTime() {
    }

	@Override
	public void addAttach(File[] file, Vector actualAttachFiles) {
	}

    @Override
    public PIMTextPane getTextPane() {
        return null;
    }

    @Override
    public void release() {
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    @Override
    public void componentMoved(
            ComponentEvent e) {
    };

    @Override
    public void componentShown(
            ComponentEvent e) {
    };

    @Override
    public void componentHidden(
            ComponentEvent e) {
    };

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    // ActionListner----------------------------------
    @Override
    public void actionPerformed(
            ActionEvent e) {
        JButton o = (JButton)e.getSource();
        if(o instanceof TableButton) {
        	if(o.getBackground() != bg) {
        		o.setBackground(bg);
        	}else {
        		o.setBackground(null);
        	}
        }else {
        	if (o == btnLine_1_1) {
        		//add on into db
        		Statement smt = PIMDBModel.getStatement();
        		for (TableButton tableButton : btnTables) {
					if (tableButton.getBackground().equals(bg)) {
						String tableName = tableButton.getText() + "_copy";
						String sql = "INSERT INTO DINING_TABLE (name, posX, posY, width, height, type) VALUES ('"
								+ tableName + "', " + (tableButton.getX() + 10) + ", " + (tableButton.getY() + 10) + ", "
								+ tableButton.getWidth() + ", " + tableButton.getHeight() + ", " + tableButton.getType() + ")";
						try {
							smt.execute(sql);
						}catch(Exception exp) {
							ErrorUtil.write(exp);
						}
					}
				}
        		initTableBtns();
        	}else if(o == btnLine_1_2) {
        		Statement smt = PIMDBModel.getStatement();
        		for (TableButton tableButton : btnTables) {
					if (tableButton.getBackground().equals(bg)) {
						int id = tableButton.getId();
						String sql = "delete from DINING_TABLE where id = " + id;
						try {
							smt.execute(sql);
						}catch(Exception exp) {
							ErrorUtil.write(exp);
						}
					}
				}
        		initTableBtns();
        	}else if (o ==btnLine_1_3) {
        		for (TableButton tableButton : btnTables) {
					if (tableButton.getBackground().equals(bg)) {
						new ModifyTableDlg(this, tableButton).setVisible(true);
						return;
					}
        		}
        	}
        }

    }

    /** 本方法用于设置View上各个组件的尺寸。 */
    @Override
    public void reLayout() {
        int panelWidth = getWidth();
        int panelHeight = getHeight() - CustOpts.SIZE_EDGE;
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 10) / 9;
        int tBtnHeight = panelHeight / 10;

        // command buttons--------------
        btnLine_1_3.setBounds(panelWidth - 100 - CustOpts.HOR_GAP,
        		panelHeight - tBtnHeight - 38, 
        		60, 60);
        btnLine_1_2.setBounds(btnLine_1_3.getX(),
        		btnLine_1_3.getY() - tBtnHeight - CustOpts.VER_GAP,
        		60, 60);
        btnLine_1_1.setBounds(btnLine_1_2.getX(),
        		btnLine_1_2.getY() - tBtnHeight - CustOpts.VER_GAP,
        		60, 60);

		invalidate();
		revalidate();
		repaint();
    }

    private void initDialog() {
        btnLine_1_1 = new JButton("+");
        btnLine_1_2 = new JButton("-");
        btnLine_1_3 = new JButton(BarDlgConst.MODIFY);

        // border----------
        setBounds(BarFrame.instance.getX(), BarFrame.instance.getY(), BarFrame.instance.getWidth(), BarFrame.instance.getHeight());
        setLayout(null);

        // built
        add(btnLine_1_1);
        add(btnLine_1_2);
        add(btnLine_1_3);

        // add listener
        addComponentListener(this);
        
        btnLine_1_1.addActionListener(this);
        btnLine_1_2.addActionListener(this);
        btnLine_1_3.addActionListener(this);
        initTableBtns();
    }

    // menu and category buttons must be init after initContent---------
	void initTableBtns() {
		//clean existing btns
		for (int i = btnTables.size() - 1; i >=0; i--) {
			TableButton tableToggleButton = btnTables.get(i);
			btnTables.remove(i);
			remove(tableToggleButton);
		}
		//renite buttons.
		try {
            Statement smt = PIMDBModel.getReadOnlyStatement();

            // load all the categorys---------------------------
            ResultSet rs = smt.executeQuery("select ID, Name, posX, posY, width, height, type, status from dining_Table order by DSP_INDEX");
            rs.beforeFirst();

            while (rs.next()) {
            	TableButton tableToggleButton = new TableButton();
            	
            	tableToggleButton.setId(rs.getInt("ID"));
            	tableToggleButton.setText(rs.getString("Name"));
            	tableToggleButton.setBounds(rs.getInt("posX"), rs.getInt("posY"), rs.getInt("width"), rs.getInt("height"));
            	tableToggleButton.setType(rs.getInt("type"));		//it's rectanglee or round?
            	tableToggleButton.setMargin(new Insets(0, 0, 0, 0));
    			tableToggleButton.addActionListener(this);
    			tableToggleButton.addMouseMotionListener(new MouseMotionListener(){
    	        	public void mouseDragged(MouseEvent e) {
    	        		if(btnPressed != null) {
    	        			btnPressed.setLocation(btnPressed.getX() + e.getX() - xGap, btnPressed.getY() + e.getY() - yGap);
    	        			isDragged = true;
    	        		}
    	        	}
    	        	public void mouseMoved(MouseEvent e) {}
    	        });
    			tableToggleButton.addMouseListener(this);
    			this.add(tableToggleButton);
    			
            	btnTables.add(tableToggleButton);
            }
            
            rs.close();// 关闭
            smt.close();
		}catch(Exception e) {
			ErrorUtil.write("Unexpected exception when init the tables from db." + e);
		}
		reLayout();
	}
	
    private JButton btnLine_1_1;
    private JButton btnLine_1_2;
    private JButton btnLine_1_3;

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Object o = e.getSource();
		if( o instanceof TableButton) {
			btnPressed = (TableButton)o;
			xGap = e.getX();
			yGap = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Object o = e.getSource();
		if(o instanceof TableButton) {
			if(btnPressed != null && isDragged) {
				TableButton btn = (TableButton)o;
				//updateDB;
				String sql = "Update DINING_TABLE set posX = " + (btn.getX() + e.getX() - xGap) + ", posY = " + (btn.getY() + e.getY()-yGap) + " where id = " + btn.getId();
	        	try {
	        		PIMDBModel.getStatement().execute(sql);
	                initTableBtns();
	        	}catch(Exception exp) {
	        		ErrorUtil.write(exp);
	        	}
				reLayout();
			}
		}

		isDragged = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
