package io.innospots.libra.kernel.module.task.operator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.CaseFormat;
import io.innospots.base.model.PageBody;
import io.innospots.libra.base.task.TaskExecution;
import io.innospots.libra.kernel.module.task.dao.TaskExecutionDao;
import io.innospots.libra.kernel.module.task.entity.TaskExecutionEntity;
import io.innospots.libra.kernel.module.task.mapper.TaskExecutionMapper;
import io.innospots.libra.kernel.module.task.model.TaskExecutionRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Smars
 * @date 2023/8/7
 */
@Slf4j
@Service
public class TaskExecutionOperator extends ServiceImpl<TaskExecutionDao, TaskExecutionEntity> {

    @Transactional(rollbackFor = Exception.class)
    public Boolean createTaskExecution(TaskExecution taskExecution) {
        TaskExecutionMapper mapper = TaskExecutionMapper.INSTANCE;
        TaskExecutionEntity entity = mapper.model2Entity(taskExecution);
        return super.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTaskExecution(TaskExecution taskExecution) {
        TaskExecutionEntity entity = this.getById(taskExecution.getTaskExecutionId());
        TaskExecutionMapper.INSTANCE.updateEntity2Model(entity, taskExecution);
        return super.updateById(entity);
    }

    public TaskExecution getTaskExecution(String taskExecutionId) {
        TaskExecutionEntity entity = this.getById(taskExecutionId);
        return TaskExecutionMapper.INSTANCE.entity2Model(entity);
    }

    public PageBody<TaskExecution> pageTaskExecutions(TaskExecutionRequest request) {
        QueryWrapper<TaskExecutionEntity> query = new QueryWrapper<>();
        if (StringUtils.isNotBlank(request.getQueryInput())) {
            query.like("TASK_NAME", "%" + request.getQueryInput() + "%");
        }
        if (CollectionUtils.isNotEmpty(request.getAppNames())) {
            query.in("APP_NAME", request.getAppNames());
        }
        if (CollectionUtils.isNotEmpty(request.getTaskCodes())) {
            query.in("EXECUTION_STATUS", request.getTaskCodes());
        }
        if (StringUtils.isNotBlank(request.getStartDate())) {
            query.ge("START_TIME", request.getStartDate());
        }
        if (StringUtils.isNotBlank(request.getEndDate())) {
            query.le("END_TIME", request.getEndDate());
        }
        if (StringUtils.isNotBlank(request.getSort())) {
            query.orderBy(true, request.getAsc(), CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, request.getSort()));
        } else {
            query.orderBy(true, false, CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "createdTime"));
        }
        PageBody<TaskExecution> pageBody = new PageBody<>();
        IPage<TaskExecutionEntity> oPage = new Page<>(request.getPage(), request.getSize());
        IPage<TaskExecutionEntity> entityPage = super.page(oPage, query);
        List<TaskExecutionEntity> entities = entityPage.getRecords();
        pageBody.setCurrent(entityPage.getCurrent());
        pageBody.setPageSize(entityPage.getSize());
        pageBody.setTotal(entityPage.getTotal());
        pageBody.setTotalPage(entityPage.getPages());
        pageBody.setList(entities.stream().map(TaskExecutionMapper.INSTANCE::entity2Model).collect(Collectors.toCollection(() -> new ArrayList<>(entities.size()))));
        return pageBody;
    }
}
