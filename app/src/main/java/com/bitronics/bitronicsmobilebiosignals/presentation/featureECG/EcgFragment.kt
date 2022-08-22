package com.bitronics.bitronicsmobilebiosignals.presentation.featureECG

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentEcgBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EcgFragment : Fragment() {

    private var _binding: FragmentEcgBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var seriesECG: LineGraphSeries<DataPoint>
    lateinit var seriesPulse: LineGraphSeries<DataPoint>
    var time: Double = 0.0

    var timer_ECG = Timer()

    val vm: EcgViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEcgBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val graphECG: GraphView = binding.ECGGraph
        seriesECG = LineGraphSeries<DataPoint>()
        graphECG.addSeries(seriesECG)
        graphECG.title = "График электрической активности сердца"
        graphECG.titleTextSize = 25f
        graphECG.viewport.setMaxY(1024.0)
        graphECG.viewport.setMinY(0.0)
        graphECG.viewport.setMaxX(10.0)
        graphECG.viewport.isYAxisBoundsManual = true
        graphECG.viewport.isXAxisBoundsManual = true

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
        timer_ECG.schedule(ShowData(), 0, 10)

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
        _binding = null
        timer_ECG.cancel()

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

                if (vm.isConnected() && vm.fetchModuleType() == 1) {
                    if (count == 3000) {
                        vm.fetchPulse(arrForProc)
                        count = 0
                    }
                    val array: DoubleArray = vm.fetchData()
                    seriesECG.appendData(DataPoint(time, array[0]), true, 10000)
                    arrForProc[count] = array[0]
                    count += 1
                    time += 0.002
                    seriesECG.appendData(DataPoint(time, array[1]), true, 10000)
                    arrForProc[count] = array[1]
                    count += 1
                    time += 0.002
                    seriesECG.appendData(DataPoint(time, array[2]), true, 10000)
                    arrForProc[count] = array[2]
                    count += 1
                    time += 0.002
                    seriesECG.appendData(DataPoint(time, array[3]), true, 10000)
                    arrForProc[count] = array[3]
                    count += 1
                    time += 0.002
                    seriesECG.appendData(DataPoint(time, array[4]), true, 10000)
                    arrForProc[count] = array[4]
                    count += 1
                    time += 0.002
                }
            }
        }
    }
}