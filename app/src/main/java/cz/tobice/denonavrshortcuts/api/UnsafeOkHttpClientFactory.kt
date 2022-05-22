package cz.tobice.denonavrshortcuts.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Creates an OkHttpClient that ignores invalid HTTPs certificates.
 *
 * This is necessary for communication with the receiver as it uses self-signed certificates.
 *
 * Source: https://stackoverflow.com/a/63399149
 */
object UnsafeOkHttpClientFactory {
    /**
     * @param interceptors optional OkHttp client interceptors
     */
    fun getInstance(interceptors: List<Interceptor> = listOf()): OkHttpClient {
        return try {
            // Create a Trust Manager that does not validate certificate chains.
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting Trust Manager.
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an SSL socket factory with our all-trusting Manager.
            val sslSocketFactory = sslContext.socketFactory

            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)

            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers

            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager = trustManagers[0] as X509TrustManager

            OkHttpClient.Builder().apply {
                sslSocketFactory(sslSocketFactory, trustManager)
                hostnameVerifier(HostnameVerifier { _, _ -> true })
                interceptors.forEach { addInterceptor(it) }
            }.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
