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
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Level;

import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInvokeOperation;
import org.ccsds.moims.mo.mal.MALProgressOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALRequestOperation;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Implementation of MALMessageHeader interface.
 */
public class TCPMessageHeader implements MALMessageHeader, Composite, Cloneable {
	protected static final long serialVersionUID = 0L;
	
	// TODO (AF): To remove !!
    @Override
    public TCPMessageHeader clone() throws CloneNotSupportedException {
    	return (TCPMessageHeader) super.clone();
    }

	protected URI URIFrom;
	protected Blob authenticationId;
	protected URI URITo;
	protected Time timestamp;
	protected QoSLevel QoSlevel;
	protected UInteger priority;
	protected IdentifierList domain;
	protected Identifier networkZone;
	protected SessionType session;
	protected Identifier sessionName;
	protected InteractionType interactionType;
	protected UOctet interactionStage;
	protected Long transactionId;
	protected UShort serviceArea;
	protected UShort service;
	protected UShort operation;
	protected UOctet areaVersion;
	protected Boolean isErrorMessage;

	// TODO (AF): Currently always set to 0.
	protected UOctet decodingId = new UOctet((short) 0);
	
	private String protocol = null;
	private String localBaseURI = null;
	private String remoteBaseURI = null;

	public Element createElement() {
		return new TCPMessageHeader();
	}
	
	/**
	 * Constructor.
	 */
	public TCPMessageHeader() { }
	
	/**
	 * Constructor.
	 */
	public TCPMessageHeader(String protocol, String localBaseURI, String remoteBaseURI) {
		this.protocol = protocol;
		this.localBaseURI = localBaseURI;
		this.remoteBaseURI = remoteBaseURI;
	}
	
	/**
	 * Constructor.
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
	 */
	public TCPMessageHeader(final URI uriFrom, final Blob authenticationId,
			final URI uriTo, final Time timestamp, final QoSLevel qosLevel,
			final UInteger priority, final IdentifierList domain,
			final Identifier networkZone, final SessionType session,
			final Identifier sessionName,
			final InteractionType interactionType,
			final UOctet interactionStage, final Long transactionId,
			final UShort serviceArea, final UShort service,
			final UShort operation, final UOctet serviceVersion,
			final UOctet decodingId,
			final Boolean isErrorMessage) {
		this.URIFrom = uriFrom;
		this.authenticationId = authenticationId;
		this.URITo = uriTo;
		this.timestamp = timestamp;
		this.QoSlevel = qosLevel;
		this.priority = priority;
		this.domain = domain;
		this.networkZone = networkZone;
		this.session = session;
		this.sessionName = sessionName;
		this.interactionType = interactionType;
		this.interactionStage = interactionStage;
		this.transactionId = transactionId;
		this.serviceArea = serviceArea;
		this.service = service;
		this.operation = operation;
		this.areaVersion = serviceVersion;
		this.decodingId = decodingId;
		this.isErrorMessage = isErrorMessage;
	}

	public URI getURIFrom() {
		return URIFrom;
	}

	public void setURIFrom(final URI urIFrom) {
		this.URIFrom = urIFrom;
	}

	public Blob getAuthenticationId() {
		return authenticationId;
	}

	public void setAuthenticationId(final Blob authenticationId) {
		this.authenticationId = authenticationId;
	}

	public IdentifierList getDomain() {
		return domain;
	}

	public void setDomain(final IdentifierList domain) {
		this.domain = domain;
	}

	public UOctet getInteractionStage() {
		return interactionStage;
	}

	public void setInteractionStage(final UOctet interactionStage) {
		this.interactionStage = interactionStage;
	}

	public InteractionType getInteractionType() {
		return interactionType;
	}

	public void setInteractionType(final InteractionType interactionType) {
		this.interactionType = interactionType;
	}

	public Boolean getIsErrorMessage() {
		return isErrorMessage;
	}

	public void setIsErrorMessage(final Boolean isErrorMessage) {
		this.isErrorMessage = isErrorMessage;
	}

	public Identifier getNetworkZone() {
		return networkZone;
	}

	public void setNetworkZone(final Identifier networkZone) {
		this.networkZone = networkZone;
	}

	public UShort getOperation() {
		return operation;
	}

	public void setOperation(final UShort operation) {
		this.operation = operation;
	}

	public UInteger getPriority() {
		return priority;
	}

	public void setPriority(final UInteger priority) {
		this.priority = priority;
	}

	public QoSLevel getQoSlevel() {
		return QoSlevel;
	}

	public void setQoSlevel(final QoSLevel qoSLevel) {
		this.QoSlevel = qoSLevel;
	}

	public UShort getService() {
		return service;
	}

	public void setService(final UShort service) {
		this.service = service;
	}

	public UShort getServiceArea() {
		return serviceArea;
	}

	public void setServiceArea(final UShort serviceArea) {
		this.serviceArea = serviceArea;
	}

	public UOctet getAreaVersion() {
		return areaVersion;
	}

	public void setAreaVersion(final UOctet areaVersion) {
		this.areaVersion = areaVersion;
	}

	public SessionType getSession() {
		return session;
	}

	public void setSession(final SessionType session) {
		this.session = session;
	}

	public Identifier getSessionName() {
		return sessionName;
	}

	public void setSessionName(final Identifier sessionName) {
		this.sessionName = sessionName;
	}

	public Time getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final Time timestamp) {
		this.timestamp = timestamp;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final Long transactionId) {
		this.transactionId = transactionId;
	}

	public URI getURITo() {
		return URITo;
	}

	public void setURITo(final URI urITo) {
		this.URITo = urITo;
	}
	
	final static int SDUTYPE_SEND = 0;
	final static int SDUTYPE_SUBMIT = 1;
	final static int SDUTYPE_SUBMIT_ACK = 2;
	final static int SDUTYPE_SUBMIT_ERROR = 2;
	final static int SDUTYPE_REQUEST = 3;
	final static int SDUTYPE_REQUEST_RESPONSE = 4;
	final static int SDUTYPE_REQUEST_ERROR = 4;
	final static int SDUTYPE_INVOKE = 5;
	final static int SDUTYPE_INVOKE_ACK = 6;
	final static int SDUTYPE_INVOKE_ERROR = 6;
	final static int SDUTYPE_INVOKE_RESPONSE = 7;
	final static int SDUTYPE_INVOKE_RESPONSE_ERROR = 7;
	final static int SDUTYPE_PROGRESS = 8;
	final static int SDUTYPE_PROGRESS_ACK = 9;
	final static int SDUTYPE_PROGRESS_ERROR = 9;
	final static int SDUTYPE_PROGRESS_UPDATE = 10;
	final static int SDUTYPE_PROGRESS_UPDATE_ERROR = 10;
	final static int SDUTYPE_PROGRESS_RESPONSE = 11;
	final static int SDUTYPE_PROGRESS_RESPONSE_ERROR = 11;
	final static int SDUTYPE_PUBSUB_REGISTER = 12;
	final static int SDUTYPE_PUBSUB_REGISTER_ACK = 13;
	final static int SDUTYPE_PUBSUB_REGISTER_ERROR = 13;
	final static int SDUTYPE_PUBSUB_PUBLISH_REGISTER = 14;
	final static int SDUTYPE_PUBSUB_PUBLISH_REGISTER_ACK = 15;
	final static int SDUTYPE_PUBSUB_PUBLISH_REGISTER_ERROR = 15;
	final static int SDUTYPE_PUBSUB_PUBLISH = 16;
	final static int SDUTYPE_PUBSUB_PUBLISH_ERROR = 16;
	final static int SDUTYPE_PUBSUB_NOTIFY = 17;
	final static int SDUTYPE_PUBSUB_NOTIFY_ERROR = 17;
	final static int SDUTYPE_PUBSUB_DEREGISTER = 18;
	final static int SDUTYPE_PUBSUB_DEREGISTER_ACK = 19;
	final static int SDUTYPE_PUBSUB_PUBLISH_DEREGISTER = 20;
	final static int SDUTYPE_PUBSUB_PUBLISH_DEREGISTER_ACK = 21;

	protected short getSDUType() {
		final short stage = (InteractionType._SEND_INDEX == interactionType.getOrdinal()) ? 0 : interactionStage.getValue();

		switch (interactionType.getOrdinal()) {
		case InteractionType._SEND_INDEX:
			return SDUTYPE_SEND;
		case InteractionType._SUBMIT_INDEX:
			if (MALSubmitOperation._SUBMIT_STAGE == stage) {
				return SDUTYPE_SUBMIT;
			}
			return SDUTYPE_SUBMIT_ACK;
		case InteractionType._REQUEST_INDEX:
			if (MALRequestOperation._REQUEST_STAGE == stage) {
				return SDUTYPE_REQUEST;
			}
			return SDUTYPE_REQUEST_RESPONSE;
		case InteractionType._INVOKE_INDEX:
			if (MALInvokeOperation._INVOKE_STAGE == stage) {
				return SDUTYPE_INVOKE;
			} else if (MALInvokeOperation._INVOKE_ACK_STAGE == stage) {
				return SDUTYPE_INVOKE_ACK;
			}
			return SDUTYPE_INVOKE_RESPONSE;
		case InteractionType._PROGRESS_INDEX: {
			if (MALProgressOperation._PROGRESS_STAGE == stage) {
				return SDUTYPE_PROGRESS;
			}
			if (MALProgressOperation._PROGRESS_ACK_STAGE == stage) {
				return SDUTYPE_PROGRESS_ACK;
			} else if (MALProgressOperation._PROGRESS_UPDATE_STAGE == stage) {
				return SDUTYPE_PROGRESS_UPDATE;
			}
			return SDUTYPE_PROGRESS_RESPONSE;
		}
		case InteractionType._PUBSUB_INDEX: {
			switch (stage) {
			case MALPubSubOperation._REGISTER_STAGE:
				return SDUTYPE_PUBSUB_REGISTER;
			case MALPubSubOperation._REGISTER_ACK_STAGE:
				return SDUTYPE_PUBSUB_REGISTER_ACK;
			case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
				return SDUTYPE_PUBSUB_PUBLISH_REGISTER;
			case MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE:
				return SDUTYPE_PUBSUB_PUBLISH_REGISTER_ACK;
			case MALPubSubOperation._PUBLISH_STAGE:
				return SDUTYPE_PUBSUB_PUBLISH;
			case MALPubSubOperation._NOTIFY_STAGE:
				return SDUTYPE_PUBSUB_NOTIFY;
			case MALPubSubOperation._DEREGISTER_STAGE:
				return SDUTYPE_PUBSUB_DEREGISTER;
			case MALPubSubOperation._DEREGISTER_ACK_STAGE:
				return SDUTYPE_PUBSUB_DEREGISTER_ACK;
			case MALPubSubOperation._PUBLISH_DEREGISTER_STAGE:
				return SDUTYPE_PUBSUB_PUBLISH_DEREGISTER;
			case MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE:
				return SDUTYPE_PUBSUB_PUBLISH_DEREGISTER_ACK;
			}
		}
		}

		return 0;
	}

	protected static InteractionType getInteractionType(short sduType) {
		switch (sduType) {
		case SDUTYPE_SEND:
			return InteractionType.SEND;
		case SDUTYPE_SUBMIT:
		case SDUTYPE_SUBMIT_ACK: // SDUTYPE_SUBMIT_ERROR
			return InteractionType.SUBMIT;
		case SDUTYPE_REQUEST:
		case SDUTYPE_REQUEST_RESPONSE: // SDUTYPE_REQUEST_ERROR
			return InteractionType.REQUEST;
		case SDUTYPE_INVOKE:
		case SDUTYPE_INVOKE_ACK: // SDUTYPE_INVOKE_ERROR
		case SDUTYPE_INVOKE_RESPONSE: // SDUTYPE_INVOKE_RESPONSE_ERROR
			return InteractionType.INVOKE;
		case SDUTYPE_PROGRESS:
		case SDUTYPE_PROGRESS_ACK: // SDUTYPE_PROGRESS_ERROR
		case SDUTYPE_PROGRESS_UPDATE: // SDUTYPE_PROGRESS_UPDATE_ERROR
		case SDUTYPE_PROGRESS_RESPONSE: // SDUTYPE_PROGRESS_RESPONSE_ERROR
			return InteractionType.PROGRESS;
		}
		// SDUTYPE_PUBSUB_*
		return InteractionType.PUBSUB;
	}

	protected static UOctet getInteractionStage(short sduType) {
		switch (sduType) {
		case SDUTYPE_SEND:
			return new UOctet((short) 0);
		case SDUTYPE_SUBMIT:
			return MALSubmitOperation.SUBMIT_STAGE;
		case SDUTYPE_SUBMIT_ACK: // SDUTYPE_SUBMIT_ERROR
			return MALSubmitOperation.SUBMIT_ACK_STAGE;
		case SDUTYPE_REQUEST:
			return MALRequestOperation.REQUEST_STAGE;
		case SDUTYPE_REQUEST_RESPONSE: // SDUTYPE_REQUEST_ERROR
			return MALRequestOperation.REQUEST_RESPONSE_STAGE;
		case SDUTYPE_INVOKE:
			return MALInvokeOperation.INVOKE_STAGE;
		case SDUTYPE_INVOKE_ACK: // SDUTYPE_INVOKE_ERROR
			return MALInvokeOperation.INVOKE_ACK_STAGE;
		case SDUTYPE_INVOKE_RESPONSE: // SDUTYPE_INVOKE_RESPONSE_ERROR
			return MALInvokeOperation.INVOKE_RESPONSE_STAGE;
		case SDUTYPE_PROGRESS:
			return MALProgressOperation.PROGRESS_STAGE;
		case SDUTYPE_PROGRESS_ACK: // SDUTYPE_PROGRESS_ERROR
			return MALProgressOperation.PROGRESS_ACK_STAGE;
		case SDUTYPE_PROGRESS_UPDATE: // SDUTYPE_PROGRESS_UPDATE_ERROR
			return MALProgressOperation.PROGRESS_UPDATE_STAGE;
		case SDUTYPE_PROGRESS_RESPONSE: // SDUTYPE_PROGRESS_RESPONSE_ERROR
			return MALProgressOperation.PROGRESS_RESPONSE_STAGE;
		case SDUTYPE_PUBSUB_REGISTER:
			return MALPubSubOperation.REGISTER_STAGE;
		case SDUTYPE_PUBSUB_REGISTER_ACK: // SDUTYPE_PUBSUB_REGISTER_ERROR
			return MALPubSubOperation.REGISTER_ACK_STAGE;
		case SDUTYPE_PUBSUB_PUBLISH_REGISTER:
			return MALPubSubOperation.PUBLISH_REGISTER_STAGE;
		case SDUTYPE_PUBSUB_PUBLISH_REGISTER_ACK: // SDUTYPE_PUBSUB_PUBLISH_REGISTER_ERROR
			return MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE;
		case SDUTYPE_PUBSUB_PUBLISH: // SDUTYPE_PUBSUB_PUBLISH_ERROR
			return MALPubSubOperation.PUBLISH_STAGE;
		case SDUTYPE_PUBSUB_NOTIFY: // SDUTYPE_PUBSUB_NOTIFY_ERROR
			return MALPubSubOperation.NOTIFY_STAGE;
		case SDUTYPE_PUBSUB_DEREGISTER:
			return MALPubSubOperation.DEREGISTER_STAGE;
		case SDUTYPE_PUBSUB_DEREGISTER_ACK:
			return MALPubSubOperation.DEREGISTER_ACK_STAGE;
		case SDUTYPE_PUBSUB_PUBLISH_DEREGISTER:
			return MALPubSubOperation.PUBLISH_DEREGISTER_STAGE;
		case SDUTYPE_PUBSUB_PUBLISH_DEREGISTER_ACK:
			return MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE;
		}

		return null;
	}

	int ERROR_MASK = 0x80;
	int QOS_LEVEL_MASK = 0x70;
	int SESSION_MASK = 0x0F;
	
	protected int getErrorFlag() {
		if (isErrorMessage) {
			return ERROR_MASK;
		}
		return 0;
	}

	protected int getQoSLevelBits() {
		return ((QoSlevel.getOrdinal() << 4) & QOS_LEVEL_MASK);
	}

	protected int getSessionBits() {
		return (session.getOrdinal() & SESSION_MASK);
	}
	
	int SOURCE_FLAG = 0x80;
	int DESTINATION_FLAG = 0x40;
	int PRIORITY_FLAG = 0x20;
	int TIMESTAMP_FLAG = 0x10;
	int NETWORK_ZONE_FLAG = 0x08;
	int SESSION_NAME_FLAG = 0x04;
	int DOMAIN_FLAG = 0x02;
	int AUTHENTICATION_FLAG = 0x01;

	void encodeFixedBinaryUOctet(OutputStream out, final UOctet value) throws IOException {
			out.write(java.nio.ByteBuffer.allocate(2).putShort(value.getValue()).array()[1]);
	}
	
	void encodeFixedBinaryUShort(OutputStream out, final UShort value) throws IOException {
	      out.write(java.nio.ByteBuffer.allocate(4).putInt(value.getValue()).array(), 2, 2);
	}
	
	void encodeFixedBinaryLong(OutputStream out, final Long value) throws IOException {
		out.write(java.nio.ByteBuffer.allocate(8).putLong(value).array());
	}
	
    void encodeBinaryUnsignedInt(OutputStream out, int value) throws IOException {
      while ((value & 0xFFFFFF80) != 0) {
        out.write((byte) ((value & 0x7F) | 0x80));
        value >>>= 7;
      }
      out.write((byte) (value & 0x7F));
    }

    void encodeBinarySignedInt(OutputStream out, final int value) throws IOException {
    	encodeBinaryUnsignedInt(out, (value << 1) ^ (value >> 31));
    }
	
    void encodeBinaryUnsignedLong(OutputStream out, long value) throws IOException {
    	while ((value & 0xFFFFFFFFFFFFFF80L) != 0L) {
    		out.write((byte) (((int) value & 0x7F) | 0x80));
    		value >>>= 7;
    	}
    	out.write((byte) ((int) value & 0x7F));
    }

    // Allows the encoding of Nullable Blob
    void encodeBinaryBytes(OutputStream out, final byte[] value) throws IOException {
    	if (null == value) {
    		encodeBinarySignedInt(out, -1);
    	} else {
    		encodeBinarySignedInt(out, value.length);
    		out.write(value);
    	}
    }
    
    void encodeBinaryBlob(OutputStream out, final byte[] value) throws IOException {
//    	if (null == value) {
//    		encodeBinarySignedInt(out, -1);
//    	} else {
    		encodeBinaryUnsignedInt(out, value.length);
    		out.write(value);
//    	}
    }
	
	void encodeBinaryString(OutputStream out, final String value) throws IOException {
		byte[] str = value.getBytes(Charset.forName("UTF-8"));
		encodeBinaryUnsignedInt(out, str.length);
		out.write(str);
	}

//	void encodeBinaryNullableString(OutputStream out, final String value) throws IOException {
//		if (null != value) {
//			encodeBinaryString(out, value);
//		} else {
//			encodeBinaryBytes(out, (byte[]) null);
//		}
//	}

	// TODO (AF): Avoid useless presence byte
//	void encodeBinaryIdentifierList(OutputStream out, final IdentifierList value) throws IOException {
//		encodeBinaryUnsignedInt(out, value.size());
//	    for (int i = 0; i < value.size(); i++) {
//	    	Identifier id = value.get(i);
//	    	if (id == null)
//	    		encodeBinaryNullableString(out, null);
//	    	else
//	    		encodeBinaryNullableString(out, id.getValue());
//	    }
//	}
	
	void encodeBinaryIdentifierList(OutputStream out, final IdentifierList value) throws IOException {
		encodeBinaryUnsignedInt(out, value.size());
	    for (int i = 0; i < value.size(); i++) {
	    	Identifier id = value.get(i);
	    	if (id == null) {
	    		out.write(0);
	    	} else {
	    		out.write(1);
	    		encodeBinaryString(out, id.getValue());
	    	}
	    }
	}
	
	public void encodeMessageHeader(OutputStream out) throws IllegalArgumentException, IOException, MALException {
		TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ >> " + this.toString());

		// First part of the header is encoded with FixedBinaryEncoder.
//		final FixedBinaryEncoder encoder1 = new FixedBinaryEncoder(lowLevelOutputStream);

		// Encode the version number.
	    encodeFixedBinaryUOctet(out, new UOctet((short) (getSDUType() | 0x20)));
		
	    encodeFixedBinaryUShort(out, serviceArea);
	    encodeFixedBinaryUShort(out, service);
	    encodeFixedBinaryUShort(out, operation);
		encodeFixedBinaryUOctet(out, areaVersion);

		encodeFixedBinaryUOctet(out, new UOctet((short) (getErrorFlag() | getQoSLevelBits() | getSessionBits())));
	    TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ >> " + getErrorFlag() + ", " + getQoSLevelBits() + ", " + getSessionBits());
	    
	    encodeFixedBinaryLong(out, transactionId);

		// Always encode URI's (Allowed by the specification).
		int sourceFlag = SOURCE_FLAG;
		int destinationFlag = DESTINATION_FLAG;
		
		int priorityFlag = ((getPriority()==null)?0:PRIORITY_FLAG);
		int timestampFlag = ((getTimestamp()==null)?0:TIMESTAMP_FLAG);
		int networkZoneFlag = ((getNetworkZone()==null)?0:NETWORK_ZONE_FLAG);
		int sessionNameFlag = ((getSessionName()==null)?0:SESSION_NAME_FLAG);
		int domainFlag = ((getDomain()==null)?0:DOMAIN_FLAG);
		int authenticationIdFlag = ((getAuthenticationId()==null)?0:AUTHENTICATION_FLAG);

		short flags = (short) (sourceFlag | destinationFlag |
				priorityFlag | timestampFlag | networkZoneFlag | sessionNameFlag | domainFlag | authenticationIdFlag);
		encodeFixedBinaryUOctet(out, new UOctet(flags));
		
		encodeFixedBinaryUOctet(out, decodingId);
		
		// The real message length is encoded later in TCPTransportDataTransceiver.sendEncoded() method.
		out.write(new byte[4]);
		
		// Second part of the header is encoded with BinaryEncoder.
//		final BinaryEncoder encoder2 = new BinaryEncoder(lowLevelOutputStream);
		
		// Always encode URI's (Allowed by the specification).
		encodeBinaryString(out, URIFrom.getValue());
		// TODO (AF): Encode only routing part of remote URI
//		encodeBinaryString(out, URITo.getValue());
		encodeBinaryString(out, TCPTransport.getRoutingPart(URITo.getValue()));
		
		if (priorityFlag != 0) encodeBinaryUnsignedLong(out, priority.getValue());
		if (timestampFlag != 0) {
			// TODO (AF): Time format ??
//			encodeBinaryUnsignedLong(out, timestamp.getValue());
			encodeFixedBinaryLong(out, timestamp.getValue());
		}
		if (networkZoneFlag != 0) encodeBinaryString(out, networkZone.getValue());
		if (sessionNameFlag != 0) encodeBinaryString(out, sessionName.getValue());
		if (domainFlag != 0) encodeBinaryIdentifierList(out, domain);
		if (authenticationIdFlag != 0) encodeBinaryBlob(out, authenticationId.getValue());
	}


	public void encode(final MALEncoder encoder) throws MALException {
		throw new MALException("Should never be used");
	}

	class Buffer {
		byte[] buf;
		int off;

		Buffer(byte[] buf) {
			this.buf = buf;
			off = 0;
		}
		
		byte get() {
			return buf[off++];
		}

		UOctet decodeFixedBinaryUOctet() throws IOException {
			return new UOctet((short) (buf[off++] & 0xFF));
		}

		UShort decodeFixedBinaryUShort() throws IOException {
			UShort result = new UShort(java.nio.ByteBuffer.wrap(buf, off, 2).getShort() & 0xFFFF);
			off += 2;
			return result;
		}
		
		Long decodeFixedBinaryLong() throws IOException {
			Long result = java.nio.ByteBuffer.wrap(buf, off, 8).getLong();
			off += 8;
			return result;
		}
		
		int decodeBinaryUnsignedInt() throws IOException {
		      int result = 0;
		      int shift = 0;
		      while ((buf[off] & 0x80) != 0) {
		        result |= (buf[off] & 0x7F) << shift;
		        shift += 7; off += 1;
		      }
		      return result | (buf[off++] << shift);
		}
		
		int decodeBinarySignedInt() throws IOException {
		      int raw = decodeBinaryUnsignedInt();
		      int temp = (((raw << 31) >> 31) ^ raw) >> 1;
		      return temp ^ (raw & (1 << 31));
		}
		
	    long decodeBinaryUnsignedLong() throws IOException {
	      long result = 0L;
	      int shift = 0;
	      while ((buf[off] & 0x80L) != 0) {
	        result |= (buf[off] & 0x7FL) << shift;
			// TODO (AF): To remove (use only for debug).
//	        System.out.println("buf[" + off + "]=" + buf[off] + " -> " + result);
	        shift += 7; off += 1;
	      }
	      result |= ((buf[off] & 0x7FL) << shift);
	      // TODO (AF): To remove (use only for debug).
//	      System.out.println("buf[" + off + "]=" + buf[off] + " -> " + result);
	      off += 1;
	      return result;
	    }

	    String decodeBinaryString() throws IOException {
	    	final int len = decodeBinaryUnsignedInt();
			TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << Decode String len=" + len);
	    	if (len >= 0) {
	    		final String str = new String(buf, off, len, Charset.forName("UTF-8"));
	    		off += len;
	    		return str;
	    	}
	    	return null;
	    }

		String decodeBinaryNullableString() throws IOException {
	    	final int len = decodeBinarySignedInt();
	    	if (len >= 0) {
	    		final String str = new String(buf, off, len, Charset.forName("UTF-8"));
	    		off += len;
	    		return str;
	    	}
	    	return null;
		}

		// TODO (AF): Remove useless presence flags
//		IdentifierList decodeBinaryIdentifierList() throws IOException {
//			IdentifierList list = new IdentifierList();
//			int size = decodeBinaryUnsignedInt();
//			for (int i=0; i<size; i++) {
//				Identifier id = new Identifier(decodeBinaryNullableString());
//				list.add(id);
//			}
//			return list;
//		}

		IdentifierList decodeBinaryIdentifierList() throws IOException {
			IdentifierList list = new IdentifierList();
			int size = decodeBinaryUnsignedInt();
			for (int i=0; i<size; i++) {
				if ((buf[off++] & 0xFF) != 0) {
					Identifier id = new Identifier(decodeBinaryString());
					list.add(id);
				} else {
					list.add(null);
				}
			}
			return list;
		}
		
		byte[] decodeBinaryBytes() throws IOException {
			int len = decodeBinarySignedInt();
			if (len >= 0) {
				final byte[] result = Arrays.copyOfRange(buf, off, off + len);
				off += len;
				return result;
			}
			throw new IllegalArgumentException("Array size must not be negative");
		}
		
		byte[] decodeBinaryBlob() throws IOException {
			int len = decodeBinaryUnsignedInt();
			if (len >= 0) {
				final byte[] result = Arrays.copyOfRange(buf, off, off + len);
				off += len;
				return result;
			}
			throw new IllegalArgumentException("Array size must not be negative");
		}
		
		void skip(int n) {
			off += n;
		}
		
		byte[] getRemainingEncodedData() {
			return Arrays.copyOfRange(buf, off, buf.length);
		}
	}
	
	public byte[] decodeMessageHeader(final byte[] packet) throws MALException {
		Buffer buffer = new Buffer(packet);
		
		try {
		// First part of the header is decoded with FixedBinaryDecoder.
		
		short tmpUOctet = buffer.decodeFixedBinaryUOctet().getValue();
		
		// TODO (AF): Remove extras logging.
		
		short version = (short) ((tmpUOctet >> 5) & 0x0007);
		TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << version=" + version);
		
		short sduType = (short) (tmpUOctet & 0x001F);
		interactionType = getInteractionType(sduType);
		TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << type=" + interactionType);
		interactionStage = getInteractionStage(sduType);
		TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << stage=" + interactionStage);
		
		serviceArea = buffer.decodeFixedBinaryUShort();
		TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << area=" + serviceArea);
		service = buffer.decodeFixedBinaryUShort();
		TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << service=" + service);
		operation = buffer.decodeFixedBinaryUShort();
		TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << operation=" + operation);
		
		areaVersion = buffer.decodeFixedBinaryUOctet();
		TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << areaVersion=" + areaVersion);
		
		tmpUOctet = buffer.decodeFixedBinaryUOctet().getValue();
	    TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << " + tmpUOctet);
		isErrorMessage = ((tmpUOctet & ERROR_MASK) != 0);
		QoSlevel = QoSLevel.fromOrdinal((tmpUOctet & QOS_LEVEL_MASK) >> 4);
		session = SessionType.fromOrdinal(tmpUOctet & SESSION_MASK);
	    TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << " + getErrorFlag() + ", " + getQoSLevelBits() + ", " + getSessionBits());

		transactionId = buffer.decodeFixedBinaryLong();
	    TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << transactionId=" + transactionId);
		
		short flags = buffer.decodeFixedBinaryUOctet().getValue();
	    TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << flags=" + flags);
		
		// Source and Destination URI could be omitted by some implementations.
		int sourceFlag = flags & SOURCE_FLAG;
		int destinationFlag = flags & DESTINATION_FLAG;
		
		int priorityFlag = flags & PRIORITY_FLAG;
		int timestampFlag = flags & TIMESTAMP_FLAG;
		int networkZoneFlag = flags & NETWORK_ZONE_FLAG;
		int sessionNameFlag = flags & SESSION_NAME_FLAG;
		int domainFlag = flags & DOMAIN_FLAG;
		int authenticationIdFlag = flags & AUTHENTICATION_FLAG;

		decodingId = buffer.decodeFixedBinaryUOctet();
	    TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << decodingId=" + decodingId);
		
		// Message length, this value is already read in TCPTransportDataTransceiver.readEncoded() method.
		buffer.skip(4);
		
		// Second part of the header is decoded with SplitBinaryDecoder.
		
		// If source URI is not present or complete we should build it.
		if (sourceFlag != 0) {
			URIFrom = new URI(buffer.decodeBinaryString());
			TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << Get URI From=" + URIFrom);
			if (! URIFrom.getValue().startsWith(protocol)) {
				URIFrom = new URI(remoteBaseURI + URIFrom.getValue());
			}
		} else {
			URIFrom = new URI(remoteBaseURI);
		}
	    TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << remoteBaseURI=" + remoteBaseURI + " -> URIFrom=" + URIFrom);

		// If destination URI is not present or complete we should build it.
		if (destinationFlag != 0) {
			URITo =  new URI(buffer.decodeBinaryString());
			TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << Get URI To=" + URITo);
			if (! URITo.getValue().startsWith(protocol)) {
				URITo = new URI(localBaseURI + URITo.getValue());
			}
		} else {
			URITo = new URI(localBaseURI);
		}
	    TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << localBaseURI=" + localBaseURI + " -> URITo=" + URITo);
		
		if (priorityFlag != 0) {
			priority = new UInteger(buffer.decodeBinaryUnsignedInt());
			TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << Decode priority=" + priority);
		}
		if (timestampFlag != 0) {
			// TODO (AF): Time format ??
//			timestamp = new Time(buffer.decodeBinaryUnsignedLong());
			timestamp = new Time(buffer.decodeFixedBinaryLong());
			TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << Decode timestamp=" + timestamp);
		}
		if (networkZoneFlag != 0) {
			networkZone = new Identifier(buffer.decodeBinaryString());
			TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << Decode networkZone=" + networkZone);
		}
		if (sessionNameFlag != 0) {
			sessionName = new Identifier(buffer.decodeBinaryString());
			TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << Decode sessionName=" + sessionName);
		}
		if (domainFlag != 0) {
			domain = buffer.decodeBinaryIdentifierList();
			TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << Decode domain=" + domain);
		}
		if (authenticationIdFlag != 0) {
			authenticationId = new Blob(buffer.decodeBinaryBlob());
			TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << Decode authenticationId=" + authenticationId);
		}
		
		TCPTransport.RLOGGER.log(Level.WARNING, "@@@@@ << " + this.toString());
		} catch (IOException exc) {
			throw new MALException(exc.getMessage());
		}
		return buffer.getRemainingEncodedData();
	}


	public Element decode(final MALDecoder decoder) throws MALException {
		throw new MALException("Should never be used");
	}

	public UShort getAreaNumber() {
		return new UShort(0);
	}

	public UShort getServiceNumber() {
		return new UShort(0);
	}

	public Long getShortForm() {
		return 0L;
	}

	public Integer getTypeShortForm() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TCPMessageHeader other = (TCPMessageHeader) obj;
		if (QoSlevel == null) {
			if (other.QoSlevel != null)
				return false;
		} else if (!QoSlevel.equals(other.QoSlevel))
			return false;
		if (URIFrom == null) {
			if (other.URIFrom != null)
				return false;
		} else if (!URIFrom.equals(other.URIFrom))
			return false;
		if (URITo == null) {
			if (other.URITo != null)
				return false;
		} else if (!URITo.equals(other.URITo))
			return false;
		if (areaVersion == null) {
			if (other.areaVersion != null)
				return false;
		} else if (!areaVersion.equals(other.areaVersion))
			return false;
		if (authenticationId == null) {
			if (other.authenticationId != null)
				return false;
		} else if (!authenticationId.equals(other.authenticationId))
			return false;
		if (decodingId == null) {
			if (other.decodingId != null)
				return false;
		} else if (!decodingId.equals(other.decodingId))
			return false;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (interactionStage == null) {
			if (other.interactionStage != null)
				return false;
		} else if (!interactionStage.equals(other.interactionStage))
			return false;
		if (interactionType == null) {
			if (other.interactionType != null)
				return false;
		} else if (!interactionType.equals(other.interactionType))
			return false;
		if (isErrorMessage == null) {
			if (other.isErrorMessage != null)
				return false;
		} else if (!isErrorMessage.equals(other.isErrorMessage))
			return false;
		if (localBaseURI == null) {
			if (other.localBaseURI != null)
				return false;
		} else if (!localBaseURI.equals(other.localBaseURI))
			return false;
		if (networkZone == null) {
			if (other.networkZone != null)
				return false;
		} else if (!networkZone.equals(other.networkZone))
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		if (priority == null) {
			if (other.priority != null)
				return false;
		} else if (!priority.equals(other.priority))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		if (remoteBaseURI == null) {
			if (other.remoteBaseURI != null)
				return false;
		} else if (!remoteBaseURI.equals(other.remoteBaseURI))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		if (serviceArea == null) {
			if (other.serviceArea != null)
				return false;
		} else if (!serviceArea.equals(other.serviceArea))
			return false;
		if (session == null) {
			if (other.session != null)
				return false;
		} else if (!session.equals(other.session))
			return false;
		if (sessionName == null) {
			if (other.sessionName != null)
				return false;
		} else if (!sessionName.equals(other.sessionName))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		return true;
	}

	public String toString() {
		final StringBuilder str = new StringBuilder("TCPMessageHeader{");
		str.append("URIFrom=");
		str.append(URIFrom);
		str.append(", authenticationId=");
		str.append(authenticationId);
		str.append(", URITo=");
		str.append(URITo);
		str.append(", timestamp=");
		str.append(timestamp);
		str.append(", QoSlevel=");
		str.append(QoSlevel);
		str.append(", priority=");
		str.append(priority);
		str.append(", domain=");
		str.append(domain);
		str.append(", networkZone=");
		str.append(networkZone);
		str.append(", session=");
		str.append(session);
		str.append(", sessionName=");
		str.append(sessionName);
		str.append(", interactionType=");
		str.append(interactionType);
		str.append(", interactionStage=");
		str.append(interactionStage);
		str.append(", transactionId=");
		str.append(transactionId);
		str.append(", serviceArea=");
		str.append(serviceArea);
		str.append(", service=");
		str.append(service);
		str.append(", operation=");
		str.append(operation);
		str.append(", serviceVersion=");
		str.append(areaVersion);
		str.append(", isErrorMessage=");
		str.append(isErrorMessage);
		str.append('}');

		return str.toString();
	}
}
