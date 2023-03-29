package com.library.concurrencytest.test

import android.util.Log
import com.library.concurrencytest.TimeUtils
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List

class Worker(
    private val dispatcher: CoroutineDispatcher
) {
    private var mWorkerCount: Int = 0
    private var mWorkerInterval: Long = 0
    private var mWorkerRunnable: Runnable? = null
    private var mWorkerRunnableList: ArrayList<Runnable> = arrayListOf()
    private var isStart = false
    private var timer:Timer? = null

    /**
     *
     */
    fun many(count: Int): Worker {
        this@Worker.mWorkerCount = count
        return this@Worker
    }

    /**
     *
     */
    fun job(runnable: Runnable): Worker {
        this@Worker.mWorkerRunnable = runnable
        return this@Worker
    }

    /**
     *
     */
    fun job(runnableList: List<Runnable>): Worker{
        this@Worker.mWorkerRunnableList.clear()
        this@Worker.mWorkerRunnableList.addAll(runnableList)
        return this@Worker
    }

    /**
     * 설정할 초를 입력해주세요
     */
    fun interval(interval: Long): Worker {
        this@Worker.mWorkerInterval = interval
        return this@Worker
    }

    /**
     *
     */
    fun work() {
        this@Worker.isStart = true

        //Timer 생성
        if(timer == null)
            timer = Timer()

        //Timer 실행
        timer?.schedule(object :TimerTask(){
            override fun run() {
                //Worker 생성
                for (index in 0 until mWorkerCount) {
                    //Worker 실행
                    CoroutineScope(dispatcher).async {
                        //worker 시작
                        val secondTime = TimeUtils.getSecondTime().toInt()
                        val currentTime = TimeUtils.getTime()
                        if (secondTime % 10 == 0) {
                            Log.d("Worker",
                                "work() secondTime = $secondTime, currentTime = $currentTime")
                            val runnable = if(this@Worker.mWorkerRunnable != null) this@Worker.mWorkerRunnable else mWorkerRunnableList[index]
                            runnable?.run()
                        }
                    }

                }
            }
        }, 0, mWorkerInterval)
    }

    fun stop() {
        Log.d("Worker", "stop()")
        this@Worker.isStart = false
        timer?.cancel()
        timer = null
    }

    fun isStart() = this@Worker.isStart

}