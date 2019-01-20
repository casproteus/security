package org.cas.client.platform.bar.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.model.Category;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.model.Printer;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.ArrowButton;
import org.cas.client.platform.bar.uibeans.CategoryToggleButton;
import org.cas.client.platform.bar.uibeans.MenuButton;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class MenuPanel extends JPanel implements ActionListener {
	
    private int curCategoryPage = 0;
    private int categoryQtPerPage = 0;

    private int curMenuPage = 0;
    private int menuQTPerPage = 0;

    Integer categoryColumn;
    Integer categoryRow;
    Integer menuColumn;
    Integer menuRow;

    String[][] categoryNameMetrix;
    ArrayList<ArrayList<CategoryToggleButton>> onSrcCategoryTgbMatrix = new ArrayList<ArrayList<CategoryToggleButton>>();
    CategoryToggleButton tgbActiveCategory;
    
    //Dish is more complecated than category, it's devided by category first, then divided by page.
    String[][] dishNameMetrix;// the struction must be [3][index]. it's more convenient than [index][3]
    String[][] classifiedDishNameMetrix;// it's sub set of all menuNameMetrix
    private ArrayList<ArrayList<MenuButton>> onSrcMenuBtnMatrix = new ArrayList<ArrayList<MenuButton>>();

    private Printer[] printers;
    private Dish[] dishAry;
    private List<Dish> classifiedDishAry;
    ArrayList<Dish> selectdDishAry = new ArrayList<Dish>();
	public MenuPanel() {
		initComponent();
	}

	public void initComponent() {
	    categoryRow = BarOption.getCategoryRow();
	    categoryColumn = BarOption.getCategoryCol();
	    menuRow = BarOption.getDishRow();
	    menuColumn = BarOption.getDishCol();
	    
		btnPageUpCategory = new ArrowButton("↑");
        btnPageDownCategory = new ArrowButton("↓");
        btnPageUpMenu = new ArrowButton("↑");
        btnPageDownMenu = new ArrowButton("↓");
        
        btnPageUpCategory.setMargin(new Insets(0,0,0,0));
        btnPageDownCategory.setMargin(btnPageUpCategory.getInsets());
        btnPageUpMenu.setMargin(btnPageUpCategory.getInsets());
        btnPageDownMenu.setMargin(btnPageUpCategory.getInsets());
        
        btnPageUpCategory.setVisible(false);
        btnPageDownCategory.setVisible(false);
        btnPageUpMenu.setVisible(false);
        btnPageDownMenu.setVisible(false);

        btnPageUpCategory.addActionListener(this);
        btnPageDownCategory.addActionListener(this);
        btnPageUpMenu.addActionListener(this);
        btnPageDownMenu.addActionListener(this);
        
        setLayout(null);
        initPrinters();
		initCategoryAndDishes();
		reLayout();
		validate();
		repaint();
	}
	
    public void initCategoryAndDishes() {
        try {
            Statement statement = PIMDBModel.getReadOnlyStatement();

            // load all the categorys---------------------------
            ResultSet categoryRS = statement.executeQuery("select ID, LANG1, LANG2, LANG3 from CATEGORY  where DSP_INDEX >= 0 order by DSP_INDEX");
            categoryRS.afterLast();
            categoryRS.relative(-1);
            int tmpPos = categoryRS.getRow();
            categoryNameMetrix = new String[3][tmpPos];
            PrintService.allCategory = new Category[tmpPos];
            categoryRS.beforeFirst();

            tmpPos = 0;
            while (categoryRS.next()) {
                categoryNameMetrix[0][tmpPos] = categoryRS.getString("LANG1");
                categoryNameMetrix[1][tmpPos] = categoryRS.getString("LANG2");
                categoryNameMetrix[2][tmpPos] = categoryRS.getString("LANG3");
                
                PrintService.allCategory[tmpPos] =  new Category();
                PrintService.allCategory[tmpPos].setID(categoryRS.getInt("ID"));
                PrintService.allCategory[tmpPos].setDspIndex(tmpPos);
                PrintService.allCategory[tmpPos].setLanguage(new String[]{categoryNameMetrix[0][tmpPos],
                		categoryNameMetrix[1][tmpPos], categoryNameMetrix[2][tmpPos]});
                
                tmpPos++;
            }
            categoryRS.close();// 关闭

            // load all the dishes----------------------------
            ResultSet productRS =
                    statement
                            .executeQuery("select ID, CODE, MNEMONIC, SUBJECT, PRICE, FOLDERID, STORE,  COST, BRAND, CATEGORY, CONTENT, UNIT, PRODUCAREA, INDEX from product where deleted != true order by index");
            productRS.afterLast();
            productRS.relative(-1);
            tmpPos = productRS.getRow();
            dishNameMetrix = new String[3][tmpPos];
            dishAry = new Dish[tmpPos];
            productRS.beforeFirst();
            
            //compose the record into dish objects--------------
            tmpPos = 0;
            while (productRS.next()) { // @NOTE: don't load all the content, because menu can be many
                dishNameMetrix[0][tmpPos] = productRS.getString("CODE");
                dishNameMetrix[1][tmpPos] = productRS.getString("MNEMONIC");
                dishNameMetrix[2][tmpPos] = productRS.getString("SUBJECT");

                dishAry[tmpPos] = new Dish();
                dishAry[tmpPos].setId(productRS.getInt("ID"));
                dishAry[tmpPos].setLanguage(0, dishNameMetrix[0][tmpPos]);
                dishAry[tmpPos].setLanguage(1, dishNameMetrix[1][tmpPos]);
                dishAry[tmpPos].setLanguage(2, dishNameMetrix[2][tmpPos]);
                dishAry[tmpPos].setPrice(productRS.getInt("PRICE"));
                dishAry[tmpPos].setGst(productRS.getInt("FOLDERID"));
                dishAry[tmpPos].setQst(productRS.getInt("STORE"));
                dishAry[tmpPos].setSize(productRS.getInt("COST"));
                dishAry[tmpPos].setPrinter(productRS.getString("BRAND"));
                dishAry[tmpPos].setCATEGORY(productRS.getString("CATEGORY"));
                dishAry[tmpPos].setPrompPrice(productRS.getString("CONTENT"));
                dishAry[tmpPos].setPrompMenu(productRS.getString("UNIT"));
                dishAry[tmpPos].setPrompMofify(productRS.getString("PRODUCAREA"));
                dishAry[tmpPos].setDspIndex(productRS.getInt("INDEX"));
                tmpPos++;
            }
            productRS.close();// 关闭
			
        } catch (Exception e) {
            ErrorUtil.write(e);
        }
        curMenuPage = 0;
        reInitCategoryAndMenuBtns();
    }

	public void initPrinters() {
		
		int tmpPos;
		//load all printers--------------------------
		String sql = "select * from hardware where category = 0 order by id";
		try {
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
			rs.afterLast();
			rs.relative(-1);
			tmpPos = rs.getRow();
			setPrinters(new Printer[tmpPos]);
			rs.beforeFirst();
			tmpPos = 0;
			while (rs.next()) {
				getPrinters()[tmpPos] = new Printer();
				getPrinters()[tmpPos].setId(rs.getInt("id"));
				getPrinters()[tmpPos].setPname(rs.getString("name"));
				getPrinters()[tmpPos].setIp(rs.getString("ip"));
				getPrinters()[tmpPos].setFirstPrint(rs.getInt("style")); // index p1, p2.....
				getPrinters()[tmpPos].setType(rs.getInt("langType"));
				tmpPos++;
			}
			rs.close();
			
			// rearrange into map
			for(Printer printer:getPrinters()){
				if(printer.getIp().trim().length() > 0) {
					PrintService.ipPrinterMap.put(printer.getIp().trim(),printer);
				}
			}
		}catch(Exception e) {
			L.e("MenuPanel", "exception when initPrinters", e);
		}
	}

    // menu and category buttons must be init after initContent---------
    private void reInitCategoryAndMenuBtns() {
        // validate rows and columns first(in case they are changed into bad value)--------
        categoryColumn = (categoryColumn == null || categoryColumn < 4) ? 5 : categoryColumn;
        categoryRow = (categoryRow == null || categoryRow < 1 || categoryRow > 9) ? 3 : categoryRow;
        categoryQtPerPage = categoryColumn * categoryRow;

        menuColumn = (menuColumn == null || menuColumn < 1) ? 4 : menuColumn;
        menuRow = (menuRow == null || menuRow < 1) ? 4 : menuRow;
        menuQTPerPage = menuColumn * menuRow;

        // clean current catogory and menus from both screen and metrix if have---------------
        removeAll();
        add(btnPageUpCategory);
        add(btnPageDownCategory);
        add(btnPageUpMenu);
        add(btnPageDownMenu);
        
        onSrcCategoryTgbMatrix.clear();
        onSrcMenuBtnMatrix.clear();

        // create new buttons and add onto the screen (no layout yet)------------
        int globleCategoryIdxOfCurCategory = curCategoryPage * categoryQtPerPage;
        for (int r = 0; r < categoryRow; r++) {
            ArrayList<CategoryToggleButton> btnCategoryArry = new ArrayList<CategoryToggleButton>();
            for (int c = 0; c < categoryColumn; c++) {
                
                CategoryToggleButton btnCategory = new CategoryToggleButton(globleCategoryIdxOfCurCategory + 1);
                btnCategory.setMargin(new Insets(0, 0, 0, 0));
                add(btnCategory);
                btnCategory.addActionListener(this);
                btnCategoryArry.add(btnCategory);
                
                if (globleCategoryIdxOfCurCategory < categoryNameMetrix[0].length) {
                    btnCategory.setText(categoryNameMetrix[CustOpts.custOps.getUserLang()][globleCategoryIdxOfCurCategory]);
                    if (tgbActiveCategory != null && categoryNameMetrix[CustOpts.custOps.getUserLang()][globleCategoryIdxOfCurCategory].equalsIgnoreCase(tgbActiveCategory.getText())) {
                        btnCategory.setSelected(true);
                    }
                }
                
                globleCategoryIdxOfCurCategory++;
            }
            onSrcCategoryTgbMatrix.add(btnCategoryArry);
        }

        // if no activeCategory, use the first one on screen.
        if (tgbActiveCategory == null) {
            tgbActiveCategory = onSrcCategoryTgbMatrix.get(0).get(0);
            tgbActiveCategory.setSelected(true);
        }

        // initialize on screen menus===============================================================
        //find out menus matching to current category and current lang
        classifiedDishNameMetrix = new String[3][dishNameMetrix[0].length];
        classifiedDishAry = new ArrayList<Dish>();
        
        int classifiedMenuIndex = 0;
        for (int i = 0; i < dishAry.length; i++) {
			if(dishAry[i].getCATEGORY().equals(tgbActiveCategory.getText())) {
				
				classifiedDishNameMetrix[0][classifiedMenuIndex] = dishNameMetrix[0][i];
				classifiedDishNameMetrix[1][classifiedMenuIndex] = dishNameMetrix[1][i];
				classifiedDishNameMetrix[2][classifiedMenuIndex] = dishNameMetrix[2][i];
				
				classifiedDishAry.add(dishAry[i]);
				//make sure the display index are lined
				if(dishAry[i].getDspIndex() != classifiedMenuIndex + 1) {
					try {
		                Statement smt =  PIMDBModel.getReadOnlyStatement();

			            StringBuilder sql = new StringBuilder("UPDATE product SET INDEX = ").append(classifiedMenuIndex + 1)
			                            	.append(" where ID = ").append(dishAry[i].getId());
			            smt.executeUpdate(sql.toString());
			            smt.close();
		                smt = null;
		            }catch(Exception exp) {
		                exp.printStackTrace();
		            }
					dishAry[i].setDspIndex(classifiedMenuIndex + 1);
				}
				
				classifiedMenuIndex++;
			}
		}
        
        int globleMenuIdxOfCurCategory = curMenuPage * menuQTPerPage;	//the ones which will be on current page.
        for (int r = 0; r < menuRow; r++) {
            ArrayList<MenuButton> btnMenuArry = new ArrayList<MenuButton>();
            for (int c = 0; c < menuColumn; c++) {
                MenuButton btnMenu = new MenuButton(globleMenuIdxOfCurCategory + 1);
                btnMenu.setMargin(new Insets(0, 0, 0, 0));
                add(btnMenu);
                btnMenu.addActionListener(this);
                btnMenuArry.add(btnMenu);
                if (globleMenuIdxOfCurCategory < classifiedMenuIndex) {	//the last page could be not full page.
                    btnMenu.setText(classifiedDishNameMetrix[CustOpts.custOps.getUserLang()][globleMenuIdxOfCurCategory]);
                    btnMenu.setDish(classifiedDishAry.get(globleMenuIdxOfCurCategory));
                    globleMenuIdxOfCurCategory++;
                }
            }
            onSrcMenuBtnMatrix.add(btnMenuArry);
        }
        
        adjustPageArrowStatus(globleCategoryIdxOfCurCategory, globleMenuIdxOfCurCategory);
    }

	private void adjustPageArrowStatus(int globleCategoryIdxOfCurCategory, int globleMenuIdxOfCurCategory) {
		
		//for category panel page up
        if(globleCategoryIdxOfCurCategory <= categoryQtPerPage) {		//the last displayed category is less than categoryQtPerPage, means
        	btnPageUpCategory.setVisible(false);				  			// now it's on first page. no need to display up arrow.
        }else {
        	btnPageUpCategory.setVisible(true);			//as long as not the first page, page up should display.
        }
        //for category panel page down
        if(globleCategoryIdxOfCurCategory > categoryNameMetrix[0].length) {//if is the last page, and not full of last page, then should not display page down arrow.
        	btnPageDownCategory.setVisible(false);
        }else if(categoryNameMetrix[0].length < categoryRow * categoryColumn) {//if there'no enough to display, then don't display page down.
        	btnPageDownCategory.setVisible(false);
        }else {	// if there's more than one page to display, and currently it's not last page then show it.
        	btnPageDownCategory.setVisible(true);
        }
		
		//for menu panel page up
        if(globleMenuIdxOfCurCategory <= menuQTPerPage) {		//the last displayed menu is less than curMenuPerPage, means
            btnPageUpMenu.setVisible(false);				  	// now it's on first page. no need to display up arrow.
        }else {
        	btnPageUpMenu.setVisible(true);			//as long as not the first page, page up should display.
        }
        //for menu panel page down
        if(globleMenuIdxOfCurCategory == classifiedDishAry.size() && globleMenuIdxOfCurCategory % (menuRow * menuColumn) != 0) {//if is the last page, and not full of last page, then should not display page down arrow.
        	btnPageDownMenu.setVisible(false);
        }else if(classifiedDishAry.size() < menuRow * menuColumn) {//if there'no enough to display, then don't display page down.
        	btnPageDownMenu.setVisible(false);
        }else {	// if there's more than one page to display, and currently it's not last page then show it.
        	btnPageDownMenu.setVisible(true);
        }
	}

    void reLayout() {
        // category area--------------
        Float categoryHeight = BarOption.getCategoryAreaPortion();

        int categeryBtnWidth = (getWidth() - CustOpts.HOR_GAP * (categoryColumn - 1)) / categoryColumn;
        if(btnPageDownCategory.isVisible() || btnPageUpCategory.isVisible()) {
        	categeryBtnWidth = (getWidth() - CustOpts.HOR_GAP * categoryColumn - BarFrame.consts.SCROLLBAR_WIDTH) / categoryColumn;
        }
        int categeryBtnHeight =
                (int) ((getHeight() * categoryHeight - CustOpts.VER_GAP * (categoryRow - 1)) / categoryRow);

        for (int r = 0; r < categoryRow; r++) {
            for (int c = 0; c < categoryColumn; c++) {
                JToggleButton toggleButton = onSrcCategoryTgbMatrix.get(r).get(c);
                toggleButton.setBounds((categeryBtnWidth + CustOpts.HOR_GAP) * c,
                        (categeryBtnHeight + CustOpts.VER_GAP) * r, categeryBtnWidth, categeryBtnHeight);
            }
        }
        btnPageUpCategory.setBounds((categeryBtnWidth + CustOpts.HOR_GAP) * categoryColumn, 0, 
        	BarFrame.consts.SCROLLBAR_WIDTH, BarFrame.consts.SCROLLBAR_WIDTH * 2);
        btnPageDownCategory.setBounds(btnPageUpCategory.getX(),
                btnPageUpCategory.getY() + btnPageUpCategory.getHeight() + CustOpts.VER_GAP,
                BarFrame.consts.SCROLLBAR_WIDTH, BarFrame.consts.SCROLLBAR_WIDTH * 2);

        // menu area--------------
        int menuY = (categeryBtnHeight + CustOpts.VER_GAP) * categoryRow + CustOpts.VER_GAP;
        int menuBtnWidth = (getWidth() - CustOpts.HOR_GAP * (menuColumn - 1)) / menuColumn;
        if(btnPageUpMenu.isVisible() || btnPageDownMenu.isVisible()) {
        	menuBtnWidth = (getWidth() - CustOpts.HOR_GAP * menuColumn - BarFrame.consts.SCROLLBAR_WIDTH) / menuColumn;
        }
        int menuBtnHeight = (int) ((getHeight() * (1 - categoryHeight) - CustOpts.VER_GAP * (menuRow + 1)) / menuRow);
        for (int r = 0; r < menuRow; r++) {
            for (int c = 0; c < menuColumn; c++) {
                onSrcMenuBtnMatrix
                        .get(r)
                        .get(c)
                        .setBounds((menuBtnWidth + CustOpts.HOR_GAP) * c,
                                menuY + (menuBtnHeight + CustOpts.VER_GAP) * r, menuBtnWidth, menuBtnHeight);
            }
        }
        btnPageUpMenu.setBounds((menuBtnWidth + CustOpts.HOR_GAP) * menuColumn,
        		getHeight() - BarFrame.consts.SCROLLBAR_WIDTH * 4 - CustOpts.VER_GAP,
                BarFrame.consts.SCROLLBAR_WIDTH,
                BarFrame.consts.SCROLLBAR_WIDTH * 2);
        btnPageDownMenu.setBounds(btnPageUpMenu.getX(), btnPageUpMenu.getY() + btnPageUpMenu.getHeight()
                + CustOpts.VER_GAP, BarFrame.consts.SCROLLBAR_WIDTH, BarFrame.consts.SCROLLBAR_WIDTH * 2);

    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
		if(o instanceof ArrowButton) {
	        if(o == btnPageUpCategory) {
	            curCategoryPage--;
	            // adjust status
	            btnPageDownCategory.setVisible(true);
	            if (curCategoryPage == 0) {
	                btnPageUpCategory.setVisible(false);
	            }
	
	            reInitCategoryAndMenuBtns();
	            reLayout();
	        } else if (o == btnPageDownCategory) {
	            curCategoryPage++;
	            // adjust status
	            btnPageUpCategory.setVisible(true);
	            if (curCategoryPage * categoryQtPerPage > categoryNameMetrix.length) {
	                btnPageDownCategory.setVisible(false);
	            }
	
	            reInitCategoryAndMenuBtns();
	            reLayout();
	        } else if (o == btnPageUpMenu) {
	            curMenuPage--;
	            btnPageDownMenu.setVisible(true);
	            if (curMenuPage == 0) {
	                btnPageUpMenu.setVisible(false);
	            }
	            reInitCategoryAndMenuBtns();
	            reLayout();
	        } else if (o == btnPageDownMenu) {
	            curMenuPage++;
	            btnPageUpMenu.setVisible(true);
	            if (curMenuPage * menuQTPerPage > dishNameMetrix.length) {
	                btnPageDownMenu.setVisible(false);
	            }
	            reInitCategoryAndMenuBtns();
	            reLayout();
	        }
        }
        // category buttons---------------------------------------------------------------------------------
        else if (o instanceof CategoryToggleButton) {
            CategoryToggleButton categoryToggle = (CategoryToggleButton) o;
            String text = categoryToggle.getText();
            if (text == null || text.length() == 0) { // check if it's empty
                if (LoginDlg.USERTYPE == LoginDlg.ADMIN_STATUS) { // and it's admin mode, add a Category.
                    CategoryDlg addCategoryDlg = new CategoryDlg(BarFrame.instance);
                    addCategoryDlg.setIndex(categoryToggle.getIndex());
                    addCategoryDlg.setVisible(true);
                } else {
                    BarFrame.instance.switchMode(3);
                }
            } else { // if it's not empty
                if (!text.equals(tgbActiveCategory.getText())) {
                    //change active toggle button, and update active menus.
                    if (tgbActiveCategory != null) {
                       tgbActiveCategory.setSelected(false);
                   }
                   tgbActiveCategory = categoryToggle;
                   initCategoryAndDishes();	//fill menu buttons with menus belong to this category.
                   reLayout();
                } else if (getParent() == BarFrame.instance.panels[3]) {
                   CategoryDlg categoryDlg = new CategoryDlg(BarFrame.instance);
                   categoryDlg.setIndex(categoryToggle.getIndex());
                   categoryDlg.setVisible(true);
                } else {
                	((CategoryToggleButton) o).setSelected(true);
                }
            }
        }
        // menu buttons------------------------------------------------------------------------------------------
        else if (o instanceof MenuButton) {
            MenuButton menuButton = (MenuButton) o;
            String text = menuButton.getText();
            if (text == null || text.length() == 0) { // check if it's empty
                if (getParent() == BarFrame.instance.panels[3]) { // and it's setting panel, add a Category.
                	if(tgbActiveCategory == null || tgbActiveCategory.getText() == null || tgbActiveCategory.getText().length() < 1) {
    					JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.SetCatogoryFirst());
    					return;
                	}
                    new DishDlg(BarFrame.instance, menuButton.getDspIndex()).setVisible(true);
                } else {
                    BarFrame.instance.switchMode(3);
                }
            } else { // if it's not empty
                if (getParent() == BarFrame.instance.panels[3]) {
                    new DishDlg(BarFrame.instance, menuButton.getDish()).setVisible(true);
                } else {
                    // add into table.
                	((SalesPanel)BarFrame.instance.panels[2]).billPanel.addContentToList(menuButton.getDish());
                }
            }
        } 
	}

    public Printer[] getPrinters() {
		return printers;
	}

	public void setPrinters(Printer[] printers) {
		this.printers = printers;
	}

	private ArrowButton btnPageUpCategory;
    private ArrowButton btnPageDownCategory;
    private ArrowButton btnPageUpMenu;
    private ArrowButton btnPageDownMenu;
}
