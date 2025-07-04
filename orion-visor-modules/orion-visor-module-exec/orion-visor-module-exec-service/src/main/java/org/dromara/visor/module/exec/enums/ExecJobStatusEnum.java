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
package org.dromara.visor.module.exec.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 执行任务状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/3/28 16:25
 */
@Getter
@AllArgsConstructor
public enum ExecJobStatusEnum {

    /**
     * 停用
     */
    DISABLED(0, "停用"),

    /**
     * 启用
     */
    ENABLED(1, "启用"),

    ;

    private final Integer status;

    private final String label;

    public static ExecJobStatusEnum of(Integer status) {
        if (status == null) {
            return DISABLED;
        }
        for (ExecJobStatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return DISABLED;
    }

    public static ExecJobStatusEnum of(String label) {
        if (label == null) {
            return DISABLED;
        }
        for (ExecJobStatusEnum value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        return DISABLED;
    }

}
