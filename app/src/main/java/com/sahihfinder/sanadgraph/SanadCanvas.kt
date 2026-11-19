package com.sahihfinder.sanadgraph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp

@Composable
fun SanadTreeCanvas(
    nodes: List<NarratorNode>,
    selectedNode: NarratorNode?,
    onSelectNode: (NarratorNode) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember(nodes) { mutableFloatStateOf(1f) }
    var offset by remember(nodes) { mutableStateOf(Offset(60f, 60f)) }
    val textMeasurer = rememberTextMeasurer()

    val nodeWidth = 340f
    val nodeHeight = 115f
    val horizontalGap = 60f
    val verticalGap = 150f

    val positions = remember(nodes) {
        val map = mutableMapOf<String, Offset>()
        val levels = mutableMapOf<String, Int>()

        fun getDepth(node: NarratorNode): Int {
            levels[node.id]?.let { return it }
            val parent = nodes.find { it.id == node.parentId }
            val depth = if (parent == null) 0 else getDepth(parent) + 1
            levels[node.id] = depth
            return depth
        }

        nodes.forEach { getDepth(it) }
        val byLevel = nodes.groupBy { levels[it.id] ?: 0 }

        byLevel.forEach { (depth, rowNodes) ->
            val totalWidth = rowNodes.size * nodeWidth + (rowNodes.size - 1) * horizontalGap
            rowNodes.forEachIndexed { index, node ->
                val x = (index * (nodeWidth + horizontalGap)) - (totalWidth / 2f) + 500f
                val y = depth * (nodeHeight + verticalGap) + 40f
                map[node.id] = Offset(x, y)
            }
        }
        map
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF061810))
            .pointerInput(nodes) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.35f, 2.5f)
                    offset += pan
                }
            }
            .pointerInput(nodes, scale, offset) {
                detectTapGestures { tapOffset ->
                    val worldTap = (tapOffset - offset) / scale
                    val hit = nodes.firstOrNull { node ->
                        val pos = positions[node.id] ?: return@firstOrNull false
                        worldTap.x in pos.x..(pos.x + nodeWidth) &&
                            worldTap.y in pos.y..(pos.y + nodeHeight)
                    }
                    if (hit != null) {
                        onSelectNode(hit)
                    }
                }
            }
    ) {
        nodes.forEach { node ->
            val parentId = node.parentId ?: return@forEach
            val parentNode = nodes.find { it.id == parentId } ?: return@forEach
            val startPos = positions[parentId] ?: return@forEach
            val endPos = positions[node.id] ?: return@forEach

            val start = Offset(
                (startPos.x + nodeWidth / 2f) * scale + offset.x,
                (startPos.y + nodeHeight) * scale + offset.y
            )
            val end = Offset(
                (endPos.x + nodeWidth / 2f) * scale + offset.x,
                endPos.y * scale + offset.y
            )

            val gap = node.deathAH - parentNode.deathAH
            val isBroken = node.brokenLink || gap > 75 || gap < 0
            val wireColor = if (isBroken) Color(0xFFF87171) else Color(0xFF34D399)

            val path = Path().apply {
                moveTo(start.x, start.y)
                cubicTo(
                    start.x, (start.y + end.y) / 2f,
                    end.x, (start.y + end.y) / 2f,
                    end.x, end.y
                )
            }

            drawPath(
                path = path,
                color = wireColor,
                style = Stroke(
                    width = 5f * scale,
                    pathEffect = if (isBroken) PathEffect.dashPathEffect(floatArrayOf(18f, 12f)) else null
                )
            )
        }

        nodes.forEach { node ->
            val rawPos = positions[node.id] ?: return@forEach
            val topLeft = Offset(rawPos.x * scale + offset.x, rawPos.y * scale + offset.y)
            val boxSize = Size(nodeWidth * scale, nodeHeight * scale)
            val isSelected = selectedNode?.id == node.id

            drawRoundRect(
                color = if (node.grade == NarratorGrade.PROPHET) Color(0xFF2B2105) else Color(0xFF0F2E20),
                topLeft = topLeft,
                size = boxSize,
                cornerRadius = CornerRadius(20f * scale, 20f * scale)
            )

            drawRoundRect(
                color = if (isSelected) Color(0xFFFEF08A) else node.grade.color,
                topLeft = topLeft,
                size = boxSize,
                cornerRadius = CornerRadius(20f * scale, 20f * scale),
                style = Stroke(width = (if (isSelected) 7f else 4f) * scale)
            )

            if (scale >= 0.45f) {
                val labelLayout = textMeasurer.measure(
                    text = "${node.name}\n(${node.deathAH} AH)",
                    style = TextStyle(
                        color = if (node.grade == NarratorGrade.PROPHET) Color(0xFFFEF08A) else Color.White,
                        fontSize = (13f * scale).coerceAtLeast(8f).sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                drawText(
                    textLayoutResult = labelLayout,
                    topLeft = Offset(
                        topLeft.x + (boxSize.width - labelLayout.size.width) / 2f,
                        topLeft.y + (boxSize.height - labelLayout.size.height) / 2f
                    )
                )
            }
        }
    }
}
