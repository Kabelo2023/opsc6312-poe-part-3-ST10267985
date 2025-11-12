package com.example.smartplanner.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.smartplanner.R
import com.example.smartplanner.databinding.ActivityHomeBinding
import com.example.smartplanner.export.Exporters
import com.example.smartplanner.feature.Notifier
import com.example.smartplanner.feature.ReminderWorker
import com.example.smartplanner.feature.occursOn
import com.example.smartplanner.focus.FocusActivity
import com.example.smartplanner.insights.InsightsActivity
import com.example.smartplanner.ui.login.LoginActivity
import com.example.smartplanner.ui.settings.SettingsActivity
import com.example.smartplanner.util.TaskBus
import com.example.smartplanner.viewmodel.SchedulerViewModel
import com.example.smartplanner.viewmodel.TaskViewModel
import com.example.smartplanner.weather.WeatherViewModel
import com.example.smartplanner.weather.ui.WeatherCardBinder
import com.google.android.material.snackbar.Snackbar
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit
import android.content.Context
import com.example.smartplanner.i18n.LocaleManager


class HomeActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.wrap(newBase))
    }

    private lateinit var binding: ActivityHomeBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var weekAdapter: WeekAdapter

    private val schedulerVm: SchedulerViewModel by viewModels()
    private val taskVm: TaskViewModel by viewModels()

    private var weatherBinder: WeatherCardBinder? = null
    private var weatherVm: WeatherViewModel? = null

    private val allTasks = mutableListOf<Task>()
    private var selectedDate: LocalDate = safeToday()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runCatching {
            binding = ActivityHomeBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setSupportActionBar(binding.topAppBar)
            supportActionBar?.title = "SyncUp"

            applyWindowInsets() // keep bars tappable

            runCatching { setupWeekBar() }.onFailure { toast("Week bar unavailable") }
            runCatching {
                setupRecyclerView()
                attachSwipeGestures()
                setupFab()
                setupBottomNav()
            }.onFailure { toast("UI init issue") }

            runCatching { initWeatherSafely() }

            runCatching {
                taskVm.load()
                taskVm.message.observe(this) { msg -> if (!msg.isNullOrBlank()) toast(msg) }
                taskVm.tasks.observe(this) { list ->
                    val mapped = list.map { Task(it.title, it.tag, it.done, safeToday()) }
                    allTasks.clear()
                    if (mapped.isEmpty()) {
                        // seed so the screen never looks empty
                        createSampleTasks()
                    } else {
                        allTasks.addAll(mapped)
                    }
                    filterFor(selectedDate)
                }
            }.onFailure { toast("Failed to load tasks") }
        }.onFailure {
            toast("Startup error: ${it.localizedMessage ?: "unknown"}")
            finish()
        }
    }

    // ---------- Menu ----------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_focus      -> { startActivity(Intent(this, FocusActivity::class.java)); true }
            R.id.action_insights   -> { startActivity(Intent(this, InsightsActivity::class.java)); true }
            R.id.action_export_pdf -> { Exporters.sharePdf(this, TaskBus.tasks); true }
            R.id.action_export_ics -> { Exporters.shareIcs(this, TaskBus.tasks); true }

            R.id.action_load -> {
                schedulerVm.loadEvents()
                loadSampleWeek()
                toast("Loaded sample week")
                true
            }
            R.id.action_create -> {
                schedulerVm.createSample()
                createSampleTasks()
                toast("Sample tasks created")
                true
            }

            R.id.action_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }

            // Proper logout: back to login with a cleared back stack
            R.id.action_logout -> {
                val i = Intent(this, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ---------- Week bar ----------
    private fun setupWeekBar() {
        val startOfWeek = safeToday().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val days = (0..6).map { i ->
            val date = startOfWeek.plusDays(i.toLong())
            WeekDay(date = date, selected = date == safeToday())
        }.toMutableList()
        selectedDate = safeToday()

        weekAdapter = WeekAdapter(days) { picked ->
            selectedDate = picked.date
            filterFor(selectedDate)
        }

        binding.weekList.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = weekAdapter
        }
        binding.tvMonth.text = monthLabel(selectedDate)
    }

    private fun loadSampleWeek() {
        val start = safeToday().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val days = (0..6).map { i ->
            val d = start.plusDays(i.toLong())
            WeekDay(d, selected = d == selectedDate)
        }.toMutableList()
        weekAdapter = WeekAdapter(days) { picked ->
            selectedDate = picked.date
            filterFor(selectedDate)
        }
        binding.weekList.adapter = weekAdapter
    }

    // ---------- Tasks ----------
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(mutableListOf())
        binding.taskList.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = taskAdapter
        }
    }

    private fun filterFor(date: LocalDate) {
        val filtered = allTasks.filter { occursOn(it, date) }
        taskAdapter.setItems(filtered)
        binding.tvMonth.text = monthLabel(date)
        TaskBus.tasks = filtered
    }

    private fun setupFab() {
        binding.fabAddTask.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
            val etTitle = dialogView.findViewById<android.widget.EditText>(R.id.etTaskTitle)
            val etTag   = dialogView.findViewById<android.widget.EditText>(R.id.etTaskTag)

            AlertDialog.Builder(this)
                .setTitle("New Task for $selectedDate")
                .setView(dialogView)
                .setPositiveButton("Add") { d, _ ->
                    val title = etTitle.text.toString().trim()
                    val tag   = etTag.text.toString().trim().ifBlank { "No tag" }
                    if (title.isBlank()) {
                        toast("Title required")
                    } else {
                        val newTask = Task(title, tag, false, selectedDate)
                        allTasks.add(newTask)
                        filterFor(selectedDate)
                        taskVm.add(title, tag) { /* server handled elsewhere */ }

                        // optional local reminder at 17:00
                        val at = newTask.dueAtTime ?: LocalTime.of(17, 0)
                        val trigger = LocalDateTime.of(newTask.dueDate, at)
                        val delayMs = Duration.between(LocalDateTime.now(), trigger).toMillis()
                            .coerceAtLeast(0L)

                        Notifier.ensureChannel(this)
                        val req = OneTimeWorkRequestBuilder<ReminderWorker>()
                            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                            .setInputData(ReminderWorker.data(newTask.title, newTask.tag))
                            .addTag("reminder_${newTask.id}")
                            .build()
                        WorkManager.getInstance(this).enqueue(req)
                    }
                    d.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun attachSwipeGestures() {
        val helper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.bindingAdapterPosition
                    if (direction == ItemTouchHelper.LEFT) {
                        val current = taskAdapter.getItem(pos)

                        val depsOk = current.dependsOn.all { depId ->
                            allTasks.firstOrNull { it.id == depId }?.done == true
                        }
                        if (!depsOk) {
                            toast("Finish prerequisites first")
                            taskAdapter.notifyItemChanged(pos)
                            return
                        }

                        val idx = allTasks.indexOfFirst { it === current }
                        if (idx != -1) {
                            allTasks[idx] = current.copy(done = !current.done)
                            filterFor(selectedDate)
                        }
                        toast(if (!current.done) "Marked done" else "Marked active")
                    } else {
                        val removed = taskAdapter.getItem(pos)
                        allTasks.remove(removed)
                        filterFor(selectedDate)
                        Snackbar.make(binding.root, "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                allTasks.add(removed)
                                filterFor(selectedDate)
                            }.show()
                    }
                }
            }
        )
        helper.attachToRecyclerView(binding.taskList)
    }

    // ---------- Nav & Insets ----------
    private fun setupBottomNav() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home    -> true
                R.id.nav_profile -> { toast("Profile (WIP)"); true }
                else -> false
            }
        }
    }

    private fun applyWindowInsets() {
        // Do NOT pad the top app bar — that can hide action icons.
        ViewCompat.setOnApplyWindowInsetsListener(binding.topAppBar) { _, insets ->
            insets
        }
        // Keep bottom nav safe above gesture/nav bar.
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, sys.bottom)
            insets
        }
    }

    // ---------- Weather (safe init) ----------
    private fun initWeatherSafely() {
        val includeRoot: View? = binding.root.findViewById(R.id.weatherCard)
        if (includeRoot == null) return
        try {
            val vm = ViewModelProvider(this)[WeatherViewModel::class.java]
            weatherVm = vm
            weatherBinder = WeatherCardBinder(includeRoot)
            vm.ui.observe(this) { ui -> weatherBinder?.bind(ui) }
            vm.refresh()
        } catch (_: Throwable) {
            includeRoot.visibility = View.GONE // silent failure
        }
    }

    // ---------- Helpers ----------
    private fun createSampleTasks() {
        val today = safeToday()
        allTasks.addAll(
            listOf(
                Task("Finish Wireframes", "Today · High", false, today),
                Task("Prepare API Endpoints", "Tomorrow · Medium", false, today.plusDays(1)),
                Task(
                    "Team Review – Calendar Flow",
                    "Fri · Low",
                    done = false,
                    dueDate = today.with(DayOfWeek.FRIDAY)
                )
            )
        )
        filterFor(selectedDate)
    }

    private fun monthLabel(date: LocalDate): String {
        val m = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
        return "$m ${date.year}"
    }

    private fun safeToday(): LocalDate = try { LocalDate.now() } catch (_: Throwable) {
        LocalDate.of(2025, 1, 1)
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

}
