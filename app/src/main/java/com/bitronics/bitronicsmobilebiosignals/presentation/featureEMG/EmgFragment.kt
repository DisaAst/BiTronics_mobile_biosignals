package com.bitronics.bitronicsmobilebiosignals.presentation.featureEMG

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentEmgBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.ValueDependentColor
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class EmgFragment : Fragment() {

    private var _binding: FragmentEmgBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var seriesEMG: LineGraphSeries<DataPoint>
    lateinit var seriesAmplitudeEMG: BarGraphSeries<DataPoint>

    var time: Double = 0.0
    var trigger = 500

    var timer_EMG = Timer()

    val vm: EmgViewModel by viewModels()

    var bCount1 = false
    var bCount1_1 = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEmgBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val graph_EMG: GraphView = binding.EMGGraph
        seriesEMG = LineGraphSeries<DataPoint>()
        graph_EMG.addSeries(seriesEMG)
        graph_EMG.titleTextSize = 25f
        graph_EMG.viewport.setMaxY(1024.0)
        graph_EMG.viewport.setMinY(0.0)
        graph_EMG.viewport.setMaxX(10.0)
        graph_EMG.viewport.isYAxisBoundsManual = true
        graph_EMG.viewport.isXAxisBoundsManual = true
        graph_EMG.viewport.isScrollable = true; // enables horizontal scrolling
        graph_EMG.viewport.isScalable = true; // enables horizontal zooming and scrolling
        seriesEMG.color = Color.GREEN

        val graphEMGAmplitude: GraphView = binding.amplitudeEmgGraph

        seriesAmplitudeEMG = BarGraphSeries(
            arrayOf(DataPoint(0.0,0.0), DataPoint(1.0, 0.0), DataPoint(2.0, 0.0), DataPoint(3.0, 0.0), DataPoint(4.0, 0.0))
        )

        graphEMGAmplitude.addSeries(seriesAmplitudeEMG)
        graphEMGAmplitude.titleTextSize = 25f
        graphEMGAmplitude.viewport.setMaxY(1024.0)
        graphEMGAmplitude.viewport.setMinY(0.0)
        graphEMGAmplitude.viewport.setMaxX(4.0)
        graphEMGAmplitude.viewport.isYAxisBoundsManual = true
        graphEMGAmplitude.viewport.isXAxisBoundsManual = true
        seriesAmplitudeEMG.spacing = 90
        seriesAmplitudeEMG.valueDependentColor = ValueDependentColor<DataPoint> { data ->
            Color.rgb(
                data.x.toInt() * 255 / 4,
                abs(data.y * 255 / 6).toInt(),
                100
            )
        }
        binding.SeekTrigger.progress = trigger
        vm.setTrigger(trigger)
        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer_EMG.schedule(ShowData(), 0, 10)

        binding.SeekTrigger.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                vm.setTrigger(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        vm.ampl.observe(viewLifecycleOwner){
            seriesAmplitudeEMG
                .resetData(arrayOf(DataPoint(1.0, it)))
            seriesAmplitudeEMG.spacing = 90
            if (it >= trigger) {
                bCount1_1 = true
            }
            if (it < trigger) {
                bCount1_1 = false
                bCount1 = false
            }
            if (!bCount1 && bCount1_1) {
                vm.plusEmg()
                bCount1 = true
            }

        }

        vm.sokr.observe(viewLifecycleOwner){
            binding.tC1.setText(it.toString())
        }

        vm.trigger.observe(viewLifecycleOwner){
            trigger = it
            binding.SeekTrigger.progress = it
            binding.txtTrigger.text = "Значение: $trigger"
        }

        binding.NullKButton.setOnClickListener {
            vm.minusEmg()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer_EMG.cancel()
        _binding = null

    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    inner class ShowData() : TimerTask() {

        var count = 0;
        var arrForProc = DoubleArray(120)
        override fun run() {
            activity?.runOnUiThread {
                if (vm.isConnected() && vm.fetchModuleType() == 3) {
                    var array = doubleArrayOf()
                    if (count == 120) {
                        vm.fetchAmplitudeEMG(arrForProc)
                        count = 0
                    }
                    array = vm.fetchData()
                    seriesEMG.appendData(DataPoint(time, array[0]), true, 10000)
                    arrForProc[count] = array[0]
                    count += 1
                    time += 0.002
                    seriesEMG.appendData(DataPoint(time, array[1]), true, 10000)
                    arrForProc[count] = array[1]
                    count += 1
                    time += 0.002
                    seriesEMG.appendData(DataPoint(time, array[2]), true, 10000)
                    arrForProc[count] = array[2]
                    count += 1
                    time += 0.002
                    seriesEMG.appendData(DataPoint(time, array[3]), true, 10000)
                    arrForProc[count] = array[3]
                    count += 1
                    time += 0.002
                    seriesEMG.appendData(DataPoint(time, array[4]), true, 10000)
                    arrForProc[count] = array[4]
                    count += 1
                    time += 0.002
                }
            }
        }
    }
}
