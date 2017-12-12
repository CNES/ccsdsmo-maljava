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
import java.util.ArrayList;
import java.util.List;

import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;

public class EntityPublishContext implements Serializable, EntityPublishContextMBean {

  private PublisherContext publisherContext;

  private EntityKey pattern;
  
  private List<EntityRequestContext> checkedEntityRequestContexts;
  
  private List<EntityRequestContext> uncheckedEntityRequestContexts;

  private void readObject(java.io.ObjectInputStream is) throws IOException,
      ClassNotFoundException {
    publisherContext = (PublisherContext) is.readObject();
    Identifier id1 = new Identifier(is.readUTF());
    Long id2 = (Long) is.readObject();
    Long id3 = (Long) is.readObject();
    Long id4 = (Long) is.readObject();
    pattern = new EntityKey(id1, id2, id3, id4);
    checkedEntityRequestContexts = (ArrayList<EntityRequestContext>) is.readObject();
    uncheckedEntityRequestContexts = (ArrayList<EntityRequestContext>) is.readObject();
  }

  private void writeObject(java.io.ObjectOutputStream os) throws IOException {
    os.writeObject(publisherContext);
    os.writeUTF(pattern.getFirstSubKey().getValue());
    os.writeObject(pattern.getSecondSubKey());
    os.writeObject(pattern.getThirdSubKey());
    os.writeObject(pattern.getFourthSubKey());
    os.writeObject(checkedEntityRequestContexts);
    os.writeObject(uncheckedEntityRequestContexts);
  }

  public EntityPublishContext(PublisherContext publisherContext,
      EntityKey pattern) {
    super();
    this.publisherContext = publisherContext;
    this.pattern = pattern;
    checkedEntityRequestContexts = new ArrayList<EntityRequestContext>();
    uncheckedEntityRequestContexts = new ArrayList<EntityRequestContext>();
  }

  public PublisherContext getPublisherContext() {
    return publisherContext;
  }

  public List<EntityRequestContext> getEntityRequestContexts() {
    return checkedEntityRequestContexts;
  }

  public List<EntityRequestContext> getUncheckedEntityRequestContexts() {
    return uncheckedEntityRequestContexts;
  }

  public boolean match(EntityKey updateKey) {
    return MALPattern.match(updateKey, pattern);
  }
  
  private boolean alreadyRegistered(SubscriptionContext subscriptionCtx, List<EntityRequestContext> contexts) {
    for (EntityRequestContext erc : contexts) {
      if (erc.getSubscriptionContext() == subscriptionCtx) {
        return true;
      }
    }
    return false;
  }

  public boolean checkEntityRequestEquals(EntityRequestContext entityRequestContext) {
    if (alreadyRegistered(entityRequestContext.getSubscriptionContext(), checkedEntityRequestContexts)) return true;
    if (entityRequestContext.getPattern().equals(pattern)) {
      checkedEntityRequestContexts.add(entityRequestContext);
      return true;
    } else {
      return false;
    }
  }
    
  public void checkEntityRequestIncludes(EntityRequestContext entityRequestContext) {
    if (alreadyRegistered(entityRequestContext.getSubscriptionContext(), checkedEntityRequestContexts)) return;
    if (MALPattern.includes(entityRequestContext.getPattern(), pattern)) {
      checkedEntityRequestContexts.add(entityRequestContext);
    }
  }
  
  public void checkEntityRequestPotentialMatch(EntityRequestContext entityRequestContext) {
    if (MALPattern.potentialMatch(entityRequestContext.getPattern(), pattern)) {
      uncheckedEntityRequestContexts.add(entityRequestContext);
    }
  }
  
  public void removeEntityRequest(EntityRequestContext entityRequestContext) {
    checkedEntityRequestContexts.remove(entityRequestContext);
    uncheckedEntityRequestContexts.remove(entityRequestContext);
  }

  @Override
  public String toString() {
    return "EntityPublishContext ["
        + ", pattern=" + pattern + ", checkedEntityRequestContexts="
        + checkedEntityRequestContexts + ", uncheckedEntityRequestContexts="
        + uncheckedEntityRequestContexts + "]";
  }

  public String getPattern() {
    return pattern.toString();
  }
  
  public String[] getCheckedSubscriptions() {
    return getSubscriptions(checkedEntityRequestContexts);
  }
  
  public String[] getUncheckedSubscriptions() {
    return getSubscriptions(uncheckedEntityRequestContexts);
  }
  
  private String[] getSubscriptions(List<EntityRequestContext> entityRequestContexts) {
    String[] res = new String[entityRequestContexts.size()];
    int i = 0;
    for (EntityRequestContext entityRequestContext : entityRequestContexts) {
      res[i++] = entityRequestContext.getSubscriptionContext().getSubscriberContext().getURI() + "-" +
          entityRequestContext.getSubscriptionContext().getSubscriptionIdAsString();
    }
    return res;
  }
}
