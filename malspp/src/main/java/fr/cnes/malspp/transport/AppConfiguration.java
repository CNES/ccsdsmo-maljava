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

import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;

import fr.cnes.maljoram.encoding.DurationDecoder;
import fr.cnes.maljoram.encoding.DurationEncoder;
import fr.cnes.maljoram.encoding.FineTimeDecoder;
import fr.cnes.maljoram.encoding.FineTimeEncoder;
import fr.cnes.maljoram.encoding.TimeDecoder;
import fr.cnes.maljoram.encoding.TimeEncoder;
import fr.cnes.maljoram.malencoding.JORAMElementStreamFactory;

public class AppConfiguration {
  
  public static final int MAX_PACKET_DATA_FIELD_SIZE_LIMIT = 65536;
  
  private int packetDataFieldSizeLimit;
  
  private TimeEncoder timeEncoder;
  
  private TimeDecoder timeDecoder;
  
  private FineTimeEncoder fineTimeEncoder;
  
  private FineTimeDecoder fineTimeDecoder;
  
  private DurationEncoder durationEncoder;
  
  private DurationDecoder durationDecoder;
  
  private UInteger priority;
  
  private Identifier networkZone;
  
  private Identifier sessionName;
  
  private IdentifierList domain;
  
  private Blob authenticationId;
  
  private JORAMElementStreamFactory elementStreamFactory;
  
  public AppConfiguration(JORAMElementStreamFactory elementStreamFactory) {
    this.elementStreamFactory = elementStreamFactory;
    packetDataFieldSizeLimit = MAX_PACKET_DATA_FIELD_SIZE_LIMIT;
    authenticationId = new Blob(new byte[0]);
    priority = new UInteger(0);
    networkZone = new Identifier("");
    sessionName = new Identifier("");
    domain = new IdentifierList();
  }

  public UInteger getPriority() {
    return priority;
  }

  public void setPriority(UInteger priority) {
    this.priority = priority;
  }

  public Identifier getNetworkZone() {
    return networkZone;
  }

  public void setNetworkZone(Identifier networkZone) {
    this.networkZone = networkZone;
  }

  public Identifier getSessionName() {
    return sessionName;
  }

  public void setSessionName(Identifier sessionName) {
    this.sessionName = sessionName;
  }

  public IdentifierList getDomain() {
    return domain;
  }

  public void setDomain(IdentifierList domain) {
    this.domain = domain;
  }

  public Blob getAuthenticationId() {
    return authenticationId;
  }

  public void setAuthenticationId(Blob authenticationId) {
    this.authenticationId = authenticationId;
  }

  public int getPacketDataFieldSizeLimit() {
    return packetDataFieldSizeLimit;
  }

  public void setPacketDataFieldSizeLimit(int limit) {
    if (limit == 0) {
      this.packetDataFieldSizeLimit = MAX_PACKET_DATA_FIELD_SIZE_LIMIT;
    } else if (limit < MAX_PACKET_DATA_FIELD_SIZE_LIMIT) {
      this.packetDataFieldSizeLimit = limit;
    } else {
      throw new RuntimeException("PacketDataFieldSize too big: " + limit);
    }
  }

  public TimeEncoder getTimeEncoder() {
    return timeEncoder;
  }

  public void setTimeEncoder(TimeEncoder timeEncoder) {
    this.timeEncoder = timeEncoder;
  }

  public TimeDecoder getTimeDecoder() {
    return timeDecoder;
  }

  public void setTimeDecoder(TimeDecoder timeDecoder) {
    this.timeDecoder = timeDecoder;
  }

  public FineTimeEncoder getFineTimeEncoder() {
    return fineTimeEncoder;
  }

  public void setFineTimeEncoder(FineTimeEncoder fineTimeEncoder) {
    this.fineTimeEncoder = fineTimeEncoder;
  }

  public FineTimeDecoder getFineTimeDecoder() {
    return fineTimeDecoder;
  }

  public void setFineTimeDecoder(FineTimeDecoder fineTimeDecoder) {
    this.fineTimeDecoder = fineTimeDecoder;
  }

  public DurationEncoder getDurationEncoder() {
    return durationEncoder;
  }

  public void setDurationEncoder(DurationEncoder durationEncoder) {
    this.durationEncoder = durationEncoder;
  }

  public DurationDecoder getDurationDecoder() {
    return durationDecoder;
  }

  public void setDurationDecoder(DurationDecoder durationDecoder) {
    this.durationDecoder = durationDecoder;
  }

  public boolean isVarintSupported() {
    return elementStreamFactory.isVarintSupported();
  }

  public void setVarintSupported(boolean varintSupported) {
    elementStreamFactory.setVarintSupported(varintSupported);
  }
  
  public MALElementStreamFactory getElementStreamFactory() {
    return elementStreamFactory;
  }

  @Override
  public String toString() {
    return "QosConfiguration [packetDataFieldSizeLimit="
        + packetDataFieldSizeLimit + ", timeEncoder=" + timeEncoder
        + ", timeDecoder=" + timeDecoder + ", fineTimeEncoder="
        + fineTimeEncoder + ", fineTimeDecoder=" + fineTimeDecoder
        + ", durationEncoder=" + durationEncoder + ", durationDecoder="
        + durationDecoder + ", priority=" + priority + ", networkZone="
        + networkZone + ", sessionName=" + sessionName + ", domain=" + domain
        + ", authenticationId=" + authenticationId + ", varintSupported="
        + elementStreamFactory.isVarintSupported() + "]";
  }
  
}
