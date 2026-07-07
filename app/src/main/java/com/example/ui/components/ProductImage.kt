package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.GiftProduct

@Composable
fun ProductImage(
    product: GiftProduct,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {
    if (!product.imageUrl.isNullOrEmpty()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(product.imageUrl)
                .crossfade(true)
                .placeholder(R.drawable.img_luxury_box_1783249841023)
                .error(R.drawable.img_luxury_box_1783249841023)
                .build(),
            contentDescription = contentDescription ?: product.title,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        val resId = if (product.imageResId != 0) product.imageResId else R.drawable.img_luxury_box_1783249841023
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription ?: product.title,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}
