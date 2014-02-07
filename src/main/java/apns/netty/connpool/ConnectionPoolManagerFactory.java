/*
 * ConnectionPoolManagerFactory.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.connpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import apns.netty.connection.impl.BatchApnsConnection;
import apns.netty.connection.impl.SingleApnsConnection;
import apns.netty.constants.ApplicationContextComponents;

/**
 * A factory for creating ConnectionPoolManager objects.
 * @author arung This class manages the different connection pools.
 */
@Component
public class ConnectionPoolManagerFactory {

    /** The application context. */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Gets the batch connection.
     * @return the batch connection
     */
    public BatchApnsConnection getBatchConnection() {
        return (BatchApnsConnection) applicationContext
                .getBean(ApplicationContextComponents.BATCH_CONNECTION);
    }

    /**
     * Gets the single connection.
     * @return the single connection
     */
    public SingleApnsConnection getSingleConnection() {
        return (SingleApnsConnection) applicationContext
                .getBean(ApplicationContextComponents.SINGLE_CONNECTION);
    }

}
