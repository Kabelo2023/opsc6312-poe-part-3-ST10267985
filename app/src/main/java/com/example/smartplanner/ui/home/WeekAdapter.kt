package com.example.smartplanner.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smartplanner.R
import java.util.Locale
import java.time.format.DateTimeFormatter

class WeekAdapter(
    private val days: MutableList<WeekDay>,
    private val onSelect: (WeekDay) -> Unit
) : RecyclerView.Adapter<WeekAdapter.DayVH>() {

    private val dowFmt = DateTimeFormatter
        .ofPattern("EEE")
        .withLocale(Locale.getDefault())

    inner class DayVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDow: TextView = itemView.findViewById(R.id.tvDow)
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_week_day, parent, false)
        return DayVH(v)
    }

    override fun onBindViewHolder(holder: DayVH, position: Int) {
        val item = days[position]

        holder.tvDow.text = item.date.format(dowFmt)
        holder.tvDay.text = String.format(Locale.getDefault(), "%02d", item.date.dayOfMonth)

        // background toggle without deprecated getDrawable
        val bgRes = if (item.selected) R.drawable.bg_day_selected else R.drawable.bg_day_unselected
        holder.tvDay.background = ContextCompat.getDrawable(holder.itemView.context, bgRes)

        holder.itemView.setOnClickListener {
            val old = days.indexOfFirst { it.selected }
            if (old != -1) {
                days[old].selected = false
                notifyItemChanged(old)
            }
            days[position].selected = true
            notifyItemChanged(position)
            onSelect(days[position])
        }
    }

    override fun getItemCount(): Int = days.size
}
