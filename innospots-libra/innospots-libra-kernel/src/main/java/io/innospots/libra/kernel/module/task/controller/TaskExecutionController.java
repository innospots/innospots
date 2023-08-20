package io.innospots.libra.kernel.module.task.controller;

import io.innospots.base.model.PageBody;
import io.innospots.base.model.response.InnospotResponse;
import io.innospots.libra.base.menu.ModuleMenu;
import io.innospots.libra.base.task.TaskExecution;
import io.innospots.libra.base.task.TaskExecutionStatus;
import io.innospots.libra.kernel.module.task.model.TaskExecutionRequest;
import io.innospots.libra.kernel.module.task.operator.TaskExecutionOperator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static io.innospots.base.model.response.InnospotResponse.success;
import static io.innospots.libra.base.controller.BaseController.PATH_ROOT_ADMIN;

/**
 * @author Smars
 * @date 2023/8/7
 */
@RestController
@RequestMapping(PATH_ROOT_ADMIN + "task-execution")
@ModuleMenu(menuKey = "libra-task-execution")
@Tag(name = "TaskExecution")
public class TaskExecutionController {

    private final TaskExecutionOperator taskExecutionOperator;

    public TaskExecutionController(TaskExecutionOperator taskExecutionOperator) {
        this.taskExecutionOperator = taskExecutionOperator;
    }

    @GetMapping("page/task-execution")
    @Operation(summary = "page task executions")
    public InnospotResponse<PageBody<TaskExecution>> pageTaskExecutions(TaskExecutionRequest request) {

        PageBody<TaskExecution> pageModel = taskExecutionOperator.pageTaskExecutions(request);
        return success(pageModel);
    }

    @GetMapping("task-code")
    @Operation(summary = "get taskCode")
    public InnospotResponse<List<String>> getTaskCode() {
        List<String> taskCodes = new ArrayList<>();
        for (TaskExecutionStatus status : TaskExecutionStatus.values()) {
            taskCodes.add(status.name());
        }
        return success(taskCodes);
    }
}
