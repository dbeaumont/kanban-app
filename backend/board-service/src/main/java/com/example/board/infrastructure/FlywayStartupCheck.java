package com.example.board.infrastructure;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Vérifie explicitement que Flyway est bien chargé et exécuté
 * au démarrage de l'application Spring Boot 4.x.
 *
 * Utile pour diagnostiquer les cas où Flyway ne s'exécute pas
 * silencieusement (absence de logs, datasource non prête, etc.).
 */
@Configuration
public class FlywayStartupCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayStartupCheck.class);

    @Bean
    public ApplicationRunner flywayStartupVerifier(Flyway flyway) {
        return new ApplicationRunner() {

            @Override
            public void run(ApplicationArguments args) {
                LOGGER.info("Flyway bean détecté : démarrage de la vérification");

                MigrationInfoService info = flyway.info();

                MigrationInfo current = info.current();
                MigrationInfo[] applied = info.applied();
                MigrationInfo[] pending = info.pending();

                if (current == null) {
                    LOGGER.warn("Aucune migration Flyway appliquée (version courante = null)");
                } else {
                    LOGGER.info("Version Flyway courante : {}", current.getVersion());
                    LOGGER.info("Description migration courante : {}", current.getDescription());
                }

                LOGGER.info("Nombre de migrations appliquées : {}", applied.length);
                LOGGER.info("Nombre de migrations en attente : {}", pending.length);

                if (pending.length > 0) {
                    LOGGER.warn("Des migrations Flyway sont en attente !");
                    for (MigrationInfo migration : pending) {
                        LOGGER.warn(" - {} : {}", migration.getVersion(), migration.getDescription());
                    }
                } else {
                    LOGGER.info("Aucune migration Flyway en attente");
                }
            }
        };
    }
}
