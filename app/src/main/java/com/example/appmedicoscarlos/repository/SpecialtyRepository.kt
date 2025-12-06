package com.example.appmedicoscarlos.repository

import com.example.appmedicoscarlos.models.AppointmentCreateRequest
import com.example.appmedicoscarlos.models.AppointmentResponseDto
import com.example.appmedicoscarlos.models.DoctorCreate
import com.example.appmedicoscarlos.models.DoctorResponse
import com.example.appmedicoscarlos.models.SpecialtyResponseDto
import com.example.appmedicoscarlos.providers.VitalTimeService
import com.example.appmedicoscarlos.utils.ErrorUtils
import retrofit2.HttpException
import retrofit2.Response

class SpecialtyRepository(private val vitalTimeService: VitalTimeService) {

    suspend fun getAllSpecialties(): Result<List<SpecialtyResponseDto>> {
        return try {
            val response = vitalTimeService.getAllSpecialties()
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}