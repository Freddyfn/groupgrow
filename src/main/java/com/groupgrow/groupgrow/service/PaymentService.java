package com.groupgrow.groupgrow.service;

import com.groupgrow.groupgrow.dto.PaymentRequest;
import com.groupgrow.groupgrow.dto.PaymentResponse;
import com.groupgrow.groupgrow.model.GroupDetails;
import com.groupgrow.groupgrow.model.Transaction;
import com.groupgrow.groupgrow.model.User;
import com.groupgrow.groupgrow.repository.GroupDetailsRepository;
import com.groupgrow.groupgrow.repository.TransactionRepository;
import com.groupgrow.groupgrow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private GroupDetailsRepository groupDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, Long userId) {
        // Validar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar grupo
        GroupDetails group = groupDetailsRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Validar monto
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor a cero");
        }

        // Validar datos de tarjeta (básico, en producción se integraría con Stripe/PayPal)
        if (request.getCardNumber() == null || request.getCardNumber().replace(" ", "").length() != 16) {
            throw new RuntimeException("Número de tarjeta inválido");
        }

        try {
            // Crear transacción
            Transaction transaction = new Transaction();
            transaction.setUserId(userId);
            transaction.setGroupId(request.getGroupId());
            
            // Asignar tipo de transacción según el tipo de grupo
            if (group.getType() == GroupDetails.GroupType.investment) {
                transaction.setType(Transaction.TransactionType.investment);
            } else {
                transaction.setType(Transaction.TransactionType.contribution);
            }
            
            transaction.setAmount(request.getAmount());
            transaction.setDescription(
                request.getDescription() != null ? 
                request.getDescription() : 
                (group.getType() == GroupDetails.GroupType.investment ? 
                    "Inversión en el grupo " + group.getName() :
                    "Contribución al grupo " + group.getName())
            );
            transaction.setStatus(Transaction.TransactionStatus.completed);
            transaction.setCreatedAt(LocalDateTime.now());

            Transaction savedTransaction = transactionRepository.save(transaction);

            // Actualizar el monto actual del grupo
            BigDecimal newAmount = group.getCurrentAmount().add(request.getAmount());
            group.setCurrentAmount(newAmount);
            groupDetailsRepository.save(group);

            return new PaymentResponse(
                savedTransaction.getId(),
                "completed",
                request.getAmount(),
                "Pago procesado exitosamente"
            );

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
        }
    }
}

