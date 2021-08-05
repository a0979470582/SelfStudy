package com.bu.selfstudy.ui.archive

import android.os.Bundle
import androidx.lifecycle.*
import com.bu.selfstudy.SelfStudyApplication
import com.bu.selfstudy.data.model.Book
import com.bu.selfstudy.data.repository.BookRepository
import com.bu.selfstudy.tool.SingleLiveData
import com.bu.selfstudy.tool.putBundle
import kotlinx.coroutines.launch


class ArchiveViewModel : ViewModel(){
    val databaseEvent = SingleLiveData<Pair<String, Bundle?>>()

    val bookListLiveData = BookRepository.loadBooksArchive().asLiveData()

    fun calculateBookSize(){
        viewModelScope.launch {
            BookRepository.updateBookSize()
        }
    }

    var chosenBook: Book? = null


    fun insertBook(bookName: String){
        viewModelScope.launch {
            val book = Book(bookName = bookName, memberId = SelfStudyApplication.memberId)
            if(BookRepository.insertBook(book).isNotEmpty()) {
                databaseEvent.postValue("insertBook" to null)
            }
        }
    }

    //, explanation:String
    fun editBook(bookName:String){
        viewModelScope.launch{
            chosenBook?.copy()?.let {
                it.bookName = bookName
                if(BookRepository.updateBook(it)>0) {
                    databaseEvent.postValue("update" to null)
                }
            }
        }
    }

    fun deleteBook(){
        viewModelScope.launch {
            chosenBook?.let {
                if(BookRepository.delete(it.id) > 0)
                    databaseEvent.postValue("delete" to putBundle("bookName", it.bookName))
            }
        }
    }

    fun archiveBook(isArchive: Boolean){
        viewModelScope.launch {
            chosenBook?.let {
                if(BookRepository.updateIsArchive(it.id, isArchive) > 0)
                    databaseEvent.postValue("archive" to putBundle("bookName", it.bookName))
            }
        }
    }

    fun updateBookColor(colorInt: Int) {
        viewModelScope.launch {
            chosenBook?.let {
                BookRepository.updateColor(it.id, colorInt)
            }
        }
    }

}