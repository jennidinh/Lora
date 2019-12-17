package com.jetbrains.handson.mpp.ny


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 */
class HelpFragment : Fragment() {

private var mTextTo : EditText? = null
private var mTextSubject : EditText? = null
private var mTextMessage : EditText? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_help, container, false)

        mTextTo =  view.findViewById(R.id.toMail)
        mTextSubject = view.findViewById(R.id.textSub)
        mTextMessage = view.findViewById(R.id.textMessage)
        val buttonSend : Button = view.findViewById(R.id.skicka)

        buttonSend.setOnClickListener {
            sendMail()

        }

        return view
    }


private fun sendMail() {
    GlobalScope.launch(Dispatchers.Main) {

        val emailTo = mTextTo?.text.toString()
        val mail : Array<String> = emailTo.split(",").toTypedArray()
        val subj = mTextSubject?.text.toString()
        val mesg = mTextMessage?.text.toString()

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, mail)
        intent.putExtra(Intent.EXTRA_SUBJECT, subj)
        intent.putExtra(Intent.EXTRA_TEXT, mesg)


        intent.setType("message/rfc822")

        try {
            startActivity(Intent.createChooser(intent, "Choose an email client"))

        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()

        }
    }


}

}


