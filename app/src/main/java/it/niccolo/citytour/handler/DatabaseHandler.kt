package it.niccolo.citytour.handler

import android.annotation.SuppressLint
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import it.niccolo.citytour.entity.Spot
import java.sql.SQLException

@SuppressLint("Recycle")
class DatabaseHandler(private val context: Context) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
) {

    private var database = this.writableDatabase

    override fun onCreate(p0: SQLiteDatabase?) {
        try {
            p0?.execSQL(CREATE_TB_SPOTS)
            Log.d("dev-sqlitedb", "Table Spots created")
            p0?.execSQL(CREATE_TB_VERSION)
            p0?.execSQL(INIT_TB_VERSION)
            Log.d("dev-sqlitedb", "Table Version created")
        } catch (e: SQLException) {
            Log.d("dev-sqlitedb", "Error creating DB: $e")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) { }

    fun getVersion() : Int {
        return try {
            val cursor = database.rawQuery(GET_VERSION, null)
            if(cursor.moveToFirst())
                cursor.getString(cursor.getColumnIndex(COL_V)).toInt()
            else
                -1
        } catch (e: SQLException) {
            Log.d("dev-sqlitedb", "Error obtaining version: $e")
            -2
        }
    }

    fun clearSpots() {
        database.delete(TB_SPOTS, null, null)
        Log.d("dev-sqlitedb", "Spots cleared")
    }

    fun addSpot(spot: Spot) {
        Log.d("dev-sqlitedb", "${spot.lat}, ${spot.lgt}")
        database.execSQL(
            "INSERT INTO $TB_SPOTS (" +
                    "$COL_NAME, $COL_SNIPPET, $COL_LAT, $COL_LGT, $COL_DESCRIPTION, $COL_IMAGEPATH) " +
                    "VALUES (${DatabaseUtils.sqlEscapeString(spot.name)}, " +
                    "${DatabaseUtils.sqlEscapeString(spot.snippet)}, " +
                    "${spot.lat}, ${spot.lgt}, " +
                    "${DatabaseUtils.sqlEscapeString(spot.description)}, " +
                    "${DatabaseUtils.sqlEscapeString(spot.imagePath)})"
        )
        Log.d("dev-sqlitedb", "Spot '${spot.name}' added")
    }

    private fun clearVersion() {
        database.delete(TB_VERSION, null, null)
        Log.d("dev-sqlitedb", "Version cleared")
    }

    fun updateVersion(newVersion: Int) {
        clearVersion()
        database.execSQL("INSERT INTO $TB_VERSION ($COL_V) VALUES ($newVersion)")
        Log.d("dev-sqlitedb", "DB updated: v.$newVersion")
    }

    fun dropDatabase() {
        context.deleteDatabase(DB_NAME)
        Log.d("dev-sqlitedb", "DB dropped")
    }

    fun getSpots() : MutableList<Spot> {
        val spots : MutableList<Spot> = mutableListOf()
        val cursor = database.rawQuery(GET_SPOTS, null)
        if(cursor.moveToFirst()) {
            do {
                spots.add(
                    0, Spot(
                        cursor.getString(cursor.getColumnIndex(COL_NAME)),
                        cursor.getString(cursor.getColumnIndex(COL_SNIPPET)),
                        cursor.getString(cursor.getColumnIndex(COL_LAT)).toDouble(),
                        cursor.getString(cursor.getColumnIndex(COL_LGT)).toDouble(),
                        cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(COL_IMAGEPATH))
                    )
                )
            } while(cursor.moveToNext())
        } else {
            Log.d("dev-sqlitedb", "Error retrieving spots")
        }
        cursor.close()
        return spots
    }

    fun getMarkers(mMap: GoogleMap) : MutableList<Marker> {
        val markers : MutableList<Marker> = mutableListOf()
        val cursor = database.rawQuery(GET_SPOTS, null)
        if(cursor.moveToFirst()) {
            do {
                markers.add(
                    mMap.addMarker(
                        MarkerOptions()
                            .title(cursor.getString(cursor.getColumnIndex(COL_NAME)))
                            .snippet(cursor.getString(cursor.getColumnIndex(COL_SNIPPET)))
                            .position(
                                LatLng(
                                    cursor.getString(cursor.getColumnIndex(COL_LAT)).toDouble(),
                                    cursor.getString(cursor.getColumnIndex(COL_LGT)).toDouble()
                                )
                            )
                    )
                )
            } while(cursor.moveToNext())
        } else {
            Log.d("dev-sqlitedb", "Error retrieving spots")
        }
        cursor.close()
        return markers
    }

    fun getSpecificSpot(spotName: String) : Spot? {
        val cursor = database.rawQuery(
            "SELECT * FROM $TB_SPOTS WHERE name = ${
                DatabaseUtils.sqlEscapeString(
                    spotName
                )
            }", null
        )
        if(cursor.moveToFirst()) {
            return Spot(
                cursor.getString(cursor.getColumnIndex(COL_NAME)),
                cursor.getString(cursor.getColumnIndex(COL_SNIPPET)),
                cursor.getString(cursor.getColumnIndex(COL_LAT)).toDouble(),
                cursor.getString(cursor.getColumnIndex(COL_LGT)).toDouble(),
                cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(COL_IMAGEPATH))
            )
        } else
            Log.d("dev-sqlitedb", "Error retrieving spot '$spotName'")
        return null
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
        // Queries
        const val CREATE_TB_SPOTS =
            "CREATE TABLE $TB_SPOTS (" +
                "$COL_NAME VARCHAR(40) PRIMARY KEY, " +
                "$COL_SNIPPET VARCHAR(60), " +
                "$COL_LAT VARCHAR(10), " +
                "$COL_LGT VARCHAR(10), " +
                "$COL_DESCRIPTION VARCHAR(350), " +
                "$COL_IMAGEPATH VARCHAR(60)"  +
            ")"
        const val CREATE_TB_VERSION =
            "CREATE TABLE $TB_VERSION (" +
                "$COL_V INT PRIMARY KEY" +
            ")"
        const val INIT_TB_VERSION =
            "INSERT INTO $TB_VERSION (" +
            "$COL_V) VALUES (1" +
        ")"
        const val GET_VERSION =
            "SELECT * FROM $TB_VERSION"
        const val GET_SPOTS =
            "SELECT * FROM $TB_SPOTS ORDER BY $COL_LAT, $COL_LGT"
    }

}