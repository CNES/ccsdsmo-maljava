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

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;

public class SegmentationKey {
  
  private InteractionType type;
  
  private Long transactionId;
  
  private URI uriFrom;
  
  private URI uriTo;
  
  private SessionType session;
  
  private Identifier sessionName;
  
  private IdentifierList domain;
  
  private Identifier networkZone;
  
  private UShort serviceArea;
  
  private UShort service;
  
  private UShort operation;

  public SegmentationKey(InteractionType type, Long transactionId, URI uriFrom,
      URI uriTo, SessionType session, Identifier sessionName,
      IdentifierList domain, Identifier networkZone, UShort serviceArea,
      UShort service, UShort operation) {
    super();
    this.type = type;
    this.transactionId = transactionId;
    this.uriFrom = uriFrom;
    this.uriTo = uriTo;
    this.session = session;
    this.sessionName = sessionName;
    this.domain = domain;
    this.networkZone = networkZone;
    this.serviceArea = serviceArea;
    this.service = service;
    this.operation = operation;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((domain == null) ? 0 : domain.hashCode());
    result = prime * result
        + ((networkZone == null) ? 0 : networkZone.hashCode());
    result = prime * result + ((operation == null) ? 0 : operation.hashCode());
    result = prime * result + ((service == null) ? 0 : service.hashCode());
    result = prime * result
        + ((serviceArea == null) ? 0 : serviceArea.hashCode());
    result = prime * result + ((session == null) ? 0 : session.hashCode());
    result = prime * result
        + ((sessionName == null) ? 0 : sessionName.hashCode());
    result = prime * result
        + ((transactionId == null) ? 0 : transactionId.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((uriFrom == null) ? 0 : uriFrom.hashCode());
    result = prime * result + ((uriTo == null) ? 0 : uriTo.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SegmentationKey other = (SegmentationKey) obj;
    if (domain == null) {
      if (other.domain != null)
        return false;
    } else if (!domain.equals(other.domain))
      return false;
    if (networkZone == null) {
      if (other.networkZone != null)
        return false;
    } else if (!networkZone.equals(other.networkZone))
      return false;
    if (operation == null) {
      if (other.operation != null)
        return false;
    } else if (!operation.equals(other.operation))
      return false;
    if (service == null) {
      if (other.service != null)
        return false;
    } else if (!service.equals(other.service))
      return false;
    if (serviceArea == null) {
      if (other.serviceArea != null)
        return false;
    } else if (!serviceArea.equals(other.serviceArea))
      return false;
    if (session == null) {
      if (other.session != null)
        return false;
    } else if (!session.equals(other.session))
      return false;
    if (sessionName == null) {
      if (other.sessionName != null)
        return false;
    } else if (!sessionName.equals(other.sessionName))
      return false;
    if (transactionId == null) {
      if (other.transactionId != null)
        return false;
    } else if (!transactionId.equals(other.transactionId))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (uriFrom == null) {
      if (other.uriFrom != null)
        return false;
    } else if (!uriFrom.equals(other.uriFrom))
      return false;
    if (uriTo == null) {
      if (other.uriTo != null)
        return false;
    } else if (!uriTo.equals(other.uriTo))
      return false;
    return true;
  }
  
}