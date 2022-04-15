package com.rafo.composeexample.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.CircleCropTransformation
import com.rafo.composeexample.ui.animation.AnimatingFabContent
import com.rafo.composeexample.utils.Item
import com.rafo.composeexample.utils.getTheData

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun List() {
    val data = getTheData()
    val scrollState = rememberLazyListState()
    val header = remember { mutableStateOf(data[0].type) }
    val firstItemIndex = remember { mutableStateOf(0) }

    val dataList = mutableListOf<Item>()
    val groupHeader = data.groupBy { it.type }

    groupHeader.forEach { (header, items) ->
        dataList.add(Item(type = header))
        val groupSubHeader = items.groupBy { it.subType }
        groupSubHeader.forEach { (subheader, items2) ->
            dataList.add(Item(type = header, subType = subheader))
            dataList.addAll(items2)
        }
    }
    val mainGroup = data.groupBy { it.type }

    Column(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            Surface {
                val openDialog = remember { mutableStateOf(false) }
                val openImageDialog = remember { mutableStateOf(false) }
                val dialogItem = remember { mutableStateOf(Item()) }
                val dialogImageItem = remember { mutableStateOf("") }
                Header(header = header.value)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    state = scrollState
                ) {
                    mainGroup.forEach { (type, groupedData) ->
                        if (header.value != type) {
                            stickyHeader { Header(header = type) }
                        }
                        val subGroup = groupedData.groupBy { it.subType }
                        subGroup.forEach { (subtype, subGroupedData) ->
                            stickyHeader {
                                Header(subheader = "$subtype (${scrollState.firstVisibleItemIndex})")
                            }

                            items(subGroupedData) {
                                SimpleItem(item = it,
                                    itemClick = { item ->
                                        openDialog.value = true
                                        dialogItem.value = item
                                    }, imageClick = { item ->
                                        openImageDialog.value = true
                                        dialogImageItem.value = item.imageUrl
                                    })
                                if (scrollState.firstVisibleItemIndex < firstItemIndex.value) { // move to start
                                    if (scrollState.firstVisibleItemIndex > 0) {
                                        header.value =
                                            dataList[scrollState.firstVisibleItemIndex - 1].type
                                    }
                                } else if (scrollState.firstVisibleItemIndex > firstItemIndex.value) { // move to the end
                                    if (scrollState.firstVisibleItemIndex - firstItemIndex.value != 2) {
                                        header.value =
                                            dataList[scrollState.firstVisibleItemIndex].type
                                    }
                                }
                                firstItemIndex.value = scrollState.firstVisibleItemIndex
                            }
                        }
                    }
                }
                AlertDialog(item = dialogItem.value, openDialog = openDialog)
                ShowAlertDialog(
                    imageUrl = dialogImageItem.value,
                    isDialogOpen = openImageDialog
                )
            }
            MyFloatingActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                extended = scrollState.firstVisibleItemScrollOffset == 0
            )
        }
    }
}

@Composable
fun Header(header: String = "", subheader: String = "") {
    if (header.isNotEmpty()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shadowElevation = 5.dp
        ) {
            Text(
                text = header,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp, bottom = 2.dp)
            )
        }
    }
    if (subheader.isNotEmpty()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shadowElevation = 5.dp
        ) {
            Text(
                text = subheader,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp, bottom = 2.dp)
            )
        }
    }
}

@Composable
fun SimpleItem(item: Item, itemClick: (Item) -> Unit, imageClick: (Item) -> Unit) {
    Surface(
        shadowElevation = 8.dp,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .size(Size.ORIGINAL)
                    .transformations(CircleCropTransformation())
                    .build(),
            )
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(5.dp)
                    .clickable { imageClick.invoke(item) }
            )
            Column(modifier = Modifier
                .padding(5.dp)
                .clickable { itemClick.invoke(item) }) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = item.description,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AlertDialog(item: Item, openDialog: MutableState<Boolean>) {
    if (openDialog.value) {

        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = item.title)
            },
            text = {
                Text(item.description)
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text("Ok")
                }
            }
        )
    }
}

@Composable
fun ShowAlertDialog(imageUrl: String, isDialogOpen: MutableState<Boolean>) {

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build(),
    )

    if (isDialogOpen.value) {
        Dialog(onDismissRequest = { isDialogOpen.value = false }) {
            Surface(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .height(IntrinsicSize.Max)
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.padding(5.dp))

                    Image(
                        painter = painter,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    Button(
                        onClick = {
                            isDialogOpen.value = false
                        },
                        modifier = Modifier.padding(10.dp),
                        shape = RoundedCornerShape(5.dp),
                    ) {
                        Text(
                            text = "Close",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

}


@Composable
fun MyFloatingActionButton(
    modifier: Modifier = Modifier,
    extended: Boolean
) {
    FloatingActionButton(
        onClick = {},
        modifier = modifier
            .navigationBarsPadding()
            .padding(16.dp)
            .height(48.dp)
            .widthIn(min = 48.dp),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        AnimatingFabContent(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = null
                )
            },
            text = {
                Text(
                    text = "Send",
                )
            },
            extended = extended

        )
    }
}