//package org.hepeng.hyperledgerfabric.app.javademo;
//
//import io.grpc.ManagedChannel;
//import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
//import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.hyperledger.fabric.gateway.Contract;
//import org.hyperledger.fabric.gateway.Gateway;
//import org.hyperledger.fabric.gateway.Identities;
//import org.hyperledger.fabric.gateway.Network;
//import org.hyperledger.fabric.gateway.Wallet;
//import org.hyperledger.fabric.gateway.Wallets;
//import org.hyperledger.fabric.protos.common.Common;
//import org.hyperledger.fabric.sdk.BlockEvent;
//import org.hyperledger.fabric.client.Contract;
//import org.hyperledger.fabric.client.Gateway;
//import org.hyperledger.fabric.client.Network;
//import org.hyperledger.fabric.client.identity.Identities;
//import org.hyperledger.fabric.client.identity.Signers;
//import org.hyperledger.fabric.client.identity.X509Identity;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.Reader;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.security.PrivateKey;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.function.Consumer;
//
///**
// * @author he peng
// * @date 2022/3/3
// */
//
//@Configuration
//@AllArgsConstructor
//@Slf4j
//public class HyperLedgerFabricGatewayJavaConfig {
//
//    final HyperLedgerFabricProperties hyperLedgerFabricProperties;
//
//    @Bean
//    public Gateway gateway() throws Exception {
//
//
//        BufferedReader certificateReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getCertificatePath()), StandardCharsets.UTF_8);
//
//        X509Certificate certificate = Identities.readX509Certificate(certificateReader);
//
//        BufferedReader privateKeyReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getPrivateKeyPath()), StandardCharsets.UTF_8);
//
//        PrivateKey privateKey = Identities.readPrivateKey(privateKeyReader);
//
//        Wallet wallet = Wallets.newInMemoryWallet();
//        wallet.put("user1" , Identities.newX509Identity(hyperLedgerFabricProperties.getMspId() , certificate , privateKey));
//
//
//        Gateway.Builder builder = Gateway.createBuilder()
//                .identity(wallet , "user1")
//                .networkConfig(Paths.get(hyperLedgerFabricProperties.getNetworkConnectionConfigPath()));
//
//        Gateway gateway = builder.connect();
//        Gateway gateway = Gateway.newInstance()
//                .identity(new X509Identity(hyperLedgerFabricProperties.getMspId() , certificate))
//                .signer(Signers.newPrivateKeySigner(privateKey))
//                .connection(newGrpcConnection())
//                .connect();
//
//        log.info("=========================================== connected fabric gateway {} " , gateway);
//
//        return gateway;
//    }
//
//    private ManagedChannel newGrpcConnection() throws IOException, CertificateException {
//        Reader tlsCertReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getTlsCertPath()));
//        X509Certificate tlsCert = Identities.readX509Certificate(tlsCertReader);
//
//        return NettyChannelBuilder.forTarget("localhost:7051")
//                .sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build())
//                .overrideAuthority("peer0.org1.example.com")
//                .build();
//    }
//
//    @Bean
//    public Contract catContract(Gateway gateway) {
//
//        Network network = gateway.getNetwork(hyperLedgerFabricProperties.getChannel());
//        network.addBlockListener(new Consumer<BlockEvent>() {
//            @Override
//            public void accept(BlockEvent blockEvent) {
//                Common.Block block = blockEvent.getBlock();
//                log.info("======================= block {} " , block);
//            }
//        });
//        return network.getContract("hyperledger-fabric-contract-java-demo" , "CatContract");
//    }
//}
