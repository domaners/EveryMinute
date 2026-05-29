package com.domaners.everyminute.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.domaners.everyminute.data.model.Team
import com.domaners.everyminute.data.repository.AuthRepository
import com.domaners.everyminute.data.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateTeamViewModel(
    private val teamRepository: TeamRepository = TeamRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTeamUiState())
    val uiState = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateIconUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(iconUri = uri)
    }

    fun addCoachByEmail(email: String) {
        viewModelScope.launch {
            val user = teamRepository.findUserByEmail(email)
            if (user != null) {
                val currentCoaches = _uiState.value.coaches.toMutableList()
                if (currentCoaches.none { it["id"] == user["id"] }) {
                    currentCoaches.add(user)
                    _uiState.value = _uiState.value.copy(coaches = currentCoaches)
                }
            }
        }
    }

    fun removeCoach(userId: String) {
        val currentCoaches = _uiState.value.coaches.filter { it["id"] != userId }
        _uiState.value = _uiState.value.copy(coaches = currentCoaches)
    }

    fun addParentByEmail(email: String) {
        viewModelScope.launch {
            val user = teamRepository.findUserByEmail(email)
            if (user != null) {
                val currentParents = _uiState.value.parents.toMutableList()
                if (currentParents.none { it["id"] == user["id"] }) {
                    currentParents.add(user)
                    _uiState.value = _uiState.value.copy(parents = currentParents)
                }
            }
        }
    }

    fun removeParent(userId: String) {
        val currentParents = _uiState.value.parents.filter { it["id"] != userId }
        _uiState.value = _uiState.value.copy(parents = currentParents)
    }

    fun saveTeam(onSuccess: () -> Unit) {
        val currentUserId = authRepository.currentUser.value?.uid ?: return
        viewModelScope.launch {
            val team = Team(
                name = _uiState.value.name,
                iconUrl = _uiState.value.iconUri?.toString(),
                coachIds = _uiState.value.coaches.mapNotNull { it["id"] } + currentUserId,
                parentIds = _uiState.value.parents.mapNotNull { it["id"] }
            )
            teamRepository.createTeam(team).onSuccess {
                onSuccess()
            }
        }
    }
}

data class CreateTeamUiState(
    val name: String = "",
    val iconUri: Uri? = null,
    val coaches: List<Map<String, String>> = emptyList(),
    val parents: List<Map<String, String>> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTeamScreen(
    viewModel: CreateTeamViewModel = viewModel(),
    onTeamCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddCoachDialog by remember { mutableStateOf(false) }
    var showAddParentDialog by remember { mutableStateOf(false) }
    var emailToAdd by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateIconUri(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Your Team") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("Team Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { imagePicker.launch("image/*") }
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.iconUri != null) {
                    AsyncImage(
                        model = uiState.iconUri,
                        contentDescription = "Team Icon",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Pick Icon",
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            }
            Text(
                text = "Tap to upload team icon",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Coaches", onAddClick = { showAddCoachDialog = true })
            PeopleList(people = uiState.coaches, onRemove = viewModel::removeCoach)

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(title = "Parents", onAddClick = { showAddParentDialog = true })
            PeopleList(people = uiState.parents, onRemove = viewModel::removeParent)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.saveTeam(onTeamCreated) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.name.isNotBlank()
            ) {
                Text("Save Team")
            }
        }
    }

    if (showAddCoachDialog || showAddParentDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddCoachDialog = false
                showAddParentDialog = false
                emailToAdd = ""
            },
            title = { Text(if (showAddCoachDialog) "Add Coach" else "Add Parent") },
            text = {
                OutlinedTextField(
                    value = emailToAdd,
                    onValueChange = { emailToAdd = it },
                    label = { Text("User Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (showAddCoachDialog) viewModel.addCoachByEmail(emailToAdd)
                    else viewModel.addParentByEmail(emailToAdd)
                    showAddCoachDialog = false
                    showAddParentDialog = false
                    emailToAdd = ""
                }) {
                    Text("Add")
                }
            }
        )
    }
}

@Composable
fun SectionHeader(title: String, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onAddClick) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun PeopleList(people: List<Map<String, String>>, onRemove: (String) -> Unit) {
    if (people.isEmpty()) {
        Text("No one added yet", style = MaterialTheme.typography.bodySmall)
    } else {
        Column {
            people.forEach { person ->
                ListItem(
                    headlineContent = { Text(person["name"] ?: "Unknown") },
                    supportingContent = { Text(person["id"] ?: "") },
                    trailingContent = {
                        IconButton(onClick = { onRemove(person["id"] ?: "") }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                )
            }
        }
    }
}
