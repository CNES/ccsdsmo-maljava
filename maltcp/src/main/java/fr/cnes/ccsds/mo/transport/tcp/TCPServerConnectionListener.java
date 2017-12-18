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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import static fr.cnes.ccsds.mo.transport.tcp.TCPTransport.RLOGGER;

import java.util.ArrayList;
import java.util.List;

/**
 * Server Thread for the MALTCP transport.
 *
 * This thread listens for new connections to a predefined port and it creates a
 * new handler when new connections arrive.
 */
public class TCPServerConnectionListener extends Thread {
	private final TCPTransport transport;
	private final ServerSocket serverSocket;

	/**
	 * Holds the list of data poller threads
	 */
	private final List<Thread> pollerThreads = new ArrayList<Thread>();

	/**
	 * Constructor.
	 *
	 * @param transport		The MALTCP transport.
	 * @param serverSocket	The listening TCP/IP socket.
	 */
	public TCPServerConnectionListener(TCPTransport transport, ServerSocket serverSocket) {
		this.transport = transport;
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
		try {
			serverSocket.setSoTimeout(1000);
		} catch (IOException e) {
			RLOGGER.log(Level.WARNING, "Error while setting connection timeout", e);
		}

		// setup socket and then listen for connections forever
		while (!interrupted()) {
			try {
				// Wait for connection, then create a connection handler
				Socket socket = serverSocket.accept();
				TCPConnectionHandler handler = transport.createConnectionHandler(socket);
				
				// Handle socket in separate thread
				TCPMessagePoller poller = new TCPMessagePoller(transport, handler);
				// TODO (AF): May be we have to fix remoteURI
//				poller.setRemoteURI(remoteRootURI);
				pollerThreads.add(poller);
				poller.start();
			} catch (java.net.SocketTimeoutException ex) {
				// this is ok, we just loop back around
			} catch (IOException e) {
				RLOGGER.log(Level.WARNING, "Error while accepting connection", e);
			}
		}

		RLOGGER.info("TCPServerConnectionListener stopping");
		
		// Cleaning
		for (Thread pollerThread : pollerThreads) {
			synchronized (pollerThread) {
				pollerThread.interrupt();
			}
		}
		pollerThreads.clear();
		
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				RLOGGER.log(Level.WARNING, "Error during termination", e);
			}
		}
		
		RLOGGER.info("TCPServerConnectionListener stopped");
	}
}
