package com.bitronics.bitronicsmobilebiosignals.presentation.featureECG

import android.annotation.SuppressLint
import android.graphics.Color
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
import java.math.RoundingMode
import java.util.*

@AndroidEntryPoint
class EcgFragment : Fragment() {

    private var _binding: FragmentEcgBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var seriesECG: LineGraphSeries<DataPoint>
    var time: Double = 0.0

    var timer_ECG = Timer()

    val vm: EcgViewModel by viewModels()


    @SuppressLint("SetTextI18n")
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
        seriesECG.color = Color.RED
        graphECG.titleTextSize = 25f
        graphECG.viewport.setMaxY(1024.0)
        graphECG.viewport.setMinY(0.0)
        graphECG.viewport.setMaxX(10.0)
        graphECG.viewport.isYAxisBoundsManual = true
        graphECG.viewport.isXAxisBoundsManual = true
        graphECG.viewport.isScrollable = true; // enables horizontal scrolling
        graphECG.viewport.isScalable = true; // enables horizontal zooming and scrolling
        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer_ECG.schedule(ShowData(), 0, 10)

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

        var countFilter = 0
        var sum = 0.0
        override fun run() {

            activity?.runOnUiThread {

                if (vm.isConnected() && vm.fetchModuleType() == 1) {
                    if (count == 3000) {
                        vm.fetchPulse(arrForProc)
                        vm.fetchAmpl(arrForProc)
                        count = 0
                    }
                    if(countFilter == 20){
                        sum /= 20
                        seriesECG.appendData(DataPoint(time, sum), true, 10000)
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