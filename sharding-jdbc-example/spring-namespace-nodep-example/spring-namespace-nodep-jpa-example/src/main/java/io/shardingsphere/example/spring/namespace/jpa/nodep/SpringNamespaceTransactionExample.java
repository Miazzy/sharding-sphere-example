/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.example.spring.namespace.jpa.nodep;

import io.shardingsphere.core.constant.transaction.TransactionType;
import io.shardingsphere.example.repository.api.service.TransactionService;
import io.shardingsphere.example.repository.jpa.service.SpringEntityTransactionService;
import io.shardingsphere.example.type.ShardingType;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringNamespaceTransactionExample {
    
    private static ShardingType type = ShardingType.SHARDING_DATABASES;
//    private static ShardingType type = ShardingType.SHARDING_TABLES;
//    private static ShardingType type = ShardingType.SHARDING_DATABASES_AND_TABLES;
//    private static ShardingType type = ShardingType.MASTER_SLAVE;
//    private static ShardingType type = ShardingType.SHARDING_MASTER_SLAVE;
    
    //    private static boolean isRangeSharding = true;
    private static boolean isRangeSharding = false;
    
    public static void main(final String[] args) {
        try (ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(isRangeSharding ? getApplicationFileRange() : getApplicationFilePrecise())) {
            process(applicationContext);
        }
    }
    
    private static String getApplicationFilePrecise() {
        switch (type) {
            case SHARDING_DATABASES:
                return "META-INF/application-sharding-databases-precise.xml";
            case SHARDING_TABLES:
                return "META-INF/application-sharding-tables-precise.xml";
            case SHARDING_DATABASES_AND_TABLES:
                return "META-INF/application-sharding-databases-tables-precise.xml";
            case MASTER_SLAVE:
                return "META-INF/application-master-slave.xml";
            case SHARDING_MASTER_SLAVE:
                return "META-INF/application-sharding-master-slave-precise.xml";
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }
    
    private static String getApplicationFileRange() {
        switch (type) {
            case SHARDING_DATABASES:
                return "META-INF/application-sharding-databases-range.xml";
            case SHARDING_TABLES:
                return "META-INF/application-sharding-tables-range.xml";
            case SHARDING_DATABASES_AND_TABLES:
                return "META-INF/application-sharding-databases-tables-range.xml";
            case MASTER_SLAVE:
                return "META-INF/application-master-slave.xml";
            case SHARDING_MASTER_SLAVE:
                return "META-INF/application-sharding-master-slave-range.xml";
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }
    
    private static void process(final ConfigurableApplicationContext applicationContext) {
        TransactionService transactionService = getTransactionService(applicationContext);
        transactionService.processSuccess(false);
        processFailureSingleTransaction(transactionService, TransactionType.LOCAL);
        processFailureSingleTransaction(transactionService, TransactionType.XA);
        processFailureSingleTransaction(transactionService, TransactionType.BASE);
        processFailureSingleTransaction(transactionService, TransactionType.LOCAL);
    }
    
    private static void processFailureSingleTransaction(TransactionService transactionService, TransactionType type) {
        try {
            switch (type) {
                case LOCAL:
                    transactionService.processFailureWithLocal();
                    break;
                case XA:
                    transactionService.processFailureWithXa();
                    break;
                case BASE:
                    transactionService.processFailureWithBase();
                    break;
                default:
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            transactionService.printData(false);
        }
    }
    
    private static TransactionService getTransactionService(final ConfigurableApplicationContext applicationContext) {
        return applicationContext.getBean("jdbcTransactionService", SpringEntityTransactionService.class);
    }
}
