// File: app/src/main/java/com/group10/carservicebook/ui/HistoryFragment.kt
package com.group10.carservicebook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.group10.carservicebook.R

class HistoryFragment : Fragment() {
    private val viewModel: ServiceViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerHistory)

        // FIX: We now pass the click listener lambda to the adapter constructor.
        val adapter = ServiceAdapter { clickedRecord ->
            // 1. Tell ViewModel which record we selected so it can pre-fill the sheet
            viewModel.setServiceToEdit(clickedRecord)

            // 2. Open the BottomSheet
            val bottomSheet = AddServiceBottomSheet()
            bottomSheet.show(parentFragmentManager, "EditServiceSheet")
        }

        recyclerView.adapter = adapter

        viewModel.allServices.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }
}