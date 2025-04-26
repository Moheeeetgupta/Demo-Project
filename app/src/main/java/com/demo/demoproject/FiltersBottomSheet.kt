package com.demo.demoproject

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
    filterType: FilterType,
    onDismissRequest: () -> Unit,
    onApply: (List<String>) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(onDismissRequest = onDismissRequest,sheetState = bottomSheetState){
        val selectedItems = remember { mutableStateOf(emptyList<FilterItem>()) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(bottom = 16.dp)
            ) {
                items(getFilterList(filterType = filterType)) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val currentSelection = selectedItems.value
                                selectedItems.value =
                                    if (item.value in currentSelection.map { it.value }) {
                                        currentSelection - item
                                    } else {
                                        currentSelection + item
                                    }
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item in selectedItems.value,
                            onCheckedChange = { isChecked ->
                                val currentSelection = selectedItems.value
                                selectedItems.value = if (isChecked) {
                                    currentSelection + item
                                } else {
                                    currentSelection - item
                                }
                            }
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(text = item.value)
                    }
                }
            }

            Button(
                onClick = {scope.launch {
                    bottomSheetState.hide()
                }
                onApply(selectedItems.value.map { it.key })
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.apply))
            }
        }
    }

}

data class FilterItem(
    val key: String,
    val value: String
)

fun getFilterList(filterType: FilterType): List<FilterItem> {
    return when (filterType) {
        FilterType.Language -> listOf(
            FilterItem("en", "English"),
            FilterItem("es", "Spanish"),
            FilterItem("it", "Italian"),
            FilterItem("ja", "Japanese"),
            FilterItem("fr", "French"),
            FilterItem("bn", "Bengali"),
            FilterItem("zh", "Chinese"),
            FilterItem("hi", "Hindi"),
            FilterItem("ru", "Russian"),
            FilterItem("ko", "Korean")
        )
        FilterType.Vote -> listOf(
            FilterItem("1", "One"),
            FilterItem("2", "Two"),
            FilterItem("3", "Three"),
            FilterItem("4", "Four"),
            FilterItem("5", "Five"),
            FilterItem("6", "Six"),
            FilterItem("7", "Seven"),
            FilterItem("8", "Eight"),
            FilterItem("9", "Nine"),
            FilterItem("10", "Ten")
        )
    }
}
