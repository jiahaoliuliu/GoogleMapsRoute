package com.jiahaoliuliu.googlemapsroute

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jiahaoliuliu.entity.Step
import com.jiahaoliuliu.googlemapsroute.databinding.ItemStepBinding

class StepsListAdapter(): RecyclerView.Adapter<StepViewHolder>() {
    private var stepsList: List<Step> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        return StepViewHolder(ItemStepBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = stepsList.size

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = stepsList[position]
        holder.bind(step)
    }

    fun updateStepsList(stepsList: List<Step>) {
        this.stepsList = stepsList
        notifyDataSetChanged()
    }
}

class StepViewHolder(private val itemStepBinding: ItemStepBinding) : RecyclerView.ViewHolder(itemStepBinding.root) {

    fun bind(step: Step) {
        itemStepBinding.step = step
        itemStepBinding.executePendingBindings()
    }
}