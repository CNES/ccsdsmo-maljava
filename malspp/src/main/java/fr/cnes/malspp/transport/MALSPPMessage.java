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
package fr.cnes.malspp.transport;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

public class MALSPPMessage implements MALMessage {

  private MALMessageHeader header;

  private MALMessageBody body;

  private Map qosProperties;

  public MALSPPMessage(MALMessageHeader header, MALMessageBody body,
      Map qosProperties) {
    super();
    this.header = header;
    this.body = body;
    this.qosProperties = qosProperties;
  }

  public MALMessageBody getBody() {
    return body;
  }

  public void acknowledge() throws MALException {
    // Nothing to do
  }

  public MALMessageHeader getHeader() {
    return header;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append('(');
    buffer.append(super.toString());
    buffer.append(",header=");
    buffer.append(header.toString());
    buffer.append(')');
    return buffer.toString();
  }

  public void free() throws MALException {
    // TODO Auto-generated method stub

  }

  public Map getQoSProperties() {
    return qosProperties;
  }
}
