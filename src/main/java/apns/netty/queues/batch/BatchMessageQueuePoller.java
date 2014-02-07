/*
 * BatchMessageQueuePoller.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.queues.batch;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import apns.netty.config.ApnsConfig;
import apns.netty.connpool.ConnectionPoolManagerFactory;
import apns.netty.constants.ApplicationContextComponents;

/**
 * The Class BatchMessageQueuePoller.
 * @author arung
 */
@Component
public class BatchMessageQueuePoller {

    /** The apns config. */
    @Autowired
    private ApnsConfig apnsConfig;

    /** The batch message queue. */
    @Autowired
    BatchMessageQueue batchMessageQueue;

    /** The application context. */
    @Autowired
    ApplicationContext applicationContext;

    /** The connection pool manager factory. */
    @Autowired
    ConnectionPoolManagerFactory connectionPoolManagerFactory;

    /**
     * Inits the.
     */
    @PostConstruct
    public void init() {
        for (int i = 0; i < apnsConfig.getMaxBatchQueuePollerThreads(); i++) {
            ((BatchMessageQueuePollerThread) applicationContext
                    .getBean(
                            ApplicationContextComponents.BATCH_MESSAGE_QUEUE_POLLER_THREAD,
                            i)).start();

        }
    }

}
