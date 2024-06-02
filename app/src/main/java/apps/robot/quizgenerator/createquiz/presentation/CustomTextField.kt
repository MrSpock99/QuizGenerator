@file:OptIn(ExperimentalMaterial3Api::class)

package apps.robot.quizgenerator.createquiz.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import apps.robot.quizgenerator.R

@Composable
fun CustomTextField(
    modifier: Modifier,
    label: String,
    onChange: (String) -> Unit,
    text: String,
    onDone: ((String) -> Unit)? = null,
) {
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = {
            onChange(it)
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
}

@Composable
fun CustomOutlineTextField(
    modifier: Modifier,
    label: String,
    onChange: (String) -> Unit,
    text: String,
    onDone: ((String) -> Unit)? = null
) {
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = {
            onChange(it)
        },
        label = { Text(label) },
        keyboardActions = KeyboardActions {
            onDone?.invoke(text)
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black
        ),
    )
}

@Composable
fun TextFieldWithBubble(
    modifier: Modifier,
    onChange: (String) -> Unit,
    text: String,
    answers: List<String>,
    label: String,
    onDone: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Row(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        LazyVerticalGrid(
            modifier = Modifier.wrapContentSize(),
            columns = GridCells.Adaptive(50.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(answers) { item ->
                Bubble(text = item, onDeleteClick = onDeleteClick)
            }
            item(
                span = { GridItemSpan(3) }
            ) {
                CustomOutlineTextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    label = label,
                    onChange = onChange,
                    text = text,
                    onDone = onDone
                )
            }
        }
    }
}

@Composable
private fun Bubble(text: String, onDeleteClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart).size(8.dp),
            onClick = {
                onDeleteClick(text)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = "Back button"
            )
        }
        Text(text = text)
    }
}