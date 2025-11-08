package com.example.smartplanner.focus

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.smartplanner.databinding.ActivityFocusBinding

class FocusActivity : AppCompatActivity() {
    private lateinit var b: ActivityFocusBinding
    private var timer: CountDownTimer? = null
    private var running = false
    private var totalMs: Long = 25 * 60 * 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityFocusBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.title = "Focus Mode"

        b.btnStart.setOnClickListener { start(25) }
        b.btnShortBreak.setOnClickListener { start(5) }
        b.btnLongBreak.setOnClickListener { start(15) }
        b.btnStop.setOnClickListener { stop() }
    }

    private fun start(mins: Int) {
        stop()
        totalMs = mins * 60 * 1000L
        running = true
        timer = object : CountDownTimer(totalMs, 1000) {
            override fun onTick(ms: Long) {
                val s = ms / 1000
                val m = s / 60
                val ss = (s % 60).toString().padStart(2, '0')
                b.tvTimer.text = "$m:$ss"
            }
            override fun onFinish() { running = false; b.tvTimer.text = "0:00" }
        }.start()
    }
    private fun stop() { running = false; timer?.cancel() }
}
