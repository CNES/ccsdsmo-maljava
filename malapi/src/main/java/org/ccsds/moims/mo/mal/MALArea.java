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
package org.ccsds.moims.mo.mal;

import java.util.Vector;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * A {@code MALArea} represents the specification of an area of services.
 */
public class MALArea {
  
  private UShort number;
  
  private Identifier name;
  
  private UOctet version;
  
  private Vector<MALService> services;
  
  /**
   * Constructs an area description with the specified number, name and version.
   * @param number the number of the area
   * @param name the name of the area
   * @param version the version of the area
   * @throws IllegalArgumentException if the number or the name is null
   */
  public MALArea(UShort number, Identifier name, UOctet version) throws IllegalArgumentException {
    if (number == null) throw new IllegalArgumentException("Null number");
    if (name == null) throw new IllegalArgumentException("Null name");
    if (version == null) throw new IllegalArgumentException("Null version");
    this.number = number;
    this.name = name;
    this.version = version;
    services = new Vector<MALService>();
  }
  
  /**
   * Returns the number of the area.
   * @return the number of the area
   */
  public final UShort getNumber() {
    return number;
  }
  
  /**
   * Returns the name of the area.
   * @return the name of the area
   */
  public final Identifier getName() {
    return name;
  }
  
  /**
   * Returns the version of the service
   * @return the version of the service
   */
  public UOctet getVersion() {
    return version;
  }
  
  /**
   * Returns the services owned by this area.
   * @return the services owned by this area.
   */
  public final MALService[] getServices() {
    MALService[] res = new MALService[services.size()];
    services.copyInto(res);
    return res;
  }
  
  /**
   * Adds a service to this area.
   * @param service the service to be added
   * @throws IllegalArgumentException if the service is null
   * @throws MALException if a service with the same name or number 
   * is already owned by the area
   */
  public void addService(MALService service) throws IllegalArgumentException, MALException {
    if (service == null) {
      throw new IllegalArgumentException("Null service");
    }
    if (getServiceByNumber(service.getNumber()) != null) {
      throw new MALException("Already added service: number=" + service.getNumber());
    }
    if (getServiceByName(service.getName()) != null) {
      throw new MALException("Already added service: name=" + service.getName());
    }
    service.setArea(this);
    services.addElement(service);
  }
  
  /**
   * Returns the service with the specified name.
   * @param serviceName name of the service
   * @return the service with the specified name
   */
  public MALService getServiceByName(Identifier serviceName) {
    for (int i = 0; i < services.size(); i++) {
      MALService s = services.elementAt(i);
      if (s.getName().equals(serviceName)) {
        return s;
      }
    }
    return null;
  }
  
  /**
   * Returns the service with the specified number.
   * @param serviceNumber 
   * @return the service with the specified number.
   */
  public MALService getServiceByNumber(UShort serviceNumber) {
    for (int i = 0; i < services.size(); i++) {
      MALService s = services.elementAt(i);
      if (s.getNumber().getValue() == serviceNumber.getValue()) {
        return s;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return "MALArea [number=" + number + ", name=" + name + ", version="
        + version + "]";
  }
}
