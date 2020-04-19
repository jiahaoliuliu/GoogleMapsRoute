package com.jiahaoliuliu.googlemapsroute

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jiahaoliuliu.entity.Place
import com.jiahaoliuliu.googlemapsroute.databinding.ItemPlaceBinding

class LocationsListAdapter(private val onPlaceClickListener: OnPlaceClickListener)
    : RecyclerView.Adapter<PlaceViewHolder>() {
    private var placesList: List<Place> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        return PlaceViewHolder(ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = placesList.size

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placesList[position]
        holder.itemView.setOnClickListener{ onPlaceClickListener.onPlaceClicked(place.id)}
        holder.bind(place)
    }

    fun updatePlacesList(placesList: List<Place>) {
        this.placesList = placesList
        notifyDataSetChanged()
    }
}

class PlaceViewHolder(private val itemPlaceBinding: ItemPlaceBinding) : RecyclerView.ViewHolder(itemPlaceBinding.root) {

    fun bind(place: Place) {
        itemPlaceBinding.place = place
        itemPlaceBinding.executePendingBindings()
    }
}

interface OnPlaceClickListener {

    fun onPlaceClicked(id: String)
}