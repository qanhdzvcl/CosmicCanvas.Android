package com.emrepbu.cosmiccanvas.data.api.interceptors

import android.content.Context
import com.emrepbu.cosmiccanvas.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            throw NoConnectivityException()
        }
        
        val request = chain.request()
        return chain.proceed(request)
    }
    
    class NoConnectivityException : IOException("No Internet connection available")
}