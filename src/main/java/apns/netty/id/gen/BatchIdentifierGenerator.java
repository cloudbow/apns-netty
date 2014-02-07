/*
 * BatchIdentifierGenerator.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.id.gen;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * The Class BatchIdentifierGenerator.
 * @author arung
 */
@Component
public class BatchIdentifierGenerator {
    // unique across multiple Batches

    /** The identifier. */
    private final AtomicInteger identifier = new AtomicInteger(0);

    /**
     * New identifier.
     * @return the integer
     */
    public Integer newIdentifier() {

        final int currentIdentifier = identifier.incrementAndGet();
        if (currentIdentifier == Integer.MAX_VALUE) {
            identifier.getAndSet(0);
        }

        return identifier.intValue();

    }

}
