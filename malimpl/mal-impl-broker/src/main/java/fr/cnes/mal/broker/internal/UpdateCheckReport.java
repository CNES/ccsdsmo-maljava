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

import java.util.ArrayList;
import java.util.List;

import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;

public class UpdateCheckReport {

  private Long transactionId;
  
  private QoSLevel qos;
  
  private UInteger priority;
  
  private List[] updatesToNotify;
  
  private List[] failedUpdates;
  
  private UpdateHeaderList updateHeadersToNotify;
  
  private UpdateHeaderList failedUpdateHeaders;
  
  private List<EntityPublishContext> entityPublishContextList;

  public UpdateCheckReport(Long transactionId, QoSLevel qos, 
      UInteger priority, List[] updatesToNotify,
      List[] failedUpdates) {
    this.transactionId = transactionId;
    this.qos = qos;
    this.priority = priority;
    this.updatesToNotify = updatesToNotify;
    this.failedUpdates = failedUpdates;
    updateHeadersToNotify = new UpdateHeaderList();
    failedUpdateHeaders = new UpdateHeaderList();
    entityPublishContextList = new ArrayList<EntityPublishContext>();
  }
  
  public void addEntityPublishContext(EntityPublishContext entityPublishContext) {
    entityPublishContextList.add(entityPublishContext);
  }
  
  public void addUpdateHeaderToNotify(UpdateHeader updateHeader) {
    updateHeadersToNotify.add(updateHeader);
  }
  
  public void addUpdateToNotify(int updateListIndex, Object update) {
    updatesToNotify[updateListIndex].add(update);
  }
  
  public void addFailedUpdateHeader(UpdateHeader updateHeader) {
    failedUpdateHeaders.add(updateHeader);
  }
  
  public void addFailedUpdate(int updateListIndex, Object update) {
    failedUpdates[updateListIndex].add(update);
  }
  
  public List<EntityPublishContext> getEntityPublishContextList() {
    return entityPublishContextList;
  }

  public List[] getUpdatesToNotify() {
    return updatesToNotify;
  }
  
  public List[] getFailedUpdates() {
    return failedUpdates;
  }
  
  public UpdateHeaderList getUpdateHeadersToNotify() {
    return updateHeadersToNotify;
  }
  
  public UpdateHeaderList getFailedUpdateHeaders() {
    return failedUpdateHeaders;
  }
  
  public int getFailedUpdateCount() {
    return failedUpdateHeaders.size();
  }

  public Long getTransactionId() {
    return transactionId;
  }

  public QoSLevel getQos() {
    return qos;
  }

  public UInteger getPriority() {
    return priority;
  }
  
  public String toString() {
    return super.toString() +
    ",transactionId=" + transactionId +
    ",qos=" + qos +
    ",priority=" + priority +
    ",updatesToNotify=" + updatesToNotify +
    ",failedUpdates=" + failedUpdates + ')';
  }
}
