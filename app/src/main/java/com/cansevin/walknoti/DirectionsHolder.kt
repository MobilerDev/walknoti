package com.cansevin.walknoti

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DirectionsHolder(view: View) : RecyclerView.ViewHolder(view) {
    val actionImage: ImageView = view.findViewById(R.id.actionImage)
    val actionDescription: TextView = view.findViewById(R.id.actionDescription)
    val actionDistanceTime: TextView = view.findViewById(R.id.actionDistanceTime)
}