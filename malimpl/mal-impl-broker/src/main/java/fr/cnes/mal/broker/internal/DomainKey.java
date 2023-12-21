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
  // TODO SL unsure whether null domain differs from empty domain
  // TODO SL je pense que le domaine ne devrait pas faire partie de la key
  private IdentifierList domain;
  // TODO SL keep area version here to be confirmed
  private UOctet areaVersion;
  
  private void readObject(java.io.ObjectInputStream is)
      throws IOException, ClassNotFoundException {
    area = new UShort(is.readInt());
    service = new UShort(is.readInt());
    operation = new UShort(is.readInt());
    areaVersion = new UOctet(is.readShort());
  }
  
  private void writeObject(java.io.ObjectOutputStream os)
      throws IOException {
    os.writeInt(area.getValue());
    os.writeInt(service.getValue());
    os.writeInt(operation.getValue());
    os.writeShort(areaVersion.getValue());
  }

  public DomainKey(UShort area, UShort service, UShort operation,
      IdentifierList domain, UOctet areaVersion) {
    super();
    this.area = area;
    this.service = service;
    this.operation = operation;
    this.domain = domain;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((area == null) ? 0 : area.hashCode());
    result = prime * result
        + ((areaVersion == null) ? 0 : areaVersion.hashCode());
    result = prime * result + ((operation == null) ? 0 : operation.hashCode());
    result = prime * result + ((service == null) ? 0 : service.hashCode());
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
    return true;
  }

  @Override
  public String toString() {
    return "DomainKey [area=" + area + ", service=" + service + ", operation="
        + operation + ", domain=" + domain + ", areaVersion=" + areaVersion + "]";
  }
  
}