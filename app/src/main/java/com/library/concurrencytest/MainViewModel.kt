package com.library.concurrencytest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.library.concurrencytest.test.TopicMessage

class MainViewModel : ViewModel() {
    private val _updateDeviceData = MutableLiveData<TopicMessage>() //화면갱신, updateDeviceId 없으면 null
    val updateDeviceData: LiveData<TopicMessage> = _updateDeviceData.apply {

    }

    /**
     *
     */
    fun postTopicMessageEvent(topicMessage: TopicMessage){
        _updateDeviceData.postValue(topicMessage)
    }
}