/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2018 CNES
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
package fr.cnes.encoding.base;

import org.ccsds.moims.mo.mal.MALException;

/**
 * Class used for binary time encoding.
 *
 * It uses CCSDS Day Segmented Time Code (CDS) with P-field assumed to be
 * "01000000" for Time and "01000010" for FineTime
 */
public class BinaryTimeEncoder implements TimeEncoder {
	/**
	 * Converts MAL seconds timestamp to CDS with CCSDS Epoch, 16 bit day segment,
	 * 32 bit ms of day segment, then writes it into the associated binary encoder.
	 */
	public void encode(long time, Encoder encoder) throws Exception {
		time += BinaryTime.MILLISECONDS_FROM_CCSDS_TO_UNIX_EPOCH;
		long days = time / BinaryTime.MILLISECONDS_IN_DAY;
		long millisecondsInDay = (time % BinaryTime.MILLISECONDS_IN_DAY);

		if (days > 65535) {
			// This check allows values bigger than maximum signed short, because the encoded value is an unsigned short
			throw new MALException("Overflow of unsigned 16-bit days when encoding MAL Time: " + days);
		}

		encoder.write16((short) days);
		encoder.write32((int) millisecondsInDay);
	}
}
