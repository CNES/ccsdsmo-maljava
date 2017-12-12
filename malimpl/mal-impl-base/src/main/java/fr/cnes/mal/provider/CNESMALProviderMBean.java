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
package fr.cnes.mal.provider;

import fr.cnes.mal.BindingMBean;

public interface CNESMALProviderMBean extends BindingMBean {

  /**
   * Tests if the provider is a publisher.
   * @return <code>true</code> if the provider
   * is a publisher; <code>false</code> otherwise.
   */
  boolean isPublisher();

  /**
   * Returns the URI of the broker used by
   * the provider.
   * @return the URI of the broker used by
   * the provider
   */
  String getBrokerURIAsString();
  
  /**
   * Returns the name of the area of the provided service.
   * @return the name of the area of the provided service
   */
  String getAreaName();

  /**
   * Returns the name of the provided service.
   * @return the name of the provided service.
   */
  String getServiceName();
  
  /**
   * Returns the number of the area of the provided service.
   * @return the number of the area of the provided service
   */
  int getAreaNumber();
  
  /**
   * Returns the number of the provided service.
   * @return the number of the provided service
   */
  int getServiceNumber();
  
  /**
   * Returns the version of the area.
   * @return the version of the area
   */
  int getAreaVersion();
  
  /**
   * Returns the number of published messages.
   * @return the number of published messages
   */
  int getPublishedMessageCount();
  
  /**
   * Returns the duration of the last publish.
   * @return the duration of the last publish
   */
  long getPublishDuration();
  
  /**
   * Returns the duration of the last notify.
   * (only for provider with a private broker)
   * @return the duration of the last notify
   */
  long getNotifyDuration();
  
  /**
   * Returns the size of the last published update list.
   * @return the size of the last published update list
   */
  int getPublishedUpdateListSize();
  
}