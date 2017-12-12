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

import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

public class BrokerEntityRequest {
  
  private IdentifierList subDomain;
  
  private Boolean allAreas;
  
  private Boolean allServices;
  
  private Boolean allOperations;
  
  private EntityKey key;
  
  private Boolean onlyOnChange;

  public BrokerEntityRequest(IdentifierList subDomain, Boolean allAreas,
      Boolean allServices, Boolean allOperations, EntityKey key,
      Boolean onlyOnChange) {
    super();
    this.subDomain = subDomain;
    this.allAreas = allAreas;
    this.allServices = allServices;
    this.allOperations = allOperations;
    this.key = key;
    this.onlyOnChange = onlyOnChange;
  }

  public IdentifierList getSubDomain() {
    return subDomain;
  }

  public void setSubDomain(IdentifierList subDomain) {
    this.subDomain = subDomain;
  }

  public Boolean isAllAreas() {
    return allAreas;
  }

  public void setAllAreas(Boolean allAreas) {
    this.allAreas = allAreas;
  }

  public Boolean isAllServices() {
    return allServices;
  }

  public void setAllServices(Boolean allServices) {
    this.allServices = allServices;
  }

  public Boolean isAllOperations() {
    return allOperations;
  }

  public void setAllOperations(Boolean allOperations) {
    this.allOperations = allOperations;
  }

  public EntityKey getKey() {
    return key;
  }

  public void setKey(EntityKey key) {
    this.key = key;
  }

  public Boolean isOnlyOnChange() {
    return onlyOnChange;
  }

  public void setOnlyOnChange(Boolean onlyOnChange) {
    this.onlyOnChange = onlyOnChange;
  }

  public String toString() {
    return "BrokerEntityRequest [subDomain="
        + (subDomain != null ? Arrays.asList(subDomain) : null) + ", allAreas="
        + allAreas + ", allServices=" + allServices + ", allOperations="
        + allOperations + ", key=" + (key != null ? Arrays.asList(key) : null)
        + ", onlyOnChange=" + onlyOnChange + "]";
  }
}
