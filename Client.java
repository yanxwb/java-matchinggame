import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

public class Client {
	private Socket s;
	private InputStream is;
	private OutputStream os;
	private DataOutputStream fo;
	private BufferedInputStream fi;

	public Client() {
		try {
			s = new Socket("127.0.0.1", 8848);
			is = s.getInputStream();
			os = s.getOutputStream();
			fo = new DataOutputStream(os);
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException!");
			// e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException!");
			// e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// Client c = new Client();
		// c.saveAll();
		// c.saveFile("README.md");

	}

	public void saveFile(String filename) throws IOException {
		// 用于读取指定文件并上传
		try {
			// 向服务器端发送一条消息
			File f = new File(filename);
			if (!f.exists()) {
				System.out.println("文件不存在！");
				return;
			}
			// 发送文件信息
			fo.writeUTF(f.getName());
			fo.flush();
			fo.writeLong(f.length());
			fo.flush();

			System.out.println("---Start sending " + filename + ".---");
			// 读取本地文件
			fi = new BufferedInputStream(new FileInputStream(f));
			byte b[] = new byte[1024];
			int length = 0;
			long progress = 0;
			while ((length = fi.read(b)) != -1) {
				fo.write(b);
				fo.flush();
				progress += length;
				System.out.println("" + (int) (100 * progress / f.length()) + "%");
			}
			System.out.println("---" + filename + "\tsent successfully.---");

		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException!");
			// e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException!");
			// e.printStackTrace();
		} finally {
			if (fi != null)
				fi.close();
			if (fo != null)
				fo.close();
		}
	}
}