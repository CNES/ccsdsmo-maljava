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

import java.util.StringTokenizer;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.URI;

public class MALSPPURI {
  
  public static final String PROTOCOL_NAME = "malspp";
  
  public static final String URI_PREFIX = PROTOCOL_NAME + ':';
  
  public static MALSPPURI parseURI(URI uri) throws MALException {
    // Check the protocol
    if (!uri.getValue().startsWith(URI_PREFIX)) {
      throw new MALException("Invalid protocol: " + uri);
    }
    
    String path = uri.getValue().substring(
        URI_PREFIX.length());
    
    if (path.charAt(0) == '/') {
      throw new MALException("Invalid root '/': " + uri);
    }
    
    if (path.charAt(path.length() - 1) == '/') {
      throw new MALException("Invalid ending '/': " + uri);
    }
    
    StringTokenizer st = new StringTokenizer(path, "/");

    // Get the APID qualifier
    String apidQualifierS = st.nextToken();
    int apidQualifier = Integer.parseInt(apidQualifierS);
    
    if (apidQualifier < 0
        || apidQualifier > MALSPPTransport.MAX_APID_QUALIFIER) {
      throw new MALException("Invalid API qualifier: "
          + apidQualifier);
    }

    // Get the APID
    String apidS = st.nextToken();
    int apid = Integer.parseInt(apidS);
    
    if (apid < 0
        || apid > MALSPPTransport.MAX_APID) {
      throw new MALException("Invalid API: "
          + apid);
    }

    Integer endpointId;
    if (st.hasMoreTokens()) {
      String endpointIdS = st.nextToken();
      endpointId = Integer.parseInt(endpointIdS);
    } else {
      endpointId = null;
    }
    
    if (endpointId != null && (endpointId < 0
        || endpointId > MALSPPTransport.MAX_ENDPOINT_ID)) {
      throw new MALException("Invalid endpoint id: "
          + endpointId);
    }
    
    return new MALSPPURI(PROTOCOL_NAME, apidQualifier, apid, endpointId);
  }
  
  private String protocol;
  
  private int apidQualifier;
  
  private int apid;
  
  private Integer endpointId;

  public MALSPPURI(String protocol, int apidQualifier, int apid, Integer endpointId) {
    super();
    this.protocol = protocol;
    this.apidQualifier = apidQualifier;
    this.apid = apid;
    this.endpointId = endpointId;
  }

  public String getProtocol() {
    return protocol;
  }

  public int getApidQualifier() {
    return apidQualifier;
  }

  public int getApid() {
    return apid;
  }

  public Integer getEndpointId() {
    return endpointId;
  }

  @Override
  public String toString() {
    return "MALSPPURI [protocol=" + protocol + ", apidQualifier="
        + apidQualifier + ", apid=" + apid + ", endpointId=" + endpointId + "]";
  }

}
