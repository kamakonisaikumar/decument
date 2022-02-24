package com.example.actionopendoctree

import android.app.Application

import android.net.Uri
import android.view.KeyEvent
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DirectoryFragmentViewModel(application: Application): AndroidViewModel(application) {
    private  val _documents = MutableLiveData<List<CachingDocumentFile>>()
    val documents = _documents

    private val _openDirectory = MutableLiveData<com.example.actionopendoctree.Event<CachingDocumentFile>>()
    val openDirectory  = _openDirectory

    private val _openDocument = MutableLiveData<com.example.actionopendoctree.Event<CachingDocumentFile>>()
    val openDocument = _openDocument


    fun loadDirectory(directoryUri : Uri){
        val documentTree = DocumentFile.fromTreeUri(getApplication(), directoryUri) ?: return
        val  childDocuments = documentTree.listFiles().toCaChingList()



        viewModelScope.launch {
            val sortedDocuments = withContext(Dispatchers.IO) {
                childDocuments.toMutableList().apply {
                    sortBy { it.name }
                }
            }
            _documents.postValue(sortedDocuments)
        }
    }


    fun documentClicked(clickedDocument: CachingDocumentFile) {
        if (clickedDocument.isDriectory) {
            _openDirectory.postValue(Event(clickedDocument))
        } else {
            openDocument.postValue(Event(clickedDocument))
        }
    }
}