package edu.uoc.rng2.usecases

import edu.uoc.rng2.repositories.DataRepository
import javax.inject.Inject

class SyncUseCase @Inject constructor(
    private val dataRepository: DataRepository
) {

    suspend fun execute() {
        dataRepository.syncData()
    }
}
