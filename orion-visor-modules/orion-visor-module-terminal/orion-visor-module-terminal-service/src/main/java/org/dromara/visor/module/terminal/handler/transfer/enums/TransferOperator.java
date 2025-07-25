/*
 * Copyright (c) 2023 - present Dromara, All rights reserved.
 *
 *   https://visor.dromara.org
 *   https://visor.dromara.org.cn
 *   https://visor.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dromara.visor.module.terminal.handler.transfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 传输操作类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/2/21 22:03
 */
@Getter
@AllArgsConstructor
public enum TransferOperator {

    /**
     * 开始传输
     */
    START("start"),

    /**
     * 传输完成
     */
    FINISH("finish"),

    /**
     * 传输失败
     */
    ERROR("error"),

    /**
     * 传输中断
     */
    ABORT("abort"),

    ;

    private final String type;

    public static TransferOperator of(String type) {
        if (type == null) {
            return null;
        }
        for (TransferOperator value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
