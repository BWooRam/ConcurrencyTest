package com.library.concurrencytest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.concurrencytest.test.TopicMessage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _updateDeviceData = MutableLiveData<TopicMessage>() //화면갱신, updateDeviceId 없으면 null
    val updateDeviceData: LiveData<TopicMessage> = _updateDeviceData

    val sharedFlow = MutableSharedFlow<TopicMessage>(extraBufferCapacity = 20, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     *
     */
    fun postTopicMessageEvent(topicMessage: TopicMessage){
//        _updateDeviceData.postValue(topicMessage)
        viewModelScope.launch {
            sharedFlow.emit(topicMessage)
        }
    }
}