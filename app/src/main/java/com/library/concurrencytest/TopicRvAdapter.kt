package com.library.concurrencytest

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.library.concurrencytest.test.DeviceStatus
import com.library.concurrencytest.test.TopicMessage
import org.w3c.dom.Text

class TopicRvAdapter: RecyclerView.Adapter<TopicRvAdapter.TopicViewHolder>() {
    private val topicMessageList = arrayListOf<TopicMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicRvAdapter.TopicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false)
        return TopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicRvAdapter.TopicViewHolder, position: Int) {
        val item = topicMessageList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return topicMessageList.size
    }

    fun setItem(topicMessageList:List<TopicMessage>){
        this@TopicRvAdapter.topicMessageList.clear()
        this@TopicRvAdapter.topicMessageList.addAll(topicMessageList)
        notifyDataSetChanged()
    }

    fun update(topicMessage: TopicMessage){
        val index = topicMessageList.indexOf(topicMessage)
        val target = topicMessageList.find {
            topicMessage.topic == it.topic
        }
        Log.d("TopicRvAdapter", "index = $index, target = $target")

        target?.let {
            it.status = topicMessage.status
            it.message = topicMessage.message
        }

        notifyItemChanged(index)
    }

    fun clear(){
        topicMessageList.clear()
        notifyDataSetChanged()
    }

    inner class TopicViewHolder(
        itemView:View
    ): RecyclerView.ViewHolder(itemView) {
        private val cl:ConstraintLayout = itemView.findViewById(R.id.cl)
        private val ivIcon:ImageView = itemView.findViewById(R.id.iv_icon)
        private val tvTopic:TextView = itemView.findViewById(R.id.tv_topic)
        private val tvMessage:TextView = itemView.findViewById(R.id.tv_message)
        private val btn1:Button = itemView.findViewById(R.id.btn1)
        private val btn2:Button = itemView.findViewById(R.id.btn2)

        fun bind(topicMessage: TopicMessage){
            tvTopic.text = topicMessage.topic
            tvMessage.text = topicMessage.message

            when(topicMessage.status){
                DeviceStatus.Connect -> {
                    cl.setBackgroundResource(R.color.white)
                    ivIcon.alpha = 1.0f
                    btn1.isEnabled = true
                    btn2.isEnabled = true
                    btn1.setBackgroundResource(R.color.purple_500)
                    btn2.setBackgroundResource(R.color.purple_500)
                }

                DeviceStatus.Lost -> {
                    cl.setBackgroundResource(R.color.red1)
                    ivIcon.alpha = 0.2f
                    btn1.isEnabled = false
                    btn2.isEnabled = false
                    btn1.setBackgroundResource(R.color.gray)
                    btn2.setBackgroundResource(R.color.gray)
                }

                DeviceStatus.Disconnect -> {
                    cl.setBackgroundResource(R.color.red2)
                    ivIcon.alpha = 0.2f
                    btn1.isEnabled = false
                    btn2.isEnabled = false
                    btn1.setBackgroundResource(R.color.gray)
                    btn2.setBackgroundResource(R.color.gray)
                }
            }
        }
    }
}