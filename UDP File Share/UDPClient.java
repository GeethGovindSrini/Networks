//client code
import java.io.*;
import java.net.*;
import java.util.*;
import java.security.MessageDigest;
public class UDPClient {
	DatagramSocket clientSocket;
	DatagramPacket sendPacket, receivePacket;
	private InputStream inStream = null;
	String checkSumValue;
	public void communicate() {
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		String path = "test.txt", checkSumValue;
		try {
			FileReader fr = new FileReader("client.properties");
			Properties prop = new Properties();
			prop.load(fr);
			String serverHostname = prop.getProperty("ipAddress");
			int port = Integer.parseInt(prop.getProperty("port"));
			System.out.println("Host: " + serverHostname);
			System.out.println("Port: " + port);
			//            BufferedReader inFromUser =
			//                    new BufferedReader(new InputStreamReader(System.in));
			clientSocket = new DatagramSocket();
			InetAddress ipAddress = InetAddress.getByName(serverHostname);
			System.out.println("Attempting to connect to " + ipAddress + " via UDP " + port);
			checkSumValue = doMD5(path);
			sendData = checkSumValue.getBytes();
			sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
			clientSocket.send(sendPacket);
			System.out.println("Client Checksum sent to server : " + new String(sendPacket.getData()));
			//            int reply;
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String res =new String(receivePacket.getData());
			//            reply=receivePacket.getData();
			//             reply=Integer.parseInt(res);
			System.out.println("Reply: "+ res);

			if (res.equalsIgnoreCase("n")) {
			fileTransfer(path);
			clientSocket.close();
			}
			else{
				Thread.sleep(2000);
				clientSocket.close();
			}
		} catch (Exception ex) {
		System.out.println("Exception :" + ex.getMessage());
		}
	}
	public String doMD5(String path) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(path);

			byte[] dataBytes = new byte[1024];

			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			};
			byte[] mdbytes = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			checkSumValue = sb.toString();
			System.out.println("Digest(in hex format):: " + checkSumValue);
		} catch (Exception ex) {
			System.out.println("Exception : " + ex.getMessage());
		}
		return checkSumValue;
	}
	public void fileTransfer(String path){
		try {
			FileInputStream fstream = new FileInputStream("text1.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			File file = new File("text1.txt");
			FileInputStream fis = new FileInputStream(file);
			byte[] fsize = new byte[(int) file.length()];
			int size = fis.read(fsize);
			System.out.println("Size = " + size);
			InetAddress addr = InetAddress.getByName("localhost");
			byte[] buf = new byte[10000];
			String DataLine;
			while ((DataLine = br.readLine()) != null) {
				//DatagramPacket packet = new DatagramPacket(DataLine.getBytes(), DataLine.length(), addr, 4555);
				receivePacket=new DatagramPacket(DataLine.getBytes(), DataLine.length());
				System.out.println(DataLine);
				DatagramSocket socket = new DatagramSocket();
				socket.send(receivePacket);
				System.out.println("Sent Packet: "+new String(receivePacket.getData()));
			}
		} catch (Exception ex) {
			System.out.println("Exception in file: " + ex.getMessage());
		}
	}
	public static void main(String[] args){
		UDPClient client= new UDPClient();
		client.communicate();
	}
}
