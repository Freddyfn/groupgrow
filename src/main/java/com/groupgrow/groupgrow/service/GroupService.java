package com.groupgrow.groupgrow.service;

import com.groupgrow.groupgrow.dto.CreateGroupRequest;
import com.groupgrow.groupgrow.dto.GroupDashboardDTO;
import com.groupgrow.groupgrow.dto.GroupResponse;
import com.groupgrow.groupgrow.dto.UpdateGroupRequest;
import com.groupgrow.groupgrow.model.GroupDetails;
import com.groupgrow.groupgrow.model.GroupMember;
import com.groupgrow.groupgrow.model.Transaction;
import com.groupgrow.groupgrow.model.User;
import com.groupgrow.groupgrow.repository.GroupDetailsRepository;
import com.groupgrow.groupgrow.repository.GroupMemberRepository;
import com.groupgrow.groupgrow.repository.TransactionRepository;
import com.groupgrow.groupgrow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupDetailsRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public GroupResponse createGroup(CreateGroupRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        GroupDetails group = new GroupDetails();
        group.setName(request.getName());
        group.setType(GroupDetails.GroupType.valueOf(request.getType()));
        group.setDescription(request.getDescription());
        // Usamos goal como objective, pero en la BD no hay campo objective, usaremos description
        if (request.getGoal() != null && !request.getGoal().isEmpty()) {
            group.setDescription(request.getDescription() != null && !request.getDescription().isEmpty() 
                ? request.getDescription() + " - " + request.getGoal() 
                : request.getGoal());
        }
        group.setTargetAmount(request.getTargetAmount());
        group.setCurrentAmount(java.math.BigDecimal.ZERO);
        group.setDeadline(request.getDeadline());
        group.setMaxMembers(request.getMaxMembers() != null ? request.getMaxMembers() : 10);
        String privacyStr = request.getPrivacy() != null ? request.getPrivacy().toUpperCase() : "PUBLIC";
        group.setPrivacy(GroupDetails.Privacy.valueOf(privacyStr));
        group.setInvitationCode(request.getInvitationCode());
        
        if (request.getContributionFrequency() != null) {
            group.setContributionFrequency(GroupDetails.ContributionFrequency.valueOf(request.getContributionFrequency()));
        }
        group.setMinimumContribution(request.getMinimumContribution());
        
        if (request.getRiskLevel() != null && !request.getRiskLevel().isEmpty()) {
            group.setRiskLevel(GroupDetails.RiskLevel.valueOf(request.getRiskLevel()));
        }
        
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            group.setCategory(GroupDetails.Category.valueOf(request.getCategory()));
        }
        
        group.setCreatorId(creatorId);

        GroupDetails savedGroup = groupRepository.save(group);
        
        // Agregar al creador como miembro del grupo
        GroupMember creatorMembership = new GroupMember();
        creatorMembership.setUserId(creatorId);
        creatorMembership.setGroupId(savedGroup.getId());
        groupMemberRepository.save(creatorMembership);
        
        return convertToResponse(savedGroup, creator);
    }

    public List<GroupResponse> getGroupsByCreator(Long userId) {
        // Obtener IDs de grupos donde el usuario es miembro
        List<Long> memberGroupIds = groupMemberRepository.findGroupIdsByUserId(userId);
        
        // Obtener grupos donde el usuario es creador
        List<GroupDetails> createdGroups = groupRepository.findByCreatorId(userId);
        
        // Combinar ambas listas sin duplicados usando Set
        java.util.Set<Long> allGroupIds = new java.util.HashSet<>(memberGroupIds);
        createdGroups.forEach(group -> allGroupIds.add(group.getId()));
        
        // Obtener todos los grupos únicos
        List<GroupDetails> allGroups = allGroupIds.stream()
                .map(groupId -> groupRepository.findById(groupId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        // Convertir a GroupResponse
        return allGroups.stream()
                .map(group -> {
                    User creator = userRepository.findById(group.getCreatorId()).orElse(null);
                    
                    // Asegurar que el creador esté en group_members
                    if (group.getCreatorId().equals(userId)) {
                        ensureCreatorIsMember(userId, group.getId());
                    }
                    
                    return convertToResponse(group, creator);
                })
                .collect(Collectors.toList());
    }
    
    private void ensureCreatorIsMember(Long userId, Long groupId) {
        // Verificar si el creador ya está en group_members
        List<Long> memberGroupIds = groupMemberRepository.findGroupIdsByUserId(userId);
        if (!memberGroupIds.contains(groupId)) {
            // Agregar el creador como miembro si no está
            GroupMember creatorMember = new GroupMember();
            creatorMember.setUserId(userId);
            creatorMember.setGroupId(groupId);
            groupMemberRepository.save(creatorMember);
        }
    }

    public GroupResponse getGroupById(Long groupId) {
        GroupDetails group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        
        // Asegurar que el creador esté en group_members
        ensureCreatorIsMember(group.getCreatorId(), group.getId());
        
        User creator = userRepository.findById(group.getCreatorId()).orElse(null);
        return convertToResponse(group, creator);
    }

    @Transactional
    public GroupResponse updateGroup(Long groupId, UpdateGroupRequest request, Long userId) {
        GroupDetails group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Verificar que el usuario es el creador
        if (!group.getCreatorId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para editar este grupo");
        }

        if (request.getName() != null) {
            group.setName(request.getName());
        }
        if (request.getType() != null) {
            group.setType(GroupDetails.GroupType.valueOf(request.getType()));
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        if (request.getGoal() != null && !request.getGoal().isEmpty()) {
            group.setDescription(request.getDescription() != null && !request.getDescription().isEmpty() 
                ? request.getDescription() + " - " + request.getGoal() 
                : request.getGoal());
        }
        if (request.getTargetAmount() != null) {
            group.setTargetAmount(request.getTargetAmount());
        }
        if (request.getDeadline() != null) {
            group.setDeadline(request.getDeadline());
        }
        if (request.getMaxMembers() != null) {
            group.setMaxMembers(request.getMaxMembers());
        }
        if (request.getPrivacy() != null) {
            group.setPrivacy(GroupDetails.Privacy.valueOf(request.getPrivacy().toUpperCase()));
        }
        if (request.getInvitationCode() != null) {
            group.setInvitationCode(request.getInvitationCode());
        }
        if (request.getContributionFrequency() != null) {
            group.setContributionFrequency(GroupDetails.ContributionFrequency.valueOf(request.getContributionFrequency()));
        }
        if (request.getMinimumContribution() != null) {
            group.setMinimumContribution(request.getMinimumContribution());
        }
        if (request.getRiskLevel() != null && !request.getRiskLevel().isEmpty()) {
            group.setRiskLevel(GroupDetails.RiskLevel.valueOf(request.getRiskLevel()));
        }
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            group.setCategory(GroupDetails.Category.valueOf(request.getCategory()));
        }

        GroupDetails updatedGroup = groupRepository.save(group);
        User creator = userRepository.findById(updatedGroup.getCreatorId()).orElse(null);
        return convertToResponse(updatedGroup, creator);
    }

    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        GroupDetails group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Verificar que el usuario es el creador
        if (!group.getCreatorId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para eliminar este grupo");
        }

        groupRepository.delete(group);
    }
    
    public List<GroupResponse> getPublicGroups() {
        return getPublicGroupsForUser(null);
    }
    
    public List<GroupResponse> getPublicGroupsForUser(Long userId) {
        List<GroupDetails> publicGroups = groupRepository.findByPrivacy(GroupDetails.Privacy.PUBLIC);
        return publicGroups.stream()
                .map(group -> {
                    // Asegurar que el creador esté en group_members
                    ensureCreatorIsMember(group.getCreatorId(), group.getId());
                    
                    User creator = userRepository.findById(group.getCreatorId()).orElse(null);
                    GroupResponse response = convertToResponse(group, creator);
                    
                    // Verificar si el usuario es miembro del grupo
                    if (userId != null) {
                        boolean isMember = groupMemberRepository.findUserIdsByGroupId(group.getId())
                                .contains(userId);
                        response.setIsMember(isMember);
                    } else {
                        response.setIsMember(false);
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void joinGroup(Long groupId, Long userId) {
        // Verificar que el grupo existe
        GroupDetails group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        
        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar si el usuario ya es miembro
        List<Long> memberIds = groupMemberRepository.findUserIdsByGroupId(groupId);
        if (memberIds.contains(userId)) {
            throw new RuntimeException("Ya eres miembro de este grupo");
        }
        
        // Verificar que el grupo no esté lleno
        int currentMembers = memberIds.size();
        if (currentMembers >= group.getMaxMembers()) {
            throw new RuntimeException("El grupo está lleno");
        }
        
        // Agregar al usuario como miembro
        GroupMember newMember = new GroupMember();
        newMember.setUserId(userId);
        newMember.setGroupId(groupId);
        groupMemberRepository.save(newMember);
        
        System.out.println("Usuario " + user.getFirstName() + " " + user.getLastName() + 
                         " (ID: " + userId + ") se unió al grupo '" + group.getName() + "' (ID: " + groupId + ")");
    }
    
    public GroupDashboardDTO getGroupDashboard(Long groupId) {
        return getGroupDashboardForUser(groupId, null);
    }
    
    public GroupDashboardDTO getGroupDashboardForUser(Long groupId, Long userId) {
        GroupDetails group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Asegurar que el creador esté en group_members
        ensureCreatorIsMember(group.getCreatorId(), group.getId());

        GroupDashboardDTO dashboard = new GroupDashboardDTO();
        dashboard.setId(group.getId().toString());
        dashboard.setName(group.getName());
        dashboard.setDescription(group.getDescription());
        dashboard.setType(group.getType().name().toLowerCase());
        dashboard.setTarget(group.getTargetAmount());
        dashboard.setCurrent(group.getCurrentAmount());
        dashboard.setMonthlyContribution(group.getMinimumContribution());
        dashboard.setDeadline(group.getDeadline());
        dashboard.setInvestmentTerm(group.getDeadline()); // Por ahora igual al deadline
        dashboard.setStatus("active");

        // Determinar el tipo de transacción según el tipo de grupo
        String transactionType = group.getType() == GroupDetails.GroupType.investment ? "investment" : "contribution";

        // Calcular contribución del usuario si se proporciona userId
        if (userId != null) {
            BigDecimal userContribution = transactionRepository.sumByUserAndGroupAndType(userId, groupId, transactionType);
            dashboard.setUserContribution(userContribution != null ? userContribution : BigDecimal.ZERO);
        } else {
            dashboard.setUserContribution(BigDecimal.ZERO);
        }
        
        // TODO: Implementar cálculo de ganancias cuando se integre con sistema de inversiones
        dashboard.setUserProfits(BigDecimal.ZERO);
        dashboard.setTotalProfits(BigDecimal.ZERO);

        // Obtener miembros del grupo y calcular sus contribuciones desde la base de datos
        List<GroupMember> groupMembers = groupMemberRepository.findByGroupId(groupId);
        List<GroupDashboardDTO.MemberDTO> members = new ArrayList<>();
        
        System.out.println("========================================");
        System.out.println("CALCULANDO CONTRIBUCIONES PARA GRUPO ID: " + groupId);
        System.out.println("Tipo de grupo: " + group.getType() + " | Tipo de transacción: " + transactionType);
        System.out.println("========================================");
        
        for (GroupMember gm : groupMembers) {
            User user = userRepository.findById(gm.getUserId()).orElse(null);
            if (user != null) {
                // Calcular contribución sumando transacciones completadas desde la base de datos
                // Usa el tipo correcto según el tipo de grupo
                BigDecimal contribution = transactionRepository.sumByUserAndGroupAndType(
                    gm.getUserId(), 
                    groupId, 
                    transactionType
                );
                
                // Verificar todas las transacciones del usuario en este grupo (para debug)
                List<Transaction> userTransactions = transactionRepository.findByGroupId(groupId).stream()
                    .filter(t -> t.getUserId().equals(gm.getUserId()))
                    .collect(Collectors.toList());
                
                System.out.println("Usuario: " + user.getFirstName() + " " + user.getLastName() + " (ID: " + gm.getUserId() + ")");
                System.out.println("  - Transacciones encontradas: " + userTransactions.size());
                for (Transaction t : userTransactions) {
                    System.out.println("    * Transacción ID " + t.getId() + ": $" + t.getAmount() + 
                                     " | Tipo: " + t.getType() + " | Status: " + t.getStatus());
                }
                System.out.println("  - CONTRIBUCIÓN TOTAL CALCULADA: $" + (contribution != null ? contribution : "NULL"));
                
                if (contribution == null) contribution = BigDecimal.ZERO;
                
                // Determinar el estado del miembro (al día o atrasado)
                String status = calculateMemberStatus(gm.getUserId(), groupId, group);
                
                members.add(new GroupDashboardDTO.MemberDTO(
                    user.getId(),
                    user.getFirstName() + " " + user.getLastName(),
                    "",
                    contribution,
                    status
                ));
            }
        }
        
        System.out.println("========================================");
        System.out.println("TOTAL DE MIEMBROS PROCESADOS: " + members.size());
        System.out.println("========================================\n");
        
        dashboard.setMembers(members);

        // Performance - Datos reales basados en transacciones del grupo
        dashboard.setPerformance(buildGroupPerformance(groupId, group));

        // Transacciones
        List<Transaction> transactions = transactionRepository.findByGroupId(groupId);
        List<GroupDashboardDTO.TransactionDTO> transactionDTOs = transactions.stream()
                .map(t -> {
                    User user = userRepository.findById(t.getUserId()).orElse(null);
                    String memberName = user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown";
                    // Formatear fecha con zona horaria local
                    String formattedDate = "";
                    if (t.getCreatedAt() != null) {
                        formattedDate = t.getCreatedAt()
                            .atZone(java.time.ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                    return new GroupDashboardDTO.TransactionDTO(
                        t.getId(),
                        t.getType().name(),
                        t.getAmount(),
                        memberName,
                        t.getDescription(),
                        formattedDate,
                        t.getStatus().name()
                    );
                })
                .collect(Collectors.toList());
        dashboard.setTransactions(transactionDTOs);

        // Por ahora no hay votaciones activas
        dashboard.setActiveVoting(null);

        return dashboard;
    }

    private List<GroupDashboardDTO.PerformanceDTO> buildGroupPerformance(Long groupId, GroupDetails group) {
        // Obtener transacciones del grupo agrupadas por mes
        List<Transaction> transactions = transactionRepository.findByGroupId(groupId);
        
        // Determinar el tipo de transacción según el tipo de grupo
        Transaction.TransactionType expectedType = group.getType() == GroupDetails.GroupType.investment ? 
            Transaction.TransactionType.investment : Transaction.TransactionType.contribution;
        
        // Agrupar transacciones por mes y sumar
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM", new java.util.Locale("es", "ES"));
        LocalDate now = LocalDate.now();
        LocalDate startMonth = now.minusMonths(3).withDayOfMonth(1);
        
        // Crear mapa de acumulados mensuales
        java.util.Map<String, BigDecimal> monthlyTotals = new java.util.HashMap<>();
        BigDecimal runningTotal = BigDecimal.ZERO;
        
        for (Transaction t : transactions) {
            if (t.getCreatedAt() != null && t.getType() == expectedType) {
                String monthKey = t.getCreatedAt().format(monthFormatter);
                monthlyTotals.put(monthKey, monthlyTotals.getOrDefault(monthKey, BigDecimal.ZERO).add(t.getAmount()));
            }
        }
        
        // Construir lista de performance
        List<GroupDashboardDTO.PerformanceDTO> performance = new ArrayList<>();
        BigDecimal accumulated = BigDecimal.ZERO;
        
        for (int i = 0; i < 4; i++) {
            LocalDate month = startMonth.plusMonths(i);
            String monthLabel = capitalize(month.format(monthFormatter).replace(".", ""));
            String monthKey = month.format(monthFormatter);
            
            // Acumular el total del mes
            BigDecimal monthContribution = monthlyTotals.getOrDefault(monthKey, BigDecimal.ZERO);
            accumulated = accumulated.add(monthContribution);
            
            // Meta proporcional basada en los meses transcurridos
            BigDecimal targetForMonth = group.getTargetAmount()
                    .multiply(BigDecimal.valueOf(i + 1))
                    .divide(BigDecimal.valueOf(4), 2, java.math.RoundingMode.HALF_UP);
            
            performance.add(new GroupDashboardDTO.PerformanceDTO(
                monthLabel,
                accumulated,
                targetForMonth
            ));
        }
        
        return performance;
    }
    
    private String calculateMemberStatus(Long userId, Long groupId, GroupDetails group) {
        // Determinar el tipo de transacción según el tipo de grupo
        Transaction.TransactionType expectedType = group.getType() == GroupDetails.GroupType.investment ? 
            Transaction.TransactionType.investment : Transaction.TransactionType.contribution;
        
        // Obtener la última transacción del usuario en este grupo
        List<Transaction> userTransactions = transactionRepository.findByGroupId(groupId).stream()
                .filter(t -> t.getUserId().equals(userId) && t.getType() == expectedType)
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .collect(Collectors.toList());
        
        // Si no tiene transacciones, verificar si es nuevo (unido recientemente)
        if (userTransactions.isEmpty()) {
            // Obtener cuando se unió al grupo
            List<GroupMember> membership = groupMemberRepository.findByGroupId(groupId).stream()
                    .filter(gm -> gm.getUserId().equals(userId))
                    .collect(Collectors.toList());
            
            if (!membership.isEmpty()) {
                LocalDateTime joinedAt = membership.get(0).getJoinedAt();
                if (joinedAt != null) {
                    long daysSinceJoined = java.time.temporal.ChronoUnit.DAYS.between(joinedAt, LocalDateTime.now());
                    // Si se unió hace menos de 7 días, aún está "current"
                    if (daysSinceJoined < 7) {
                        return "current";
                    }
                }
            }
            // Si no tiene transacciones y lleva más de 7 días, está atrasado
            return "late";
        }
        
        // Obtener la última transacción
        Transaction lastTransaction = userTransactions.get(0);
        LocalDateTime lastPayment = lastTransaction.getCreatedAt();
        
        // Calcular días desde el último pago
        long daysSinceLastPayment = java.time.temporal.ChronoUnit.DAYS.between(lastPayment, LocalDateTime.now());
        
        // Determinar el límite de días según la frecuencia de contribución
        int daysLimit = 30; // Por defecto mensual
        if (group.getContributionFrequency() != null) {
            switch (group.getContributionFrequency()) {
                case weekly:
                    daysLimit = 10; // 7 días + 3 días de gracia
                    break;
                case monthly:
                    daysLimit = 35; // 30 días + 5 días de gracia
                    break;
                case quarterly:
                    daysLimit = 95; // 90 días + 5 días de gracia
                    break;
                case flexible:
                    return "current"; // En modo flexible siempre están al día
            }
        }
        
        // Si excedió el límite, está atrasado
        if (daysSinceLastPayment > daysLimit) {
            return "late";
        }
        
        return "current";
    }
    
    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private GroupResponse convertToResponse(GroupDetails group, User creator) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setType(group.getType().name());
        response.setDescription(group.getDescription());
        // Extraer objective de description si está separado por " - "
        String description = group.getDescription() != null ? group.getDescription() : "";
        if (description.contains(" - ")) {
            String[] parts = description.split(" - ", 2);
            response.setDescription(parts[0]);
            response.setObjective(parts[1]);
        } else {
            response.setObjective(description);
        }
        response.setTargetAmount(group.getTargetAmount());
        response.setCurrentAmount(group.getCurrentAmount());
        response.setDeadline(group.getDeadline());
        response.setMaxMembers(group.getMaxMembers());
        // Calcular currentMembers desde la tabla group_members
        int currentMembers = (int) groupMemberRepository.countByGroupId(group.getId());
        response.setCurrentMembers(currentMembers);
        response.setPrivacy(group.getPrivacy().name().toLowerCase());
        response.setInvitationCode(group.getInvitationCode());
        response.setContributionFrequency(group.getContributionFrequency() != null ? group.getContributionFrequency().name() : null);
        response.setMinimumContribution(group.getMinimumContribution());
        response.setRiskLevel(group.getRiskLevel() != null ? group.getRiskLevel().name() : null);
        response.setCategory(group.getCategory() != null ? group.getCategory().name() : null);
        response.setCreatorId(group.getCreatorId());
        if (creator != null) {
            response.setCreatorName(creator.getFirstName() + " " + creator.getLastName());
        }
        response.setCreatedAt(group.getCreatedAt());
        return response;
    }
}

