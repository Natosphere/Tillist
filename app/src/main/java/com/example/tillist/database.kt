package com.example.tillist

import android.content.Context
import androidx.room.*
import java.text.SimpleDateFormat
import java.util.*


@Entity
public data class Receipt(
    @ColumnInfo(name = "total_price") val totalPrice: Double?,
    @ColumnInfo(name = "store_name") val storeName: String?,
    @ColumnInfo(name = "date_of_purchase") val dateOfPurchase: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "tag") val tag: String? = "",
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "timeStamp") val timeStamp: String = SimpleDateFormat("yyyy/MM/dd hh:mm").format(Calendar.getInstance().time),
)



@Dao
interface ReceiptDao {
    // get all receipts
    @Query("SELECT * FROM receipt")
    fun getAll(): MutableList<Receipt>

    // pass in list of ID, return matched receipts
    @Query("SELECT * FROM receipt WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Receipt>

    // get all receipts with a certain tag
    @Query("SELECT * FROM receipt WHERE tag LIKE :tagSearch")
    fun loadAllByTag(tagSearch: String): List<Receipt>

    // get all previously created tags
    @Query("SELECT DISTINCT tag FROM receipt")
    fun getTagList(): List<String>

    @Query("SELECT * FROM receipt ORDER BY id DESC LIMIT :number")
    fun getRecent(number: Int): List<Receipt>

    @Update
    fun updateReceipt(receipt: Receipt)

    @Insert
    fun insertAll(vararg receipts: Receipt)

    @Delete
    fun delete(receipt: Receipt)
}


@Database(entities = [Receipt::class], version = 1)
abstract class ReceiptDatabase : RoomDatabase() {

    abstract fun receiptDao(): ReceiptDao

    companion object {

        // prevents multiple instances being open at the same time
        @Volatile
        private var INSTANCE: ReceiptDatabase? = null

        fun getDatabase(context: Context): ReceiptDatabase {
            // if instance is not null, return it. If null, create database.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReceiptDatabase::class.java,
                    "receipt_database"
                ).allowMainThreadQueries().build() // TODO learn and implement coroutines to get rid of main thread hog
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}