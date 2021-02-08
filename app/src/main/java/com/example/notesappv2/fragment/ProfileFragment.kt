package com.example.notesappv2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.notesappv2.databinding.FragmentProfileDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment(private val signOut : (() -> Unit)? = null) : DialogFragment() {


    private lateinit var binding : FragmentProfileDialogBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lytSignOut.setOnClickListener {
            dismiss()
            signOut?.invoke()
        }
    }
}