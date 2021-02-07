package com.example.notesappv2.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
class NotesListModule {


    @Provides
    fun provideFirebaseFireStore() : FirebaseFirestore {
        return Firebase.firestore
    }
}