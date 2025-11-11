package com.example.foodapi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MealAdapter(private val meals: List<Meal>) :
    RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivThumb: ImageView = view.findViewById(R.id.ivMealThumb)
        val tvName: TextView = view.findViewById(R.id.tvMealName)
        val tvOrigin: TextView = view.findViewById(R.id.tvMealOrigin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meal_item, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        holder.tvName.text = meal.name
        holder.tvOrigin.text = meal.origin
        Glide.with(holder.itemView.context).load(meal.thumbnail).into(holder.ivThumb)


        Glide.with(holder.itemView.context)
            .load(meal.thumbnail)
            .into(holder.ivThumb)
    }

    override fun getItemCount(): Int = meals.size
}
