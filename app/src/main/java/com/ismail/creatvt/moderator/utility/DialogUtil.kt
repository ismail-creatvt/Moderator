package com.ismail.creatvt.moderator.utility

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import com.ismail.creatvt.moderator.R
import kotlinx.android.synthetic.main.yes_no_dialog_layout.*

fun Context.showYesNoDialog(message: String, listener: () -> Unit) {
    Dialog(this).apply {
        setContentView(R.layout.yes_no_dialog_layout)
        setCancelable(true)
        window?.setBackgroundDrawableResource(R.color.transparentGray)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        message_text.text = message
        yes_button.setOnClickListener {
            dismiss()
            listener()
        }
        no_button.setOnClickListener {
            dismiss()
        }
        show()
    }
}