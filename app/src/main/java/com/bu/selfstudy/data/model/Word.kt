package com.bu.selfstudy.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(indices = [
        Index(value=["wordName"]),
        Index(value=["translation"]),
        Index(value=["variation"]),
        Index(value=["example"])
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
    var bookId: Long,
    var wordName: String,
    var pronounceString: String="",
    var pronounceUri: String="",
    var translation: String="",
    var variation: String="",
    var example: String="",
    var dictionaryUri: String="",
    var note: String = "",
    var timestamp: Date = Date(),
    var isTrash:Boolean = false
):Parcelable