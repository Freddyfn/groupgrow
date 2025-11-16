package com.groupgrow.groupgrow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupgrow.groupgrow.dto.*;
import com.groupgrow.groupgrow.dto.gemini.GeminiRequest;
import com.groupgrow.groupgrow.dto.gemini.GeminiResponse;
import com.groupgrow.groupgrow.model.GroupDetails;
import com.groupgrow.groupgrow.repository.GroupDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeminiAiService {

    @Autowired
    private GroupDetailsRepository groupDetailsRepository;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askGro(Long groupId, String userQuestion) {
        GroupDetails groupDetails = groupDetailsRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        List<MemberStatusDto> memberStatusList = groupDetailsRepository.getMemberStatusForGroup(groupId);
        AiPromptData promptData = buildPromptData(groupDetails, memberStatusList);
        String finalPrompt = buildFinalPrompt(promptData, userQuestion);

        return callGeminiApi(finalPrompt);
    }

    private String callGeminiApi(String prompt) {
        String url = UriComponentsBuilder.fromHttpUrl(geminiApiUrl)
                .queryParam("key", geminiApiKey)
                .toUriString();

        GeminiRequest requestBody = new GeminiRequest(prompt);

        try {
            System.out.println("=== LLAMANDO A GEMINI API ===");
            System.out.println("URL: " + url.replace(geminiApiKey, "***API_KEY***"));
            System.out.println("Prompt: " + prompt.substring(0, Math.min(200, prompt.length())) + "...");
            
            GeminiResponse response = restTemplate.postForObject(url, requestBody, GeminiResponse.class);

            if (response != null) {
                String answer = response.getFirstCandidateText();
                System.out.println("Respuesta recibida: " + answer.substring(0, Math.min(100, answer.length())) + "...");
                return answer;
            } else {
                System.err.println("ERROR: La respuesta de Gemini es nula");
                return "El asistente no devolvió una respuesta.";
            }
        } catch (Exception e) {
            System.err.println("ERROR al llamar a Gemini API: " + e.getMessage());
            e.printStackTrace();
            return "Error: No se pudo conectar con el asistente de IA. " + e.getMessage();
        }
    }
    
    // --- El resto de los métodos (buildPromptData, mapToMemberInfo, etc.) se mantienen sin cambios ---

    private AiPromptData buildPromptData(GroupDetails groupDetails, List<MemberStatusDto> memberStatusList) {
        double currentAmount = groupDetails.getCurrentAmount().doubleValue();
        double targetAmount = groupDetails.getTargetAmount().doubleValue();
        double progressPercentage = (targetAmount > 0) ? (currentAmount * 100 / targetAmount) : 0;
        BigDecimal progressDecimal = new BigDecimal(progressPercentage).setScale(2, RoundingMode.HALF_UP);

        // Calcular días restantes hasta el deadline
        int diasRestantes = 0;
        String fechaLimite = "No especificada";
        if (groupDetails.getDeadline() != null) {
            LocalDate now = LocalDate.now();
            LocalDate deadline = groupDetails.getDeadline();
            diasRestantes = (int) java.time.temporal.ChronoUnit.DAYS.between(now, deadline);
            fechaLimite = deadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        // Preparar información del grupo
        GroupInfo groupInfo = new GroupInfo(
                groupDetails.getName(),
                groupDetails.getType() != null ? groupDetails.getType().name() : "saving",
                targetAmount,
                currentAmount,
                progressDecimal.doubleValue(),
                groupDetails.getMinimumContribution() != null ? groupDetails.getMinimumContribution().doubleValue() : 0,
                groupDetails.getContributionFrequency() != null ? groupDetails.getContributionFrequency().name() : "monthly",
                fechaLimite,
                diasRestantes,
                memberStatusList.size()
        );

        List<MemberInfo> membersInfo = memberStatusList.stream()
                .map(this::mapToMemberInfo)
                .collect(Collectors.toList());

        return new AiPromptData(groupInfo, membersInfo);
    }

    private MemberInfo mapToMemberInfo(MemberStatusDto dto) {
        String lastContributionDate = (dto.getFechaUltimoAporte() != null)
                ? new SimpleDateFormat("yyyy-MM-dd").format(dto.getFechaUltimoAporte())
                : null;

        String nextContributionDate = calculateNextContributionDate(
                dto.getFechaUltimoAporte(),
                dto.getFrecuenciaDeAporte()
        );

        return new MemberInfo(
                dto.getNombreMiembro(),
                dto.getTotalAportadoEnGrupo(),
                lastContributionDate,
                nextContributionDate
        );
    }

    private String calculateNextContributionDate(Date lastContribution, String frequency) {
        if (lastContribution == null) {
            return "Primer aporte pendiente";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastContribution);

        if (frequency != null) {
            switch (frequency.toLowerCase()) {
                case "weekly":
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                    break;
                case "monthly":
                    calendar.add(Calendar.MONTH, 1);
                    break;
                case "quarterly":
                    calendar.add(Calendar.MONTH, 3);
                    break;
                case "flexible":
                    return "Flexible (sin fecha fija)";
                default:
                    return "Frecuencia desconocida";
            }
        } else {
            return "Frecuencia no especificada";
        }


        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    private String buildFinalPrompt(AiPromptData promptData, String userQuestion) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonData;
        try {
            jsonData = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(promptData);
        } catch (JsonProcessingException e) {
            jsonData = "{\"error\": \"Could not generate JSON data\"}";
        }

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy"));

        return "Eres \"Gro\", un asistente de IA amigable y motivador para la plataforma GroupGrow. Tu única función es analizar el progreso de los grupos de ahorro/inversión y responder las preguntas de los usuarios basándote en los datos que te proporciono.\n\n" +
                "### 📜 REGLAS ESTRICTAS:\n" +
                "1.  **PROHIBIDO DAR CONSEJOS DE INVERSIÓN:** Bajo ninguna circunstancia puedes recomendar dónde invertir, sugerir activos financieros o dar consejos de inversión. Tu enfoque es solo el ahorro y el progreso del grupo.\n" +
                "2.  **USA SOLO LOS DATOS PROPORCIONADOS:** Basa el 100% de tus respuestas en el contexto JSON que te entrego. No inventes cifras, fechas ni progreso.\n" +
                "3.  **HAZ CÁLCULOS Y PROYECCIONES:** Tienes acceso a contribución mínima, frecuencia, deadline y número de miembros. DEBES hacer proyecciones cuando te pregunten cuándo alcanzarán la meta.\n" +
                "4.  **INTERPRETA LOS DATOS:** Si \"fechaUltimoAporte\" es nulo o \"totalAportado\" es 0, significa que el miembro es nuevo y aún no ha hecho su primer aporte.\n" +
                "5.  **SÉ CONCISO Y CLARO:** Responde como un chat. Usa un lenguaje sencillo y motivador. Usa formato Markdown para listas con * o - y negritas con **texto**.\n" +
                "6.  **FECHA DE REFERENCIA:** Para tus cálculos, hoy es " + currentDate + ".\n\n" +
                "### 🧮 CÓMO HACER PROYECCIONES:\n" +
                "- Si te preguntan cuándo alcanzarán la meta, calcula:\n" +
                "  * Dinero faltante = metaGrupo - actualGrupo\n" +
                "  * Aporte esperado por periodo = contribucionMinima × totalMiembros\n" +
                "  * Periodos necesarios = dinero faltante ÷ aporte esperado por periodo\n" +
                "  * Proyecta la fecha según la frecuencia (weekly/monthly/quarterly)\n" +
                "- Compara con diasRestantes y fechaLimite para ver si es realista\n" +
                "- Si no alcanzarían a tiempo, sugiéreles aumentar aportes o añadir miembros\n\n" +
                "---\n\n" +
                "### 📊 DATOS DEL GRUPO (Contexto JSON)\n" +
                "A continuación, te entrego los datos completos del grupo y sus miembros:\n\n" +
                jsonData + "\n\n" +
                "---\n\n" +
                "### 👤 PREGUNTA DEL USUARIO:\n" +
                userQuestion + "\n\n" +
                "### 🤖 TU RESPUESTA:";
    }
}
