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
  
  private Object[] updateToNotify;
  
  private Object[] failedUpdate;
  
  private UpdateHeader updateHeaderToNotify;
  
  private UpdateHeader failedUpdateHeader;
  
//  private List<EntityPublishContext> entityPublishContextList;
  private List<PublisherContext> publisherContextList;

  public UpdateCheckReport(Long transactionId) {
    this.transactionId = transactionId;
//    entityPublishContextList = new ArrayList<EntityPublishContext>();
    publisherContextList = new ArrayList<PublisherContext>();
  }
  
//  public void addEntityPublishContext(EntityPublishContext entityPublishContext) {
//    entityPublishContextList.add(entityPublishContext);
//  }
  public void addPublisherContext(PublisherContext publisherContext) {
    publisherContextList.add(publisherContext);
  }
  
  public void addUpdateHeaderToNotify(UpdateHeader updateHeader) {
    if (updateHeaderToNotify != null)
      throw new IllegalStateException("Unexpected existing value in UpdateCheckReport.updateHeaderToNotify");
    updateHeaderToNotify = updateHeader;
  }
  
  public void addUpdateToNotify(Object[] update) {
    if (updateToNotify != null)
      throw new IllegalStateException("Unexpected existing value in UpdateCheckReport.updateToNotify");
    updateToNotify = update;
  }
  
  public void addFailedUpdateHeader(UpdateHeader updateHeader) {
    if (failedUpdateHeader != null)
      throw new IllegalStateException("Unexpected existing value in UpdateCheckReport.failedUpdateHeader");
    failedUpdateHeader = updateHeader;
  }
  
  public void addFailedUpdate(Object[] update) {
    if (failedUpdate != null)
      throw new IllegalStateException("Unexpected existing value in UpdateCheckReport.failedUpdate");
    failedUpdate = update;
  }
  
//  public List<EntityPublishContext> getEntityPublishContextList() {
//    return entityPublishContextList;
//  }
  public List<PublisherContext> getPublisherContextList() {
    return publisherContextList;
  }

  public Object[] getUpdateToNotify() {
    return updateToNotify;
  }
  
  public Object[] getFailedUpdate() {
    return failedUpdate;
  }
  
  public UpdateHeader getUpdateHeaderToNotify() {
    return updateHeaderToNotify;
  }
  
  public UpdateHeader getFailedUpdateHeader() {
    return failedUpdateHeader;
  }
  
  /**
   * @deprecated
   */
  public int getFailedUpdateCount() {
    return 1;
  }

  public Long getTransactionId() {
    return transactionId;
  }

  public String toString() {
    return super.toString() +
    ",transactionId=" + transactionId +
    ",updateToNotify=" + updateToNotify +
    ",failedUpdate=" + failedUpdate + ')';
  }
}
