package io.innospots.libra.kernel.module.task.mapper;

import io.innospots.base.mapper.BaseConvertMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Smars
 * @date 2023/8/7
 */
@Mapper
public interface TaskExecutionMapper extends BaseConvertMapper {

    TaskExecutionMapper INSTANCE = Mappers.getMapper(TaskExecutionMapper.class);
}
