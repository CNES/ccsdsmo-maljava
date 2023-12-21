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
package fr.cnes.mal.consumer;

import java.util.List;

import fr.cnes.mal.BindingMBean;

public interface CNESMALConsumerMBean extends BindingMBean {

  String getAreaName();
  
  String getServiceName();
  
  int getAreaNumber();
  
  int getServiceNumber();
  
  /**
   * TODO SL revert to area version
   *
  int getAreaVersion();
  */
  int getServiceVersion();
  
  String getURIToAsString();
  
  String getBrokerURIAsString();

  String getQoSLevelAsString();
  
  long getPriority();

  int getSubscriptionCount();
  
  // TODO SL getAuthenticationId is already defined in MALConsumer with a different return value
  // this change may have other consequences
  byte[] getAuthenticationIdValue();
  
  String getDomainAsString();

  String getNetworkZone();

  String getSessionType();
  
  List<Long> getRunningTransactions();
  
  int getInteractionCount();

}