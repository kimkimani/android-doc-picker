package com.greentoad.turtlebody.docpicker.ui.components.file

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.greentoad.turtlebody.docpicker.R
import kotlinx.android.synthetic.main.tb_doc_picker_item_doc.view.*
import org.jetbrains.anko.AnkoLogger
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import com.greentoad.turtlebody.docpicker.core.DocPickerConfig
import com.greentoad.turtlebody.docpicker.labels.DocLabelSet
import org.jetbrains.anko.info


/**
 * Created by WANGSUN on 26-Mar-19.
 */
class DocAdapter: RecyclerView.Adapter<DocAdapter.DocVewHolder>(), AnkoLogger {
    private var mData: MutableList<DocModel> = arrayListOf()
    private var mOnDocClickListener: OnDocClickListener? = null
    private var mPickerConfig: DocPickerConfig = DocPickerConfig()
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocVewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tb_doc_picker_item_doc, parent, false)
        return DocVewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: DocVewHolder, position: Int) {
        holder.bind(mData[position])
    }


    fun setListener(listener : OnDocClickListener){
        mOnDocClickListener = listener
    }

    fun setData(pData: MutableList<DocModel>){
        mData = pData
        notifyDataSetChanged()
    }

    fun updateIsSelected(pData: DocModel){
        val pos = mData.indexOf(pData)
        if(pos>=0){
            mData[pos] = pData
            notifyItemChanged(pos)
        }
    }

    fun setPickerConfig(config: DocPickerConfig){
        mPickerConfig = config
    }
    inner class DocVewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(pData: DocModel){

            setDrawableForMime(itemView, pData.mimeType,pData.filePath)

            itemView.tb_doc_picker_item_doc_ivc.isChecked = pData.isSelected
            val size = (pData.size/1000).toString()

            itemView.tb_doc_picker_doc_name.text = pData.name
            itemView.tb_doc_picker_doc_size.text = "$size KB"

            itemView.setOnClickListener {
                mOnDocClickListener?.onDocCheck(pData)
            }

            if(!mPickerConfig.mAllowMultiSelection){
                itemView.tb_doc_picker_item_doc_ivc.visibility = View.GONE
            }
            else{
                itemView.tb_doc_picker_item_doc_ivc.visibility = View.VISIBLE
            }
        }

        private fun setDrawableForMime(itemView: View, mimeType: String?, filePath: String) {

            var extType = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

            if(extType==null){
                val i = filePath.lastIndexOf('.')
                if (i > 0 && i< filePath.length-1) {
                    extType = filePath.substring(i + 1)
                }
            }

            val mDrawable = ContextCompat.getDrawable(mContext, R.drawable.dr_rect_round_red_doc_background)

            info { "extType: $extType" }
            info { "res: ${DocLabelSet().getLabelForExt(extType!!)}" }

            val label = mPickerConfig.mDocLabelSet.getLabelForExt(extType!!)
            mDrawable?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(mContext,label.colorRes), PorterDuff.Mode.SRC)
            itemView.tb_doc_picker_item_doc_file_ext.text = label.text
            itemView.tb_doc_picker_item_doc_file_ext.background = mDrawable
        }
    }


    interface OnDocClickListener {
        fun onDocCheck(pData: DocModel)
    }
}