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
package fr.cnes.malspp.encoding;

import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import fr.cnes.maljoram.encoding.Encoder;
import fr.cnes.maljoram.encoding.FineTimeEncoder;

public class CUCFineTimeEncoder implements FineTimeEncoder {

  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(CUCFineTimeEncoder.class.getName());

  public static final double PICO = Math.pow(10, 12);

  public static AbsoluteDate getJavaEpoch() {
    String epochAsString = System.getProperty(FineTimeEncoder.FINETIME_EPOCH,
        "2013-01-01T00:00:00.000");
    AbsoluteDate epoch = new AbsoluteDate(epochAsString,
        TimeScalesFactory.getTAI());
    return epoch;
  }

  private AbsoluteDate javaEpoch;

  private CUCTimeCode timeCode;

  public CUCFineTimeEncoder() {
    javaEpoch = getJavaEpoch();
  }

  public CUCFineTimeEncoder(CUCTimeCode timeCode) {
    this();
    this.timeCode = timeCode;
  }

  public CUCTimeCode getTimeCode() {
    return timeCode;
  }

  public void setTimeCode(CUCTimeCode timeCode) {
    this.timeCode = timeCode;
  }

  public void encode(long timeToEncode, Encoder encoder) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CUCFineTimeEncoder.encode(" + timeToEncode
          + ')');
    // Convert to seconds
    double elapsedDuration = ((double) timeToEncode) / PICO;
    CUCTimeEncoder.encodeCUC(elapsedDuration, encoder, timeCode, javaEpoch);
  }

  @Override
  public String toString() {
    return "CUCFineTimeEncoder [javaEpoch=" + javaEpoch + ", timeCode="
        + timeCode + "]";
  }

}
