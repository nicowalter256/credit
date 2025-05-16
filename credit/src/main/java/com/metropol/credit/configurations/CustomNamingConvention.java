package com.metropol.credit.configurations;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;

public class CustomNamingConvention extends PhysicalNamingStrategyStandardImpl {

    Logger logger = LoggerFactory.getLogger(CustomNamingConvention.class);

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {

        if (name == null) {
            return name;
        }
        String columeName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name.getText());
        return Identifier.toIdentifier(columeName);
    }

}
