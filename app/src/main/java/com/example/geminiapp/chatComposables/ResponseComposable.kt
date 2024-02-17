import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.launch

@Composable
fun ResponseComposable(response: List<String>) {
    val coroutineScope = rememberCoroutineScope()
    val transitionState = remember { MutableTransitionState(0) }
    transitionState.targetState = response.size

    LazyColumn {
        items(response) { chunk ->
            val transition = updateTransition(transitionState.targetState, label = "")
            val alpha = if (transitionState.targetState == response.size) 1f else 0f
            val color = lerp(Color.Transparent, Color.Unspecified, alpha)

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = color)) {
                        append(chunk)
                    }
                }
            )
        }
    }

    // Trigger the animation when a new chunk is added
    coroutineScope.launch {
        transitionState.targetState = response.size
    }
}