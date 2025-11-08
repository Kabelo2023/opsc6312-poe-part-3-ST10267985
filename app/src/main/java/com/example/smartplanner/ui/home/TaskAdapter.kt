package com.example.smartplanner.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartplanner.R

class TaskAdapter(private val tasks: MutableList<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)
        val tvTaskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val tvTaskTag: TextView = itemView.findViewById(R.id.tvTaskTag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(v)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.tvTaskTitle.text = task.title
        holder.tvTaskTag.text = task.tag

        // prevent recycling from firing the listener
        holder.cbDone.setOnCheckedChangeListener(null)
        holder.cbDone.isChecked = task.done
        holder.cbDone.setOnCheckedChangeListener { _, isChecked ->
            val idx = holder.bindingAdapterPosition
            if (idx != RecyclerView.NO_POSITION) {
                // Task.done is a val: replace the element with a copy that has the new state
                tasks[idx] = tasks[idx].copy(done = isChecked)
                notifyItemChanged(idx)
            }
        }
    }

    override fun getItemCount(): Int = tasks.size

    // Helpers used by HomeActivity
    fun add(task: Task) {
        tasks.add(task)
        notifyItemInserted(tasks.lastIndex)
    }

    fun removeAt(position: Int): Task {
        val removed = tasks.removeAt(position)
        notifyItemRemoved(position)
        return removed
    }

    fun toggleDoneAt(position: Int) {
        tasks[position] = tasks[position].copy(done = !tasks[position].done)
        notifyItemChanged(position)
    }

    fun getItem(position: Int): Task = tasks[position]
    fun getItems(): List<Task> = tasks

    fun setItems(newItems: List<Task>) {
        tasks.clear()
        tasks.addAll(newItems)
        notifyDataSetChanged()
    }
}
