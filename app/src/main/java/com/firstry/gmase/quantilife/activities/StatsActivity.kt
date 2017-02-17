package com.firstry.gmase.quantilife.activities

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.firstry.gmase.quantilife.R
import com.firstry.gmase.quantilife.controler.MyModeldApter
import com.firstry.gmase.quantilife.model.AllQuestions
import com.firstry.gmase.quantilife.model.AppDay
import com.firstry.gmase.quantilife.model.ProgressData
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class StatsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val numDias: Int
    var data: ProgressData
    var chart: LineChart? = null
    var toolbar: Toolbar? = null
    var firstTimeHere = false

    private val daysMax = 15 //Es el doble de este valor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        //Reviso si es la primera vez en main
        val settings = getSharedPreferences(resources.getString(R.string.pref), 0)
        firstTimeHere = settings.getBoolean("firstStats", true)
        //TODO hacer algo especial si firstTimeHere

        toolbar = findViewById(R.id.toolbarS) as Toolbar

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)


        val layoutParams = android.support.v7.app.ActionBar.LayoutParams(android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT, android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.LEFT
        supportActionBar!!.setCustomView(layoutInflater.inflate(R.layout.top_bar_stats, null), layoutParams)


        val button1 = findViewById(R.id.go_main) as ImageButton
        button1.setOnClickListener { goToMain() }
        val button2 = findViewById(R.id.go_questions) as ImageButton
        button2.setOnClickListener { goToQuestions() }
//        val button3 = findViewById(R.id.id3) as ImageButton
//        button3.setOnClickListener { goToStats()}

        readData()
    }

    init {
        numDias = AppDay.today()
        data = ProgressData(numDias)
    }

    fun readData() {
        val modelAdapeter = MyModeldApter(this)
        var ent: Entry
        chart = findViewById(R.id.chartEvol) as LineChart
        chart!!.setDescription("")
        chart!!.axisLeft.isEnabled = false

        chart!!.axisRight.setAxisMinValue(0f)
        chart!!.axisRight.setAxisMaxValue(100.4f)
        chart!!.axisRight.setLabelCount(11, true)
        //chart!!.axisRight.granularity = 10f
        //chart!!.axisRight.isGranularityEnabled = true
        //chart!!.axisRight.spaceTop
        chart!!.axisRight.mDecimals = 0
        chart!!.scaleY = 1f
        chart!!.axisRight.setDrawGridLines(false)

        chart!!.xAxis.setAxisMinValue(1f)
        chart!!.xAxis.setAxisMaxValue(numDias + 1.1f)
        chart!!.xAxis.setDrawAxisLine(false)
        chart!!.xAxis.granularity = 1f
        chart!!.axisRight.isGranularityEnabled = true
        chart!!.xAxis.setDrawLabels(true)
        chart!!.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart!!.animateX(3000, Easing.EasingOption.EaseInOutBack)

        chart!!.setDrawBorders(true)
        chart!!.setBorderWidth(1f)
        chart!!.setBorderColor(ContextCompat.getColor(this, R.color.black1))
        chart!!.setBackgroundColor(ContextCompat.getColor(this, R.color.grey1))
        chart!!.legend.isEnabled = false

        chart!!.xAxis.setDrawGridLines(false)

        var i = 0
        var step = 1
        if (numDias >= 2 * daysMax)
            step = (numDias / daysMax).toInt()
        while (i <= numDias) {
            modelAdapeter.historicResult(i)
            ent = Entry(i + 1f, AllQuestions.totalRelationships / 10f)

            //ent= Entry(i+1f,modelAdapeter.historicResult(i, Scope.RELATIONSHIPS).toFloat()/10f)
            data.red.add(ent)
            ent = Entry(i + 1f, AllQuestions.totalHealth / 10f)
            data.blue.add(ent)
            ent = Entry(i + 1f, AllQuestions.totalWork / 10f)
            data.yellow.add(ent)
            ent = Entry(i + 1f, AllQuestions.totalEthics / 10f)
            data.grey.add(ent)


            i += step
        }
        //Garantizamos que mostramos el dia actual en la grafica
        if (i - step != numDias) {
            modelAdapeter.historicResult(numDias)
            ent = Entry(i + 1f, AllQuestions.totalRelationships / 10f)
            data.red.add(ent)
            ent = Entry(i + 1f, AllQuestions.totalHealth / 10f)
            data.blue.add(ent)
            ent = Entry(i + 1f, AllQuestions.totalWork / 10f)
            data.yellow.add(ent)
            ent = Entry(i + 1f, AllQuestions.totalEthics / 10f)
            data.grey.add(ent)
        }

        val dataSetRed = LineDataSet(data.red, resources.getString(R.string.relationships))
        dataSetRed.color = ContextCompat.getColor(this, R.color.lightRed)
        dataSetRed.setCircleColor(ContextCompat.getColor(this, R.color.lightRed))
        dataSetRed.circleRadius = 2f
        dataSetRed.lineWidth = 2f

        val dataSetBlue = LineDataSet(data.blue, resources.getString(R.string.health))
        dataSetBlue.color = ContextCompat.getColor(this, R.color.lightBlue)
        dataSetBlue.setCircleColor(ContextCompat.getColor(this, R.color.lightBlue))
        dataSetBlue.circleRadius = 2f
        dataSetBlue.lineWidth = 2f

        val dataSetYellow = LineDataSet(data.yellow, resources.getString(R.string.work))
        dataSetYellow.color = ContextCompat.getColor(this, R.color.lightYellow)
        dataSetYellow.setCircleColor(ContextCompat.getColor(this, R.color.lightYellow))
        dataSetYellow.circleRadius = 2f
        dataSetYellow.lineWidth = 2f

        val dataSetGrey = LineDataSet(data.grey, resources.getString(R.string.ethics))
        dataSetGrey.color = ContextCompat.getColor(this, R.color.black1)
        dataSetGrey.setCircleColor(ContextCompat.getColor(this, R.color.black1))
        dataSetGrey.circleRadius = 2f
        dataSetGrey.lineWidth = 2f


        dataSetRed.axisDependency = YAxis.AxisDependency.RIGHT
        dataSetBlue.axisDependency = YAxis.AxisDependency.RIGHT
        dataSetYellow.axisDependency = YAxis.AxisDependency.RIGHT
        dataSetGrey.axisDependency = YAxis.AxisDependency.RIGHT
        val lineData = LineData(dataSetRed)
        lineData.addDataSet(dataSetBlue)
        lineData.addDataSet(dataSetYellow)
        lineData.addDataSet(dataSetGrey)

        lineData.setDrawValues(false)
        chart!!.data = lineData
        chart!!.data.getDataSetByIndex(2).isHighlightEnabled = true



        chart!!.invalidate() // refresh

        val settings = getSharedPreferences(resources.getString(R.string.pref), 0)
        //Reviso si es la primera vez en stats
        if (settings.getBoolean("firstStats", true))
            firstTime(settings, "firstStats")
    }

    fun firstTime(settings: SharedPreferences, key: String) {
        val button = Button(this)
        button.text = ""
        button.isEnabled = false
        button.visibility = View.GONE
        ShowcaseView.Builder(this).setTarget(ViewTarget(findViewById(R.id.go_stats))).hideOnTouchOutside().setContentTitle(R.string.showStatsTitle).setContentText(R.string.showStatsText).replaceEndButton(button).setStyle(R.style.CustomShowcaseTheme3).build()
        //Pongo el valor a false para que no vuelva a ocurrir
        val editor = settings.edit()
        editor.putBoolean(key, false)
        editor.apply()
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_relationships) {

        } else if (id == R.id.nav_health) {

        } else if (id == R.id.nav_work) {

        } else if (id == R.id.progress) {
//            val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
//            drawer.closeDrawer(GravityCompat.START)
            //           goToStats()
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_stats, menu)
        return true
    }

    private fun goToStats() {
        val intent = Intent(this, StatsActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

    }

    private fun goToQuestions() {
        val intent = Intent(this, QuestionsActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
