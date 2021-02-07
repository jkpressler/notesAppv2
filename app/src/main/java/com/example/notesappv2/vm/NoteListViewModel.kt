package com.example.notesappv2.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.notesappv2.model.NoteModel
import com.example.notesappv2.utils.Constants.NOTES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteListViewModel @ViewModelInject constructor(
        private val db : FirebaseFirestore
) : ViewModel() {

    private val _allNotes = MutableLiveData<List<NoteModel>>()
    val allNotes : LiveData<List<NoteModel>>
        get() = _allNotes

    init {
        loadNoteAllNote()
    }

    private fun loadNoteAllNote() {

        viewModelScope.launch(Dispatchers.IO) {

            db.collection(NOTES)
                    .orderBy("creationDate",Query.Direction.DESCENDING)
                    .addSnapshotListener { value, _ ->

                        value?.let {

                            val tempNoteList = mutableListOf<NoteModel>()
                            for(doc in it.iterator()) {
                                val note = doc.toObject(NoteModel::class.java)
                                tempNoteList.add(note)
                            }
                            _allNotes.postValue(tempNoteList)
                        }

                    }
        }
    }

    fun filterNote(query : String) : List<NoteModel> {

        return _allNotes.value?.let { noteList ->
            noteList.filter { note ->
                note.title.contains(query,true)
                        || note.note.contains(query,true) }
        } ?: listOf()

    }

    fun updateNote(noteId : Int,noteData : Map<String,Any>,
                   doOnSuccess: () -> Unit,
                   doOnFailure: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            db.collection(NOTES)
                    .document("$noteId")
                    .update(noteData)
                    .addOnSuccessListener {
                        doOnSuccess.invoke()
                    }
                    .addOnFailureListener {
                        doOnFailure.invoke()
                    }
        }
    }

    fun saveNote(note : NoteModel,doOnSuccess: () -> Unit,doOnFailure: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            db.collection(NOTES)
                    .document("${note.id}")
                    .set(note)
                    .addOnSuccessListener {
                        doOnSuccess.invoke()
                    }
                    .addOnFailureListener {
                        doOnFailure.invoke()
                    }
        }
    }

    fun deleteNote(note : NoteModel,doOnSuccess: () -> Unit,doOnFailure: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            db.collection(NOTES)
                    .document("${note.id}")
                    .delete()
                    .addOnSuccessListener {
                        doOnSuccess.invoke()
                    }
                    .addOnFailureListener {
                        doOnFailure.invoke()
                    }

        }
    }

}