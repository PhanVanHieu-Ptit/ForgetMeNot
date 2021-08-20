package com.example.forgetmenot

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_task_description.*
import java.nio.file.Files.delete
import java.text.FieldPosition
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PREFS_TASKS = "prefs_tasks"
    private val KEY_TAKS_LIST = "tasks_list"
    private val ADD_TASK_REQUEST = 1

    private val taskList:MutableList<String> = mutableListOf()
    private val adapter by lazy { makeAdapter(taskList) }
    private val tickReceiver by lazy { makeBroadcastReceiever() }

    companion object{
        private  const val LOG_TAG = "MainActivityLog"

        private  fun getCurrentTimeStamp():String{
            val simpleDateFormat= SimpleDateFormat("yyyyy--MM--dd HH:mm",Locale.US)
            val now = Date()
            return simpleDateFormat.format(now)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        taskListView.adapter = adapter


        /*taskListView.onItemClickListener = AdapterView.OnItemClickListener{
            _,_,position,_ -> taskSeLected(position)
        }*/
       /* addTaskButton.setOnClickListener {
            val intent= Intent(this,TaskDescriptionActivity::class.java)
            startActivity(intent)
        }*/
        val saveList = getSharedPreferences(PREFS_TASKS, Context.MODE_PRIVATE).getString(KEY_TAKS_LIST,null)
        if(saveList != null){
            val items = saveList.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            taskList.addAll(items)
        }
    }

    override fun onResume() {
        super.onResume()
        dateTimeTextView.text = getCurrentTimeStamp()
        registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    override fun onPause() {
        super.onPause()

        try{
            unregisterReceiver(tickReceiver)
        }catch (e:IllegalMonitorStateException){
            Log.e(MainActivity.LOG_TAG,"Time tick Receiver not registered",e)
        }
    }

    override fun onStop() {
        super.onStop()

        // Save all data which you qnt to persist
        val savedList = StringBuilder()
        for(task in taskList){
            savedList.append(task)
            savedList.append(",")
        }
        getSharedPreferences(PREFS_TASKS,Context.MODE_PRIVATE).edit()
            .putString(KEY_TAKS_LIST,savedList.toString()).apply()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==ADD_TASK_REQUEST){
            if(resultCode==Activity.RESULT_OK){
                val task = data?.getStringExtra(TaskDescriptionActivity.EXTRA_TASK_DESCRIPTION)
                task?.let{
                    taskList.add(task)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun addTaskClicked(view:View){
        addTaskButton.setOnClickListener{
            val intent = Intent(this, TaskDescriptionActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST)
        }
    }

    private fun makeAdapter(list: List<String>):ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_list_item_1,list)

    private fun makeBroadcastReceiever():BroadcastReceiver{
        return object :BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent?.action == Intent.ACTION_TIME_TICK){
                    dateTimeTextView.text = getCurrentTimeStamp()
                }
            }
        }
    }

   /* private fun taskSeLected(position: Int){
        AlertDialog.Builder(this)
            .setTitle("Enter")
            .setMessage(taskList[position])
            .setPositiveButton(R.string.delete,{ _,_ ->
                taskList.removeAt(position)
                adapter.notifyDataSetChanged()
            })
            .setNegativeButton(R.string.cancel, {
                dialog,_ ->dialog.cancel()
            })
            .create()
            .show()
    }*/
}