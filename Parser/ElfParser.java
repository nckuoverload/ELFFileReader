package Parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
/**
 * 此方法同時也是一個ELF物件<br>
 * 將.elf檔案以byte的型別讀取，因為java的byte僅有signed byte，會發生溢位。<br>
 * 所以讀取進來後的會再針對溢位處理後以int[]的形式儲存該資料流。<br>
 *這份物件會將elf剖析後根據wiki得到的變數以1. 資料流 2. 剖析後的意義 儲存<br>
 *@author KappaTseng
 *@version 1.0 
 * */
public class ElfParser {

	public static String path;

	private static final int EI_NIDENT = 16;
	/* declare the basic word type and its size */

	/**
	 * 32-bit中一個Addrsize的長度
	 * 
	 * @since 1.0
	 */
	private static final int Elf32_Addr = 4;
	/**
	 * 32-bit中一個offset的長度
	 * 
	 * @since 1.0
	 */
	private static final int Elf32_Off = 4;
	/**
	 * 64-bit中一個word的長度
	 * 
	 * @since 1.0
	 */
	private static final int Elf32_Word = 4;

	/**
	 * 用來計算.elf的長度
	 * 
	 * @since 1.0
	 */
	private static final int unsigned_char = 1;

	/**
	 * 64-bit中一個Addrsize的長度
	 * 
	 * @since 1.0
	 */
	private static final int Elf64_Addr = 8;
	/**
	 * 64-bit中四分之一個Addrsize的長度
	 * 
	 * @since 1.0
	 */
	private static final int Elf64_Half = 2;
	/**
	 * 64-bit中一個word的長度
	 * 
	 * @since 1.0
	 */
	private static final int Elf64_Word = 4;

	/**
	 * 該檔案是否為.elf檔案
	 * 
	 * @since 1.0
	 */
	boolean isELF = false;

	/**
	 * 判斷此.elf檔案為64-bit或32-bit之CPU架構。 <br>
	 * true 為64-bit <br>
	 * false 為32-bit
	 * 
	 * @since 1.0
	 */
	boolean elfClass;
	/**
	 * 輸入.elf 檔案，但會以byte形式 讀入，之後再以int型別儲存進陣列<br>
	 * **因為java並沒有支援無號位元(unsigned byte)，當讀入的byte超過127時，會發生溢位，故先以byte讀入後使用int儲存之。
	 * 
	 * @since 1.0
	 */
	int[] dataArray;

	/**
	 * 用來儲存目前Addrsize大小。
	 * 
	 * @since 1.0
	 */
	public static int AddrSize;

	/**
	 * 描述此.elf檔案的編碼格式。
	 */
	String elfEncode;
	/**
	 * 描述此.elf檔案適用的作業系統。
	 */
	String elfOS;
	/**
	 * 描述此.elf檔案的型別為何種。
	 */
	String elfType;
	/**
	 * 描述產生此.elf的CPU指令集。
	 */
	String elfMachine;
	/**
	 * 描述此.elf檔案的版本。
	 */
	String elfVersion;
	/**
	 * 描述此.elf檔案的Entry Point。
	 */
	String elfEntryPoint;
	/**
	 * 描述此.elf檔案的program header offset。
	 */
	String PHOffset;
	/**
	 * 描述此.elf檔案的section header offset。
	 */
	String SHOffset;
	/**
	 * 描述此.elf檔案的flags。
	 */
	String elfFlags;
	/**
	 * 描述此.elf檔案的Header大小。
	 */
	String HeaderSize;
	/**
	 * 描述此.elf檔案的Program header之Entry大小。
	 */
	String PHEntrySize;
	/**
	 * 描述此.elf檔案的program header 數目。
	 */
	String pHnums;
	/**
	 * 描述此.elf檔案的section header 數目。
	 */
	String sHnums;
	/**
	 * 描述此.elf檔案的section header Entry之大小。
	 */
	String SHEntrySize;

	/**
	 * 用來確認.elf檔案的magic number。
	 */
	public static final int ELFMAG0 = 0x7f;
	/**
	 * 用來確認.elf檔案的magic number。
	 */
	public static final int ELFMAG1 = 'E';
	/**
	 * 用來確認.elf檔案的magic number。
	 */
	public static final int ELFMAG2 = 'L';
	/**
	 * 用來確認.elf檔案的magic number。
	 */
	public static final int ELFMAG3 = 'F';

	/**
	 * 用來儲存elf標頭檔中的e_ident。
	 */
	int[] e_ident = new int[ElfParser.unsigned_char * ElfParser.EI_NIDENT];
	/**
	 * 用來儲存elf標頭檔中的e_type。
	 */
	int[] e_type = new int[ElfParser.Elf64_Half];
	/**
	 * 用來儲存elf標頭檔中的e_machine位元。
	 */
	int[] e_machine = new int[ElfParser.Elf64_Half];
	/**
	 * 用來儲存elf標頭檔中的e_version位元。
	 */
	int[] e_version = new int[ElfParser.Elf64_Word];
	/**
	 * 用來儲存elf標頭檔中的e_32entry位元。
	 */
	int[] e_32entry = new int[ElfParser.Elf32_Addr];
	/**
	 * 用來儲存elf標頭檔中的e_64entry位元。
	 */
	int[] e_64entry = new int[ElfParser.Elf64_Addr];
	/**
	 * 用來儲存elf標頭檔中的e_64phoff位元。
	 */
	int[] e_64phoff = new int[ElfParser.Elf64_Addr];
	/**
	 * 用來儲存elf標頭檔中的e_64shoff位元。
	 */
	int[] e_64shoff = new int[ElfParser.Elf64_Addr];
	/**
	 * 用來儲存elf標頭檔中的e_32phoff位元。
	 */
	int[] e_32phoff = new int[ElfParser.Elf32_Addr];
	/**
	 * 用來儲存elf標頭檔中的e_32shoff位元。
	 */
	int[] e_32shoff = new int[ElfParser.Elf32_Addr];
	/**
	 * 用來儲存elf標頭檔中的e_flags位元。
	 */
	int[] e_flags = new int[ElfParser.Elf64_Word];
	/**
	 * 用來儲存elf標頭檔中的e_ehsize位元。
	 */
	int[] e_ehsize = new int[ElfParser.Elf64_Half];
	/**
	 * 用來儲存elf標頭檔中的e_phentsize位元。
	 */
	int[] e_phentsize = new int[ElfParser.Elf64_Half];
	/**
	 * 用來儲存elf標頭檔中的e_phnum位元。
	 */
	int[] e_phnum = new int[ElfParser.Elf64_Half];
	/**
	 * 用來儲存elf標頭檔中的e_shentsize位元。
	 */
	int[] e_shentsize = new int[ElfParser.Elf64_Half];
	/**
	 * 用來儲存elf標頭檔中的e_shnum位元。
	 */
	int[] e_shnum = new int[ElfParser.Elf64_Half];
	/**
	 * 用來儲存elf標頭檔中的e_shstrndx位元。
	 */
	int[] e_shstrndx = new int[ElfParser.Elf64_Half];

	/**
	 * 該ArrayList儲存所有program header的資料流。<br>
	 * 使用自定義的inner class "pHeader"來當作該ArrayList的內容型別。<br>
	 * 對於該型別詳細的說明可以參照{@link Parser.ElfParser.pHeader}。
	 * 
	 * @since 1.0
	 */
	ArrayList<pHeader> pHeaderTable = new ArrayList<pHeader>();
	/**
	 * 該ArrayList儲存所有section header的資料流。<br>
	 * 使用自定義的inner class "secHeader"來當作該ArrayList的內容型別。<br>
	 * 對於該型別詳細的說明可以參照{@link Parser.ElfParser.secHeader}。<br>
	 * 該變數內容和pHeaderTable相似。
	 * 
	 * @since 1.0
	 */
	ArrayList<secHeader> secHeaderTable = new ArrayList<secHeader>();
	/**
	 * 該ArrayList儲存所有section header的資訊。<br>
	 * 使用自定義的inner class "secHeaderInfo"來當作該ArrayList的內容型別。<br>
	 * 對於該型別詳細的說明可以參照{@link Parser.ElfParser.secHeaderInfo}。<br>
	 * 
	 * @since 1.0
	 */
	ArrayList<secHeaderInfo> sectionHeaderInfoTable = new ArrayList<secHeaderInfo>();
	/**
	 * 該HashMap用來儲存ELF Header的資訊。<br>
	 * 第一個String為ELF Header 中哪個部分，可以為type、flags等。<br>
	 * 第二個String為ELF Header 中對應上述部分的內容值。<br>
	 * 
	 * @since 1.0
	 */
	HashMap<String, String> ELFHeaderInfo = new HashMap<>();

	/**
	 * 是ElfParser的main方法。<br>
	 * 1. 先使用{@link FileSelector#select()}取得對應的檔案 <br>
	 * 2. 再使用{@link tools#readFile(File)}
	 * 將檔案讀進，並且處理完溢位後以Integer陣列型別存進{@link #dataArray}。<br>
	 * 3. 使用{@link #getELFHeader(int[])}取得ELF Header之資訊。<br>
	 * 4. 使用{@link #getELFProgramHeaderTable()} 取得ELF Program Header
	 * Table中的所有內容。<br>
	 * 5. 使用{@link #getELFSectionHeaderTable()} 取得 ELF Section Header
	 * Table中的所有內容<br>
	 * 6. 使用{@link #processSectionHeaderInfo()} 將ELF Section剖析完並且得到每個Section的資訊。
	 * <br>
	 * 
	 * @throws IOException 如果檔案之I/O有問題會丟出該IOException
	 */
	ElfParser() throws IOException {
		FileSelector fs = new FileSelector();
		path = fs.select().getAbsolutePath(); // the path of file
		// path = "E:\\Course\\2019_RTES\\output32";
		File tempFile = new File(path);
		dataArray = tools.readFile(tempFile);
		getELFHeader(dataArray);
		getELFProgramHeaderTable();
		getELFSectionHeaderTable();
		processSectionHeaderInfo();
	}

	/**
	 * debug工具。
	 */
	public void printAllElfInformation() {
		System.out.println("Welcome to ELF Parser");
		System.out.println("------------------------------------");
		System.out.println("the class is:" + elfClass);
		System.out.println("the data is:" + elfEncode);
		System.out.println("the Version is:" + elfVersion);
		System.out.println("the OS is:" + elfOS);
		System.out.println("the Type is:" + elfType);
		System.out.println("the Machine is:" + elfMachine);

		System.out.println("there is n program Header : " + pHeaderTable.size());
		System.out.println("there is n section Header : " + secHeaderTable.size());
		for (int i = 0; i < secHeaderTable.size(); i++) {
			System.out.println(sectionHeaderInfoTable.get(i).name);
		}

	}

	/**
	 * 針對ELF Header做處理。<br>
	 * 先將檔案的資料流讀進來後，針對每個offset和對應的內容作儲存。<br>
	 * 之後再針對這份Header作標籤化，將type等內容貼上對應的標籤。<br>
	 * 
	 * @param inputStream 參數為檔案的資料流。
	 * @return 輸出為是否成功執行該method。
	 */
	boolean getELFHeader(int[] inputStream) {
		int index = 0;
		e_ident = Arrays.copyOfRange(inputStream, 0, 16);
		e_type = Arrays.copyOfRange(inputStream, 16, 18);
		e_machine = Arrays.copyOfRange(inputStream, 18, 20);
		e_version = Arrays.copyOfRange(inputStream, 20, 24);

		if (e_ident[4] == 2) {
			elfClass = true;
			AddrSize = 8;
			e_64entry = Arrays.copyOfRange(inputStream, 24, 32);
			e_64phoff = Arrays.copyOfRange(inputStream, 32, 40);
			e_64shoff = Arrays.copyOfRange(inputStream, 40, 48);
			elfEntryPoint = "0x" + Integer.toHexString(tools.hexToInt(e_64entry));
			PHOffset = Integer.toString(tools.hexToInt(e_64phoff));
			SHOffset = Integer.toString(tools.hexToInt(e_64shoff));
			index = 48;
		} else if (e_ident[4] == 1) {
			elfClass = false;
			AddrSize = 4;
			e_32entry = Arrays.copyOfRange(inputStream, 24, 28);
			e_32phoff = Arrays.copyOfRange(inputStream, 28, 32);
			e_32shoff = Arrays.copyOfRange(inputStream, 32, 36);
			index = 36;
			elfEntryPoint = "0x" + Integer.toHexString(tools.hexToInt(e_32entry));
			PHOffset = Integer.toString(tools.hexToInt(e_32phoff));
			SHOffset = Integer.toString(tools.hexToInt(e_32shoff));
		}

		e_flags = Arrays.copyOfRange(inputStream, index, index + 4);
		e_ehsize = Arrays.copyOfRange(inputStream, index + 4, index + 6);
		e_phentsize = Arrays.copyOfRange(inputStream, index + 6, index + 8);
		e_phnum = Arrays.copyOfRange(inputStream, index + 8, index + 10);
		e_shentsize = Arrays.copyOfRange(inputStream, index + 10, index + 12);
		e_shnum = Arrays.copyOfRange(inputStream, index + 12, index + 14);
		e_shstrndx = Arrays.copyOfRange(inputStream, index + 14, index + 16);

		// check is .elf
		if (e_ident[0] != ELFMAG0 && e_ident[1] != ELFMAG1 && e_ident[2] != ELFMAG2 && e_ident[3] != ELFMAG3) {
			isELF = false;
			return false;
		} else {
			isELF = true;
		}

		// little endian or big endian
		switch (e_ident[5]) {
		case 2:
			elfEncode = "Big Endian";
			break;
		case 1:
			elfEncode = "Little Endian";
			break;
		case 0:
			elfEncode = "";
			isELF = false;
		}

		elfVersion = Integer.toString(e_ident[6]);

		// which OS:
		HashMap<Integer, String> OSHash = new HashMap<>();
		OSHash.put(0x00, "System V");
		OSHash.put(0x01, "HP-UX");
		OSHash.put(0x02, "NetBSD");
		OSHash.put(0x03, "Linux");
		OSHash.put(0x04, "GNU Hurd");
		OSHash.put(0x06, "Solaris");
		OSHash.put(0x07, "AIX");
		OSHash.put(0x08, "IRIX");
		OSHash.put(0x09, "FreeBSD");
		OSHash.put(0x0A, "Tru64");
		OSHash.put(0x0B, "Novell Modesto");
		OSHash.put(0x0C, "OpenBSD");
		OSHash.put(0x0D, "OpenVMS");
		OSHash.put(0x0E, "NonStop Kernel");
		OSHash.put(0x0F, "AROS");
		OSHash.put(0x10, "Fenix OS");
		OSHash.put(0x11, "CloudABI");
		elfOS = OSHash.get(e_ident[7]);

		// e_type
		HashMap<Integer, String> typeHash = new HashMap<>();
		typeHash.put(0, "None");
		typeHash.put(1, "REL");
		typeHash.put(2, "EXEC");
		typeHash.put(3, "DYN");
		typeHash.put(4, "CORE");
		typeHash.put(254, "LOOS");
		typeHash.put(509, "HIOS");
		typeHash.put(255, "LOPROC");
		typeHash.put(510, "HIPROC");
		elfType = typeHash.get(e_type[0] + e_type[1]);

		// which machine
		HashMap<Integer, String> machineHash = new HashMap<>();
		machineHash.put(0, "None");
		machineHash.put(3, "x86");
		machineHash.put(8, "MIPS");
		machineHash.put(40, "ARM");
		machineHash.put(62, "x86-64");
		elfMachine = machineHash.get(e_machine[0]);

		elfFlags = Integer.toString(tools.hexToInt(e_flags));
		HeaderSize = Integer.toString(tools.hexToInt(e_ehsize));
		PHEntrySize = Integer.toString(tools.hexToInt(e_phentsize));
		SHEntrySize = Integer.toString(tools.hexToInt(e_shentsize));
		pHnums = Integer.toString(tools.hexToInt(e_phnum));
		sHnums = Integer.toString(tools.hexToInt(e_shnum));

		ELFHeaderInfo.put("File Address", path);
		if (elfClass) {
			ELFHeaderInfo.put("Class", "64-bit");
		} else {
			ELFHeaderInfo.put("Class", "32-bit");
		}
		ELFHeaderInfo.put("Enconding", elfEncode);
		ELFHeaderInfo.put("ELF Version", elfVersion);
		ELFHeaderInfo.put("OS ABI", elfOS);
		ELFHeaderInfo.put("ABI Version", Integer.toString(e_ident[7]));
		ELFHeaderInfo.put("ELF Type", elfType);
		ELFHeaderInfo.put("Machine", elfMachine);
		ELFHeaderInfo.put("Entry Point", elfEntryPoint);
		ELFHeaderInfo.put("Program Header Offset", PHOffset);
		ELFHeaderInfo.put("Section Header Offset", SHOffset);
		ELFHeaderInfo.put("Flags", elfFlags);
		ELFHeaderInfo.put("ELF Header Size", HeaderSize);
		ELFHeaderInfo.put("Program Header Size", PHEntrySize);
		ELFHeaderInfo.put("Program Header Entries", pHnums);
		ELFHeaderInfo.put("Section Header Size", SHEntrySize);
		ELFHeaderInfo.put("Section Header Entries", sHnums);

		return true;
	}

	/**
	 * 尚未實踐之方法。
	 */
	class ELF64bit {

	}

	/**
	 * 尚未實踐之方法。
	 */
	class ELF32bit {

	}

	/**
	 * 該class為一program header類別，在此程式中，每一個program header皆為一個物件。<br>
	 * 每一個program header都會有一樣的內容如p_type、p_flags...等。<br>
	 * 所以這邊使用pHeader定義一個父類別，宣告會使用到的變數有哪些<br>
	 * 但有些變數會隨著32-bit或64-bit改變，這些變數不會在父類別宣告長度<br>
	 * 會在{@link Parser.ElfParser.p64Header}和{@link Parser.ElfParser.p32Header}的建構子去定義這些變動變數的長度。<br>
	 * 並且使用 {@link Parser.ElfParser.pHeader#getELFProgramHeader(int[], int)}
	 * 方法來提取每一個program header。<br>
	 * 因為不論是64-bit或32-bit 處理program header的方式皆相同，所以這個方法僅需在父類別定義並實作即可。
	 */
	class pHeader {
		/**
		 * program header之p_type資料流。
		 */
		int[] p_type = new int[4];
		/**
		 * program header之p_flags資料流。
		 */
		int[] p_flags = new int[4];
		/**
		 * program header之p_offset資料流。<br>
		 * 因在64-bit或32-bit中的長度不同，故不在此宣告長度，僅宣告該變數。
		 */
		int[] p_offset;
		/**
		 * program header之p_vaddr資料流。<br>
		 * 因在64-bit或32-bit中的長度不同，故不在此宣告長度，僅宣告該變數。
		 */
		int[] p_vaddr;
		/**
		 * program header之p_paddr資料流。<br>
		 * 因在64-bit或32-bit中的長度不同，故不在此宣告長度，僅宣告該變數。
		 */
		int[] p_paddr;
		/**
		 * program header之p_filesz資料流。<br>
		 * 因在64-bit或32-bit中的長度不同，故不在此宣告長度，僅宣告該變數。
		 * 
		 */
		int[] p_filesz;
		/**
		 * program header之p_memsz資料流。<br>
		 * 因在64-bit或32-bit中的長度不同，故不在此宣告長度，僅宣告該變數。
		 * 
		 */
		int[] p_memsz;
		/**
		 * program header之p_align資料流。<br>
		 * 因在64-bit或32-bit中的長度不同，故不在此宣告長度，僅宣告該變數。
		 * 
		 */
		int[] p_align;
		/**
		 * program header在整份資料流中的offset。<br>
		 */
		int id;

		/**
		 * pHeader的建構子
		 */
		public pHeader() {
			// getELFProgramHeader(dataArray, id);
		}

		/**
		 * 從資料流{@link Parser.ElfParser#dataArray}中提取program header table的部分，<br>
		 * 並且逐一做處理。
		 * 
		 * @param dataArray 需要處理的檔案的資料流。
		 * @param id        需要處理的檔案的programHeader的offset
		 * @return 此方法是否成功。
		 */
		boolean getELFProgramHeader(int[] dataArray, int id) {
			p_type = Arrays.copyOfRange(dataArray, id, id + 4);
			id += 4;
			if (elfClass) {
				p_flags = Arrays.copyOfRange(dataArray, id, id + 4);
				id += 4;
			}
			p_offset = Arrays.copyOfRange(dataArray, id, id + AddrSize);
			id += AddrSize;
			p_vaddr = Arrays.copyOfRange(dataArray, id, id + AddrSize);
			id += AddrSize;
			p_paddr = Arrays.copyOfRange(dataArray, id, id + AddrSize);
			id += AddrSize;
			p_filesz = Arrays.copyOfRange(dataArray, id, id + AddrSize);
			id += AddrSize;
			p_memsz = Arrays.copyOfRange(dataArray, id, id + AddrSize);
			id += AddrSize;
			if (!elfClass) {
				p_flags = Arrays.copyOfRange(dataArray, id, id + 4);
				id += 4;
			}
			p_align = Arrays.copyOfRange(dataArray, id, id + 4);
			id += 4;
			return true;
		}

	}

	/**
	 * 繼承自{@link Parser.ElfParser.pHeader}<br>
	 * 因部分變數長度與32-bit不同<br>
	 * 故另外用子類別繼承父類別的形式補足宣告<br>
	 * 只在建構子中宣告，因方法{@link #getELFProgramHeader(int[], int)}並沒有做更動的部分。
	 **/
	class p64Header extends pHeader {
		/**
		 * 建構子，用來補宣告64-bit中一些變數的長度。
		 */
		public p64Header() {
			super.p_offset = new int[ElfParser.Elf64_Addr];
			super.p_vaddr = new int[ElfParser.Elf64_Addr];
			super.p_paddr = new int[ElfParser.Elf64_Addr];
			super.p_filesz = new int[ElfParser.Elf64_Addr];
			super.p_memsz = new int[ElfParser.Elf64_Addr];
			super.p_align = new int[ElfParser.Elf64_Addr];
		}
	}

	/**
	 * 繼承自{@link Parser.ElfParser.pHeader}<br>
	 * 因部分變數長度與64-bit不同<br>
	 * 故另外用子類別繼承父類別的形式補足宣告<br>
	 * 只在建構子中宣告，因方法{@link #getELFProgramHeader(int[], int)}並沒有做更動的部分。
	 **/
	class p32Header extends pHeader {
		/**
		 * 建構子，用來補宣告64-bit中一些變數的長度。
		 */
		public p32Header() {
			super.p_offset = new int[ElfParser.Elf32_Addr];
			super.p_vaddr = new int[ElfParser.Elf32_Addr];
			super.p_paddr = new int[ElfParser.Elf32_Addr];
			super.p_filesz = new int[ElfParser.Elf32_Addr];
			super.p_memsz = new int[ElfParser.Elf32_Addr];
			super.p_flags = new int[ElfParser.Elf32_Addr];
			super.p_align = new int[ElfParser.Elf32_Addr];
		}

	}

	/**
	 * 主要用來將所有的program header提取後放進 {@link Parser.ElfParser#pHeaderTable}。<br>
	 * 1. 從 {@link Parser.ElfParser#e_phnum}中得知共有幾個program header。<br>
	 * 2. 從{@link Parser.ElfParser#e_phentsize}中得知每個program header的大小。<br>
	 * 3. 從{@link Parser.ElfParser#elfClass}得知為64或32位元之系統，再得到offset。<br>
	 * 4. 搭配for-loop，將每一個program header都提出來並存進 {@link Parser.ElfParser#pHeaderTable}
	 * 
	 * @return 此方法是否執行成功。
	 */
	boolean getELFProgramHeaderTable() {
		int pHeaderNum = tools.hexToInt(e_phnum);
		int startAddress;
		int size = tools.hexToInt(e_phentsize);
		if (elfClass) {
			startAddress = tools.hexToInt(e_64phoff);
		} else {
			startAddress = tools.hexToInt(e_32phoff);
		}
		for (int i = 0; i < pHeaderNum; i++) {
			pHeader ph;
			if (elfClass) {
				ph = new p64Header();
			} else {
				ph = new p32Header();
			}
			ph.getELFProgramHeader(dataArray, startAddress);
			startAddress += (size);
			pHeaderTable.add(ph);
		}
		return true;
	}

	/**
	 * 做法和{@link Parser.ElfParser.pHeader}相似<br>
	 * 因section header在32和64位元中的長度不同，並且為了提高程式碼的可讀性<br>
	 * 將不同長度的變數用子類別的建構子宣告，父類別僅宣告相同長度的變數<br>
	 * 搭配 {@link #getSectionHeader(int[], int)}從資料流中得到section header table並做剖析。
	 */
	class secHeader {
		/**
		 * 從資料流中提取sh_name的部分。
		 */
		int[] sh_name = new int[4];
		/**
		 * 從資料流中提取sh_type的部分。
		 */
		int[] sh_type = new int[4];
		/**
		 * 從資料流中提取sh_link的部分。
		 */
		int[] sh_link = new int[4];
		/**
		 * 從資料流中提取sh_info的部分。
		 */
		int[] sh_info = new int[4];
		/**
		 * 從資料流中提取section的offset。
		 * */
		int id;
		
		/**
		 * 預計存放從資料流中提取sh_flags的部分。<br>
		 * 因在32和64位元中長度不同，故不在此宣告長度。
		 * */
		int[] sh_flags;
		/**
		 * 預計存放從資料流中提取sh_addr的部分。<br>
		 * 因在32和64位元中長度不同，故不在此宣告長度。
		 * */
		int[] sh_addr;
		/**
		 * 預計存放從資料流中提取sh_offset的部分。<br>
		 * 因在32和64位元中長度不同，故不在此宣告長度。
		 * */
		int[] sh_offset;
		/**
		 * 預計存放從資料流中提取sh_size的部分。<br>
		 * 因在32和64位元中長度不同，故不在此宣告長度。
		 * */
		int[] sh_size;
		/**
		 * 預計存放從資料流中提取sh_addralign的部分。<br>
		 * 因在32和64位元中長度不同，故不在此宣告長度。
		 * */
		int[] sh_addralign;
		/**
		 * 預計存放從資料流中提取sh_entsize的部分。<br>
		 * 因在32和64位元中長度不同，故不在此宣告長度。
		 * */
		int[] sh_entsize;
/**
 * secHeader之建構子
 * */
		public secHeader() {
		}

		/**
		 * 從資料流{@link Parser.ElfParser#dataArray}中提取section header的部分。<br>
		 * 提取方法為針對從資料流中針對section header的起點為始<br>
		 * 針對每個變數進行offset提取<br>
		 * 32和64位元不同長度的部分會在{@link Parser.ElfParser#AddrSize}中定義。
		 * @param rawArr 需要處理的資料流{@link Parser.ElfParser#dataArray}
		 * @param id section header之offset
		 * @return 此方法是否執行成功。
		 * */
		boolean getSectionHeader(int[] rawArr, int id) {
			sh_name = Arrays.copyOfRange(rawArr, id, id + 4);
			id += 4;
			sh_type = Arrays.copyOfRange(rawArr, id, id + 4);
			id += 4;
			sh_flags = Arrays.copyOfRange(rawArr, id, id + AddrSize);
			id += AddrSize;
			sh_addr = Arrays.copyOfRange(rawArr, id, id + AddrSize);
			id += AddrSize;
			sh_offset = Arrays.copyOfRange(rawArr, id, id + AddrSize);
			id += AddrSize;
			sh_size = Arrays.copyOfRange(rawArr, id, id + AddrSize);
			id += AddrSize;
			sh_link = Arrays.copyOfRange(rawArr, id, id + 4);
			id += 4;
			sh_info = Arrays.copyOfRange(rawArr, id, id + 4);
			id += 4;
			sh_addralign = Arrays.copyOfRange(rawArr, id, id + AddrSize);
			id += AddrSize;
			sh_entsize = Arrays.copyOfRange(rawArr, id, id + AddrSize);
			id += AddrSize;
			return true;
		}
	}
	
	/**
	 * 繼承自{@link Parser.ElfParser.secHeader}<br>
	 * 因為部分變數長度在32和64位元中不同<br>
	 * 故在此類別中的建構子宣告該類變數的長度。
	 * */
	class sec32Header extends secHeader {
		/**
		 * 建構子。
		 * 變數繼承自{@link Parser.ElfParser.secHeader}<br>
		 * 並且在此建構子中宣告長度。
		 * */
		public sec32Header() {
			super.sh_flags = new int[ElfParser.Elf32_Word];
			super.sh_addr = new int[ElfParser.Elf32_Addr];
			super.sh_offset = new int[ElfParser.Elf32_Off];
			super.sh_size = new int[ElfParser.Elf32_Word];
			super.sh_addralign = new int[ElfParser.Elf32_Word];
			super.sh_entsize = new int[ElfParser.Elf32_Word];
		}
	}
	/**
	 * 繼承自{@link Parser.ElfParser.secHeader}<br>
	 * 因為部分變數長度在32和64位元中不同<br>
	 * 故在此類別中的建構子宣告該類變數的長度。
	 * */
	class sec64Header extends secHeader {
		/**
		 * 建構子。
		 * 變數繼承自{@link Parser.ElfParser.secHeader}<br>
		 * 並且在此建構子中宣告長度。
		 * */
		public sec64Header() {
			super.sh_flags = new int[ElfParser.Elf64_Addr];
			super.sh_addr = new int[ElfParser.Elf64_Addr];
			super.sh_offset = new int[ElfParser.Elf64_Addr];
			super.sh_size = new int[ElfParser.Elf64_Addr];
			super.sh_addralign = new int[ElfParser.Elf64_Addr];
			super.sh_entsize = new int[ElfParser.Elf64_Addr];
		}
	}

	/**
	 * 得到所有的Section Header
	 * 
	 * @return 回傳一個布林值，顯示該方法是否有成功完成。
	 */
	boolean getELFSectionHeaderTable() {
		int sHeaderNum = tools.hexToInt(e_shnum);
		int entry;
		int size;
		if (elfClass) {
			entry = tools.hexToInt(e_64shoff);
			size = 64;
		} else {
			entry = tools.hexToInt(e_32shoff);
			size = 40;
		}
		for (int i = 0; i < sHeaderNum; i++) {
			secHeader temp;
			if (elfClass) {
				temp = new sec64Header();
				entry += size;
			} else {
				temp = new sec32Header();
				entry += size;
			}
			temp.getSectionHeader(dataArray, entry - size);
			secHeaderTable.add(temp);
		}

		return true;
	}

	
	/**
	 * 因section name的提取方法為<br>
	 * 1. 提取{@link Parser.ElfParser.secHeader#sh_name}，因此變數僅代表該section的名稱在{@link Parser.ElfParser#e_shstrndx}的偏移量。<br>
	 * 2. 透過此偏移量從{@link Parser.ElfParser#e_shstrndx}中提取對應的資料並轉為ascii碼
	 * 
	 * @param offset section header中{@link Parser.ElfParser.secHeader#sh_name}
	 * @return 該偏移量所代表的section name
	 * */
	String getSectionName(int offset) {
		int indexOfSectionName = (int) tools.hexToInt(e_shstrndx);
		secHeader SectionNameHeader = secHeaderTable.get(indexOfSectionName);
		int start = tools.hexToInt(SectionNameHeader.sh_offset);
		int size = tools.hexToInt(SectionNameHeader.sh_size);
		start += offset;
		String ans = "";
		for (int i = (start); i < (start + size); i++) {
			if (dataArray[i] != 0) {
				ans += (char) dataArray[i];
			} else {
				break;
			}
		}
		return ans;
	}

	/**
	 * 用來提取每一個 {@link Parser.ElfParser.secHeader}中的資訊<br>
	 * 方法和{@link #getELFProgramHeaderTable()}相似<br>
	 * 將{@link Parser.ElfParser.secHeader}的資訊提取出來後存自 {@link #ELFHeaderInfo}
	 * @return 此方法是否成功執行。
	 * */
	boolean processSectionHeaderInfo() {
		HashMap<Integer, String> typeHash = new HashMap<>();
		typeHash.put(0, "SHT_NULL");
		typeHash.put(1, "SHT_PROGBITS");
		typeHash.put(2, "SHT_SYMTAB");
		typeHash.put(3, "SHT_STRTAB");
		typeHash.put(4, "SHT_RELA");
		typeHash.put(5, "SHT_HASH");
		typeHash.put(6, "SHT_DYNAMIC");
		typeHash.put(7, "SHT_NOTE");
		typeHash.put(8, "SHT_NOBITS");
		typeHash.put(9, "SHT_REL");
		typeHash.put(10, "SHT_SHLIB");
		typeHash.put(11, "SHT_DYNSYM");
		typeHash.put(14, "SHT_INIT_ARRAY");
		typeHash.put(15, "SHT_FINI_ARRAY");
		typeHash.put(16, "SHT_PREINIT_ARRAY");
		typeHash.put(17, "SHT_GROUP");
		typeHash.put(18, "SHT_SYMTAB_SHNDX");
		typeHash.put(19, "SHT_NUM");

		HashMap<Integer, String> flagHash = new HashMap<>();
		flagHash.put(1, "SHF_WRITE");
		flagHash.put(2, "SHF_ALLOC");
		flagHash.put(4, "SHF_EXECINSTR");
		flagHash.put(10, "SHF_MERGE");
		flagHash.put(32, "SHF_STRINGS");
		flagHash.put(64, "SHF_INFO_LINK");
		flagHash.put(128, "SHF_LINK_ORDER");
		flagHash.put(256, "SHF_OS_NONCONFORMING");
		flagHash.put(512, "SHF_GROUP");
		flagHash.put(1024, "SHF_TLS");
		flagHash.put(267386880, "SHF_MASKOS");
		// flagHash.put(4026531840,"SHF_MASKPROC");
		flagHash.put(67108864, "SHF_ORDERED");
		flagHash.put(134217728, "SHF_EXCLUDE");

		for (int i = 0; i < tools.hexToInt(e_shnum); i++) {
			secHeaderInfo p = new secHeaderInfo();
			int offset = (int) tools.hexToInt(secHeaderTable.get(i).sh_name);
			p.name = getSectionName(offset);
			p.type = typeHash.get(tools.hexToInt(secHeaderTable.get(i).sh_type));
			p.flags = flagHash.get(tools.hexToInt(secHeaderTable.get(i).sh_flags));
			p.address = tools.hexToInt(secHeaderTable.get(i).sh_addr) + "";
			p.offset = tools.hexToInt(secHeaderTable.get(i).sh_offset) + "";
			p.size = tools.hexToInt(secHeaderTable.get(i).sh_size) + "";
			p.link = tools.hexToInt(secHeaderTable.get(i).sh_link) + "";
			p.info = tools.hexToInt(secHeaderTable.get(i).sh_info) + "";
			p.entsize = tools.hexToInt(secHeaderTable.get(i).sh_entsize) + "";
			sectionHeaderInfoTable.add(p);
		}
		return true;
	}

	
	/**
	 * 用來儲存{@link Parser.ElfParser.secHeader}之資訊，應該可以和{@link Parser.ElfParser.secHeader}合併。
	 * */
	class secHeaderInfo {
		/**
		 * 該section header之name。
		 * */
		String name;
		/**
		 * 該section header之type。
		 * */
		String type;
		/**
		 * 該section header之address。
		 * */
		String address;
		/**
		 * 該section header之offset。
		 * */
		String offset;
		/**
		 * 該section header之size。
		 * */
		String size;
		/**
		 * 該section header之entsize。
		 * */
		String entsize;
		/**
		 * 該section header之flags。
		 * */
		String flags;
		/**
		 * 該section header之link。
		 * */
		String link;
		/**
		 * 該section header之info。
		 * */
		String info;
		/**
		 * 該section header之align。
		 * */
		String align;
	}

}
