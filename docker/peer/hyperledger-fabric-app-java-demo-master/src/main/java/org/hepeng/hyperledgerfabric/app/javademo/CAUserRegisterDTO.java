package org.wang.hyperledgerfabric.app.javademo;

import lombok.Data;

import java.util.List;

/**
 * @author he peng
 * @date 2022/3/11
 */

@Data
public class CAUserRegisterDTO {

    String id;

    String secret;

    List<CAUserAttributeDTO> attrs;
}
