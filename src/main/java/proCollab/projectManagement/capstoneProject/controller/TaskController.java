package proCollab.projectManagement.capstoneProject.controller;

import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proCollab.projectManagement.capstoneProject.model.Project;
import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.TaskHistory;
import proCollab.projectManagement.capstoneProject.model.TaskThreads;
import proCollab.projectManagement.capstoneProject.model.User;
import proCollab.projectManagement.capstoneProject.repository.TaskThreadsRepository;
import proCollab.projectManagement.capstoneProject.service.CompanyService;
import proCollab.projectManagement.capstoneProject.service.ProjectService;
import proCollab.projectManagement.capstoneProject.service.TaskHistoryService;
import proCollab.projectManagement.capstoneProject.service.TaskService;
import proCollab.projectManagement.capstoneProject.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Controller
public class TaskController {

    private TaskService taskService;
    private UserService userService;
    private CompanyService companyService;
    private ProjectService projectService;
    private TaskHistoryService taskHistoryService;

    @Autowired
    private TaskThreadsRepository taskThreadsRepository;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, CompanyService companyService,
            ProjectService projectService, TaskHistoryService taskHistoryService) {
        this.taskService = taskService;
        this.userService = userService;
        this.companyService = companyService;
        this.projectService = projectService;
        this.taskHistoryService = taskHistoryService;
    }

    @GetMapping("/tasks")
    public String listTasks(Model model, Principal principal, SecurityContextHolderAwareRequestWrapper request) {
        prepareTasksListModel(model, principal, request);
        model.addAttribute("onlyInProgress", false);
        return "views/tasks";
    }

    @GetMapping("/tasks/in-progress")
    public String listTasksInProgress(Model model, Principal principal,
            SecurityContextHolderAwareRequestWrapper request) {
        prepareTasksListModel(model, principal, request);
        model.addAttribute("onlyInProgress", true);
        return "views/tasks";
    }

    private void prepareTasksListModel(Model model, Principal principal,
            SecurityContextHolderAwareRequestWrapper request) {
        String email = principal.getName();
        User signedUser = userService.getUserByEmail(email);
        boolean isAdminSigned = request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_SUPERADMIN");
        List<User> allUsers = companyService.getCompanyUsers(signedUser.getCompany().getId());
        List<Task> allTask = companyService.getAllTaskByCompany(signedUser.getCompany().getId());
        List<Project> usersProjects = userService.getUserProjects(signedUser);

        // Using iterator to avoid ConcurrentModificationException
        Iterator<Task> taskIterator = allTask.iterator();
        while (taskIterator.hasNext()) {
            Task t = taskIterator.next();
            if (t.getCreatedUser() != signedUser
                    || (t.getProject() != null && !usersProjects.contains(t.getProject()))) {
                taskIterator.remove();
            }
        }

        model.addAttribute("tasks", allTask);
        model.addAttribute("users", allUsers);
        model.addAttribute("signedUser", signedUser);
        model.addAttribute("isAdminSigned", isAdminSigned);
    }

    @GetMapping("/task/create")
    public String showEmptyTaskForm(Model model, Principal principal,
            SecurityContextHolderAwareRequestWrapper request) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
                
        Task task = new Task();
        task.setCreatorName(user.getName());
        task.setCreatedUser(user);
        if (request.isUserInRole("ROLE_USER")) {
            task.setOwner(user);
        }
        model.addAttribute("task", task);
        return "forms/task-new";
    }

    @GetMapping("/task/create/{projectId}")
    public String createTaskForProject(@PathVariable long projectId, Model model, Principal principal,
            SecurityContextHolderAwareRequestWrapper request) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        Task task = new Task();
        task.setCreatorName(user.getName());
        task.setCreatedUser(user);
        if (request.isUserInRole("ROLE_USER")) {
            task.setOwner(user);
        }
        model.addAttribute("task", task);
        return "forms/task-new";
    }

    @PostMapping("/task/create/{projectId}")
    public String createTaskForProject(@PathVariable long projectId,
            @Valid Task task,
            BindingResult bindingResult,
            Model model,
            Principal principal,
            SecurityContextHolderAwareRequestWrapper request) {
        if (bindingResult.hasErrors()) {
            return "forms/task-new";
        }

        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        // Set creator, owner, and created user
        task.setCreatorName(user.getName());
        task.setCreatedUser(user);
        if (request.isUserInRole("ROLE_USER")) {
            task.setOwner(user);
        }

        // Set the project for the task
        Project project = projectService.getProjectById(projectId);
        task.setProject(project);

        // Save the task
        taskService.createTask(task);

        return "redirect:/projects/tasks/" + projectId;
    }

    @PostMapping("/task/create")
    public String createTask(@Valid Task task, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "forms/task-new";
        }

        taskService.createTask(task);

        return "redirect:/tasks";
    }

    @GetMapping("/task/edit/{id}")
    public String showFilledTaskForm(@PathVariable Long id, Model model) {
        model.addAttribute("task", taskService.getTaskById(id));
        return "forms/task-edit";
    }

    @PostMapping("/task/edit/{id}")
    public String updateTask(@Valid Task task, BindingResult bindingResult, @PathVariable Long id, Model model) {
        if (bindingResult.hasErrors()) {
            return "forms/task-edit";
        }
        taskService.updateTask(id, task);
        return "redirect:/tasks";
    }

    @GetMapping("/task/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }

    @GetMapping("/task/mark-done/{id}")
    public String setTaskCompleted(@PathVariable Long id) {
        taskService.setTaskCompleted(id);
        Task task = taskService.getTaskById(id);
        User user = task.getOwner();
        if (user != null) {
            user.setAllocatedStoryPoints(user.getAllocatedStoryPoints() - task.getStoryPoints());
            userService.saveUser(user);
        }
        return "redirect:/tasks";
    }

    @GetMapping("/task/unmark-done/{id}")
    public String setTaskNotCompleted(@PathVariable Long id) {
        taskService.setTaskNotCompleted(id);
        Task task = taskService.getTaskById(id);
        User user = task.getOwner();
        if (user != null) {
            user.setAllocatedStoryPoints(user.getAllocatedStoryPoints() - task.getStoryPoints());
            userService.saveUser(user);
        }
        return "redirect:/tasks";
    }

    @PostMapping("/task/taskDetails/{projectId}/mark-done/{id}")
    public String setTaskDetailsCompleted(@PathVariable long projectId, @PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        boolean isCompleted = task.isCompleted();
        if (isCompleted) {
            taskService.setTaskNotCompleted(id);
            User user = task.getOwner();
            if (user != null) {
                user.setAllocatedStoryPoints(user.getAllocatedStoryPoints() + task.getStoryPoints());
                userService.saveUser(user);
            }
        } else {
            taskService.setTaskCompleted(id);
            User user = task.getOwner();
            if (user != null) {
                user.setAllocatedStoryPoints(user.getAllocatedStoryPoints() - task.getStoryPoints());
                userService.saveUser(user);
            }
        }
        return "redirect:/projects/projectTasks/" + projectId + "/taskDetails/" + id;
    }

    @GetMapping("/projects/projectTasks/{projectId}/taskDetails/{taskId}")
    public String taskDetails(@PathVariable("projectId") long projectId, @PathVariable("taskId") long taskId,
            Model model) {
        Task task = taskService.getTaskById(taskId);
        Project project = projectService.getProjectById(projectId);
        List<TaskHistory> taskHistory = taskHistoryService.findByTaskId(taskId);
        Collections.sort(taskHistory, Comparator.comparing(TaskHistory::getTimestamp).reversed());
        List<TaskThreads> threads = taskThreadsRepository.findByTask(task);
        model.addAttribute("task", task);
        model.addAttribute("project", project);
        model.addAttribute("taskHistory", taskHistory);
        model.addAttribute("taskThreads", threads);
        return "views/taskDetails";
    }

    @GetMapping("/tasks/yourTasks")
    public String yourTasks(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        List<Project> projects = user.getProjects();

        model.addAttribute("projects", projects);
        model.addAttribute("user", user);
        return "views/myTasks";
    }

    @PostMapping("/tasks/{taskId}/comment")
    public String addComment(@PathVariable("taskId") long taskId,
            @RequestParam("content") String content,
            RedirectAttributes redirectAttributes, Principal principal) {
        Task task = taskService.getTaskById(taskId);

        TaskThreads thread = new TaskThreads();
        thread.setTask(task);
        thread.setLocalDateTime(LocalDateTime.now());
        thread.setContent(content);
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        thread.setUser(user);
        taskThreadsRepository.save(thread);

        long projectId = task.getProject().getId();
        redirectAttributes.addAttribute("projectId", projectId);
        redirectAttributes.addAttribute("taskId", taskId);

        return "redirect:/projects/projectTasks/{projectId}/taskDetails/{taskId}";
    }

    @PostMapping("/project/{projectId}/tasks/{taskId}")
    public String deleteTask(@PathVariable long projectId, @PathVariable long taskId) {
        taskService.deleteTask(taskId);
        return "redirect:/projects/tasks/" + projectId;
    }
}
