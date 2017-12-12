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

import fr.cnes.maljoram.encoding.Decoder;
import fr.cnes.maljoram.encoding.FineTimeDecoder;

public class CUCFineTimeDecoder implements FineTimeDecoder {

  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(CUCFineTimeDecoder.class.getName());
  
  private AbsoluteDate javaEpoch;
  
  private CUCTimeCode timeCode;
  
  public CUCFineTimeDecoder() {
    javaEpoch = CUCFineTimeEncoder.getJavaEpoch();
  }
  
  public CUCFineTimeDecoder(CUCTimeCode timeCode) {
    this();
    this.timeCode = timeCode;
  }
  
  public CUCTimeCode getTimeCode() {
    return timeCode;
  }

  public void setTimeCode(CUCTimeCode timeCode) {
    this.timeCode = timeCode;
  }

  public long decode(Decoder decoder) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CUCFineTimeDecoder.decode()");
    double elapsedDuration = CUCTimeDecoder.decodeCUC(decoder, timeCode, javaEpoch);
    return (long) (elapsedDuration * CUCFineTimeEncoder.PICO);
  }

  @Override
  public String toString() {
    return "CUCFineTimeDecoder [javaEpoch=" + javaEpoch + ", timeCode="
        + timeCode + "]";
  }

}