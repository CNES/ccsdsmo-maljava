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

import java.util.Arrays;
import java.util.List;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;

// TODO SL move to v2 single update
// je traite uniquement les Lists[0]
// TODO SL move to v2 Identifier from
public class BrokerPublication {
  
  private Identifier uriFrom;
  
  private UpdateHeader updateHeader;
  
  private Object[] updateObjects;
  
  private UShort area;
  
  private UShort service;
  
  private UShort operation;
  
  private UOctet version;

  public BrokerPublication(Identifier uriFrom,
      UpdateHeader updateHeader,
      Object[] updateObjects, UShort area,
      UShort service, UShort operation, UOctet version) {
    super();
    this.uriFrom = uriFrom;
    this.updateHeader = updateHeader;
    this.updateObjects = updateObjects;
    this.area = area;
    this.service = service;
    this.operation = operation;
    this.version = version;
  }

  public Identifier getUriFrom() {
    return uriFrom;
  }

  public UpdateHeader getUpdateHeader() {
    return updateHeader;
  }

  public Object[] getUpdateObjects() {
    return updateObjects;
  }
  
  public int getUpdateListCount() {
    return 1;
  }

  public UShort getArea() {
    return area;
  }

  public UShort getService() {
    return service;
  }

  public UShort getOperation() {
    return operation;
  }

  public UOctet getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return "BrokerPublication [uriFrom=" + uriFrom + ", updateHeader=" + updateHeader
        + ", updateObjects=" + Arrays.toString(updateObjects) + ", area=" + area
        + ", service=" + service + ", operation=" + operation + ", version="
        + version + "]";
  }
}
