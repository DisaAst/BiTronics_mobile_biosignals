package com.bitronics.bitronicsmobilebiosignals.presentation.featurePPG

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentPpgBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PpgFragment : Fragment() {

    private var _binding: FragmentPpgBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var seriesPPG: LineGraphSeries<DataPoint>
    lateinit var seriesPulse: LineGraphSeries<DataPoint>

    var time: Double = 0.0

    var timer_PPG = Timer()

    val vm: PpgViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPpgBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val graph_PPG: GraphView = binding.PPGraph
        seriesPPG = LineGraphSeries<DataPoint>()
        graph_PPG.addSeries(seriesPPG)
        graph_PPG.title = "График фотоплетизмограммы"
        graph_PPG.titleTextSize = 25f
        graph_PPG.viewport.setMaxY(1024.0)
        graph_PPG.viewport.setMinY(0.0)
        graph_PPG.viewport.setMaxX(10.0)
        graph_PPG.viewport.isYAxisBoundsManual = true
        graph_PPG.viewport.isXAxisBoundsManual = true
        graph_PPG.viewport.isScrollable = true; // enables horizontal scrolling
        graph_PPG.viewport.isScalable = true; // enables horizontal zooming and scrolling


        val graphPulse: GraphView = binding.PulseGraph
        seriesPulse = LineGraphSeries<DataPoint>()
        graphPulse.addSeries(seriesPulse)
        graphPulse.title = "График пульса"
        graphPulse.titleTextSize = 25f
        graphPulse.viewport.setMaxY(200.0)
        graphPulse.viewport.setMinY(0.0)
        graphPulse.viewport.setMaxX(10.0)
        graphPulse.viewport.isYAxisBoundsManual = true
        graphPulse.viewport.isXAxisBoundsManual = true

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer_PPG.schedule(ShowData(), 0, 10)


        vm.pulse.observe(viewLifecycleOwner, {
            seriesPulse.appendData(
                DataPoint(time, it),
                true,
                10000
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer_PPG.cancel()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    inner class ShowData() : TimerTask() {

        var count = 0
        var arrForProc = DoubleArray(3000)

        override fun run() {

            activity?.runOnUiThread {

                if (vm.isConnected() && vm.fetchModuleType() == 2) {
                    if (count == 3000) {
                        vm.fetchPulse(arrForProc)
                        count = 0
                    }
                    val array: DoubleArray = vm.fetchData()
                    seriesPPG.appendData(DataPoint(time, array[0]), true, 10000)
                    arrForProc[count] = array[0]
                    count += 1
                    time += 0.002
                    seriesPPG.appendData(DataPoint(time, array[1]), true, 10000)
                    arrForProc[count] = array[1]
                    count += 1
                    time += 0.002
                    seriesPPG.appendData(DataPoint(time, array[2]), true, 10000)
                    arrForProc[count] = array[2]
                    count += 1
                    time += 0.002
                    seriesPPG.appendData(DataPoint(time, array[3]), true, 10000)
                    arrForProc[count] = array[3]
                    count += 1
                    time += 0.002
                    seriesPPG.appendData(DataPoint(time, array[4]), true, 10000)
                    arrForProc[count] = array[4]
                    count += 1
                    time += 0.002
                }
            }
        }
    }
}