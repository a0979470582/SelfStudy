package com.bu.selfstudy.ui.recentword


import androidx.lifecycle.*
import com.bu.selfstudy.data.model.RecentWord
import com.bu.selfstudy.data.repository.RecentWordRepository
import kotlinx.coroutines.launch

/**
 * 更改資料庫中的wordList都會透過LiveData反應在目前的ViewPager, 例如新增,刪除,修改都會同步到資料庫,
 * 接著透過LiveData傳回並在ViewPager看到更新, 而wordFragment的生命週期進入onStop時, 會同步目前使用者所
 * 看到的單字頁(wordId)到資料庫
 */
class RecentWordViewModel() : ViewModel() {
    fun refreshRecentWord(recentWord: RecentWord) {
        viewModelScope.launch {
            RecentWordRepository.refreshRecentWord(recentWord)
        }
    }

    val recentWordLiveData = RecentWordRepository.loadRecentWord().asLiveData()

}