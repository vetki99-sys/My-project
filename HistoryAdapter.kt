package com.smartcalculator.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smartcalculator.data.database.entity.HistoryEntity
import com.smartcalculator.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * محول قائمة السجل
 */
class HistoryAdapter(
    private val onItemClick: (HistoryEntity) -> Unit,
    private val onDeleteClick: (HistoryEntity) -> Unit,
    private val onFavoriteClick: (HistoryEntity) -> Unit
) : ListAdapter<HistoryEntity, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

        fun bind(item: HistoryEntity) {
            binding.apply {
                tvExpression.text = item.expression
                tvResult.text = item.result
                tvTime.text = dateFormat.format(Date(item.timestamp))
                tvType.text = item.calculatorType

                btnDelete.setOnClickListener {
                    onDeleteClick(item)
                }

                btnFavorite.setOnClickListener {
                    onFavoriteClick(item)
                }

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryEntity>() {
        override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return oldItem == newItem
        }
    }
}
