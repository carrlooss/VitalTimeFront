package com.example.appmedicoscarlos.repository

import com.example.appmedicoscarlos.models.DoctorCreate
import com.example.appmedicoscarlos.models.DoctorResponse
import com.example.appmedicoscarlos.models.DoctorUpdateRequest
import com.example.appmedicoscarlos.models.PatientResponseDto
import com.example.appmedicoscarlos.providers.VitalTimeService
import com.example.appmedicoscarlos.utils.ErrorUtils
import kotlin.Result

class DoctorRepository(private val vitalTimeService: VitalTimeService) {

    suspend fun createDoctor(doctorCreate: DoctorCreate): Result<DoctorResponse> {
        return try {
            val response = vitalTimeService.createDoctor(doctorCreate)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDoctor(id: Long): Result<Unit> {
        return try {
            val response = vitalTimeService.deleteDoctor(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorJson = response.errorBody()?.string()
                val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDoctor(id: Long): Result<DoctorResponse> {
        return try {
            val response = vitalTimeService.getDoctor(id)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllDoctor(): Result<List<DoctorResponse>> {
        return try {
            val response = vitalTimeService.getAllDoctor()
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateDoctor(id: Long, updateRequest: DoctorUpdateRequest): Result<DoctorResponse> {
        return try {
            val response = vitalTimeService.updateDoctor(id, updateRequest)
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