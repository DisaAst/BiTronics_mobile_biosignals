package com.bitronics.bitronicsmobilebiosignals.presentation.featureGSR

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGsrBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val graph_GSR: GraphView = binding.GSRGraph
        seriesGSR = LineGraphSeries<DataPoint>()
        graph_GSR.addSeries(seriesGSR)
        graph_GSR.title = "График сопротивления кожи"
        graph_GSR.titleTextSize = 25f
        graph_GSR.viewport.setMaxY(1024.0)
        graph_GSR.viewport.setMinY(0.0)
        graph_GSR.viewport.setMaxX(10.0)
        graph_GSR.viewport.setYAxisBoundsManual(true)
        graph_GSR.viewport.setXAxisBoundsManual(true)
        graph_GSR.viewport.isScrollable = true; // enables horizontal scrolling
        graph_GSR.viewport.isScalable = true; // enables horizontal zooming and scrolling
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer_GSR.schedule(ShowData(), 0, 10)

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

        override fun run() {
            activity?.runOnUiThread {

                if (vm.isConnected() && vm.fetchModuleType() ==4) {
                    var array: DoubleArray
                    array = vm.fetchData()
                    seriesGSR.appendData(DataPoint(time, array[0]), true, 10000)
                    time+= 0.002
                    seriesGSR.appendData(DataPoint(time, array[1]), true, 10000)
                    time+= 0.002
                    seriesGSR.appendData(DataPoint(time, array[2]), true, 10000)
                    time+= 0.002
                    seriesGSR.appendData(DataPoint(time, array[3]), true, 10000)
                    time+= 0.002
                    seriesGSR.appendData(DataPoint(time, array[4]), true, 10000)
                    time+= 0.002
                }
            }
        }
    }

}