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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import fr.cnes.ccsds.mo.transport.tcp.body.TCPDeregisterBody;
import fr.cnes.ccsds.mo.transport.tcp.body.TCPErrorBody;
import fr.cnes.ccsds.mo.transport.tcp.body.TCPMessageBody;
import fr.cnes.ccsds.mo.transport.tcp.body.TCPNotifyBody;
import fr.cnes.ccsds.mo.transport.tcp.body.TCPPublishBody;
import fr.cnes.ccsds.mo.transport.tcp.body.TCPPublishRegisterBody;
import fr.cnes.ccsds.mo.transport.tcp.body.TCPRegisterBody;

/**
 * Implementation of the MALMessage interface.
 */
public class TCPMessage implements MALMessage, java.io.Serializable {
	protected final TCPMessageHeader header;
	protected final TCPMessageBody body;
	protected final Map qosProperties;
	protected MALOperation operation = null;
	private static final long serialVersionUID = 0L;

	/**
	 * Constructor.
	 *
	 * @param wrapBodyParts	True if the encoded body parts should be wrapped in BLOBs.
	 * @param header		The message header to use.
	 * @param qosProperties	The QoS properties for this message.
	 * @param operation		The details of the operation being encoding, can be null.
	 * @param body			the body of the message.
	 * 
	 * @throws org.ccsds.moims.mo.mal.MALInteractionException If the operation is unknown.
	 */
	public TCPMessage(
			final TCPMessageHeader header, 
			final Map qosProperties,
			final MALOperation operation, final Object... body) throws MALInteractionException {
		this.header = header;
		if (null == operation) {
			MALArea area = MALContextFactory.lookupArea(
					this.header.getServiceArea(), this.header.getAreaVersion());
			if (null == area) {
				throw new MALInteractionException(new MALStandardError(
						MALHelper.UNSUPPORTED_AREA_ERROR_NUMBER, null));
			}

			MALService service = area.getServiceByNumber(this.header.getService());
			if (null == service) {
				throw new MALInteractionException(new MALStandardError(
						MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, null));
			}

			this.operation = service.getOperationByNumber(this.header.getOperation());
			if (null == this.operation) {
				throw new MALInteractionException(new MALStandardError(
						MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, null));
			}
		} else {
			this.operation = operation;
		}
		this.body = createMessageBody(body);
		this.qosProperties = qosProperties;
	}

	/**
	 * Constructor.
	 *
	 * @param wrapBodyParts	True if the encoded body parts should be wrapped in BLOBs.
	 * @param readHeader	True if the header should be read from the packet.
	 * @param header		An instance of the header class to use.
	 * @param qosProperties	The QoS properties for this message.
	 * @param packet		The message in encoded form.
	 * @param encFactory	The stream factory to use for decoding.
	 * 
	 * @throws MALException	On decoding error.
	 */
	
	public TCPMessage(
			final boolean readHeader,
			final TCPMessageHeader header,
			final Map qosProperties,
			final byte[] packet, final MALElementStreamFactory encFactory) throws MALException {
		this.qosProperties = qosProperties;
		byte[] packet1 = null;

		System.out.println("\n\n\t##### total -> " + packet.length);
		
		if (readHeader) {
			packet1 = header.decodeMessageHeader(packet);
			
			// TODO (AF): To remove (use only for debug).
//			System.out.print("Packet[" + packet.length + "]=");
//			for (int i=0; i<packet.length; i++)
//				System.out.print(packet[i] + ", ");
//			System.out.println("");
//
//			TCPMessageHeader header2 = null;
//			try {
//				header2 = (TCPMessageHeader) header.clone();
//			} catch (CloneNotSupportedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
//			try {
//				header2.decodeMessageHeader1(packet);
//				if (! header.equals(header2))
//					throw new IOException("Bad header decoding !!");
//			} catch (IOException e) {
//				System.out.println("Header1 = " + header);
//				System.out.println("Header2 = " + header2);
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				System.exit(0);
//			}
			
			// TODO (AF): To remove (use 2 encoding for header).
//			MALEncodingContext ctx = new MALEncodingContext(header, null, 0, qosProperties, qosProperties);
//			
//			final ByteArrayInputStream bis = new ByteArrayInputStream(packet);
//			FixedBinaryDecoder decoder = new FixedBinaryDecoder(bis);
//			this.header = (TCPMessageHeader) header.decode(decoder);
//			
//			packet1 = decoder.getRemainingEncodedData();
			System.out.println("\n\n\t##### remain -> " + packet1.length);
		}
		this.header = header;

		final ByteArrayInputStream bis = new ByteArrayInputStream(packet1);
		final MALElementInputStream enc = encFactory.createInputStream(bis);
		this.body = createMessageBody(encFactory, enc);
	}

	public MALMessageHeader getHeader() {
		return header;
	}

	public MALMessageBody getBody() {
		return body;
	}

	public Map getQoSProperties() {
		return qosProperties;
	}

	public void free() throws MALException {
		// TODO (AF): ?
	}
	
	/**
	 * Encodes the contents of the message into the provided stream
	 *
	 * @param streamFactory
	 *            The stream factory to use for encoder creation.
	 * @param enc
	 *            The output stream to use for encoding.
	 * @param out
	 *            the stream to write to.
	 * @param writeHeader
	 *            True if the header should be written to the output stream.
	 * @throws MALException
	 *             On encoding error.
	 */
	public void encodeMessage(
			final MALElementStreamFactory streamFactory,
			final MALElementOutputStream enc,
			final OutputStream out) throws MALException {
		try {
			MALEncodingContext ctx = new MALEncodingContext(header, operation, 0, qosProperties, qosProperties);

			if (null != header) {
				header.encodeMessageHeader(out);
				
				// TODO (AF): To remove (use only for debug).
//				ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
//				header.encodeMessageHeader(bos1);
//
//				ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
//				header.encodeMessageHeader(bos2);
//
//				
//				byte[] array1 = bos1.toByteArray();
//				System.out.print("Array1[" + array1.length + "]=");
//				for (int i=0; i<array1.length; i++)
//					System.out.print(array1[i] + ", ");
//				System.out.println("");
//
//				byte[] array2 = bos2.toByteArray();
//				System.out.print("Array2[" + array2.length + "]=");
//				for (int i=0; i<array2.length; i++)
//					System.out.print(array2[i] + ", ");
//				System.out.println("");
//
//				if (! Arrays.equals(array1, array2)) {
//					System.out.println("Bad encoding !!");
//					System.exit(0);
//				}
					
				// TODO (AF): To remove (use 2 encoding for header).
//				// First encode the header with FixedBinaryEncoder.
//				MALElementOutputStream hos = new FixedBinaryElementOutputStream(lowLevelOutputStream);
//				hos.writeElement(header, ctx);
//				// TODO (AF): To remove.
////				enc.writeElement(header, ctx);
				
				// TODO (AF): To remove.
				System.out.println("\n\n\t##### header -> " + ((ByteArrayOutputStream) out).size());
			} else {
				throw new MALException("Internal error encoding message, header NULL");
			}
			// TODO (AF): To remove.
			System.out.println("\n\n\t##### body=" + body);

			// Now encode the body with the specified encoding
			body.encodeMessageBody(streamFactory, enc, out, header.getInteractionStage(), ctx);
			// TODO (AF): No longer needed.
			// Be careful, flush method throws a NPE with encoding wrapper.
//			enc.flush();
			
			System.out.println("\n\n\t##### total -> " + ((ByteArrayOutputStream) out).size());
		} catch (Exception ex) {
			throw new MALException("Internal error encoding message", ex);
		}
	}

	private TCPMessageBody createMessageBody(
			final MALElementStreamFactory encFactory,
			final MALElementInputStream encBodyElements) {
		MALEncodingContext ctx = new MALEncodingContext(header, operation, 0, qosProperties, qosProperties);

		if (header.getIsErrorMessage()) {
			return new TCPErrorBody(ctx, false, encFactory, encBodyElements);
		}

		if (InteractionType._PUBSUB_INDEX == header.getInteractionType().getOrdinal()) {
			final short stage = header.getInteractionStage().getValue();
			switch (stage) {
			case MALPubSubOperation._REGISTER_STAGE:
				return new TCPRegisterBody(ctx, false, encFactory, encBodyElements);
			case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
				return new TCPPublishRegisterBody(ctx, false, encFactory, encBodyElements);
			case MALPubSubOperation._PUBLISH_STAGE:
				return new TCPPublishBody(ctx, false, encFactory, encBodyElements);
			case MALPubSubOperation._NOTIFY_STAGE:
				return new TCPNotifyBody(ctx, false, encFactory, encBodyElements);
			case MALPubSubOperation._DEREGISTER_STAGE:
				return new TCPDeregisterBody(ctx, false, encFactory, encBodyElements);
			default:
				return new TCPMessageBody(ctx, false, encFactory, encBodyElements);
			}
		}

		return new TCPMessageBody(ctx, false, encFactory, encBodyElements);
	}

	private TCPMessageBody createMessageBody(final Object[] bodyElements) {
		MALEncodingContext ctx = new MALEncodingContext(header, operation, 0, qosProperties, qosProperties);

		if (header.getIsErrorMessage()) {
			return new TCPErrorBody(ctx, bodyElements);
		}

		if (InteractionType._PUBSUB_INDEX == header.getInteractionType().getOrdinal()) {
			final short stage = header.getInteractionStage().getValue();
			switch (stage) {
			case MALPubSubOperation._REGISTER_STAGE:
				return new TCPRegisterBody(ctx, bodyElements);
			case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
				return new TCPPublishRegisterBody(ctx, bodyElements);
			case MALPubSubOperation._PUBLISH_STAGE:
				return new TCPPublishBody(ctx, bodyElements);
			case MALPubSubOperation._NOTIFY_STAGE:
				return new TCPNotifyBody(ctx, bodyElements);
			case MALPubSubOperation._DEREGISTER_STAGE:
				return new TCPDeregisterBody(ctx, bodyElements);
			default:
				return new TCPMessageBody(ctx, bodyElements);
			}
		}

		return new TCPMessageBody(ctx, bodyElements);
	}
}
