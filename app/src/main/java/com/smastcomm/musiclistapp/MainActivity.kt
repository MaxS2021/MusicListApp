package com.smastcomm.musiclistapp

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.core.content.ContextCompat
import kotlin.reflect.typeOf


class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermission()

        // get the widgets reference from XML layout
        val button = findViewById<Button>(R.id.button)
        val listView = findViewById<ListView>(R.id.listView)


        // Important : handle the runtime permission
        // Check runtime permission to read external storage


        // Button click listener
        button.setOnClickListener{
            // Disable the button itself
            it.isEnabled = false

            // Get the external storage/sd card music files list
            val list:MutableList<Music> = musicFiles()

            // Get the sd card music titles list
            val titles = mutableListOf<String>()
            for (music in list){titles.add(music.artist + " " +music.title)}

            // Display external storage music files list on list view
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,titles
            )
            listView.adapter = adapter
        }
    }

    private fun getPermission() {
        var permissionList = mutableListOf<String>()
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(android.Manifest.permission.CAMERA)
//        }
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionList.size > 0) {
            requestPermissions(permissionList.toTypedArray(), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                getPermission()
            }
        }
    }

//    fun getAllAudioFiles(): MutableList<String> {
//        val songs: MutableList<String> = ArrayList()
//        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
//        val projection = arrayOf(
//            MediaStore.Audio.Media._ID,
//            MediaStore.Audio.Media.ARTIST,
//            MediaStore.Audio.Media.TITLE,
//            MediaStore.Audio.Media.DATA,
//            MediaStore.Audio.Media.DISPLAY_NAME,
//            MediaStore.Audio.Media.DURATION
//        )
//        applicationContext.contentResolver.query(
//            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//            projection,
//            selection,
//            null,
//            null
//        )?.use{ cursor ->
//            while (cursor.moveToNext()) {
//                songs.add(
//                    cursor.getString(0)
//                        .toString() + "||" + cursor.getString(1) + "||" + cursor.getString(2) + "||" + cursor.getString(
//                        3
//                    ) + "||" + cursor.getString(4) + "||" + cursor.getString(5)
//                )
//            }
//        }
//        return songs
//    }
}



// Get all music files list from external storage/sd card
fun Context.musicFiles():MutableList<Music>{
    // Initialize an empty mutable list of music
    val list:MutableList<Music> = mutableListOf()

    // Get the external storage media store audio uri
    val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    //val uri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI

    // IS_MUSIC : Non-zero if the audio file is music
    val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"

    // Sort the musics
    val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
    //val sortOrder = MediaStore.Audio.Media.TITLE + " DESC"

    // Query the external storage for music files
    val cursor: Cursor? = this.contentResolver.query(
        uri, // Uri
        null, // Projection
        selection, // Selection
        null, // Selection arguments
        sortOrder // Sort order
    )

    // If query result is not empty
    cursor?.apply {
        if (cursor.moveToFirst()){
            val id:Int = cursor.getColumnIndex(
                MediaStore.Audio.Media._ID)
            val title:Int = cursor.getColumnIndex(
                MediaStore.Audio.Media.TITLE)
            val artist:Int = cursor.getColumnIndex(
                MediaStore.Audio.Media.ARTIST)
            val data:Int = cursor.getColumnIndex(
                MediaStore.Audio.Media.DATA)
            val dispName:Int = cursor.getColumnIndex(
                MediaStore.Audio.Media.DISPLAY_NAME)
//            val duration:Int = cursor.getColumnIndex(
//                MediaStore.Audio.Media.DURATION)


            // Now loop through the music files
            do {
                val audioId:Long = cursor.getLong(id)
                val audioTitle:String = cursor.getString(title)
                val audioArtist:String = cursor.getString(artist)
                val audioData:String = cursor.getString(data)
                val audioDiplayName:String = cursor.getString(dispName)
                //val audioDuration:String = cursor.getString(duration)
//println(duration)
                // Add the current music to the list
                list.add(Music(audioId,audioTitle,audioArtist, audioData, audioDiplayName))
            }while (cursor.moveToNext())
        }
    }

    // Finally, return the music files list
    println(list)
    return  list
}


// Initialize a new data class to hold music data
data class Music(
    val id:Long,
    val title:String,
    val artist: String,
    val data: String,
    val disName: String,

)