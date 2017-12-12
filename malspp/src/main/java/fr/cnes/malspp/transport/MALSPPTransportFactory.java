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

import java.util.Hashtable;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.ccsds.moims.mo.mal.transport.MALTransportFactory;
import org.objectweb.util.monolog.api.Logger;

public class MALSPPTransportFactory extends MALTransportFactory {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(MALSPPTransportFactory.class.getName());
  
  private static Hashtable<MALContext, MALSPPTransport> transports = new Hashtable<MALContext, MALSPPTransport>();
  
  public MALSPPTransportFactory(String protocol) {
    super(protocol);
  }

  public MALTransport createTransport(MALContext malContext, Map properties) throws MALException {
    if (malContext != null) {
      synchronized (transports) {
        MALSPPTransport transport = transports.get(malContext);
        if (transport == null) {
          transport = new MALSPPTransport();
          transport.init(getProtocol(), properties);
          transports.put(malContext, transport);
        }
        return transport;
      }
    } else {
      MALSPPTransport transport = new MALSPPTransport();
      transport.init(getProtocol(), properties);
      return transport;
    }
  }
  
}
