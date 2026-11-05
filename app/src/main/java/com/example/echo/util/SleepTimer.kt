package com.example.echo.util

import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SleepTimer(private val dispatcher: CoroutineDispatcher = Dispatchers.Main) {

    private var job: Job? = null
    private val _remainingsMs = MutableStateFlow(0L)
    val remainings: StateFlow<Long> = _remainingsMs

    fun start(minutes: Int, onFinish: () -> Unit) {
        require(minutes > 0) {"minutes must be positive"}
        cancel()
        val durationMs = minutes * 60 * 1000L
        _remainingsMs.value = durationMs

        job = CoroutineScope(dispatcher).launch {
            val tickMs = 500L
            var left = durationMs
            while (isActive && left > 0) {
                delay(tickMs)
                left -= tickMs
                if(left <= 0){
                    _remainingsMs.value = 0
                    onFinish()
                    break
                }
                else {
                    _remainingsMs.value = left
                }
            }
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
        _remainingsMs.value = 0
    }
}