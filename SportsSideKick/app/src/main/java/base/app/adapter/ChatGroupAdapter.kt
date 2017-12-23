package base.app.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import java.util.ArrayList

import base.app.R
import base.app.model.im.ChatInfo
import base.app.util.Utility

class ChatGroupAdapter(internal var context: Context, screenWidth: Int) : RecyclerView.Adapter<ChatGroupAdapter.ViewHolder>() {


    // Start with first item selected
    internal var screenWidth = 0
    private var values: List<ChatInfo>? = null

    fun getValues(): List<ChatInfo>? {
        return values
    }

    fun setValues(values: List<ChatInfo>): ChatGroupAdapter {
        this.values = values
        notifyDataSetChanged()
        return this
    }


    inner class ViewHolder internal constructor(// each data item is just a string in this case
            var view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.chat_head_image_view)
        internal var imageView: CircleImageView? = null
        @BindView(R.id.chat_name)
        internal var chatName: TextView? = null

        init {
            ButterKnife.bind(this, view)
        }
    }

    init {
        values = ArrayList()
        this.screenWidth = screenWidth
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatGroupAdapter.ViewHolder {
        val viewHolder: ViewHolder

        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_head_item, parent, false)
        viewHolder = ViewHolder(view)
        if (screenWidth != 0) {
            view.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT

            view.getLayoutParams().width = screenWidth
        }
        view.setOnClickListener(View.OnClickListener {
            //TODO @Aleksandar add on click
        })
        return viewHolder

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val info = values!![position]
        if (holder.imageView != null) {

            holder.imageView!!.showRoundImage(RequestOptions.circleCropTransform())
        }
        assert(holder.chatName != null)
        holder.chatName!!.text = info.name
    }


    override fun getItemCount(): Int {
        return if (values == null) 0 else values!!.size
    }
}