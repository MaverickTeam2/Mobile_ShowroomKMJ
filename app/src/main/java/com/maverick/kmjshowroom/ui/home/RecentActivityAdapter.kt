package com.maverick.kmjshowroom.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.Model.ActivityItem
import com.maverick.kmjshowroom.R
import java.util.Calendar
import java.util.Locale

class RecentActivityAdapter(
    private val items: List<ActivityItem>,
    private val onClick: (ActivityItem) -> Unit
) : RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_title)
        val description: TextView = view.findViewById(R.id.tv_description)
        val time: TextView = view.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.activity_type
        holder.description.text = item.description
        holder.time.text = formatRelativeTime(item.created_at)

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    private fun formatRelativeTime(dateString: String): String {
        // Format dari API → sesuaikan kalau berbeda
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString)

        val now = Calendar.getInstance()
        val cal = Calendar.getInstance()
        cal.time = date

        // Format jam & menit
        val timeFormat = java.text.SimpleDateFormat("HH.mm", Locale.getDefault())
        val timeOnly = timeFormat.format(date)

        // Cek hari ini
        val isToday = now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)

        if (isToday) {
            return "Hari ini, $timeOnly"
        }

        // Cek kemarin
        now.add(Calendar.DAY_OF_YEAR, -1)
        val isYesterday = now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)

        if (isYesterday) {
            return "Kemarin, $timeOnly"
        }

        // Format hari dan tanggal lengkap
        val dayNameFormat = java.text.SimpleDateFormat("EEEE", Locale("in", "ID"))
        val dateLongFormat = java.text.SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))

        val dayName = dayNameFormat.format(date)
        val longDate = dateLongFormat.format(date)

        return "$dayName, $longDate, $timeOnly"
    }


    override fun getItemCount(): Int = items.size
}

