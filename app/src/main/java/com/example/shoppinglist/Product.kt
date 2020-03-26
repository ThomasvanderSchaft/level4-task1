package com.example.shoppinglist

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
//assigning the data class to be an entity of the databse
data class Product(
    //assigns primary key to table
    @PrimaryKey(autoGenerate = true)
    //assigns variable columns
    @ColumnInfo(name = "id") val id: Long? = null,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "amount") val amount: String?
) : Parcelable