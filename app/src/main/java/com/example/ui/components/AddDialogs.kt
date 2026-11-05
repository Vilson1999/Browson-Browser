package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun AddSpeedDialDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, url: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.Speed,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = stringResource(R.string.add_speed_dial),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.dialog_title)) },
                    placeholder = { Text("e.g. Google") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Title, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_speed_dial_title_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text(stringResource(R.string.dialog_url)) },
                    placeholder = { Text("e.g. google.com") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Link, contentDescription = null)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_speed_dial_url_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, url) },
                enabled = title.isNotBlank() && url.isNotBlank(),
                modifier = Modifier.testTag("dialog_speed_dial_save_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun AddBookmarkDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, url: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.Bookmark,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        title = {
            Text(
                text = stringResource(R.string.add_bookmark),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.dialog_title)) },
                    placeholder = { Text("e.g. Android Developers") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Title, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_bookmark_title_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text(stringResource(R.string.dialog_url)) },
                    placeholder = { Text("e.g. developer.android.com") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Link, contentDescription = null)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_bookmark_url_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, url) },
                enabled = title.isNotBlank() && url.isNotBlank(),
                modifier = Modifier.testTag("dialog_bookmark_save_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun EditSpeedDialDialog(
    item: com.example.model.SpeedDialItem,
    onDismiss: () -> Unit,
    onConfirm: (name: String, website: String) -> Unit
) {
    var name by remember { mutableStateOf(item.title) }
    var website by remember { mutableStateOf(item.url) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = stringResource(R.string.edit_speed_dial),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. Name box
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name_label)) },
                    placeholder = { Text("Enter name") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Title, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_edit_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Website box
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text(stringResource(R.string.website_label)) },
                    placeholder = { Text("https://...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Link, contentDescription = null)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_edit_website_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, website) },
                enabled = name.isNotBlank() && website.isNotBlank(),
                modifier = Modifier.testTag("dialog_edit_save_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun EditBookmarkDialog(
    item: com.example.model.BookmarkItem,
    onDismiss: () -> Unit,
    onConfirm: (name: String, website: String) -> Unit
) {
    var name by remember { mutableStateOf(item.title) }
    var website by remember { mutableStateOf(item.url) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        title = {
            Text(
                text = stringResource(R.string.edit_bookmark),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. Name box
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name_label)) },
                    placeholder = { Text("Enter name") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Title, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_edit_bookmark_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Website box
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text(stringResource(R.string.website_label)) },
                    placeholder = { Text("https://...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Link, contentDescription = null)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_edit_bookmark_website_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, website) },
                enabled = name.isNotBlank() && website.isNotBlank(),
                modifier = Modifier.testTag("dialog_edit_bookmark_save_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
