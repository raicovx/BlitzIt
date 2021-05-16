package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Invoice
import au.com.blitzit.roomdata.LineItem

@Dao
interface LineItemDAO
{
    @Query("SELECT * FROM LineItem WHERE invoice_id = :invoiceID")
    suspend fun getLineItemsByInvoice(invoiceID: String): List<LineItem>

    @Query("SELECT * FROM LineItem WHERE plan_id = :planId AND invoice_id = :invoiceId AND supportCode = :supportCode AND total = :total LIMIT 1")
    suspend fun getLineItem(planId: String, invoiceId: String, supportCode: String, total: Double): LineItem

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLineItem(lineItem: LineItem)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateLineItem(lineItem: LineItem)

    @Transaction
    suspend fun upsertLineItem(lineItem: LineItem)
    {
        try
        {
            insertLineItem(lineItem)
        }
        catch (e: SQLiteConstraintException)
        {
            updateLineItem(lineItem)
        }
    }
}