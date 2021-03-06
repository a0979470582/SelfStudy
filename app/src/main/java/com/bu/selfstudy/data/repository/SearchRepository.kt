package com.bu.selfstudy.data.repository

import com.bu.selfstudy.data.AppDatabase
import com.bu.selfstudy.data.local.LoadSearchAutoComplete
import com.bu.selfstudy.data.model.SearchAutoComplete
import com.bu.selfstudy.data.model.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

//handle Search Suggestion
object SearchRepository {
    private val historyDao = AppDatabase.getDatabase().searchHistoryDao()
    private val autoCompleteDao = AppDatabase.getDatabase().searchAutoCompleteDao()

    //select
    fun loadHistory(query: String) = historyDao.loadHistory(query.trim())
    fun loadAutoComplete(query: String) = autoCompleteDao.loadAutoComplete(query.trim())

    //insert
    suspend fun insertHistory(searchName: String) = withContext(Dispatchers.IO){
        if(searchName.isBlank())
            return@withContext

        historyDao.insertHistory(SearchHistory(searchName=searchName.trim())).also {
            autoCompleteDao.setIsHistory(searchName, isHistory = true)
        }
    }


    //prepare DB
    suspend fun insertLocalAutoComplete(fileName: String) = withContext(Dispatchers.IO){
        val autoCompleteList = LoadSearchAutoComplete.loadData(fileName)
        autoCompleteList.iterator().forEach {
            it.isHistory = historyDao.checkHistoryExists(it.searchName)
        }
        autoCompleteDao.insertAutoComplete(*autoCompleteList.toTypedArray())
    }

    //delete
    suspend fun deleteHistory(history: SearchHistory) = withContext(Dispatchers.IO){
        historyDao.delete(history).also {
            autoCompleteDao.setIsHistory(history.searchName, isHistory = false)
        }
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO){
        autoCompleteDao.detachAllHistory()
        historyDao.deleteAll()
    }

    //prepare db
    suspend fun checkAutoComplete() = withContext(Dispatchers.IO){
        val autoCompleteList  = autoCompleteDao.loadAutoComplete("").first()

        autoCompleteList.forEach first@{ autoComplete ->
            if(autoComplete.searchName.length <= 1) {
                autoCompleteDao.delete(autoComplete)
                return@first //next autoComplete check
            }
            autoComplete.searchName.forEach { char->
                if(!char.isLowerCase()) {
                    autoCompleteDao.delete(autoComplete)
                    return@first //next autoComplete check
                }
            }
        }
    }

}