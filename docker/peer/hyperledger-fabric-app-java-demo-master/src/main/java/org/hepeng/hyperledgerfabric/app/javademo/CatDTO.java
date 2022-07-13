package org.wang.hyperledgerfabric.app.javademo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * author he peng
 * date 2022/1/22 22:00
 */

@Data
@Accessors(chain = true)
public class CatDTO {

    String key;

    String name;

    Integer age;

    String color;

    String breed;
}
