package org.wang.hyperledgerfabric.app.javademo;

import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.util.Properties;

/**
 * @author he peng
 * @date 2022/3/10
 */

@Configuration
public class FabricCAConfiguration {


    @Bean
    public HFCAClient hfcaClient() throws Exception {

        Properties properties = new Properties();
        properties.setProperty("pemFile" , "D:\\OpenProject\\hyperledger-fabric-app-java-demo\\src\\main\\resources\\ca-cert.pem");
        HFCAClient hfcaClient = HFCAClient.createNewInstance("org1-int-ca", "https://192.168.0.105:7056", properties);

        hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        return hfcaClient;
    }
}
