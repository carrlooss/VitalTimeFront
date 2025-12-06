package com.example.appmedicoscarlos.repository

import com.example.appmedicoscarlos.models.DoctorScheduleCreateDto
import com.example.appmedicoscarlos.models.DoctorScheduleResponseDto
import com.example.appmedicoscarlos.providers.VitalTimeService
import com.example.appmedicoscarlos.utils.ErrorUtils

class DoctorScheduleRepository(private val vitalTimeService: VitalTimeService) {

    suspend fun createSchedule(dto: List<DoctorScheduleCreateDto>): Result<List<DoctorScheduleResponseDto>> {
        return try {
            val response = vitalTimeService.createSchedule(dto)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val error = e.response()?.errorBody()?.string()
            Result.failure(Exception(error ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSchedulesByDoctor(doctorId: Long): Result<List<DoctorScheduleResponseDto>> {
        return try {
            Result.success(vitalTimeService.getSchedulesByDoctor(doctorId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteScheduleByDoctorId(doctorId: Long): Result<Unit> {
        return try {
            val response = vitalTimeService.deleteScheduleByDoctorId(doctorId)
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
}