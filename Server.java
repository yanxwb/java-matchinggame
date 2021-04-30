import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class Server {
	private String dir = "OnlineData/";
	private ServerSocket ss;
	private Socket s;
	private DataInputStream fi;
	private BufferedOutputStream fo;

	public Server() {

	}

	public void init() {
		try {
			ss = new ServerSocket(8848);
			while (true) {
				s = ss.accept();
				System.out.println("Client:\t" + s.getInetAddress().getLocalHost() + "\tConnected.");
				fi = new DataInputStream(s.getInputStream());

				String name = fi.readUTF();
				long length = fi.readLong();
				save(name, length);
				System.out.println("客户端：" + name);
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

	public void save(String filename, long length) throws IOException {
		// 实际上这个length 参数并没有被用上...
		try {
			File f = new File(this.dir + filename);
			fo = new BufferedOutputStream(new FileOutputStream(f));
			byte[] bytes = new byte[1024];
			while ((fi.read(bytes, 0, bytes.length)) != -1) {
				fo.write(bytes);
				fo.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fo != null)
				fo.close();
			if (fi != null)
				fi.close();
			s.close();
		}

	}
}