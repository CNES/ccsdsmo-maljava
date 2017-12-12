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

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;

/**
 * Implementation of the MALErrorBody interface.
 */
public class TCPErrorBody extends TCPMessageBody implements MALErrorBody {
	private static final long serialVersionUID = 0L;

	/**
	 * Constructor.
	 *
	 * @param ctx			The encoding context to use.
	 * @param messageParts	The message parts that compose the body.
	 */
	public TCPErrorBody(final MALEncodingContext ctx, final Object[] messageParts) {
		super(ctx, messageParts);
	}

	/**
	 * Constructor.
	 *
	 * @param ctx				The encoding context to use.
	 * @param wrappedBodyParts	True if the encoded body parts are wrapped in BLOBs.
	 * @param encFactory		The encoder stream factory to use.
	 * @param encBodyElements	The input stream that holds the encoded body parts.
	 */
	public TCPErrorBody(final MALEncodingContext ctx,
			final boolean wrappedBodyParts,
			final MALElementStreamFactory encFactory,
			final MALElementInputStream encBodyElements) {
		super(ctx, wrappedBodyParts, encFactory, encBodyElements);
	}

	public MALStandardError getError() throws MALException {
		decodeMessageBody();

		if (1 < messageParts.length) {
			return new MALStandardError((UInteger) messageParts[0],
					messageParts[1]);
		} else {
			return new MALStandardError((UInteger) messageParts[0], null);
		}
	}
}
