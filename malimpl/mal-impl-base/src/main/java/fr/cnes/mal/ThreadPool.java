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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.dyade.aaa.common.Daemon;

public class ThreadPool {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(ThreadPool.class.getName());
  
  private String poolName;
  
  private Vector<Task> taskQueue;
  
  private List<Worker> workers;
  
  private boolean started;

  public ThreadPool(String poolName, int size) {
    this.poolName = poolName;
    taskQueue = new Vector<Task>();
    workers = new ArrayList<ThreadPool.Worker>(size);
    for (int i = 0; i < size; i++) {
      workers.add(new Worker(i));
    }
  }

  public synchronized void start() {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "ThreadPool.start()");
    if (! started) {
      started = true;
      for (Worker w : workers) {
        w.start();
      }
    }
  }
  
  public synchronized void setThreadPoolSize(int newSize) {
    if (newSize > workers.size()) {
      int delta = newSize - workers.size();
      for (int i = 0; i < delta; i++) {
        int index = workers.size();
        Worker w = new Worker(index);
        workers.add(w);
        if (started) {
          w.start();
        }
      }
    } else if (newSize < workers.size()) {
      int delta = workers.size() - newSize;
      for (int i = 0; i < delta; i++) {
        Worker w = workers.remove(workers.size() - 1);
        if (started) {
          w.stop();
        }
      }
    }
  }

  public synchronized void stop() {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "ThreadPool.stop()");
    if (started) {
      started = false;
      for (Worker w : workers) {
        w.stop();
      }
    }
  }
  
  public void push(Task task) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "ThreadPool.push(" + task + ')');
    synchronized (taskQueue) {
      taskQueue.addElement(task);
      taskQueue.notify();
    }
  }
  
  private class Worker extends Daemon {
    
    Worker(int i) {
      super(poolName + "Thread#" + i, logger);
    }

    protected void close() {}

    protected void shutdown() {}

    public void run() {
      canStop = true;
      loop:
      while (running) {
        try {
          Task task;
          synchronized (taskQueue) {
            waitLoop: while (true) {
              for (int i = 0; i < taskQueue.size(); i++) {
                task = taskQueue.elementAt(i);
                if (task.runnable()) {
                  taskQueue.removeElementAt(i);
                  break waitLoop;
                }
              }
              taskQueue.wait();
            }
            task.init();
          }
          
          try {
            task.run();
          } finally {
            synchronized (taskQueue) {
              task.finalizeTask();
              taskQueue.notify();
            }
            // Not synchronized with taskQueue
            // to avoid deadlock.
            // Moreover free() can take some time
            // if an acknowledgment message is sent.
            task.free();
          }
        } catch (InterruptedException exc) {
          break loop;
        } catch (Throwable error) {
          if (logger.isLoggable(BasicLevel.WARN))
            logger.log(BasicLevel.WARN, "", error);
        }
      }
    }
  }

  public int getTaskQueueSize() {
    return taskQueue.size();
  }

  public int getSize() {
    return workers.size();
  }

  public boolean isStarted() {
    return started;
  }

}
