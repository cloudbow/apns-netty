/*
 * ApnsConfig.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The Class ApnsConfig.
 */
@Component
public class ApnsConfig {

    // public static final String PRODUCTION_HOST = "gateway.push.apple.com";
    // public static final int PRODUCTION_PORT = 2195;
    //
    // public static final String DEVELOPMENT_HOST =
    // "gateway.sandbox.push.apple.com";
    // public static final int DEVELOPMENT_PORT = 2195;
    //
    // public static final String PRODUCTION_HOST = "feedback.push.apple.com";
    // public static final int PRODUCTION_PORT = 2196;
    //
    // public static final String DEVELOPMENT_HOST =
    // "feedback.sandbox.push.apple.com";
    // public static final int DEVELOPMENT_PORT = 2196;

    /** The Constant AV_PROCESSORS. */
    private static final int AV_PROCESSORS = Runtime.getRuntime()
            .availableProcessors() * 2;

    /** The prod p host. */
    @Value("${APNSConfig.prodPHost}")
    private String prodPHost;

    /** The prod p port. */
    @Value("${APNSConfig.prodPPort}")
    private Integer prodPPort;

    /** The dev p host. */
    @Value("${APNSConfig.devPHost}")
    private String devPHost;

    /** The dev p port. */
    @Value("${APNSConfig.devPPort}")
    private Integer devPPort;

    /** The prod f host. */
    @Value("${APNSConfig.prodFHost}")
    private String prodFHost;

    /** The prod f port. */
    @Value("${APNSConfig.prodFPort}")
    private Integer prodFPort;

    /** The dev f host. */
    @Value("${APNSConfig.devFHost}")
    private String devFHost;

    /** The dev f port. */
    @Value("${APNSConfig.devFPort}")
    private Integer devFPort;

    /** The Constant CONFIG_BASE. */
    private static final String CONFIG_BASE = "CONFIG_BASE";

    /** The production. */
    @Value("${APNSConfig.production}")
    private Boolean production;

    /** The certificate path. */

    private String certificatePath;

    /** The password. */
    @Value("${APNSConfig.password}")
    private String password;

    /** The hand shake timeout. */
    @Value("${APNSConfig.handShakeTimeout}")
    private int handShakeTimeout;

    /** The batch accept count. */
    private int batchAcceptCount;

    /** The batch processing count. */
    private int batchProcessingCount;

    /** The batch accept conn count. */
    @Value("${APNSConfig.batchAcceptConnCount}")
    private int batchAcceptConnCount;

    /** The batch procss conn count. */
    @Value("${APNSConfig.batchProcssConnCount}")
    private int batchProcssConnCount;

    /** The max queue size. */
    @Value("${APNSConfig.maxQueueSize}")
    private int maxQueueSize;

    /** The max batch queue size. */
    @Value("${APNSConfig.maxBatchQueueSize}")
    private int maxBatchQueueSize;

    /** The max batch queue poller threads. */
    @Value("${APNSConfig.maxBatchQueuePollerThreads}")
    private int maxBatchQueuePollerThreads;

    /**
     * Gets the certificate path.
     * @return the certificate path
     */
    public String getCertificatePath() {
        return certificatePath;
    }

    /**
     * Gets the password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the production.
     * @return the production
     */
    public Boolean getProduction() {
        return production;
    }

    /**
     * Sets the certificate path.
     * @param certificatePath
     *            the new certificate path
     */
    @Value("${APNSConfig.certificatePath}")
    public void setCertificatePath(final String certificatePath) {

        this.certificatePath = System.getenv(ApnsConfig.CONFIG_BASE)
                               + File.separator + certificatePath;
    }

    /**
     * Sets the password.
     * @param password
     *            the new password
     */

    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Sets the production.
     * @param production
     *            the new production
     */

    public void setProduction(final Boolean production) {
        this.production = production;
    }

    /**
     * Gets the push host.
     * @return the pushHost
     */
    public String getPushHost() {

        if (getProduction()) {
            return getProdPHost();
        } else {
            return getDevPHost();
        }

    }

    /**
     * Gets the push port.
     * @return the pushPort
     */
    public Integer getPushPort() {
        if (getProduction()) {
            return getProdPPort();
        } else {
            return getDevPPort();
        }
    }

    /**
     * Gets the feedback host.
     * @return the feedbackHost
     */
    public String getFeedbackHost() {
        if (getProduction()) {
            return getProdFHost();
        } else {
            return getDevFHost();
        }
    }

    /**
     * Gets the feedback port.
     * @return the feedbackPort
     */
    public Integer getFeedbackPort() {
        if (getProduction()) {
            return getProdFPort();
        } else {
            return getDevFPort();
        }
    }

    /**
     * Gets the prod p host.
     * @return the prodPHost
     */
    public String getProdPHost() {
        return prodPHost;
    }

    /**
     * Sets the prod p host.
     * @param prodPHost
     *            the prodPHost to set
     */
    public void setProdPHost(final String prodPHost) {
        this.prodPHost = prodPHost;
    }

    /**
     * Gets the prod p port.
     * @return the prodPPort
     */
    public Integer getProdPPort() {
        return prodPPort;
    }

    /**
     * Sets the prod p port.
     * @param prodPPort
     *            the prodPPort to set
     */
    public void setProdPPort(final Integer prodPPort) {
        this.prodPPort = prodPPort;
    }

    /**
     * Gets the dev p host.
     * @return the devPHost
     */
    public String getDevPHost() {
        return devPHost;
    }

    /**
     * Sets the dev p host.
     * @param devPHost
     *            the devPHost to set
     */
    public void setDevPHost(final String devPHost) {
        this.devPHost = devPHost;
    }

    /**
     * Gets the dev p port.
     * @return the devPPort
     */
    public Integer getDevPPort() {
        return devPPort;
    }

    /**
     * Sets the dev p port.
     * @param devPPort
     *            the devPPort to set
     */
    public void setDevPPort(final Integer devPPort) {
        this.devPPort = devPPort;
    }

    /**
     * Gets the prod f host.
     * @return the prodFHost
     */
    public String getProdFHost() {
        return prodFHost;
    }

    /**
     * Sets the prod f host.
     * @param prodFHost
     *            the prodFHost to set
     */
    public void setProdFHost(final String prodFHost) {
        this.prodFHost = prodFHost;
    }

    /**
     * Gets the prod f port.
     * @return the prodFPort
     */
    public Integer getProdFPort() {
        return prodFPort;
    }

    /**
     * Sets the prod f port.
     * @param prodFPort
     *            the prodFPort to set
     */
    public void setProdFPort(final Integer prodFPort) {
        this.prodFPort = prodFPort;
    }

    /**
     * Gets the dev f host.
     * @return the devFHost
     */
    public String getDevFHost() {
        return devFHost;
    }

    /**
     * Sets the dev f host.
     * @param devFHost
     *            the devFHost to set
     */
    public void setDevFHost(final String devFHost) {
        this.devFHost = devFHost;
    }

    /**
     * Gets the dev f port.
     * @return the devFPort
     */
    public Integer getDevFPort() {
        return devFPort;
    }

    /**
     * Sets the dev f port.
     * @param devFPort
     *            the devFPort to set
     */
    public void setDevFPort(final Integer devFPort) {
        this.devFPort = devFPort;
    }

    /**
     * Gets the hand shake timeout.
     * @return the hand shake timeout
     */
    public int getHandShakeTimeout() {
        return handShakeTimeout;
    }

    /**
     * Sets the hand shake timeout.
     * @param handShakeTimeout
     *            the new hand shake timeout
     */
    public void setHandShakeTimeout(final int handShakeTimeout) {
        this.handShakeTimeout = handShakeTimeout;
    }

    /**
     * Gets the batch accept count.
     * @return the batch accept count
     */
    public int getBatchAcceptCount() {
        return batchAcceptCount;
    }

    /**
     * Sets the batch accept count.
     * @param batchAcceptCount
     *            the new batch accept count
     */
    @Value("${APNSConfig.batchAcceptCount}")
    public void setBatchAcceptCount(final int batchAcceptCount) {
        this.batchAcceptCount = batchAcceptCount;
        if (this.batchAcceptCount < ApnsConfig.AV_PROCESSORS) {
            this.batchAcceptCount = ApnsConfig.AV_PROCESSORS;
        }
    }

    /**
     * Gets the batch processing count.
     * @return the batch processing count
     */
    public int getBatchProcessingCount() {
        return batchProcessingCount;
    }

    /**
     * Sets the batch processing count.
     * @param batchProcessingCount
     *            the new batch processing count
     */
    @Value("${APNSConfig.batchProcessingCount}")
    public void setBatchProcessingCount(final int batchProcessingCount) {
        this.batchProcessingCount = batchProcessingCount;
        if (this.batchProcessingCount < ApnsConfig.AV_PROCESSORS) {
            this.batchProcessingCount = ApnsConfig.AV_PROCESSORS;
        }
    }

    /**
     * Gets the batch accept conn count.
     * @return the batch accept conn count
     */
    public int getBatchAcceptConnCount() {
        return batchAcceptConnCount;
    }

    /**
     * Sets the batch accept conn count.
     * @param batchAcceptConnCount
     *            the new batch accept conn count
     */
    public void setBatchAcceptConnCount(final int batchAcceptConnCount) {
        this.batchAcceptConnCount = batchAcceptConnCount;
    }

    /**
     * Gets the batch procss conn count.
     * @return the batch procss conn count
     */
    public int getBatchProcssConnCount() {
        return batchProcssConnCount;
    }

    /**
     * Sets the batch procss conn count.
     * @param batchProcssConnCount
     *            the new batch procss conn count
     */
    public void setBatchProcssConnCount(final int batchProcssConnCount) {
        this.batchProcssConnCount = batchProcssConnCount;
    }

    /**
     * Gets the max queue size.
     * @return the max queue size
     */
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    /**
     * Sets the max queue size.
     * @param maxQueueSize
     *            the new max queue size
     */
    public void setMaxQueueSize(final int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    /**
     * Gets the max batch queue size.
     * @return the max batch queue size
     */
    public int getMaxBatchQueueSize() {
        return maxBatchQueueSize;
    }

    /**
     * Sets the max batch queue size.
     * @param maxBatchQueueSize
     *            the new max batch queue size
     */
    public void setMaxBatchQueueSize(final int maxBatchQueueSize) {
        this.maxBatchQueueSize = maxBatchQueueSize;
    }

    /**
     * Gets the max batch queue poller threads.
     * @return the max batch queue poller threads
     */
    public int getMaxBatchQueuePollerThreads() {
        return maxBatchQueuePollerThreads;
    }

    /**
     * Sets the max batch queue poller threads.
     * @param maxBatchQueuePollerThreads
     *            the new max batch queue poller threads
     */
    public void setMaxBatchQueuePollerThreads(
            final int maxBatchQueuePollerThreads) {
        this.maxBatchQueuePollerThreads = maxBatchQueuePollerThreads;
    }

}
