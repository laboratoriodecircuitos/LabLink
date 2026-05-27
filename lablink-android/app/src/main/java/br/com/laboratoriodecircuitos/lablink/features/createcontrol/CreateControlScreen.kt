package br.com.laboratoriodecircuitos.lablink.features.createcontrol

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlType
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkDrawer
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkTopAppBar
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

private val BgDeep = Color(0xFF000000)
private val CardDark = Color(0xFF151515)
private val AccentYellow = Color(0xFFFFE382)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentGreen = Color(0xFFC4FA8C)
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)
private val BorderSoft = Color.White.copy(alpha = 0.06f)

private enum class CreateControlStep {
    ChooseType,
    ChooseQuantity,
    ConfigureControls,
}

@Composable
fun CreateControlScreen(
    isBluetoothConnected: Boolean,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenControls: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var drawerOpen by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(CreateControlStep.ChooseType) }
    var selectedType by remember { mutableStateOf<ControlType?>(null) }
    var quantity by remember { mutableIntStateOf(1) }
    var controlNames by remember { mutableStateOf(List(8) { "" }) }
    var controlPins by remember { mutableStateOf(List(8) { "" }) }

    fun updateName(index: Int, value: String) {
        controlNames = controlNames.toMutableList().also {
            it[index] = value
        }
    }

    fun updatePin(index: Int, value: String) {
        controlPins = controlPins.toMutableList().also {
            it[index] = value.trim().take(4)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgDeep,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDeep),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = 112.dp, bottom = 40.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    when (currentStep) {
                        CreateControlStep.ChooseType -> {
                            HeaderSection(
                                title = "Que controle você quer criar?",
                                description = "Escolha o tipo de controle que será usado no seu projeto Arduino.",
                            )

                            ControlType.values().forEach { type ->
                                ControlTypeCard(
                                    type = type,
                                    selected = selectedType == type,
                                    onClick = { selectedType = type },
                                )
                            }

                            ContinueCard(
                                enabled = selectedType != null,
                                title = if (selectedType == null) {
                                    "Selecione um tipo para continuar"
                                } else {
                                    "Continuar"
                                },
                                description = if (selectedType == null) {
                                    "Depois disso, o LabLink vai perguntar a quantidade de controles e os pinos usados."
                                } else {
                                    "Definir quantos controles ${selectedType?.displayName} serão criados."
                                },
                                onClick = {
                                    if (selectedType != null) {
                                        currentStep = CreateControlStep.ChooseQuantity
                                    }
                                },
                            )
                        }

                        CreateControlStep.ChooseQuantity -> {
                            val type = selectedType

                            HeaderSection(
                                title = "Quantos controles?",
                                description = if (type == null) {
                                    "Defina a quantidade de controles que deseja criar."
                                } else {
                                    "Escolha quantos controles ${type.displayName} você quer adicionar ao projeto."
                                },
                            )

                            SelectedTypeSummary(
                                type = type,
                                label = "Tipo selecionado",
                                onBack = {
                                    currentStep = CreateControlStep.ChooseType
                                },
                            )

                            QuantitySelector(
                                quantity = quantity,
                                onDecrease = {
                                    if (quantity > 1) quantity--
                                },
                                onIncrease = {
                                    if (quantity < 8) quantity++
                                },
                            )

                            ContinueCard(
                                enabled = true,
                                title = "Continuar",
                                description = "Agora vamos configurar nome e pino de cada controle.",
                                onClick = {
                                    currentStep = CreateControlStep.ConfigureControls
                                },
                            )
                        }

                        CreateControlStep.ConfigureControls -> {
                            val type = selectedType
                            val allPinsFilled = controlPins.take(quantity).all { it.isNotBlank() }

                            HeaderSection(
                                title = "Configure os controles",
                                description = "Dê um nome opcional e informe o pino usado em cada controle.",
                            )

                            SelectedTypeSummary(
                                type = type,
                                label = "$quantity controle${if (quantity > 1) "s" else ""}",
                                onBack = {
                                    currentStep = CreateControlStep.ChooseQuantity
                                },
                            )

                            repeat(quantity) { index ->
                                ControlConfigCard(
                                    index = index,
                                    name = controlNames[index],
                                    pin = controlPins[index],
                                    type = type,
                                    onNameChange = { updateName(index, it) },
                                    onPinChange = { updatePin(index, it) },
                                )
                            }

                            ContinueCard(
                                enabled = allPinsFilled,
                                title = if (allPinsFilled) "Salvar controles" else "Informe todos os pinos",
                                description = if (allPinsFilled) {
                                    "Na próxima etapa, o LabLink vai salvar e mostrar esses controles na tela principal."
                                } else {
                                    "Cada controle precisa ter um pino definido antes de continuar."
                                },
                                onClick = {
                                    // Próxima etapa: salvar controles em memória/local.
                                },
                            )
                        }
                    }
                }
            }

            LabLinkTopAppBar(
                title = "Criar controle",
                isBluetoothConnected = isBluetoothConnected,
                onOpenDrawer = { drawerOpen = true },
            )

            AnimatedVisibility(
                visible = drawerOpen,
                enter = fadeIn(animationSpec = tween(180)) + slideInHorizontally(
                    animationSpec = tween(260),
                    initialOffsetX = { -it },
                ),
                exit = fadeOut(animationSpec = tween(160)) + slideOutHorizontally(
                    animationSpec = tween(220),
                    targetOffsetX = { -it },
                ),
            ) {
                LabLinkDrawer(
                    onClose = { drawerOpen = false },
                    onOpenHome = {
                        drawerOpen = false
                        onOpenHome()
                    },
                    onOpenBluetooth = {
                        drawerOpen = false
                        onOpenConnection()
                    },
                    onOpenControls = {
                        drawerOpen = false
                        onOpenControls()
                    },
                    onOpenTerminal = {
                        drawerOpen = false
                        onOpenTerminal()
                    },
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    title: String,
    description: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
    ) {
        Text(
            text = title,
            color = WhiteSoft,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.4).sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            color = WhiteSoft.copy(alpha = 0.78f),
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )
    }
}

@Composable
private fun ControlTypeCard(
    type: ControlType,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val accent = type.accentColor()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (selected) accent else CardDark,
                shape = RoundedCornerShape(24.dp),
            )
            .border(
                width = 1.dp,
                color = if (selected) accent.copy(alpha = 0.75f) else BorderSoft,
                shape = RoundedCornerShape(24.dp),
            )
            .clickable { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = if (selected) Color.Black.copy(alpha = 0.10f) else accent.copy(alpha = 0.16f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = type.icon(),
                contentDescription = null,
                tint = if (selected) Color.Black else accent,
                modifier = Modifier.size(25.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = type.displayName,
                color = if (selected) Color.Black else WhiteSoft,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = type.description,
                color = if (selected) Color.Black.copy(alpha = 0.68f) else TextDim,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }

        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun SelectedTypeSummary(
    type: ControlType?,
    label: String,
    onBack: () -> Unit,
) {
    if (type == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(22.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(22.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(type.accentColor().copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = type.icon(),
                contentDescription = null,
                tint = type.accentColor(),
                modifier = Modifier.size(23.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = type.displayName,
                color = WhiteSoft,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = label,
                color = TextDim,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
                tint = WhiteSoft,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(30.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(30.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Quantidade",
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            QuantityButton(
                icon = Icons.Rounded.Remove,
                enabled = quantity > 1,
                onClick = onDecrease,
            )

            Text(
                text = quantity.toString(),
                color = WhiteSoft,
                fontSize = 64.sp,
                lineHeight = 70.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-1.2).sp,
            )

            QuantityButton(
                icon = Icons.Rounded.Add,
                enabled = quantity < 8,
                onClick = onIncrease,
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Máximo recomendado nesta versão: 8 controles.",
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun QuantityButton(
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(58.dp)
            .alpha(if (enabled) 1f else 0.35f)
            .background(
                color = if (enabled) AccentPurple else Color.White.copy(alpha = 0.05f),
                shape = CircleShape,
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) Color.Black else TextDim,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun ControlConfigCard(
    index: Int,
    name: String,
    pin: String,
    type: ControlType?,
    onNameChange: (String) -> Unit,
    onPinChange: (String) -> Unit,
) {
    val defaultName = when (type) {
        ControlType.DigitalToggle -> "Liga / Desliga ${index + 1}"
        ControlType.PwmSlider -> "Slider PWM ${index + 1}"
        ControlType.ServoSlider -> "Servo ${index + 1}"
        ControlType.PulseButton -> "Pulso ${index + 1}"
        ControlType.AnalogRead -> "Leitura ${index + 1}"
        null -> "Controle ${index + 1}"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = if (pin.isNotBlank()) AccentGreen.copy(alpha = 0.45f) else BorderSoft,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "Controle ${index + 1}",
            color = WhiteSoft,
            fontSize = 18.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.SemiBold,
        )

        LabTextField(
            label = "Nome opcional",
            value = name,
            placeholder = defaultName,
            keyboardType = KeyboardType.Text,
            onValueChange = onNameChange,
        )

        LabTextField(
            label = "Pino",
            value = pin,
            placeholder = when (type) {
                ControlType.AnalogRead -> "A0"
                else -> "13"
            },
            keyboardType = KeyboardType.Text,
            monospace = true,
            onValueChange = onPinChange,
        )
    }
}

@Composable
private fun LabTextField(
    label: String,
    value: String,
    placeholder: String,
    keyboardType: KeyboardType,
    onValueChange: (String) -> Unit,
    monospace: Boolean = false,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            color = TextDim,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = WhiteSoft,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = if (monospace) FontFamily.Monospace else FontFamily.Default,
            ),
            cursorBrush = SolidColor(AccentGreen),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                keyboardType = keyboardType,
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .border(1.dp, BorderSoft, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 15.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = placeholder,
                            color = TextDim.copy(alpha = 0.75f),
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = if (monospace) FontFamily.Monospace else FontFamily.Default,
                        )
                    }

                    innerTextField()
                }
            },
        )
    }
}

@Composable
private fun ContinueCard(
    enabled: Boolean,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.52f)
            .background(
                color = if (enabled) AccentGreen else CardDark,
                shape = RoundedCornerShape(24.dp),
            )
            .border(
                width = 1.dp,
                color = if (enabled) AccentGreen.copy(alpha = 0.75f) else BorderSoft,
                shape = RoundedCornerShape(24.dp),
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = if (enabled) Color.Black.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.04f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = if (enabled) Color.Black else TextDim,
                modifier = Modifier.size(22.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                color = if (enabled) Color.Black else WhiteSoft,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = description,
                color = if (enabled) Color.Black.copy(alpha = 0.68f) else TextDim,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

private fun ControlType.icon(): ImageVector {
    return when (this) {
        ControlType.DigitalToggle -> Icons.Rounded.PowerSettingsNew
        ControlType.PwmSlider -> Icons.Rounded.Tune
        ControlType.ServoSlider -> Icons.Rounded.Speed
        ControlType.PulseButton -> Icons.Rounded.Bolt
        ControlType.AnalogRead -> Icons.Rounded.GraphicEq
    }
}

private fun ControlType.accentColor(): Color {
    return when (this) {
        ControlType.DigitalToggle -> AccentYellow
        ControlType.PwmSlider -> AccentPurple
        ControlType.ServoSlider -> AccentGreen
        ControlType.PulseButton -> AccentYellow
        ControlType.AnalogRead -> AccentPurple
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateControlScreenPreview() {
    LabLinkTheme {
        CreateControlScreen(
            isBluetoothConnected = true,
            onOpenHome = {},
            onOpenConnection = {},
            onOpenTerminal = {},
            onOpenControls = {},
        )
    }
}
