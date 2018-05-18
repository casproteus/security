package org.cas.client.platform.bar.dialog;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public interface BarDlgConst {
    int SCROLLBAR_WIDTH = 40;
    int SubTotal_HEIGHT = 50;
    String ChangeDate = "CHANGE DATE";
    String ViewDetail = "VIEW DETAIL";
    String CLOSE = "CLOSE";
    String Tip = "Tip";
    String ServiceFee = "Service Fee";
    String CashBack = "Cash Back";
    String Status = "Status";
    String APPLY = "APPLY";
    
    String ChangeMode = "CHANGE MODE";
    String AddTable = "ADD TABLE";
    String OrderManage = "CHECK ORDER";
    String OpenDrawer = "OPEN DRAWER";
    String VolumnDiscount = "DISC BILL";
    String WaiterReport = "SERVER REPORT";
    String Reservation = "RESERVATION";
    String Report = "REPORT";
    String CheckInOut = "SING IN/OUT";
    
    String Menu = "Menu";
    String Language1 = "Language1";
    String Language2 = "Language2";
    String Language3 = "Language3";
    String DSPINDEX = "Idx";
    String PRICE = "Price";

    String EXACT_AMOUNT = "EXACT AMOUNT";
    String CASH = "CASH";
    String PAY = "PAY";
    String REMOVEITEM = "REMOVE ITEM";
    String VOID_ITEM = "VOID ITEM";
    String SPLIT_BILL = "SPLIT BILL";
    String Modify = "MODIFY";
    String ChangePrice = "CHANGE PRICE";
    String TIP = "SERVICE FEE";
    String QTY = "QTY";
    String QTYNOTICE = "will be applied to selected dish!";
    String DISC_ITEM = "DISC ITEM";
    String PRINT_BILL = "PRINT BILL";

    String DEBIT = "DEBIT";
    String VISA = "VISA";
    String MASTER = "MASTER";
    String CANCEL_ALL = "CANCEL ALL";
    String VOID_ORDER = "VOID ORDER";
    String SETTINGS = "SETTINGS";
    String RETURN = "RETURN";//
    String MORE = "MORE";
    String SEND = "SEND";

    String EQUL_BILL = "EQUL BILL";
    String FAST_DISCOUNT = "FAST DISCOUNT";
    String QUICK_OPEN = "QUICK OPEN";
    String MODIFY = "MODIFY";
    String DISC_VOLUMN = "DISC VOLUMN";

    String Printer = "Printer";
    String OnlyOneShouldBeSelected = "Please make sure one and only one item selected, then try again.";
    String InvalidInput = "The inputted content is invalid for this function.";
    String ADMIN_MODE = "System is in setting mode! Click logout button back to use mode.";
    String USE_MODE = "System is in operating mode!";
    String Title = "STGOBar - Copyright 2018 - http://www.ShareTheGoodOnes.com";
    String DuplicatedInput = "Dubplicated name found! please choose an other name.";
    String NoBillSeleted = "Found no bill to split the item to. Please select at least one other bill before release the button.";
    String Colon = "：";
    String Operator = "USER";
    String Table = "Table";
    String Bill = "Bill";
    String UnSavedRecordFound = "Are you sure to split the bill withoud sending new added ones? the un send records will be cancelled";
    String COMFIRMDELETEACTION = "The dish might allready be prepared, are you sure to cancel?";
    String COMFIRMDELETEACTION2 = "Are you sure to remove it from list?";
    String COMFIRMLOSTACTION = "Are you sure not to send or save the new input content?";
    String SetCatogoryFirst = "Please Set Category First.";
    String SendItemCanNotModify = "Item already send can not be modified. we added a new item into the list.";
    String LeftMoney = "From Last Session";
    String StartTime = "Start Time";
    String EndTime = "End Time";
    String Profit = "当班盈利";


    String BillPageRow = "Rows of Bills:";
    String BillPageCol = "Columns of Bills:";
    String IsSingleUser = "Is single user mode";
    String IsDiscBeforeTax = "Is discount apply on price before tax";
    String StartTimeOfDay = "Start time of a day";
    String PrinterError = "WARNING! Error occured when printing in back end, please check printer and try again.";
    
    String OtherCost = "其他开支";
    String ProdNumber = "Id";
    String ProdName = "Name";
    String Pinyin = "助记";
    String Count = "QTY";
    String Price = "Price";
    String Subtotal = "SubTotal";
    String Total = "Total";
    String QST = "QST";
    String GST = "GST";
    String PricePomp = "PricePomp";
    String MenuPomp = "MenuPomp";
    String ModifyPomp = "ModifyPomp";

    String Customer = "顾客";
    String Package = "单位";
    String Note = "说明";
    String Cost = "进价";

    String OffDuty = "当日结算";
    String Check = "库存信息";
    String Hangup = "挂单";
    String MUser = "修改用户";
    String MProd = "挂单";
    String MRate = "修改汇率";
    String Static = "统计查询";
    String Option = "系统设置";
    String Input = "进货填单";
    String Refund = "REFUND";
    String Type = "类别";
    String Store = "库存";

    String Calculate = "结算";
    String Unit = "元";

    String Discount = "Discount";
    String Receive = "Received";
    String comment = "Comment";
    String OpenTime = "OpenTime";
    
    String MoneyType = "币种";
    String Change = "找零";
    String Continue = "继续收银";

    String AddUser = "ADD CLIENT";
    String PrintAll = "PRINT ALL";

    String EqualBill = "EQUAL BILL";
    String SplitItem = "SPLIT ITEM";
    String MoveItem = "MoveItem";
    String CombineAll = "COMBINE ALL";
	
    String CompleteAll = "COMPLETE ALL";
    String CancelAll = "Cancel All";
    String UserName = "用户名";
    String Password = "密码";
    String Makesure = "确认";
    String MoneyUnit[] = new String[] { "人民币", "欧元", "美元", "港元", "台币", "日元", "韩元", "英镑", "加元" };
    String ProdCodeLength = "ProdCodeLength";
    // for modifyuserDlg---------
    String AddNewUser = "添加新用户(A)";
    String ModiFyUser = "修改用户信息(M)";
    String DeleteUser = "删除用户(D)";

    String[] USERTYPE = { "经理", "职员" };
    String PasswordNotEqual = "两次输入的密码不完全一致，请重新输入。";
    String PasswordMakeSure = "请您将密码在确认框内再输入一遍，系统将比较确认框中的密码和密码框中的密码，确认匹配无勿后，密码方会生效。";
    String WrongFormatMes =
            "系统提醒您，您输入的内容可疑。请确定后检查框内输入的内容是否存在错误，并重新输入。\n按Del键或BackSpace键可清除已经输入的内容；按tab键可以控制光标在各输入框间移动。";
    String More = "更多(E)";
    String Less = "隐藏(E)";

    String AddProd = "添加商品";
    String Shoestring = "shoeString";
    String GoodBye = "您辛苦了，今日的工作成绩已记录入库，下班的路上请注意安全。";
    String MoneyInBox = "钱箱目前金额";

    String EncodeStyle = "编码方式：";
    String Size = "Size";
    String[] Sizes = {"Size1","Size2","Size3","Size4","Size5","Size6"};

    String DspServer = "显示服务器";
    String DspSuperTool = "显示JPOS工具";
    String DspPrintTool = "显示打印测试器";

    String Product = "商品";
    String Cause = "原因";
    String Supplier = "供货商";
    String AddNewSupplier = "添加新供货商...";
    String Focus = "聚焦选中(F)";
    String UnFocus = "取消聚焦(U)";
    String ModifyMerchanInfo = "修改商品信息";

    String BasicData = "资料信息";
    String MerchandiseInfo = "商品资料";
    String CustomerInfo = "客户资料";
    String SupplierInfo = "供货商资料";
    String EmployeeInfo = "员工资料";
    String BasicRecs = "原始记录";
    String SaleRecs = "Bill Record";
    String InputRecs = "进货记录";
    String RefundRecs = "退货记录";
    String Statistic = "统计信息";
    String WorkRecs = "工作记录";

    String ProfitStatChange = "阶段盈利情况统计";

    String Name = "Name";
    String IPAddress = "IPAddress";
    String JobTitle = "职位";
    String NickName = "昵称";
    String Cellphone = "手机";
    String PhoneNum = "宅电";
    String HomeAddress = "家庭住址";
    String MailAddress = "电子邮件地址";
    String Account = "帐务";
    String Categary = "Category";

    String CompName = "单位名称";
    String CompAddress = "单位地址";
    String CompTel = "单位电话";
    String CompFax = "单位传真";

    String QQ = "即时通讯号码";
    String MainPage = "主页";
    String Sex = "性别";
    String JoinTime = "进单位时间";
    String Salary = "工资";
    String INSURANCE = "保险";
    String SSCNUMBER = "社保号码";
    String IDCARD = "身份证";
    String BIRTHDAY = "生日";
    String BANKNUMBER = "银行卡号";

    String TIME = "Create Time";
    String Porfit = "利润";

    String FROM = "FROM";
    String TO = "TO";
    String YEAR = "YEAR";
    String MONTH = "MONTH";
    String WEEK = "WEEK";
    String DAY = "DAY";
    String RefreshChart = "更新图表";

    String MONEYBOX = "钱箱";
    String UniCommand = "通用开钱箱命令";
    String SpecialCommand = "专用命令";
    String OneKeyOpen = "允许空格键开钱箱";
    String PRINTER = "Printer Support";
    String PrintDebugger = "打印样式设置";
    String PrintCommand = "命令";
    String parameters = "参数";
    String Add = "添加(A)";
    String Test = "测试";
    String MsgMissPara = "缺少必需的参数，系统暂以默认值代替。";
    String[] CommandAry = new String[] { "01-打印机初始化", "02-打印水平制表字符", "03-打印并回车", "04-打印并换行", "05",// -打印并走纸n*0.125mm",
            "06",// -打印并进纸n字符行",

            "07",// -设置汉字左右间距",
            "08",// -设置非中文字符的右间距",
            "09",// -设置默认字符行间距",
            "10",// -设置字符行间距为n*0.125mm",

            "11-设置非中文字符打印方式", "12",// -允许/禁止用户自定义非中文字符",
            "13",// -设置用户自定义非中文字符",
            "14",// -取消用户自定义非中文字符",

            "15",// -设置非中文字符下划线",
            "16",// -选择/取消汉字下划线模式",

            "17",// -设置左边距",
            "18",// -设置绝对打印位置",
            "19-设置水平制表符位置", "20",// -设置相对横向打印位置",
            "21",// -设置字符对齐模式",
            "22",// -设置非中文字符加粗",
            "23",// -设置非中文字符双重打印",
            "24",// -允许／禁止按键开关命令",
            "25",// -选择/取消倒置打印模式",
            "26",// -选择切纸方式及切纸送纸",
            "27",// -设置打印区域宽度",
            "28-设置中文字符模式", "29",// -设定点阵图形命令",
            "30",// -用户自定义汉字",
            "31"// -设置/取消汉字四倍模式打印"
    };

    String[] commandTips =
            new String[] {
                    "01\n\n[27][64]   		\n\n    初始化打印机内部数据：清除打印缓冲器；恢复默认值；恢复字符打印方式。",
                    "02\n\n[9] n     		\n\n    打印机依据ESC  D命令设置的水平制表位置，在下一水平制表位置打印制表符n，n为任何一个可以打印的ASCII字符。如果n没有设置，该指令被忽略。如果水平制表位置超过当前打印宽度，则当前位置为下行行首。水平制表位置由ESC D命令设置。",
                    "03\n\n[13]				\n\n    将缓冲器里的内容打印出来，但不走纸。",
                    "04\n\n[10]				\n\n    将行缓冲器里的内容打印出来并向前走纸一行。当行缓冲器空时就只向前走纸一行。",
                    "05\n\n[27][74]n		\n\n    将行缓冲器里的内容打印出来并向前走纸n*0.125mm。该命令只对本行有效，不影响其他命令的设置值。0<=n<=255",
                    "06\n\n[27][100]n		\n\n    打印行缓冲器里的数据并向前走纸n字符行。0<=n<=255",

                    "07\n\n[28][83]n1 n2	\n\n    0<=n1<=255设置汉字左间距，0<=n2<=255设置汉字右间距，默认n1=0，n2=0。",
                    "08\n\n[27][32]n		\n\n    设置非中文字符右边间距为[n*横向移动单位]英寸。0<=n<=255，默认n=0。当字符放大时，间距也随之放大相同的倍数。",
                    "09\n\n[27][50]			\n\n    设置默认的字符行间距。",
                    "10\n\n[27][51]n		\n\n    设置字符行间距为n*0.125mm。0<=n<=255。",

                    "11\n\n[27][33]n		\n\n    设置打印非中文字符的大小和下划线，该设置对汉字无效。",
                    "12\n\n[27][37]n		\n\n    当n=0时，选择内部字符集；n=1时，选择用户自定义字符集。默认n=0",
                    "13\n\n[27][38]  s  n  m  [a,P1,P2…Ps*a]m-n+1	\n\n    该命令用于定义m-n+1个用户自定义字符。s表示字符在垂直方向字节数s=3；n，m表示起始码和终止码，允许定义的字符码自[20H]至[7FH]最多96个，32<=n<=m<=127；a表示自定义字符在水平方向的点数，a=12（12*24点阵）。P是字符点阵数据，总共s*a个数据。",
                    "14\n\n[27][63]n		\n\n    取消用户自定义的某位非中文字符。32<=n<=127。",

                    "15\n\n[27][45]n		\n\n    n=0或48，取消非中文字符下划线；n=1或49，设置非中文字符下划线（1点宽）；n=2或50，设置非中文字符下划线（2点宽）。该指令可以取消由ESC !设置的下划线。",
                    "16\n\n[28][45]n		\n\n    n=0或48，取消汉字下划线；n=1或49，设置汉字下划线（1点宽）；n=2或50，设置汉字下划线（2点宽）。该指令可以取消由FS !设置的下划线。",

                    "17\n\n[29][76]nL nH	\n\n    设置打印内容的左边距为（nL+nH*256）*0.125mm。1<nL<=nH<=255",
                    "18\n\n[27][36]nL  nH	\n\n    设置当前打印位置距离行首（nL+nH*256）*（移动单位）。如果设置位置在打印区域外，该指令被忽略。0<=nL<=255，0<=nH<=255。",
                    "19\n\n[27][68]  [n]k  [0]	\n\n    设置水平制表位置为n1,n2…nk。1<=k<=32，最多可设置32个水平位置制表符。所有水平制表位置都应在打印机允许行宽之内。NUL加在最后，表示该命令结束。ESC  D  NUL命令清除所有水平制表位置，之后再执行HT命令将无效。",
                    "20\n\n[27][92]nL nH	\n\n    将打印位置设置到距当前位置（nL+nH*256）*0.125mm处。0<=nL<=255，0<=nH<=255。",
                    "21\n\n[27][97]n		\n\n    设置打印内容的对齐模式。n=0或48，左对齐方式；n=1或49，中间对齐方式；n=2或50，右对齐方式。",
                    "22\n\n[27][69]n		\n\n    n=0，取消非中文字符加粗；n=1，设置非中文字符加粗。该指令可以取消由ESC !设置的加粗模式。",
                    "23\n\n[27][71]n		\n\n    n=0，取消非中文字符双重打印；n=1，设置非中文字符双重打印。该指令打印效果与加粗打印效果相同。当无法取消由ESC !设置的加粗模式。",
                    "24\n\n[27][99][53]n	\n\n    n=0时，允许“走纸”按键起作用。n=1时，禁止“走纸”按键起作用。默认值为n=0。",
                    "25\n\n[27][123]n		\n\n    当n=0取消倒置打印模式；当n=1选择倒置打印模式。在导致模式下，需要将打印内容从底部往上打印。",
                    "26\n\n[29][86]m  [n]	\n\n    当m=0、1、48、49时，n参数无效，打印机送内容到切刀位置执行直接切纸。当m=66时，打印机送纸n*0.125mm后切纸。0<=n<=255",
                    "27\n\n[29][87]nL nH	\n\n    设置打印内容的宽度为（nL+nH*256）*0.125mm。1<nL<=nH<=255",
                    "28\n\n[28][33]n		\n\n    设置中文字符打印方式，该设置只对汉字有效。",
                    "29\n\n[27][42]m n1 n2 d1…dx	\n\n    该命令用来设置点图方式（m）和点图横向点数（n1，n2）。m=33、0<=n1<=255，0<=n2<=3，0<=d<=255。",
                    "30\n\n[28][50]c1  c2  d1…dk	\n\n    c1、c2表示自定义汉字的汉字编码。c1=[FEH]、[A1H]<=c2<=[FEH]、k=72",
                    "31\n\n[28][87]n		\n\n    n=0，取消汉字四倍模式打印。n=1，设置汉字四倍模式打印。" };
    String SumTotal = "总计:";
    String UseMoenyBox = "使用专用钱箱";
    String UsePrinter = "需要打印票据";
    String PrintTitle = "抬头";
    String Thankword = "谢辞";

    String NoteProdNumber = "请扫描产品条码，或通过直接敲入产品货号+回车键，将顾客选中的商品加入列表";
    String NoteProdNumber1 = "ESC下班 F1盘货 F2进货 F3退货 F4挂单 F5统计查询 F6修改汇率 F7修改用户 F8系统设置";
    String NotePordNumber2 = "通过向下键可选中列表中不同的商品，通过Del键取消列表中选中的商品";
    String NotePordNumber3 = "可通过输入+、-、*号+金额来调整刚刚加入的商品的价格或数量。商品录完后请按回车结帐";
    String NoteProdName = "按向下键查看所有没有条码的商品，按回车将选中项加入下方列表。如果没有找到，请先输入4个*号然后按回车将其入库";
    String NoteCount = "数量必须是一个整数。输入完数量，请再次按Ctrl键，方可录入商品";
    String NoteActiveReceive = "请敲入实际收银的金额后，按回车显示找零。F1欧元 F2美元 F3港元 F4台币 F5日元 F6韩元 F7英镑 F8加元";
    String ValidateFucusAction = "选中项目太少！必须选择多条记录，才能选择[聚焦选中]按钮，对选中的记录进行重点观察。";
}
