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

    private val binding get() = _binding!!

    lateinit var seriesECG: LineGraphSeries<DataPoint>

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
        graphECG.viewport.setMaxY(5.0)
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

        vm.runReading()

        vm.ecgValue.observe(viewLifecycleOwner) {
            seriesECG.appendData(DataPoint(vm.time, it), true, 10000)
        }

        vm.stress.observe(viewLifecycleOwner) {
            if(it.isNaN()) binding.stressText.text = "Стресс: не определено"
            else binding.stressText.text = "Стресс: ${it}"
        }

        vm.pulse.observe(viewLifecycleOwner) {
            if(it < 50 || it.isNaN()) {
                binding.textPulse.text = "Пульс: -"
                binding.textRR.text = "RR-интервал: -"
                binding.textState.text = "Физиологическое состояние: не определено"
            }
            else {
                binding.textPulse.text =
                    "Пульс: ${it.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()}"
                binding.textRR.text = "RR-интервал: ${(60 / it).toBigDecimal().setScale(1, RoundingMode.UP).toDouble()}"
                if(it >= 50 && it < 80) binding.textState.text = "Физиологическое состояние: расслаблен"
                if(it >= 80 && it < 120) binding.textState.text = "Физиологическое состояние: активная деятельность"
                if(it > 120) binding.textState.text = "Физиологическое состояние: напряжён"
            }
        }


        vm.ampl.observe(viewLifecycleOwner){
            binding.textEBC.text = "EBC (расчетный цикл дыхания): ${it.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }
}