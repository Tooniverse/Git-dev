package com.example.test4_map

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.os.*
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.test4_map.bluetooth.BluetoothManager
import com.example.test4_map.utils.Const
import com.example.test4_map.utils.Logs
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.jetbrains.anko.*
import java.nio.charset.Charset
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Double.parseDouble
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlin.concurrent.timer

class MarkerItem(var m_num : String = "", var lat : Double = 0.0, var lon : Double = 0.0 , var recieve_time : String = "")

class MainActivity : AppCompatActivity(){

    companion object {
        val TAG = "MainActivity"
    }
    private val mBtHandler = BluetoothHandler()
    private val mBluetoothManager: BluetoothManager = BluetoothManager.getInstance()
    private var mIsConnected = false
    private var timerTask : Timer? = null

    var text_gps = "GPS"
    var text_sound = "CAL"
    var text_check = "CHK"
    var realm = Realm.getDefaultInstance()
    var longGPSText =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logs.d(TAG, "# MainActivity - onCreate()")

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        navView.setOnNavigationItemSelectedListener {item ->
            when (item.itemId) {
                R.id.navigation_notifications -> {
                    navController.navigate(
                        R.id.navigation_notifications)
                }
                R.id.navigation_dashboard -> {
                    val bundle = bundleOf("navGPS" to longGPSText)
                    //val navController1 = findNavController(R.id.nav_host_fragment)
                    navController.navigate(
                        R.id.navigation_dashboard, bundle )
                }

                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

         val destinationChangedListener = NavController.OnDestinationChangedListener { _, destination, _ ->
             if (destination.id == R.id.navigation_dashboard) {
                 floatingActionButton.hide()
                 text_chat.visibility = View.INVISIBLE
                 text_status.visibility = View.INVISIBLE
                 img_status.visibility = View.INVISIBLE


             }
             else if (destination.id == R.id.navigation_notifications)
             {
                 floatingActionButton.hide()
                 text_chat.visibility = View.INVISIBLE
                 text_status.visibility = View.INVISIBLE
                 img_status.visibility = View.INVISIBLE
             }
             else {
                 floatingActionButton.show()
                 text_chat.visibility = View.VISIBLE
                 text_status.visibility = View.VISIBLE
                 img_status.visibility = View.VISIBLE
             }

         }
        navController.addOnDestinationChangedListener(destinationChangedListener)


        val ab = supportActionBar
        ab!!.hide()

        if(!checkBluetooth()) {
            // btn_scan.isEnabled = false
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, Const.REQUEST_ENABLE_BT)
        } else {
            //btn_scan.isEnabled = true
        }
        mBluetoothManager.setHandler(mBtHandler)

// Register for broadcasts when a device is discovered
        var filter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        this.registerReceiver(mReceiver, filter)

        initialize()
    }



    private fun initialize() {
        val aries = findViewById<ImageView>(R.id.img_status)
        aries.setImageDrawable(resources.getDrawable(android.R.drawable.presence_invisible, null))
        text_status.text = "Please Choose the Bluetooth Device -->"

        text_chat.text = ""

        floatingActionButton.setOnClickListener {
            if(mIsConnected) {
                disconnect()
                text_status.visibility = View.INVISIBLE
                img_status.visibility = View.INVISIBLE
            }
            else {
                doScan()
                text_status.visibility = View.VISIBLE
                img_status.visibility = View.VISIBLE
            }
        }
    }

    fun toastMe(view: View) {
        gps_pause()
        toast("모두에게 호출을 보냈습니다")
        mBluetoothManager.write(text_sound.toByteArray())
        gps_start()

    }

    fun checkMe(view: View) {
        gps_pause()
        toast("순차적으로 정보가 들어옵니다")
        mBluetoothManager.write(text_check.toByteArray())
    }

    fun TestMe(view: View) {
// val myToast = Toast.makeText(this, message, duration);
        var arg : String = ""
       var tmp : Long = realm.where<logGPS>().count()
        arg += "전체 : "
        arg += tmp.toString()
        arg += "\n"
        for (it: Int in 0..5) {
            var test = countId(it.toString())
            arg += it.toString()
            arg += " : "
            arg += test.toString()
            arg += "개 \n"
        }
        toast(arg.toString())
        startActivity<StatusActivity>()
    }

    fun infoAlert(view: View) {
        alert("동의대학교 컴퓨터공학과\n캡스톤프로젝트 2조 \n이삼주 이현준 김용진 유현우 하광주", "만든이") {
            okButton { null }
        }.show()
    }

    fun checkAlert(view : View) {
        alert("확인하셨나요?") {
            positiveButton("네") {
                    view.isEnabled = false
            }
            negativeButton("아니오") { }
            // okButton { toast("확인 버튼만 만들고 싶을때") }
        }.show()
    }

    fun resetAlert(view: View) {
        alert("초기화하시겠습니까?") {
            positiveButton("네") {
                button1.isEnabled = true
                button2.isEnabled = true
                button3.isEnabled = true
                button4.isEnabled = true
                button5.isEnabled = true
                button6.isEnabled = true
            }
            negativeButton("아니오") { }
            // okButton { toast("확인 버튼만 만들고 싶을때") }
        }.show()
    }

    override fun onBackPressed() {
        // Exit dialog
        val alertDiag = AlertDialog.Builder(this)
        alertDiag.setMessage("종료하시겠습니까?")
        alertDiag.setPositiveButton("종료") { _: DialogInterface, _: Int ->
            // finish app
            finishApp()
        }
        alertDiag.setNegativeButton("취소") { _: DialogInterface, _: Int -> }

        val alert = alertDiag.create()
        alert.setTitle("종료")
        alert.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        finalizeActivity()
        realm.close()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        finalizeActivity()
    }

    private fun finalizeActivity() {

        // Close bluetooth connection
        mBluetoothManager.stop()
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver)
    }

    private fun finishApp() {
        ActivityCompat.finishAffinity(this);
        //System.runFinalizersOnExit(true);
        System.exit(0);
    }


    private fun checkBluetooth(): Boolean {
        return mBluetoothManager.isBluetoothEnabled()
    }

    private fun doScan() {
        val intent = Intent(this, DeviceListActivity::class.java)
        startActivityForResult(intent, Const.REQUEST_CONNECT_DEVICE)
    }

    private fun setConnected(connected: Boolean) {
        mIsConnected = connected
        if(connected) {
            gps_start()
            //Timer().schedule(5000, 8000) {                mBluetoothManager.write(text_gps.toByteArray())            }

        }
        else {
        }
    }

    private fun disconnect() {
        mBluetoothManager.stop()
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            // When discovery finds a device
            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED == action) {
                val scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1)
                when(scanMode) {
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> {
                        floatingActionButton.isEnabled = false
                        floatingActionButton.isVisible = false

                        text_chat.append("\nSCAN_MODE_CONNECTABLE_DISCOVERABLE")
                        text_chat.append("\nMake server socket")

                        mBluetoothManager.start()
                    }
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> {
                        floatingActionButton.isEnabled = true
                        floatingActionButton.isVisible = true
                        floatingActionButton.show()
                        text_chat.append("\nSCAN_MODE_CONNECTABLE")
                    }
                    BluetoothAdapter.SCAN_MODE_NONE -> {
                        // Bluetooth is not enabled
                        floatingActionButton.isEnabled = false
                        floatingActionButton.isVisible = false
                        floatingActionButton.hide()
                        text_chat.append("\nBluetooth is not enabled!!")

                    }
                }
            }
        }
    }

    inner class BluetoothHandler : Handler() {
        //메시지 수신
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BluetoothManager.MESSAGE_READ -> {
                    if (msg.obj != null) {
                        val bluetooth_string =
                            (msg.obj as ByteArray).toString(Charset.defaultCharset())
                        bluetooth_string.replace(" ", "")
                        bluetooth_string.trim()

                        var sss = bluetooth_string.split("!")
                        //toast(sss.size.toString())
                        if (sss?.size > 1) {
                            longGPSText = ""
                            text_chat.text = ""
                            for (it: Int in 0..sss?.size.toInt()-1) {
                                InsertData(sss[it])
                            }
                        }
                    }
                }

                BluetoothManager.MESSAGE_STATE_CHANGE -> {
                    when(msg.arg1) {
                        BluetoothManager.STATE_NONE -> {    // we're doing nothing
                            text_status.text = resources.getString(R.string.bt_title) + ": " + resources.getString(R.string.bt_state_init)
                            img_status.setImageDrawable(resources.getDrawable(android.R.drawable.presence_invisible))
                            setConnected(false)
                        }
                        BluetoothManager.STATE_LISTEN -> {  // now listening for incoming connections
                            text_status.text = resources.getString(R.string.bt_title) + ": " + resources.getString(R.string.bt_state_wait)
                            //img_status.setImageDrawable(resources.getDrawable(android.R.drawable.presence_invisible))
                            img_status.setImageDrawable(resources.getDrawable(android.R.drawable.presence_busy))

                            setConnected(false)
                        }
                        BluetoothManager.STATE_CONNECTING -> {  // connecting to remote
                            text_status.text = resources.getString(R.string.bt_title) + ": " + resources.getString(R.string.bt_state_connect)
                            img_status.setImageDrawable(resources.getDrawable(android.R.drawable.presence_away))
                        }
                        BluetoothManager.STATE_CONNECTED -> {   // now connected to a remote device
                            text_status.text = resources.getString(R.string.bt_state_connected)
                            img_status.setImageDrawable(resources.getDrawable(android.R.drawable.presence_online))
                            setConnected(true)
                        }
                    }
                }
                BluetoothManager.MESSAGE_DEVICE_NAME -> {
                    if(msg.data != null) {
                        val deviceName = msg.data.getString(BluetoothManager.MSG_DEVICE_NAME)
                        val deviceAddr = msg.data.getString(BluetoothManager.MSG_DEVICE_ADDRESS)
                        text_status.append(" to ")
                        text_status.append(deviceName)
                        text_status.append(", ")
                        text_status.append(deviceAddr)
                    }
                }
            }

            super.handleMessage(msg)
        }
    }

    private fun gps_pause() {
        timerTask?.cancel()
    }

    private  fun gps_start() {
        timerTask = timer(initialDelay = 5000, period = 10000) {
            mBluetoothManager.write(text_gps.toByteArray())
        }
    }
    fun checkarg(args: String) : Boolean {
        var numeric = true
        try {
            val num = parseDouble(args)
        } catch (e: NumberFormatException) {
            numeric = false
        }
        return numeric
    }



    private fun InsertData(arg : String) {
        realm.beginTransaction()

        var sss = arg.split("/")
        if (sss.size == 3) {
            if (checkarg(sss[1]) && checkarg(sss[2])) {
                val newItem = realm.createObject<logGPS>(nextId())
                val sdf = SimpleDateFormat("yyyy.MM.dd hh:mm:ss")
                val currentDate = sdf.format(Date())

                newItem.m_num = sss[0]
                newItem.lat = sss[1].toDouble()
                newItem.lon = sss[2].toDouble()
                newItem.recieve_time = currentDate

                var str : String =
                    newItem.m_num + "/" + newItem.lat.toString() + "/" + newItem.lon.toString() + "/" + newItem.recieve_time + "!"
                //toast(str)
                longGPSText += str
                text_chat.append(str)
                text_chat.append("\n")

                if (countId(sss[0]) >= 5) {
                    val deleteItem =
                        realm.where<logGPS>().equalTo("id", minId(sss[0])).findFirst()!!
                    deleteItem.deleteFromRealm()
                }
                realm.commitTransaction()
            }
        }
    }

    private fun countId(arg: String) : Int {
        val equalNum = realm.where<logGPS>().equalTo("m_num",arg).findAll()!!
        val countID = equalNum.count()

        if(countID != null)
            return  countID
        return  0
    }

    private fun minId(arg: String) : Int {
        val equalNum = realm.where<logGPS>().equalTo("m_num",arg).findAll()!!
        val minID = equalNum.min("id")

        if(minID != null)
            return  minID.toInt()
        return  0
    }

    private fun nextId() : Int  {
        val maxID = realm.where<logGPS>().max("id")
        if(maxID != null)
            return maxID.toInt() + 1
        return 0
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logs.d(TAG, "onActivityResult $resultCode")

        when (requestCode) {

            Const.REQUEST_CONNECT_DEVICE -> {
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    val address = data?.extras?.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)
                    // Attempt to connect to the device
                    mBluetoothManager.connect(address)
                }
            }
            Const.REQUEST_ENABLE_BT -> {
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a BT session
                    floatingActionButton.isEnabled = true
                } else {
                    // User did not enable Bluetooth or an error occured
                    Logs.e(TAG, "BT is not enabled")
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show()
                    floatingActionButton.isEnabled = false
                }
            }
            Const.REQUEST_DISCOVERABLE -> {
                // resultCode is always false
            }
        }    // End of switch(requestCode)
    }


}
