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
package org.dromara.visor.module.terminal.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * sftp 获取文件内容缓存对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/10/10 20:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SftpGetContentCacheDTO", description = "sftp 获取文件内容缓存对象")
public class SftpGetContentCacheDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主机id")
    private Long hostId;

    @Schema(description = "文件路径")
    private String path;

}
