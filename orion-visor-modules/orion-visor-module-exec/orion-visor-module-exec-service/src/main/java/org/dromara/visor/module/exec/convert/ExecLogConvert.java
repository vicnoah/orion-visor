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
package org.dromara.visor.module.exec.convert;

import org.dromara.visor.module.exec.entity.domain.ExecLogDO;
import org.dromara.visor.module.exec.entity.request.exec.ExecLogQueryRequest;
import org.dromara.visor.module.exec.entity.vo.ExecLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 批量执行日志 内部对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.1
 * @since 2024-3-11 11:31
 */
@Mapper
public interface ExecLogConvert {

    ExecLogConvert MAPPER = Mappers.getMapper(ExecLogConvert.class);

    ExecLogDO to(ExecLogQueryRequest request);

    ExecLogVO to(ExecLogDO domain);

    List<ExecLogVO> to(List<ExecLogDO> list);

}
