package fastcampus.aop.part3.aop_part3_chapter05_final

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// 나랑 매치된 사용자들 목록 표시할 리스트 어뎁터
class MatchedUserAdapter: ListAdapter<CardItem, MatchedUserAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(cardItem: CardItem) {
            view.findViewById<TextView>(R.id.userNameTextView).text = cardItem.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_user, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CardItem>() {
            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem) =
                oldItem.userId == newItem.userId
        }
    }

}