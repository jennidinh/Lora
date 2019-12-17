package com.jetbrains.handson.mpp.ny


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        val b: Button = view.findViewById(R.id.svknapp)

        //image1.visibility = View.VISIBLE
        b.setOnClickListener {
            if(b.text.equals("Show")) {
                b.text = "Hide"
                yes()
            }

            else if(b.text.equals("Hide")) {
                b.text = "Show"
                no()
            }


        }

        val c : Button = view.findViewById(R.id.closeButton)

        c.setOnClickListener{

            activity?.image1?.visibility = View.INVISIBLE
            activity?.imageView2?.visibility = View.VISIBLE
        }

        return view

    }
    private fun yes() {
        activity!!.im.visibility = View.VISIBLE
    }

    private fun no() {
        activity!!.im.visibility = View.INVISIBLE
    }

}
