package com.example.appmedicoscarlos.repository

import com.example.appmedicoscarlos.models.AppointmentCreateRequest
import com.example.appmedicoscarlos.models.AppointmentResponseDto
import com.example.appmedicoscarlos.models.PatientCreateRequest
import com.example.appmedicoscarlos.models.PatientResponseDto
import com.example.appmedicoscarlos.models.PatientUpdateRequest
import com.example.appmedicoscarlos.providers.VitalTimeService
import com.example.appmedicoscarlos.utils.ErrorUtils
import retrofit2.Response

class PatientRepository(private val vitalTimeService: VitalTimeService) {
    suspend fun getPatientById(id: Long): Result<PatientResponseDto> {
        return try {
            val response = vitalTimeService.getPatientById(id)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePatient(id: Long): Result<Unit> {
        return try {
            val response = vitalTimeService.deletePatient(id)
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

    suspend fun updatePatient(id: Long?, updateDto: PatientUpdateRequest): Result<PatientResponseDto> {
        return try {
            val response = vitalTimeService.updatePatient(id, updateDto)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllPatients(): Result<List<PatientResponseDto>> {
        return try {
            val response = vitalTimeService.getAllPatients()
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorMessage = ErrorUtils.parseErrorMessage(errorJson)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPatient(dto: PatientCreateRequest): Result<PatientResponseDto> {
        return try {
            val response = vitalTimeService.createPatient(dto)
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