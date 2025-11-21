//package com.maverick.kmjshowroom.ui.transaksi
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.RecyclerView
//import com.maverick.kmjshowroom.R
//import com.maverick.kmjshowroom.Model.Transaction
//
//class TransactionAdapter(
//    private val context: Context,
//    private val transactionList: List<Transaction>
//) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
//
//    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val txtTransactionId: TextView = view.findViewById(R.id.txtTransactionId)
//        val txtDate: TextView = view.findViewById(R.id.txtDate)
//        val txtCustomerName: TextView = view.findViewById(R.id.txtCustomerName)
//        val txtPhoneNumber: TextView = view.findViewById(R.id.txtPhoneNumber)
//        val txtCar: TextView = view.findViewById(R.id.txtCar)
//        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
//        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
//        val txtDealPrice: TextView = view.findViewById(R.id.txtDealPrice)
//        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_transaksi, parent, false)
//        return TransactionViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
//        val transaction = transactionList[position]
//
//        holder.txtTransactionId.text = transaction.id
//        holder.txtDate.text = transaction.date
//        holder.txtCustomerName.text = transaction.customerName
//        holder.txtPhoneNumber.text = transaction.phoneNumber
//        holder.txtCar.text = transaction.car
//        holder.txtDescription.text = transaction.description
//        holder.txtPrice.text = transaction.price
//        holder.txtDealPrice.text = transaction.dealPrice
//
//        when (transaction.status) {
//            "Completed" -> {
//                holder.txtStatus.text = "Completed"
//                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.green_status))
//                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_completed)
//            }
//            "Pending" -> {
//                holder.txtStatus.text = "Pending"
//                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.orange_status))
//                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_pending)
//            }
//            "Canceled" -> {
//                holder.txtStatus.text = "Canceled"
//                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.red_status))
//                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_canceled)
//            }
//        }
//    }
//
//    override fun getItemCount(): Int = transactionList.size
//}
//package com.maverick.kmjshowroom.ui.transaksi
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.RecyclerView
//import com.maverick.kmjshowroom.R
//import com.maverick.kmjshowroom.Model.Transaction
//
//class TransactionAdapter(
//
//    private val context: Context,
//    private val transactionList: List<Transaction>
//) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
//
//
//    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val txtTransactionId: TextView = view.findViewById(R.id.txtTransactionId)
//        val txtDate: TextView = view.findViewById(R.id.txtDate)
//        val txtCustomerName: TextView = view.findViewById(R.id.txtCustomerName)
//        val txtPhoneNumber: TextView = view.findViewById(R.id.txtPhoneNumber)
//        val txtCar: TextView = view.findViewById(R.id.txtCar)
//        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
//        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
//        val txtDealPrice: TextView = view.findViewById(R.id.txtDealPrice)
//        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_transaksi, parent, false)
//        return TransactionViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
//        val transaction = transactionList[position]
//
//        holder.txtTransactionId.text = transaction.id
//        holder.txtDate.text = transaction.date
//        holder.txtCustomerName.text = transaction.customerName
//        holder.txtPhoneNumber.text = transaction.phoneNumber
//        holder.txtCar.text = transaction.car
//        holder.txtDescription.text = transaction.description
//        holder.txtPrice.text = transaction.price
//        holder.txtDealPrice.text = transaction.dealPrice
//
//        when (transaction.status) {
//            "Completed" -> {
//                holder.txtStatus.text = "Completed"
//                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.green_status))
//                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_completed)
//            }
//            "Pending" -> {
//                holder.txtStatus.text = "Pending"
//                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.orange_status))
//                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_pending)
//            }
//            "Canceled" -> {
//                holder.txtStatus.text = "Canceled"
//                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.red_status))
//                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_canceled)
//            }
//        }
//    }
//
//    override fun getItemCount(): Int = transactionList.size
//
//
//}

package com.maverick.kmjshowroom.ui.transaksi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.Model.Transaction

class TransactionAdapter(
    private val context: Context,
    private val transactionList: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    var onDetailClick: ((Transaction) -> Unit)? = null

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTransactionId: TextView = view.findViewById(R.id.txtTransactionId)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtCustomerName: TextView = view.findViewById(R.id.txtCustomerName)
        val txtPhoneNumber: TextView = view.findViewById(R.id.txtPhoneNumber)
        val txtCar: TextView = view.findViewById(R.id.txtCar)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtDealPrice: TextView = view.findViewById(R.id.txtDealPrice)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
        val txtViewDetails: TextView = view.findViewById(R.id.txtViewDetails) // TextView!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]

        holder.txtTransactionId.text = transaction.id
        holder.txtDate.text = transaction.date
        holder.txtCustomerName.text = transaction.customerName
        holder.txtPhoneNumber.text = transaction.phoneNumber
        holder.txtCar.text = transaction.car
        holder.txtDescription.text = transaction.description
        holder.txtPrice.text = transaction.price
        holder.txtDealPrice.text = transaction.dealPrice

        // Status
        when (transaction.status) {
            "Completed" -> {
                holder.txtStatus.text = "Completed"
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.green_status))
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_completed)
            }
            "Pending" -> {
                holder.txtStatus.text = "Pending"
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.orange_status))
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_pending)
            }
            "Canceled" -> {
                holder.txtStatus.text = "Canceled"
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.red_status))
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_canceled)
            }
        }

        // KLIK VIEW DETAILS → PAKAI TextView!
        holder.txtViewDetails.setOnClickListener {
            onDetailClick?.invoke(transaction)
        }
    }

    override fun getItemCount() = transactionList.size
}