package com.back.global.jpa.config

import org.hibernate.boot.Metadata
import org.hibernate.boot.spi.BootstrapContext
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.integrator.spi.Integrator
import org.hibernate.mapping.PersistentClass

class DynamicUpdateIntegrator : Integrator {
    override fun integrate(
        metadata: Metadata,
        bootstrapContext: BootstrapContext,
        sessionFactory: SessionFactoryImplementor
    ) {
        for (persistentClass: PersistentClass in metadata.entityBindings) {
            persistentClass.setDynamicUpdate(true)
        }
    }
}
