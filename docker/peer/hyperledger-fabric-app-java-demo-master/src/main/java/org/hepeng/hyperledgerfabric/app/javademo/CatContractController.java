package org.wang.hyperledgerfabric.app.javademo;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.hyperledger.fabric.client.ChaincodeEvent;
import org.hyperledger.fabric.client.ChaincodeEventsRequest;
import org.hyperledger.fabric.client.CloseableIterator;
import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.CommitStatusException;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.EndorseException;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.GatewayException;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.Status;
import org.hyperledger.fabric.client.SubmitException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * author he peng
 * date 2022/1/22 21:34
 */

@RestController
@RequestMapping("/cat")
@Slf4j
@AllArgsConstructor
public class CatContractController {

    final Gateway gateway;

    final Contract contract;

    final HyperLedgerFabricProperties hyperLedgerFabricProperties;


    @GetMapping("/{key}")
    public Map<String, Object> queryCatByKey(@PathVariable String key) throws GatewayException {

        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] cat = contract.evaluateTransaction("queryCat", key);

        result.put("payload", StringUtils.newStringUtf8(cat));
        result.put("status", "ok");

        return result;
    }

    @PutMapping("/")
    public Map<String, Object> createCat(@RequestBody CatDTO cat) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();

        byte[] bytes = contract.submitTransaction("createCat", cat.getKey(), cat.getName(), String.valueOf(cat.getAge()), cat.getColor(), cat.getBreed());

        result.put("payload", StringUtils.newStringUtf8(bytes));
        result.put("status", "ok");
        return result;
    }

    @PutMapping("/async")
    public Map<String, Object> createCatAsync(@RequestBody CatDTO cat) throws Exception {
        Map<String, Object> result = Maps.newConcurrentMap();

//        Status status = contract.newProposal("createCat")
//                .addArguments(cat.getKey(), cat.getName(), String.valueOf(cat.getAge()), cat.getColor(), cat.getBreed())
//                .build()
//                .endorse()
//                .submitAsync()
//                .getStatus();

        contract.newProposal("createCat")
                .addArguments(cat.getKey(), cat.getName(), String.valueOf(cat.getAge()), cat.getColor(), cat.getBreed())
                .build()
                .endorse()
                .submitAsync();

//        if (!status.isSuccessful()) {
//            throw new RuntimeException("Transaction " + status.getTransactionId() + " failed to commit with status code: " + status.getCode());
//        }

        result.put("status", "ok");

        return result;
    }

    @RequestMapping("/tps-test")
    public Map<String, Object> tpsTest() throws Exception {

        CatDTO cat = new CatDTO()
                .setAge(new Random().nextInt())
                .setColor("蓝色-" + System.currentTimeMillis())
                .setBreed("蓝猫")
                .setName("爱迪生" + System.currentTimeMillis())
                .setKey("cat-" + System.nanoTime());
        return createCatAsync(cat);
    }

    @PostMapping("/")
    public Map<String, Object> updateCat(@RequestBody CatDTO cat) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] bytes = contract.submitTransaction("updateCat", cat.getKey(), cat.getName(), String.valueOf(cat.getAge()), cat.getColor(), cat.getBreed());

        result.put("payload", StringUtils.newStringUtf8(bytes));
        result.put("status", "ok");

        return result;
    }

    @DeleteMapping("/{key}")
    public Map<String, Object> deleteCatByKey(@PathVariable String key) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();

        byte[] cat = contract.submitTransaction("deleteCat" , key);

        result.put("payload", StringUtils.newStringUtf8(cat));
        result.put("status", "ok");

        return result;
    }

    @GetMapping("/private/{collection}/{key}")
    public Map<String, Object> queryPrivateCatByKey(@PathVariable String collection , @PathVariable String key) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] cat = contract.evaluateTransaction("queryPrivateCat", collection , key);

        result.put("payload", StringUtils.newStringUtf8(cat));
        result.put("status", "ok");

        return result;
    }

    @GetMapping("/private/hash/{collection}/{key}")
    public Map<String, Object> queryPrivateCatHashByKey(@PathVariable String collection , @PathVariable String key) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] hash = contract.evaluateTransaction("queryPrivateCatHash", collection , key);

        result.put("payload", StringUtils.newStringUtf8(hash));
        result.put("status", "ok");

        return result;
    }

    @PutMapping("private/")
    public Map<String, Object> createPrivateCat(@RequestBody PrivateCatDTO privateCat) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();

        CatDTO cat = privateCat.getCat();

        byte[] bytes = contract.submitTransaction("createPrivateCat" , privateCat.getCollection() , cat.getKey(), cat.getName(), String.valueOf(cat.getAge()), cat.getColor(), cat.getBreed());

        result.put("payload", StringUtils.newStringUtf8(bytes));
        result.put("status", "ok");
        return result;
    }

    @PostMapping("private/")
    public Map<String, Object> updatePrivateCat(@RequestBody PrivateCatDTO privateCat) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();

        CatDTO cat = privateCat.getCat();
        byte[] bytes = contract.submitTransaction("updatePrivateCat" ,privateCat.getCollection() , cat.getKey(), cat.getName(), String.valueOf(cat.getAge()), cat.getColor(), cat.getBreed());

        result.put("payload", StringUtils.newStringUtf8(bytes));
        result.put("status", "ok");

        return result;
    }

    @DeleteMapping("/private/{collection}/{key}")
    public Map<String, Object> deletePrivateCatByKey(@PathVariable String collection , @PathVariable String key) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] cat = contract.evaluateTransaction("deletePrivateCat", collection , key);
        contract.submitTransaction("deletePrivateCat" ,collection , key);

        result.put("payload", StringUtils.newStringUtf8(cat));
        result.put("status", "ok");

        return result;
    }

}
