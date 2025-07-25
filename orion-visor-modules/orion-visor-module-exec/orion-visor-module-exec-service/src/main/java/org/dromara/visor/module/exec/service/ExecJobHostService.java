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
package org.dromara.visor.module.exec.service;

import java.util.List;

/**
 * 计划任务主机 服务类
 *
 * @author Jiahang Li
 * @version 1.0.3
 * @since 2024-3-28 12:03
 */
public interface ExecJobHostService {

    /**
     * 设置任务主机
     *
     * @param jobId      jobId
     * @param hostIdList hostIdList
     */
    void setHostIdByJobId(Long jobId, List<Long> hostIdList);

    /**
     * 通过 hostId 获取 jobId
     *
     * @param jobId jobId
     * @return hostId
     */
    List<Long> getHostIdByJobId(Long jobId);

    /**
     * 通过 jobId 删除
     *
     * @param jobId jobId
     * @return effect
     */
    Integer deleteByJobId(Long jobId);

    /**
     * 通过 jobId 删除
     *
     * @param jobIdList jobIdList
     * @return effect
     */
    Integer deleteByJobIdList(List<Long> jobIdList);

}
