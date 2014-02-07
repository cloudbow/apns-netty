/*
 * APNSMessenger.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import apns.netty.connection.impl.BatchApnsConnection;
import apns.netty.constants.ApplicationContextComponents;
import apns.netty.id.gen.BatchIdentifierGenerator;
import apns.netty.model.impl.ApnsMessage;
import apns.netty.model.impl.BatchFullMessage;
import apns.netty.queues.batch.BatchMessageQueue;

/**
 * The Class APNSMessenger.
 * @author arung
 */
@Component
public class APNSMessenger {

    /** The Constant SINGLE_MESSAGE_QUEUE. */
    private static final String SINGLE_MESSAGE_QUEUE = "singleMessageQueue";

    /**
     * The Class ShutDownHook.
     */
    public static class ShutDownHook extends Thread {

        /*
         * (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            APNSMessenger.LOGGER.info(APNSMessenger.RUNNING_SHUTDOWN_HOOK);
            APNSMessenger.getRunner().getContext().close();
            // getRunner().getLock().notify();
        }
    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger
            .getLogger(APNSMessenger.class);
    /** The Constant SHUT_DOWN_HOOK_ATTACHED. */
    private static final String SHUT_DOWN_HOOK_ATTACHED = "Shut Down Hook Attached.";

    /** The Constant RUNNING_SHUTDOWN_HOOK. */
    private static final String RUNNING_SHUTDOWN_HOOK = "Running shutdown hook.";

    /** The Constant MAIN_APPLICATION_CONTEXT_XML. */
    private static final String MAIN_APPLICATION_CONTEXT_XML = "apnsAppContext.xml";

    /** The runner. */
    private static APNSMessenger runner;

    /** The context. */
    private AbstractApplicationContext context;

    /**
     * The main method.
     * @param args
     *            the arguments
     */
    public static void main(final String[] args) {
        APNSMessenger.setRunner(new APNSMessenger());
        APNSMessenger.getRunner().attachShutDownHook();
        APNSMessenger.getRunner().setContext(
                new ClassPathXmlApplicationContext(
                        APNSMessenger.MAIN_APPLICATION_CONTEXT_XML));
        APNSMessenger.getRunner().getContext().registerShutdownHook();
        new Thread(new Runnable() {

            @Override
            public void run() {

                APNSMessenger.getRunner().getContext()
                        .getBean(APNSMessenger.SINGLE_MESSAGE_QUEUE);

                // final SingleMessageQueue singleMessageQueue =
                // (SingleMessageQueue) APNSMessenger
                // .getRunner().getContext()
                // .getBean("singleMessageQueue");
                //
                // {
                // final ApnsMessage.Builder builder = new
                // ApnsMessage.Builder();
                // builder.deviceToken("02ae608511d2f858881c6761ae7d9278ffe6ea2a0a1f4fba9310dea63bea86dd");
                // builder.payLoad("{\"aps\" : {\"alert\" : \"Reds trying to hold on to lead.\",\"badge\" : 9,\"sound\" : \"bingbong.aiff\"},\"acme1\" : \"bar\",\"acme2\" : 42}");
                // builder.expTime((int) (Calendar.getInstance()
                // .getTimeInMillis() / 1000 + 86400));
                // builder.id(1244);
                // builder.prio((byte) 10);
                //
                // singleMessageQueue.pushQueue(builder.build());
                //
                // }
                //
                // {
                // final ApnsMessage.Builder builder = new
                // ApnsMessage.Builder();
                // builder.deviceToken("03ae608511d2f858881c6761ae7d9278ffe6ea2a0a1f4fba9310dea63bea86dd");
                // builder.payLoad("{\"aps\" : {\"alert\" : \"Reds trying to hold on to lead.\",\"badge\" : 9,\"sound\" : \"bingbong.aiff\"},\"acme1\" : \"bar\",\"acme2\" : 42}");
                // builder.expTime((int) (Calendar.getInstance()
                // .getTimeInMillis() / 1000 + 86400));
                // builder.id(1245);
                // builder.prio((byte) 10);
                //
                // singleMessageQueue.pushQueue(builder.build());
                //
                // }
                //
                // {
                // final ApnsMessage.Builder builder = new
                // ApnsMessage.Builder();
                // builder.deviceToken("7e590ccdadd7adf4294be527d41191eb4639a984f92121260a9995cc64c69aef");
                // builder.payLoad("{\"aps\" : {\"alert\" : \"Reds trying to hold on to lead.\",\"badge\" : 9,\"sound\" : \"bingbong.aiff\"},\"acme1\" : \"bar\",\"acme2\" : 42}");
                // builder.expTime((int) (Calendar.getInstance()
                // .getTimeInMillis() / 1000 + 86400));
                // builder.id(1246);
                // builder.prio((byte) 10);
                //
                // singleMessageQueue.pushQueue(builder.build());
                //
                // }

                final BatchMessageQueue batchMessageQueue = (BatchMessageQueue) APNSMessenger
                        .getRunner()
                        .getContext()
                        .getBean(
                                ApplicationContextComponents.BATCH_MESSAGE_QUEUE);

                final BatchApnsConnection bConn = (BatchApnsConnection) APNSMessenger
                        .getRunner()
                        .getContext()
                        .getBean(
                                ApplicationContextComponents.BATCH_CONNECTION);

                final BatchIdentifierGenerator batchIdentifierGenerator = (BatchIdentifierGenerator) APNSMessenger
                        .getRunner()
                        .getContext()
                        .getBean(
                                ApplicationContextComponents.BATCH_IDENTIFIER_GENERATOR);

                while (bConn.getAtomicBatchCounter().intValue() != 75) {
                    ;
                }

                final BatchFullMessage bfm = new BatchFullMessage();

                for (int i = 0; i < 10; i++) {
                    final ApnsMessage.Builder builder = new ApnsMessage.Builder();
                    if (i == 5) {
                        builder.deviceToken("8e590ccdadd7adf4294be527d41191eb4639a984f92121260a9995cc64c69aef");
                    } else {
                        builder.deviceToken("7e590ccdadd7adf4294be527d41191eb4639a984f92121260a9995cc64c69aef");
                    }
                    builder.payLoad("{\"aps\" : {\"alert\" : \"Reds trying to hold on to lead."
                                    + i
                                    + "\",\"badge\" : 9,\"sound\" : \"bingbong.aiff\"},\"acme1\" : \"bar\",\"acme2\" : 42}");
                    builder.expTime((int) (Calendar.getInstance()
                            .getTimeInMillis() / 1000 + 86400));
                    builder.id(batchIdentifierGenerator.newIdentifier());
                    builder.prio((byte) 10);

                    bfm.addInvMessages(builder.build());
                }
                batchMessageQueue.pushQueue(bfm);

                // bfm = new BatchFullMessage();
                //
                // for (int i = 10; i < 20; i++) {
                // final ApnsMessage.Builder builder = new
                // ApnsMessage.Builder();
                // if (i == 5) {
                // builder.deviceToken("03ae608511d2f858881c6761ae7d9278ffe6ea2a0a1f4fba9310dea63bea86dd");
                // } else {
                // builder.deviceToken("02ae608511d2f858881c6761ae7d9278ffe6ea2a0a1f4fba9310dea63bea86dd");
                // }
                // builder.payLoad("{\"aps\" : {\"alert\" : \"Reds trying to hold on to lead."
                // + i
                // +
                // "\",\"badge\" : 9,\"sound\" : \"bingbong.aiff\"},\"acme1\" : \"bar\",\"acme2\" : 42}");
                // builder.expTime((int) (Calendar.getInstance()
                // .getTimeInMillis() / 1000 + 86400));
                // builder.id(batchIdentifierGenerator.newIdentifier());
                // builder.prio((byte) 10);
                //
                // bfm.addInvMessages(builder.build());
                // }
                //
                // batchMessageQueue.pushQueue(bfm);
            }

        }).start();

        // final ApnsMessage.Builder builder = new ApnsMessage.Builder();
        // builder.deviceToken("02ae608511d2f858881c6761ae7d9278ffe6ea2a0a1f4fba9310dea63bea86dd");
        // builder.payLoad("{\"aps\" : {\"alert\" : \"test local .\",\"badge\" : 9,\"sound\" : \"bingbong.aiff\"}");
        // builder.expTime(10000000);
        // builder.id("ab");
        // builder.prio((byte) 1);
        // factory.getSingleConnection().write(builder.build());
    }

    /**
     * Attach shut down hook.
     */
    public void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new ShutDownHook());
        APNSMessenger.LOGGER.info(APNSMessenger.SHUT_DOWN_HOOK_ATTACHED);
    }

    /**
     * Gets the runner.
     * @return the runner
     */
    public static APNSMessenger getRunner() {
        return APNSMessenger.runner;
    }

    /**
     * Sets the runner.
     * @param runner
     *            the new runner
     */
    public static void setRunner(final APNSMessenger runner) {
        APNSMessenger.runner = runner;
    }

    /**
     * Gets the context.
     * @return the context
     */
    public AbstractApplicationContext getContext() {
        return context;
    }

    /**
     * Sets the context.
     * @param context
     *            the new context
     */
    public void setContext(final AbstractApplicationContext context) {
        this.context = context;
    }

}
