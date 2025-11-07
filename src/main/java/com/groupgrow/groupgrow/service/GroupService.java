package com.groupgrow.groupgrow.service;

import com.groupgrow.groupgrow.dto.CreateGroupRequest;
import com.groupgrow.groupgrow.dto.GroupResponse;
import com.groupgrow.groupgrow.dto.UpdateGroupRequest;
import com.groupgrow.groupgrow.model.GroupDetails;
import com.groupgrow.groupgrow.model.User;
import com.groupgrow.groupgrow.repository.GroupDetailsRepository;
import com.groupgrow.groupgrow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupDetailsRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

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
        return convertToResponse(savedGroup, creator);
    }

    public List<GroupResponse> getGroupsByCreator(Long creatorId) {
        List<GroupDetails> groups = groupRepository.findByCreatorId(creatorId);
        return groups.stream()
                .map(group -> {
                    User creator = userRepository.findById(group.getCreatorId()).orElse(null);
                    return convertToResponse(group, creator);
                })
                .collect(Collectors.toList());
    }

    public GroupResponse getGroupById(Long groupId) {
        GroupDetails group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
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
        // TODO: Calcular currentMembers desde la tabla de transacciones o miembros
        response.setCurrentMembers(1); // Por ahora, solo el creador
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

