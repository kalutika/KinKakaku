package com.app.kinkakaku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.kinkakaku.shared.model.DataItem
import com.app.kinkakaku.shared.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DataUiState(
    val isLoading: Boolean = false,
    val data: List<DataItem> = emptyList(),
    val error: String? = null
)

class DataViewModel(
    private val repository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DataUiState())
    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getDataItems()
                .onSuccess { items ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        data = items
                    )
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    fun retry() {
        loadData()
    }
}
