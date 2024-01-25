package com.example.ungdungbanhang.dialog

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ungdungbanhang.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar


fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
){
    val dialog = BottomSheetDialog(requireContext(),R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog,null)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.setContentView(view)
    dialog.show()

    // anh xa cac thu can thao tac
    val edEmail = view.findViewById<EditText>(R.id.edResetPassword)
    val buttonSend = view.findViewById<Button>(R.id.buttonSendResetPassword)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCancelResetPassword)

    buttonSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        if(email.isEmpty())
        {
            Snackbar.make(requireView(),"Vui lòng nhập gmail muốn khôi phục.",
                Snackbar.LENGTH_LONG).show()
        }else {
            onSendClick(email)
            dialog.dismiss()
        }
    }
    buttonCancel.setOnClickListener {
        dialog.dismiss()
    }
}
