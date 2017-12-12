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

public interface BindingManagerMBean {
  
  /**
   * Returns the number of message delivering
   * tasks waiting to be performed.
   * @return the number of message delivering
   * tasks waiting to be performed
   */
  int getThreadPoolTaskQueueSize();

  /**
   * Returns the number of activation threads
   * @return the number of activation threads
   */
  int getThreadPoolSize();

  /**
   * Tests if the thread pool has been started
   * or not.
   * @return <code>true</code> if the thread pool
   * is started; <code>false</code> otherwise
   */
  boolean isThreadPoolStarted();
  
  /**
   * Sets the number of activation threads.
   * @param newSize the number of activation threads
   */
  void setThreadPoolSize(int newSize);

}
