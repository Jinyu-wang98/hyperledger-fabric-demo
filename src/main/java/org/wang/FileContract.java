package org.wang;

import com.alibaba.fastjson.JSON;
import lombok.extern.java.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

/**
 * author he peng
 * date 2022/1/19 14:56
 */


@Contract(
        name = "FileContract",
        info = @Info(
                title = "File contract",
                description = "The hyperlegendary file contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "f.carr@example.com",
                        name = "F Carr",
                        url = "https://hyperledger.example.com")))
@Default
@Log
public class FileContract implements ContractInterface {


    @Transaction
    public void initLedger(final Context ctx) {

        ChaincodeStub stub = ctx.getStub();
        for (int i = 0; i < 10; i++ ) {
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            File file = new File().setName("file-" + i)
                    .setUpdateTime(sdf.format(System.currentTimeMillis()))
                    .setFirstPartyName("王某某")
                    .setSecondPartyName("刘某某")
                    .setContent(null);
            stub.putStringState(file.getName() , JSON.toJSONString(file));
        }

    }

    @Transaction
    public File queryFile(final Context ctx, final String key) {

        ChaincodeStub stub = ctx.getStub();
        String fileState = stub.getStringState(key);

        if (StringUtils.isBlank(fileState)) {
            String errorMessage = String.format("File %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        return JSON.parseObject(fileState , File.class);
    }

    @Transaction
    public File createFile(final Context ctx, final String key , String name , String firstPartyName , String secondPartyName, Image content) {

        ChaincodeStub stub = ctx.getStub();
        String catState = stub.getStringState(key);

        if (StringUtils.isNotBlank(catState)) {
            String errorMessage = String.format("File %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage);
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File file = new File().setName(name)
                .setUpdateTime(sdf.format(System.currentTimeMillis()))
                .setFirstPartyName(firstPartyName)
                .setSecondPartyName(secondPartyName)
                .setContent(content);

        String json = JSON.toJSONString(file);
        stub.putStringState(key, json);

        stub.setEvent("createFileEvent" , org.apache.commons.codec.binary.StringUtils.getBytesUtf8(json));
        return file;
    }

    @Transaction
    public File updateFile(final Context ctx, final String key , String name , String firstPartyName , String secondPartyName, Image content) {

        ChaincodeStub stub = ctx.getStub();
        String FileState = stub.getStringState(key);

        if (StringUtils.isBlank(FileState)) {
            String errorMessage = String.format("File %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage);
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File file = new File().setName(name)
                .setUpdateTime(sdf.format(System.currentTimeMillis()))
                .setFirstPartyName(firstPartyName)
                .setSecondPartyName(secondPartyName)
                .setContent(content);

        stub.putStringState(key, JSON.toJSONString(file));

        return file;
    }

    @Transaction
    public File deleteFile(final Context ctx, final String key) {

        ChaincodeStub stub = ctx.getStub();
        String fileState = stub.getStringState(key);

        if (StringUtils.isBlank(fileState)) {
            String errorMessage = String.format("File %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        stub.delState(key);

        return JSON.parseObject(fileState , File.class);
    }

    @Transaction
    public byte[] queryPrivateFileHash(final Context ctx, final String collection ,final String key) {

        ChaincodeStub stub = ctx.getStub();

        byte[] hash = stub.getPrivateDataHash(collection, key);

        if (ArrayUtils.isEmpty(hash)) {
            String errorMessage = String.format("Private File %s does not exist", key);
            log.log(Level.WARNING , errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        return hash;
    }

    @Transaction
    public PrivateFile queryPrivateCat(final Context ctx, final String collection , final String key) {

        ChaincodeStub stub = ctx.getStub();

        log.info(String.format("查询私有数据 , collection [%s] key [%s] , mspId [%s] " , collection , stub.getMspId() , key));

        String catState = stub.getPrivateDataUTF8(collection , key);

        if (StringUtils.isBlank(catState)) {
            String errorMessage = String.format("Private Cat %s does not exist", key);
            log.log(Level.WARNING , errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        return JSON.parseObject(catState , PrivateFile.class);
    }


    @Transaction
    public PrivateFile createPrivateCat(final Context ctx, final String collection , final String key , String name , String firstPartyName , String secondPartyName, Image content) {

        ChaincodeStub stub = ctx.getStub();
        log.info(String.format("创建私有数据 , collection [%s] , mspId [%s] , key [%s] , name [%s] firstPartyName [%s] secondPartyName [%s] content [%s] " , collection , stub.getMspId() , key , name , firstPartyName , secondPartyName , content));

        String fileState = stub.getPrivateDataUTF8(collection , key);

        if (StringUtils.isNotBlank(fileState)) {
            String errorMessage = String.format("Private Cat %s already exists", key);
            log.log(Level.WARNING , errorMessage);
            throw new ChaincodeException(errorMessage);
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PrivateFile  privateFile = new PrivateFile()
                .setFile(new File().setName(name)
                .setUpdateTime(sdf.format(System.currentTimeMillis()))
                .setFirstPartyName(firstPartyName)
                .setSecondPartyName(secondPartyName)
                        .setContent(content))
                .setCollection(collection);

        String json = JSON.toJSONString(privateFile);

        log.info(String.format("要保存的数据 %s" , json));

        stub.putPrivateData(collection , key , json);

        return privateFile;
    }

    @Transaction
    public PrivateFile updatePrivateCat(final Context ctx, final String collection, final String key , String name , String firstPartyName , String secondPartyName, Image content) {

        ChaincodeStub stub = ctx.getStub();
        log.info(String.format("更新私有数据 , collection [%s] , mspId [%s] , key [%s] , name [%s] firstPartyName [%s] secondPartyName [%s] content [%s] " , collection,stub.getMspId() , key , name , firstPartyName, secondPartyName, content));

        String fileState = stub.getPrivateDataUTF8(collection , key);

        if (StringUtils.isBlank(fileState)) {
            String errorMessage = String.format("Private File %s does not exist", key);
            log.log(Level.WARNING , errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PrivateFile  privateFile = new PrivateFile()
                .setFile(new File().setName(name)
                        .setUpdateTime(sdf.format(System.currentTimeMillis()))
                        .setFirstPartyName(firstPartyName)
                        .setSecondPartyName(secondPartyName)
                        .setContent(content))
                .setCollection(collection);

        String json = JSON.toJSONString(privateFile);

        log.info(String.format("要保存的数据 %s" , json));

        stub.putPrivateData(collection , key , json);

        return privateFile;
    }

    @Transaction
    public PrivateFile deletePrivateCat(final Context ctx, final String collection ,final String key) {

        ChaincodeStub stub = ctx.getStub();

        log.info(String.format("删除私有数据 , collection [%s] , mspId [%s] , key [%s] " , collection , stub.getMspId() , key));

        String fileState = stub.getPrivateDataUTF8(collection , key);

        if (StringUtils.isBlank(fileState)) {
            String errorMessage = String.format("Private File %s does not exist", key);
            log.log(Level.WARNING , errorMessage);

            throw new ChaincodeException(errorMessage);
        }

        stub.delPrivateData(collection , key);

        return JSON.parseObject(fileState , PrivateFile.class);
    }

    @Override
    public void beforeTransaction(Context ctx) {
        log.info("*************************************** beforeTransaction ***************************************");
    }

    @Override
    public void afterTransaction(Context ctx, Object result) {
        log.info("*************************************** afterTransaction ***************************************");
        System.out.println("result --------> " + result);
    }
}
