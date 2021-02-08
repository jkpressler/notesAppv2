package com.example.notesappv2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.notesappv2.R
import com.example.notesappv2.adapter.NoteListAdapter
import com.example.notesappv2.databinding.FragmentNoteListBinding
import com.example.notesappv2.model.NoteModel
import com.example.notesappv2.utils.NetworkUtils
import com.example.notesappv2.viewmodel.NoteListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteListFragment : Fragment() {


    private val noteListViewModel: NoteListViewModel by activityViewModels()
    private lateinit var binding: FragmentNoteListBinding

    private val noteListAdapter = NoteListAdapter { _, note ->
        navigateToNoteAdd(note)
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch(Dispatchers.Main){}

        binding.toolbar.imgProfile.setOnClickListener {
            val profileDialog = ProfileFragment {
                MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_AlertDialog)
                        .setTitle("Confirm")
                        .setMessage("Are you sure to sign out?")
                        .setPositiveButton("Sign out") { _, _ ->
                            signOutUser()
                        }
                        .setNegativeButton("Cancel", null).show()
            }
            profileDialog.show(childFragmentManager, "")
        }

        binding.rvNote.apply {
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = noteListAdapter
        }

        binding.fabNew.setOnClickListener {
            navigateToNoteAdd(null)
        }

        binding.etSearch.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.etSearch to "note_search")
            findNavController().navigate(R.id.action_noteList_to_searchNote, null, null, extras)
        }


        noteListViewModel.allNotes.observe(viewLifecycleOwner, { list ->
            if (list.isEmpty()) {
                binding.lblNoNote.visibility = View.VISIBLE
                binding.layoutNoteList.visibility = View.GONE
            } else {
                binding.lblNoNote.visibility = View.GONE
                binding.layoutNoteList.visibility = View.VISIBLE
            }
            noteListAdapter.updateNoteList(list)
        })


        NetworkUtils.getNetworkLiveData(requireContext()).observe(viewLifecycleOwner) {
            binding.lblNoInternet.visibility = if (it) View.GONE else View.VISIBLE
        }
    }

    private fun navigateToNoteAdd(note: NoteModel?) {
        val action = NoteListFragmentDirections.actionNoteListToNoteAdd(note)
        findNavController().navigate(action)
    }


    private fun signOutUser() {

    }

}