package com.lighthouse.swm_logging

import com.lighthouse.swm_logging.logging_scheme.SWMLoggingScheme
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface LoggingService {

    @POST("{loggingPath}")
    suspend fun postLogging(
        @Path("loggingPath") loggingPath: String,
        @Body loggingScheme: SWMLoggingScheme
    ): Response<BaseDTO>
}

