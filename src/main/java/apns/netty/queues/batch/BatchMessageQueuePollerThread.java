/*
 * BatchMessageQueuePollerThread.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.queues.batch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import apns.netty.connection.impl.BatchApnsConnection;
import apns.netty.connpool.ConnectionPoolManagerFactory;
import apns.netty.model.ApnsBatchMessage;

/**
 * The Class BatchMessageQueuePollerThread.
 * @author arung
 */
@DependsOn("batchApnsConnection")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BatchMessageQueuePollerThread extends Thread {

    /** The Constant GOT_BATCH_MESSAGE_FROM_BATCH_QUEUE. */
    private static final String GOT_BATCH_MESSAGE_FROM_BATCH_QUEUE = "Got batch message from batch queue";

    /** The Constant BATCH_CONNECTIONS_IS. */
    private static final String BATCH_CONNECTIONS_IS = ": Batch connections is";

    /** The Constant BATCH_MESSAGE_QUEUE_IS. */
    private static final String BATCH_MESSAGE_QUEUE_IS = "Batch message queue is:";

    /** The Constant BATCH_QUEUE_NON_EMPTY. */
    private static final String BATCH_QUEUE_NON_EMPTY = "Batch Queue non empty:";

    /** The Constant BATCH_QUEUE_POLLER. */
    private static final String BATCH_QUEUE_POLLER = "BatchQueuePoller-";

    /** The Constant logger. */
    private static final Logger logger = Logger
            .getLogger(BatchMessageQueuePollerThread.class);

    /** The batch message queue. */
    @Autowired
    private BatchMessageQueue batchMessageQueue;

    /** The batch connection. */
    @Autowired
    private BatchApnsConnection batchConnection;

    /** The connection pool manager factory. */
    @Autowired
    private ConnectionPoolManagerFactory connectionPoolManagerFactory;

    /**
     * Instantiates a new batch message queue poller thread.
     * @param name
     *            the name
     */
    public BatchMessageQueuePollerThread(final int name) {
        super(BatchMessageQueuePollerThread.BATCH_QUEUE_POLLER + name);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        while (true) {

            final int batchConnections = batchConnection
                    .getAtomicBatchCounter().intValue();
            BatchMessageQueuePollerThread.logger
                    .trace(BatchMessageQueuePollerThread.BATCH_QUEUE_NON_EMPTY
                           + batchMessageQueue.size());
            BatchMessageQueuePollerThread.logger
                    .trace(BatchMessageQueuePollerThread.BATCH_MESSAGE_QUEUE_IS
                           + batchMessageQueue
                           + BatchMessageQueuePollerThread.BATCH_CONNECTIONS_IS
                           + batchConnections);

            final ApnsBatchMessage msg = batchMessageQueue.peekQueue();
            BatchMessageQueuePollerThread.logger
                    .trace(BatchMessageQueuePollerThread.GOT_BATCH_MESSAGE_FROM_BATCH_QUEUE);
            connectionPoolManagerFactory.getBatchConnection().randomWrite(
                    msg);

        }

    }
}
