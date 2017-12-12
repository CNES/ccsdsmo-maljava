/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2017 CNES
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
  *******************************************************************************/
package fr.cnes.ccsds.mo.transport.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

/**
 * This class handles a TCP connection.
 * It implements the low level of MALTCP transport protocol.
 */
public class TCPConnectionHandler {
	protected final Socket socket;
	protected final DataOutputStream dos;
	protected final DataInputStream dis;

	private String remoteBaseURI = null;

	/**
	 * Constructor.
	 *
	 * @param socket
	 *            the TCPIP socket.
	 * @throws IOException
	 *             if there is an error.
	 */
	public TCPConnectionHandler(Socket socket, String remoteBaseURI) throws IOException {
		this.socket = socket;
		this.remoteBaseURI = remoteBaseURI;
		
		dos = new DataOutputStream(socket.getOutputStream());
		dis = new DataInputStream(socket.getInputStream());

		TCPTransport.RLOGGER.log(Level.WARNING, "Creates TCPConnectionHandler: " + this);
	}

	static final int HEADER_FIXED_SIZE = 23;
	static final int MESSAGE_LENGTH_OFFSET = 19;
	// TODO (AF): To remove (framing)
	static final int MESSAGE_LENGTH_OFFSET_TEST = 0;

	void MalBinaryWrite32(byte[] data, int offset, int value) {
		data[offset + 0] = (byte) (value >> 24);
		data[offset + 1] = (byte) (value >> 16);
		data[offset + 2] = (byte) (value >> 8);
		data[offset + 3] = (byte) (value >> 0);
	}

	int MalBinaryRead32(byte[] data, int offset) {
		return (((int) (data[0 + offset] & 255) << 24)
				+ ((int) (data[1 + offset] & 255) << 16)
				+ ((int) (data[2 + offset] & 255) << 8) + ((int) (data[3 + offset] & 255) << 0));
	}

	public void sendEncodedMessage(byte[] packet) throws IOException {
		// TODO (AF): for Debug
		StringBuffer strbuf = new StringBuffer();
		for (int i = 0; i <packet.length; i++)
			strbuf.append(packet[i] & 0xFF).append(' ');
		TCPTransport.RLOGGER.log(Level.WARNING,
				"sendEncodedMessage: buf=" + strbuf.toString());
		
		MalBinaryWrite32(packet, MESSAGE_LENGTH_OFFSET, packet.length - HEADER_FIXED_SIZE);
		
		synchronized (dos) {
			// TODO (AF): To remove (framing)
//			// write packet length
//			dos.writeInt(packet.length);
			// Writes the packet
			dos.write(packet);
			dos.flush();
		}
	}

	public byte[] readEncodedMessage() throws IOException {
		try {
			synchronized (dis) {
				// read the header
				// TODO (AF): To remove (framing)
//				byte[] header = new byte[HEADER_FIXED_SIZE +4];
				byte[] header = new byte[HEADER_FIXED_SIZE];
				dis.readFully(header);
				// get the packet size then read the remaining bytes
				
				// TODO (AF): To remove (framing)
//				int length1 = MalBinaryRead32(header, MESSAGE_LENGTH_OFFSET +4);
				int length = MalBinaryRead32(header, MESSAGE_LENGTH_OFFSET);
				// TODO (AF): To remove (framing)
//				int length = MalBinaryRead32(header, MESSAGE_LENGTH_OFFSET_TEST);
//				if (length != length1) {
//					TCPTransport.RLOGGER.log(Level.SEVERE,
//							"Bad encoded length: " + length + " != " + length1);
//				}
				byte[] data = new byte[HEADER_FIXED_SIZE+length];
				// TODO (AF): To remove (framing)
//				System.arraycopy(header, 4, data, 0, HEADER_FIXED_SIZE);
//				dis.readFully(data, HEADER_FIXED_SIZE, length - HEADER_FIXED_SIZE);
				System.arraycopy(header, 0, data, 0, HEADER_FIXED_SIZE);
				dis.readFully(data, HEADER_FIXED_SIZE, length);
				
				// TODO (AF): for Debug
				StringBuffer strbuf = new StringBuffer();
				for (int i = 0; i <data.length; i++)
					strbuf.append(data[i] & 0xFF).append(' ');
				TCPTransport.RLOGGER.log(Level.WARNING,
						"readEncodedMessage: buf=" + strbuf.toString());

				return data;
			}
		} catch (java.net.SocketException exc) {
			if (socket.isClosed()) {
				// socket has been closed to throw EOF exception higher
				throw new java.io.EOFException();
			}
			throw exc;
		}
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// nothing to do
		}
	}
	
	public String getRemoteBaseURI() {
		return remoteBaseURI;
	}
}
