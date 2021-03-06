package com.bu.selfstudy.data.model

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(indices = [
        Index(value=["wordName"]),
        Index(value=["example"]),
        Index(value=["bookId", "wordName"]),
        Index(value=["bookId", "timestamp"])
    ],
    foreignKeys = [
        ForeignKey(entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE)
    ]
)
@Parcelize
data class Word(
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0,
    var bookId: Long = 0,
    var wordName: String = "",
    var pronunciation: String="",
    var translation: String="",
    var variation: String="",
    var example: String="",
    var synonyms: String="",
    var note: String = "",

    var audioFilePath:String="",//ex: reserve.mp3, dir: /data/data/<application package>/files/
    var dictionaryPath: String="",//a file about complete word data from api
    var isMark: Boolean = false,

    var timestamp: Date = Date(),
):Parcelable