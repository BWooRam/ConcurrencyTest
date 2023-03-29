package com.library.concurrencytest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.library.concurrencytest.databinding.ActivityMainBinding
import com.library.concurrencytest.test.DeviceStatus
import com.library.concurrencytest.test.TopicMessage
import com.library.concurrencytest.test.Worker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val worker: Worker = Worker(Dispatchers.IO)
    private val messageChannel = Channel<TopicMessage>(20, BufferOverflow.DROP_OLDEST)
    private val count = 20
    private var topicRvAdapter:TopicRvAdapter? = null
    private lateinit var viewModel:MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        //ViewModel
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
        
        //Worker 초기화
        worker
            .many(count)
            .interval(1000)
            .job(getTestRunnableList())

        initEvent()
        initView()
    }

    private fun initView(){
        topicRvAdapter = TopicRvAdapter()
        binding.rv.adapter = topicRvAdapter

        topicRvAdapter?.setItem(getTestTopicList())
    }

    private fun initEvent(){
        GlobalScope.launch {
            messageChannel.consumeEach {
                val time = TimeUtils.getTime()
                Log.d("MainActivity", "messageChannel time = $time, topicMessage = $it")

                viewModel.postTopicMessageEvent(it)
            }
        }

        /*viewModel.updateDeviceData.observe(this@MainActivity){
            Handler(Looper.getMainLooper()).post {
                Log.d("MainActivity", "updateDeviceData topicMessage = $it")
                topicRvAdapter?.update(it)
            }
        }*/

        GlobalScope.launch {
            viewModel.sharedFlow.collect{
                Handler(Looper.getMainLooper()).post {
                    Log.d("MainActivity", "sharedFlow topicMessage = $it")
                    topicRvAdapter?.update(it)
                }
            }
        }

        binding.btn1.setOnClickListener {
            if (worker.isStart())
                return@setOnClickListener

            worker.work()
        }

        binding.btn2.setOnClickListener {
            worker.stop()
        }
    }

    private fun getTestRunnableList(): List<Runnable> {
        val runnableList = arrayListOf<Runnable>()
        for (count in 0 until count) {
            runnableList.add {
                val tag = count.toString()
                val msg = Random.nextInt(0, 10000)
                val statusRandomIndex = Random.nextInt(0, 3)

                val deviceStatus = when(statusRandomIndex){
                    0 -> DeviceStatus.Connect
                    1 -> DeviceStatus.Lost
                    2 -> DeviceStatus.Disconnect
                    else -> DeviceStatus.Connect
                }

                GlobalScope.launch {
                    val topicMessage = TopicMessage(tag, msg.toString(), deviceStatus)
                    Log.d("getTestRunnableList", "runnable statusRandomIndex = $statusRandomIndex, topicMessage = $topicMessage")
                    messageChannel.send(topicMessage)
                }
            }
        }
        return runnableList
    }

    private fun getTestTopicList(): List<TopicMessage> {
        val topicMessageList = arrayListOf<TopicMessage>()
        for (count in 0 until count) {
            val tag = count.toString()
            val msg = Random.nextInt(0, 10000)
            val statusRandomIndex = Random.nextInt(0, 3)
            Log.d("getTestTopicList", "statusRandomIndex = $statusRandomIndex")

            val deviceStatus = when(statusRandomIndex){
                0 -> DeviceStatus.Connect
                1 -> DeviceStatus.Lost
                2 -> DeviceStatus.Disconnect
                else -> DeviceStatus.Connect
            }

            topicMessageList.add(TopicMessage(tag, msg.toString(), deviceStatus))
        }
        return topicMessageList
    }
}