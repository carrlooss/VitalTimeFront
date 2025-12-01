package com.example.appmedicoscarlos.providers

import com.example.appmedicoscarlos.models.AppointmentCreateRequest
import com.example.appmedicoscarlos.models.AppointmentResponseDto
import com.example.appmedicoscarlos.models.AvailabilityResponse
import com.example.appmedicoscarlos.models.DoctorCreate
import com.example.appmedicoscarlos.models.DoctorResponse
import com.example.appmedicoscarlos.models.DoctorUpdateRequest
import com.example.appmedicoscarlos.models.PatientCreateRequest
import com.example.appmedicoscarlos.models.PatientResponseDto
import com.example.appmedicoscarlos.models.PatientUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VitalTimeService {

    // === DOCTORS ===

    @POST("doctors")
    suspend fun createDoctor(@Body request: DoctorCreate): DoctorResponse

    @DELETE("doctors/{id}")
    suspend fun deleteDoctor(@Path("id") userId: Long): Response<Void>

    @GET("doctors/{id}")
    suspend fun getDoctor( @Path("id") userId: Long): DoctorResponse

    @PATCH("doctors/{id}")
    suspend fun updateDoctor(@Path("id") id: Long, @Body request: DoctorUpdateRequest): DoctorResponse

    @GET("doctors/getAll")
    suspend fun getAllDoctor(): List<DoctorResponse>

    @POST("appointments")
    suspend fun createAppointment(@Body request: AppointmentCreateRequest): AppointmentResponseDto

    // === PATIENTS ===

    @GET("patients/{id}")
    suspend fun getPatientById(@Path("id") id: Long): PatientResponseDto

    @DELETE("patients/{id}")
    suspend fun deletePatient(@Path("id") id: Long): Response<Void>

    @PATCH("patients/{id}")
    suspend fun updatePatient(@Path("id") id: Long?, @Body request: PatientUpdateRequest): PatientResponseDto

    @GET("patients/getAll")
    suspend fun getAllPatients(): List<PatientResponseDto>

    @POST("patients")
    suspend fun createPatient(@Body request: PatientCreateRequest): PatientResponseDto

    // === APPOIMENTS ===

    @DELETE("appointments/{id}")
    suspend fun deleteAppointment(@Path("id") id: Long): Response<Void>

    @GET("appointments/patient/{patientId}")
    suspend fun getAppointmentsByPatientId(@Path("patientId") patientId: Long): Response<List<AppointmentResponseDto>>

    @GET("appointments/doctor/{doctorId}")
    suspend fun getAppointmentsByDoctorId(@Path("doctorId") doctorId: Long): Response<List<AppointmentResponseDto>>

    // === AVAILABILITY ===

    @GET("availability/doctor/{doctorId}")
    suspend fun getAvailability(@Path("doctorId") doctorId: Long, @Query("from") from: String, @Query("to") to: String): AvailabilityResponse

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/" // Reemplaza por tu URL

        fun getService(): VitalTimeService {
            return RetrofitInstance.getRetrofit(BASE_URL).create(VitalTimeService::class.java)
        }
        
    }
}


