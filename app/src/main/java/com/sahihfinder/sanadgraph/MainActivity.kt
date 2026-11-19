package com.sahihfinder.sanadgraph

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SanadGraphApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SanadGraphApp() {
    var selectedChain by remember { mutableStateOf(HadithRepository.chains.first()) }
    var selectedNode by remember { mutableStateOf<NarratorNode?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val darkEmerald = Color(0xFF093621)
    val primaryEmerald = Color(0xFF0F5132)
    val accentGold = Color(0xFFD4AF37)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("☪ SanadGraph", color = accentGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("SahihFinder • Isnad Visualizer", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkEmerald)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                OutlinedButton(
                    onClick = { dropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(selectedChain.title, color = darkEmerald, fontWeight = FontWeight.Bold)
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    HadithRepository.chains.forEach { chain ->
                        DropdownMenuItem(
                            text = { Text(chain.title) },
                            onClick = {
                                selectedChain = chain
                                selectedNode = null
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Surface(
                color = if (selectedChain.isSahih) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (selectedChain.isSahih) "SAHIH (CONTINUOUS SANAD)" else "DA'IF / MUNQATI' (BROKEN CHAIN)",
                        color = if (selectedChain.isSahih) Color(0xFF166534) else Color(0xFF991B1B),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(selectedChain.text, fontSize = 12.sp, color = Color(0xFF1E293B))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                SanadTreeCanvas(
                    nodes = selectedChain.nodes,
                    selectedNode = selectedNode,
                    onSelectNode = { selectedNode = it }
                )
            }

            selectedNode?.let { node ->
                val teacher = selectedChain.nodes.find { it.id == node.parentId }
                val gap = if (teacher != null) node.deathAH - teacher.deathAH else 0
                val isBroken = node.brokenLink || gap > 75 || gap < 0

                Card(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = node.grade.color, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = node.grade.label,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Died: ${node.deathAH} AH", fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(node.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryEmerald)
                        Text(node.bio, fontSize = 13.sp, color = Color(0xFF334155))
                        if (teacher != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBroken) "⚠️ Disconnected ($gap yrs between deaths with ${teacher.name})"
                                else "✅ Connected ($gap yrs difference with teacher ${teacher.name})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBroken) Color(0xFFDC2626) else Color(0xFF166534)
                            )
                        }
                    }
                }
            }
        }
    }
}
