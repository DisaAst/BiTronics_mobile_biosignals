package com.bitronics.bitronicsmobilebiosignals.presentation.featureGSR

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentGsrBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import java.math.RoundingMode

@AndroidEntryPoint
class GsrFragment : Fragment() {

    private var _binding: FragmentGsrBinding? = null
    private val binding get() = _binding!!

    lateinit var seriesGSR: LineGraphSeries<DataPoint>

    val vm: GsrViewModel by viewModels()

    lateinit var graph_GSR: GraphView

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
        graph_GSR.viewport.setMaxY(5.0)
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

        vm.runReading()

        vm.amplitude.observe(viewLifecycleOwner){
            binding.AmplitudeGSR.text = "Амплитуда: ${it.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()}"
        }

        vm.timeReaction.observe(viewLifecycleOwner){
            binding.ReactionGSR.text = "Время восстановления: $it"
        }

        vm.gsr_value.observe(viewLifecycleOwner){
            graph_GSR.viewport.setMaxY(it + 0.15)
            graph_GSR.viewport.setMinY(it - 0.3)
            seriesGSR.appendData(DataPoint(vm.time, it), true, 10000)
        }

        binding.NullKButton.setOnClickListener {
            vm.startCase()
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