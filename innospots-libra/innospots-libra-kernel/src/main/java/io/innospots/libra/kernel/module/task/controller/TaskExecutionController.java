package io.innospots.libra.kernel.module.task.controller;

import io.innospots.libra.base.menu.ModuleMenu;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.innospots.libra.base.controller.BaseController.PATH_ROOT_ADMIN;

/**
 * @author Smars
 * @date 2023/8/7
 */
@RestController
@RequestMapping(PATH_ROOT_ADMIN + "task-execution")
@ModuleMenu(menuKey = "libra-task")
@Tag(name = "TaskExecution")
public class TaskExecutionController {


}
