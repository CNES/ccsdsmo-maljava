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

import org.ccsds.moims.mo.mal.structures.SubscriptionFilter;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

// TODO SL: change the name? Entity -> Filter
public class BrokerEntityRequest {
  
  private IdentifierList subDomain;
  
  private Boolean allAreas;
  
  private Boolean allServices;
  
  private Boolean allOperations;
  
  private SubscriptionFilter filter;
  
  private Boolean onlyOnChange;

  public BrokerEntityRequest(IdentifierList subDomain, Boolean allAreas,
      Boolean allServices, Boolean allOperations, SubscriptionFilter filter,
      Boolean onlyOnChange) {
    super();
    this.subDomain = subDomain;
    this.allAreas = allAreas;
    this.allServices = allServices;
    this.allOperations = allOperations;
    this.filter = filter;
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

  public SubscriptionFilter getKey() {
    return filter;
  }

  public void setKey(SubscriptionFilter filter) {
    this.filter = filter;
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
        + allOperations + ", filter=" + filter
        + ", onlyOnChange=" + onlyOnChange + "]";
  }
}
