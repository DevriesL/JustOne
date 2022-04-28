package com.example.justone.model

import androidx.lifecycle.viewModelScope
import com.example.foundation.BaseViewModel
import com.example.foundation.network.ResourceState
import com.example.foundation.network.onError
import com.example.foundation.network.onSuccess
import com.example.justone.data.JustOneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class JustOneViewModel @Inject constructor(
    private val repository: JustOneRepository
) : BaseViewModel<JustOneState, JustOneAction>() {
    private val wordsNumber: Int = 5
    private val timer: Int = 60

    override fun configureInitState(): JustOneState {
        return JustOneState(wordsNumber = wordsNumber, timer = timer)
    }

    override fun dispatch(action: JustOneAction) {
        when (action) {
            JustOneAction.GenerateWords -> getRandomWords()
            is JustOneAction.TranslateWord -> translateWord(action.word)
        }
    }

    private fun getRandomWords() {
        updateState { copy(words = ResourceState.Loading) }
        viewModelScope.launch {
            repository.getRandomWordList(wordsNumber)
                .onSuccess { wordList ->
                    updateState { copy(words = ResourceState.Success(wordList)) }
                }
                .onError { errorType ->
                    updateState { copy(words = ResourceState.Error(errorType)) }
                }
        }
    }

    private fun translateWord(word: String) {
        updateState { copy(translation = ResourceState.Loading) }
        viewModelScope.launch {
            repository.translateWord(word)
                .onSuccess { result ->
                    updateState { copy(translation = ResourceState.Success(result)) }
                }
                .onError { errorType ->
                    updateState { copy(translation = ResourceState.Error(errorType)) }
                }
        }
    }

}
