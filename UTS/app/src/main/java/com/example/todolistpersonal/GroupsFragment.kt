package com.example.todolistpersonal

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * GroupsFragment - Fragment untuk menampilkan groups/categories
 * User dapat memilih group atau membuat group baru
 */
class GroupsFragment : Fragment() {

    private lateinit var recyclerViewGroups: RecyclerView
    private lateinit var fabAddGroup: FloatingActionButton
    private lateinit var btnCreateGroup: MaterialButton
    
    private lateinit var groupsAdapter: GroupsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TaskManager.init(requireContext().applicationContext)

        recyclerViewGroups = view.findViewById(R.id.rv_groups)
        fabAddGroup = view.findViewById(R.id.fab_add_group)
        btnCreateGroup = view.findViewById(R.id.btn_create_group)

        // Setup FAB
        fabAddGroup.setOnClickListener {
            showCreateGroupDialog()
        }

        // Setup Create Group Button
        btnCreateGroup.setOnClickListener {
            showCreateGroupDialog()
        }

        // Setup RecyclerView
        setupGroupsRecyclerView()
    }

    private fun setupGroupsRecyclerView() {
        val groups = TaskManager.getAllCategories().filter { it.id != "cat_all" }
        groupsAdapter = GroupsAdapter(
            groups,
            onGroupClick = { category ->
                // When group clicked, navigate to TasksFragment with category filter
                val fragment = TasksFragment()
                val bundle = Bundle()
                bundle.putString("category_id", category.id)
                bundle.putString("category_name", category.name)
                fragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onGroupDelete = { category ->
                // Show confirmation dialog before delete
                UIHelper.showDeleteConfirmation(
                    requireContext(),
                    title = "Delete Group",
                    message = "Are you sure you want to delete \"${category.name}\"?\nAll tasks in this group will be moved to default category.",
                    onConfirm = {
                        try {
                            TaskManager.deleteCategory(category.id)
                            setupGroupsRecyclerView()
                            UIHelper.showToast(requireContext(), "Group \"${category.name}\" deleted successfully")
                        } catch (e: Exception) {
                            UIHelper.showErrorDialog(requireContext(), "Error", "Failed to delete group: ${e.message}")
                        }
                    }
                )
            }
        )

        recyclerViewGroups.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupsAdapter
        }
    }

    /**
     * Show dialog untuk create new group
     */
    private fun showCreateGroupDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.create_new_group))

        val input = EditText(requireContext())
        input.hint = getString(R.string.enter_group_name)
        input.setPadding(16, 16, 16, 16)
        builder.setView(input)

        builder.setPositiveButton(getString(R.string.create)) { _, _ ->
            val groupName = input.text.toString().trim()

            // HCI: Validate input dengan meaningful error
            val (isValid, errorMsg) = ValidationHelper.validateGroupName(groupName)
            if (!isValid) {
                UIHelper.showToast(requireContext(), errorMsg ?: "Invalid group name")
                return@setPositiveButton
            }

            try {
                val newGroup = Category(
                    id = TaskManager.generateCategoryId(),
                    name = groupName,
                    color = "#4da8a3"
                )
                TaskManager.addCategory(newGroup)
                setupGroupsRecyclerView()
                UIHelper.showToast(requireContext(), "Group \"$groupName\" created successfully")
            } catch (e: Exception) {
                UIHelper.showErrorDialog(requireContext(), "Error", "Failed to create group: ${e.message}")
            }
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onResume() {
        super.onResume()
        setupGroupsRecyclerView()
    }
}

/**
 * Adapter untuk menampilkan groups/categories
 */
class GroupsAdapter(
    private val groups: List<Category>,
    private val onGroupClick: (Category) -> Unit,
    private val onGroupDelete: (Category) -> Unit
) : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val groupNameView = itemView.findViewById<TextView>(R.id.tv_group_name)
        private val groupColorView = itemView.findViewById<View>(R.id.view_group_color)
        private val taskCountView = itemView.findViewById<TextView>(R.id.tv_task_count)
        private val deleteButton = itemView.findViewById<android.widget.ImageButton>(R.id.btn_delete_group)

        fun bind(category: Category) {
            groupNameView.text = category.name
            // Semua grup gunakan warna yang sama: primary_teal (#4DA8A3)
            groupColorView.setBackgroundColor(itemView.context.getColor(R.color.primary_teal))

            // Count tasks for this category
            val taskCount = TaskManager.getTasksByCategory(category.id).size
            taskCountView.text = itemView.context.getString(R.string.task_count_format, taskCount)

            itemView.setOnClickListener {
                onGroupClick(category)
            }

            // Delete button handler
            deleteButton.setOnClickListener {
                onGroupDelete(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount() = groups.size
}
