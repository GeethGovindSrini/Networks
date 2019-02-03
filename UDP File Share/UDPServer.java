//server code
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.security.MessageDigest;
public class UDPServer {

	String checkSumValue;
	DatagramSocket serverSocket;
	DatagramPacket receivePacket, sendpacket;
	private OutputStream ouStream = null;

	public void communicate() {
		try {
			String path = "test.txt";
			FileReader fr = new FileReader("server.properties");
			Properties prop = new Properties();
			prop.load(fr);
			int port = Integer.parseInt(prop.getProperty("port"));
			serverSocket = new DatagramSocket(port);
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			InetAddress IPAddress = receivePacket.getAddress();
			System.out.println("Waiting for datagram packet. . .\n");
			checkSumValue = new String(doMD5(path));
			String msg = new String(receivePacket.getData());
			System.out.println("Checksum of Client: " + msg);
			if (checkSumValue.equalsIgnoreCase(msg)) {
				System.out.println("The file didn't update");
				String reply = "y";
				//                String r=new String(reply.);
				//                ouStream.write(reply);
				sendData = reply.getBytes();
				sendpacket = new DatagramPacket(sendData, sendData.length);
				serverSocket.send(sendpacket);
				System.out.println("Reply :" + new String(sendData));
			} else {
				String reply = "n";
				//                ouStream.write(reply);
				sendData = reply.getBytes();
				System.out.println("Reply :" + new String(sendData));
				sendpacket = new DatagramPacket(sendData, sendData.length);
				serverSocket.send(sendpacket);
				System.out.println("The file updated");
				fileTransfer(path);
			}
		} catch (Exception ex) {
			System.out.println("Error msg: " + ex.getMessage());
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
			System.out.println("Error msg: " + ex);
		}
		return checkSumValue;
	}

	public void fileTransfer(String path) {
		try {
			FileWriter fw = new FileWriter(new File("text1.txt"));
			//            fw.write("hi");
			//            DatagramSocket Socket = new DatagramSocket(port);
			byte[] receiveData = new byte[1000000];
			while (receiveData != null) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());
				fw.write(sentence.trim());
				fw.flush();
				System.out.printf("RECEIVED: %s ", new String(receivePacket.getData()));
			}
			fw.flush();
			fw.close();
			serverSocket.close();
		}catch (Exception e) {
			System.err.println(e);
		}
	}
	public static void main(String[] args){
		UDPServer server= new UDPServer();
		server.communicate();
	}
}
