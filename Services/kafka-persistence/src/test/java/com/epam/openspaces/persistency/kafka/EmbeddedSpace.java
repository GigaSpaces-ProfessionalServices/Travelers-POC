package com.epam.openspaces.persistency.kafka;

import org.openspaces.core.cluster.ClusterInfo;
import org.openspaces.pu.container.ProcessingUnitContainer;
import org.openspaces.pu.container.integrated.IntegratedProcessingUnitContainerProvider;

import java.io.IOException;


/**
 * @author Mykola_Zalyayev
 */
public class EmbeddedSpace {

    private ProcessingUnitContainer space;
    private String xmlConfigFile;
    private Schema schema;

    public EmbeddedSpace(String xmlConfigFile, Schema schema) {
        this.xmlConfigFile = xmlConfigFile;
        this.schema = schema;
    }

    public void startup() throws IOException {
        IntegratedProcessingUnitContainerProvider container = new IntegratedProcessingUnitContainerProvider();
        container.addConfigLocation(xmlConfigFile);
        if (schema.equals(Schema.PARTITIONED)) {
            ClusterInfo spaceClusterInfo = getPartitionedSchemaSchema();
            container.setClusterInfo(spaceClusterInfo);
        }
        space = container.createContainer();
    }

    private ClusterInfo getPartitionedSchemaSchema() {
        ClusterInfo spaceClusterInfo = new ClusterInfo();
        spaceClusterInfo.setNumberOfInstances(1);
        spaceClusterInfo.setNumberOfBackups(1);
        spaceClusterInfo.setSchema("partitioned-sync2backup");
        return spaceClusterInfo;
    }

    public void shutdown() {
        space.close();
    }

    public enum Schema {
        NONE, PARTITIONED
    }

}
