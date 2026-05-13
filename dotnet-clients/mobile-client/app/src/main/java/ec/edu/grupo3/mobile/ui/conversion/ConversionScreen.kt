package ec.edu.grupo3.mobile.ui.conversion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversionScreen(
    username: String,
    onLogout: () -> Unit,
    viewModel: ConversionViewModel = viewModel()
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
    )
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("SOAP Hub", fontWeight = FontWeight.ExtraBold, color = Color(0xFFA855F7), fontSize = 20.sp)
                        Text("Usuario: $username", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color(0xFFEF4444))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                        Text("Categoría de Unidad", color = Color(0xFF94A3B8), fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                        
                        var expandedCat by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedCat,
                            onExpandedChange = { expandedCat = !expandedCat }
                        ) {
                            OutlinedTextField(
                                value = viewModel.categories.find { it.first == viewModel.selectedCategory }?.second ?: "",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6366F1), unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                                viewModel.categories.forEach { (catKey, catLabel) ->
                                    DropdownMenuItem(
                                        text = { Text(catLabel) },
                                        onClick = { viewModel.onCategoryChange(catKey); expandedCat = false }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            var expandedFrom by remember { mutableStateOf(false) }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Convertir desde", color = Color(0xFF94A3B8), fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                                ExposedDropdownMenuBox(expanded = expandedFrom, onExpandedChange = { expandedFrom = !expandedFrom }) {
                                    OutlinedTextField(
                                        value = viewModel.fromUnit, onValueChange = {}, readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrom) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, unfocusedBorderColor = Color(0xFF334155)),
                                        shape = RoundedCornerShape(12.dp), modifier = Modifier.menuAnchor().fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(expanded = expandedFrom, onDismissRequest = { expandedFrom = false }) {
                                        viewModel.availableUnits.forEach { u ->
                                            DropdownMenuItem(text = { Text(u) }, onClick = { viewModel.onFromUnitChange(u); expandedFrom = false })
                                        }
                                    }
                                }
                            }

                            var expandedTo by remember { mutableStateOf(false) }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Hacia destino", color = Color(0xFF94A3B8), fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                                ExposedDropdownMenuBox(expanded = expandedTo, onExpandedChange = { expandedTo = !expandedTo }) {
                                    OutlinedTextField(
                                        value = viewModel.toUnit, onValueChange = {}, readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, unfocusedBorderColor = Color(0xFF334155)),
                                        shape = RoundedCornerShape(12.dp), modifier = Modifier.menuAnchor().fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(expanded = expandedTo, onDismissRequest = { expandedTo = false }) {
                                        viewModel.availableUnits.forEach { u ->
                                            DropdownMenuItem(text = { Text(u) }, onClick = { viewModel.onToUnitChange(u); expandedTo = false })
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Valor numérico", color = Color(0xFF94A3B8), fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                        OutlinedTextField(
                            value = viewModel.inputValue,
                            onValueChange = viewModel::onInputValueChange,
                            placeholder = { Text("0.0", color = Color(0xFF475569)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1), unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = viewModel::convert,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            enabled = viewModel.uiState !is UiState.Loading
                        ) {
                            if (viewModel.uiState is UiState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("PROCESAR CONVERSIÓN", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                AnimatedVisibility(visible = viewModel.uiState is UiState.Success) {
                    val successState = viewModel.uiState as? UiState.Success
                    successState?.let { state ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF052E16).copy(alpha = 0.8f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("RESULTADO EXITOSO", color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${state.response.inputValue} ${state.response.fromUnit} =", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                Text("${state.response.resultValue} ${state.response.toUnit}", color = Color(0xFF22C55E), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = viewModel.uiState is UiState.Error) {
                    val errorState = viewModel.uiState as? UiState.Error
                    errorState?.let { state ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF450A0A).copy(alpha = 0.8f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Error en Operación", color = Color(0xFFF87171), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(state.message, color = Color(0xFFFCA5A5), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}