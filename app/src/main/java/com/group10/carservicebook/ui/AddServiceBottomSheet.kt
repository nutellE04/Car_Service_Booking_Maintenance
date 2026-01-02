// File: app/src/main/java/com/group10/carservicebook/ui/AddServiceBottomSheet.kt
package com.group10.carservicebook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.group10.carservicebook.R
import com.group10.carservicebook.data.ServiceRecord

class AddServiceBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: ServiceViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_add_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI References
        val tvTitle = view.findViewById<TextView>(R.id.tvSheetTitle)
        val etType = view.findViewById<TextInputEditText>(R.id.etServiceType)
        val etMileage = view.findViewById<TextInputEditText>(R.id.etServiceMileage)
        val etCost = view.findViewById<TextInputEditText>(R.id.etServiceCost)
        val etNotes = view.findViewById<TextInputEditText>(R.id.etServiceNotes)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveService)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDeleteService)

        // CHECK: Are we editing an existing record?
        val recordToEdit = viewModel.serviceToEdit.value

        if (recordToEdit != null) {
            // EDIT MODE
            tvTitle.text = "Edit Service"
            btnSave.text = "Update Record"
            btnDelete.visibility = View.VISIBLE // Show delete button

            // Pre-fill fields
            etType.setText(recordToEdit.serviceType)
            etMileage.setText(recordToEdit.mileageAtService.toString())
            etCost.setText(recordToEdit.cost.toString())
            etNotes.setText(recordToEdit.notes)

            // Delete Logic
            btnDelete.setOnClickListener {
                viewModel.deleteService(recordToEdit)
                dismiss()
                Toast.makeText(requireContext(), "Record Deleted", Toast.LENGTH_SHORT).show()
            }
        }

        // Save/Update Logic
        btnSave.setOnClickListener {
            val type = etType.text.toString()
            val mileage = etMileage.text.toString().toIntOrNull()
            val cost = etCost.text.toString().toDoubleOrNull()
            val notes = etNotes.text.toString()

            if (type.isNotEmpty() && mileage != null && cost != null) {
                // Determine ID: 0 for new, existing ID for update
                val idToUse = recordToEdit?.id ?: 0
                val timestamp = recordToEdit?.date ?: System.currentTimeMillis()

                val newRecord = ServiceRecord(
                    id = idToUse,
                    serviceType = type,
                    mileageAtService = mileage,
                    cost = cost,
                    date = timestamp, // Keep original date if editing
                    notes = notes
                )

                if (idToUse == 0) {
                    viewModel.addService(newRecord)
                    Toast.makeText(requireContext(), "Service Added!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.updateService(newRecord)
                    Toast.makeText(requireContext(), "Service Updated!", Toast.LENGTH_SHORT).show()
                }
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}