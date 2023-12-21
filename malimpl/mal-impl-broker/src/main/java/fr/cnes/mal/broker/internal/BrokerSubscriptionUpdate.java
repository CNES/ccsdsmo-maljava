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
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;

public class BrokerSubscriptionUpdate {
  
  private Identifier subscriptionId;
  
  private UpdateHeader updateHeader;
  
  private Object[] updateObjects;
  
  public BrokerSubscriptionUpdate(Identifier subscriptionId,
      Object[] updateObjects) {
    this.subscriptionId = subscriptionId;
    this.updateObjects = updateObjects;
  }
  
  public Identifier getSubscriptionId() {
    return subscriptionId;
  }
  
  public boolean alreadyPublished(UpdateHeader updateHeader) {
    return updateHeader.equals(this.updateHeader);
  }
  
  public void addUpdateHeader(UpdateHeader updateHeader) {
    this.updateHeader = updateHeader;
  }
  
  public void addUpdate(Object[] update) {
    updateObjects = update;
  }
  
  public UpdateHeader getUpdateHeader() {
    return updateHeader;
  }
  
  public Object[] getUpdateObjects() {
    return updateObjects;
  }

  @Override
  public String toString() {
    return "BrokerSubscriptionUpdate [subscriptionId=" + subscriptionId
        + ", updateHeader=" + updateHeader + ", updateObjects="
        + Arrays.toString(updateObjects) + "]";
  }
}
