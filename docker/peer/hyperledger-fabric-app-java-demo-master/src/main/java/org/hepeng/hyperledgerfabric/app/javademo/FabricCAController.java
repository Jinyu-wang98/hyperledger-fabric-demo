package org.wang.hyperledgerfabric.app.javademo;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author he peng
 * @date 2022/3/10
 */

@RestController
@RequestMapping("/fabric-ca")
@AllArgsConstructor
public class FabricCAController {

    final HFCAClient hfcaClient;

    @PostMapping("/enroll")
    public Map<String , Object> enroll(String user, String secret) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();

        Enrollment enrollment = hfcaClient.enroll(user, secret);

        result.put("payload", enrollment.getCert());
        result.put("status", "ok");
        return result;
    }

    @PostMapping("/register")
    public Map<String , Object> register(@RequestBody CAUserRegisterDTO registerDTO) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();

        RegistrationRequest rr = new RegistrationRequest(registerDTO.getId() , "org1.department1");
        rr.setSecret(registerDTO.getSecret());
        rr.setType(HFCAClient.HFCA_TYPE_USER);

        if (CollectionUtils.isNotEmpty(registerDTO.getAttrs())) {
            for (CAUserAttributeDTO attr : registerDTO.getAttrs()) {
                rr.addAttribute(new Attribute(attr.getName() , attr.getValue() , false));
            }

        }

        User registrar = new User() {
            @Override
            public String getName() {
                return "xiaoming";
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return null;
            }

            @Override
            public Enrollment getEnrollment() {
                try {
                    return hfcaClient.enroll("intcaadmin" , "intcaadminpw");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String getMspId() {
                return "Org1MSP";
            }
        };
        String register = hfcaClient.register(rr, registrar);

        result.put("payload", JSON.toJSONString(register));
        result.put("status", "ok");
        return result;
    }


    @GetMapping("/user")
    public Map<String , Object> getUser(String id) throws Exception {

        Map<String, Object> result = Maps.newConcurrentMap();

        User user = new User() {
            @Override
            public String getName() {
                return id;
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return null;
            }

            @Override
            public Enrollment getEnrollment() {
                try {
                    return hfcaClient.enroll("intcaadmin" , "intcaadminpw");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String getMspId() {
                return null;
            }
        };

        Collection<HFCAIdentity> identities = hfcaClient.getHFCAIdentities(user);

        result.put("payload", identities);
        result.put("status", "ok");
        return result;
    }

}
