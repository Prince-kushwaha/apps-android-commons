package fr.free.nrw.commons.upload.depicts

import androidx.room.*
import fr.free.nrw.commons.upload.structure.depictions.DepictedItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

@Dao
abstract class DepictsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(depictedItem: Depicts);

    @Query("Select * From depicts_table order by lastUsed DESC")
    abstract suspend fun getAllDepict(): List<Depicts>;

    @Query("Select * From depicts_table order by lastUsed DESC LIMIT :n OFFSET 10")
    abstract suspend fun getItemToDelete(n: Int): List<Depicts>;

    @Delete
    abstract suspend fun delete(depicts: Depicts);

    lateinit var allDepict: List<Depicts>;
    lateinit var listOfDelete: List<Depicts>;

    fun depictsList(): List<Depicts> {
        runBlocking {
            launch(Dispatchers.IO) {
                allDepict = getAllDepict();
            }
        }
        return allDepict;
    }

    /**
     *  insert Depicts  in DepictsRoomDataBase
     */
    fun insertDepict(depictes: Depicts) {
        runBlocking {
            launch(Dispatchers.IO) {
                insert(depictes);
            }
        }
    }

    /**
     *  get all Depicts item which need to delete
     */
    fun getItemTodelete(number: Int): List<Depicts> {
        runBlocking {
            launch(Dispatchers.IO) {
                listOfDelete = getItemToDelete(number);
            }
        }
        return listOfDelete;
    }

    /**
     *  delete Depicts  in DepictsRoomDataBase
     */
    fun deleteDepicts(depictes: Depicts) {
        runBlocking {
            launch(Dispatchers.IO) {
                delete(depictes);
            }
        }
    }

    /**
     *  save Depicts in DepictsRoomDataBase
     */
    fun savingDepictsInRoomDataBase(listDepictedItem: List<DepictedItem>) {
        var numberofItemInRoomDataBase: Int
        val maxNumberOfItemSaveInRoom = 10

        for (depictsItem in listDepictedItem) {
            depictsItem.isSelected = false
            insertDepict(Depicts(depictsItem, Date()))
        }

        numberofItemInRoomDataBase = depictsList().size;
        // delete the depicts for depictsroomdataBase when number of element in depictsroomdataBase is greater than 10
        if (numberofItemInRoomDataBase > maxNumberOfItemSaveInRoom) {

            val listOfDepictsToDelete: List<Depicts> =
                getItemTodelete(numberofItemInRoomDataBase)
            for (i in listOfDepictsToDelete) {
                deleteDepicts(i)
            }
        }
    }
}