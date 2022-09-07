package com.bitronics.bitronicsmobilebiosignals.presentation.featureEEG

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bitronics.bitronicsmobilebiosignals.R
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentEegBinding
import com.google.android.material.slider.Slider
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.ValueDependentColor
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import java.math.RoundingMode
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class EegFragment : Fragment() {

    private var _binding: FragmentEegBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var seriesEEG: LineGraphSeries<DataPoint>
    lateinit var seriesEEGSpectr: BarGraphSeries<DataPoint>

    var time: Double = 0.0

    var timer_EEG = Timer()

    val vm: EegViewModel by viewModels()
    var player: MediaPlayer? = null

    var amplAlpha = 0.0
    var m = 0
    var trigger = 1.5

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEegBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val graph_EEG: GraphView = binding.EEGGraph
        seriesEEG = LineGraphSeries<DataPoint>()
        seriesEEG.color = Color.rgb(161, 99, 51)
        graph_EEG.addSeries(seriesEEG)
        graph_EEG.titleTextSize = 25f
        graph_EEG.viewport.setMaxY(3.0)
        graph_EEG.viewport.setMinY(0.0)
        graph_EEG.viewport.setMaxX(10.0)
        graph_EEG.viewport.isYAxisBoundsManual = true
        graph_EEG.viewport.isXAxisBoundsManual = true
        graph_EEG.viewport.isScrollable = true; // enables horizontal scrolling
        graph_EEG.viewport.isScalable = true; // enables horizontal zooming and scrolling
        val graph_Spectr: GraphView = binding.spectrEEGGraph

        seriesEEGSpectr = BarGraphSeries(
            arrayOf(
                DataPoint(0.0, 0.0), DataPoint(1.0, 0.0), DataPoint(2.0, 0.0),
                DataPoint(3.0, 0.0), DataPoint(4.0, 0.0), DataPoint(5.0, 0.0),
                DataPoint(6.0, 0.0), DataPoint(7.0, 0.0), DataPoint(8.0, 0.0),
                DataPoint(9.0, 0.0), DataPoint(10.0, 0.0), DataPoint(11.0, 0.0),
                DataPoint(12.0, 0.0), DataPoint(13.0, 0.0), DataPoint(14.0, 0.0),
                DataPoint(15.0, 0.0), DataPoint(16.0, 0.0), DataPoint(17.0, 0.0),
                DataPoint(18.0, 0.0), DataPoint(19.0, 0.0), DataPoint(20.0, 0.0),
            ))
        graph_Spectr.addSeries(seriesEEGSpectr)
        graph_Spectr.viewport.setMaxY(30.0)
        graph_Spectr.viewport.setMinY(0.0)
        graph_Spectr.viewport.setMaxX(28.0)
        graph_Spectr.viewport.isYAxisBoundsManual = true

        vm.setTrigger(trigger)

        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer_EEG.schedule(ShowData(), 0, 10)

        vm.spectr.observe(viewLifecycleOwner) {
            seriesEEGSpectr
                .resetData(
                    arrayOf(
                        DataPoint(0.0, 0.0), DataPoint(1.0, it[1]), DataPoint(2.0, it[2]),
                        DataPoint(3.0, it[3]), DataPoint(4.0, it[4]), DataPoint(5.0, it[5]),
                        DataPoint(6.0, it[6]), DataPoint(7.0, it[7]), DataPoint(8.0, it[8]),
                        DataPoint(9.0, it[9]), DataPoint(10.0, it[10]), DataPoint(11.0, it[11]),
                        DataPoint(12.0, it[12]), DataPoint(13.0, it[13]), DataPoint(14.0, it[14]),
                        DataPoint(15.0, it[15]), DataPoint(16.0, it[16]), DataPoint(17.0, it[17]),
                        DataPoint(18.0, it[18]), DataPoint(19.0, it[19]), DataPoint(20.0, it[20]),
                    )
                )
            amplAlpha = it[8] + it[9] + it[10] + it[11]
        }

        binding.SeekEEG.addOnChangeListener(object : Slider.OnChangeListener{
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                vm.setTrigger(value.toDouble())
            }

        })


        vm.trigger.observe(viewLifecycleOwner){
            trigger = it
            binding.SeekEEG.value = it.toFloat()
            binding.txtTrigEEG.text = "Значение: ${trigger.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()}"
        }

        binding.music.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { arg0, id ->
            when (id) {
                R.id.Rain -> {
                    m = 1
                    stopSound()
                }
                R.id.BohemianRapsody -> {
                    m = 2
                    stopSound()
                }
                R.id.Lunnaya_Sonata -> {
                    stopSound()
                    m = 3
                    stopSound()
                }
                else -> {}
            }
        })
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer_EEG.cancel()
        _binding = null
    }

    inner class ShowData() : TimerTask() {

        var count = 0
        var arrForProc = DoubleArray(1024)

        var n = 1024
        override fun run() {

            activity?.runOnUiThread {

                if (vm.isConnected() && vm.fetchModuleType() == 5) {
                    if (count == n) {
                        vm.fetchSpectr(arrForProc)
                        count = 0
                    }
                    val array: DoubleArray = vm.fetchData()
                    seriesEEG.appendData(DataPoint(time, array[0]), true, 10000)
                    if (count == n) {
                        vm.fetchSpectr(arrForProc)
                        count = 0
                    }
                    if(amplAlpha > trigger) playSound()
                    else stopSound()
                    arrForProc[count] = array[0]
                    count += 1
                    time += 0.002
                    seriesEEG.appendData(DataPoint(time, array[1]), true, 10000)
                    if (count == n) {
                        vm.fetchSpectr(arrForProc)
                        count = 0
                    }
                    arrForProc[count] = array[1]
                    count += 1
                    time += 0.002
                    seriesEEG.appendData(DataPoint(time, array[2]), true, 10000)
                    if (count == n) {
                        vm.fetchSpectr(arrForProc)
                        count = 0
                    }
                    arrForProc[count] = array[2]
                    count += 1
                    time += 0.002
                    seriesEEG.appendData(DataPoint(time, array[3]), true, 10000)
                    if (count == n) {
                        vm.fetchSpectr(arrForProc)
                        count = 0
                    }
                    arrForProc[count] = array[3]
                    count += 1
                    time += 0.002
                    seriesEEG.appendData(DataPoint(time, array[4]), true, 10000)
                    if (count == n) {
                        vm.fetchSpectr(arrForProc)
                        count = 0
                    }
                    arrForProc[count] = array[4]
                    count += 1
                    time += 0.002
                }
                else stopSound()
            }
        }
    }

    fun playSound() {
        if (m != 0) {
            if (player == null) {
                player = MediaPlayer()
                if (m == 1) player = MediaPlayer.create(
                    activity,
                    R.raw.rain1
                ) else if (m == 2) player = MediaPlayer.create(
                    activity,
                    R.raw.queen
                ) else if (m == 3) player =
                    MediaPlayer.create(activity, R.raw.betkhoven)
                player?.setOnCompletionListener(OnCompletionListener { stopSound() })
            }
            player?.start()
        }
    }

    fun stopSound() {
        if (player != null) {
            player?.release()
            player = null
        }
    }
}
/*
if (vm.isConnected() && vm.fetchModuleType() == 5) {
    var array: DoubleArray
    array = vm.fetchData()
    seriesEEG.appendData(DataPoint(time, array[0]), true, 10000)
    time+= 0.002
    seriesEEG.appendData(DataPoint(time, array[1]), true, 10000)
    time+= 0.002
    seriesEEG.appendData(DataPoint(time, array[2]), true, 10000)
    time+= 0.002
    seriesEEG.appendData(DataPoint(time, array[3]), true, 10000)
    time+= 0.002
    seriesEEG.appendData(DataPoint(time, array[4]), true, 10000)
    time+= 0.002
}
*/
