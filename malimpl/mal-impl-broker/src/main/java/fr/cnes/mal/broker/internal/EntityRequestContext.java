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
import java.util.Arrays;

import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.objectweb.util.monolog.api.BasicLevel;

class EntityRequestContext implements Serializable, EntityRequestContextMBean {
  
  private SubscriptionContext subscriptionContext;
  
  private IdentifierList subDomain;
  
  private Boolean allAreas;
  
  private Boolean allServices;
  
  private Boolean allOperations;
  
  private EntityKey pattern;
  
  private Boolean onlyOnChange;
  
  private Boolean allSubDomains;
  
  private void readObject(java.io.ObjectInputStream is)
      throws IOException, ClassNotFoundException {
    subscriptionContext = (SubscriptionContext) is.readObject();
    if (is.readBoolean()) {
      subDomain = null;
    } else {
      int subDomainLength = is.readInt();
      subDomain = new IdentifierList(subDomainLength);
      for (int i = 0; i < subDomainLength; i++) {
        subDomain.add(new Identifier(is.readUTF()));
      }
    }
    allAreas = new Boolean(is.readBoolean());
    allServices = new Boolean(is.readBoolean());
    allOperations = new Boolean(is.readBoolean());
    Identifier id1 = new Identifier(is.readUTF());
    Long id2 = (Long) is.readObject();
    Long id3 = (Long) is.readObject();
    Long id4 = (Long) is.readObject();
    pattern = new EntityKey(id1, id2, id3, id4);
    onlyOnChange = new Boolean(is.readBoolean());
    allSubDomains = new Boolean(is.readBoolean());
  }
  
  private void writeObject(java.io.ObjectOutputStream os)
      throws IOException {
    os.writeObject(subscriptionContext);
    if (subDomain == null) {
      os.writeBoolean(true);
    } else {
      os.writeBoolean(false);
      os.writeInt(subDomain.size());
      for (int i = 0; i < subDomain.size(); i++) {
        os.writeUTF(subDomain.get(i).getValue());
      }
    }
    os.writeBoolean(allAreas.booleanValue());
    os.writeBoolean(allServices.booleanValue());
    os.writeBoolean(allOperations.booleanValue());
    os.writeUTF(pattern.getFirstSubKey().getValue());
    os.writeObject(pattern.getSecondSubKey());
    os.writeObject(pattern.getThirdSubKey());
    os.writeObject(pattern.getFourthSubKey());
    os.writeBoolean(onlyOnChange.booleanValue());
    os.writeBoolean(allSubDomains.booleanValue());
  }
  
  public EntityRequestContext(SubscriptionContext subscriptionContext,
      IdentifierList subDomain, Boolean allAreas,
      Boolean allServices, Boolean allOperations, EntityKey pattern,
      Boolean onlyOnChange) {
    this.subscriptionContext = subscriptionContext;
    this.subDomain = subDomain;
    this.allAreas = allAreas;
    this.allServices = allServices;
    this.allOperations = allOperations;
    this.pattern = pattern;
    this.onlyOnChange = onlyOnChange;
    if (subDomain != null && subDomain.size() > 0) {
      if (subDomain.get(subDomain.size() - 1).getValue().equals("*")) {
        allSubDomains = true;
      } else {
        allSubDomains = false;
      }
    } else {
      allSubDomains = false;
    }
  }

  public SubscriptionContext getSubscriptionContext() {
    return subscriptionContext;
  }

  public EntityKey getPattern() {
    return pattern;
  }
  
  public boolean getOnlyOnChange() {
    return onlyOnChange;
  }
  
  public boolean isAllAreas() {
    return allAreas;
  }

  public boolean isAllServices() {
    return allServices;
  }

  public boolean isAllOperations() {
    return allOperations;
  }
  
  public boolean matchArea(UShort area) {
    if (! isAllAreas()) {
      SubscriberContext subscriberContext = getSubscriptionContext().getSubscriberContext();
      if (! subscriberContext.getArea().equals(area)) {
        if (Broker.logger.isLoggable(BasicLevel.DEBUG))
          Broker.logger.log(BasicLevel.DEBUG, "Different area: " + 
              subscriberContext.getArea() + " != " + area);
        return false;
      }
    }
    return true;
  }
  
  public boolean matchService(UShort service) {
    if (! isAllServices()) {
      SubscriberContext subscriberContext = getSubscriptionContext().getSubscriberContext();
      if (! subscriberContext.getService().equals(service)) {
        if (Broker.logger.isLoggable(BasicLevel.DEBUG))
          Broker.logger.log(BasicLevel.DEBUG, "Different service: " + 
              subscriberContext.getService() + " != " + service);
        return false;
      }
    }
    return true;
  }
  
  public boolean matchOperation(UShort operation) {
    if (! isAllOperations()) {
      SubscriberContext subscriberContext = getSubscriptionContext().getSubscriberContext();
      if (! subscriberContext.getOperation().equals(operation)) {
        if (Broker.logger.isLoggable(BasicLevel.DEBUG))
          Broker.logger.log(BasicLevel.DEBUG, "Different operation: " + 
              subscriberContext.getOperation() + " != " + operation);
        return false;
      }
    }
    return true;
  }
  
  public boolean matchDomain(IdentifierList publicationDomain) {
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, "EntityRequestContext.matchDomain(" + publicationDomain + ')');
    int startIndex;
    IdentifierList subscriptionDomain = 
        getSubscriptionContext().getSubscriberContext().getKey().getDomainKey().getDomain();
    if (publicationDomain.size() == subscriptionDomain.size()) {
      startIndex = -1;
    } else {
      startIndex = subscriptionDomain.size();
    }
    if (subDomain == null || subDomain.size() == 0) {
      if (startIndex != -1) {
        if (Broker.logger.isLoggable(BasicLevel.DEBUG))
          Broker.logger.log(BasicLevel.DEBUG, "No subdomain and publication domain bigger than subscription domain");
        return false;
      }
    } else {
      if (startIndex == -1) {
        if (subDomain.size() == 1 && allSubDomains) {
          return true;
        } else {
          if (Broker.logger.isLoggable(BasicLevel.DEBUG))
            Broker.logger.log(BasicLevel.DEBUG, "Publication domain same as subscription domain but subdomain is not '*'");
          return false;
        }
      } else {
        int domainIndex = startIndex;
        int subDomainEndIndex;
        if (allSubDomains) {
          subDomainEndIndex = subDomain.size() - 1;
        } else {
          if (publicationDomain.size() - startIndex > subDomain.size()) {
            if (Broker.logger.isLoggable(BasicLevel.DEBUG))
              Broker.logger.log(BasicLevel.DEBUG, "Publication domain bigger than subscription domain + subdomain");
            return false;
          } else {
            subDomainEndIndex = subDomain.size();
          }
        }
        for (int j = 0; j < subDomainEndIndex; j++) {
          if (domainIndex < publicationDomain.size()) {
            if (!subDomain.get(j).equals(publicationDomain.get(domainIndex))) {
              if (Broker.logger.isLoggable(BasicLevel.DEBUG))
                Broker.logger.log(BasicLevel.DEBUG, subDomain.get(j) + " != "
                    + publicationDomain.get(domainIndex));
              return false;
            }
            domainIndex++;
          } else {
            if (Broker.logger.isLoggable(BasicLevel.DEBUG))
              Broker.logger.log(BasicLevel.DEBUG, "Publication domain smaller than subscription domain + subdomain");
            return false;
          }
        }
      }
    }
    return true;
  }
  
  
  public boolean matchUpdateType(int updateType) {
    if (onlyOnChange.booleanValue() && 
        updateType == UpdateType._UPDATE_INDEX) {
      return false;
    }
    return true;
  }
  
  public boolean matchEntityKey(EntityKey key) {
    return MALPattern.match(key, pattern);
  }

  @Override
  public String toString() {
    return "EntityRequestContext ["
        + ", subDomain=" + subDomain + ", allAreas=" + allAreas
        + ", allServices=" + allServices + ", allOperations=" + allOperations
        + ", pattern=" + pattern + ", onlyOnChange=" + onlyOnChange
        + ", allSubDomains=" + allSubDomains + "]";
  }

  public String getSubDomain() {
    if (subDomain == null) {
      return null;
    } else {
      return subDomain.toString();
    }
  }

  public String getPatternAsString() {
    return pattern.toString();
  }
}