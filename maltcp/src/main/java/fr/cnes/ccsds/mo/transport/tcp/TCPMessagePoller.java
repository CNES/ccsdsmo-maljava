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
import java.io.EOFException;
import java.util.logging.Level;

/**
 * This thread receives messages from TCP connection and forwards
 * them to related transport.
 */
public class TCPMessagePoller extends Thread {
	/**
	 * Reference to the transport
	 */
	protected final TCPTransport transport;
	/**
	 * the low level message sender and receiver
	 */
	protected final TCPConnectionHandler handler;
	/**
	 * the remote URI (client) this connection is associated to. This is
	 * volatile as it is potentially set by a different thread after its
	 * creation
	 */
	private volatile String remoteURI = null;

	/**
	 * Constructor.
	 *
	 * @param transport	Message transport being used.
	 * @param messageSender
	 *            The message sending interface associated to this connection.
	 * @param messageReceiver
	 *            The message reception interface, used for pulling messaging
	 *            into this transport.
	 * @param decoderFactory
	 *            The decoder factory to create message decoders from.
	 */
	public TCPMessagePoller(TCPTransport transport, TCPConnectionHandler handler) {
		TCPTransport.RLOGGER.info("Creates TCPMessagePoller " + this + " - " + handler);
		this.transport = transport;
		this.handler = handler;
		setName(getClass().getName());
	}

	@Override
	public void run() {
		boolean bContinue = true;

		// handles message reads from this client
		while (bContinue && !interrupted()) {
			try {
				// Get a message from the receiver and passes it to the transport.
				TCPTransport.RLOGGER.log(Level.FINE, "Client @" + this.getId() + " wait message: {0}", remoteURI);

				byte[] msg = handler.readEncodedMessage();
				TCPTransport.RLOGGER.log(Level.FINE, "Client @" + this.getId() + " readEncodedMessage: {0}", msg.length);

				if (null != msg) {
					// msg should never be null
					transport.receiveMessage(this, msg);
				}

				TCPTransport.RLOGGER.log(Level.FINE, "Client @" + this.getId() + " receive message: {0}", remoteURI);
			} catch (EOFException ex) {
				TCPTransport.RLOGGER.log(Level.INFO, "Client @" + this.getId() + " closing connection: {0}", remoteURI);

				transport.closeConnection(remoteURI, this);
				close();

				// and terminate
				bContinue = false;
			} catch (IOException e) {
				TCPTransport.RLOGGER.log(Level.WARNING, "Cannot read message from client @" + this.getId(), e);

				transport.communicationError(remoteURI, this);
				close();

				// and terminate
				bContinue = false;
			}
		}
	}

	public String getRemoteURI() {
		return remoteURI;
	}

	public void setRemoteURI(String remoteURI) {
		this.remoteURI = remoteURI;
		setName(getClass().getName() + " URI:" + remoteURI);
	}

	public TCPConnectionHandler getConnectionHandler()  {
		return handler;
	}
	
	public void close() {
		handler.close();
	}

	public String getRemoteBaseURI() {
		return handler.getRemoteBaseURI();
	}
}