package com.example.smartplanner.insights

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartplanner.databinding.ActivityInsightsBinding
import com.example.smartplanner.ui.home.Task
import com.example.smartplanner.util.TaskBus

class InsightsActivity : AppCompatActivity() {
    private lateinit var b: ActivityInsightsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityInsightsBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.title = "Insights"

        val tasks: List<Task> = TaskBus.tasks
        val total = tasks.size
        val done = tasks.count { it.done }
        val pct = if (total == 0) 0 else (100 * done / total)

        val avgEst = tasks.map { it.estimateMin }.ifEmpty { listOf(0) }.average()
        val highs = tasks.count { it.priority.name == "HIGH" }

        b.tvSummary.text = "Total: $total\nCompleted: $done ($pct%)\nAvg estimate: ${avgEst.toInt()} min\nHigh priority: $highs"
    }
}
