package com.example.actionopendoctree

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DirectoryEntryAdapter(
    private val clickListeners: ClickListeners
): RecyclerView.Adapter<DirectoryEntryAdapter.ViewHolder>() {

    private  val directionEntries = mutableListOf<CachingDocumentFile>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.directory_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            val  item = directionEntries[position]
            val itemDrawalbeRes = if (item.isDriectory){
                R.drawable.ic_folder_black_24dp
            }else{
                R.drawable.ic_folder_black_24dp
            }
              fileName.text = item.name
             fileName.text = item.type  ?: " "
             imageView.setImageResource(itemDrawalbeRes)

            root.setOnClickListener{
                clickListeners.onDocumentClicked(item)
            }
            root.setOnLongClickListener{
                clickListeners.onDocumentLongClicked(item)
                true
            }
        }
    }

    override fun getItemCount(): Int {
      return directionEntries.size
    }

    fun setEntries(newList: List<CachingDocumentFile>){
        synchronized(directionEntries){
            directionEntries.clear()
            directionEntries.addAll(newList)
            notifyDataSetChanged()
        }
    }


    class  ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val root = view
        val fileName : TextView = view.findViewById(R.id.file_name)
        val mimeType: TextView = view.findViewById(R.id.mime_type)
        val imageView : ImageView  = view .findViewById(R.id.entry_image)

    }
}



interface  ClickListeners{

fun onDocumentClicked(clickedDocument : CachingDocumentFile)
fun onDocumentLongClicked(clickedDocument: CachingDocumentFile)
}
