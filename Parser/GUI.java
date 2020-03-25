package Parser;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.Box;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 用來繪畫介面並觸發一些按鍵事件。<br>
 * 變數宣告的部分主要為介面的物件宣告。<br>
 * 方法皆為按鍵事件的方法。<br>
 * 因為大部分的變數宣告都是介面的物件宣告，所以這邊就不特別描述各個物件的意義，基本上物件名稱會對應到該物件的內容位置。
 */
public class GUI {

	private JFrame frame;
	ElfParser ELF;
	JTextArea textAreaUP;
	JTextArea textAreaDown;
	JTable table;
	String[][] data = {};
	String[][] ELFHeaderData = {};
	DefaultTableModel update_table;
	DefaultTableModel updateTableRight;
	private JTable tableRight;
	static GUI window;
	Box horizontalBox_3;
	Box horizontalBox_2;
	JScrollPane scrollPaneRight;
	JButton btnClose;
	JButton btnOpen;
	JButton btnAbout; // save
	JButton btnTricky;
	JButton btnELFHeader;
	JButton btnSHeader;
	JButton btnModify;
	JButton btnTag;
	JButton btnFind;
	JButton btnPHeader;
	int pressDis;

	/**
	 * Launch the application.
	 * 
	 * @param args 不用輸入。
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("ELF Parser using Java");
		frame.setBounds(100, 100, 1450, 703);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		Box verticalBox = Box.createVerticalBox();
		verticalBox.setBorder(new TitledBorder(null, "Overview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox.setFont(new Font("Arial", Font.PLAIN, 12));
		verticalBox.setBounds(10, 0, 637, 639);
		frame.getContentPane().add(verticalBox);

		String[] colnumNames = { "Offset", "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C",
				"0D", "0E", "0F" };
		update_table = new DefaultTableModel(data, colnumNames);
		table = new JTable(update_table);
		table.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);

		// table.setBounds(30,40,200,300);
		JScrollPane scrollLeft = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		verticalBox.add(scrollLeft);

		JLabel label_space_2 = new JLabel("  ");
		label_space_2.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		verticalBox.add(label_space_2);

		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);

		Box verticalBox_1 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_1);

		btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ELF = new ElfParser();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				table = OpenFileTable(ELF);
				showELFHeader(ELF);
				frame.setTitle("This function takes me... lots of night to do QQ ");
			}
		});
		btnOpen.setHorizontalAlignment(SwingConstants.LEFT);
		verticalBox_1.add(btnOpen);

		btnAbout = new JButton("Save");
		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tools.saveAsByte(ELF.path + ".modify", ELF);
				textAreaUP.setText("");
				textAreaDown.setText("");
				textAreaUP.setVisible(true);
				textAreaDown.setVisible(true);
				horizontalBox_3.setVisible(true);
				horizontalBox_2.setVisible(false);
				horizontalBox_3.setBorder(
						new TitledBorder(null, "The Save Path", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				scrollPaneRight.setVisible(false);
				textAreaUP.setText("The Saved path is : " + ELF.path + ".modify");
			}
		});
		verticalBox_1.add(btnAbout);

		Box verticalBox_2 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_2);

		btnTricky = new JButton("Disassemble");
		btnTricky.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressDis += 1;
				if (pressDis == 5) {
					pressDis = 0;
					textAreaUP.setVisible(true);
					horizontalBox_3.setVisible(true);
					scrollPaneRight.setVisible(false);
					horizontalBox_3.setBorder(new TitledBorder(null, "Nooooooooooo ><", TitledBorder.LEADING,
							TitledBorder.TOP, null, null));
					horizontalBox_2.setVisible(false);
					textAreaUP.setEnabled(false);
					textAreaUP.setBackground(Color.black);
					// textArea_2.setFont(new Font("Times New Roman", Font.BOLD, 18));
					textAreaUP.setFont(new Font("monospaced", Font.BOLD, 16));
					System.out.println(textAreaUP.getFont());
					System.out.print("            , ,\\ ,\'\\,\'\\ ,\'\\ ,\\ ," + "\n"
							+ "      ,  ,\\/ \\\'  \'         \'  /|" + "\n" + "      |\\/                      |"
							+ "\n" + "      :                        |" + "\n" + "      :                        |"
							+ "\n" + "       |                       |" + "\n" + "       |                       |"
							+ "\n" + "       :               -      _|" + "\n" + "       :                \\       "
							+ "\n" + "       |         ________:______\\" + "\n" + "       :       ,\'o       / o    ,"
							+ "\n" + "       :       \\       ,\'----- /" + "\n" + "        \\_       -- --\'        )"
							+ "\n" + "       ,                  ,---\'|" + "\n" + "       :                       |"
							+ "\n" + "         ,-\'                   |" + "\n" + "        /      ,---           ,\'"
							+ "\n" + "     ,-\'             -,------\'" + "\n" + "               ,--\'" + "\n"
							+ "        - ____/" + "\n" + "              \\");
					textAreaUP.setText("                        ,  ,\\  ,\'\\,\'\\  ,\'\\  ,\\  ," + "\n"
							+ "            ,    ,\\/  \\\'    \'                  \'    /|" + "\n"
							+ "            |\\/                                            |" + "\n"
							+ "            :                                                |" + "\n"
							+ "            :                                                |" + "\n"
							+ "              |                                              |" + "\n"
							+ "              |                                              |" + "\n"
							+ "              :                              -            _|" + "\n"
							+ "              :                                \\              " + "\n"
							+ "              |                  ________:______\\" + "\n"
							+ "              :              ,\'o              /  o        ," + "\n"
							+ "              :              \\              ,\'-----  /" + "\n"
							+ "                \\_              --  --\'                )" + "\n"
							+ "              ,                                    ,---\'|" + "\n"
							+ "              :                                              |" + "\n"
							+ "                  ,-\'                                      |" + "\n"
							+ "                /            ,---                      ,\'" + "\n"
							+ "          ,-\'                          -,------\'" + "\n"
							+ "                              ,--\'" + "\n" + "                -  ____/" + "\n"
							+ "                            \\");
					btnOpen.setEnabled(false);
					btnAbout.setEnabled(false); // save
					btnELFHeader.setEnabled(false);
					btnSHeader.setEnabled(false);
					btnModify.setEnabled(false);
					btnTag.setEnabled(false);
					btnFind.setEnabled(false);
					btnPHeader.setEnabled(false);
					frame.setTitle("This function takes me 5 minute");
				}
				if (pressDis == 1) {
					frame.setTitle("ELF Parser using Java");
					btnOpen.setEnabled(true);
					btnAbout.setEnabled(true); // save
					btnELFHeader.setEnabled(true);
					btnSHeader.setEnabled(true);
					btnModify.setEnabled(true);
					btnTag.setEnabled(true);
					btnFind.setEnabled(true);
					btnPHeader.setEnabled(true);
					textAreaUP.setEnabled(true);
					textAreaUP.setText("");
					textAreaUP.setBackground(Color.white);
					textAreaUP.setFont(new Font("Times New Roman", Font.PLAIN, 18));
					horizontalBox_3.setBorder(
							new TitledBorder(null, "Hello World", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				}
			}
		});
		verticalBox_2.add(btnTricky);

		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setTitle("This button takes me 30 sec");
				System.exit(0);
			}
		});
		verticalBox_2.add(btnClose);

		Box verticalBox_3 = Box.createVerticalBox();
		verticalBox_3.setBorder(new TitledBorder(null, "Content", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox_3.setBounds(657, 0, 767, 645);
		frame.getContentPane().add(verticalBox_3);

		Box horizontalBox_1 = Box.createHorizontalBox();
		verticalBox_3.add(horizontalBox_1);

		String[] HeaderColnumNames = { "", "" };
		updateTableRight = new DefaultTableModel(ELFHeaderData, HeaderColnumNames);

		horizontalBox_3 = Box.createHorizontalBox();
		horizontalBox_3.setBorder(
				new TitledBorder(null, "Find the String input", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox_3.add(horizontalBox_3);
		horizontalBox_3.setVisible(false);

		textAreaUP = new JTextArea();
		textAreaUP.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		horizontalBox_3.add(textAreaUP);
		textAreaUP.setLineWrap(true);
		textAreaUP.setVisible(false);

		JLabel lblNewLabel_2 = new JLabel("  ");
		verticalBox_3.add(lblNewLabel_2);
		tableRight = new JTable(updateTableRight);
		tableRight.setCellSelectionEnabled(true);
		tableRight.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		tableRight.setColumnSelectionAllowed(true);
		scrollPaneRight = new JScrollPane(tableRight, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		verticalBox_3.add(scrollPaneRight);

		ListSelectionModel selectionModel = tableRight.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int col = tableRight.getSelectedColumn();
				int row = tableRight.getSelectedRow();
				System.out.print(col + " " + row);
			}
		});

		horizontalBox_2 = Box.createHorizontalBox();
		horizontalBox_2.setBorder(new TitledBorder(null, "Detail", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox_3.add(horizontalBox_2);
		horizontalBox_2.setVisible(false);

		textAreaDown = new JTextArea();
		textAreaDown.setFont(new Font("Monospaced", Font.PLAIN, 18));
		horizontalBox_2.add(textAreaDown);
		textAreaDown.setVisible(false);

		btnELFHeader = new JButton("ELF Header");
		btnELFHeader.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showELFHeader(ELF);
				horizontalBox_3.setVisible(false);
				horizontalBox_2.setVisible(false);
				scrollPaneRight.setVisible(true);
			}
		});
		horizontalBox_1.add(btnELFHeader);

		btnPHeader = new JButton("Program Headers");
		btnPHeader.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showpHeader(ELF);
				textAreaDown.setVisible(false);
				textAreaUP.setVisible(false);
				horizontalBox_2.setVisible(false);
				scrollPaneRight.setVisible(true);
			}
		});
		horizontalBox_1.add(btnPHeader);

		btnSHeader = new JButton("Section Headers");
		btnSHeader.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showsHeader(ELF);
				horizontalBox_3.setVisible(false);
				horizontalBox_2.setVisible(false);
				scrollPaneRight.setVisible(true);
			}
		});
		horizontalBox_1.add(btnSHeader);

		btnModify = new JButton("Modify");
		btnModify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textAreaUP.setVisible(true);
				textAreaDown.setVisible(true);
				textAreaUP.setEditable(true);
				textAreaDown.setEditable(true);
				String target = textAreaUP.getText();
				String toT = textAreaDown.getText();
				System.out.println(target);
				horizontalBox_3.setVisible(true);
				horizontalBox_2.setVisible(true);
				horizontalBox_3.setBorder(new TitledBorder(null, "Final String input", TitledBorder.LEADING,
						TitledBorder.TOP, null, null));
				scrollPaneRight.setVisible(false);
				if (target.length() != 0 && tools.findString(target, ELF.dataArray) != -1) {
					ELF = modify(target, toT, ELF);
					table = OpenFileTable(ELF);
					fitTableColumns(table);
				} else {
					System.out.println("please input data");
				}
				textAreaUP.setText("");
				textAreaDown.setText("");
			}
		});
		horizontalBox_1.add(btnModify);

		btnTag = new JButton("Tag");
		btnTag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textAreaUP.setVisible(true);
				horizontalBox_3.setVisible(true);
				scrollPaneRight.setVisible(false);
				horizontalBox_3.setBorder(new TitledBorder(null, "input the Tag String", TitledBorder.LEADING,
						TitledBorder.TOP, null, null));
				horizontalBox_2.setVisible(false);
				System.out.println(textAreaUP.getText());
				if (textAreaUP.getText().length() != 0) {
					ELF = tag(textAreaUP.getText(), ELF);
					table = OpenFileTable(ELF);
					fitTableColumns(table);
				}
				textAreaUP.setText("");
				textAreaDown.setText("");
			}
		});
		horizontalBox_1.add(btnTag);

		btnFind = new JButton("Find String");
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textAreaUP.setVisible(true);
				textAreaDown.setVisible(true);
				String target = textAreaUP.getText();
				horizontalBox_3.setVisible(true);
				horizontalBox_2.setVisible(true);
				horizontalBox_3.setBorder(new TitledBorder(null, "Find String Input", TitledBorder.LEADING,
						TitledBorder.TOP, null, null));
				horizontalBox_2.setBorder(
						new TitledBorder(null, "Result", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				textAreaDown.setEditable(false);
				textAreaUP.setEditable(true);
				scrollPaneRight.setVisible(false);
				if (textAreaUP.getText().length() != 0) {
					String temp = findMultiString(target, ELF);
					System.out.println(temp);
					textAreaDown.setText(temp);
				} else {
					textAreaDown.setText("Not Found, Please check the Input again");
				}
				textAreaUP.setText("");
			}
		});
		horizontalBox_1.add(btnFind);

	}

	/**
	 * 實踐{@link #btnOpen}之方法
	 * 
	 * @param 要剖析的ELF物件。
	 * @return 將剖析結果存至JTable並回傳。
	 */
	private JTable OpenFileTable(ElfParser elf) {
		String[] colnumNames = { "Offset", "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C",
				"0D", "0E", "0F" };
		String[] dataString = tools.IntToHex(elf);
		String[][] res = new String[(elf.dataArray.length / 16) + 1][17];

		int index = 0;
		for (int col = 0; col < (elf.dataArray.length / 16) + 1; col++) {
			String head = (Integer.toHexString((index / 16) * 16));
			while (head.length() < 8) {
				head = "0" + head;
			}
			res[col][0] = head;
			for (int i = 1; i < 17; i++) {
				if (index == elf.dataArray.length) {
					break;
				}
				res[col][i] = dataString[index];
				index++;
			}
		}
		data = res;
		update_table.setDataVector(res, colnumNames);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.setDefaultRenderer(Object.class, centerRenderer);
		fitTableColumns(table);
		return table;
	}

	/**
	 * 尚未完成的方法，原本是要用來實踐table內點擊事件。
	 */
	private static void fitTableColumns(JTable myTable) {
		myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JTableHeader header = myTable.getTableHeader();
		int rowCount = myTable.getRowCount();
		Enumeration columns = myTable.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();
			int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
			int width = (int) header.getDefaultRenderer()
					.getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col)
					.getPreferredSize().getWidth();
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) myTable.getCellRenderer(row, col)
						.getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col)
						.getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth);
			}
			header.setResizingColumn(column);
			column.setWidth(width + myTable.getIntercellSpacing().width);
		}
	}

	/**
	 * 實踐{@link #btnELFHeader}之方法。
	 * 
	 * @param 要剖析的ELF物件。
	 * @return 將剖析結果存至JTable並回傳。
	 */
	private JTable showELFHeader(ElfParser elf) {
		String[] HeaderColnumNames = { "", "" };
		String[][] res = new String[19][2];
		res = (String[][]) tools.toArray(elf.ELFHeaderInfo);

		updateTableRight.setDataVector(res, HeaderColnumNames);
		fitTableColumns(tableRight);
		return tableRight;
	}

	/**
	 * 實踐{@link #btnPHeader}的方法
	 * 
	 * @param elf 傳入要剖析的ELF物件。
	 * @return 將剖析結果存至JTable並回傳。
	 */
	private JTable showpHeader(ElfParser elf) {
		HashMap<Integer, String> type = new HashMap<>();
		type.put(0x00000000, "PT_NULL");
		type.put(0x00000001, "PT_LOAD");
		type.put(0x00000002, "PT_DYNAMIC");
		type.put(0x00000003, "PT_INTERP");
		type.put(0x00000004, "PT_NOTE");
		type.put(0x00000005, "PT_SHLIB");
		type.put(0x00000006, "PT_PHDR");
		type.put(0x60000000, "PT_LOOS");
		type.put(0x6FFFFFFF, "PT_HIOS");
		type.put(0x70000000, "PT_LOPROC");
		type.put(0x7FFFFFFF, "PT_HIPROC");

		int size = elf.pHeaderTable.size();
		String[][] data = new String[size][7];
		for (int i = 0; i < size; i++) {
			data[i][0] = type.get(tools.hexToInt(elf.pHeaderTable.get(i).p_type));
			data[i][1] = Integer.toString(tools.hexToInt(elf.pHeaderTable.get(i).p_offset));
			data[i][2] = "0x" + Integer.toHexString(tools.hexToInt(elf.pHeaderTable.get(i).p_vaddr));
			data[i][3] = "0x" + Integer.toHexString(tools.hexToInt(elf.pHeaderTable.get(i).p_paddr));
			data[i][4] = Integer.toString(tools.hexToInt(elf.pHeaderTable.get(i).p_filesz));
			data[i][5] = Integer.toString(tools.hexToInt(elf.pHeaderTable.get(i).p_memsz));
			data[i][6] = Integer.toString(tools.hexToInt(elf.pHeaderTable.get(i).p_flags));
		}
		String[] col = { "Type", "Offset", "Virtual Address", "Physical Address", "File Size", "Memory Size", "Flags" };
		// update the table
		updateTableRight.setDataVector(data, col);
		fitTableColumns(tableRight);
		return tableRight;
	}

	/**
	 * 用來實踐{@link #btnSHeader}的方法
	 * 
	 * @param elf 傳入要print出來的ELF物件。
	 * @return 將該物件的section header存至JTable中回傳。
	 */
	private JTable showsHeader(ElfParser elf) {
		int size = elf.sectionHeaderInfoTable.size();
		String[][] data = new String[size][11];
		String[] col = { "index", "sh_ame", "sh_type", "sh_flags", "sh_addr", "sh_offset", "sh_size", "sh_link",
				"sh_info", "sh_addralign", "sh_entsize" };
		for (int i = 0; i < size; i++) {
			data[i][0] = Integer.toString(i);
			data[i][1] = elf.sectionHeaderInfoTable.get(i).name;
			data[i][2] = elf.sectionHeaderInfoTable.get(i).type;
			data[i][3] = elf.sectionHeaderInfoTable.get(i).flags;
			data[i][4] = elf.sectionHeaderInfoTable.get(i).address;
			data[i][5] = elf.sectionHeaderInfoTable.get(i).offset;
			data[i][6] = elf.sectionHeaderInfoTable.get(i).size;
			data[i][7] = elf.sectionHeaderInfoTable.get(i).link;
			data[i][8] = elf.sectionHeaderInfoTable.get(i).info;
			data[i][9] = elf.sectionHeaderInfoTable.get(i).align;
			data[i][10] = elf.sectionHeaderInfoTable.get(i).entsize;
		}
		// update the table
		updateTableRight.setDataVector(data, col);
		fitTableColumns(tableRight);
		return tableRight;
	}

	/**
	 * 用來實踐{@link #btnModify}之方法。
	 * 
	 * @param target   傳入要改變的字串
	 * @param toTarget 改變後的字串
	 * @param e        針對哪個物件做更改
	 * @return 回傳已經被修改完的ELF物件。
	 */
	private ElfParser modify(String target, String toTarget, ElfParser e) {
		int index = tools.findString(target, e.dataArray);
		if (index == -1) {
			return null;
		}
		int[] Arr = tools.StringToASCII(toTarget);
		int a = 0;
		for (int i = index; i < index + Arr.length; i++) {
			e.dataArray[i] = Arr[a];
			a++;
		}
		return e;
	}

	/**
	 * 用來實踐{@link #btnTag}之方法。
	 * 
	 * @param tag 要添加的tag字串。
	 * @param e   要被添加的ELF物件。
	 * @return 回傳一個被修改過的ELF物件。
	 */
	private ElfParser tag(String tag, ElfParser e) {
		if (tag.length() > 7) {
			return null;
		}
		int[] Arr = tools.StringToASCII(tag);
		int a = 0;
		for (int i = 9; i < 9 + Arr.length; i++) {
			e.dataArray[i] = Arr[a];
			a++;
		}
		return e;
	}

	/**
	 * 用來實踐找多個字串的情境。
	 * 
	 * @param target 要尋找的字串。
	 * @param e      被尋找的ELF物件，主要是用來提取該物件的資料流。
	 * @return 搜尋結果可能不只一個。
	 */
	private String findMultiString(String target, ElfParser e) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		res = tools.findMultiString(target, e.dataArray);
		String resString = "";
		for (int i = 0; i < res.size(); i++) {
			int offset = res.get(i);
			String t = Integer.toHexString(offset / 16) + "0";
			while (t.length() != 8) {
				t = "0" + t;
			}
			resString += "The " + Integer.toString(i) + "th result is " + t + " and 0"
					+ Integer.toHexString(offset % 16) + "\n";
		}
		return resString;

	}

}
