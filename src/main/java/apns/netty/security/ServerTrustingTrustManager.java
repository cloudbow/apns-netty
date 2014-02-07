/*
 * ServerTrustingTrustManager.java
 * 
 **********************************************************************

             Copyright (c) 2013 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.security;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * A trust manager that automatically trusts all servers. Used to avoid having
 * handshake errors with Apple's sandbox servers.
 * @author Sylvain Pedneault
 */
class ServerTrustingTrustManager implements X509TrustManager {
    
    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    @Override
    public void checkClientTrusted(final X509Certificate[] chain,
            final String authType) throws CertificateException {
        throw new CertificateException("Client is not trusted.");
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    @Override
    public void checkServerTrusted(final X509Certificate[] chain,
            final String authType) {
        // trust all servers
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;// new X509Certificate[0];
    }
}