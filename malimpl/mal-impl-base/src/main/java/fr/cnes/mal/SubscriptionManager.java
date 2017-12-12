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
package fr.cnes.mal;

import java.util.Enumeration;
import java.util.Hashtable;

import org.ccsds.moims.mo.mal.consumer.MALInteractionListener;
import org.ccsds.moims.mo.mal.structures.Identifier;

public class SubscriptionManager {
  
  private Hashtable<Identifier, SubscriptionContext> subscriptionListeners;
  
  public SubscriptionManager() {
    subscriptionListeners = new Hashtable<Identifier, SubscriptionContext>();
  }
  
  public void registerListener(Identifier subscriptionId, MALInteractionListener listener) {
    subscriptionListeners.put(subscriptionId, new SubscriptionContext(null, listener));
  }
  
  public boolean registerTransactionId(Identifier subscriptionId, Long tid) {
    SubscriptionContext ctx = subscriptionListeners.get(subscriptionId);
    if (ctx == null) {
      return false;
    } else {
      ctx.setTid(tid);
      return true;
    }
  }
  
  public MALInteractionListener getListener(Identifier subscriptionId) {
    return subscriptionListeners.get(subscriptionId).getListener();
  }
  
  public synchronized Long deregisterListener(Identifier subscriptionId) {
    SubscriptionContext ctx = subscriptionListeners.remove(subscriptionId);
    return ctx.getTid();
  }
  
  public Enumeration<SubscriptionContext> getContexts() {
    return subscriptionListeners.elements();
  }
  
  public int size() {
    return subscriptionListeners.size();
  }
  
  public static class SubscriptionContext {
    
    private Long tid;
    
    private MALInteractionListener listener;

    private SubscriptionContext(Long tid, MALInteractionListener listener) {
      super();
      this.tid = tid;
      this.listener = listener;
    }

    public Long getTid() {
      return tid;
    }

    private void setTid(Long tid) {
      this.tid = tid;
    }

    public MALInteractionListener getListener() {
      return listener;
    }

    private void setListener(MALInteractionListener listener) {
      this.listener = listener;
    }
    
  }
}
