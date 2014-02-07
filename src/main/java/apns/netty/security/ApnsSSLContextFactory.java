/*
 * ApnsSSLContextFactory.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.security;

import io.netty.handler.ssl.SslHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apns.netty.config.ApnsConfig;

/**
 * Creates a bogus {@link SSLContext}. A client-side context created by this
 * factory accepts any certificate even if it is invalid. A server-side context
 * created by this factory sends a bogus certificate defined in
 * {@link SecureChatKeyStore}.
 * <p>
 * You will have to create your context differently in a real world application.
 * <h3>Client Certificate Authentication</h3> To enable client certificate
 * authentication:
 * <ul>
 * <li>Enable client authentication on the server side by calling
 * {@link SSLEngine#setNeedClientAuth(boolean)} before creating
 * {@link SslHandler}.</li>
 * <li>When initializing an {@link SSLContext} on the client side, specify the
 * {@link KeyManager} that contains the client certificate as the first argument
 * of {@link SSLContext#init(KeyManager[], TrustManager[], SecureRandom)}.</li>
 * <li>When initializing an {@link SSLContext} on the server side, specify the
 * proper {@link TrustManager} as the second argument of
 * {@link SSLContext#init(KeyManager[], TrustManager[], SecureRandom)} to
 * validate the client certificate.</li>
 * </ul>
 */
@Component
public final class ApnsSSLContextFactory {

    /** The Constant SUNX509. */
    private static final String SUNX509 = "sunx509";

    /** The Constant SSL_KEY_MANAGER_FACTORY_ALGORITHM. */
    private static final String SSL_KEY_MANAGER_FACTORY_ALGORITHM = "ssl.KeyManagerFactory.algorithm";

    /** The apns config. */
    @Autowired
    private ApnsConfig apnsConfig;

    /* The algorithm used by KeyManagerFactory */
    /** The Constant ALGORITHM. */
    private static final String ALGORITHM = Security
            .getProperty(ApnsSSLContextFactory.SSL_KEY_MANAGER_FACTORY_ALGORITHM) == null ? ApnsSSLContextFactory.SUNX509
            : Security
                    .getProperty(ApnsSSLContextFactory.SSL_KEY_MANAGER_FACTORY_ALGORITHM);

    /* The protocol used to create the SSLSocket */
    /** The Constant PROTOCOL. */
    private static final String PROTOCOL = "TLS";

    /* PKCS12 */
    /** The Constant KEYSTORE_TYPE_PKCS12. */
    public static final String KEYSTORE_TYPE_PKCS12 = "PKCS12";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Gets the sSL context.
     * @return the sSL context
     */
    public SSLContext getSSLContext() {
        SSLContext sslc = null;

        // SSLContext serverContext;

        // try {
        //
        // // InputStream keystoreStream = streamKeystore(keystore);
        // // if (keystoreStream instanceof WrappedKeystore) return
        // // ((WrappedKeystore) keystoreStream).getKeystore();
        // // KeyStore keyStore;
        // // try {
        // // keyStore = KeyStore.getInstance(server.getKeystoreType());
        // // char[] password =
        // // KeystoreManager.getKeystorePasswordForSSL(server);
        // // keyStore.load(keystoreStream, password);
        // // } catch (Exception e) {
        // // throw wrapKeystoreException(e);
        // // } finally {
        // // try {
        // // keystoreStream.close();
        // // } catch (Exception e) {
        // // }
        // // }
        // //
        //
        // final KeyStore ks = KeyStore
        // .getInstance(ApnsSSLContextFactory.KEYSTORE_TYPE_PKCS12);
        // ks.load(new BufferedInputStream(new FileInputStream(new File(
        // apnsConfig.getCertificatePath()))), apnsConfig
        // .getPassword().toCharArray());
        //
        // // Set up key manager factory to use our key store
        // final KeyManagerFactory kmf = KeyManagerFactory
        // .getInstance(ApnsSSLContextFactory.ALGORITHM);
        // kmf.init(ks, apnsConfig.getPassword().toCharArray());
        //
        // // Initialize the SSLContext to work with our key managers.
        // serverContext = SSLContext
        // .getInstance(ApnsSSLContextFactory.PROTOCOL);
        // serverContext.init(kmf.getKeyManagers(), null, null);
        // } catch (final Exception e) {
        // throw new Error(
        // "Failed to initialize the server-side SSLContext", e);
        // }

        try {
            final KeyStore keystore = KeyStore
                    .getInstance(ApnsSSLContextFactory.KEYSTORE_TYPE_PKCS12);
            keystore.load(new BufferedInputStream(new FileInputStream(
                    new File(apnsConfig.getCertificatePath()))), apnsConfig
                    .getPassword().toCharArray());
            final KeyManagerFactory kmf = KeyManagerFactory
                    .getInstance(ApnsSSLContextFactory.ALGORITHM);
            try {
                final char[] password = apnsConfig.getPassword()
                        .toCharArray();
                kmf.init(keystore, password);
            } catch (final Exception e) {

                throw e;
            }

            // Get the SSLContext to help create SSLSocketFactory
            sslc = SSLContext.getInstance(ApnsSSLContextFactory.PROTOCOL);
            sslc.init(kmf.getKeyManagers(),
                    new TrustManager[] { new ServerTrustingTrustManager() },
                    null);

        } catch (final Exception e) {
            // throw new KeystoreException("Keystore exception: "
            // + e.getMessage(), e);
        }

        return sslc;
    }
}
