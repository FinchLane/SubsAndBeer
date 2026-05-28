package com.example.barbershop.ui.profile.editProfile

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.R
import com.example.barbershop.ui.components.ConfirmationMessage
import com.example.barbershop.ui.components.customComponent.AdaptiveIcon
import com.example.barbershop.ui.components.customComponent.toImageResourceString
import com.example.barbershop.ui.components.gallery.GalleryModal
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.utils.CropImage
import com.example.barbershop.viewmodel.galery.GalleryViewModel
import com.example.barbershop.viewmodel.profile.ProfileViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import timber.log.Timber
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val navigationEvent by profileViewModel.navigateTo.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            "editProfile" -> {
                navController.navigate("editProfile")
                profileViewModel.clearNavigationEvent()
            }
            "login" -> {
                navController.navigate("login")
                profileViewModel.clearNavigationEvent()
            }
            else -> Unit
        }
    }


    var showDatePicker by remember { mutableStateOf(false) }

    var showMoreBottomSheet by remember { mutableStateOf(false) }
    val sheetMoreState = rememberModalBottomSheetState()

    var showGalleryBottomSheet by remember { mutableStateOf(false) }
    val sheetGalleryState = rememberModalBottomSheetState()
    val context = LocalContext.current
    val viewModel = remember {GalleryViewModel(context)}

    val imageUri by profileViewModel.profileImage.collectAsState()

    val uCropLauncher = rememberLauncherForActivityResult(CropImage()) { uri ->
        profileViewModel.setProfileImage(uri.toString())

        uri?.apply {
            profileViewModel.uploadPhoto(
                context = context,
                uri = uri
            )
        }
    }

    val imageSelectorLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            uCropLauncher.launch(
                Pair(
                    first = uri,
                    second = Uri.fromFile(
                        File(context.cacheDir, "temp_image_file_${Date().time}")
                    )
                )
            )
        } else {
            Timber.w("Селектор изображений вернул null")
        }
    }

    if (showDatePicker) {
        DateBirthdayPicker(
            onDateSelected = { date ->
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
                val formattedDate = date?.let {
                    formatter.format(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate())
                } ?: "Не указано"
                profileViewModel.updateBirthday(formattedDate)
                showDatePicker = false
            },
            onDismiss = {showDatePicker = false}
        )
    }

    Scaffold(
        topBar = { AppBarBack(nav = {navController.popBackStack()}) },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ){ innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                UserImageCard(
                    image = imageUri,
                    onClick = {
                        imageSelectorLauncher.launch("image/*")
                    }
                )
                Spacer(Modifier.height(8.dp))
                UserDataCard(
                    title = "Личные данные",
                    data = listOf(
                        Triple(
                            "Имя, фамилия",
                            "${profileViewModel.firstNameUser} ${profileViewModel.lastNameUser}"
                        )
                        { navController.navigate("editName") },
                        Triple(
                            "Телефон",
                            "+" + profileViewModel.phoneNumberUser
                        )
                        { navController.navigate("editPhoneNumber") },
                        Triple(
                            "Почта",
                            profileViewModel.emailUser
                        )
                        { navController.navigate("editEmail") },
                        Triple(
                            "День рождения",
                            profileViewModel.birthdayUser
                        )
                        { showDatePicker = true }
                    )
                )
                Spacer(Modifier.height(8.dp))
                UserAddCard(
                    title = "Безопасность",
                    securityOptions = listOf(
                        Triple("Авторизованные устройства", null) { navController.navigate("authDevices") },
//                        Triple("Связанные аккаунты", null) { navController.navigate("relatedAccounts") },
//                        Triple("Вход по биометрии", "Без кода подтверждения") { },
//                        Triple("Двухфакторная аутентификация (2FA)", "Дополнительное подтверждение по почте") {},
//                        Triple("Вход по Telegram", "Подтверждение входа через телеграмм") {},
                        Triple("Другое", null) {showMoreBottomSheet = true}
                    )
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {profileViewModel.logout()},
                    colors = ButtonDefaults.buttonColors(Color(0xFF412a2b)),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Выйти из аккаунта",
                        color = Color(0xFFff5c52)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            if (showMoreBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showMoreBottomSheet = false
                    },
                    sheetState = sheetMoreState
                ) {
                    DeleteAccountModal(
                        {profileViewModel.showConfirmationMessage(true)},
                        {showMoreBottomSheet = false}
                    )
                }
            }

            if (showGalleryBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showGalleryBottomSheet = false
                    },
                    sheetState = sheetGalleryState
                ) {
                    GalleryModal(viewModel, { })
                }
            }

            if (profileViewModel.showConfirmationMessageDeleteAccount){
                ConfirmationMessage(
                    "Удаление аккаунта",
                    "Длиное описание про удаление аккаунта и персональных даных т.д т.п",
                    "Подтвердить",
                    {profileViewModel.deleteAccount()},
                    {profileViewModel.showConfirmationMessage(false)}
                )
            }

            LaunchedEffect(Unit) {
                viewModel.loadMediaFiles()
            }
        }
    }
}

//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//private fun EditProfilePreview() {
//    BarbershopTheme {
//        EditProfileScreen(navController = rememberNavController())
//    }
//}

@Composable
fun UserImageCard(image: String?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AdaptiveIcon(
                imageSrc = image ?: R.drawable.m0nesy_featured_image.toImageResourceString(),
                applyTint = false,
                contentScale = ContentScale.Crop,
                containerShape = RoundedCornerShape(10),
                iconSize = 160.dp,
                containerSize = 160.dp,
                onClick = {onClick()},
                modifier = Modifier.clip(RoundedCornerShape(25))
            )
            TextButton(
                onClick = {onClick()},
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = "Сменить фото",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
fun UserDataCard(
    title: String,
    data: List<Triple<String, String, (() -> Unit)?>>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            data.forEachIndexed { index, (title, value, onClick) ->
                if (index > 0) HorizontalDivider()
                UserIconRow(title, value, modifier.clickable(onClick != null ) { onClick?.invoke() })
            }
        }
    }
}

@Composable
fun UserAddCard(
    title: String,
    securityOptions: List<Triple<String, String?, (() -> Unit)?>>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
            securityOptions.forEachIndexed { index, (title, description, onClick) ->
                if (index > 0) HorizontalDivider()
                if (description == null) {
                    UserIconRow(title, modifier.clickable(onClick != null) { onClick?.invoke() })
                } else {
                    UserSwitchRow(title, description, onClick)
                }
            }
        }
    }
}

@Composable
fun UserIconRow(title: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = if (value != "" && value != " ") value else "Не указано",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
fun UserIconRow(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
fun UserSwitchRow(title: String, value: String, onToggle: (() -> Unit)? = null, modifier: Modifier = Modifier) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Switch(
            checked = false,
            onCheckedChange = { onToggle?.invoke() }
        )
    }
}

//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
//@Composable
//private fun UserImagePreview() {
//    BarbershopTheme {
//        UserImageCard(R.drawable.m0nesy_featured_image)
//    }
//}

//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
//@Composable
//private fun UserDataCardPreview() {
//    BarbershopTheme {
//        UserDataCard(
//            "Имя, фамилия", "Илья Осипов",
//            "Телефон", "+7 951 997-79-90",
//            "Почта","Не указано",
//            "День рождения", "Не указано"
//        )
//    }
//}
//
//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
//@Composable
//private fun UserAddCardPreview() {
//    BarbershopTheme {
//        UserAddCard()
//    }
//}