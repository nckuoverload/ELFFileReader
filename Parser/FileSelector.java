package Parser;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
/**
 * 主要是使用java的方法來呈現選擇檔案的介面。<br>
 * 大二的時候寫的，因為很好用就當作工具包一直保存到現在。
 * */
class FileSelector extends JFrame {
	public static void main(String[] args) {
		FileSelector fs = new FileSelector();
		ArrayList<File> file = fs.Mselect();
		for (int i = 0; i < file.size(); i++) {
			System.out.println("file selected (fully qualified name): " + file.get(i).getAbsolutePath());
			System.out.println("file selected (file name only): " + file.get(i).getName());
		}
	}
/**
 * 單一個檔案選擇器。
 * @return 回傳一個File類別。
 * */
	File select() {
		JFileChooser fileChooser = new JFileChooser();
		File selectedFile = null; // change to arraylist
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			String dir = fileChooser.getCurrentDirectory().getName();
			fileChooser.setCurrentDirectory(new File(dir));
		}
		return selectedFile;
	}

	/**
	 * 多個檔案選擇器。
	 * @return 將多個檔案存進ArrayList中回傳。
	 * */
	ArrayList<File> Mselect() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		ArrayList<File> selectedFile = new ArrayList<File>();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File[] sss = fileChooser.getSelectedFiles();
			for (int i = 0; i < sss.length; i++) {
				selectedFile.add(sss[i]);
			}
			String dir = fileChooser.getCurrentDirectory().getName();
			fileChooser.setCurrentDirectory(new File(dir));
		}
		return selectedFile;
	}
}