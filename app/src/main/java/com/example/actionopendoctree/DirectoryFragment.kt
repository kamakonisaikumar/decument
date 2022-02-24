package com.example.actionopendoctree

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class DirectoryFragment : Fragment() {

    private lateinit var  directoryUri : Uri
    private lateinit var  recyclerView: RecyclerView
    private lateinit var adapter: DirectoryEntryAdapter
    private lateinit var viewModel: DirectoryFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        directoryUri = arguments?. getString(ARG_DIRECTORY_URI)?.toUri()
            ?: throw IllegalArgumentException("Must pass URI of directory to open")


        val view = inflater.inflate(R.layout.fragment_directory,container,false)
        recyclerView = view.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

       adapter = DirectoryEntryAdapter(object : ClickListeners {
           override fun onDocumentClicked(clickedDocument: CachingDocumentFile) {
               viewModel.documentClicked(clickedDocument)
           }

           override fun onDocumentLongClicked(clickedDocument: CachingDocumentFile) {
               renameDocument(clickedDocument)
           }
       })

        recyclerView.adapter = adapter

        viewModel.documents.observe(viewLifecycleOwner, Observer {documents->
            documents?.let { adapter.setEntries(documents) }
        })

        viewModel.openDirectory.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled().let {
                (activity as? MainActivity)?.showDirectoryContents(it!!.uri)

            }
        })
        viewModel.openDocument.observe(viewLifecycleOwner, Observer {event ->
            event.getContentIfNotHandled()?.let {
                openDocument(it)
            }
        })
        return  view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadDirectory(directoryUri)
    }

    private fun openDocument(document : CachingDocumentFile){
      try {
          val opentIntent = Intent(Intent.ACTION_VIEW).apply {
              flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
              data = document.uri
          }
          startActivity(opentIntent)
      }catch (ex : ActivityNotFoundException){
          Toast.makeText(
              requireContext(),
              resources.getString(R.string.error_no_activity,document.name),
              Toast.LENGTH_SHORT
          ).show()
       }
    }


   @SuppressLint("InflateParams")
    private fun renameDocument(document: CachingDocumentFile) {

       val dialogView = layoutInflater.inflate(R.layout.rename_layout, null)
       val editText = dialogView.findViewById<EditText>(R.id.file_name)
       editText.setText(document.name)

       val buttonCallback: (DialogInterface, Int) -> Unit = { _, buttonId ->
           when (buttonId) {
               DialogInterface.BUTTON_POSITIVE -> {
                   val newName = editText.text.toString()
                   document.rename(newName)

                   viewModel.loadDirectory(directoryUri)
               }
           }
       }


       val renameDialog = AlertDialog.Builder(requireContext())
           .setTitle(R.string.rename_title)
           .setPositiveButton(R.string.rename_okay, buttonCallback)
           .setNegativeButton(R.string.rename_cancel, buttonCallback)
           .create()

       renameDialog.setOnShowListener {
           editText.requestFocus()
           editText.selectAll()
       }
       renameDialog.show()
   }
    companion object{

        fun newInstance(directoryUri : Uri) =
            DirectoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DIRECTORY_URI,directoryUri.toString())
                }
            }


    }

}



private const val ARG_DIRECTORY_URI = "com.example.android.directoryselection.ARG_DIRECTORY_URI"
