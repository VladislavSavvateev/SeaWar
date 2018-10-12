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
				System.out.print("Fool connected: ");
				System.out.println(socket);
				switch (socket.getInputStream().read()) {
					case 34:
						random.nextBytes(buffer);

						rooms.put(buffer[0] + 1000 * buffer[1], new Room(socket));
						socket.getOutputStream().write(buffer);
						socket.getOutputStream().flush();

						System.out.println("Fool created new room: " + buffer[0] + ' ' + buffer[1]);
						break;
					case 69:
						int i = socket.getInputStream().read(buffer);
						if (i != 2) {
							socket.getOutputStream().write(47);
							socket.close();
							break;
						}

						Room room = rooms.remove(buffer[0] + 1000 * buffer[1]);
						if (room == null) {
							socket.getOutputStream().write(47);
							socket.close();
							break;
						}

						socket.getOutputStream().write(69);
						socket.getOutputStream().flush();

						room.setOpponent(socket);

						System.out.println("Foold joined a room");

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
