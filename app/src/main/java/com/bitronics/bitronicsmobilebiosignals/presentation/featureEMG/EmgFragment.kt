package com.bitronics.bitronicsmobilebiosignals.presentation.featureEMG

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentEmgBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EmgFragment : Fragment() {

    private var _binding: FragmentEmgBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var seriesEMG: LineGraphSeries<DataPoint>
    lateinit var seriesAmplitudeEMG: LineGraphSeries<DataPoint>

    var time: Double = 0.0

    var timer_EMG = Timer()

    val vm: EmgViewModel by viewModels()


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
        graph_EMG.title = "График электрической активности мышц"
        graph_EMG.titleTextSize = 25f
        graph_EMG.viewport.setMaxY(1024.0)
        graph_EMG.viewport.setMinY(0.0)
        graph_EMG.viewport.setMaxX(10.0)
        graph_EMG.viewport.setYAxisBoundsManual(true)
        graph_EMG.viewport.setXAxisBoundsManual(true)

        val graphEMGAmplitude: GraphView = binding.amplitudeEmgGraph
        seriesAmplitudeEMG = LineGraphSeries<DataPoint>()
        graphEMGAmplitude.addSeries(seriesAmplitudeEMG)
        graphEMGAmplitude.title = "График амплитуды электрической активности мышц"
        graphEMGAmplitude.titleTextSize = 25f
        graphEMGAmplitude.viewport.setMaxY(1024.0)
        graphEMGAmplitude.viewport.setMinY(0.0)
        graphEMGAmplitude.viewport.setMaxX(10.0)
        graphEMGAmplitude.viewport.isYAxisBoundsManual = true
        graphEMGAmplitude.viewport.isXAxisBoundsManual = true

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer_EMG.schedule(ShowData(), 0, 10)


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
                        seriesAmplitudeEMG.appendData(
                            DataPoint(
                                time,
                                vm.fetchAmplitudeEMG(arrForProc)
                            ), true, 10000
                        )
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