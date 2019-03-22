package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.cas.client.platform.bar.uibeans.TableButton;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;

public class TabbleSettingDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, MouseListener{
	
	Color bg = new Color(132,34, 22);

    private TableButton btnPressed;
    private boolean isDragged;
    private int xGap, yGap;
    
    static ArrayList<TableButton> btnTables = new ArrayList<TableButton>();
    
    public TabbleSettingDlg(JFrame pParent) {
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
        	if (o == btnMore) {
        		//add on into db
        		Statement smt = PIMDBModel.getStatement();
        		boolean isAnyTableButtonSelected = false;
        		for (TableButton tableButton : btnTables) {
					if (tableButton.getBackground().equals(bg)) {
						isAnyTableButtonSelected = true;
						String tableName = tableButton.getText();
						try {
							int value = Integer.valueOf(tableName) + 1;
							boolean isNameUsed = isNameUserd(String.valueOf(value), btnTables);
							if(isNameUsed) {
								createTempTable(smt, tableButton);
							}else {	//the new name is number and is not used
								createdAlignedTable(smt, tableButton, value);
							}
						}catch(Exception exp) {	// if copying non-number table will trigger the exception and create temporal name table.
							createTempTable(smt, tableButton);
						}
					}
				}
        		//if no table selected, then Add a new table();
        		if(!isAnyTableButtonSelected) {
        			new TableDlg(this, new TableButton()).setVisible(true);
        		}
        		initContent();
        		btnTables.get(btnTables.size() - 1).setBackground(bg);
        	}else if(o == btnLess) {
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
        		initContent();
        	}else if (o ==btnModify) {
        		for (TableButton tableButton : btnTables) {
					if (tableButton.getBackground().equals(bg)) {
						new TableDlg(this, tableButton).setVisible(true);
						return;
					}
        		}
        	}
        }

    }

	public void createdAlignedTable(Statement smt, TableButton tableButton, int value) {
		String tableName = String.valueOf(value);
		int x = (value / 10) * (tableButton.getWidth() + 10) + 10;
		int y = (value - (value / 10) * 10) * (tableButton.getHeight() + 10) + 10;
		StringBuilder sql = new StringBuilder("INSERT INTO DINING_TABLE (name, posX, posY, width, height, type) VALUES ('")
				.append(tableName).append("', ").append(x).append(", ")
				.append(y).append(", ")
				.append(tableButton.getWidth()).append(", ").append(tableButton.getHeight()).append(", ")
				.append(tableButton.getType()).append(")");
		try {
			smt.executeUpdate(sql.toString());
		}catch(Exception exp) {
			ErrorUtil.write(exp);
		}
	}

	private boolean isNameUserd(String value, ArrayList<TableButton> btnTables) {
		for (TableButton tableButton2 : btnTables) {
			if(tableButton2.getText().equals(value)) {	//if new number already used, then created temporal name table
				return true;
			}
		}
		return false;
	}

	public void createTempTable(Statement smt, TableButton tableButton) {
		String tableName = tableButton.getText() + "_copy";
		String sql = "INSERT INTO DINING_TABLE (name, posX, posY, width, height, type) VALUES ('"
				+ tableName + "', " + (tableButton.getX() + 10) + ", " + (tableButton.getY() + 10) + ", "
				+ tableButton.getWidth() + ", " + tableButton.getHeight() + ", " + tableButton.getType() + ")";
		try {
			smt.executeUpdate(sql);
		}catch(Exception exp) {
			ErrorUtil.write(exp);
		}
	}

    /** 本方法用于设置View上各个组件的尺寸。 */
    @Override
    public void reLayout() {
        int panelHeight = getHeight() - CustOpts.SIZE_EDGE;
        int tBtnWidht = 120;
        int tBtnHeight = 50;

        // command buttons--------------
        btnModify.setBounds(getWidth() - tBtnWidht - CustOpts.HOR_GAP - CustOpts.SIZE_EDGE - 50,
        		panelHeight - tBtnHeight - CustOpts.VER_GAP - CustOpts.SIZE_EDGE - 50, 
        		tBtnWidht, tBtnHeight);
        btnLess.setBounds(btnModify.getX(),
        		btnModify.getY() - tBtnHeight - CustOpts.VER_GAP,
        		tBtnHeight, tBtnHeight);
        btnMore.setBounds(btnLess.getX() + btnLess.getWidth() + 20, btnLess.getY(),
        		tBtnHeight, tBtnHeight);

		invalidate();
		revalidate();
		repaint();
    }

    private void initDialog() {
        btnMore = new JButton("+");
        btnLess = new JButton("-");
        btnModify = new JButton(BarFrame.consts.MODIFY());

        // border----------
        setBounds(BarFrame.instance.getX(), BarFrame.instance.getY(), BarFrame.instance.getWidth(), BarFrame.instance.getHeight());
        setLayout(null);

        // built
        add(btnMore);
        add(btnLess);
        add(btnModify);

        // add listener
        addComponentListener(this);
        
        btnMore.addActionListener(this);
        btnLess.addActionListener(this);
        btnModify.addActionListener(this);
        initContent();
    }

    // menu and category buttons must be init after initContent---------
	void initContent() {
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
            	//tableToggleButton.setMargin(new Insets(0, 0, 0, 0));
    			tableToggleButton.addActionListener(this);
    			tableToggleButton.addMouseMotionListener(new MouseMotionListener(){
    	        	@Override
					public void mouseDragged(MouseEvent e) {
    	        		if(btnPressed != null) {
    	        			btnPressed.setLocation(btnPressed.getX() + e.getX() - xGap, btnPressed.getY() + e.getY() - yGap);
    	        			isDragged = true;
    	        		}
    	        	}
    	        	@Override
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
	
    private JButton btnMore;
    private JButton btnLess;
    private JButton btnModify;

	@Override
	public void mouseClicked(MouseEvent e) {}

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
	                initContent();
	        	}catch(Exception exp) {
	        		ErrorUtil.write(exp);
	        	}
				reLayout();
			}
		}

		isDragged = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
