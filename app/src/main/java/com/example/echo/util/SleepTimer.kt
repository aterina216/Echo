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

class SleepTimer {

    private var job: Job? = null
    private val _remainingsMs = MutableStateFlow(0L)
    val remainings: StateFlow<Long> = _remainingsMs

    fun start(minutes: Int, onFinish: () -> Unit) {
        cancel()
        val durationMs = minutes * 60 * 1000L
        val endTime = System.currentTimeMillis() + durationMs
        _remainingsMs.value = durationMs

        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(500)
                val left = endTime - System.currentTimeMillis()
                if(left <= 0){
                    _remainingsMs.value
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