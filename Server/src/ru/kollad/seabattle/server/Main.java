package ru.kollad.seabattle.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {

	public static void main(String[] args) throws IOException {
		byte[] buffer = new byte[2];
		Map<Integer, Room> rooms = new HashMap<>();
		Random random = new Random(System.currentTimeMillis());
		ServerSocket serverSocket = new ServerSocket(1337, 256);
		while (!Thread.interrupted()) {
			try {
				Socket socket = serverSocket.accept();
				switch (socket.getInputStream().read()) {
					case 34:
						random.nextBytes(buffer);
						rooms.put(ByteBuffer.wrap(buffer).getInt(), new Room(socket));
						socket.getOutputStream().write(buffer);
						break;
					case 69:
						int i = socket.getInputStream().read(buffer);
						if (i != 2) {
							socket.getOutputStream().write(47);
							socket.close();
							break;
						}

						Room room = rooms.remove(ByteBuffer.wrap(buffer).getInt());
						if (room == null) {
							socket.getOutputStream().write(47);
							socket.close();
							break;
						}

						room.setOpponent(socket);
						break;
				}
			} catch (SocketTimeoutException ignored) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		serverSocket.close();
	}
}
