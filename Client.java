import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

public class Client {
	private Socket s;
	public String dir = "./OnlineData/";
	private DataInputStream ctrli;
	private DataOutputStream ctrlo;

	private BufferedInputStream sendi;
	private DataOutputStream sendo;
	private DataInputStream geti;
	private BufferedOutputStream geto;

	public Client() {
		try {
			s = new Socket("127.0.0.1", 8848);
			ctrlo = new DataOutputStream(s.getOutputStream());
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
		try {
			sendo = new DataOutputStream(s.getOutputStream());
			// 向服务器端发送一条消息
			File f = new File(filename);
			if (!f.exists()) {
				System.out.println("文件不存在！");
				return;
			}
			// 发送标记信息
			ctrlo.writeBoolean(true);
			// 发送文件信息
			ctrlo.writeUTF(f.getName());
			ctrlo.flush();
			ctrlo.writeLong(f.length());
			ctrlo.flush();

			System.out.println("---Start sending " + filename + ".---");
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
			System.out.println("---" + filename + "\tsent successfully.---");

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
		try {
			ctrlo.writeBoolean(false);
			ctrlo.writeUTF(filename);
			File f = new File(this.dir + filename);
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
}