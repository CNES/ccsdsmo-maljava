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

/**
 * Class used for binary time decoding
 *
 * It uses CCSDS Day Segmented Time Code (CDS) with P-field assumed to be
 * "01000000" for Time and "01000010" for FineTime
 */
public class BinaryFineTimeDecoder implements FineTimeDecoder {
	/**
	 * Reads CDS with CCSDS Epoch, 16 bit day segment, 32 bit ms of day segment,
	 * 32 bit sub-ms segment, then converts it into a MAL nanoseconds timestamp
	 */
	public long decode(Decoder decoder) throws Exception {
		long days = ((long) decoder.read16()) & 0xFFFFL;
		long millisecondsInDay = ((long) decoder.read32()) & 0xFFFFFFFFL;
		long picosecondsInMillisecond = ((long) decoder.read32()) & 0xFFFFFFFFL;
		long time = days * BinaryTime.NANOSECONDS_IN_DAY;
		time += millisecondsInDay * BinaryTime.ONE_MILLION;
		time += picosecondsInMillisecond / 1000;
		time -= BinaryTime.NANOSECONDS_FROM_CCSDS_TO_UNIX_EPOCH;
		return time;
	}
}
