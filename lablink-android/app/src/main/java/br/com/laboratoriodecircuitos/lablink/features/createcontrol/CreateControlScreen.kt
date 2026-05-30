package br.com.laboratoriodecircuitos.lablink.features.createcontrol

import java.util.UUID

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material.icons.rounded.Memory
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
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardPin
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardPinValidator
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfile
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfiles
import br.com.laboratoriodecircuitos.lablink.core.boards.PinValidationResult
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlType
import br.com.laboratoriodecircuitos.lablink.core.controls.LabLinkControl
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkDrawer
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkTopAppBar
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

private val BgDeep = Color(0xFF000000)
private val CardDark = Color(0xFF151515)
private val AccentYellow = Color(0xFFFFE382)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentDanger = Color(0xFFFF8A8A)
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)
private val BorderSoft = Color.White.copy(alpha = 0.06f)

private enum class CreateControlStep {
    ChooseBoard,
    ChooseType,
    ChooseQuantity,
    ConfigureControls,
}

@Composable
fun CreateControlScreen(
    isBluetoothConnected: Boolean,
    initialBoard: BoardProfile? = null,
    initialControlType: ControlType? = null,
    existingControls: List<LabLinkControl> = emptyList(),
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenControls: () -> Unit,
    onSaveControls: (List<LabLinkControl>) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    var drawerOpen by remember { mutableStateOf(false) }
    var currentStep by remember(initialBoard?.type, initialControlType) {
        mutableStateOf(
            when {
                initialBoard == null -> CreateControlStep.ChooseBoard
                initialControlType != null -> CreateControlStep.ChooseQuantity
                else -> CreateControlStep.ChooseType
            },
        )
    }

    var selectedBoard by remember(initialBoard?.type) {
        mutableStateOf(initialBoard)
    }

    var selectedType by remember(initialControlType) {
        mutableStateOf(initialControlType)
    }
    var quantity by remember { mutableIntStateOf(1) }
    var controlNames by remember { mutableStateOf(List(8) { "" }) }
    var controlPins by remember { mutableStateOf(List(8) { "" }) }

    val usedPinsFromPanel = existingControls
        .map { BoardPinValidator.normalizePin(it.pin) }
        .filter { it.isNotBlank() }
        .toSet()

    fun updateName(index: Int, value: String) {
        controlNames = controlNames.toMutableList().also {
            it[index] = value
        }
    }

    fun selectPin(index: Int, value: String) {
        controlPins = controlPins.toMutableList().also {
            it[index] = BoardPinValidator.normalizePin(value)
        }
    }

    fun clearPins() {
        controlPins = List(8) { "" }
    }
    fun createUniqueModuleId(
        type: ControlType,
        pin: String,
    ): String {
        val normalizedType = type.name.lowercase()
        val normalizedPin = BoardPinValidator
            .normalizePin(pin)
            .lowercase()

        return "${normalizedType}_${normalizedPin}_${UUID.randomUUID()}"
    }

    fun usedPinsExcept(index: Int): Set<String> {
        return controlPins
            .take(quantity)
            .mapIndexedNotNull { pinIndex, pin ->
                if (pinIndex != index && pin.isNotBlank()) {
                    BoardPinValidator.normalizePin(pin)
                } else {
                    null
                }
            }
            .toSet()
    }

    fun availablePinsAt(index: Int): List<BoardPin> {
        val board = selectedBoard ?: BoardProfiles.defaultBoard
        val type = selectedType ?: ControlType.DigitalToggle

        return BoardPinValidator.availablePinsFor(
            board = board,
            controlType = type,
            usedPins = usedPinsFromPanel + usedPinsExcept(index),
        )
    }

    fun buildConfiguredControls(): List<LabLinkControl> {
        val type = selectedType ?: ControlType.DigitalToggle

        return (0 until quantity).map { index ->
            val normalizedPin = BoardPinValidator.normalizePin(controlPins[index])
            val fallbackName = when (type) {
                ControlType.DigitalToggle -> "Liga / Desliga ${index + 1}"
                ControlType.PwmSlider -> "Slider PWM ${index + 1}"
                ControlType.ServoSlider -> "Servo ${index + 1}"
                ControlType.PulseButton -> "Pulso ${index + 1}"
                ControlType.AnalogRead -> "Leitura ${index + 1}"
            }

            LabLinkControl(
                id = createUniqueModuleId(type, normalizedPin),
                type = type,
                name = controlNames[index].ifBlank { fallbackName },
                pin = normalizedPin.removePrefix("D"),
                minValue = when (type) {
                    ControlType.PwmSlider -> 0
                    ControlType.ServoSlider -> 0
                    else -> null
                },
                maxValue = when (type) {
                    ControlType.PwmSlider -> 255
                    ControlType.ServoSlider -> 180
                    else -> null
                },
                currentValue = when (type) {
                    ControlType.PwmSlider -> 0
                    ControlType.ServoSlider -> 90
                    else -> null
                },
                isOn = false,
            )
        }
    }

    fun validatePinAt(index: Int): PinValidationResult {
        val board = selectedBoard ?: BoardProfiles.defaultBoard
        val type = selectedType ?: ControlType.DigitalToggle

        return BoardPinValidator.validatePinForControl(
            board = board,
            controlType = type,
            pin = controlPins[index],
            usedPins = usedPinsFromPanel + usedPinsExcept(index),
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgDeep,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
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
                        CreateControlStep.ChooseBoard -> {
                            HeaderSection(
                                title = "Qual placa você está usando?",
                                description = "A escolha da placa ajuda o LabLink a mostrar apenas os pinos corretos para cada tipo de controle.",
                            )

                            BoardProfiles.supportedBoards.forEach { board ->
                                BoardCard(
                                    board = board,
                                    selected = selectedBoard?.type == board.type,
                                    onClick = {
                                        selectedBoard = board
                                        clearPins()
                                    },
                                )
                            }

                            ContinueCard(
                                enabled = selectedBoard != null,
                                title = if (selectedBoard == null) {
                                    "Selecione uma placa para continuar"
                                } else {
                                    "Continuar"
                                },
                                description = if (selectedBoard == null) {
                                    "Nesta versão, começaremos com Arduino Uno / Nano."
                                } else {
                                    "Agora escolha o tipo de controle que deseja criar."
                                },
                                onClick = {
                                    if (selectedBoard != null) {
                                        currentStep = CreateControlStep.ChooseType
                                    }
                                },
                            )
                        }

                        CreateControlStep.ChooseType -> {
                            HeaderSection(
                                title = "Que módulo você quer adicionar?",
                                description = "Escolha o tipo de módulo que será usado no seu painel Arduino.",
                            )

                            SelectedBoardSummary(
                                board = selectedBoard,
                                onBack = {
                                    currentStep = CreateControlStep.ChooseBoard
                                },
                            )

                            ControlType.values().forEach { type ->
                                ControlTypeCard(
                                    type = type,
                                    selected = selectedType == type,
                                    onClick = {
                                        selectedType = type
                                        clearPins()
                                    },
                                )
                            }

                            ContinueCard(
                                enabled = selectedType != null,
                                title = if (selectedType == null) {
                                    "Selecione um módulo para continuar"
                                } else {
                                    "Continuar"
                                },
                                description = if (selectedType == null) {
                                    "Depois disso, o LabLink vai perguntar a quantidade de módulos e os pinos usados."
                                } else {
                                    "Definir quantos módulos ${selectedType?.displayName} serão criados."
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

                            SelectedBoardSummary(
                                board = selectedBoard,
                                onBack = {
                                    currentStep = CreateControlStep.ChooseBoard
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
                                    if (quantity > 1) {
                                        quantity--
                                    }
                                },
                                onIncrease = {
                                    if (quantity < 8) {
                                        quantity++
                                    }
                                },
                            )

                            ContinueCard(
                                enabled = true,
                                title = "Continuar",
                                description = "Agora vamos escolher os pinos de cada módulo.",
                                onClick = {
                                    currentStep = CreateControlStep.ConfigureControls
                                },
                            )
                        }

                        CreateControlStep.ConfigureControls -> {
                            val type = selectedType
                            val validationResults = (0 until quantity).map { index ->
                                validatePinAt(index)
                            }

                            val allPinsValid = validationResults.all { it == PinValidationResult.Valid }

                            HeaderSection(
                                title = "Escolha os pinos",
                                description = "O LabLink mostra apenas os pinos compatíveis com a placa e o tipo de módulo escolhido.",
                            )

                            SelectedBoardSummary(
                                board = selectedBoard,
                                onBack = {
                                    currentStep = CreateControlStep.ChooseBoard
                                },
                            )

                            SelectedTypeSummary(
                                type = type,
                                label = "$quantity módulo${if (quantity > 1) "s" else ""}",
                                onBack = {
                                    currentStep = CreateControlStep.ChooseQuantity
                                },
                            )

                            repeat(quantity) { index ->
                                ControlConfigCard(
                                    index = index,
                                    name = controlNames[index],
                                    selectedPin = controlPins[index],
                                    type = type,
                                    availablePins = availablePinsAt(index),
                                    validationResult = validationResults[index],
                                    onNameChange = { updateName(index, it) },
                                    onSelectPin = { selectPin(index, it) },
                                )
                            }

                            ContinueCard(
                                enabled = allPinsValid,
                                title = if (allPinsValid) "Salvar módulos" else "Escolha os pinos dos módulos",
                                description = if (allPinsValid) {
                                    "Todos os módulos estão com pinos válidos e não repetidos."
                                } else {
                                    "Selecione um pino disponível para cada módulo."
                                },
                                onClick = {
                                    onSaveControls(buildConfiguredControls())
                                },
                            )
                        }
                    }
                }
            }

            LabLinkTopAppBar(
                title = "Adicionar módulo",
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
private fun BoardCard(
    board: BoardProfile,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (selected) AccentGreen else CardDark,
                shape = RoundedCornerShape(24.dp),
            )
            .border(
                width = 1.dp,
                color = if (selected) AccentGreen.copy(alpha = 0.75f) else BorderSoft,
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
                    color = if (selected) Color.Black.copy(alpha = 0.10f) else AccentGreen.copy(alpha = 0.16f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Memory,
                contentDescription = null,
                tint = if (selected) Color.Black else AccentGreen,
                modifier = Modifier.size(25.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = board.displayName,
                color = if (selected) Color.Black else WhiteSoft,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = board.description,
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
private fun SelectedBoardSummary(
    board: BoardProfile?,
    onBack: () -> Unit,
) {
    if (board == null) return

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
                .background(AccentGreen.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Memory,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(23.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = board.displayName,
                color = WhiteSoft,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Placa selecionada",
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
    selectedPin: String,
    type: ControlType?,
    availablePins: List<BoardPin>,
    validationResult: PinValidationResult,
    onNameChange: (String) -> Unit,
    onSelectPin: (String) -> Unit,
) {
    val defaultName = when (type) {
        ControlType.DigitalToggle -> "Liga / Desliga ${index + 1}"
        ControlType.PwmSlider -> "Slider PWM ${index + 1}"
        ControlType.ServoSlider -> "Servo ${index + 1}"
        ControlType.PulseButton -> "Pulso ${index + 1}"
        ControlType.AnalogRead -> "Leitura ${index + 1}"
        null -> "Módulo ${index + 1}"
    }

    val isValid = validationResult == PinValidationResult.Valid
    val hasPin = selectedPin.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = when {
                    isValid -> AccentGreen.copy(alpha = 0.45f)
                    hasPin -> AccentDanger.copy(alpha = 0.55f)
                    else -> BorderSoft
                },
                shape = RoundedCornerShape(24.dp),
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "Módulo ${index + 1}",
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

        PinSelector(
            selectedPin = selectedPin,
            availablePins = availablePins,
            onSelectPin = onSelectPin,
        )
    }
}

@Composable
private fun PinSelector(
    selectedPin: String,
    availablePins: List<BoardPin>,
    onSelectPin: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Pino",
            color = TextDim,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
        )

        if (availablePins.isEmpty()) {
            Text(
                text = "Não há pinos disponíveis para este tipo de controle.",
                color = AccentDanger,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
            return
        }

        availablePins.chunked(4).forEach { rowPins ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowPins.forEach { pin ->
                    PinChip(
                        pin = pin,
                        selected = BoardPinValidator.normalizePin(selectedPin) == BoardPinValidator.normalizePin(pin.id),
                        onClick = { onSelectPin(pin.id) },
                        modifier = Modifier.weight(1f),
                    )
                }

                repeat(4 - rowPins.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        if (selectedPin.isBlank()) {
            Text(
                text = "Escolha um pino disponível.",
                color = TextDim,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        } else {
            Text(
                text = "${BoardPinValidator.normalizePin(selectedPin)} selecionado.",
                color = AccentGreen,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun PinChip(
    pin: BoardPin,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(
                color = if (selected) AccentGreen else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(14.dp),
            )
            .border(
                width = 1.dp,
                color = if (selected) AccentGreen.copy(alpha = 0.75f) else BorderSoft,
                shape = RoundedCornerShape(14.dp),
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = pin.label,
            color = if (selected) Color.Black else WhiteSoft,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
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
                capitalization = KeyboardCapitalization.Sentences,
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
            initialBoard = BoardProfiles.defaultBoard,
            existingControls = emptyList(),
            onOpenHome = {},
            onOpenConnection = {},
            onOpenTerminal = {},
            onOpenControls = {},
            onSaveControls = {},
        )
    }
}












