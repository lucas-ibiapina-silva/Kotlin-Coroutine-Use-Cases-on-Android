package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase8

import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AndroidVersionRepository(
    private var database: AndroidVersionDao,
    private val scope: CoroutineScope,
    private val api: MockApi = mockApi()
) {
    suspend fun getLocalAndroidVersions() : List<AndroidVersion> {
        return database.getAndroidVersions().mapToUiModelList()
    }

    suspend fun loadAndRemoteAndroidVersions() {
        scope.async {
            val recentVersions = api.getRecentAndroidVersions()
            for (recentVersion in recentVersions) {
                database.insert(recentVersion.mapToEntity())
            }
            recentVersions
        }.await()
    }

    fun clearDatabase() {
        scope.launch {
            database.clear()
        }
    }
}