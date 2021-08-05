package com.bu.selfstudy.data.repository

import com.bu.selfstudy.R
import com.bu.selfstudy.SelfStudyApplication
import com.bu.selfstudy.data.model.Book
import com.bu.selfstudy.data.AppDatabase.Companion.getDatabase
import com.bu.selfstudy.data.local.LoadLocalBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//use coroutine, try-catch, don't write many code here
object BookRepository {
    private val bookDao = getDatabase().bookDao()


    //load
    fun loadOneBook(bookId: Long) = bookDao.loadOneBook(bookId)

    fun loadBooks() = bookDao.loadBooks()

    fun loadBooksArchive() = bookDao.loadBooksArchived()

    suspend fun loadLocalBookNames() = LoadLocalBook.loadNames()


    //insert
    suspend fun insertBook(book: Book) = withContext(Dispatchers.IO){

        return@withContext bookDao.insert(book).also { idList->

            if(book.colorInt == 0){
                val intArray = SelfStudyApplication.context.resources
                    .getIntArray(R.array.book_color_list)

                bookDao.updateColorInt(idList[0], intArray[idList[0].toInt() % intArray.size])
            }
        }
    }


    suspend fun insertLocalBook(bookName: String) = withContext(Dispatchers.IO) {
        val bookId = insertBook(
            Book(
                memberId = SelfStudyApplication.memberId,
                bookName = bookName
            )
        )[0]

        LoadLocalBook.loadBookData(bookName).let { wordList->
            wordList.forEach { it.bookId = bookId }

            WordRepository.insertWord(*wordList.toTypedArray()).also { idList->
                return@withContext if(idList.isNotEmpty())
                    "新增成功"
                else
                    "新增失敗"
            }
        }
    }

    //update
    suspend fun updateBook(vararg book: Book) = withContext(Dispatchers.IO){
        bookDao.update(*book)
    }

    suspend fun updatePosition(bookId: Long, position: Int) = withContext(Dispatchers.IO){
        bookDao.updatePosition(bookId, position)
    }

    suspend fun updateBookSize() = withContext(Dispatchers.IO){
        bookDao.updateSize()
    }

    suspend fun updateIsArchive(bookId: Long, isArchive: Boolean) = withContext(Dispatchers.IO){
        bookDao.updateIsArchive(bookId, isArchive)
    }

    suspend fun updateColor(bookId: Long, colorInt: Int) {
        bookDao.updateColorInt(bookId, colorInt)
    }

    //delete
    suspend fun delete(bookId: Long) = withContext(Dispatchers.IO){
        bookDao.delete(bookId)
    }
}