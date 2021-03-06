import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
	private Socket s;
	public String dir = "./data/";
	public String name = "temp";
	public String sendname = "toBeSent";
	private DataInputStream ctrli;
	private DataOutputStream ctrlo;

	private BufferedInputStream sendi;
	private DataOutputStream sendo;
	private DataInputStream geti;
	private BufferedOutputStream geto;
	private SimpleDateFormat sdf;

	public Client() {
		try {
			s = new Socket("127.0.0.1", 8848);
			ctrlo = new DataOutputStream(s.getOutputStream());
			ctrli = new DataInputStream(s.getInputStream());

			sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyy_MM_dd-HH_mm_ss");
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException!");
			// e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException!");
			// e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

	}

	public void sendFile(String filename) throws IOException {
		// 用于读取指定文件并上传
		// 函数接受参数为文件名，不包含路径。
		try {
			Date date = new Date();
			String targetName = sdf.format(date);
			sendo = new DataOutputStream(s.getOutputStream());
			// 向服务器端发送一条消息
			File f = new File(filename);
			if (!f.exists()) {
				System.out.println("文件不存在！");
				return;
			}
			// 发送标记信息
			ctrlo.writeInt(1);
			// 发送文件信息
			ctrlo.writeUTF(targetName);
			ctrlo.flush();
			ctrlo.writeLong(f.length());
			ctrlo.flush();

			System.out.println("---Start sending " + targetName + ".---");
			// 读取本地文件
			sendi = new BufferedInputStream(new FileInputStream(f));
			byte b[] = new byte[1024];
			int length = 0;
			long progress = 0;
			while ((length = sendi.read(b)) != -1) {
				sendo.write(b);
				sendo.flush();
				progress += length;
				System.out.println("" + (int) (100 * progress / f.length()) + "%");
			}
			System.out.println("---" + targetName + "\tsent successfully.---");

		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException!");
			// e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException!");
			// e.printStackTrace();
		} finally {
			if (sendi != null)
				sendi.close();
			if (sendo != null)
				sendo.close();
			s.close();
		}
	}

	public void getFile(String filename) throws IOException {
		// 下载指定文件并保存至本地
		// 函数接受参数为文件名，不包含路径。
		try {
			ctrlo.writeInt(2);
			ctrlo.writeUTF(filename);
			File f = new File(this.dir + this.name);
			geto = new BufferedOutputStream(new FileOutputStream(f));
			geti = new DataInputStream(s.getInputStream());
			byte[] bytes = new byte[1024];
			while ((geti.read(bytes, 0, bytes.length)) != -1) {
				geto.write(bytes);
				geto.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (geti != null)
				geti.close();
			if (geto != null)
				geto.close();
			s.close();
		}
	}

	public String[] getList() throws IOException {
		try {
			ctrlo.writeInt(0);
			Integer n = ctrli.readInt();
			String[] str = new String[n];
			for (int i = 0; i < n; i++) {
				String temp = ctrli.readUTF();
				str[i] = temp;
			}
			return str;
		} catch (IOException e) {
			System.out.println("读取列表失败！！！");
			return null;
		} finally {
			s.close();
		}
	}
}