package uk.ac.cam.jk510.part2project.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;

public class TCPConnection extends DeviceConnection {

	TCPConnection(Device device) throws UnknownHostException, IOException {
		super();
		if(device == null) {
			socket = new Socket(Config.getServerIP(), Config.getServerPort());
		} else {
			socket = new Socket(((DeviceHandleIP) device.getHandle()).getIP(), Config.getClientTCPPort());
		}
		is = socket.getInputStream();
		os = socket.getOutputStream();
		os.write(Session.getSession().getThisDevice().getDeviceID());
		System.out.println("connected to device "+device.getDeviceID());
	}

	public TCPConnection(Socket sock, InputStream is2, OutputStream os2) {
		super();
		socket = sock;
		is = is2;
		os = os2;
	}

	private Socket socket;
	private InputStream is;
	private OutputStream os;

	public ByteBuffer receiveEncryptedData(byte[] data) throws IOException {
		//TODO

		return null;
	}
	@Override
	public ByteBuffer abstractReceiveData(byte[] data) throws IOException {

		/*
		 * Want to:
		 * Receive quantised message into array.
		 * Construct bytebuffer for it and set its limit
		 */

		int length = is.read();
		System.out.println("Received message of size: "+length);
		int offset = 0;
		while(offset<length) {
			int numRead = is.read(data, offset, length - offset);
			offset += numRead;
		}
		ByteBuffer bb = ByteBuffer.wrap(data, 0, length);
		return bb;
	}

	protected void sendEncrypted(byte[] data, int length) throws IOException {
		//TODO
	}
	@Override
	protected void send(byte[] data, int length) throws IOException {
		os.write(length);
		os.write(data, 0, length);
		os.flush();
	}

	public static void getConnectable(final DeviceConnection[] connections) throws UnknownHostException, IOException {
		int myID = Session.getSession().getThisDevice().getDeviceID();
		ServerSocket serv = new ServerSocket(Config.getClientTCPPort());
		for(int device=0; device<Session.getSession().numDevices(); device++) {
			if(device == myID) {continue;}
			if(device<myID) {
				final Socket sock = serv.accept();
				System.out.println("accepted device: "+device);
				new Thread(new Runnable() {

					private void connect() throws IOException {
						InputStream is = sock.getInputStream();
						OutputStream os = sock.getOutputStream();
						int device = is.read();
						connections[device] = new TCPConnection(sock, is, os);
					}

					public void run() {
						try {
							connect();
						} catch (IOException e) {
							// TODO add limit, may result in infnite recursion
							run();
						}
					}

				}).start();

			} else {
				connections[device] = new TCPConnection(Session.getDevice(device));
			}
			System.out.println("opened all sockets");
		}
	}

}
