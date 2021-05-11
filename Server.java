import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

public class Server {
	private String dir = "../Data/";
	private ServerSocket ss;
	private Socket s;
	private DataInputStream ctrli;
	private DataOutputStream ctrlo;

	private DataInputStream geti;
	private BufferedOutputStream geto;
	private BufferedInputStream sendi;
	private DataOutputStream sendo;

	public Server() {

	}

	public void init() {
		try {
			ss = new ServerSocket(8848);
			while (true) {
				s = ss.accept();
				System.out.println("Client:\t" + s.getInetAddress().getLocalHost() + "\tConnected.");
				ctrli = new DataInputStream(s.getInputStream());
				boolean flag = ctrli.readBoolean();
				// 若为1则接受，若为零则发送
				if (flag) {
					String name = ctrli.readUTF();
					long length = ctrli.readLong();
					System.out.println("从客户端接受文件：" + name);
					get(name, length);
				} else {
					String name = ctrli.readUTF();
					System.out.println("向客户端发送文件：" + name);
					send(name);
				}
			}

		} catch (Exception e) {
			System.out.println("服务器异常！");
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Initialing Server....");
		Server server = new Server();
		server.init();

	}

	public void get(String filename, long length) throws IOException {
		// 实际上这个length 参数并没有被用上...
		try {
			geti = new DataInputStream(s.getInputStream());
			File f = new File(this.dir + filename);
			geto = new BufferedOutputStream(new FileOutputStream(f));
			byte[] bytes = new byte[1024];
			while ((geti.read(bytes, 0, bytes.length)) != -1) {
				geto.write(bytes);
				geto.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (geto != null)
				geto.close();
			if (geti != null)
				geti.close();
			s.close();
		}

	}

	public void send(String filename) throws IOException {
		try {
			sendo = new DataOutputStream(s.getOutputStream());
			File f = new File(this.dir + filename);
			if (!f.exists()) {
				System.out.println("文件不存在！");
				return;
			}
			sendi = new BufferedInputStream(new FileInputStream(f));
			byte b[] = new byte[1024];
			while (sendi.read(b) != -1) {
				sendo.write(b);
				sendo.flush();
			}
			System.out.println("已发送" + filename);
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
}