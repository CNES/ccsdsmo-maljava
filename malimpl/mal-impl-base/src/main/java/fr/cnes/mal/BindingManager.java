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
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.dyade.aaa.util.management.MXWrapper;

public abstract class BindingManager<B extends Binding> {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(BindingManager.class.getName());

  private CNESMALContext malContext;
  
  private ThreadPool threadPool;
  
  private Vector<B> bindings;
  
  private String name;
  
  private String jmxName;
  
  private volatile boolean closed;
  
  private ExecutorService executorService;
  
  private List<BindingTaskExecutor> taskExecutors;
  
  private AtomicInteger currentExecutor;
  
  public BindingManager(CNESMALContext malContext, 
      int threadPoolSize,
      String name,
      String jmxName) {
    super();
    this.malContext = malContext;
    this.name = name;
    this.jmxName = jmxName;
    bindings = new Vector<B>();
    
    if (malContext.isPollExec()) {
      currentExecutor = new AtomicInteger();
      executorService = Executors.newFixedThreadPool(threadPoolSize, new ThreadFactory() {
        
        private int threadCounter;

        public Thread newThread(Runnable runnable) {
          Thread newThread = new Thread(runnable, BindingManager.this.name + 
              ".Executor#" + threadCounter++);
          return newThread;
        }
      });
      
      taskExecutors = new ArrayList<BindingTaskExecutor>(threadPoolSize);
      for (int i = 0; i < threadPoolSize; i++) {
        taskExecutors.add(new BindingTaskExecutor());
      }
    } else {
      threadPool = new ThreadPool(name, threadPoolSize);
      threadPool.start();
    }
  }

  public String getJmxName() {
    return jmxName;
  }

  public CNESMALContext getMalContext() {
    return malContext;
  }

  /*
  public ThreadPool getThreadPool() {
    return threadPool;
  }*/
  
  public void executeTask(Task task) throws MALException {
    if (malContext.isPollExec()) {
      // Publisher acknowledgements should not be load-balanced
      if (task.getMessage().getHeader().getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX) {
        switch (task.getMessage().getHeader().getInteractionStage().getValue()) {
        case MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE:
        case MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE:
          execute(task);
          break;
        default:
          roundRobin(task);
        }
      } else {
        roundRobin(task);
      }
    } else {
      threadPool.push(task);
    }
  }
  
  private void execute(final Task task) {
    executorService.execute(new Runnable() {

      public void run() {
        try {
          task.run();
          task.free();
        } catch (Exception e) {
          if (logger.isLoggable(BasicLevel.ERROR))
            logger.log(BasicLevel.ERROR, "", e);
        }
      }
    });
  }
  
  private void roundRobin(Task task) throws MALException {
    int taskExecutorIndex = task.getExecutorIndex();
    if (taskExecutorIndex < 0) {
      // Round robin
      taskExecutorIndex = currentExecutor.getAndIncrement()  % taskExecutors.size();
      task.setExecutorIndex(taskExecutorIndex);
    }
    taskExecutors.get(taskExecutorIndex).execute(task);
  }
  
  public void setThreadPool(ThreadPool newThreadPool) {
    if (threadPool != null) {
      threadPool.stop();
    }
    threadPool = newThreadPool;
    threadPool.start();
  }
  
  public int getThreadPoolSize() {
    return threadPool.getSize();
  }
  
  public void setThreadPoolSize(int newSize) {
    threadPool.setThreadPoolSize(newSize);
  }

  public int getThreadPoolTaskQueueSize() {
    return threadPool.getTaskQueueSize();
  }

  public boolean isThreadPoolStarted() {
    return threadPool.isStarted();
  }
  
  protected void addBinding(B binding) {
    bindings.add(binding);
  }
  
  protected B getBinding(int index) {
    return bindings.get(index);
  }
  
  void removeBinding(B binding) {
    bindings.remove(binding);
  }
  
  public int getBindingCount() {
    return bindings.size();
  }
  
  public synchronized void close() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "BindingManager.close()");
    if (! closed) {
      Vector<B> bindingsClone = (Vector<B>) bindings.clone();
      for (int i = 0; i < bindingsClone.size(); i++) {
        B binding = bindingsClone.get(i);
        binding.close();
      }
      bindings.clear();
      doClose();
      if (malContext.isPollExec()) {
        executorService.shutdown();
      } else {
        threadPool.stop();
      }
      finalizeManager();
      try {
        MXWrapper.unregisterMBean(jmxName);
      } catch (Exception e) {
        logger.log(BasicLevel.WARN, getClass().getName() + " jmx failed", e);
      }
      closed = true;
    }
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "BindingManager[" + jmxName
          + "] is closed.");
  }
  
  protected abstract void doClose() throws MALException;
  
  protected abstract void finalizeManager() throws MALException;
  
  public void checkClosed() throws MALException {
    if (closed) throw new MALException("Closed");
  }
  
  public static void checkTransport(MALTransport transport, 
      QoSLevel qosLevel, MALService service) throws MALException {
    if (! transport.isSupportedQoSLevel(qosLevel)) {
      throw new MALException("QoS level not supported: " + qosLevel);
    }
    if (service.getSendOperations().length > 0) {
      checkInteractionPattern(transport, InteractionType.SEND);
    }
    if (service.getSubmitOperations().length > 0) {
      checkInteractionPattern(transport, InteractionType.SUBMIT);
    }
    if (service.getRequestOperations().length > 0) {
      checkInteractionPattern(transport, InteractionType.REQUEST);
    }
    if (service.getInvokeOperations().length > 0) {
      checkInteractionPattern(transport, InteractionType.INVOKE);
    }
    if (service.getProgressOperations().length > 0) {
      checkInteractionPattern(transport, InteractionType.PROGRESS);
    }
    if (service.getPubSubOperations().length > 0) {
      checkInteractionPattern(transport, InteractionType.PUBSUB);
    }
  }
  
  public static void checkInteractionPattern(MALTransport transport, InteractionType interactionType) throws MALException {
    if (! transport.isSupportedInteractionType(interactionType)) {
      // No error should be thrown because all IPs have to be supported except Pub/Sub in a native way.
      // But even if Pub/Sub is not natively supported by the transport, it is supported at the MAL level.
      // So Pub/Sub not supported doesn't mean that a consumer can't use a Pub/Sub operation.
      // Nevertheless, the call to 'isSupportedInteractionType' is required by the MAL Blue Book prototyping tests.
      // throw new MALException("Interaction pattern not supported: " + interactionType);
    }
  }
  
  public static String getProtocol(String uri) {
    StringTokenizer st = new StringTokenizer(uri, ":/");
    String proto = st.nextToken();
    return proto;
  }
  
  private class BindingTaskExecutor implements Runnable {
    
    private boolean running;
    
    private AtomicInteger pending;
    
    private ConcurrentLinkedQueue<Task> tasks;
    
    BindingTaskExecutor() {
      tasks = new ConcurrentLinkedQueue<Task>();
      pending = new AtomicInteger(0);
    }

    public void run() {
      while (true) {
        try {
          Task task = tasks.poll();
          if (task == null) {
            synchronized (tasks) {
              task = tasks.poll();
              if (task == null) {
                running = false;
                return;
              }
            }
          }
          
          pending.decrementAndGet();
          task.run();
          task.free();
          
        } catch (Throwable error) {
          if (logger.isLoggable(BasicLevel.ERROR))
            logger.log(BasicLevel.ERROR, "", error);
        }
      }
    }
    
    public void execute(Task task) {
      tasks.offer(task);
      pending.incrementAndGet();
      synchronized (tasks) {
        if (! running) {
          running = true;
          executorService.execute(this);
        }
      }
    }
    
  }
  
}
