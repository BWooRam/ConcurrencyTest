package com.library.concurrencytest.test

import android.util.Log
import android.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List

class Worker() {
    private val mWorkerList: ArrayList<Thread> = arrayListOf()
    private var mWorkerCount: Int = 0
    private var mWorkerInterval: Int = 0
    private var mWorkerRunnable: Runnable? = null
    private var mWorkerRunnableList: ArrayList<Runnable> = arrayListOf()
    private var isStart = false

    /**
     *
     */
    private class WorkThread(
        runnable: Runnable
    ):Thread(runnable)

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
    fun interval(interval: Int): Worker {
        this@Worker.mWorkerInterval = interval
        return this@Worker
    }

    /**
     *
     */
    fun work() {
        this@Worker.isStart = true
        this@Worker.mWorkerList.clear()
        //Worker 생성
        for (index in 0 until mWorkerCount) {
            val workThread = WorkThread {
                while (true) {
                    //worker 시작
                    val secondTime = getSecondTime().toInt()
                    val currentTime = getTime()
                    if (secondTime % mWorkerInterval == 0) {
                        Log.d("Worker",
                            "work() secondTime = $secondTime, currentTime = $currentTime")
                        val runnable = if(this@Worker.mWorkerRunnable != null) this@Worker.mWorkerRunnable else mWorkerRunnableList[index]
                        runnable?.run()
                        Thread.sleep(1000)
                    }

                    //Stop 발생 시 종료
                    if (!this@Worker.isStart) {
                        Log.d("Worker", "stop()")
                        break
                    }
                }
            }

            this@Worker.mWorkerList.add(workThread)
        }

        //Worker 실행
        this@Worker.mWorkerList.forEach {
            it.start()
        }
    }

    fun stop() {
        Log.d("Worker", "stop()")
        this@Worker.isStart = false
    }

    fun isStart() = this@Worker.isStart

    private fun getTime(): String {
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("HH:mm:ss")
        val resultdate = Date(currentTime)
        return sdf.format(resultdate)
    }

    private fun getSecondTime(): String {
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("ss")
        val resultdate = Date(currentTime)
        return sdf.format(resultdate)
    }
}