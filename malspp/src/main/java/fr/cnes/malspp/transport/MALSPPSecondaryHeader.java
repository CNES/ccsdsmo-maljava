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

import java.util.Arrays;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

public class MALSPPSecondaryHeader {
  
  private int malsppVersion;
  
  private int sduType;
  
  private int area;
  
  private int service;
  
  private int operation;
  
  private int areaVersion;
  
  private int isError;
  
  private int qos;
  
  private int session;
  
  private int secondaryApid;
  
  private int secondaryApidQualifier;
  
  private long transactionId;
  
  private byte sourceIdFlag;
  
  private byte destinationIdFlag;
  
  private byte priorityFlag;
  
  private byte timestampFlag;
  
  private byte networkZoneFlag;
  
  private byte sessionNameFlag;
  
  private byte domainFlag;
  
  private byte authenticationIdFlag;
 
  private Integer sourceId;
  
  private Integer destinationId;
  
  private long segmentCounter;
  
  private long priority;
  
  private long timestamp;
  
  private Identifier networkZone;
  
  private Identifier sessionName;
  
  private IdentifierList domain;
  
  private byte[] authenticationId;

  public int getMalsppVersion() {
    return malsppVersion;
  }

  public void setMalsppVersion(int malsppVersion) {
    this.malsppVersion = malsppVersion;
  }

  /**
   * @return the sduType
   */
  public int getSduType() {
    return sduType;
  }

  /**
   * @param sduType the sduType to set
   */
  public void setSduType(int sduType) {
    this.sduType = sduType;
  }

  /**
   * @return the area
   */
  public int getArea() {
    return area;
  }

  /**
   * @param area the area to set
   */
  public void setArea(int area) {
    this.area = area;
  }

  /**
   * @return the areaVersion
   */
  public int getAreaVersion() {
    return areaVersion;
  }

  /**
   * @param serviceVersion the areaVersion to set
   */
  public void setAreaVersion(int areaVersion) {
    this.areaVersion = areaVersion;
  }

  /**
   * @return the service
   */
  public int getService() {
    return service;
  }

  /**
   * @param service the service to set
   */
  public void setService(int service) {
    this.service = service;
  }

  public int getQos() {
    return qos;
  }

  public void setQos(int qos) {
    this.qos = qos;
  }

  public int getSession() {
    return session;
  }

  public void setSession(int session) {
    this.session = session;
  }

  public int getSecondaryApid() {
    return secondaryApid;
  }

  public void setSecondaryApid(int secondaryApid) {
    this.secondaryApid = secondaryApid;
  }

  public int getSecondaryApidQualifier() {
    return secondaryApidQualifier;
  }

  public void setSecondaryApidQualifier(int secondaryApidQualifier) {
    this.secondaryApidQualifier = secondaryApidQualifier;
  }

  public byte getSourceIdFlag() {
    return sourceIdFlag;
  }

  public void setSourceIdFlag(byte sourceIdFlag) {
    this.sourceIdFlag = sourceIdFlag;
  }

  public byte getDestinationIdFlag() {
    return destinationIdFlag;
  }

  public void setDestinationIdFlag(byte destinationIdFlag) {
    this.destinationIdFlag = destinationIdFlag;
  }

  public byte getPriorityFlag() {
    return priorityFlag;
  }

  public void setPriorityFlag(byte priorityFlag) {
    this.priorityFlag = priorityFlag;
  }

  public byte getTimestampFlag() {
    return timestampFlag;
  }

  public void setTimestampFlag(byte timestampFlag) {
    this.timestampFlag = timestampFlag;
  }

  public byte getNetworkZoneFlag() {
    return networkZoneFlag;
  }

  public void setNetworkZoneFlag(byte networkZoneFlag) {
    this.networkZoneFlag = networkZoneFlag;
  }

  public byte getSessionNameFlag() {
    return sessionNameFlag;
  }

  public void setSessionNameFlag(byte sessionNameFlag) {
    this.sessionNameFlag = sessionNameFlag;
  }

  public byte getDomainFlag() {
    return domainFlag;
  }

  public void setDomainFlag(byte domainFlag) {
    this.domainFlag = domainFlag;
  }

  public byte getAuthenticationIdFlag() {
    return authenticationIdFlag;
  }

  public void setAuthenticationIdFlag(byte authenticationIdFlag) {
    this.authenticationIdFlag = authenticationIdFlag;
  }

  public Integer getSourceId() {
    return sourceId;
  }

  public void setSourceId(Integer sourceId) {
    this.sourceId = sourceId;
  }

  public Integer getDestinationId() {
    return destinationId;
  }

  public void setDestinationId(Integer destinationId) {
    this.destinationId = destinationId;
  }

  /**
   * @return the operation
   */
  public int getOperation() {
    return operation;
  }

  /**
   * @param operation the operation to set
   */
  public void setOperation(int operation) {
    this.operation = operation;
  }

  /**
   * @return the transactionId
   */
  public long getTransactionId() {
    return transactionId;
  }

  /**
   * @param transactionId the transactionId to set
   */
  public void setTransactionId(long transactionId) {
    this.transactionId = transactionId;
  }

  public int getIsError() {
    return isError;
  }

  public void setIsError(int isError) {
    this.isError = isError;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  
  public long getPriority() {
    return priority;
  }

  public void setPriority(long priority) {
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

  public byte[] getAuthenticationId() {
    return authenticationId;
  }

  public void setAuthenticationId(byte[] authenticationId) {
    this.authenticationId = authenticationId;
  }

  public long getSegmentCounter() {
    return segmentCounter;
  }

  public void setSegmentCounter(long segmentCounter) {
    this.segmentCounter = segmentCounter;
  }

  @Override
  public String toString() {
    return "MALSPPSecondaryHeader [malsppVersion=" + malsppVersion
        + ", sduType=" + sduType + ", area=" + area + ", service=" + service
        + ", operation=" + operation + ", areaVersion=" + areaVersion
        + ", isError=" + isError + ", qos=" + qos + ", session=" + session
        + ", secondaryApid=" + secondaryApid + ", secondaryApidQualifier="
        + secondaryApidQualifier + ", transactionId=" + transactionId
        + ", sourceIdFlag=" + sourceIdFlag + ", destinationIdFlag="
        + destinationIdFlag + ", priorityFlag=" + priorityFlag
        + ", timestampFlag=" + timestampFlag + ", networkZoneFlag="
        + networkZoneFlag + ", sessionNameFlag=" + sessionNameFlag
        + ", domainFlag=" + domainFlag + ", authenticationIdFlag="
        + authenticationIdFlag + ", sourceId=" + sourceId + ", destinationId="
        + destinationId + ", segmentCounter=" + segmentCounter + ", priority="
        + priority + ", timestamp=" + timestamp + ", networkZone="
        + networkZone + ", sessionName=" + sessionName + ", domain=" + domain
        + ", authenticationId=" + Arrays.toString(authenticationId) + "]";
  }

}
