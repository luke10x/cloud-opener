package com.example.cloudopener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.cloudopener.databinding.FragmentScannerBinding
import org.openapitools.client.apis.DefaultApi
import org.openapitools.client.models.CloudlinkPatch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ScannerFragment : Fragment() {
    private lateinit var cam: CameraStuff

    private var _binding: FragmentScannerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun createToastCamEventHandler(activity: MainActivity): ScannerObserver = object : ScannerObserver {
        override fun onScanned(scannedValue: String) {
            activity.runOnUiThread {
                findNavController().popBackStack()
                val msg = "🚩 value- $scannedValue"
                System.out.println(msg)
            }

            val api = DefaultApi(Config.API_BASE_PATH)

            // Get scanned cloudlink
            val cloudlink = api.getCloudlink(scannedValue)

            // Create a new Exchange
            val exchange = api.createExchange()

            // Patch the Cloudlink with an Exchange
            val patch = CloudlinkPatch(exchange.handle)
            api.updateCloudlink(cloudlink.code, patch)

            activity.exchangeHandle = exchange.handle

            System.out.println("🌵 Exchange added to cloudlink, clients should stop polling the cloudlink")
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentScannerBinding.inflate(inflater, container, false)

        cam = CameraStuff(
            activity as MainActivity,
            _binding
        )
        cam.add(createToastCamEventHandler(activity as MainActivity))

        cam.setupControls()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_ListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cam.stop()
    }
}