package com.bitronics.bitronicsmobilebiosignals.presentation.featureGSR

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentEegBinding
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentGsrBinding
import com.bitronics.bitronicsmobilebiosignals.presentation.featureEMG.EmgViewModel
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import java.math.RoundingMode
import java.util.*

@AndroidEntryPoint
class GsrFragment : Fragment() {

    private var _binding: FragmentGsrBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var seriesGSR: LineGraphSeries<DataPoint>
    var time: Double = 0.0

    var timer_GSR = Timer()

    val vm: GsrViewModel by viewModels()

    var timeReaction = 0.0
    var amplitude = 0.0
    lateinit var graph_GSR: GraphView

    var tekValue = 0.0

    var porog = 0.0

    var startKeis = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGsrBinding.inflate(inflater, container, false)
        val root: View = binding.root

        graph_GSR = binding.GSRGraph
        seriesGSR = LineGraphSeries<DataPoint>()
        graph_GSR.addSeries(seriesGSR)
        seriesGSR.color = Color.rgb(161, 99, 51)
        graph_GSR.viewport.setMaxY(3.0)
        graph_GSR.viewport.setMinY(0.0)
        graph_GSR.viewport.setMaxX(10.0)
        graph_GSR.viewport.isYAxisBoundsManual = true
        graph_GSR.viewport.isXAxisBoundsManual = true
        graph_GSR.viewport.isScrollable = true; // enables horizontal scrolling
        graph_GSR.viewport.setScrollableY(true); // enables vertical scrolling
        graph_GSR.viewport.setScalableY(true); // enables vertical zooming and scrolling
        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer_GSR.schedule(ShowData(), 0, 10)

        vm.amplitude.observe(viewLifecycleOwner){
            binding.AmplitudeGSR.text = "Амплитуда: ${it.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()}"
            amplitude = it
        }

        vm.timeReaction.observe(viewLifecycleOwner){
            binding.ReactionGSR.text = "Время восстановление: $it"
        }

        binding.NullKButton.setOnClickListener {
            porog = tekValue
            startKeis = true
            vm.setTimeReaction(0.0)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer_GSR.cancel()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    inner class ShowData() : TimerTask() {
        val arrayForProc = DoubleArray(500)
        var count = 0
        @SuppressLint("SetTextI18n")
        override fun run() {
            activity?.runOnUiThread {

                if (vm.isConnected() && vm.fetchModuleType() ==4) {
                    if(count == 500) {
                        vm.fetchAmplitude(arrayForProc)
                        count = 0
                    }

                    val array: DoubleArray = vm.fetchData()
                    val res = (array[0] + array[1] + array[2] + array[3] + array[4]) / 5.0
                    if((res >= porog + 0.025 || res <= porog - 0.025) && startKeis){
                        vm.setTimeReaction(timeReaction.toBigDecimal().setScale(1, RoundingMode.UP).toDouble())
                        timeReaction += 0.01
                    }
                    else timeReaction = 0.0
                    tekValue = res
                    graph_GSR.viewport.setMaxY(res + 0.15)
                    graph_GSR.viewport.setMinY(res - 0.3)
                    seriesGSR.appendData(DataPoint(time, array[0]), true, 10000)
                    arrayForProc[count] = array[0]
                    count ++
                    time+= 0.002
                    seriesGSR.appendData(DataPoint(time, array[1]), true, 10000)
                    arrayForProc[count] = array[1]
                    count ++
                    time+= 0.002
                    seriesGSR.appendData(DataPoint(time, array[2]), true, 10000)
                    arrayForProc[count] = array[2]
                    count ++
                    time+= 0.002
                    seriesGSR.appendData(DataPoint(time, array[3]), true, 10000)
                    arrayForProc[count] = array[3]
                    count ++
                    time+= 0.002
                    seriesGSR.appendData(DataPoint(time, array[4]), true, 10000)
                    arrayForProc[count] = array[4]
                    count ++
                    time+= 0.002
                }
            }
        }
    }

}