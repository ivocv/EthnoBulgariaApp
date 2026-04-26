package com.etnobulgaria.app.ui

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.etnobulgaria.app.R

@Composable
internal fun DiaryCameraCaptureDialog(
    onDismiss: () -> Unit,
    onCaptured: (String) -> Unit,
    onError: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var isCameraReady by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
    var selectedLensFacing by remember {
        mutableStateOf(CameraSelector.LENS_FACING_BACK)
    }

    DisposableEffect(lifecycleOwner, selectedLensFacing) {
        val executor = ContextCompat.getMainExecutor(context)
        val bindCamera = Runnable {
            runCatching {
                val cameraProvider = cameraProviderFuture.get()
                val selector = selectCamera(cameraProvider, selectedLensFacing)
                if (selector == null) {
                    onError(context.getString(R.string.diary_capture_failed))
                    return@Runnable
                }

                isCameraReady = false
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
                isCameraReady = true
            }.onFailure {
                onError(context.getString(R.string.diary_capture_failed))
            }
        }

        cameraProviderFuture.addListener(bindCamera, executor)

        onDispose {
            runCatching {
                cameraProviderFuture.get().unbindAll()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.diary_camera_title),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = stringResource(R.string.diary_camera_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(460.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(24.dp),
                        ),
                ) {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.fillMaxSize(),
                    )

                    if (!isCameraReady) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(44.dp),
                        )
                    }
                }

                FilledTonalButton(
                    onClick = {
                        selectedLensFacing = if (selectedLensFacing == CameraSelector.LENS_FACING_FRONT) {
                            CameraSelector.LENS_FACING_BACK
                        } else {
                            CameraSelector.LENS_FACING_FRONT
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.diary_action_switch_camera))
                }

                FilledTonalButton(
                    onClick = {
                        if (isCapturing || !isCameraReady) return@FilledTonalButton
                        isCapturing = true
                        val outputFile = createDiaryImageFile(context)
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

                        imageCapture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    isCapturing = false
                                    onCaptured(outputFile.absolutePath)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    outputFile.delete()
                                    isCapturing = false
                                    onError(context.getString(R.string.diary_capture_failed))
                                }
                            },
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isCameraReady && !isCapturing,
                ) {
                    Text(
                        text = if (isCapturing) {
                            stringResource(R.string.diary_camera_saving)
                        } else {
                            stringResource(R.string.diary_action_capture)
                        },
                    )
                }

                FilledTonalButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.action_close))
                }
            }
        }
    }
}

private fun selectCamera(
    cameraProvider: ProcessCameraProvider,
    preferredLensFacing: Int,
): CameraSelector? {
    val preferredSelector = CameraSelector.Builder()
        .requireLensFacing(preferredLensFacing)
        .build()
    if (cameraProvider.hasCamera(preferredSelector)) {
        return preferredSelector
    }

    val fallbackLensFacing = if (preferredLensFacing == CameraSelector.LENS_FACING_FRONT) {
        CameraSelector.LENS_FACING_BACK
    } else {
        CameraSelector.LENS_FACING_FRONT
    }
    val fallbackSelector = CameraSelector.Builder()
        .requireLensFacing(fallbackLensFacing)
        .build()
    return fallbackSelector.takeIf { cameraProvider.hasCamera(it) }
}
