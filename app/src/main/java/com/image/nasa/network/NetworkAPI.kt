package com.image.nasa.network

import android.annotation.SuppressLint
import android.util.Log
import com.image.nasa.BuildConfig
import com.image.nasa.MyApplication
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.IOException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object NetworkAPI {
    private var retrofit: Retrofit? = null

    //10 MB
    @JvmStatic
    val client: Retrofit?
        get() {
            val httpCacheDirectory = File(MyApplication.context?.cacheDir, "offlineCache")

            //10 MB
            val cache = Cache(httpCacheDirectory, 10 * 1024 * 1024)
            val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(s: String) {
                    Log.d("Retrofit: OkHttp:", s)
                }
            })

            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addNetworkInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val original = chain.request()
                            val request = original.newBuilder()
                                    .removeHeader("Pragma")
                                    .build()
                            val response = chain.proceed(request)

                            val cacheControl = CacheControl.Builder()
                                    .maxAge(5, TimeUnit.SECONDS)
                                    .build()

                            return response.newBuilder()
                                    .removeHeader("Pragma")
                                    .header("Cache-Control", cacheControl.toString())
                                    .build()
                        }
                    })
                    .addInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val original = chain.request()
                            return try {
                                chain.proceed(original)
                            } catch (e: Exception) {

                                val cacheControl = CacheControl.Builder()
                                        .maxStale(30, TimeUnit.DAYS)
                                        .build()

                                val request = original.newBuilder()
                                        .removeHeader("Pragma")
                                        .header("Cache-Control", cacheControl.toString())
                                        .build()

                                chain.proceed(request)
                            }
                        }
                    })
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .cache(cache)
                    .build()

            val client = trustAllSslClient(okHttpClient)

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(BuildConfig.BASEURL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
            }
            return retrofit
        }

    @SuppressLint("TrustAllX509TrustManager")
    private val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
    )
    private var trustAllSslContext: SSLContext? = null

    init {
        try {
            trustAllSslContext = SSLContext.getInstance("SSL")
            trustAllSslContext!!.init(null, trustAllCerts, SecureRandom())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: KeyManagementException) {
            throw RuntimeException(e)
        }
    }

    private val trustAllSslSocketFactory = trustAllSslContext!!.socketFactory

    private fun trustAllSslClient(client: OkHttpClient): OkHttpClient {
        val builder = client.newBuilder()
        builder.sslSocketFactory(trustAllSslSocketFactory, (trustAllCerts[0] as X509TrustManager))
        builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
        return builder.build()
    }
}