package com.example.actionopendoctree

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val OPEN_DIRECTORY_REQUEST_CODE = 0xf11e
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // setSupportActionBar(toolbar)
        val openDirectoryButton = findViewById<FloatingActionButton>(R.id.fab_open_directory)
        openDirectoryButton.setOnClickListener {
            openDirectory()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val directorOpen = supportFragmentManager.backStackEntryCount > 0
            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(directorOpen)
                it.setDisplayShowHomeEnabled(directorOpen)
            }

            if (directorOpen){
                openDirectoryButton.visibility = View.GONE
            }else{
                openDirectoryButton.visibility = View.VISIBLE
            }

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val directionUri = data?.data ?: return

            contentResolver.takePersistableUriPermission(
                directionUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
           showDirectoryContents(directionUri)

        }
    }
     fun showDirectoryContents(directoyUri : Uri){

         supportFragmentManager.commit {
             val directoryTag = directoyUri.toString()
             val directoryFragment = DirectoryFragment.newInstance(directoyUri)
             replace(R.id.fragment_container,directoryFragment,directoryTag)
             addToBackStack(directoryTag)
         }
     }

    private fun openDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivity(intent)
    }
}