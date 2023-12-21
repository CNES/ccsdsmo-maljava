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
package fr.cnes.mal.broker;

import java.util.Vector;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.broker.MALBroker;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.broker.MALBrokerHandler;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.URI;
import org.objectweb.util.monolog.api.Logger;

public class CNESMALBroker implements MALBroker {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(CNESMALBroker.class.getName());
  
  private CNESMALBrokerManager brokerManager;
  
  private MALBrokerHandler handler;
  
  private Vector<CNESMALBrokerBinding> brokerBindings;
  
  private String jmxName;
  
  public CNESMALBroker(CNESMALBrokerManager brokerManager,
      MALBrokerHandler handler,
      String jmxName) {
    this.brokerManager = brokerManager;
    if (handler == null) {
      handler = new CNESMALBrokerHandler(null, jmxName);
    }
    this.handler = handler;
    this.jmxName = jmxName;
    brokerBindings = new Vector<CNESMALBrokerBinding>();
  }
  
  public MALBrokerHandler getHandler() {
    return handler;
  }
  
  void addBinding(CNESMALBrokerBinding brokerBinding) {
    brokerBindings.addElement(brokerBinding);
    handler.malInitialize(brokerBinding);
  }
  
  void removeBinding(Identifier uri) throws MALException {
    for (int i = 0; i < brokerBindings.size(); i++) {
      CNESMALBrokerBinding brokerBinding = (CNESMALBrokerBinding) brokerBindings.elementAt(i);
      if (brokerBinding.getURI().equals(uri)) {
        handler.malFinalize(brokerBinding);
        brokerBindings.remove(i);
        return;
      }
    }
  }
  
  public void activate() throws MALException {
    for (int i = 0; i < brokerBindings.size(); i++) {
      CNESMALBrokerBinding brokerBinding = (CNESMALBrokerBinding) brokerBindings.elementAt(i);
      brokerBinding.startMessageDelivery();
    }
  }

  public void close() throws MALException {
    for (int i = 0; i < brokerBindings.size(); i++) {
      CNESMALBrokerBinding brokerBinding = (CNESMALBrokerBinding) brokerBindings.elementAt(i);
      brokerBinding.close();
    }
    brokerManager.closeBroker(this);
  }

  public Identifier[] getDestinationIds() {
    Identifier[] res = new Identifier[brokerBindings.size()];
    for (int i = 0; i < brokerBindings.size(); i++) {
      CNESMALBrokerBinding brokerBinding = (CNESMALBrokerBinding) brokerBindings.elementAt(i);
      res[i] = brokerBinding.getDestinationId();
    }
    return res;
  }

  public MALBrokerBinding[] getBindings() {
    MALBrokerBinding[] res = new MALBrokerBinding[brokerBindings.size()];
    brokerBindings.copyInto(res);
    return res;
  }
  
  public void commitMessageSending() throws MALInteractionException, MALException {
    for (int i = 0; i < brokerBindings.size(); i++) {
      CNESMALBrokerBinding brokerBinding = (CNESMALBrokerBinding) brokerBindings.elementAt(i);
      brokerBinding.commitMessageSending();
    }
  }
}
