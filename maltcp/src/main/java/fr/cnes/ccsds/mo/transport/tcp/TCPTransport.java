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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInvokeOperation;
import org.ccsds.moims.mo.mal.MALProgressOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALRequestOperation;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.mal.transport.MALTransport;

/**
 * Implementation of the MALTransport interface for MALTCP protocol.
 */
public class TCPTransport implements MALTransport {
	public static final Logger RLOGGER = Logger.getLogger("fr.cnes.ccsds.mo.transport.tcp");

	/**
	 * The string used to represent this protocol.
	 */
	private static final String PROTOCOL_SCHEME = "maltcp";
	/**
	 * The delimiter to use to separate the protocol part from the address part of the URL.
	 */
	private static final String PROTOCOL_DELIMITER = "://";
	/**
	 * The delimiter to separate the adress par of the URL from the port
	 */
	private static final char PORT_DELIMITER = ':';
	/**
	 * The delimiter to use to separate the external address part from the internal object part of the URL.
	 */
	private static final char SERVICE_DELIMITER = '/';
	
	/**
	 * Map of string MAL names to endpoints.
	 */
	protected final Map<String, TCPEndPoint> endpointMalMap = new HashMap<String, TCPEndPoint>();
	/**
	 * Map of string transport routing names to endpoints.
	 */
	protected final Map<String, TCPEndPoint> endpointRoutingMap = new HashMap<String, TCPEndPoint>();
	/**
	 * Map of outgoing channels. This associates a URI to a transport resource
	 * that is able to send messages to this URI.
	 */
	private final Map<String, TCPConnectionHandler> outgoingDataChannels = Collections.synchronizedMap(new HashMap<String, TCPConnectionHandler>());
	/**
	 * The stream factory used for encoding and decoding messages.
	 */
	private final MALElementStreamFactory streamFactory;
	/**
	 * Map of QoS properties.
	 */
	protected final Map qosProperties;

	/**
	 * The base string for URL for this protocol.
	 */
	protected String baseURI;
	
	protected String getLocalBaseURI() {
		return baseURI;
	}

	/**
	 * The server port that the TCP transport listens for incoming connections
	 */
	private final int serverPort;

	/**
	 * Server host, this can be one of the IP Addresses / hostnames of the host.
	 */
	private final String serverHost;

	/**
	 * The server connection listener
	 */
	private TCPServerConnectionListener serverConnectionListener = null;

	/**
	 * The list of data poller threads
	 */
	private final List<TCPMessagePoller> pollerThreads = new ArrayList<TCPMessagePoller>();

	public TCPTransport(Map properties) throws MALException {
		this.qosProperties = properties;
		this.streamFactory = MALElementStreamFactory.newFactory(PROTOCOL_SCHEME, properties);

		RLOGGER.info("Starting TCP Server Transport " + PROTOCOL_SCHEME + "\n" + properties);		
		if (properties != null) {
			// host / ip adress
			if (properties.containsKey("org.ccsds.moims.mo.mal.transport.maltcp.host")) {
				this.serverHost = (String) properties.get("org.ccsds.moims.mo.mal.transport.maltcp.host");
			} else {
				this.serverHost = null; // this is only a client
			}

			// port.
			if (properties.containsKey("org.ccsds.moims.mo.mal.transport.maltcp.port")) {
				this.serverPort = Integer.parseInt((String) properties.get("org.ccsds.moims.mo.mal.transport.maltcp.port"));
			} else {
				if (serverHost != null) {
					// this is a server, use default port
					this.serverPort = 61616;
				} else {
					// this is a client
					this.serverPort = 0; // 0 means this is a client
				}
			}
		} else {
			// default values
			this.serverPort = 0; // 0 means this is a client
			this.serverHost = null; // null means this is a client
		}

	}

	public void init() throws MALException {
	    baseURI = getProtocol() + PROTOCOL_DELIMITER + createTransportAddress() + SERVICE_DELIMITER;

		if (serverHost != null) {
			// this is also a server (i.e. provides some services)
			RLOGGER.log(Level.INFO, "Starting TCP Server Transport on port {0}", serverPort);

			// start server socket on predefined port / interface
			try {
				InetAddress serverHostAddr = InetAddress.getByName(serverHost);
				ServerSocket serverSocket = new ServerSocket(serverPort, 0, serverHostAddr);

				// create thread that will listen for connections
				synchronized (this) {
					serverConnectionListener = new TCPServerConnectionListener(this, serverSocket);
					serverConnectionListener.start();
				}

				RLOGGER.log(Level.INFO, "Started TCP Server Transport on port {0}", serverPort);
			} catch (Exception exc) {
				RLOGGER.log(Level.SEVERE,
						"Error starting TCP Server Transport on port " + serverPort, exc);
				throw new MALException("Error initialising TCP Server", exc);
			}
		}
	}

	protected String createTransportAddress() throws MALException {
		if (serverHost == null) {
			// This is a client in this case we get the IP Address of the host and provide a unique id
			// as the port (this information is uniquely used as an identifier for the MAL in the URI).
			return getDefaultHost() + PORT_DELIMITER + new UID().toString().replaceAll("[^abcdef0-9]", "");
		} else {
			// This a server (and potentially a client)
			return serverHost + PORT_DELIMITER + serverPort;
		}
	}

	/**
	 * Provide a default IP address for this host
	 *
	 * @return The transport specific address part.
	 * @throws MALException
	 *             On error
	 */
	private String getDefaultHost() throws MALException {
		try {
			// Build RMI url string
			final InetAddress addr = Inet4Address.getLocalHost();
			final StringBuilder hostAddress = new StringBuilder();
			if (addr instanceof Inet6Address) {
				RLOGGER.fine("TCPIP Address class is IPv6");
				hostAddress.append('[');
				hostAddress.append(addr.getHostAddress());
				hostAddress.append(']');
			} else {
				RLOGGER.fine("TCPIP Address class is IPv4");
				hostAddress.append(addr.getHostAddress());
			}

			return hostAddress.toString();
		} catch (UnknownHostException ex) {
			throw new MALException("Could not determine local host address", ex);
		}
	}

	/**
	 * Used to create random local names for endpoints.
	 */
	protected static final Random RANDOM_NAME = new Random();

	public MALEndpoint createEndpoint(String localName, final Map qosProperties) throws MALException {
		String strRoutingName = localName;
		if ((null == localName) || (0 == localName.length())) {
			strRoutingName = String.valueOf(RANDOM_NAME.nextInt());
		}
		TCPEndPoint endpoint = endpointRoutingMap.get(strRoutingName);

		if (null == endpoint) {
			RLOGGER.info("TCP Creating endpoint " + localName + " : " + strRoutingName);
			
			endpoint = new TCPEndPoint(this, localName, strRoutingName, baseURI + strRoutingName);
			endpointMalMap.put(localName, endpoint);
			endpointRoutingMap.put(strRoutingName, endpoint);
			endpoint.start();
		}

		return endpoint;
	}

	public MALEndpoint getEndpoint(final String localName) throws IllegalArgumentException {
		return endpointMalMap.get(localName);
	}

	public MALEndpoint getEndpoint(final URI uri) throws IllegalArgumentException {
		String endpointUriPart = getRoutingPart(uri.getValue());
		return endpointRoutingMap.get(endpointUriPart);
	}

	private static int getServiceIdx(String fullURI) {
		// TODO (AF): We should use the Java URI class.
		int delimPosition = fullURI.indexOf(PROTOCOL_DELIMITER);
		if (delimPosition < 0) {
			// There is no URI scheme
			delimPosition = 0;
		} else {
			// Jump "://"
			delimPosition += 3;
		}
		return fullURI.indexOf(SERVICE_DELIMITER, delimPosition);
	}
	
	/**
	 * Returns the "root" URI from the full URI. The root URI only contains the
	 * protocol and the main destination and is something unique for all URIs of
	 * the same MAL.
	 *
	 * @param fullURI	the full URI, for example maltcp://10.0.0.1:61616/serviceXYZ
	 * 
	 * @return the root URI, for example maltcp://10.0.0.1:61616
	 */
	public static String getRootURI(String fullURI) {
		int delimPosition = getServiceIdx(fullURI);

		if (delimPosition < 0) {
			// does not exist, return as is
			return fullURI;
		}

		return fullURI.substring(0, delimPosition);
	}

	/**
	 * Returns the routing part of the URI.
	 *
	 * @param uriValue
	 *            The URI value
	 * @return the routing part of the URI
	 */
	public static String getRoutingPart(String fullURI) {
		int delimPosition = getServiceIdx(fullURI);

		if (delimPosition < 0) {
			// does not exist, return as is
			return fullURI;
		}

		return fullURI.substring(delimPosition +1);
	}

	public void deleteEndpoint(final String localName) throws MALException {
		final TCPEndPoint endpoint = endpointMalMap.get(localName);

		if (null != endpoint) {
			RLOGGER.log(Level.INFO, "TCP Deleting endpoint", localName);
			endpointMalMap.remove(localName);
			endpointRoutingMap.remove(endpoint.getRoutingName());
			endpoint.close();
		}
	}

	public MALBrokerBinding createBroker(
			final String localName,
			final Blob authenticationId,
			final QoSLevel[] expectedQos,
			final UInteger priorityLevelNumber,
			final Map defaultQoSProperties) throws MALException {
		// not supported by TCP transport
		return null;
	}

	public MALBrokerBinding createBroker(
			final MALEndpoint endpoint,
			final Blob authenticationId,
			final QoSLevel[] qosLevels,
			final UInteger priorities,
			final Map properties) throws MALException {
		// not supported by TCP transport
		return null;
	}

	public boolean isSupportedInteractionType(final InteractionType type) {
		// Supports all IPs except Pub/Sub
		return InteractionType.PUBSUB.getOrdinal() != type.getOrdinal();
	}

	public boolean isSupportedQoSLevel(final QoSLevel qos) {
		// The transport only supports BESTEFFORT in reality but this is only a
		// test transport so we say it supports all
		return true;
	}

	public void close() throws MALException {
		RLOGGER.info("TCPTransport.close()");
		synchronized (this) {
			for (TCPMessagePoller entry : pollerThreads) {
				entry.close();
			}
			pollerThreads.clear();
		}

		for (Map.Entry<String, TCPEndPoint> entry : endpointMalMap.entrySet()) {
			entry.getValue().close();
		}

		endpointMalMap.clear();
		endpointRoutingMap.clear();

		RLOGGER.fine("Closing outgoing channels");
		for (Map.Entry<String, TCPConnectionHandler> entry : outgoingDataChannels.entrySet()) {
			final TCPConnectionHandler sender = entry.getValue();
			sender.close();
		}

		outgoingDataChannels.clear();
		RLOGGER.fine("Closed outgoing channels");

		synchronized (this) {
			if (null != serverConnectionListener) {
				serverConnectionListener.interrupt();
			}
		}
	}
	
	public void sendMessage(TCPMessage msg) throws MALTransmitErrorException, IllegalArgumentException {
		try {
			// get the root URI, (e.g. tcpip://10.0.0.1:61616 )
			String destinationURI = msg.getHeader().getURITo().getValue();
			String remoteRootURI = getRootURI(destinationURI);

			RLOGGER.log(Level.FINE,
					"TCP sending msg. Target root URI: {0} full URI:{1} ## {2} ##",
					new Object[] { remoteRootURI, destinationURI, msg.getHeader().getTransactionId() });

			// Get outgoing channel
			TCPConnectionHandler handler = outgoingDataChannels.get(remoteRootURI);
			if (null == handler) {
				// we do not have any channel for this URI
				// try to create a set of connections to this URI
				RLOGGER.log(Level.FINE, "TCP received request to create connections to URI: {0}", remoteRootURI);

				try {
					// create new sender for this URI
					handler = registerConnectionHandler(createConnectionHandler(msg, remoteRootURI), remoteRootURI);
				} catch (MALException e) {
					RLOGGER.log(Level.WARNING, "TCP could not connect to :" + remoteRootURI, e);
					throw new MALTransmitErrorException(msg.getHeader(),
							new MALStandardError(
									MALHelper.DESTINATION_UNKNOWN_ERROR_NUMBER,
									null), null);
				}
			}
			RLOGGER.log(Level.FINE, "TCP send message using: {0}", handler);

			handler.sendEncodedMessage(internalEncodeMessage(msg));

			RLOGGER.log(Level.FINE, "TCP finished Sending data to {0}", remoteRootURI);
		} catch (MALTransmitErrorException e) {
			// this stops any true MAL exceptions getting caught by the
			// generic catch all below
			RLOGGER.log(Level.SEVERE, "Interrupted while sending message", e);
			throw e;
		} catch (InterruptedException e) {
			RLOGGER.log(Level.SEVERE, "Interrupted while waiting for data reply", e);
			throw new MALTransmitErrorException(msg.getHeader(), new MALStandardError(MALHelper.INTERNAL_ERROR_NUMBER, null), null);
		} catch (Exception t) {
			RLOGGER.log(Level.SEVERE, "TCP could not send message!", t);
			throw new MALTransmitErrorException(msg.getHeader(), new MALStandardError(MALHelper.INTERNAL_ERROR_NUMBER, null), null);
		}
	}

	/**
	 * Registers a connection handler for a given root URI.
	 *
	 * @param handler		The data sender that is able to send messages to the URI
	 * @param remoteRootURI	The remote root URI
	 * 
	 * @return returns the connection handler for this URI.
	 */
	protected synchronized TCPConnectionHandler registerConnectionHandler(
			TCPConnectionHandler handler, String remoteRootURI) {
		RLOGGER.log(Level.INFO, "TCP register connection to URI: {0}", remoteRootURI);
		
		// Check if there is already a TCP channel for this URI
		TCPConnectionHandler h = outgoingDataChannels.get(remoteRootURI);
		if (h != null) {
			// There is already a TCP channel for this URI
			RLOGGER.log(Level.WARNING, "TCP connection handler already registerd for URI:" + remoteRootURI, new Exception());
		} else {
			// There is no TCP channel for this URI, register it
			RLOGGER.fine("TCP registering connection handler for URI:" + remoteRootURI);
			outgoingDataChannels.put(remoteRootURI, handler);
			h = handler;
		}

		return h;
	}

	protected byte[] internalEncodeMessage(final TCPMessage msg) throws Exception {
		// encode the message
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final MALElementOutputStream enc = getStreamFactory().createOutputStream(baos);
			msg.encodeMessage(getStreamFactory(), enc, baos);
			byte[] data = baos.toByteArray();
			return data;
		} catch (MALException ex) {
			RLOGGER.log(Level.SEVERE, "TCP could not encode message!", ex);
			throw new MALTransmitErrorException(msg.getHeader(), new MALStandardError(MALHelper.BAD_ENCODING_ERROR_NUMBER, null), null);
		}
	}

	/**
	 * Returns the stream factory.
	 *
	 * @return the stream factory
	 */
	public MALElementStreamFactory getStreamFactory() {
		return streamFactory;
	}

	public void closeConnection(final String uriTo, final TCPMessagePoller poller) {
		String localUriTo = uriTo;
		// remove all associations with this target URI
		if ((null == localUriTo) && (null != poller)) {
			localUriTo = poller.getRemoteURI();
		}

		if (localUriTo != null) {
			TCPConnectionHandler commsChannel = outgoingDataChannels.get(localUriTo);
			if (commsChannel != null) {
				commsChannel.close();
				outgoingDataChannels.remove(localUriTo);
			} else {
				RLOGGER.log(Level.WARNING,
						"Could not locate associated data to close communications for URI : {0} ", localUriTo);
			}
		}

		if (null != poller) {
			poller.close();
		}
	}

	/**
	 * Used to inform the transport about communication problems with clients.
	 * In this case the transport will terminate all communication channels with
	 * the destination in order for them to be re-established.
	 *
	 * @param uriTo
	 *            the connection handler that received this message
	 * @param receptionHandler
	 */
	public void communicationError(String uriTo, TCPMessagePoller poller) {
		RLOGGER.log(Level.WARNING, "TCP Communication Error with {0} ", uriTo);
		closeConnection(uriTo, poller);
	}

	/**
	 * This method processes an incoming message by routing it to the
	 * appropriate endpoint, returning an error if the message cannot be
	 * processed.
	 *
	 * @param msg
	 *            The source message.
	 * @param smsg
	 *            The message in a string representation for logging.
	 */
	protected void processIncomingMessage(final TCPMessage msg) {
		try {
			RLOGGER.fine("TCP Processing message : " + msg.getHeader().getTransactionId());

			String endpointUriPart = getRoutingPart(msg.getHeader().getURITo().getValue());

			final TCPEndPoint endpoint = endpointRoutingMap.get(endpointUriPart);

			if (null != endpoint) {
				RLOGGER.fine("TCP Passing to message handler " + endpoint.getLocalName());
				endpoint.receiveMessage(msg);
			} else {
				RLOGGER.log(Level.FINE, "TCP Message handler NOT FOUND {0}", new Object[] { endpointUriPart });
				returnErrorMessage(
						null, msg,
						MALHelper.DESTINATION_UNKNOWN_ERROR_NUMBER,
						"TCP Cannot find endpoint: " + endpointUriPart);
			}
		} catch (Exception e) {
			RLOGGER.log(Level.WARNING,
					"TCP Error occurred when receiving data : {0}", e);

			final StringWriter wrt = new StringWriter();
			e.printStackTrace(new PrintWriter(wrt));

			try {
				returnErrorMessage(
						null,
						msg,
						MALHelper.INTERNAL_ERROR_NUMBER,
						"TCP Error occurred: " + e.toString() + " : " + wrt.toString());
			} catch (MALException ex) {
				RLOGGER.log(Level.SEVERE,
						"TCP Error occurred when return error data : {0}", ex);
			}
		} catch (Error e) {
			// This is bad, Java errors are serious, so inform the other side if we can
			RLOGGER.log(Level.SEVERE,
					"TCP Error occurred when processing message : {0}", e);

			final StringWriter wrt = new StringWriter();
			e.printStackTrace(new PrintWriter(wrt));

			try {
				returnErrorMessage(
						null,
						msg,
						MALHelper.INTERNAL_ERROR_NUMBER,
						"TCP Error occurred: " + e.toString() + " : " + wrt.toString());
			} catch (MALException ex) {
				RLOGGER.log(Level.SEVERE,
						"TCP Error occurred when return error data : {0}", ex);
			}
		}
	}

	/**
	 * Creates a return error message based on a received message.
	 *
	 * @param ep
	 *            The endpoint to use for sending the error.
	 * @param oriMsg
	 *            The original message
	 * @param errorNumber
	 *            The error number
	 * @param errorMsg
	 *            The error message.
	 * @throws MALException
	 *             if cannot encode a response message
	 */
	protected void returnErrorMessage(TCPEndPoint ep, final TCPMessage oriMsg,
			final UInteger errorNumber, final String errorMsg)
			throws MALException {
		try {
			final int type = oriMsg.getHeader().getInteractionType()
					.getOrdinal();
			final short stage = oriMsg.getHeader().getInteractionStage()
					.getValue();

			// first check that message should be responded to
			if (((type == InteractionType._SUBMIT_INDEX) && (stage == MALSubmitOperation._SUBMIT_STAGE))
					|| ((type == InteractionType._REQUEST_INDEX) && (stage == MALRequestOperation._REQUEST_STAGE))
					|| ((type == InteractionType._INVOKE_INDEX) && (stage == MALInvokeOperation._INVOKE_STAGE))
					|| ((type == InteractionType._PROGRESS_INDEX) && (stage == MALProgressOperation._PROGRESS_STAGE))
					|| ((type == InteractionType._PUBSUB_INDEX) && (stage == MALPubSubOperation._REGISTER_STAGE))
					|| ((type == InteractionType._PUBSUB_INDEX) && (stage == MALPubSubOperation._DEREGISTER_STAGE))
					|| ((type == InteractionType._PUBSUB_INDEX) && (stage == MALPubSubOperation._PUBLISH_REGISTER_STAGE))
					|| ((type == InteractionType._PUBSUB_INDEX) && (stage == MALPubSubOperation._PUBLISH_DEREGISTER_STAGE))) {
				final MALMessageHeader srcHdr = oriMsg.getHeader();

				if ((null == ep) && (!endpointMalMap.isEmpty())) {
					TCPEndPoint endpoint = endpointMalMap.entrySet().iterator()
							.next().getValue();

					final TCPMessage retMsg = (TCPMessage) endpoint
							.createMessage(srcHdr.getAuthenticationId(), srcHdr
									.getURIFrom(),
									new Time(new Date().getTime()), srcHdr
											.getQoSlevel(), srcHdr
											.getPriority(), srcHdr.getDomain(),
									srcHdr.getNetworkZone(), srcHdr
											.getSession(), srcHdr
											.getSessionName(), srcHdr
											.getInteractionType(), new UOctet(
											(short) (srcHdr
													.getInteractionStage()
													.getValue() + 1)), srcHdr
											.getTransactionId(), srcHdr
											.getServiceArea(), srcHdr
											.getService(), srcHdr
											.getOperation(), srcHdr
											.getAreaVersion(), true, oriMsg
											.getQoSProperties(), errorNumber,
									new Union(errorMsg));

					retMsg.getHeader().setURIFrom(srcHdr.getURITo());

					sendMessage(retMsg);
				} else {
					RLOGGER.log(Level.WARNING,
							"TCP Unable to return error number ({0}) as no endpoint supplied : {1}",
							new Object[] { errorNumber, oriMsg.getHeader() });
				}
			} else {
				RLOGGER.log(Level.WARNING,
						"TCP Unable to return error number ({0}) as already a return message : {1}",
						new Object[] { errorNumber, oriMsg.getHeader() });
			}
		} catch (MALTransmitErrorException ex) {
			RLOGGER.log(Level.WARNING,
					"TCP Error occurred when attempting to return previous error : {0}",
					ex);
		}
	}

	public TCPMessage createMessage(byte[] packet, String remoteBaseURI) throws MALException {
	    return new TCPMessage(true, new TCPMessageHeader(getProtocol(), getLocalBaseURI(), remoteBaseURI), qosProperties, packet, getStreamFactory());
	}
	
	public void receiveMessage(TCPMessagePoller poller, byte[] rawMsg) {
		try {
			TCPTransport.RLOGGER.log(Level.FINE, "TCP Receiving message");
			TCPMessage malMsg = createMessage(rawMsg, poller.getRemoteBaseURI());
			TCPTransport.RLOGGER.log(Level.FINE,
					"TCP Receiving message : {0}", new Object[] { malMsg.getHeader().getTransactionId() });

			// Register communication channel if needed.
			TCPConnectionHandler handler = null;
			if ((null != poller) && (null == poller.getRemoteURI())) {
				// transport supports bi-directional communication this is the first message received
				// from this reception handler add the remote base URI it is receiving messages from
				String sourceURI = malMsg.getHeader().getURIFrom().getValue();
				String sourceRootURI = getRootURI(sourceURI);

				poller.setRemoteURI(sourceRootURI);

				// register the communication channel with this URI if needed
				handler = registerConnectionHandler(poller.getConnectionHandler(), sourceRootURI);
			}
			RLOGGER.log(Level.FINE, "TCP receive message using: {0}", handler);

			processIncomingMessage(malMsg);
		} catch (MALException e) {
			TCPTransport.RLOGGER.log(Level.WARNING, "TCP Error occurred when decoding data : {0}", e);
			communicationError(null, poller);
		}
	}

	private final String getProtocol() {
		if (PROTOCOL_SCHEME.contains(":")) {
			return PROTOCOL_SCHEME.substring(0, PROTOCOL_SCHEME.indexOf(':'));
		}
		return PROTOCOL_SCHEME;
	}
	
	private String remoteBaseURI(Socket socket) {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append(getProtocol()).append(PROTOCOL_DELIMITER);
		strbuf.append(socket.getInetAddress().getHostAddress());
		strbuf.append(PORT_DELIMITER).append(socket.getPort()).append(SERVICE_DELIMITER);
		return strbuf.toString();
	}
	
	public TCPConnectionHandler createConnectionHandler(Socket socket) throws IOException {
		return new TCPConnectionHandler(socket, remoteBaseURI(socket));
	}

	protected TCPConnectionHandler createConnectionHandler(
			TCPMessage msg,
			String remoteRootURI) throws MALException, MALTransmitErrorException {
		RLOGGER.log(Level.INFO, "TCP create connection to URI: {0}", remoteRootURI);

		try {
			// decode target address
			String targetAddress = remoteRootURI.replaceAll(PROTOCOL_SCHEME + PROTOCOL_DELIMITER, "");
			targetAddress = targetAddress.replaceAll(PROTOCOL_SCHEME, ""); // in case
			// the protocol is in the format tcpip://

			if (!targetAddress.contains(":")) {
				// malformed URI
				throw new MALException("Malformed URI:" + remoteRootURI);
			}

			String host = targetAddress.split(":")[0];
			int port = Integer.parseInt(targetAddress.split(":")[1]);

			// Creates a connection handler for the socket
			TCPConnectionHandler handler = createConnectionHandler(new Socket(host, port));
			// Creates also a thread for this connection in order to read messages from it.
			TCPMessagePoller poller = new TCPMessagePoller(this, handler);
			poller.setRemoteURI(remoteRootURI);
			pollerThreads.add(poller);
			poller.start();
			
			RLOGGER.log(Level.FINE, "TCP connection created: {0}", handler);

			return handler;
		} catch (NumberFormatException nfe) {
			RLOGGER.log(Level.WARNING, "Have no means to communicate with client URI : {0}", remoteRootURI);
			throw new MALException("Have no means to communicate with client URI : " + remoteRootURI);
		} catch (UnknownHostException e) {
			RLOGGER.log(Level.WARNING, "TCPIP could not find host :{0}", remoteRootURI);
			throw new MALTransmitErrorException(msg.getHeader(), 
					new MALStandardError(MALHelper.DESTINATION_UNKNOWN_ERROR_NUMBER, null),
					new HashMap());
		} catch (java.net.ConnectException e) {
			RLOGGER.log(Level.WARNING, "TCPIP could not connect to :{0}", remoteRootURI);
			throw new MALTransmitErrorException(msg.getHeader(),
					new MALStandardError(MALHelper.DESTINATION_TRANSIENT_ERROR_NUMBER, null),
					null);
		} catch (IOException e) {
			// there was a communication problem, we need to clean up the
			// objects we created in the meanwhile
			communicationError(remoteRootURI, null);

			// rethrow for higher MAL leyers
			throw new MALException("IO Exception", e);
		}
	}
}
