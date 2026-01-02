// File: app/src/main/java/com/group10/carservicebook/ui/DashboardFragment.kt
package com.group10.carservicebook.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.group10.carservicebook.R

class DashboardFragment : Fragment() {

    // FIX: Changed from 'by activityViewModels()' to standard lazy ViewModelProvider
    // This avoids the 'Unresolved reference' error if fragment-ktx is missing
    private val viewModel: ServiceViewModel by lazy {
        ViewModelProvider(requireActivity())[ServiceViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvRemaining = view.findViewById<TextView>(R.id.tvRemainingKm)
        val progress = view.findViewById<ProgressBar>(R.id.progressBarService)
        val btnUpdateOdo = view.findViewById<MaterialButton>(R.id.btnUpdateOdometer)
        val fabAdd = view.findViewById<ExtendedFloatingActionButton>(R.id.fabAddService)

        // NEW: Reference to the new Odometer Bar
        val tvLastOdometer = view.findViewById<TextView>(R.id.tvLastOdometer)

        viewModel.currentOdometer.observe(viewLifecycleOwner) { updateUI(view) }
        viewModel.lastServiceMileage.observe(viewLifecycleOwner) { updateUI(view) }
        viewModel.serviceInterval.observe(viewLifecycleOwner) { updateUI(view) }

        fabAdd.setOnClickListener {
            viewModel.clearServiceToEdit()
            val bottomSheet = AddServiceBottomSheet()
            bottomSheet.show(parentFragmentManager, "AddServiceSheet")
        }

        btnUpdateOdo.setOnClickListener {
            showUpdateOdometerDialog()
        }
    }

    private fun updateUI(view: View) {
        val remaining = viewModel.calculateRemainingKm()
        val percent = viewModel.calculateProgress()

        // Get the current odometer value from ViewModel safely
        val currentOdo = viewModel.currentOdometer.value ?: 0

        view.findViewById<TextView>(R.id.tvRemainingKm).text = "$remaining km"
        view.findViewById<ProgressBar>(R.id.progressBarService).progress = percent

        // NEW: Update the Odometer Bar Text
        view.findViewById<TextView>(R.id.tvLastOdometer).text = "Current Odometer: $currentOdo km"

        val statusText = view.findViewById<TextView>(R.id.tvStatus)
        if (remaining < 500) {
            statusText.text = "Service Due Soon!"
            statusText.setTextColor(resources.getColor(R.color.danger_status, null))
        } else {
            statusText.text = "Vehicle Healthy"
            statusText.setTextColor(resources.getColor(R.color.white, null))
        }
    }

    private fun showUpdateOdometerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_odo, null)
        val input = dialogView.findViewById<TextInputEditText>(R.id.etOdometerInput)

        // Pre-fill with current value
        input.setText(viewModel.currentOdometer.value?.toString() ?: "")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update Odometer")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog: DialogInterface, which: Int ->
                val newKm = input.text.toString().toIntOrNull()
                if (newKm != null) {
                    viewModel.updateOdometer(newKm)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}