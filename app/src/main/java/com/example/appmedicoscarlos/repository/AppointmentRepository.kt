package com.example.appmedicoscarlos.repository

import com.example.appmedicoscarlos.models.AppointmentCreateRequest
import com.example.appmedicoscarlos.models.AppointmentResponseDto
import com.example.appmedicoscarlos.models.DoctorCreate
import com.example.appmedicoscarlos.models.DoctorResponse
import com.example.appmedicoscarlos.providers.VitalTimeService
import com.example.appmedicoscarlos.utils.ErrorUtils
import retrofit2.Response

class AppointmentRepository(private val vitalTimeService: VitalTimeService) {

    suspend fun createAppointment(appointmentCreate: AppointmentCreateRequest): Result<AppointmentResponseDto> {
        return try {
            val response = vitalTimeService.createAppointment(appointmentCreate)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAppointmentsByPatientId(patientId: Long): Result<List<AppointmentResponseDto>> {
        return try {
            val response = vitalTimeService.getAppointmentsByPatientId(patientId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAppointmentsByDoctorId(doctorId: Long): Result<List<AppointmentResponseDto>> {
        return try {
            val response = vitalTimeService.getAppointmentsByDoctorId(doctorId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAppointment(appointmentId: Long): Result<Unit> {
        return try {
            val response = vitalTimeService.deleteAppointment(appointmentId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}