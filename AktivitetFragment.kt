package com.jetbrains.handson.mpp.ny


import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import kotlinx.android.synthetic.main.fragment_aktivitet.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
@TargetApi(26) class AktivitetFragment : Fragment(){

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "com.jetbrains.handson.mpp.ny"
    private val description = "Lora Halmstad"

    var s = 1.5
    var count = 0
    var valArr = arrayOfNulls<String>(6)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_aktivitet, container, false)

            GlobalScope.launch(Dispatchers.Default) {
                    while(true) {
                        printTimeValue("http://things.ubidots.com/api/v1.6/devices/{DEVICE_NAME}/{VARIABEL_NAME}/values/?page_size=6&token={TOKEN}")
                        delay(20000)
                    }
            }

        return view
    }
    
    /*
    Denna funktionen lägger in punkter till grafen i en ArrayList
    */   
    private fun dataValues() : ArrayList<Entry> {
        val dataVals = ArrayList<Entry>()
        var temp = 0.0F
        for (i in 0 until valArr.size-1){
            dataVals.add(Entry(temp,valArr[i]!!.toFloat()))
           temp += 1F
        }
        return dataVals
    }

    private fun printTimeValue(url: String){
        var output = ""
       GlobalScope.launch(Dispatchers.Main) {
            val postOperation = async(Dispatchers.IO) { // <- extension on launch scope, launched in IO dispatcher
                // blocking I/O operation
                output = runRequest(url)
            }
            postOperation.await()

            bodyJson.text = output
            val foo = JSonResponse(output)
            /*
            val stamp = Timestamp(System.currentTimeMillis())
            val date = Date(stamp.time)
            val tim = SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(stamp)
            Log.d("hej", tim)
            */
            
            //Array av textview för att visa tid och värde
            var txtArr: Array<TextView> = arrayOf(yay, yay1, yay2, yay3, yay4, yay5)
            Log.d("Dagens datum", date.toString()) 

            for (i in 0..foo.length()+1){
                Log.d("tid", Timestamp(foo.data!!.get(i).timestamp).toString())
                Log.d("värde", foo.data[i].value.toString())
                txtArr.get(i).text = "Time: " + SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format((foo.data.get(i).timestamp))  + "    |    Value: " + foo.data[i].value
                // sparar ner value för visning till graf
                valArr[i] = foo.data?.get(i)?.value.toString()
           }
            activity?.senast?.text = foo.data?.get(0)?.value.toString()

            //här visas grafen
            val dataset1 = LineDataSet(dataValues(), "AKTIVITETSNIVÅ")
             // dataset1.valueTextSize = 10F
             // dataset1.lineWidth = 2F
            val dataset : ArrayList<ILineDataSet> = ArrayList<ILineDataSet>()
            dataset.add(dataset1)
            chart2.data = LineData(dataset)
            chart2.invalidate()

            // Här kolla vi senaste värdet om det är större än gränsen
            if(s <= foo.data?.get(0)!!.value)
            {
                var text1 = "VARNING!"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(getActivity()?.getApplicationContext(), text1, duration)
                toast.show()

                activity?.imageView2?.visibility = View.INVISIBLE
                activity?.image1?.visibility = View.VISIBLE

                count = 0
                // Här sätts notifikationen
                larm()
            }

            else if(count == 720){
                ejLarm() // Uppdatering varje 4h sedan larm har gått osv. 
                count = 0

            }
            else{
                count++
                activity?.imageView2?.visibility = View.VISIBLE
                activity?.image1?.visibility = View.INVISIBLE
            }
           activity?.senast?.text = foo.data.get(0).value.toString()

       }

    }

   private fun runRequest(url: String) : String {
        val client = OkHttpClient()
        client.setConnectTimeout(30, TimeUnit.SECONDS) // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS)
        val request = Request.Builder().url(url).build()
        val response : Response = client.newCall(request).execute()
        if(response.isSuccessful){
            return response.body().string().toString()
        }

        return ""
    }

        private fun larm(){
        notificationManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context, channelId)
                .setContentTitle("Lora Larm")
                .setContentText("OBS! Hög aktivitet ")
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.hejj))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        }
                notificationManager.notify(1234, builder.build())
    }

    private fun ejLarm(){

        notificationManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context, channelId)
                .setContentTitle("4 Timmars Rapport")
                .setContentText("Uppdatering, inget larm")
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.s))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(2345, builder.build())
    }
}

class JSonResponse(json: String) : JSONObject(json) {
    //val type: String? = this.optString("type")
    val data = this.optJSONArray("results")
        ?.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } } // returns an array of JSONObject
        ?.map { Get(it.toString()) } // transforms each JSONObject of the array into Get
}

class Get(json: String) : JSONObject(json) {
    val timestamp = this.optLong("timestamp")
    val value = this.optDouble("value")
}
