package com.example.notesappv2.frag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.notesappv2.R
import com.example.notesappv2.databinding.FragmentNoteAddBinding
import com.example.notesappv2.model.NoteModel
import com.example.notesappv2.utils.DateFormatter
import com.example.notesappv2.vm.NoteListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint                                      //another entry point
class NoteAddFragment : Fragment() {

    private val args : NoteAddFragmentArgs by navArgs()

    private val viewModel : NoteListViewModel by activityViewModels()
    private lateinit var binding : FragmentNoteAddBinding


    private var note : NoteModel? = null
    private var isAddAction : Boolean = true

    private fun isValidData() : Boolean {

        val tempTitle = binding.etTitle.text.toString().trim()
        val tempNote = binding.etNote.text.toString().trim()
        return (tempNote.isNotBlank() && tempNote.isNotEmpty() && if(isAddAction) true else { tempTitle != note!!.title || tempNote != note!!.note })
    }

    private val noteTextWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            updateActionSave(isValidData())                 //allow save button to turn on depending on data validity
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            updateActionSave(isValidData())
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // not used
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        note = args.note
        isAddAction = args.note == null
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteAddBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateActionSave(false)                         //save button off

        binding.lblDate.text = DateFormatter.formatDate(if (isAddAction) Date() else note!!.creationDate)  //format date time

        binding.layoutToolbar.lblLabel.text = if (isAddAction) "Add Note" else "Edit Note"              //add or edit

        binding.etTitle.setOnFocusChangeListener { _, hasFocus ->               //when focus of view changes...
            if (hasFocus) {
                binding.etTitle.addTextChangedListener(noteTextWatcher)
            } else {
                binding.etTitle.removeTextChangedListener(noteTextWatcher)
            }
        }
        binding.etNote.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etNote.addTextChangedListener(noteTextWatcher)
            } else {
                binding.etNote.removeTextChangedListener(noteTextWatcher)
            }
        }

        binding.layoutToolbar.imgBack.setOnClickListener {
            findNavController().popBackStack()                                  //go back to notes list
        }

        binding.layoutToolbar.imgSave.setOnClickListener {
            saveNote()                                                          //save note
            findNavController().popBackStack()                                  //go back to notes list
        }

        binding.layoutToolbar.imgDelete.visibility = if (isAddAction) View.GONE else View.VISIBLE       //delete btn visible when working with a past note
        binding.layoutToolbar.imgDelete.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext(),R.style.ThemeOverlay_App_AlertDialog)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteNote(note = note!!,
                                doOnSuccess = {},
                                doOnFailure = {})
                        findNavController().popBackStack()
                    }
                    .setNegativeButton("Cancel", null).show()
        }

        binding.etTitle.setText(note?.title ?: "")
        binding.etNote.setText(note?.note ?: "")

    }

    private fun updateActionSave(isEnable : Boolean) {

        binding.layoutToolbar.imgSave.apply {
            isEnabled = isEnable
            imageAlpha = if (isEnable) 255 else 100                     //turn on save button once text is entered
        }
    }

    private fun saveNote() {

        val tempTitle = binding.etTitle.text.toString()
        val tempNote = binding.etNote.text.toString()
        val currentMillis = System.currentTimeMillis()

        if (isAddAction) {

            viewModel.saveNote(note = NoteModel(
                    title = tempTitle,
                    note = tempNote,
                    creationDate = Date(),
                    modifiedDate = Date(currentMillis)),
                    doOnSuccess = {},
                    doOnFailure = {
                        Toast.makeText(context,"Save note failed",Toast.LENGTH_SHORT).show()
                    })

        } else {

            viewModel.updateNote(
                    noteId = note!!.id,
                    noteData = mapOf(
                            "title" to tempTitle,
                            "note" to tempNote,
                            "modifiedDate" to Date(currentMillis)
                    ),
                    doOnSuccess = {},
                    doOnFailure = {
                        Toast.makeText(context,"Update note failed",Toast.LENGTH_SHORT).show()
                    })

        }
    }
}