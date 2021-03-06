package com.example.forgetmenot

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_task_description.*


class TaskDescriptionActivity : AppCompatActivity() {

    companion object{
        val EXTRA_TASK_DESCRIPTION= "task"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_description)
        buttonDone.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    fun doneClicked(view:View){
        val taskDescription = descriptionText.text.toString()
        if(!taskDescription.isEmpty()){
            val result = Intent()

            result.putExtra(EXTRA_TASK_DESCRIPTION,taskDescription)
            setResult(Activity.RESULT_OK,result)
        }
        else{
            setResult(Activity.RESULT_CANCELED)
        }
        finish()
    }
}