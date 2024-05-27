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

    private val binding get() = _binding!!

    lateinit var seriesPPG: LineGraphSeries<DataPoint>

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
        graph_PPG.viewport.setMaxY(5.0)
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
        vm.runReading()

        vm.ppgValue.observe(viewLifecycleOwner) {
            seriesPPG.appendData(DataPoint(vm.time, it), true, 10000)
        }

        vm.pulse.observe(viewLifecycleOwner) {
            if (it < 50 || it.isNaN()) {
                binding.textPulse.text = "Пульс: -"
                binding.textRR.text = "RR-интервал: -"
            } else {
                binding.textPulse.text =
                    "Пульс: ${it.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()}"
                binding.textRR.text = "RR-интервал: ${
                    (60 / it).toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                }"
            }
        }

        vm.ampl.observe(viewLifecycleOwner) {
            binding.textEBC.text = "EBC (расчетный цикл дыхания): ${
                it.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
            }"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

