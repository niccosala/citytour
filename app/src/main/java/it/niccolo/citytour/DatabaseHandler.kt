package it.niccolo.citytour

import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.sql.SQLException

class DatabaseHandler(private val context : Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private var database = this.writableDatabase

    override fun onCreate(p0: SQLiteDatabase?) {
        try {
            p0?.execSQL(CREATE_TB_SPOTS)
            Log.d("dev-sqlitedb-creation", "Table Spots created")
            p0?.execSQL(CREATE_TB_VERSION)
            p0?.execSQL(INIT_TB_VERSION)
            Log.d("dev-sqlitedb-creation", "Table Version created")
        } catch (e : SQLException) {
            Log.d("dev-sqlitedb-creation", "Error creating DB: $e")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) { }

    fun getVersion() : Int {
        return try {
            val query = "SELECT * FROM $TB_VERSION"
            val cursor = database.rawQuery(query, null)
            if(cursor.moveToFirst())
                cursor.getString(cursor.getColumnIndex(COL_V)).toInt()
            else
                -1
        } catch (e : SQLException) {
            Log.d("dev-sqlitedb-version", "Error obtaining version: $e")
            -2
        }
    }

    fun clearSpots() {
        database.delete(TB_SPOTS, null, null)
        Log.d("dev-sqlitedb-writing", "Spots cleared")
    }

    fun addSpot(spot : Spot) {
        database.execSQL("INSERT INTO $TB_SPOTS (" +
                "$COL_NAME, $COL_SNIPPET, $COL_LAT, $COL_LGT, $COL_DESCRIPTION, $COL_IMAGEPATH) " +
                "VALUES (${DatabaseUtils.sqlEscapeString(spot.name)}, " +
                "${DatabaseUtils.sqlEscapeString(spot.snippet)}, " +
                "${spot.lat}, ${spot.lgt}, " +
                "${DatabaseUtils.sqlEscapeString(spot.description)}, " +
                "${DatabaseUtils.sqlEscapeString(spot.imagePath)})")
        Log.d("dev-sqlitedb-newspot", "Spot '${spot.name}' added")
    }

    private fun clearVersion() {
        database.delete(TB_VERSION, null, null)
        Log.d("dev-sqlitedb-writing", "Version cleared")
    }

    fun updateVersion(newVersion : Int) {
        clearVersion()
        database.execSQL("INSERT INTO $TB_VERSION ($COL_V) VALUES ($newVersion)")
        Log.d("dev-sqlitedb-updatedb", "DB updated: v.$newVersion")
    }

    fun dropDatabase() {
        context.deleteDatabase(DB_NAME)
        Log.d("dev-sqlitedb-dropdb", "DB dropped")
    }

    private companion object {
        // DB Info
        const val DB_NAME = "CityTourDB"
        const val DB_VERSION = 1
        // TB Spots
        const val TB_SPOTS = "Spots"
        const val COL_NAME = "name"
        const val COL_SNIPPET = "snippet"
        const val COL_LAT = "lat"
        const val COL_LGT = "lgt"
        const val COL_DESCRIPTION = "description"
        const val COL_IMAGEPATH = "imagePath"
        // TB Version
        const val TB_VERSION = "Version"
        const val COL_V = "v"
        // Init
        const val CREATE_TB_SPOTS =
            "CREATE TABLE $TB_SPOTS (" +
                "$COL_NAME VARCHAR(40) PRIMARY KEY, " +
                "$COL_SNIPPET VARCHAR(60), " +
                "$COL_LAT DOUBLE, " +
                "$COL_LGT DOUBLE, " +
                "$COL_DESCRIPTION VARCHAR(350), " +
                "$COL_IMAGEPATH VARCHAR(60)" +
            ")"
        const val CREATE_TB_VERSION =
            "CREATE TABLE $TB_VERSION (" +
                "$COL_V INT PRIMARY KEY" +
            ")"
        const val INIT_TB_VERSION =
            "INSERT INTO $TB_VERSION (" +
            "$COL_V) VALUES (1" +
        ")"
    }

}