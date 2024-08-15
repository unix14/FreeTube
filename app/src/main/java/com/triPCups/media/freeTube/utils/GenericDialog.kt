package com.triPCups.media.freeTube.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog


/**
 * Shows a generic dialog with custom actions and text.
 *
 * @param context The context to use for showing the dialog.
 * @param title The title of the dialog.
 * @param message The message of the dialog.
 * @param positiveButtonText The text for the positive button.
 * @param negativeButtonText The text for the negative button.
 * @param onPositiveAction The action to perform when the positive button is clicked.
 * @param onNegativeAction The action to perform when the negative button is clicked.
 */
fun <T> showGenericDialog(
    context: Context,
    title: String,
    message: String,
    positiveButtonText: String,
    negativeButtonText: String,
    onPositiveAction: (T) -> Unit,
    onNegativeAction: () -> Unit,
    data: T
) {
    val dialogBuilder = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonText) { _, _ ->
            onPositiveAction(data)
        }
        .setNegativeButton(negativeButtonText) { _, _ ->
            onNegativeAction()
        }

    val dialog = dialogBuilder.create()
    dialog.show()
}