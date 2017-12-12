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
package fr.cnes.mal.broker.internal;

import java.io.IOException;
import java.io.Serializable;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;

import fr.dyade.aaa.common.Strings;

class DomainKey implements Serializable {
  private UShort area;
  private UShort service;
  private UShort operation;
  private IdentifierList domain;
  private Identifier networkZone;
  private SessionType sessionType;
  private Identifier sessionName;
  private UOctet areaVersion;
  
  private void readObject(java.io.ObjectInputStream is)
      throws IOException, ClassNotFoundException {
    area = new UShort(is.readInt());
    service = new UShort(is.readInt());
    operation = new UShort(is.readInt());
    int domainLength = is.readInt();
    domain = new IdentifierList(domainLength);
    for (int i = 0; i < domainLength; i++) {
      domain.add(new Identifier(is.readUTF()));
    }
    networkZone = new Identifier(is.readUTF());
    sessionType = SessionType.fromOrdinal(is.readInt());
    boolean nullSessionName = is.readBoolean();
    if (nullSessionName) {
      sessionName = null;
    } else {
      sessionName = new Identifier(is.readUTF());
    }
    areaVersion = new UOctet(is.readShort());
  }
  
  private void writeObject(java.io.ObjectOutputStream os)
      throws IOException {
    os.writeInt(area.getValue());
    os.writeInt(service.getValue());
    os.writeInt(operation.getValue());
    os.writeInt(domain.size());
    for (int i = 0; i < domain.size(); i++) {
      os.writeUTF(domain.get(i).getValue());
    }
    os.writeUTF(networkZone.getValue());
    os.writeInt(sessionType.getOrdinal());
    if (sessionName == null) {
      os.writeBoolean(true);
    } else {
      os.writeBoolean(false);
      os.writeUTF(sessionName.getValue());
    }
    os.writeShort(areaVersion.getValue());
  }

  public DomainKey(UShort area, UShort service, UShort operation,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, UOctet areaVersion) {
    super();
    this.area = area;
    this.service = service;
    this.operation = operation;
    this.domain = domain;
    this.networkZone = networkZone;
    this.sessionType = sessionType;
    this.sessionName = sessionName;
    this.areaVersion = areaVersion;
  }

  public UShort getArea() {
    return area;
  }

  public UOctet getAreaVersion() {
    return areaVersion;
  }

  public UShort getService() {
    return service;
  }

  public UShort getOperation() {
    return operation;
  }

  public IdentifierList getDomain() {
    return domain;
  }

  public Identifier getNetworkZone() {
    return networkZone;
  }

  public SessionType getSessionType() {
    return sessionType;
  }

  public Identifier getSessionName() {
    return sessionName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((area == null) ? 0 : area.hashCode());
    result = prime * result
        + ((areaVersion == null) ? 0 : areaVersion.hashCode());
    result = prime * result + ((domain == null) ? 0 : domain.hashCode());
    result = prime * result
        + ((networkZone == null) ? 0 : networkZone.hashCode());
    result = prime * result + ((operation == null) ? 0 : operation.hashCode());
    result = prime * result + ((service == null) ? 0 : service.hashCode());
    result = prime * result
        + ((sessionName == null) ? 0 : sessionName.hashCode());
    result = prime * result
        + ((sessionType == null) ? 0 : sessionType.hashCode());
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
    DomainKey other = (DomainKey) obj;
    if (area == null) {
      if (other.area != null)
        return false;
    } else if (!area.equals(other.area))
      return false;
    if (areaVersion == null) {
      if (other.areaVersion != null)
        return false;
    } else if (!areaVersion.equals(other.areaVersion))
      return false;
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
    if (sessionName == null) {
      if (other.sessionName != null)
        return false;
    } else if (!sessionName.equals(other.sessionName))
      return false;
    if (sessionType == null) {
      if (other.sessionType != null)
        return false;
    } else if (!sessionType.equals(other.sessionType))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "DomainKey [area=" + area + ", service=" + service + ", operation="
        + operation + ", domain=" + domain + ", networkZone=" + networkZone
        + ", sessionType=" + sessionType + ", sessionName=" + sessionName
        + ", areaVersion=" + areaVersion + "]";
  }
  
}