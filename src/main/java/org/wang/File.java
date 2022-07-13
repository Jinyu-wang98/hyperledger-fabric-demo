package org.wang;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.awt.*;

/**
 * author he peng
 * date 2022/1/19 15:02
 */

@DataType
@Data
@Accessors(chain = true)
public class File {

    @Property
    String key;

    @Property
    String name;

    @Property
    String updateTime;

    @Property
    String firstPartyName;

    @Property
    String secondPartyName;

    @Property
    Image content;
}
