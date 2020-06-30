package com.cansevin.walknoti

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.huawei.nearestcinema.model.Steps

class DirectionsAdapter (val context: Context, private val steps: List<Steps>) : RecyclerView.Adapter<DirectionsHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectionsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.direction_view_holder, parent, false)
        return DirectionsHolder(view)
    }

    override fun getItemCount(): Int = steps.size

    override fun onBindViewHolder(holder: DirectionsHolder, position: Int) {
        val step: Steps? = steps[position]
        if (step != null) {
            holder.actionDescription.text = step.instruction
            holder.actionDistanceTime.text = context.getString(R.string.distance_time,step.distanceText,step.durationText)
            when(step.action){
                DirectionActionType.STRAIGHT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_go,null))
                DirectionActionType.TURN_LEFT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_left,null))
                DirectionActionType.TURN_RIGHT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_right,null))
                DirectionActionType.TURN_SLIGHT_LEFT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_left,null))
                DirectionActionType.TURN_SLIGHT_RIGHT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_right,null))
                DirectionActionType.TURN_SHARP_LEFT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_left,null))
                DirectionActionType.TURN_SHARP_RIGHT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_right,null))
                DirectionActionType.UTURN_LEFT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_return,null))
                DirectionActionType.UTURN_RIGHT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_return,null))
                DirectionActionType.RAMP_LEFT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_left,null))
                DirectionActionType.RAMP_RIGHT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_right,null))
                DirectionActionType.MERGE.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_go,null))
                DirectionActionType.FORK_LEFT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_left,null))
                DirectionActionType.FORK_RIGHT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_right,null))
                DirectionActionType.FERRY.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_go,null))
                DirectionActionType.FERRY_TRAIN.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_go,null))
                DirectionActionType.ROUNDABOUT_LEFT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_left,null))
                DirectionActionType.ROUNDABOUT_RIGHT.type-> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_up_right,null))
                DirectionActionType.END.type->{
                    holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.bottle128,null))
                    holder.actionDescription.text = step.roadName
                }
                else -> holder.actionImage.setImageDrawable(context.resources.getDrawable(R.drawable.bottle128,null))
            }
        }
    }

}
