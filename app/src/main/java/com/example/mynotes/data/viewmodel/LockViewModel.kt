package com.example.mynotes.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.Graph
import com.example.mynotes.data.repository.LockRepository
import com.example.mynotes.data.repository.NoteRepository
import com.example.mynotes.model.LockModel
import com.example.mynotes.model.NoteModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LockViewModel(
    private val lockRepository: LockRepository = Graph.lockRepository
) : ViewModel() {

    private val _lock = MutableStateFlow<LockModel?>(null)

    //it is the lock
    val lock: StateFlow<LockModel?> = _lock


    //to check passcode is setup or not
    val isPasscodeSet: StateFlow<Boolean> = _lock.map { lockModel ->
        val passcodeSet = lockModel?.passcode?.isNotBlank() == true
        val phonePasscodeSet = lockModel?.isPhonePasscode == true
        passcodeSet || phonePasscodeSet
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    init {
        viewModelScope.launch {
            _lock.value = lockRepository.getLock()
        }
    }

    fun insertOrUpdate(lock: LockModel) {
        viewModelScope.launch {
            lockRepository.insertOrUpdate(lock)
            _lock.value = lock // update the state
        }
    }

    fun deleteLock() {
        viewModelScope.launch {
            lockRepository.deleteLock()
            _lock.value = null // update the state
        }
    }


}