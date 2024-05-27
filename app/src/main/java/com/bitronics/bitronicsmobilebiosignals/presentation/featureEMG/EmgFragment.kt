package com.bitronics.bitronicsmobilebiosignals.presentation.featureEMG

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentEmgBinding
import com.google.android.material.slider.Slider
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.ValueDependentColor
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import java.math.RoundingMode
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class EmgFragment : Fragment() {

    private var _binding: FragmentEmgBinding? = null

    private val binding get() = _binding!!

    lateinit var firstSeriesEMG: LineGraphSeries<DataPoint>
    lateinit var secondSeriesEMG: LineGraphSeries<DataPoint>
    lateinit var seriesAmplitudeEMG: BarGraphSeries<DataPoint>
    val vm: EmgViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEmgBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val graph_EMG: GraphView = binding.EMGGraph
        firstSeriesEMG = LineGraphSeries<DataPoint>()
        secondSeriesEMG = LineGraphSeries<DataPoint>()
        graph_EMG.addSeries(firstSeriesEMG)
        graph_EMG.secondScale.addSeries(secondSeriesEMG)
        graph_EMG.titleTextSize = 25f
        graph_EMG.viewport.setMaxY(5.0)
        graph_EMG.viewport.setMinY(0.0)
        graph_EMG.viewport.setMaxX(10.0)
        graph_EMG.viewport.isYAxisBoundsManual = true
        graph_EMG.viewport.isXAxisBoundsManual = true
        graph_EMG.viewport.isScrollable = true; // enables horizontal scrolling
        graph_EMG.viewport.isScalable = true; // enables horizontal zooming and scrolling
        firstSeriesEMG.color = Color.GREEN

        val graphEMGAmplitude: GraphView = binding.amplitudeEmgGraph

        seriesAmplitudeEMG = BarGraphSeries(
            arrayOf(
                DataPoint(0.0, 0.0),
                DataPoint(1.0, 0.0),
                DataPoint(2.0, 0.0),
                DataPoint(3.0, 0.0),
                DataPoint(4.0, 0.0)
            )
        )

        graphEMGAmplitude.addSeries(seriesAmplitudeEMG)
        graphEMGAmplitude.titleTextSize = 25f
        graphEMGAmplitude.viewport.setMaxY(5.0)
        graphEMGAmplitude.viewport.setMinY(0.0)
        graphEMGAmplitude.viewport.setMaxX(3.0)
        graphEMGAmplitude.viewport.isYAxisBoundsManual = true
        graphEMGAmplitude.viewport.isXAxisBoundsManual = true
        seriesAmplitudeEMG.spacing = 90
        seriesAmplitudeEMG.valueDependentColor = ValueDependentColor<DataPoint> { data ->
            Color.rgb(
                data.x.toInt() * 255 / 4,
                abs(data.y * 255 / 6).toInt(),
                100
            )
        }
        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.runReading()

        vm.emgValue.observe(viewLifecycleOwner) {
            firstSeriesEMG.appendData(DataPoint(vm.time, it[0]), true, 10000)
            secondSeriesEMG.appendData(DataPoint(vm.time, it[1]), true, 10000)
        }

        vm.ampl.observe(viewLifecycleOwner) {
            seriesAmplitudeEMG.resetData(arrayOf(DataPoint(1.0, it[0]), DataPoint(2.0, it[1])))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vm.closeReading()
        _binding = null

    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }
}
