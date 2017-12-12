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
package fr.cnes.ccsds.mo.transport.tcp.body;

import java.util.List;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALEncodedElement;
import org.ccsds.moims.mo.mal.transport.MALPublishBody;

/**
 * Implementation of the MALPublishBody interface.
 */
public class TCPPublishBody extends TCPMessageBody implements MALPublishBody {
	private static final long serialVersionUID = 0L;
	private final int offset;
	private UpdateHeaderList hdrList = null;

	/**
	 * Constructor.
	 *
	 * @param ctx			The encoding context to use.
	 * @param messageParts	The message parts that compose the body.
	 */
	public TCPPublishBody(final MALEncodingContext ctx, final Object[] messageParts) {
		super(ctx, messageParts);
		offset = 0;
	}

	/**
	 * Constructor.
	 *
	 * @param ctx			The encoding context to use.
	 * @param messageParts	The message parts that compose the body.
	 * @param offset		The offset in the message parts where the updates start.
	 */
	public TCPPublishBody(final MALEncodingContext ctx, final Object[] messageParts, final int offset) {
		super(ctx, messageParts);
		this.offset = offset;
	}

	/**
	 * Constructor.
	 *
	 * @param ctx				The encoding context to use.
	 * @param wrappedBodyParts	True if the encoded body parts are wrapped in BLOBs.
	 * @param encFactory		The encoder stream factory to use.
	 * @param encBodyElements	The input stream that holds the encoded body parts.
	 */
	public TCPPublishBody(
			final MALEncodingContext ctx,
			final boolean wrappedBodyParts,
			final MALElementStreamFactory encFactory,
			final MALElementInputStream encBodyElements) {
		super(ctx, wrappedBodyParts, encFactory, encBodyElements);
		offset = 0;
	}

	/**
	 * Constructor.
	 *
	 * @param ctx				The encoding context to use.
	 * @param wrappedBodyParts	True if the encoded body parts are wrapped in BLOBs.
	 * @param encFactory		The encoder stream factory to use.
	 * @param encBodyElements	The input stream that holds the encoded body parts.
	 * @param offset			The offset in the message parts where the updates start.
	 */
	public TCPPublishBody(
			final MALEncodingContext ctx,
			final boolean wrappedBodyParts,
			final MALElementStreamFactory encFactory,
			final MALElementInputStream encBodyElements, final int offset) {
		super(ctx, wrappedBodyParts, encFactory, encBodyElements);
		this.offset = offset;
	}

	public int getUpdateCount() throws MALException {
		if (null == hdrList) {
			getUpdateHeaderList();
		}
		return hdrList.size();
	}

	public UpdateHeaderList getUpdateHeaderList() throws MALException {
		hdrList = (UpdateHeaderList) getBodyElement(offset,
				new UpdateHeaderList());
		return hdrList;
	}

	public List getUpdateList(final int listIndex, final List updateList)
			throws MALException {
		return (List) getBodyElement(offset + listIndex + 1, updateList);
	}

	public List[] getUpdateLists(final List... updateLists) throws MALException {
		decodeMessageBody();

		final List[] rv = new List[messageParts.length - offset - 1];

		for (int i = 0; i < rv.length; i++) {
			rv[i] = (List) messageParts[i + offset + 1];
		}
		return rv;
	}

	public Object getUpdate(final int listIndex, final int updateIndex)
			throws MALException {
		decodeMessageBody();

		return ((List) (messageParts[offset + 1 + listIndex])).get(updateIndex);
	}

	public MALEncodedElement getEncodedUpdate(final int listIndex, final int updateIndex) throws MALException {
		// TODO:
		throw new MALException("Not supported yet.");
	}
}
