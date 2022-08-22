package com.bitronics.bitronicsmobilebiosignals.presentation.featureEEG

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentEegBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EegFragment : Fragment() {

    private var _binding: FragmentEegBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var seriesEEG: LineGraphSeries<DataPoint>
    var time: Double = 0.0

    var timer_EEG = Timer()

    val vm: EegViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEegBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val graph_EEG: GraphView = binding.EEGGraph
        seriesEEG = LineGraphSeries<DataPoint>()
        graph_EEG.addSeries(seriesEEG)
        graph_EEG.title = "График электрической активности мозга"
        graph_EEG.titleTextSize = 25f
        graph_EEG.viewport.setMaxY(1024.0)
        graph_EEG.viewport.setMinY(0.0)
        graph_EEG.viewport.setMaxX(10.0)
        graph_EEG.viewport.isYAxisBoundsManual = true
        graph_EEG.viewport.isXAxisBoundsManual = true
        graph_EEG.viewport.isScrollable = true; // enables horizontal scrolling
        graph_EEG.viewport.setScrollableY(true); // enables vertical scrolling
        graph_EEG.viewport.isScalable = true; // enables horizontal zooming and scrolling
        graph_EEG.viewport.setScalableY(true); // enables vertical zooming and scrolling
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer_EEG.schedule(ShowData(), 0, 10)
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

        var testData = 0.0
        override fun run() {
            activity?.runOnUiThread {

                if(testData >= 1023.0) testData = 0.0
                seriesEEG.appendData(DataPoint(time, testData), true, 10000)
                testData += 1.0
                time+=0.002
                if(testData >= 1023.0) testData = 0.0
                seriesEEG.appendData(DataPoint(time, testData), true, 10000)
                testData += 1.0
                time+=0.002
                if(testData >= 1023.0) testData = 0.0
                seriesEEG.appendData(DataPoint(time, testData), true, 10000)
                testData += 1.0
                time+=0.002
                if(testData >= 1023.0) testData = 0.0
                seriesEEG.appendData(DataPoint(time, testData), true, 10000)
                testData += 1.0
                time+=0.002
                if(testData >= 1023.0) testData = 0.0
                seriesEEG.appendData(DataPoint(time, testData), true, 10000)
                testData += 1.0
                time+=0.002
            }
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
