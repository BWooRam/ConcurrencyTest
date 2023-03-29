package com.library.concurrencytest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.library.concurrencytest.databinding.ActivityMainBinding
import com.library.concurrencytest.test.DeviceStatus
import com.library.concurrencytest.test.TopicMessage
import com.library.concurrencytest.test.Worker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val worker: Worker = Worker()
    private val messageChannel = Channel<TopicMessage>(10, BufferOverflow.DROP_OLDEST)
    private val count = 20
    private var topicRvAdapter:TopicRvAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
    }

    private fun initView(){
        topicRvAdapter = TopicRvAdapter()
        binding.rv.adapter = topicRvAdapter

        topicRvAdapter?.setItem(getTestTopicList())
    }

    private fun initEvent(){
        GlobalScope.launch {
            messageChannel.consumeEach {
                Log.d("MainActivity", "messageChannel string = $it")

                Handler(Looper.getMainLooper()).post {
                    topicRvAdapter?.update(it)
                }
            }
        }

        binding.btn1.setOnClickListener {
            if (worker.isStart())
                return@setOnClickListener

            worker
                .many(count)
                .interval(10)
                .job(getTestRunnableList())
                .work()
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
                Log.d("getTestRunnableList", "statusRandomIndex = $statusRandomIndex")

                val deviceStatus = when(statusRandomIndex){
                    0 -> DeviceStatus.Connect
                    1 -> DeviceStatus.Lost
                    2 -> DeviceStatus.Disconnect
                    else -> DeviceStatus.Connect
                }

                GlobalScope.launch {
                    messageChannel.send(TopicMessage(tag, msg.toString(), deviceStatus))
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