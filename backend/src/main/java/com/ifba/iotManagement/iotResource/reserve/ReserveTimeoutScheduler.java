package com.ifba.iotManagement.iotResource.reserve;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReserveTimeoutScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(ReserveTimeoutScheduler.class);
    
    private final IotResourceReserveService reserveService;
    
    public ReserveTimeoutScheduler(IotResourceReserveService reserveService) {
        this.reserveService = reserveService;
    }
    
    @Scheduled(fixedRate = 60000) // Execute a cada 1 minuto
    public void handleExpiredReserves() {
        try {
            logger.debug("Verificando reservas expiradas...");
            reserveService.handleExpiredReserves();
            logger.debug("Verificação de reservas expiradas concluída");
        } catch (Exception e) {
            logger.error("Erro ao processar reservas expiradas", e);
        }
    }
}