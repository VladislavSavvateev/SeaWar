package ru.kollad.seabattle.server;

import java.io.IOException;
import java.net.Socket;

public class Room extends Thread {

	private Socket socket1, socket2;

	Room(Socket socket) {
		this.socket1 = socket;
	}

	void setOpponent(Socket socket) {
		this.socket2 = socket;
		this.start();
	}

	@Override
	public void run() {
		try {
			byte[] field1 = receiveField(socket1);
			byte[] field2 = receiveField(socket2);

			socket1.getOutputStream().write(34);
			socket1.getOutputStream().flush();

			socket2.getOutputStream().write(69);
			socket2.getOutputStream().flush();

			boolean whatever = false;
			while (!Thread.interrupted()) {
				if (whatever) turn(socket1, socket2, field2);
				else turn(socket2, socket1, field1);
				whatever = !whatever;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			socket1.close();
			socket2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] receiveField(Socket socket) throws IOException {
		byte[] bytes = new byte[8];
		if (socket.getInputStream().read(bytes) != bytes.length)
			throw new IOException();

		return bytes;
	}

	private void turn(Socket sender, Socket receiver, byte[] field) throws Exception {
		int x = sender.getInputStream().read();
		int y = sender.getInputStream().read();
		byte row = field[y];
		byte target = pow(x);

		boolean done = true;
		if ((row & target) == target) { // Hope for the best
			field[y] = (byte) (row & ~target);
			for (byte b : field) {
				if (b != 0) {
					done = false;
					break;
				}
			}

			sender.getOutputStream().write(done ? 34 : 69);
		} else {
			sender.getOutputStream().write(47);
		}

		sender.getOutputStream().flush();

		if (done) {
			receiver.getOutputStream().write(69);
			receiver.getOutputStream().flush();
			throw new Exception("Game finished!");
		} else {
			receiver.getOutputStream().write(x);
			receiver.getOutputStream().write(y);
			receiver.getOutputStream().flush();
		}
	}

	private byte pow(int power) {
		byte result = 1;
		for (int i = 0; i < power; i++)
			result *= 2;
		return result;
	}
}
