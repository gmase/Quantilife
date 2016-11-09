package com.firstry.gmase.quantilife.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.firstry.gmase.quantilife.R

/**
 * Created by Guille2 on 04/11/2016
 *
 */
class LoginDialog : DialogFragment() {
    var callbackManager: CallbackManager? = null

    interface IdentityDialogListener {
        fun onFinishEditDialog(inputText: String)
    }

    private var listener: IdentityDialogListener? = null

    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        FacebookSdk.sdkInitialize(context)
        callbackManager = CallbackManager.Factory.create()

        val view = inflater!!.inflate(R.layout.login_dialog, container)
        val loginButton = view.findViewById(R.id.facebookLogin) as LoginButton

        val cancelButton = view.findViewById(R.id.loginCancelButton) as Button
        cancelButton.setOnClickListener {
            listener!!.onFinishEditDialog("cancelled")
            Toast.makeText(context, R.string.identification_canceled, Toast.LENGTH_LONG).show()
            dismiss()
        }

        // If using in a fragment
        loginButton.fragment = this

        loginButton.setReadPermissions("email")
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("MyApp", ("exito" + "User ID: " + loginResult.accessToken.userId + "\n" + "Auth Token: " + loginResult.accessToken.token))
                Toast.makeText(context, R.string.identification_success, Toast.LENGTH_LONG).show()
                listener!!.onFinishEditDialog(loginResult.accessToken.userId)
                dismiss()
            }

            override fun onCancel() {
                Log.d("MyApp", ("cancel"))
                Toast.makeText(context, R.string.identification_canceled, Toast.LENGTH_LONG).show()
                dismiss()
            }

            override fun onError(exception: FacebookException) {
                Log.d("MyApp", ("error"))
                Toast.makeText(context, R.string.identification_failed, Toast.LENGTH_LONG).show()
                dismiss()
            }
        })
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the EditNameDialogListener so we can send events to the host
            listener = context as IdentityDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(context.toString() + " must implement EditNameDialogListener")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }
}