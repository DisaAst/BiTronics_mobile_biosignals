package com.bitronics.bitronicsmobilebiosignals.presentation.featurePPG

import android.annotation.SuppressLint
import android.graphics.Color
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
import java.math.RoundingMode
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

        val graph_PPG: GraphView = binding.PPGGraph
        seriesPPG = LineGraphSeries<DataPoint>()
        graph_PPG.addSeries(seriesPPG)
        seriesPPG.color = Color.BLUE
        graph_PPG.titleTextSize = 25f
        graph_PPG.viewport.setMaxY(1024.0)
        graph_PPG.viewport.setMinY(0.0)
        graph_PPG.viewport.setMaxX(10.0)
        graph_PPG.viewport.isYAxisBoundsManual = true
        graph_PPG.viewport.isXAxisBoundsManual = true
        graph_PPG.viewport.isScrollable = true // enables horizontal scrolling
        graph_PPG.viewport.isScalable = true // enables horizontal zooming and scrolling

        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer_PPG.schedule(ShowData(), 0, 10)


        vm.pulse.observe(viewLifecycleOwner) {
            binding.textPulse.text = "Пульс: ${it.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()}"
            binding.textRR.text = "RR-интервал: ${(60/it).toBigDecimal().setScale(1, RoundingMode.UP).toDouble()}"
        }

        vm.ampl.observe(viewLifecycleOwner){
            binding.textEBC.text = "EBC (расчетный цикл дыхания): $it"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer_PPG.cancel()
        _binding = null
    }

    inner class ShowData : TimerTask() {

        var count = 0
        var arrForProc = DoubleArray(3000)
        var countFilter = 0
        var sum = 0.0
        override fun run() {

            activity?.runOnUiThread {

                if (vm.isConnected() && vm.fetchModuleType() == 2) {
                    if (count == 3000) {
                        vm.fetchPulse(arrForProc)
                        vm.fetchAmpl(arrForProc)
                        count = 0
                    }
                    if(countFilter == 20){
                        sum /= 20
                        seriesPPG.appendData(DataPoint(time, sum), true, 10000)
                        sum = 0.0
                        countFilter = 0
                    }
                    val array: DoubleArray = vm.fetchData()
                    sum += array[0] + array[1] + array[2] + array[3] + array[4]
                    countFilter += 5
                    arrForProc[count] = array[0]
                    count += 1
                    time += 0.002
                    arrForProc[count] = array[1]
                    count += 1
                    time += 0.002
                    arrForProc[count] = array[2]
                    count += 1
                    time += 0.002
                    arrForProc[count] = array[3]
                    count += 1
                    time += 0.002
                    arrForProc[count] = array[4]
                    count += 1
                    time += 0.002
                }
            }
        }
    }
}

