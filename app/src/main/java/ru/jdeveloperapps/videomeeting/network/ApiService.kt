package ru.jdeveloperapps.videomeeting.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ApiService {

    @POST("send")
    suspend fun sendRemoteMessage(
        @HeaderMap headers: Map<String, String>,
        @Body remoteBody: String
    ) : Response<String>
}