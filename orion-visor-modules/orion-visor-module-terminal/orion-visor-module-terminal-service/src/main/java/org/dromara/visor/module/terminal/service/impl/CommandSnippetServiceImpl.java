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
package org.dromara.visor.module.terminal.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.dromara.visor.common.constant.ErrorMessage;
import org.dromara.visor.common.utils.Valid;
import org.dromara.visor.framework.redis.core.utils.RedisMaps;
import org.dromara.visor.framework.redis.core.utils.barrier.CacheBarriers;
import org.dromara.visor.framework.security.core.utils.SecurityUtils;
import org.dromara.visor.module.terminal.convert.CommandSnippetConvert;
import org.dromara.visor.module.terminal.dao.CommandSnippetDAO;
import org.dromara.visor.module.terminal.define.cache.CommandSnippetCacheKeyDefine;
import org.dromara.visor.module.terminal.entity.domain.CommandSnippetDO;
import org.dromara.visor.module.terminal.entity.dto.CommandSnippetCacheDTO;
import org.dromara.visor.module.terminal.entity.request.snippet.CommandSnippetCreateRequest;
import org.dromara.visor.module.terminal.entity.request.snippet.CommandSnippetUpdateRequest;
import org.dromara.visor.module.terminal.entity.vo.CommandSnippetGroupVO;
import org.dromara.visor.module.terminal.entity.vo.CommandSnippetVO;
import org.dromara.visor.module.terminal.entity.vo.CommandSnippetWrapperVO;
import org.dromara.visor.module.terminal.service.CommandSnippetGroupService;
import org.dromara.visor.module.terminal.service.CommandSnippetService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 命令片段 服务实现类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024-1-22 15:28
 */
@Slf4j
@Service
public class CommandSnippetServiceImpl implements CommandSnippetService {

    @Resource
    private CommandSnippetDAO commandSnippetDAO;

    @Resource
    private CommandSnippetGroupService commandSnippetGroupService;

    @Override
    public Long createCommandSnippet(CommandSnippetCreateRequest request) {
        Long userId = SecurityUtils.getLoginUserId();
        log.info("CommandSnippetService-createCommandSnippet request: {}", JSON.toJSONString(request));
        // 转换
        CommandSnippetDO record = CommandSnippetConvert.MAPPER.to(request);
        record.setUserId(userId);
        // 查询数据是否冲突
        this.checkCommandSnippetPresent(record);
        // 插入
        int effect = commandSnippetDAO.insert(record);
        Long id = record.getId();
        log.info("CommandSnippetService-createCommandSnippet id: {}, effect: {}", id, effect);
        // 删除缓存
        RedisMaps.delete(CommandSnippetCacheKeyDefine.SNIPPET.format(userId));
        return id;
    }

    @Override
    public Integer updateCommandSnippetById(CommandSnippetUpdateRequest request) {
        Long id = Valid.notNull(request.getId(), ErrorMessage.ID_MISSING);
        Long userId = SecurityUtils.getLoginUserId();
        log.info("CommandSnippetService-updateCommandSnippetById id: {}, request: {}", id, JSON.toJSONString(request));
        // 查询
        CommandSnippetDO record = commandSnippetDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        // 查询数据是否冲突
        CommandSnippetDO updateRecord = CommandSnippetConvert.MAPPER.to(request);
        this.checkCommandSnippetPresent(updateRecord);
        // 更新
        LambdaUpdateWrapper<CommandSnippetDO> update = Wrappers.<CommandSnippetDO>lambdaUpdate()
                .set(CommandSnippetDO::getGroupId, request.getGroupId())
                .set(CommandSnippetDO::getName, request.getName())
                .set(CommandSnippetDO::getCommand, request.getCommand())
                .eq(CommandSnippetDO::getId, id)
                .eq(CommandSnippetDO::getUserId, userId);
        int effect = commandSnippetDAO.update(null, update);
        log.info("CommandSnippetService-updateCommandSnippetById effect: {}", effect);
        // 删除缓存
        RedisMaps.delete(CommandSnippetCacheKeyDefine.SNIPPET.format(userId));
        return effect;
    }

    @Override
    public CommandSnippetWrapperVO getCommandSnippet() {
        // 查询分组
        List<CommandSnippetGroupVO> groups = commandSnippetGroupService.getCommandSnippetGroupList();
        // 查询命令片段
        List<CommandSnippetVO> items = this.getCommandSnippetList();
        // 设置组内数据
        Map<Long, CommandSnippetGroupVO> groupMap = groups.stream()
                .collect(Collectors.toMap(CommandSnippetGroupVO::getId, Function.identity()));
        groupMap.forEach((groupId, group) -> {
            List<CommandSnippetVO> groupedItems = items.stream()
                    .filter(s -> groupId.equals(s.getGroupId()))
                    .collect(Collectors.toList());
            group.setItems(groupedItems);
        });
        // 未分组数据
        List<CommandSnippetVO> ungroupedItems = items.stream()
                .filter(s -> s.getGroupId() == null)
                .collect(Collectors.toList());
        return CommandSnippetWrapperVO.builder()
                .groups(groups)
                .ungroupedItems(ungroupedItems)
                .build();
    }

    @Override
    public List<CommandSnippetVO> getCommandSnippetList() {
        Long userId = SecurityUtils.getLoginUserId();
        String cacheKey = CommandSnippetCacheKeyDefine.SNIPPET.format(userId);
        // 查询缓存
        List<CommandSnippetCacheDTO> list = RedisMaps.valuesJson(cacheKey, CommandSnippetCacheKeyDefine.SNIPPET);
        if (list.isEmpty()) {
            // 查询数据库
            list = commandSnippetDAO.of()
                    .createWrapper()
                    .eq(CommandSnippetDO::getUserId, userId)
                    .then()
                    .list(CommandSnippetConvert.MAPPER::toCache);
            // 设置屏障 防止穿透
            CacheBarriers.checkBarrier(list, CommandSnippetCacheDTO::new);
            // 设置缓存
            RedisMaps.putAllJson(cacheKey, CommandSnippetCacheKeyDefine.SNIPPET, s -> s.getId().toString(), list);
        }
        // 删除屏障
        CacheBarriers.removeBarrier(list);
        // 转换
        return list.stream()
                .map(CommandSnippetConvert.MAPPER::to)
                .sorted(Comparator.comparing(CommandSnippetVO::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Integer setGroupNull(Long userId, Long groupId) {
        int effect = commandSnippetDAO.setGroupIdWithNull(groupId);
        // 删除缓存
        RedisMaps.delete(CommandSnippetCacheKeyDefine.SNIPPET.format(userId));
        return effect;
    }

    @Override
    public Integer deleteCommandSnippetById(Long id) {
        Long userId = SecurityUtils.getLoginUserId();
        log.info("CommandSnippetService-deleteCommandSnippetById id: {}", id);
        // 检查数据是否存在
        CommandSnippetDO record = commandSnippetDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        // 删除
        int effect = commandSnippetDAO.deleteById(id);
        log.info("CommandSnippetService-deleteCommandSnippetById id: {}, effect: {}", id, effect);
        // 删除缓存
        RedisMaps.delete(CommandSnippetCacheKeyDefine.SNIPPET.format(userId), id);
        return effect;
    }

    @Override
    public Integer deleteByGroupId(Long userId, Long groupId) {
        int effect = commandSnippetDAO.deleteByGroupId(groupId);
        // 删除缓存
        RedisMaps.delete(CommandSnippetCacheKeyDefine.SNIPPET.format(userId));
        return effect;
    }

    @Override
    public Integer deleteByUserIdList(List<Long> userIdList) {
        return commandSnippetDAO.deleteByUserIdList(userIdList);
    }

    /**
     * 检查对象是否存在
     *
     * @param domain domain
     */
    private void checkCommandSnippetPresent(CommandSnippetDO domain) {
        // 构造条件
        LambdaQueryWrapper<CommandSnippetDO> wrapper = commandSnippetDAO.wrapper()
                // 更新时忽略当前记录
                .ne(CommandSnippetDO::getId, domain.getId())
                // 用其他字段做重复校验
                .eq(CommandSnippetDO::getUserId, domain.getUserId())
                .eq(CommandSnippetDO::getGroupId, domain.getGroupId())
                .eq(CommandSnippetDO::getName, domain.getName());
        // 检查是否存在
        boolean present = commandSnippetDAO.of(wrapper).present();
        Valid.isFalse(present, ErrorMessage.DATA_PRESENT);
    }

}
