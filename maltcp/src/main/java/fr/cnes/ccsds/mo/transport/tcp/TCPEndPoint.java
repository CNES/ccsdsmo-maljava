/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2017 - 2018 CNES
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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.*;

/**
 *  Implementation of MALEndPoint interface.
 */
public class TCPEndPoint extends Thread implements MALEndpoint {
	protected final TCPTransport transport;
	protected final String localName;
	protected final String routingName;
	protected final String localURI;
	private boolean active = false;
	private MALMessageListener listener = null;

	/**
	 * Constructor.
	 *
	 * @param transport
	 *            Parent transport.
	 * @param localName
	 *            Endpoint local MAL name.
	 * @param routingName
	 *            Endpoint local routing name.
	 * @param uri
	 *            The URI string for this end point.
	 * @param wrapBodyParts
	 *            True if the encoded body parts should be wrapped in BLOBs.
	 */
	public TCPEndPoint(final TCPTransport transport, final String localName,
			final String routingName, final String uri) {
		this.transport = transport;
		this.localName = localName;
		this.routingName = routingName;
		this.localURI = uri;

		if (localName != null) 
			setName(localName);
		else if (routingName != null)
			setName(routingName);
	}

	public void startMessageDelivery() throws MALException {
		TCPTransport.RLOGGER.log(Level.INFO, "TCPEndpoint ({0}) Activating message delivery", localName);
		active = true;
	}

	public void stopMessageDelivery() throws MALException {
		TCPTransport.RLOGGER.log(Level.INFO, "TCPEndpoint ({0}) Deactivating message delivery", localName);
		active = false;
	}

	public String getLocalName() {
		return localName;
	}

	/**
	 * Returns the routing name used by this endpoint.
	 *
	 * @return the routing name.
	 */
	public String getRoutingName() {
		return routingName;
	}

	public URI getURI() {
		return new URI(localURI);
	}

	// TODO (AF): Currently always set to 0.
	protected UOctet decodingId = new UOctet((short) 0);
	
	public MALMessage createMessage(final Blob authenticationId,
			final URI uriTo, final Time timestamp, final QoSLevel qosLevel,
			final UInteger priority, final IdentifierList domain,
			final Identifier networkZone, final SessionType session,
			final Identifier sessionName,
			final InteractionType interactionType,
			final UOctet interactionStage, final Long transactionId,
			final UShort serviceArea, final UShort service,
			final UShort operation, final UOctet serviceVersion,
			final Boolean isErrorMessage, final Map qosProperties,
			final Object... body) throws IllegalArgumentException, MALException {
		try {
			return new TCPMessage(createMessageHeader(getURI(),
					authenticationId, uriTo, timestamp, qosLevel, priority,
					domain, networkZone, session, sessionName, interactionType,
					interactionStage, transactionId, serviceArea, service,
					operation, serviceVersion, decodingId, isErrorMessage), qosProperties,
					null, body);
		} catch (MALInteractionException ex) {
			throw new MALException("Error creating message", ex);
		}
	}

	public MALMessage createMessage(final Blob authenticationId,
			final URI uriTo, final Time timestamp, final QoSLevel qosLevel,
			final UInteger priority, final IdentifierList domain,
			final Identifier networkZone, final SessionType session,
			final Identifier sessionName,
			final InteractionType interactionType,
			final UOctet interactionStage, final Long transactionId,
			final UShort serviceArea, final UShort service,
			final UShort operation, final UOctet serviceVersion,
			final Boolean isErrorMessage, final Map qosProperties,
			final MALEncodedBody body) throws IllegalArgumentException,
			MALException {
		try {
			return new TCPMessage(createMessageHeader(getURI(),
					authenticationId, uriTo, timestamp, qosLevel, priority,
					domain, networkZone, session, sessionName, interactionType,
					interactionStage, transactionId, serviceArea, service,
					operation, serviceVersion, decodingId, isErrorMessage), qosProperties,
					null, body);
		} catch (MALInteractionException ex) {
			throw new MALException("Error creating message", ex);
		}
	}

	public MALMessage createMessage(final Blob authenticationId,
			final URI uriTo, final Time timestamp, final QoSLevel qosLevel,
			final UInteger priority, final IdentifierList domain,
			final Identifier networkZone, final SessionType session,
			final Identifier sessionName, final Long transactionId,
			final Boolean isErrorMessage, final MALOperation op,
			final UOctet interactionStage, final Map qosProperties,
			final MALEncodedBody body) throws IllegalArgumentException,
			MALException {
		try {
			return new TCPMessage(createMessageHeader(getURI(),
					authenticationId, uriTo, timestamp, qosLevel, priority,
					domain, networkZone, session, sessionName,
					op.getInteractionType(), interactionStage, transactionId,
					op.getService().getArea().getNumber(), op.getService()
					.getNumber(), op.getNumber(), op.getService()
					.getArea().getVersion(), decodingId, isErrorMessage),
					qosProperties, op, body);
		} catch (MALInteractionException ex) {
			throw new MALException("Error creating message", ex);
		}
	}

	public MALMessage createMessage(final Blob authenticationId,
			final URI uriTo, final Time timestamp, final QoSLevel qosLevel,
			final UInteger priority, final IdentifierList domain,
			final Identifier networkZone, final SessionType session,
			final Identifier sessionName, final Long transactionId,
			final Boolean isErrorMessage, final MALOperation op,
			final UOctet interactionStage, final Map qosProperties,
			final Object... body) throws IllegalArgumentException, MALException {
		try {
			return new TCPMessage(createMessageHeader(getURI(),
					authenticationId, uriTo, timestamp, qosLevel, priority,
					domain, networkZone, session, sessionName,
					op.getInteractionType(), interactionStage, transactionId,
					op.getService().getArea().getNumber(),
					op.getService().getNumber(), op.getNumber(),
					op.getService().getArea().getVersion(), decodingId, isErrorMessage),
					qosProperties, op, body);
		} catch (MALInteractionException ex) {
			throw new MALException("Error creating message", ex);
		}
	}

	public void sendMessage(final MALMessage msg) throws MALTransmitErrorException {
		TCPTransport.RLOGGER.log(Level.FINE, "TCPEndpoint ({0}) Send message {1}", new Object[] { localName, ((TCPMessage) msg).header });
		transport.sendMessage((TCPMessage) msg);
	}

	public void sendMessages(final MALMessage[] msgList) throws MALTransmitMultipleErrorException {
		TCPTransport.RLOGGER.log(Level.FINE, "TCPEndpoint ({0}) Send messages", localName);
		
		final List<MALTransmitErrorException> v = new LinkedList<MALTransmitErrorException>();

		try {
			for (int idx = 0; idx < msgList.length; idx++) {
				try {
					transport.sendMessage((TCPMessage) msgList[idx]);
				} catch (MALTransmitErrorException ex) {
					v.add(ex);
				}
			}

		} catch (Exception ex) {
			v.add(new MALTransmitErrorException(null,
					new MALStandardError(MALHelper.INTERNAL_ERROR_NUMBER, new Union(ex.getMessage())), null));
		}

		if (!v.isEmpty()) {
			throw new MALTransmitMultipleErrorException(
					v.toArray(new MALTransmitErrorException[v.size()]));
		}
	}

	/**
	 * Returns the current message listener.
	 *
	 * @return the current message listener.
	 */
	public MALMessageListener getMessageListener() {
		return listener;
	}

	public void setMessageListener(final MALMessageListener listener) throws MALException {
		this.listener = listener;
	}

	/**
	 * Callback method when a message is received for this endpoint.
	 *
	 * @param pmsg
	 *            The received message.
	 * @throws MALException
	 *             on an error.
	 */

	public void receiveMessage(final MALMessage pmsg) throws MALException {
		TCPTransport.RLOGGER.log(Level.FINE, "TCPEndpoint ({0}) insert message {1}", new Object[] { localName, pmsg.toString() });
		queue.offer(pmsg);
	}
	
	//	public void receiveMessage(final MALMessage pmsg) throws MALException {
	public void handleMessage(final MALMessage pmsg) throws MALException {
		TCPTransport.RLOGGER.log(Level.FINE, "TCPEndpoint ({0}) Delivering message", localName);
		
		if (active && (null != listener)) {
			// TODO (AF): Avoid a dead-lock in some tests.
//			new Thread() {
//				public void run() {
//					TCPTransport.RLOGGER.log(Level.SEVERE, "TCPEndpoint ({0}) Deliver message active({1}) listener({2}) {3}",
//							new Object[] { getName(), active, listener, pmsg.toString() });
//					listener.onMessage(TCPEndPoint.this, pmsg);
//				}
//			}.start();
			TCPTransport.RLOGGER.log(Level.SEVERE, "TCPEndpoint ({0}) Deliver message active({1}) listener({2}) {3}",
					new Object[] { getName(), active, listener, pmsg.toString() });
			listener.onMessage(TCPEndPoint.this, pmsg);
		} else {
			TCPTransport.RLOGGER.log(Level.WARNING,
					"TCPEndpoint ({0}) Discarding message active({1}) listener({2}) {3}",
					new Object[] { getName(), active, listener,
					pmsg.toString() });
		}
	}

	boolean running = true;
	BlockingQueue<MALMessage> queue = new LinkedBlockingQueue<MALMessage>();
	
	public void run() {
		try {
			TCPTransport.RLOGGER.log(Level.INFO, "TCPEndpoint (" + getName() + ") running");
			while (running) {
				try {
					MALMessage msg = queue.poll(1000L, TimeUnit.MILLISECONDS);
					if (msg != null) handleMessage(msg);
				} catch (InterruptedException exc) {
					TCPTransport.RLOGGER.log(Level.WARNING, 
								"TCPEndpoint (" + getName() + ") interupted)", exc);
				} catch (MALException exc) {
					TCPTransport.RLOGGER.log(Level.WARNING, 
							"TCPEndpoint (" + getName() + ") error handling message)", exc);
				}
			}
		} catch (Throwable t) {
			TCPTransport.RLOGGER.log(Level.SEVERE, "TCPEndpoint (" + getName() + ")", t);
		} finally {
			TCPTransport.RLOGGER.log(Level.INFO, "TCPEndpoint (" + getName() + ") exit");
		}
	}
	
	/**
	 * Callback method when multiple messages are received for this endpoint.
	 *
	 * @param pmsgs
	 *            The received messages.
	 * @throws MALException
	 *             on an error.
	 */
	public void receiveMessages(final TCPMessage[] pmsgs) throws MALException {
		if (active && (null != listener)) {
			listener.onMessages(this, pmsgs);
		} else {
			TCPTransport.RLOGGER.log(Level.WARNING,
					"TCPEndpoint ({0}) Discarding messages active({1}) listener({2})",
					new Object[] { getName(), active, listener });
		}
	}

	public void close() throws MALException {
		running = false;
	}

	/**
	 * Internal method for creating the correct message header type. Expected to
	 * be overridden in derived classes.
	 *
	 * @param uriFrom
	 *            URI of the message source
	 * @param authenticationId
	 *            Authentication identifier of the message
	 * @param uriTo
	 *            URI of the message destination
	 * @param timestamp
	 *            Timestamp of the message
	 * @param qosLevel
	 *            QoS level of the message
	 * @param priority
	 *            Priority of the message
	 * @param domain
	 *            Domain of the service provider
	 * @param networkZone
	 *            Network zone of the service provider
	 * @param session
	 *            Session of the service provider
	 * @param sessionName
	 *            Session name of the service provider
	 * @param interactionType
	 *            Interaction type of the operation
	 * @param interactionStage
	 *            Interaction stage of the interaction
	 * @param transactionId
	 *            Transaction identifier of the interaction, may be null.
	 * @param serviceArea
	 *            Area number of the service
	 * @param service
	 *            Service number
	 * @param operation
	 *            Operation number
	 * @param serviceVersion
	 *            Service version number
	 * @param isErrorMessage
	 *            Flag indicating if the message conveys an error
	 * @return the new message header.
	 */
	public TCPMessageHeader createMessageHeader(final URI uriFrom,
			final Blob authenticationId, final URI uriTo, final Time timestamp,
			final QoSLevel qosLevel, final UInteger priority,
			final IdentifierList domain, final Identifier networkZone,
			final SessionType session, final Identifier sessionName,
			final InteractionType interactionType,
			final UOctet interactionStage, final Long transactionId,
			final UShort serviceArea, final UShort service,
			final UShort operation, final UOctet serviceVersion,
			final UOctet decodingId,
			final Boolean isErrorMessage) {
		return new TCPMessageHeader(uriFrom, authenticationId, uriTo,
				timestamp, qosLevel, priority, domain, networkZone, session,
				sessionName, interactionType, interactionStage, transactionId,
				serviceArea, service, operation, serviceVersion, decodingId, 
				isErrorMessage);
	}
}
